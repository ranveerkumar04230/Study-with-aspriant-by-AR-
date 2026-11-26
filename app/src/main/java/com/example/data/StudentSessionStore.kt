package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.model.StudentSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.studentDataStore: DataStore<Preferences> by preferencesDataStore(
  name = "study_aspirant_session"
)

class StudentSessionStore(private val context: Context) {
  private val dataStore get() = context.applicationContext.studentDataStore

  val sessionFlow: Flow<StudentSession?> = dataStore.data.map { prefs ->
    val studentId = prefs[KEY_STUDENT_ID].orEmpty()
    val fullName = prefs[KEY_FULL_NAME].orEmpty()
    val phoneNumber = prefs[KEY_PHONE_NUMBER].orEmpty()
    val isAdmin = prefs[KEY_IS_ADMIN_SESSION] ?: false
    if (isAdmin || (studentId.isNotBlank() && fullName.isNotBlank() && phoneNumber.isNotBlank())) {
      StudentSession(
        studentId = studentId,
        fullName = fullName,
        phoneNumber = phoneNumber,
        isAdminSession = isAdmin,
      )
    } else {
      null
    }
  }

  val completedLectureIdsFlow: Flow<Set<String>> = dataStore.data.map { prefs ->
    prefs[KEY_COMPLETED_LECTURES] ?: emptySet()
  }

  suspend fun saveStudentSession(
    studentId: String,
    fullName: String,
    phoneNumber: String,
  ) {
    dataStore.edit { prefs ->
      prefs[KEY_STUDENT_ID] = studentId
      prefs[KEY_FULL_NAME] = fullName
      prefs[KEY_PHONE_NUMBER] = phoneNumber
      prefs[KEY_IS_ADMIN_SESSION] = false
    }
  }

  suspend fun setAdminSession(enabled: Boolean) {
    dataStore.edit { prefs ->
      prefs[KEY_IS_ADMIN_SESSION] = enabled
      if (enabled && prefs[KEY_FULL_NAME].isNullOrBlank()) {
        prefs[KEY_FULL_NAME] = "Admin"
      }
    }
  }

  suspend fun toggleCompletedLecture(lectureId: String) {
    dataStore.edit { prefs ->
      val current = prefs[KEY_COMPLETED_LECTURES] ?: emptySet()
      prefs[KEY_COMPLETED_LECTURES] = if (lectureId in current) {
        current - lectureId
      } else {
        current + lectureId
      }
    }
  }

  suspend fun clearSession() {
    dataStore.edit { prefs ->
      prefs.remove(KEY_STUDENT_ID)
      prefs.remove(KEY_FULL_NAME)
      prefs.remove(KEY_PHONE_NUMBER)
      prefs.remove(KEY_IS_ADMIN_SESSION)
    }
  }

  private companion object {
    val KEY_STUDENT_ID = stringPreferencesKey("student_id")
    val KEY_FULL_NAME = stringPreferencesKey("full_name")
    val KEY_PHONE_NUMBER = stringPreferencesKey("phone_number")
    val KEY_IS_ADMIN_SESSION = booleanPreferencesKey("is_admin_session")
    val KEY_COMPLETED_LECTURES = stringSetPreferencesKey("completed_lectures")
  }
}
