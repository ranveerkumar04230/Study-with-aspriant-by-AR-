package com.example.data.model

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@Keep
@IgnoreExtraProperties
data class StudyBatch(
  @DocumentId val id: String = "",
  val name: String = "",
  val subtitle: String = "",
  val sortOrder: Long = 0L,
  val createdBy: String = "",
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null,
)

@Keep
@IgnoreExtraProperties
data class StudySubject(
  @DocumentId val id: String = "",
  val batchId: String = "",
  val name: String = "",
  val sortOrder: Long = 0L,
  val createdBy: String = "",
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null,
)

@Keep
@IgnoreExtraProperties
data class StudyLecture(
  @DocumentId val id: String = "",
  val batchId: String = "",
  val subjectId: String = "",
  val title: String = "",
  val videoUrl: String = "",
  val engNotesUrl: String = "",
  val hinNotesUrl: String = "",
  val pptUrl: String = "",
  val createdBy: String = "",
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null,
  @get:Exclude val completed: Boolean = false,
)

@Keep
@IgnoreExtraProperties
data class AppPreferences(
  @DocumentId val id: String = "",
  val whatsappUrl: String = "https://whatsapp.com",
  val telegramUrl: String = "https://telegram.org",
  val seededDefaults: Boolean = false,
  val updatedBy: String = "",
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null,
)

data class StudentSession(
  val studentId: String = "",
  val fullName: String = "",
  val phoneNumber: String = "",
  val isAdminSession: Boolean = false,
)

data class SeedBatchSpec(
  val id: String,
  val name: String,
  val subtitle: String,
  val subjects: List<String>,
)

object DefaultSeedData {
  val batches = listOf(
    SeedBatchSpec(
      id = "parmar_4_0",
      name = "PARMAR 4.0",
      subtitle = "BY PARMAR SIR • GK & GS COMPLETE",
      subjects = listOf(
        "BIOLOGY",
        "PHYSICS",
        "CHEMISTRY",
        "ECONOMICS",
        "STATIC G.K",
        "POLITY",
        "ANCIENT HISTORY",
        "MEDIEVAL HISTORY",
        "MODERN HISTORY",
        "GEOGRAPHY",
        "ENVIRONMENT AND ECOLOGY",
        "CURRENT AFFAIRS",
      ),
    ),
    SeedBatchSpec(
      id = "science_magnet",
      name = "SCIENCE MAGNET",
      subtitle = "SCIENCE SPECIAL BATCH • BIO, PHY, CHEM",
      subjects = listOf(
        "BIOLOGY",
        "PHYSICS",
        "CHEMISTRY",
      ),
    ),
    SeedBatchSpec(
      id = "gkgs_masti",
      name = "GKGS MASTI",
      subtitle = "BY JAGDEV SIR • BIHAR DAROGA BATCH",
      subjects = listOf(
        "BIHAR SPECIAL GK",
        "HISTORY",
        "POLITY",
        "GEOGRAPHY",
        "GENERAL SCIENCE",
        "CURRENT AFFAIRS",
      ),
    ),
  )

  fun fallbackBatches(): List<StudyBatch> =
    batches.mapIndexed { index, spec ->
      StudyBatch(
        id = spec.id,
        name = spec.name,
        subtitle = spec.subtitle,
        sortOrder = index.toLong(),
      )
    }

  fun fallbackSubjects(): List<StudySubject> =
    batches.flatMap { spec ->
      spec.subjects.mapIndexed { index, name ->
        StudySubject(
          id = "${spec.id}_${name.lowercase().replace(Regex("[^a-z0-9]+"), "_").trim('_')}",
          batchId = spec.id,
          name = name,
          sortOrder = index.toLong(),
        )
      }
    }
}
