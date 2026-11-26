package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.StudentSessionStore
import com.example.data.StudyRepository
import com.example.ui.theme.CobaltBlue
import com.example.ui.theme.CobaltBlueDeep
import com.example.ui.theme.DarkPink
import com.example.ui.theme.ElevatedNavy
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.GoldBase
import com.example.ui.theme.GoldLight
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPinkSoft
import com.example.ui.theme.NeonPinkText
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.SkyAccent
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.StudyCardBlueEnd
import com.example.ui.theme.StudyCardPinkStart
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
  repository: StudyRepository,
  sessionStore: StudentSessionStore,
  onAdminAuthSuccess: () -> Unit,
  onStudentLoginSuccess: () -> Unit,
) {
  val scope = rememberCoroutineScope()

  var fullNameInput by remember { mutableStateOf("") }
  var phoneInput by remember { mutableStateOf("") }
  var isSubmittingStudent by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  var showAdminDialog by remember { mutableStateOf(false) }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(ObsidianBg),
  ) {
    Image(
      painter = painterResource(id = R.drawable.img_study_hero_banner),
      contentDescription = null,
      contentScale = ContentScale.Crop,
      modifier = Modifier
        .fillMaxWidth()
        .height(250.dp)
        .alpha(0.45f),
    )
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(260.dp)
        .background(
          Brush.verticalGradient(
            colors = listOf(Color.Transparent, ObsidianBg.copy(alpha = 0.72f), ObsidianBg)
          )
        ),
    )

    Column(
      modifier = Modifier
        .fillMaxSize()
        .windowInsetsPadding(WindowInsets.safeDrawing)
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 24.dp, vertical = 20.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
    ) {
      Column(
        modifier = Modifier.widthIn(max = 460.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Box(
          modifier = Modifier
            .size(86.dp)
            .shadow(22.dp, CircleShape, ambientColor = GoldBase, spotColor = NeonPink)
            .clip(CircleShape)
            .border(
              width = 2.dp,
              brush = Brush.linearGradient(listOf(GoldLight, NeonPink, CobaltBlue)),
              shape = CircleShape,
            ),
          contentAlignment = Alignment.Center,
        ) {
          Image(
            painter = painterResource(id = R.drawable.img_saraswati_maa),
            contentDescription = stringResource(R.string.saraswati_image_desc),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = buildAnnotatedString {
            append(stringResource(R.string.brand_title_prefix))
            append(" ")
            withStyle(
              SpanStyle(brush = Brush.horizontalGradient(listOf(NeonPink, CobaltBlue)))
            ) {
              append(stringResource(R.string.brand_title_accent))
            }
          },
          style = MaterialTheme.typography.headlineMedium,
          textAlign = TextAlign.Center,
          color = TextPrimary,
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = stringResource(R.string.splash_quote),
          style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
          color = GoldLight,
          textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(22.dp))

        Surface(
          shape = RoundedCornerShape(24.dp),
          color = Color.Transparent,
          border = BorderStroke(
            1.5.dp,
            Brush.linearGradient(listOf(NeonPink, CobaltBlue)),
          ),
          modifier = Modifier
            .fillMaxWidth()
            .background(
              brush = Brush.linearGradient(listOf(StudyCardPinkStart, StudyCardBlueEnd)),
              shape = RoundedCornerShape(24.dp),
            ),
        ) {
          Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(RoundedCornerShape(14.dp))
                  .background(Brush.linearGradient(listOf(DarkPink, CobaltBlueDeep))),
                contentAlignment = Alignment.Center,
              ) {
                Icon(
                  imageVector = Icons.Rounded.School,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(22.dp),
                )
              }
              Column {
                Text(
                  text = stringResource(R.string.student_login_title),
                  style = MaterialTheme.typography.titleLarge,
                  color = TextPrimary,
                )
                Text(
                  text = stringResource(R.string.student_login_subtitle),
                  style = MaterialTheme.typography.bodySmall,
                  color = NeonPinkSoft,
                )
              }
            }

            OutlinedTextField(
              value = fullNameInput,
              onValueChange = { incoming ->
                fullNameInput = incoming
                errorMessage = null
              },
              label = { Text("Full Name") },
              placeholder = { Text("Enter your full name") },
              leadingIcon = {
                Icon(
                  imageVector = Icons.Rounded.Person,
                  contentDescription = null,
                  tint = SkyAccent,
                )
              },
              keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
              ),
              singleLine = true,
              shape = RoundedCornerShape(14.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CobaltBlue,
                unfocusedBorderColor = SlateBorder,
                focusedContainerColor = ObsidianBg,
                unfocusedContainerColor = ObsidianBg,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedLabelColor = SkyAccent,
                unfocusedLabelColor = TextMuted,
                cursorColor = CobaltBlue,
              ),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("student_name_input"),
            )

            OutlinedTextField(
              value = phoneInput,
              onValueChange = { incoming ->
                val trimmed = incoming.trim()
                if (trimmed.equals("#admin23", ignoreCase = true) ||
                  trimmed.endsWith("#admin23", ignoreCase = true)
                ) {
                  phoneInput = ""
                  errorMessage = null
                  showAdminDialog = true
                } else {
                  phoneInput = incoming
                  errorMessage = null
                }
              },
              label = { Text("10-Digit Mobile Number") },
              placeholder = { Text("e.g. 9876543210") },
              leadingIcon = {
                Icon(
                  imageVector = Icons.Rounded.PhoneAndroid,
                  contentDescription = null,
                  tint = NeonPinkText,
                )
              },
              keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done,
              ),
              singleLine = true,
              shape = RoundedCornerShape(14.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonPink,
                unfocusedBorderColor = SlateBorder,
                focusedContainerColor = ObsidianBg,
                unfocusedContainerColor = ObsidianBg,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedLabelColor = NeonPinkText,
                unfocusedLabelColor = TextMuted,
                cursorColor = NeonPink,
              ),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("student_phone_input"),
            )

            if (errorMessage != null) {
              ErrorCallout(errorMessage ?: "")
            }

            Button(
              onClick = {
                val trimmedName = fullNameInput.trim()
                if (trimmedName.length < 2) {
                  errorMessage = "Please enter your full name (at least 2 characters)."
                  return@Button
                }
                if (!StudyRepository.isValid10DigitPhone(phoneInput)) {
                  errorMessage = "Please enter a valid 10-digit mobile number."
                  return@Button
                }
                errorMessage = null
                isSubmittingStudent = true
                scope.launch {
                  val result = repository.saveStudentProfile(trimmedName, phoneInput)
                  result.fold(
                    onSuccess = { session ->
                      sessionStore.saveStudentSession(
                        studentId = session.studentId,
                        fullName = session.fullName,
                        phoneNumber = session.phoneNumber,
                      )
                      isSubmittingStudent = false
                      onStudentLoginSuccess()
                    },
                    onFailure = { err ->
                      isSubmittingStudent = false
                      errorMessage = err.localizedMessage ?: "Unable to start studying. Please try again."
                    },
                  )
                }
              },
              enabled = !isSubmittingStudent,
              shape = RoundedCornerShape(14.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                disabledContainerColor = ElevatedNavy,
              ),
              contentPadding = PaddingValues(0.dp),
              modifier = Modifier
                .fillMaxWidth()
                .minimumInteractiveComponentSize()
                .height(52.dp)
                .testTag("start_studying_button")
                .background(
                  brush = Brush.horizontalGradient(listOf(DarkPink, NeonPink, CobaltBlue)),
                  shape = RoundedCornerShape(14.dp),
                ),
            ) {
              if (isSubmittingStudent) {
                CircularProgressIndicator(
                  modifier = Modifier.size(22.dp),
                  color = Color.White,
                  strokeWidth = 2.5.dp,
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                  text = "Starting...",
                  style = MaterialTheme.typography.labelLarge,
                  color = Color.White,
                )
              } else {
                Text(
                  text = stringResource(R.string.start_studying),
                  style = MaterialTheme.typography.labelLarge.copy(fontSize = 15.sp),
                  color = Color.White,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                  imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(18.dp),
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center,
        ) {
          Icon(
            imageVector = Icons.Rounded.AutoStories,
            contentDescription = null,
            tint = NeonPinkText,
            modifier = Modifier.size(16.dp),
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "GK/GS • Science Special • Bihar Daroga Batches",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted,
            textAlign = TextAlign.Center,
          )
        }
      }
    }
  }

  if (showAdminDialog) {
    AdminLoginDialog(
      onDismiss = { showAdminDialog = false },
      onUnlockSuccess = {
        showAdminDialog = false
        onAdminAuthSuccess()
      },
    )
  }
}

@Composable
private fun ErrorCallout(message: String) {
  Surface(
    color = ErrorRed.copy(alpha = 0.14f),
    border = BorderStroke(1.dp, ErrorRed.copy(alpha = 0.5f)),
    shape = RoundedCornerShape(12.dp),
    modifier = Modifier.fillMaxWidth(),
  ) {
    Text(
      text = message,
      color = NeonPinkSoft,
      style = MaterialTheme.typography.bodySmall,
      modifier = Modifier.padding(12.dp),
    )
  }
}
