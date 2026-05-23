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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.compose.ui.unit.sp
import com.vocabmaxxing.app.R
import com.vocabmaxxing.app.ui.theme.*

private val HEADER_HEIGHT = 222.dp

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
        // ---- Brown body — fills from below HEADER_HEIGHT down to the bottom ----
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = HEADER_HEIGHT)
                .zIndex(3f)
        ) {
            // Curve + brown background as a single exported PNG (transparent above the curve)
            Image(
                painter = painterResource(id = R.drawable.brown_curve_bg),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )

            // Dome + laurels at the bottom
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

            // Form column with SpaceBetween (top group anchored top, sign-up row anchored bottom)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 17.dp)
                    .padding(top = 48.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // ---- Top group ----
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isLoginMode) "Welcome Back" else "Create Account",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Medium,
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
                            .height(67.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SignInOlive,
                            contentColor = SignInTextOnOlive,
                            disabledContainerColor = SignInOlive,
                            disabledContentColor = SignInTextOnOlive
                        ),
                        shape = RoundedCornerShape(20.dp)
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
                }

                // ---- Bottom group: Sign up row + dome clearance ----
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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
                            modifier = Modifier
                                .width(137.dp)
                                .height(52.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SignInOlive,
                                contentColor = SignInTextOnOlive
                            ),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp)
                        ) {
                            Text(
                                text = if (isLoginMode) "Sign up" else "Sign in",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(Modifier.height(160.dp))
                }
            }
        }

        // ---- Header overlay (statue + wordmark) ----
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(2f)
                .padding(start = 0.dp, end = 8.dp, top = 0.dp, bottom = 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.statue_head),
                contentDescription = null,
                modifier = Modifier
                    .height(257.dp)
                    .width(182.dp)
                    .offset(x = (-31).dp, y = 55.dp)
                    .zIndex(2f),
                contentScale = ContentScale.Fit
            )
            Spacer(Modifier.width(12.dp))
            Image(
                painter = painterResource(id = R.drawable.vocabmaxxing_wordmark),
                contentDescription = "vocabMAXXING",
                modifier = Modifier
                    .weight(1f)
                    .offset(x = (-37).dp, y = 24.dp)
                    .height(35.dp),
                contentScale = ContentScale.Fit
            )
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
            .height(67.dp),
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
        shape = RoundedCornerShape(20.dp)
    )
}
