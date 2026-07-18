package com.vocabmaxxing.app.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vocabmaxxing.app.ui.theme.*

private const val CODE_LENGTH = 6

/**
 * Enter Verification Code page, Figma frame 281-30.
 * Front-end only: no code is actually verified — "Verify Code" just advances to
 * the reset-password screen once 6 digits are entered (TODO: real verification).
 * Per project decision the pilot uses Firebase's link-based reset, so this screen
 * is built for fidelity but stays unwired until a custom code flow lands.
 */
@Composable
fun VerificationCodeScreen(
    onVerify: (String) -> Unit,
    onNavigateSignIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    var code by remember { mutableStateOf("") }

    // Spacers follow the Figma frame (281-30) element tops: OTP row 372,
    // CTA 461, footer row 591.
    AuthScaffold(
        title = "Enter Verification Code",
        titleTopPadding = 76.dp,
        modifier = modifier
    ) {
        Spacer(Modifier.height(39.dp))

        VerificationCodeInput(
            value = code,
            onValueChange = { code = it },
            length = CODE_LENGTH
        )

        Spacer(Modifier.height(34.5.dp))

        PrimaryButton(
            text = "Verify Code",
            onClick = { onVerify(code) },
            enabled = code.length == CODE_LENGTH
        )

        Spacer(Modifier.height(63.dp))

        SignInRow(
            prompt = "Have an account?",
            buttonText = "Sign In",
            onClick = onNavigateSignIn
        )
    }
}
