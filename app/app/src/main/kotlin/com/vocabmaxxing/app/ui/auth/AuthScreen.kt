package com.vocabmaxxing.app.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vocabmaxxing.app.R
import com.vocabmaxxing.app.ui.theme.*

private val CurvedTopShape: Shape = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            moveTo(0f, size.height * 0.10f)
            quadraticBezierTo(
                size.width / 2f, -size.height * 0.10f,
                size.width, size.height * 0.10f
            )
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        return Outline.Generic(path)
    }
}

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

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SignInHeaderBg)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // ---- Top white header with statue + brand ----
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SignInHeaderBg)
                    .padding(start = 16.dp, end = 24.dp, top = 16.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.statue_head),
                    contentDescription = null,
                    modifier = Modifier
                        .height(130.dp)
                        .width(100.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(Modifier.width(12.dp))
                BrandText(modifier = Modifier.weight(1f))
            }

            // Thin olive divider line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(SignInDividerOlive)
            )

            // ---- Dark brown body with curved top ----
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(CurvedTopShape)
                    .background(SignInBodyBg)
            ) {
                // Dome + laurels background image at the bottom
                Image(
                    painter = painterResource(id = R.drawable.dome_laurels),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    contentScale = ContentScale.FillWidth
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp)
                        .padding(top = 56.dp, bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isLoginMode) "Welcome Back" else "Create Account",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SignInTextOnDark,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(28.dp))

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

                    if (error != null) {
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = error,
                            fontSize = 13.sp,
                            color = ScoreLow.copy(alpha = 0.95f),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (isLoginMode) onLogin(email, password)
                            else onRegister(email, password)
                        },
                        enabled = !isLoading && email.isNotBlank() && password.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SignInOlive,
                            contentColor = SignInTextOnOlive,
                            disabledContainerColor = SignInOliveDim.copy(alpha = 0.6f),
                            disabledContentColor = SignInTextOnOlive.copy(alpha = 0.6f)
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = if (isLoading) "Processing..."
                                   else if (isLoginMode) "Sign in"
                                   else "Sign up",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "Forgot your password?",
                        fontSize = 14.sp,
                        color = SignInMutedOnDark,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(32.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isLoginMode) "Don’t have an account?"
                                   else "Already have an account?",
                            fontSize = 14.sp,
                            color = SignInTextOnDark
                        )
                        Button(
                            onClick = { isLoginMode = !isLoginMode },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SignInOlive,
                                contentColor = SignInTextOnOlive
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = if (isLoginMode) "Sign up" else "Sign in",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(Modifier.height(120.dp))
                }
            }
        }
    }
}

@Composable
private fun BrandText(modifier: Modifier = Modifier) {
    val big = SpanStyle(
        fontSize = 30.sp,
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF1A1410)
    )
    val small = SpanStyle(
        fontSize = 22.sp,
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF1A1410)
    )
    val annotated = buildAnnotatedString {
        withStyle(big) { append("V") }
        withStyle(small) { append("OCAB") }
        withStyle(big) { append("M") }
        withStyle(small) { append("A") }
        withStyle(big) { append("XX") }
        withStyle(small) { append("ING") }
    }
    Text(
        text = annotated,
        modifier = modifier,
        textAlign = TextAlign.Start,
        letterSpacing = 1.sp
    )
}

@Composable
private fun PillTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        label = {
            Text(
                text = label,
                color = SignInMutedOnDark,
                fontSize = 14.sp
            )
        },
        textStyle = TextStyle(color = SignInTextOnDark, fontSize = 16.sp),
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = SignInFieldBorder,
            unfocusedBorderColor = SignInFieldBorder.copy(alpha = 0.7f),
            focusedTextColor = SignInTextOnDark,
            unfocusedTextColor = SignInTextOnDark,
            focusedLabelColor = SignInMutedOnDark,
            unfocusedLabelColor = SignInMutedOnDark,
            cursorColor = SignInOlive,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(14.dp)
    )
}
