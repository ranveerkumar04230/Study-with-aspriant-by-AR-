package com.example.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.StudentSessionStore
import com.example.data.StudyRepository
import com.example.data.model.AppPreferences
import com.example.data.model.DefaultSeedData
import com.example.data.model.StudyBatch
import com.example.data.model.StudyLecture
import com.example.data.model.StudySubject
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface UiState<out T> {
  data object Loading : UiState<Nothing>
  data class Success<T>(val data: T) : UiState<T>
  data class Error(val message: String) : UiState<Nothing>
}

data class StudyWorkspaceData(
  val batches: List<StudyBatch> = emptyList(),
  val subjects: List<StudySubject> = emptyList(),
  val lectures: List<StudyLecture> = emptyList(),
  val preferences: AppPreferences = AppPreferences(),
)

enum class SyncStatus {
  CONNECTING,
  LIVE_SYNCED,
  ERROR,
}

class StudyViewModel(
  private val repository: StudyRepository,
  private val sessionStore: StudentSessionStore,
  initialAdminUnlocked: Boolean = false,
  openAdminDashboardOnStart: Boolean = false,
) : ViewModel() {

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _isAdminUnlocked = MutableStateFlow(initialAdminUnlocked)
  val isAdminUnlocked: StateFlow<Boolean> = _isAdminUnlocked.asStateFlow()

  private val _isAdminSheetOpen = MutableStateFlow(initialAdminUnlocked && openAdminDashboardOnStart)
  val isAdminSheetOpen: StateFlow<Boolean> = _isAdminSheetOpen.asStateFlow()

  private val _isSavingAdmin = MutableStateFlow(false)
  val isSavingAdmin: StateFlow<Boolean> = _isSavingAdmin.asStateFlow()

  private val _syncStatus = MutableStateFlow(SyncStatus.CONNECTING)
  val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()

  private val _toastMessages = MutableSharedFlow<String>(extraBufferCapacity = 8)
  val toastMessages: SharedFlow<String> = _toastMessages.asSharedFlow()

  init {
    if (initialAdminUnlocked && openAdminDashboardOnStart) {
      viewModelScope.launch {
        repository.seedDefaultDataIfEmpty().onFailure { err ->
          Log.w(TAG, "Catalog seed warning", err)
        }
      }
    }
  }

  val workspaceState: StateFlow<UiState<StudyWorkspaceData>> =
    combine(
      repository.observeBatches(),
      repository.observeSubjects(),
      repository.observeLectures(),
      repository.observePublicPreferences(),
      sessionStore.completedLectureIdsFlow,
    ) { batches, subjects, lectures, prefs, completedIds ->
      val effectiveBatches = if (batches.isEmpty()) DefaultSeedData.fallbackBatches() else batches
      val effectiveSubjects = if (batches.isEmpty() && subjects.isEmpty()) {
        DefaultSeedData.fallbackSubjects()
      } else {
        subjects
      }
      val mergedLectures = lectures.map { lec ->
        lec.copy(completed = lec.id in completedIds)
      }
      _syncStatus.value = SyncStatus.LIVE_SYNCED
      UiState.Success(
        StudyWorkspaceData(
          batches = effectiveBatches,
          subjects = effectiveSubjects,
          lectures = mergedLectures,
          preferences = prefs ?: AppPreferences(),
        )
      ) as UiState<StudyWorkspaceData>
    }
      .catch { error ->
        val firestoreError = generateSequence(error) { it.cause }
          .filterIsInstance<FirebaseFirestoreException>()
          .firstOrNull()
        if (firestoreError == null && error is CancellationException) throw error
        val rootError = firestoreError ?: error
        Log.w(TAG, "Error observing study workspace", rootError)
        _syncStatus.value = SyncStatus.ERROR
        emit(UiState.Error(rootError.localizedMessage ?: "Failed to sync study data"))
      }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = UiState.Loading,
      )

  fun onSearchQueryChange(newQuery: String) {
    _searchQuery.value = newQuery
  }

  fun clearSearch() {
    _searchQuery.value = ""
  }

  fun onAdminUnlockedFromLogin() {
    _isAdminUnlocked.value = true
    _isAdminSheetOpen.value = true
    _toastMessages.tryEmit("🔓 Admin Dashboard unlocked")
    viewModelScope.launch {
      repository.seedDefaultDataIfEmpty().onFailure { err ->
        Log.w(TAG, "Catalog seed warning", err)
      }
    }
  }

  fun openAdminSheetIfUnlocked() {
    if (_isAdminUnlocked.value) {
      _isAdminSheetOpen.value = true
    }
  }

  fun closeAdminSheet() {
    _isAdminSheetOpen.value = false
  }

  fun saveLecture(
    batchName: String,
    batchSubtitle: String,
    subjectName: String,
    lectureTitle: String,
    videoUrl: String,
    engNotesUrl: String,
    hinNotesUrl: String,
    pptUrl: String,
    onSuccess: () -> Unit,
  ) {
    if (batchName.isBlank() || subjectName.isBlank() || lectureTitle.isBlank()) {
      _toastMessages.tryEmit("Batch, Subject और Lecture नाम ज़रूरी हैं!")
      return
    }
    viewModelScope.launch {
      _isSavingAdmin.value = true
      val result = repository.saveLectureWithBatchAndSubject(
        batchNameInput = batchName,
        batchSubtitleInput = batchSubtitle,
        subjectNameInput = subjectName,
        lectureTitleInput = lectureTitle,
        videoUrlInput = videoUrl,
        engNotesUrlInput = engNotesUrl,
        hinNotesUrlInput = hinNotesUrl,
        pptUrlInput = pptUrl,
      )
      _isSavingAdmin.value = false
      result.fold(
        onSuccess = {
          _toastMessages.tryEmit("✅ लेक्चर लाइव सेव हो गया! सभी छात्रों को दिखेगा।")
          _isAdminSheetOpen.value = false
          onSuccess()
        },
        onFailure = { err ->
          _toastMessages.tryEmit(err.localizedMessage ?: "Failed to save lecture")
        },
      )
    }
  }

  fun saveSocialLinks(whatsappInput: String, telegramInput: String) {
    viewModelScope.launch {
      _isSavingAdmin.value = true
      val result = repository.saveSocialLinks(whatsappInput, telegramInput)
      _isSavingAdmin.value = false
      result.fold(
        onSuccess = {
          _toastMessages.tryEmit("✅ लिंक्स लाइव अपडेट हो गए!")
          _isAdminSheetOpen.value = false
        },
        onFailure = { err ->
          _toastMessages.tryEmit(err.localizedMessage ?: "Failed to save links")
        },
      )
    }
  }

  fun toggleLectureCompleted(lecture: StudyLecture) {
    viewModelScope.launch {
      sessionStore.toggleCompletedLecture(lecture.id)
    }
  }

  fun deleteLecture(lectureId: String) {
    viewModelScope.launch {
      val result = repository.deleteLecture(lectureId)
      result.fold(
        onSuccess = { _toastMessages.tryEmit("Lecture removed") },
        onFailure = { err ->
          _toastMessages.tryEmit(err.localizedMessage ?: "Failed to delete lecture")
        },
      )
    }
  }

  private companion object {
    const val TAG = "StudyVM"
    const val STOP_TIMEOUT_MILLIS = 5000L
  }
}
