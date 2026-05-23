package com.vocabmaxxing.app.ui.auth

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.compose.ui.unit.sp
import com.vocabmaxxing.app.R
import com.vocabmaxxing.app.ui.theme.*

private val CurvedTopShape: Shape = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val dipY = with(density) { 40.dp.toPx() }
        val plateauY = with(density) { 4.dp.toPx() }
        val path = Path().apply {
            moveTo(0f, dipY)
            cubicTo(
                size.width * 0.05f, plateauY,
                size.width * 0.35f, plateauY,
                size.width * 0.60f, plateauY
            )
            lineTo(size.width, plateauY)
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
    var bodyTopPx by remember { mutableStateOf(0) }
    val density = LocalDensity.current

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
                    .zIndex(2f)
                    .background(SignInHeaderBg)
                    .padding(start = 0.dp, end = 24.dp, top = 16.dp, bottom = 0.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.statue_head),
                    contentDescription = null,
                    modifier = Modifier
                        .height(210.dp)
                        .width(150.dp)
                        .offset(x = (-14).dp, y = 57.dp)
                        .zIndex(2f),
                    contentScale = ContentScale.Fit
                )
                Spacer(Modifier.width(12.dp))
                Image(
                    painter = painterResource(id = R.drawable.vocabmaxxing_wordmark),
                    contentDescription = "vocabMAXXING",
                    modifier = Modifier
                        .weight(1f)
                        .offset(y = 25.dp)
                        .offset(x = (-20).dp, y = 10.dp)
                        .height(36.dp),
                    contentScale = ContentScale.Fit
                )
            }

            // ---- Dark brown body with curved top ----
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .onGloballyPositioned { coords ->
                        bodyTopPx = coords.positionInRoot().y.toInt()
                    }
            ) {
                // Brown body
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(CurvedTopShape)
                        .background(SignInBodyBg)
                ) {
                // Dome + laurels decorative image, centered at the bottom
                Image(
                    painter = painterResource(id = R.drawable.dome_laurels),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp)
                        .fillMaxWidth(244f / 412f)
                        .aspectRatio(244f / 138.41f),
                    contentScale = ContentScale.Fit
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp)
                        .padding(top = 32.dp, bottom = 24.dp),
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
                            disabledContainerColor = SignInOlive,
                            disabledContentColor = SignInTextOnOlive
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
                        color = SignInLinkBlue,
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

        // Olive stroke that traces the curve, drawn above everything (including the statue)
        if (bodyTopPx > 0) {
            val strokeWidthDp = 14.dp
            val strokeWidthPx = with(density) { strokeWidthDp.toPx() }
            val canvasHeightDp = 60.dp
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(canvasHeightDp)
                    .offset { IntOffset(0, bodyTopPx - (strokeWidthPx / 2f).toInt()) }
                    .zIndex(10f)
            ) {
                val w = size.width
                val dipY = with(density) { 40.dp.toPx() } + strokeWidthPx / 2f
                val plateauY = with(density) { 4.dp.toPx() } + strokeWidthPx / 2f
                val curvePath = Path().apply {
                    moveTo(0f, dipY)
                    cubicTo(
                        w * 0.05f, plateauY,
                        w * 0.35f, plateauY,
                        w * 0.60f, plateauY
                    )
                    lineTo(w, plateauY)
                }
                drawPath(
                    path = curvePath,
                    color = SignInDividerOlive,
                    style = Stroke(width = strokeWidthPx)
                )
            }
        }
    }
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
