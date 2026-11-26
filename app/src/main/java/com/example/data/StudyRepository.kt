package com.example.data

import android.content.Context
import com.example.R
import com.example.data.model.AppPreferences
import com.example.data.model.DefaultSeedData
import com.example.data.model.StudentSession
import com.example.data.model.StudyBatch
import com.example.data.model.StudyLecture
import com.example.data.model.StudySubject
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID

class StudyRepository(
  private val db: FirebaseFirestore,
) {
  constructor(context: Context) : this(
    FirebaseFirestore.getInstance(
      context.applicationContext.getString(R.string.firestore_database_id)
    )
  )

  fun observeBatches(): Flow<List<StudyBatch>> {
    val path = "batches"
    return db.collection("batches")
      .snapshots()
      .map { snapshot ->
        snapshot.toObjects(
          StudyBatch::class.java,
          DocumentSnapshot.ServerTimestampBehavior.ESTIMATE,
        ).sortedWith(compareBy<StudyBatch> { it.sortOrder }.thenBy { it.name })
      }
      .catch { error ->
        logFlowError(error, OperationType.LIST, path)
        throw error
      }
  }

  fun observeSubjects(): Flow<List<StudySubject>> {
    val path = "subjects"
    return db.collection("subjects")
      .snapshots()
      .map { snapshot ->
        snapshot.toObjects(
          StudySubject::class.java,
          DocumentSnapshot.ServerTimestampBehavior.ESTIMATE,
        ).sortedWith(compareBy<StudySubject> { it.sortOrder }.thenBy { it.name })
      }
      .catch { error ->
        logFlowError(error, OperationType.LIST, path)
        throw error
      }
  }

  fun observeLectures(): Flow<List<StudyLecture>> {
    val path = "lectures"
    return db.collection("lectures")
      .snapshots()
      .map { snapshot ->
        snapshot.toObjects(
          StudyLecture::class.java,
          DocumentSnapshot.ServerTimestampBehavior.ESTIMATE,
        ).sortedBy { it.createdAt?.toDate()?.time ?: Long.MAX_VALUE }
      }
      .catch { error ->
        logFlowError(error, OperationType.LIST, path)
        throw error
      }
  }

  fun observePublicPreferences(): Flow<AppPreferences?> {
    val path = "settings/public"
    return db.collection("settings").document("public")
      .snapshots()
      .map { snapshot ->
        if (snapshot.exists()) {
          snapshot.toObject(
            AppPreferences::class.java,
            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE,
          )
        } else {
          null
        }
      }
      .catch { error ->
        logFlowError(error, OperationType.GET, path)
        throw error
      }
  }

  suspend fun saveStudentProfile(
    fullNameInput: String,
    phoneInput: String,
  ): Result<StudentSession> {
    val cleanName = fullNameInput.trim().replace(Regex("\\s+"), " ")
    val normalizedPhone = normalize10DigitPhone(phoneInput)
    val suffix = UUID.randomUUID().toString().replace("-", "").take(8)
    val studentId = "stu_${normalizedPhone}_$suffix"
    val docPath = "students/$studentId"

    return try {
      require(cleanName.length in 2..80) {
        "Please enter your full name (at least 2 characters)."
      }
      require(normalizedPhone.matches(Regex("^[0-9]{10}$"))) {
        "Please enter a valid 10-digit mobile number."
      }

      db.collection("students").document(studentId)
        .set(
          mapOf(
            "fullName" to cleanName,
            "phoneNumber" to normalizedPhone,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp(),
          )
        )
        .await()

      Result.success(
        StudentSession(
          studentId = studentId,
          fullName = cleanName,
          phoneNumber = normalizedPhone,
          isAdminSession = false,
        )
      )
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, docPath)
      Result.failure(e)
    }
  }

  suspend fun seedDefaultDataIfEmpty(): Result<Boolean> {
    val path = "batches"
    return try {
      val publicRef = db.collection("settings").document("public")
      val publicSnap = publicRef.get().await()
      if (publicSnap.exists() && publicSnap.getBoolean("seededDefaults") == true) {
        return Result.success(false)
      }

      val existingBatches = db.collection("batches").limit(1).get().await()
      val batchWrite = db.batch()
      if (existingBatches.isEmpty) {
        DefaultSeedData.batches.forEachIndexed { batchIndex, batchSpec ->
          val batchRef = db.collection("batches").document(batchSpec.id)
          batchWrite.set(
            batchRef,
            mapOf(
              "name" to batchSpec.name,
              "subtitle" to batchSpec.subtitle,
              "sortOrder" to batchIndex.toLong(),
              "createdBy" to ADMIN_ACTOR_ID,
              "createdAt" to FieldValue.serverTimestamp(),
              "updatedAt" to FieldValue.serverTimestamp(),
            ),
          )
          batchSpec.subjects.forEachIndexed { subIndex, subName ->
            val subSlug = "${batchSpec.id}_${slugify(subName)}"
            val subRef = db.collection("subjects").document(subSlug)
            batchWrite.set(
              subRef,
              mapOf(
                "batchId" to batchSpec.id,
                "name" to subName,
                "sortOrder" to subIndex.toLong(),
                "createdBy" to ADMIN_ACTOR_ID,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp(),
              ),
            )
          }
        }
      }

      if (!publicSnap.exists()) {
        batchWrite.set(
          publicRef,
          mapOf(
            "whatsappUrl" to "https://whatsapp.com",
            "telegramUrl" to "https://telegram.org",
            "seededDefaults" to true,
            "updatedBy" to ADMIN_ACTOR_ID,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp(),
          ),
        )
      } else {
        batchWrite.update(
          publicRef,
          mapOf(
            "seededDefaults" to true,
            "updatedBy" to ADMIN_ACTOR_ID,
            "updatedAt" to FieldValue.serverTimestamp(),
          ),
        )
      }

      batchWrite.commit().await()
      Result.success(true)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.WRITE, path)
      Result.failure(e)
    }
  }

  suspend fun saveLectureWithBatchAndSubject(
    batchNameInput: String,
    batchSubtitleInput: String,
    subjectNameInput: String,
    lectureTitleInput: String,
    videoUrlInput: String,
    engNotesUrlInput: String,
    hinNotesUrlInput: String,
    pptUrlInput: String,
  ): Result<Unit> {
    var opPath = "lectures"
    return try {
      val batchName = batchNameInput.trim().uppercase()
      val batchSubtitle = batchSubtitleInput.trim().uppercase().ifBlank { "COMPLETE BATCH" }
      val subjectName = subjectNameInput.trim().uppercase()
      val lectureTitle = lectureTitleInput.trim().uppercase()
      val videoUrl = videoUrlInput.trim()
      val engNotesUrl = engNotesUrlInput.trim()
      val hinNotesUrl = hinNotesUrlInput.trim()
      val pptUrl = pptUrlInput.trim()

      require(batchName.length in 1..80) { "Batch name must be 1–80 characters." }
      require(batchSubtitle.length <= 160) { "Batch subtitle must be 160 characters or fewer." }
      require(subjectName.length in 1..100) { "Subject name must be 1–100 characters." }
      require(lectureTitle.length in 1..150) { "Lecture title must be 1–150 characters." }
      require(
        videoUrl.length <= 1000 &&
          engNotesUrl.length <= 1000 &&
          hinNotesUrl.length <= 1000 &&
          pptUrl.length <= 1000
      ) { "Links must be 1000 characters or fewer." }

      val liveBatchesSnap = db.collection("batches").get().await()
      val liveBatches = liveBatchesSnap.toObjects(StudyBatch::class.java)
      val existingBatch = liveBatches.firstOrNull { it.name.equals(batchName, ignoreCase = true) }

      val writeBatch = db.batch()
      val batchId = existingBatch?.id ?: db.collection("batches").document().id
      if (existingBatch == null) {
        val batchRef = db.collection("batches").document(batchId)
        val nextSortOrder = ((liveBatches.maxOfOrNull { it.sortOrder } ?: -1L) + 1L)
          .coerceAtMost(10000L)
        writeBatch.set(
          batchRef,
          mapOf(
            "name" to batchName,
            "subtitle" to batchSubtitle,
            "sortOrder" to nextSortOrder,
            "createdBy" to ADMIN_ACTOR_ID,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp(),
          ),
        )
      }

      val liveSubjectsSnap = db.collection("subjects")
        .whereEqualTo("batchId", batchId)
        .get()
        .await()
      val liveSubjects = liveSubjectsSnap.toObjects(StudySubject::class.java)
      val existingSubject = liveSubjects.firstOrNull {
        it.name.equals(subjectName, ignoreCase = true)
      }
      val subjectId = existingSubject?.id ?: db.collection("subjects").document().id
      if (existingSubject == null) {
        val subjectRef = db.collection("subjects").document(subjectId)
        val nextSubOrder = ((liveSubjects.maxOfOrNull { it.sortOrder } ?: -1L) + 1L)
          .coerceAtMost(10000L)
        writeBatch.set(
          subjectRef,
          mapOf(
            "batchId" to batchId,
            "name" to subjectName,
            "sortOrder" to nextSubOrder,
            "createdBy" to ADMIN_ACTOR_ID,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp(),
          ),
        )
      }

      val lectureRef = db.collection("lectures").document()
      opPath = lectureRef.path
      writeBatch.set(
        lectureRef,
        mapOf(
          "batchId" to batchId,
          "subjectId" to subjectId,
          "title" to lectureTitle,
          "videoUrl" to videoUrl,
          "engNotesUrl" to engNotesUrl,
          "hinNotesUrl" to hinNotesUrl,
          "pptUrl" to pptUrl,
          "createdBy" to ADMIN_ACTOR_ID,
          "createdAt" to FieldValue.serverTimestamp(),
          "updatedAt" to FieldValue.serverTimestamp(),
        ),
      )

      writeBatch.commit().await()
      Result.success(Unit)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, opPath)
      Result.failure(e)
    }
  }

  suspend fun deleteLecture(lectureId: String): Result<Unit> {
    val docPath = "lectures/$lectureId"
    return try {
      require(lectureId.isNotBlank()) { "Lecture ID must not be blank." }
      db.collection("lectures").document(lectureId).delete().await()
      Result.success(Unit)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.DELETE, docPath)
      Result.failure(e)
    }
  }

  suspend fun saveSocialLinks(
    whatsappInput: String,
    telegramInput: String,
  ): Result<Unit> {
    val docPath = "settings/public"
    return try {
      val normalizedWa = normalizeWhatsAppUrl(whatsappInput)
      val normalizedTg = normalizeTelegramUrl(telegramInput)
      require(normalizedWa.length <= 500 && normalizedTg.length <= 500) {
        "Social link URL must be 500 characters or fewer."
      }

      val publicRef = db.collection("settings").document("public")
      val existing = publicRef.get().await()
      if (existing.exists()) {
        publicRef.update(
          mapOf(
            "whatsappUrl" to normalizedWa,
            "telegramUrl" to normalizedTg,
            "updatedBy" to ADMIN_ACTOR_ID,
            "updatedAt" to FieldValue.serverTimestamp(),
          )
        ).await()
      } else {
        publicRef.set(
          mapOf(
            "whatsappUrl" to normalizedWa,
            "telegramUrl" to normalizedTg,
            "seededDefaults" to true,
            "updatedBy" to ADMIN_ACTOR_ID,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp(),
          )
        ).await()
      }
      Result.success(Unit)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.WRITE, docPath)
      Result.failure(e)
    }
  }

  private fun logFlowError(
    error: Throwable,
    operationType: OperationType,
    path: String,
  ) {
    val firestoreError = generateSequence(error) { it.cause }
      .filterIsInstance<FirebaseFirestoreException>()
      .firstOrNull()
    if (firestoreError == null && error is CancellationException) throw error
    val exceptionToLog = firestoreError ?: (error as? Exception)
    if (exceptionToLog != null) {
      handleFirestoreError(exceptionToLog, operationType, path)
    }
  }

  private fun slugify(raw: String): String =
    raw.lowercase()
      .replace(Regex("[^a-z0-9]+"), "_")
      .trim('_')
      .ifBlank { "item" }

  companion object {
    const val ADMIN_ACTOR_ID = "admin_local"

    fun normalize10DigitPhone(rawInput: String): String {
      val digits = rawInput.trim().filter { it.isDigit() }
      return when {
        digits.length == 10 -> digits
        digits.length == 12 && digits.startsWith("91") -> digits.substring(2)
        digits.length == 11 && digits.startsWith("0") -> digits.substring(1)
        else -> digits
      }
    }

    fun isValid10DigitPhone(rawInput: String): Boolean {
      val normalized = normalize10DigitPhone(rawInput)
      return normalized.matches(Regex("^[0-9]{10}$"))
    }

    fun normalizeWhatsAppUrl(raw: String): String {
      val trimmed = raw.trim()
      if (trimmed.isBlank()) return "https://whatsapp.com"
      if (trimmed.startsWith("http://", ignoreCase = true) ||
        trimmed.startsWith("https://", ignoreCase = true)
      ) {
        return trimmed
      }
      val digits = trimmed.replace(Regex("[^0-9]"), "")
      return if (digits.isNotEmpty()) "https://wa.me/$digits" else "https://whatsapp.com"
    }

    fun normalizeTelegramUrl(raw: String): String {
      val trimmed = raw.trim()
      if (trimmed.isBlank()) return "https://telegram.org"
      if (trimmed.startsWith("http://", ignoreCase = true) ||
        trimmed.startsWith("https://", ignoreCase = true)
      ) {
        return trimmed
      }
      val handle = trimmed.removePrefix("@").trim()
      return if (handle.isNotEmpty()) "https://t.me/$handle" else "https://telegram.org"
    }
  }
}
