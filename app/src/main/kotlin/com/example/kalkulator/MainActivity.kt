package com.example.kalkulator

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    KalkulatorApp()
                }
            }
        }
    }
}

enum class CalculatorButtonType {
    NUMBER,
    OPERATOR,
    ACTION,
    EQUALS
}

data class CalcButtonSpec(
    val label: String,
    val type: CalculatorButtonType,
    val appendValue: String = label,
    val contentDescription: String? = null
)

private val calculatorButtons = listOf(
    listOf(
        CalcButtonSpec("C", CalculatorButtonType.ACTION, contentDescription = "Clear all"),
        CalcButtonSpec("(", CalculatorButtonType.ACTION, contentDescription = "Open parenthesis"),
        CalcButtonSpec(")", CalculatorButtonType.ACTION, contentDescription = "Close parenthesis"),
        CalcButtonSpec("÷", CalculatorButtonType.OPERATOR, appendValue = "÷", contentDescription = "Divide")
    ),
    listOf(
        CalcButtonSpec("7", CalculatorButtonType.NUMBER, contentDescription = "Seven"),
        CalcButtonSpec("8", CalculatorButtonType.NUMBER, contentDescription = "Eight"),
        CalcButtonSpec("9", CalculatorButtonType.NUMBER, contentDescription = "Nine"),
        CalcButtonSpec("×", CalculatorButtonType.OPERATOR, appendValue = "×", contentDescription = "Multiply")
    ),
    listOf(
        CalcButtonSpec("4", CalculatorButtonType.NUMBER, contentDescription = "Four"),
        CalcButtonSpec("5", CalculatorButtonType.NUMBER, contentDescription = "Five"),
        CalcButtonSpec("6", CalculatorButtonType.NUMBER, contentDescription = "Six"),
        CalcButtonSpec("-", CalculatorButtonType.OPERATOR, contentDescription = "Minus")
    ),
    listOf(
        CalcButtonSpec("1", CalculatorButtonType.NUMBER, contentDescription = "One"),
        CalcButtonSpec("2", CalculatorButtonType.NUMBER, contentDescription = "Two"),
        CalcButtonSpec("3", CalculatorButtonType.NUMBER, contentDescription = "Three"),
        CalcButtonSpec("+", CalculatorButtonType.OPERATOR, contentDescription = "Plus")
    ),
    listOf(
        CalcButtonSpec("%", CalculatorButtonType.ACTION, contentDescription = "Percent"),
        CalcButtonSpec("0", CalculatorButtonType.NUMBER, contentDescription = "Zero"),
        CalcButtonSpec(".", CalculatorButtonType.NUMBER, contentDescription = "Decimal point"),
        CalcButtonSpec("=", CalculatorButtonType.EQUALS, contentDescription = "Equals")
    )
)

@Composable
fun KalkulatorApp(viewModel: CalculatorViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    LaunchedEffect(uiState.expression) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    val displayDescription = remember(uiState.expression, uiState.result, uiState.previewResult) {
        buildString {
            append("Display: ")
            append(if (uiState.expression.isEmpty()) "0" else uiState.expression)
            when {
                uiState.result != null -> append(", result: ${uiState.result}")
                uiState.previewResult != null -> append(", preview: ${uiState.previewResult}")
            }
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding(),
        color = MaterialTheme.colorScheme.background
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isLandscape = maxWidth > maxHeight

            if (isLandscape) {
                LandscapeCalculatorLayout(
                    uiState = uiState,
                    scrollState = scrollState,
                    displayDescription = displayDescription,
                    onAppend = { viewModel.onAppend(it) },
                    onBackspace = { viewModel.onBackspace() },
                    onClear = { viewModel.onClear() },
                    onCalculate = { viewModel.onCalculate() }
                )
            } else {
                PortraitCalculatorLayout(
                    uiState = uiState,
                    scrollState = scrollState,
                    displayDescription = displayDescription,
                    onAppend = { viewModel.onAppend(it) },
                    onBackspace = { viewModel.onBackspace() },
                    onClear = { viewModel.onClear() },
                    onCalculate = { viewModel.onCalculate() }
                )
            }
        }
    }
}

@Composable
private fun PortraitCalculatorLayout(
    uiState: CalculatorUiState,
    scrollState: ScrollState,
    displayDescription: String,
    onAppend: (String) -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit,
    onCalculate: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val haptic = LocalHapticFeedback.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = uiState.errorResId != null,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                if (uiState.errorResId != null) {
                    ErrorCard(errorResId = uiState.errorResId!!)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(24.dp)
                    .semantics(mergeDescendants = true) {
                        contentDescription = displayDescription
                    },
                contentAlignment = Alignment.CenterEnd
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(scrollState),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text = if (uiState.expression.isEmpty()) "0" else uiState.expression,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.End,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (uiState.result != null) {
                        Text(
                            text = uiState.result!!,
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.End,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.clickable {
                                clipboardManager.setText(AnnotatedString(uiState.result!!))
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                Toast.makeText(context, "Hasil disalin", Toast.LENGTH_SHORT).show()
                            }
                        )
                    } else if (uiState.previewResult != null) {
                        Text(
                            text = uiState.previewResult!!,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.outline,
                            textAlign = TextAlign.End,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    } else {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }

            AnimatedVisibility(
                visible = uiState.expression.isNotEmpty(),
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    BackspaceButton(onClick = onBackspace)
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            calculatorButtons.forEach { rowButtons ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowButtons.forEach { spec ->
                        CalculatorKey(
                            spec = spec,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f),
                            isLandscape = false,
                            onClick = {
                                when (spec.type) {
                                    CalculatorButtonType.EQUALS -> onCalculate()
                                    CalculatorButtonType.ACTION -> {
                                        if (spec.label == "C") {
                                            onClear()
                                        } else {
                                            onAppend(spec.appendValue)
                                        }
                                    }
                                    else -> onAppend(spec.appendValue)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LandscapeCalculatorLayout(
    uiState: CalculatorUiState,
    scrollState: ScrollState,
    displayDescription: String,
    onAppend: (String) -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit,
    onCalculate: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val haptic = LocalHapticFeedback.current

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Sisi Kiri: Panel Display yang rapi & luas
        Surface(
            modifier = Modifier
                .weight(0.95f)
                .fillMaxHeight(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header Display (Error banner atau nama aplikasi)
                Column(modifier = Modifier.fillMaxWidth()) {
                    AnimatedVisibility(
                        visible = uiState.errorResId != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        if (uiState.errorResId != null) {
                            ErrorCard(
                                errorResId = uiState.errorResId!!,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    }

                    if (uiState.errorResId == null) {
                        Text(
                            text = "Kalkulator",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                // Bagian Tengah: Tampilan Ekspresi & Hasil
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .semantics(mergeDescendants = true) {
                            contentDescription = displayDescription
                        },
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.End
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(scrollState),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text = if (uiState.expression.isEmpty()) "0" else uiState.expression,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.End,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (uiState.result != null) {
                        Text(
                            text = uiState.result!!,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.End,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.clickable {
                                clipboardManager.setText(AnnotatedString(uiState.result!!))
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                Toast.makeText(context, "Hasil disalin", Toast.LENGTH_SHORT).show()
                            }
                        )
                    } else if (uiState.previewResult != null) {
                        Text(
                            text = uiState.previewResult!!,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.outline,
                            textAlign = TextAlign.End,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    } else {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // Bagian Bawah Display: Tombol Backspace
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AnimatedVisibility(
                        visible = uiState.expression.isNotEmpty(),
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut() + scaleOut()
                    ) {
                        BackspaceButton(onClick = onBackspace)
                    }
                }
            }
        }

        // Sisi Kanan: Panel Tombol Keypad (proporsional 5 baris menyesuaikan tinggi landscape)
        Column(
            modifier = Modifier
                .weight(1.05f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            calculatorButtons.forEach { rowButtons ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    rowButtons.forEach { spec ->
                        CalculatorKey(
                            spec = spec,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            isLandscape = true,
                            onClick = {
                                when (spec.type) {
                                    CalculatorButtonType.EQUALS -> onCalculate()
                                    CalculatorButtonType.ACTION -> {
                                        if (spec.label == "C") {
                                            onClear()
                                        } else {
                                            onAppend(spec.appendValue)
                                        }
                                    }
                                    else -> onAppend(spec.appendValue)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ErrorCard(
    errorResId: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = stringResource(errorResId),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(12.dp)
        )
    }
}

@Composable
private fun BackspaceButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "backspace_scale"
    )
    val haptic = LocalHapticFeedback.current

    FilledTonalIconButton(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        },
        interactionSource = interactionSource,
        modifier = modifier
            .size(48.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .semantics {
                contentDescription = "Hapus karakter terakhir"
            }
    ) {
        Text(
            text = "⌫",
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun CalculatorKey(
    spec: CalcButtonSpec,
    modifier: Modifier = Modifier,
    isLandscape: Boolean = false,
    onClick: () -> Unit
) {
    val (containerColor, contentColor) = when (spec.type) {
        CalculatorButtonType.EQUALS -> Pair(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.onPrimary
        )
        CalculatorButtonType.OPERATOR -> Pair(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer
        )
        CalculatorButtonType.ACTION -> Pair(
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer
        )
        CalculatorButtonType.NUMBER -> Pair(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    val description = spec.contentDescription ?: spec.label
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "key_scale_${spec.label}"
    )
    val haptic = LocalHapticFeedback.current

    Button(
        onClick = {
            haptic.performHapticFeedback(
                if (spec.type == CalculatorButtonType.EQUALS) HapticFeedbackType.LongPress
                else HapticFeedbackType.TextHandleMove
            )
            onClick()
        },
        interactionSource = interactionSource,
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .semantics {
                contentDescription = description
            },
        shape = if (isLandscape) RoundedCornerShape(20.dp) else CircleShape,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Text(
            text = spec.label,
            fontSize = if (isLandscape) 20.sp else 24.sp,
            fontWeight = if (spec.type == CalculatorButtonType.NUMBER) FontWeight.Normal else FontWeight.Bold
        )
    }
}
