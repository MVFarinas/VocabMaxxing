package com.vocabmaxxing.app.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

/**
 * Sign Up page ("Create An Account"), Figma frame 200-30.
 * Name is captured but not yet sent to the backend (front-end only — TODO);
 * registration uses email + password via the existing AuthViewModel.
 */
@Composable
fun SignUpScreen(
    onRegister: (String, String) -> Unit,
    onNavigateSignIn: () -> Unit,
    isLoading: Boolean,
    error: String?,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val passwordsMatch = password == confirmPassword
    val canSubmit = !isLoading && name.isNotBlank() && email.isNotBlank() &&
        password.isNotBlank() && confirmPassword.isNotBlank() && passwordsMatch

    // Spacers follow the Figma frame (200-30) element tops: name 329,
    // email 409, password 491.5, confirm 580, CTA 662, footer row 779.
    AuthScaffold(
        title = "Create An Account",
        titleTopPadding = 61.dp,
        modifier = modifier
    ) {
        Spacer(Modifier.height(11.dp))

        PillTextField(
            value = name,
            onValueChange = { name = it },
            label = "Name"
        )

        Spacer(Modifier.height(13.dp))

        PillTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email Address",
            keyboardType = KeyboardType.Email
        )

        Spacer(Modifier.height(15.5.dp))

        PillTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            keyboardType = KeyboardType.Password,
            isPassword = true
        )

        Spacer(Modifier.height(21.5.dp))

        PillTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Confirm Password",
            keyboardType = KeyboardType.Password,
            isPassword = true
        )

        Spacer(Modifier.height(15.dp))

        val message = when {
            error != null -> error
            confirmPassword.isNotBlank() && !passwordsMatch -> "Passwords do not match."
            else -> null
        }
        AuthErrorText(message = message)

        PrimaryButton(
            text = if (isLoading) "Processing..." else "Create Account",
            onClick = { onRegister(email, password) },
            enabled = canSubmit
        )

        Spacer(Modifier.height(50.dp))

        SignInRow(
            prompt = "Have an account?",
            buttonText = "Sign In",
            onClick = onNavigateSignIn
        )
    }
}
