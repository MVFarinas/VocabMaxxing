package com.vocabmaxxing.app.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vocabmaxxing.app.ui.theme.*

@Composable
fun AuthScreen(
    onLogin: (String, String) -> Unit,
    onRegister: (String, String) -> Unit,
    isLoading: Boolean,
    error: String?,
    modifier: Modifier = Modifier
) {
    var isLoginMode by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Surface950)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Brand
        Row {
            Text(
                text = "Vocab",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Maxxing",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Accent
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Measure and improve your expressive intelligence.",
            fontSize = 13.sp,
            color = TextMuted,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(40.dp))

        // Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Surface900)
                .padding(24.dp)
        ) {
            // Tab toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Sign In",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isLoginMode) Color.White else TextMuted,
                    modifier = Modifier.clickable { isLoginMode = true }
                )
                Text(
                    text = "Register",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (!isLoginMode) Color.White else TextMuted,
                    modifier = Modifier.clickable { isLoginMode = false }
                )
            }

            Spacer(Modifier.height(24.dp))

            // Email
            Text(
                text = "EMAIL",
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                color = TextMuted,
                letterSpacing = 2.sp
            )
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("you@example.com", color = TextMuted.copy(alpha = 0.5f)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Accent.copy(alpha = 0.5f),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = Accent
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(Modifier.height(16.dp))

            // Password
            Text(
                text = "PASSWORD",
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                color = TextMuted,
                letterSpacing = 2.sp
            )
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    if (!isLoginMode) Text("Min. 8 characters", color = TextMuted.copy(alpha = 0.5f))
                },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Accent.copy(alpha = 0.5f),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = Accent
                ),
                shape = RoundedCornerShape(8.dp)
            )

            // Error
            if (error != null) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = error,
                    fontSize = 13.sp,
                    color = ScoreLow.copy(alpha = 0.9f)
                )
            }

            Spacer(Modifier.height(20.dp))

            // Submit button
            Button(
                onClick = {
                    if (isLoginMode) onLogin(email, password)
                    else onRegister(email, password)
                },
                enabled = !isLoading && email.isNotBlank() && password.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Accent,
                    contentColor = Surface950
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (isLoading) "Processing..."
                           else if (isLoginMode) "Sign In"
                           else "Create Account",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(Modifier.height(24.dp))
        Text(
            text = "Performance-focused vocabulary training.\nNo games. No gimmicks.",
            fontSize = 11.sp,
            color = TextMuted.copy(alpha = 0.5f),
            textAlign = TextAlign.Center,
            lineHeight = 16.sp
        )
    }
}
