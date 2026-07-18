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

    // Spacers follow the Figma frame (202-88) element tops: email 367,
    // CTA 461, footer row 594.
    AuthScaffold(
        title = "Forgot Password?",
        titleTopPadding = 76.dp,
        modifier = modifier
    ) {
        Spacer(Modifier.height(34.dp))

        PillTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email Address",
            keyboardType = KeyboardType.Email
        )

        Spacer(Modifier.height(27.dp))

        PrimaryButton(
            text = "Send Verification Code",
            onClick = { onSendCode(email) },
            enabled = email.isNotBlank()
        )

        Spacer(Modifier.height(66.dp))

        SignInRow(
            prompt = "Have an account?",
            buttonText = "Sign In",
            onClick = onNavigateSignIn
        )
    }
}
