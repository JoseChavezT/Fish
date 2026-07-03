package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.AmoledBackground
import com.example.ui.theme.CardBackground
import com.example.ui.theme.MainGreen
import com.example.ui.theme.WhiteText
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SubtleBorder
import com.example.ui.theme.GrayButton
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                CalculadoraScreen()
            }
        }
    }
}

@Composable
fun CalculadoraScreen(
    viewModel: PalletCalculatorViewModel = viewModel()
) {
    val input by viewModel.jabasInput.collectAsStateWithLifecycle()
    val resultText by viewModel.resultText.collectAsStateWithLifecycle()
    val pallets by viewModel.palletsCount.collectAsStateWithLifecycle()
    val pucho by viewModel.puchoCount.collectAsStateWithLifecycle()
    val triggerCount by viewModel.animationTrigger.collectAsStateWithLifecycle()

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val haptic = LocalHapticFeedback.current
    val focusManager = LocalFocusManager.current

    // Spring animation scale for the results section
    val resultScale = remember { Animatable(1f) }

    LaunchedEffect(triggerCount) {
        if (triggerCount > 0) {
            resultScale.animateTo(
                targetValue = 0.88f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)
            )
            resultScale.animateTo(
                targetValue = 1.0f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)
            )
        }
    }

    // Autofocus keyboard on launch
    LaunchedEffect(Unit) {
        delay(150)
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = AmoledBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(AmoledBackground)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ==========================================
            // PARTE SUPERIOR (Header)
            // ==========================================
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "CALCULADORA DE PALLETS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SecondaryText,
                    letterSpacing = 2.sp
                )
            }

            // ==========================================
            // CENTRO (Cantidad & TextField & Resultado)
            // ==========================================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(resultScale.value)
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Cantidad de jabas",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = WhiteText
                )
                
                Spacer(modifier = Modifier.height(10.dp))

                TextField(
                    value = input,
                    onValueChange = { viewModel.onJabasInputChanged(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .testTag("jabas_input"),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color.White
                    ),
                    placeholder = {
                        Text(
                            text = "0",
                            modifier = Modifier.fillMaxWidth(),
                            fontSize = 44.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = Color(0xFF2E2E2E)
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.triggerCalculationAnimation()
                        }
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = MainGreen,
                        unfocusedIndicatorColor = SubtleBorder,
                        disabledIndicatorColor = Color.Transparent,
                        cursorColor = MainGreen
                    ),
                    trailingIcon = {
                        if (input.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    viewModel.clearInput()
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                },
                                modifier = Modifier.testTag("clear_input_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = "Borrar entrada",
                                    tint = SecondaryText
                                )
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Information details
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Info,
                        contentDescription = "Información",
                        tint = SecondaryText,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "1 pallet = 72 jabas",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = SecondaryText
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Output text format
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("result_display")
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedContent(
                        targetState = resultText,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(220, delayMillis = 40)) +
                            scaleIn(initialScale = 0.94f, animationSpec = tween(220, delayMillis = 40)) togetherWith
                            fadeOut(animationSpec = tween(90))
                        },
                        label = "resultTextAnim"
                    ) { targetText ->
                        FormattedResultText(targetText)
                    }
                }
            }

            // ==========================================
            // PARTE INFERIOR (Botones de Acción)
            // ==========================================
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Main CALCULAR button
                Button(
                    onClick = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.triggerCalculationAnimation()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .testTag("calcular_button"),
                    shape = RoundedCornerShape(29.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MainGreen,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "CALCULAR",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }

                // Secondary LIMPIAR button
                TextButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.clearInput()
                        focusRequester.requestFocus()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("limpiar_button")
                        .border(1.dp, SubtleBorder, RoundedCornerShape(25.dp)),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = GrayButton,
                        contentColor = WhiteText
                    )
                ) {
                    Text(
                        text = "LIMPIAR",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = SecondaryText,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

/**
 * Custom Composable to format the calculated text, emphasizing and sizing numbers
 * approximately double the surrounding labels.
 */
@Composable
fun FormattedResultText(text: String, modifier: Modifier = Modifier) {
    val annotatedString = buildAnnotatedString {
        val pattern = Regex("(\\d+)|([^\\d]+)")
        val matches = pattern.findAll(text)
        for (match in matches) {
            val value = match.value
            val isNumber = value.all { it.isDigit() }
            if (isNumber) {
                withStyle(style = SpanStyle(
                    fontSize = 46.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MainGreen
                )) {
                    append(value)
                }
            } else {
                val color = if (value.trim() == "+") SecondaryText else WhiteText
                val size = if (value.trim() == "+") 24.sp else 22.sp
                val weight = if (value.trim() == "+") FontWeight.Light else FontWeight.Medium
                withStyle(style = SpanStyle(
                    fontSize = size,
                    fontWeight = weight,
                    color = color
                )) {
                    append(value)
                }
            }
        }
    }
    Text(
        text = annotatedString,
        modifier = modifier,
        textAlign = TextAlign.Center,
        lineHeight = 52.sp
    )
}
