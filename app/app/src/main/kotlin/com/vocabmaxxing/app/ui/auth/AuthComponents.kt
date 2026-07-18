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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.vocabmaxxing.app.R
import com.vocabmaxxing.app.ui.theme.*

private val HEADER_HEIGHT = 222.dp

/**
 * Shared chrome for all auth screens, matching the Figma wireframes:
 * statue + wordmark header overlay, brown curve body, an optional dome/laurels
 * footer ([showDome] — Sign In only per Figma), and a Raleway-Medium title
 * placed [titleTopPadding] below the header region (per-screen Figma Y).
 *
 * The whole composition sits on a fixed-height design canvas inside a single
 * outer scroll container. With the keyboard closed the content fits the
 * viewport, so nothing can scroll; when the IME resizes the window, the entire
 * canvas (header artwork included) scrolls as one unit, so fields can never
 * slide over the header. Screens supply everything below the title — including
 * the footer row — via [content], using explicit Figma-derived spacers.
 */
@Composable
fun AuthScaffold(
    title: String,
    titleTopPadding: Dp,
    modifier: Modifier = Modifier,
    showDome: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val config = LocalConfiguration.current
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(SignInHeaderBg)
    ) {
        val viewportHeight = maxHeight
        // The tallest viewport seen for this window configuration is the
        // height with the IME closed; it keeps the canvas stable while the
        // window resizes for the keyboard. Rotation/resize resets the key.
        var canvasHeight by remember(
            config.orientation, config.screenWidthDp, config.screenHeightDp
        ) {
            mutableStateOf(viewportHeight)
        }
        if (viewportHeight > canvasHeight) canvasHeight = viewportHeight

        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState, enabled = scrollState.maxValue > 0)
        ) {
            // ---- Design canvas: header + body scroll (or stay put) as one unit ----
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = canvasHeight)
            ) {
                // ---- Brown body — below the header region; grows only on overflow ----
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = HEADER_HEIGHT)
                        .heightIn(min = canvasHeight - HEADER_HEIGHT)
                        .zIndex(3f)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.brown_curve_bg),
                        contentDescription = null,
                        modifier = Modifier.matchParentSize(),
                        contentScale = ContentScale.FillBounds
                    )

                    if (showDome) {
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
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 17.dp)
                            .padding(bottom = 9.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = title,
                            modifier = Modifier.padding(top = titleTopPadding),
                            style = TextStyle(
                                fontFamily = Raleway,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Medium,
                                color = SignInTextOnDark,
                                textAlign = TextAlign.Center,
                                // Deterministic 35dp text box so the Figma gap
                                // math below the title holds across font metrics.
                                lineHeight = 35.sp,
                                platformStyle = PlatformTextStyle(includeFontPadding = false),
                                lineHeightStyle = LineHeightStyle(
                                    alignment = LineHeightStyle.Alignment.Center,
                                    trim = LineHeightStyle.Trim.None
                                )
                            )
                        )

                        content()
                    }
                }

                // ---- Header overlay (statue + wordmark) ----
                HeaderOverlay()
            }
        }
    }
}

/** Statue + wordmark header overlay, drawn at the top of the design canvas. */
@Composable
private fun HeaderOverlay() {
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

/**
 * Rounded pill text field (wireframe: #2F2417 fill, white border, 20dp radius,
 * 66.5dp tall). Persistent inline label per Figma: white Poppins 16 label,
 * top-anchored inside the pill; typed text renders below it and the label
 * never floats or cuts the border.
 */
@Composable
fun PillTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(67.dp),
        textStyle = TextStyle(
            color = SignInTextOnDark,
            fontFamily = Poppins,
            fontSize = 16.sp,
            lineHeight = 20.sp
        ),
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        cursorBrush = SolidColor(SignInOlive),
        decorationBox = { innerTextField ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SignInFieldFill, RoundedCornerShape(20.dp))
                    .border(1.dp, SignInFieldBorder, RoundedCornerShape(20.dp))
                    .padding(horizontal = 19.dp)
                    .padding(top = 10.dp)
            ) {
                Text(
                    text = label,
                    color = SignInTextOnDark,
                    fontFamily = Poppins,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 20.sp
                )
                Spacer(Modifier.height(2.dp))
                Box(Modifier.fillMaxWidth()) { innerTextField() }
            }
        }
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

/**
 * Validation/auth error text. Renders nothing when [message] is null so the
 * clean layout stays wireframe-exact; when a message appears it inserts
 * between the last field and the CTA and transiently pushes the content
 * below it down until the error clears.
 */
@Composable
fun AuthErrorText(message: String?, modifier: Modifier = Modifier) {
    if (message == null) return
    Text(
        text = message,
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        style = TextStyle(
            fontFamily = Poppins,
            fontSize = 13.sp,
            lineHeight = 16.sp,
            color = ScoreLow.copy(alpha = 0.95f),
            textAlign = TextAlign.Center,
            platformStyle = PlatformTextStyle(includeFontPadding = false),
            lineHeightStyle = LineHeightStyle(
                alignment = LineHeightStyle.Alignment.Center,
                trim = LineHeightStyle.Trim.None
            )
        ),
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
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
