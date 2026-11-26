package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.CobaltBlueBright
import com.example.ui.theme.DarkPink
import com.example.ui.theme.RoyalBlue
import com.example.ui.theme.StudyBlueTintCard
import com.example.ui.theme.StudyInkPrimary
import com.example.ui.theme.StudyInkSecondary
import com.example.ui.theme.StudyLightBorder
import com.example.ui.theme.StudyWhiteCard
import com.example.ui.theme.WhatsAppGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSheet(
  initialBatchName: String,
  initialBatchSubtitle: String,
  initialSubjectName: String,
  initialWhatsappUrl: String,
  initialTelegramUrl: String,
  isSaving: Boolean,
  onDismiss: () -> Unit,
  onSaveLecture: (
    batchName: String,
    batchSubtitle: String,
    subjectName: String,
    lectureTitle: String,
    videoUrl: String,
    engNotesUrl: String,
    hinNotesUrl: String,
    pptUrl: String,
    onCleared: () -> Unit,
  ) -> Unit,
  onSaveSocials: (whatsapp: String, telegram: String) -> Unit,
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  var batchName by remember(initialBatchName) { mutableStateOf(initialBatchName) }
  var batchSubtitle by remember(initialBatchSubtitle) { mutableStateOf(initialBatchSubtitle) }
  var subjectName by remember(initialSubjectName) { mutableStateOf(initialSubjectName) }
  var lectureTitle by remember { mutableStateOf("") }
  var videoUrl by remember { mutableStateOf("") }
  var engNotesUrl by remember { mutableStateOf("") }
  var hinNotesUrl by remember { mutableStateOf("") }
  var pptUrl by remember { mutableStateOf("") }

  var whatsappInput by remember(initialWhatsappUrl) { mutableStateOf(initialWhatsappUrl) }
  var telegramInput by remember(initialTelegramUrl) { mutableStateOf(initialTelegramUrl) }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = StudyWhiteCard,
    contentColor = StudyInkPrimary,
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .navigationBarsPadding()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Text(
        text = stringResource(R.string.admin_sheet_title),
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
        color = DarkPink,
      )

      AdminField(
        value = batchName,
        onValueChange = { batchName = it },
        label = "Batch नाम (जैसे: PARMAR 4.0)",
        tag = "admin_batch_input",
      )
      AdminField(
        value = batchSubtitle,
        onValueChange = { batchSubtitle = it },
        label = "Batch Subtitle",
        tag = "admin_batch_sub_input",
      )
      AdminField(
        value = subjectName,
        onValueChange = { subjectName = it },
        label = "Subject नाम (जैसे: BIOLOGY)",
        tag = "admin_subject_input",
      )
      AdminField(
        value = lectureTitle,
        onValueChange = { lectureTitle = it },
        label = "Lecture नाम (जैसे: CELL - LEC 1)",
        tag = "admin_lecture_input",
      )
      AdminField(
        value = videoUrl,
        onValueChange = { videoUrl = it },
        label = "YouTube Video लिंक",
        tag = "admin_video_input",
      )
      AdminField(
        value = engNotesUrl,
        onValueChange = { engNotesUrl = it },
        label = "English Notes PDF लिंक",
        tag = "admin_eng_input",
      )
      AdminField(
        value = hinNotesUrl,
        onValueChange = { hinNotesUrl = it },
        label = "Hindi Notes PDF लिंक",
        tag = "admin_hin_input",
      )
      AdminField(
        value = pptUrl,
        onValueChange = { pptUrl = it },
        label = "PPT / Class PDF लिंक",
        tag = "admin_ppt_input",
      )

      Button(
        onClick = {
          onSaveLecture(
            batchName,
            batchSubtitle,
            subjectName,
            lectureTitle,
            videoUrl,
            engNotesUrl,
            hinNotesUrl,
            pptUrl,
          ) {
            lectureTitle = ""
            videoUrl = ""
            engNotesUrl = ""
            hinNotesUrl = ""
            pptUrl = ""
          }
        },
        enabled = !isSaving,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier
          .fillMaxWidth()
          .minimumInteractiveComponentSize()
          .height(50.dp)
          .testTag("admin_save_lecture_button")
          .background(
            brush = Brush.horizontalGradient(listOf(DarkPink, RoyalBlue)),
            shape = RoundedCornerShape(12.dp),
          ),
      ) {
        if (isSaving) {
          CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            color = Color.White,
            strokeWidth = 2.dp,
          )
        } else {
          Text(
            text = stringResource(R.string.admin_save_lecture),
            style = MaterialTheme.typography.labelLarge,
            color = Color.White,
          )
        }
      }

      HorizontalDivider(
        color = StudyLightBorder,
        modifier = Modifier.padding(vertical = 8.dp),
      )

      Text(
        text = stringResource(R.string.admin_social_title),
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = RoyalBlue,
      )

      AdminField(
        value = whatsappInput,
        onValueChange = { whatsappInput = it },
        label = "WhatsApp लिंक या नंबर (जैसे: 919876543210)",
        tag = "admin_wa_input",
      )
      AdminField(
        value = telegramInput,
        onValueChange = { telegramInput = it },
        label = "Telegram लिंक (जैसे: https://t.me/...)",
        tag = "admin_tg_input",
      )

      Button(
        onClick = { onSaveSocials(whatsappInput, telegramInput) },
        enabled = !isSaving,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
        modifier = Modifier
          .fillMaxWidth()
          .minimumInteractiveComponentSize()
          .height(48.dp)
          .testTag("admin_save_socials_button"),
      ) {
        Text(
          text = stringResource(R.string.admin_save_socials),
          style = MaterialTheme.typography.labelLarge,
          color = Color.White,
        )
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

@Composable
private fun AdminField(
  value: String,
  onValueChange: (String) -> Unit,
  label: String,
  tag: String,
) {
  OutlinedTextField(
    value = value,
    onValueChange = onValueChange,
    label = { Text(text = label, style = MaterialTheme.typography.bodySmall) },
    singleLine = true,
    shape = RoundedCornerShape(12.dp),
    colors = OutlinedTextFieldDefaults.colors(
      focusedBorderColor = DarkPink,
      unfocusedBorderColor = StudyLightBorder,
      focusedContainerColor = StudyBlueTintCard,
      unfocusedContainerColor = StudyBlueTintCard,
      focusedTextColor = StudyInkPrimary,
      unfocusedTextColor = StudyInkPrimary,
      focusedLabelColor = DarkPink,
      unfocusedLabelColor = StudyInkSecondary,
      cursorColor = CobaltBlueBright,
    ),
    modifier = Modifier
      .fillMaxWidth()
      .testTag(tag),
  )
}
