package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.CobaltBlue
import com.example.ui.theme.DarkPink
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.MidnightCard
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPinkSoft
import com.example.ui.theme.NeonPinkText
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

private const val LOCAL_ADMIN_PASSWORD = "admin123"

@Composable
fun AdminLoginDialog(
  onDismiss: () -> Unit,
  onUnlockSuccess: () -> Unit,
) {
  var password by remember { mutableStateOf("") }
  var passwordVisible by remember { mutableStateOf(false) }
  var localError by remember { mutableStateOf<String?>(null) }

  val submitPassword = {
    val trimmed = password.trim()
    when {
      trimmed.isEmpty() -> localError = "Please enter the Admin Password."
      trimmed != LOCAL_ADMIN_PASSWORD -> localError = "Incorrect Admin Password. Please try again."
      else -> {
        localError = null
        onUnlockSuccess()
      }
    }
  }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(22.dp),
      color = MidnightCard,
      border = BorderStroke(
        1.5.dp,
        Brush.linearGradient(listOf(NeonPink, CobaltBlue)),
      ),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("admin_login_dialog"),
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(DarkPink, CobaltBlue))),
              contentAlignment = Alignment.Center,
            ) {
              Icon(
                imageVector = Icons.Rounded.AdminPanelSettings,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp),
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Admin Login",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
              )
              Text(
                text = "Secret Portal (#admin23)",
                style = MaterialTheme.typography.labelSmall,
                color = NeonPinkText,
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .minimumInteractiveComponentSize()
              .size(48.dp)
              .testTag("close_admin_login_button"),
          ) {
            Icon(
              imageVector = Icons.Rounded.Close,
              contentDescription = "Close Admin Login",
              tint = TextMuted,
            )
          }
        }

        Text(
          text = "Enter the Admin Password to immediately open the Admin Dashboard.",
          style = MaterialTheme.typography.bodySmall,
          color = TextMuted,
        )

        OutlinedTextField(
          value = password,
          onValueChange = {
            password = it
            localError = null
          },
          label = { Text("Admin Password") },
          placeholder = { Text("Enter admin password") },
          leadingIcon = {
            Icon(
              imageVector = Icons.Rounded.Lock,
              contentDescription = null,
              tint = NeonPinkText,
            )
          },
          trailingIcon = {
            IconButton(
              onClick = { passwordVisible = !passwordVisible },
              modifier = Modifier.minimumInteractiveComponentSize(),
            ) {
              Icon(
                imageVector = if (passwordVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                tint = TextMuted,
              )
            }
          },
          visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
          keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.None,
            autoCorrectEnabled = false,
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
          ),
          keyboardActions = KeyboardActions(
            onDone = { submitPassword() },
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
            .testTag("admin_password_input"),
        )

        if (!localError.isNullOrBlank()) {
          Surface(
            color = ErrorRed.copy(alpha = 0.14f),
            border = BorderStroke(1.dp, ErrorRed.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth(),
          ) {
            Text(
              text = localError.orEmpty(),
              style = MaterialTheme.typography.bodySmall,
              color = NeonPinkSoft,
              modifier = Modifier.padding(10.dp),
            )
          }
        }

        Button(
          onClick = submitPassword,
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
          contentPadding = PaddingValues(0.dp),
          modifier = Modifier
            .fillMaxWidth()
            .minimumInteractiveComponentSize()
            .height(50.dp)
            .testTag("admin_login_submit_button")
            .background(
              brush = Brush.horizontalGradient(listOf(DarkPink, NeonPink, CobaltBlue)),
              shape = RoundedCornerShape(14.dp),
            ),
        ) {
          Text(
            text = "Login / Unlock",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold),
            color = Color.White,
          )
        }
      }
    }
  }
}
