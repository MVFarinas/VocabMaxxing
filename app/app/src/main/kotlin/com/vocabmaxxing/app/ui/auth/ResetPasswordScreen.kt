package com.vocabmaxxing.app.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

/**
 * Enter New Password (reset) page, Figma frame 283-1327.
 * Front-end only: no password is actually changed — "Reset Password" just
 * returns to Sign In once both fields match (TODO: real reset backend).
 */
@Composable
fun ResetPasswordScreen(
    onResetPassword: (String) -> Unit,
    onNavigateSignIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val passwordsMatch = password == confirmPassword
    val canSubmit = password.isNotBlank() && confirmPassword.isNotBlank() && passwordsMatch

    AuthScaffold(
        title = "Enter New Password",
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
            value = password,
            onValueChange = { password = it },
            label = "Enter New Password",
            keyboardType = KeyboardType.Password,
            isPassword = true
        )

        Spacer(Modifier.height(16.dp))

        PillTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Confirm New Password",
            keyboardType = KeyboardType.Password,
            isPassword = true
        )

        val message = if (confirmPassword.isNotBlank() && !passwordsMatch) {
            "Passwords do not match."
        } else {
            null
        }
        ErrorSlot(message = message)

        PrimaryButton(
            text = "Reset Password",
            onClick = { onResetPassword(password) },
            enabled = canSubmit
        )
    }
}
