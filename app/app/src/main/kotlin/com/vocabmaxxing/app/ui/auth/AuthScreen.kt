package com.vocabmaxxing.app.ui.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import com.vocabmaxxing.app.ui.theme.*

/**
 * Sign In page ("Welcome Back"), Figma frame 242-2808. Sign Up and password
 * recovery live on their own routes; this screen only logs in and navigates out.
 */
@Composable
fun AuthScreen(
    onLogin: (String, String) -> Unit,
    onNavigateSignUp: () -> Unit,
    onForgotPassword: () -> Unit,
    isLoading: Boolean,
    error: String?,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    AuthScaffold(
        title = "Welcome Back",
        modifier = modifier,
        showDome = true,
        bottomContent = {
            SignInRow(
                prompt = "Don’t have an account?",
                buttonText = "Sign up",
                onClick = onNavigateSignUp
            )
        }
    ) {
        PillTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email Address",
            keyboardType = KeyboardType.Email
        )

        Spacer(Modifier.height(16.dp))

        PillTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            keyboardType = KeyboardType.Password,
            isPassword = true
        )

        ErrorSlot(message = error)

        PrimaryButton(
            text = if (isLoading) "Processing..." else "Sign in",
            onClick = { onLogin(email, password) },
            enabled = !isLoading && email.isNotBlank() && password.isNotBlank()
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Forgot your password?",
            fontFamily = Poppins,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = SignInLinkBlue,
            textAlign = TextAlign.Center,
            modifier = Modifier.clickableNoRipple(onForgotPassword)
        )
    }
}

/** Small helper so the "Forgot your password?" text behaves as a link. */
@Composable
private fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier {
    val interaction = remember { MutableInteractionSource() }
    return this.clickable(
        interactionSource = interaction,
        indication = null,
        onClick = onClick
    )
}
