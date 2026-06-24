package com.vocabmaxxing.app.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.vocabmaxxing.app.ui.theme.*

/**
 * Forgot Password page ("Forgot Password?"), Figma frame 202-88.
 * Front-end only: no email is actually sent — the CTA just advances to the
 * verification-code screen (TODO: wire to a real send-code backend).
 */
@Composable
fun ForgotPasswordScreen(
    onSendCode: (String) -> Unit,
    onNavigateSignIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }

    AuthScaffold(
        title = "Forgot Password?",
        modifier = modifier,
        bottomContent = {
            SignInRow(
                prompt = "Have an account?",
                buttonText = "Sign In",
                onClick = onNavigateSignIn
            )
        }
    ) {
        PillTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email Address",
            keyboardType = KeyboardType.Email
        )

        Spacer(Modifier.height(24.dp))

        PrimaryButton(
            text = "Send Verification Code",
            onClick = { onSendCode(email) },
            enabled = email.isNotBlank()
        )
    }
}
