package com.vocabmaxxing.app.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.vocabmaxxing.app.R
import com.vocabmaxxing.app.ui.theme.*

private val HEADER_HEIGHT = 222.dp

/**
 * Shared chrome for all auth screens, matching the Figma wireframes:
 * statue + wordmark header overlay, brown curve body, dome/laurels footer,
 * and a Raleway-Medium title. Screen-specific content is supplied via [content];
 * the optional [bottomContent] (e.g. the "Have an account?" row) is anchored to
 * the bottom of the scroll column.
 */
@Composable
fun AuthScaffold(
    title: String,
    modifier: Modifier = Modifier,
    bottomContent: (@Composable ColumnScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
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
            Image(
                painter = painterResource(id = R.drawable.brown_curve_bg),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )

            Image(
                painter = painterResource(id = R.drawable.dome_laurels),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 9.dp)
                    .fillMaxWidth(244f / 412f)
                    .aspectRatio(244f / 138.41f),
                contentScale = ContentScale.Fit
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 17.dp)
                    .padding(top = 48.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = title,
                        fontFamily = Raleway,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Medium,
                        color = SignInTextOnDark,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(28.dp))

                    content()
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (bottomContent != null) bottomContent()
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

/** Rounded pill text field (wireframe: #2F2417 fill, white border, 20dp radius, 66.5dp tall). */
@Composable
fun PillTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(67.dp),
        label = {
            Text(
                text = label,
                color = SignInMutedOnDark,
                fontFamily = Poppins,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )
        },
        textStyle = TextStyle(color = SignInTextOnDark, fontFamily = Poppins, fontSize = 16.sp),
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = SignInFieldBorder,
            unfocusedBorderColor = SignInFieldBorder,
            focusedTextColor = SignInTextOnDark,
            unfocusedTextColor = SignInTextOnDark,
            focusedLabelColor = SignInMutedOnDark,
            unfocusedLabelColor = SignInMutedOnDark,
            cursorColor = SignInOlive,
            focusedContainerColor = SignInFieldFill,
            unfocusedContainerColor = SignInFieldFill
        ),
        shape = RoundedCornerShape(20.dp)
    )
}

/** Olive primary CTA (full width, 67dp, Poppins Medium 20sp, black text). */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
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
            text = text,
            fontFamily = Poppins,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/** Bottom "Have an account? / Sign In" row shared by every auth screen. */
@Composable
fun SignInRow(
    prompt: String,
    buttonText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = prompt,
            fontFamily = Poppins,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = SignInTextOnDark
        )
        Button(
            onClick = onClick,
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
                text = buttonText,
                fontFamily = Poppins,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * 6-cell verification-code input (wireframe: #2F2417 cells, white border,
 * 11.7dp radius, 54.5dp square, ~10.7dp gap). A single hidden BasicTextField
 * holds the digits and drives the visual cells; tapping anywhere focuses it.
 */
@Composable
fun VerificationCodeInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    length: Int = 6
) {
    BasicTextField(
        value = value,
        onValueChange = { new ->
            if (new.length <= length && new.all { it.isDigit() }) onValueChange(new)
        },
        modifier = modifier,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        cursorBrush = SolidColor(SignInOlive),
        decorationBox = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(length) { i ->
                    val char = value.getOrNull(i)?.toString() ?: ""
                    Box(
                        modifier = Modifier
                            .size(54.5.dp)
                            .background(SignInFieldFill, RoundedCornerShape(11.7.dp))
                            .border(1.dp, SignInFieldBorder, RoundedCornerShape(11.7.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = char,
                            fontFamily = Poppins,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Medium,
                            color = SignInTextOnDark
                        )
                    }
                }
            }
        }
    )
}
