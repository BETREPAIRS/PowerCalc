package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.CalculationResult
import com.example.ui.theme.*

@Composable
fun ResultCard(
    result: CalculationResult,
    calculatorTitle: String,
    inputSummary: String,
    onSave: (CalculationResult) -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showSteps by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("result_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CALCULATION RESULT",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = result.formula,
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Primary Big Result
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = result.primaryValue,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.testTag("primary_result_value")
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = result.primaryUnit,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            // Warning Banner if present
            if (result.warning != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = HighVisOrange.copy(alpha = 0.15f),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(HighVisOrange))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Warning",
                            tint = HighVisOrange,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = result.warning,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Secondary Quantities
            if (result.secondaryValues.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    result.secondaryValues.forEach { (label, value) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = value,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Step-by-step breakdown toggle
            if (result.steps.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { showSteps = !showSteps }
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (showSteps) "Hide Step-by-Step Calculation" else "Show Step-by-Step Substitution",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        imageVector = if (showSteps) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Toggle steps",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }

                AnimatedVisibility(visible = showSteps) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.background.copy(alpha = 0.6f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Substitution: ${result.substitution}",
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        result.steps.forEach { step ->
                            Text(
                                text = step,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Standard citation note
            if (result.standardNote.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Ref: ${result.standardNote}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ThreeDButton(
                    onClick = {
                        onSave(result)
                        Toast.makeText(context, "Saved to History", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("save_calculation_button"),
                    baseColor = MaterialTheme.colorScheme.primary,
                    pressedColor = Color(0xFF00E5FF), // Dynamic color change to electric cyan on touch!
                    pressedContentColor = Slate950
                ) {
                    Icon(Icons.Default.Bookmark, contentDescription = "Save", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                ThreeDOutlinedButton(
                    onClick = {
                        val text = "${calculatorTitle}\nResult: ${result.primaryValue} ${result.primaryUnit}\nFormula: ${result.formula}\nInputs: ${inputSummary}"
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("PowerCalc Result", text))
                        Toast.makeText(context, "Result copied to clipboard", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f),
                    pressedColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }

                ThreeDIconButton(
                    onClick = {
                        val text = "${calculatorTitle} Calculation\nResult: ${result.primaryValue} ${result.primaryUnit}\nFormula: ${result.formula}\nInputs: ${inputSummary}\nStandard: ${result.standardNote}\nCalculated via PowerCalc"
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "$calculatorTitle Calculation")
                            putExtra(Intent.EXTRA_TEXT, text)
                        }
                        context.startActivity(Intent.createChooser(intent, "Share Calculation"))
                    },
                    icon = Icons.Default.Share,
                    contentDescription = "Share",
                    pressedColor = ElectricAmber
                )

                ThreeDIconButton(
                    onClick = onReset,
                    icon = Icons.Default.Refresh,
                    contentDescription = "Reset",
                    pressedColor = ElectricBlue
                )
            }
        }
    }
}

// =============================================================================
// TACTILE 3D BUTTONS & CHIPS WITH TOUCH ANIMATIONS & DYNAMIC COLOR SHIFT
// =============================================================================

@Composable
fun ThreeDButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    baseColor: Color = MaterialTheme.colorScheme.primary,
    pressedColor: Color = Color(0xFF00E5FF),
    depthColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
    contentColor: Color = Color.White,
    pressedContentColor: Color = Slate950,
    shape: RoundedCornerShape = RoundedCornerShape(12.dp),
    contentPadding: PaddingValues = PaddingValues(horizontal = 14.dp, vertical = 11.dp),
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val animatedElevation by androidx.compose.animation.core.animateDpAsState(
        targetValue = if (isPressed) 1.dp else 4.dp,
        animationSpec = androidx.compose.animation.core.spring(stiffness = androidx.compose.animation.core.Spring.StiffnessMediumLow),
        label = "btnElevation"
    )
    val animatedOffsetY by androidx.compose.animation.core.animateDpAsState(
        targetValue = if (isPressed) 3.dp else 0.dp,
        animationSpec = androidx.compose.animation.core.spring(stiffness = androidx.compose.animation.core.Spring.StiffnessMediumLow),
        label = "btnOffsetY"
    )
    val animatedScale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1.0f,
        animationSpec = androidx.compose.animation.core.spring(stiffness = androidx.compose.animation.core.Spring.StiffnessMediumLow),
        label = "btnScale"
    )
    val animatedBgColor by androidx.compose.animation.animateColorAsState(
        targetValue = if (isPressed) pressedColor else baseColor,
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 150),
        label = "btnBgColor"
    )
    val animatedTextColor by androidx.compose.animation.animateColorAsState(
        targetValue = if (isPressed) pressedContentColor else contentColor,
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 150),
        label = "btnTextColor"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
            }
            .clip(shape)
            .background(depthColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .padding(bottom = animatedElevation)
    ) {
        Surface(
            modifier = Modifier
                .offset(y = animatedOffsetY)
                .fillMaxWidth(),
            shape = shape,
            color = animatedBgColor,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = if (isPressed) 0.5f else 0.2f)),
            shadowElevation = if (isPressed) 0.dp else 2.dp
        ) {
            Row(
                modifier = Modifier
                    .padding(contentPadding)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CompositionLocalProvider(LocalContentColor provides animatedTextColor) {
                    content()
                }
            }
        }
    }
}

@Composable
fun ThreeDOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    baseColor: Color = MaterialTheme.colorScheme.surface,
    pressedColor: Color = MaterialTheme.colorScheme.primaryContainer,
    borderColor: Color = MaterialTheme.colorScheme.outline,
    shape: RoundedCornerShape = RoundedCornerShape(12.dp),
    contentPadding: PaddingValues = PaddingValues(horizontal = 14.dp, vertical = 11.dp),
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val animatedElevation by androidx.compose.animation.core.animateDpAsState(
        targetValue = if (isPressed) 1.dp else 3.dp,
        animationSpec = androidx.compose.animation.core.spring(stiffness = androidx.compose.animation.core.Spring.StiffnessMediumLow),
        label = "outlinedElevation"
    )
    val animatedOffsetY by androidx.compose.animation.core.animateDpAsState(
        targetValue = if (isPressed) 2.dp else 0.dp,
        animationSpec = androidx.compose.animation.core.spring(stiffness = androidx.compose.animation.core.Spring.StiffnessMediumLow),
        label = "outlinedOffsetY"
    )
    val animatedScale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1.0f,
        animationSpec = androidx.compose.animation.core.spring(stiffness = androidx.compose.animation.core.Spring.StiffnessMediumLow),
        label = "outlinedScale"
    )
    val animatedBgColor by androidx.compose.animation.animateColorAsState(
        targetValue = if (isPressed) pressedColor else baseColor,
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 150),
        label = "outlinedBgColor"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
            }
            .clip(shape)
            .background(borderColor.copy(alpha = 0.4f))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .padding(bottom = animatedElevation)
    ) {
        Surface(
            modifier = Modifier
                .offset(y = animatedOffsetY)
                .fillMaxWidth(),
            shape = shape,
            color = animatedBgColor,
            border = androidx.compose.foundation.BorderStroke(1.dp, borderColor.copy(alpha = 0.6f))
        ) {
            Row(
                modifier = Modifier
                    .padding(contentPadding)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                content()
            }
        }
    }
}

@Composable
fun ThreeDIconButton(
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    baseColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    pressedColor: Color = MaterialTheme.colorScheme.primary,
    iconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    pressedIconTint: Color = Color.White
) {
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val animatedScale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1.0f,
        animationSpec = androidx.compose.animation.core.spring(stiffness = androidx.compose.animation.core.Spring.StiffnessMediumLow),
        label = "iconScale"
    )
    val animatedBgColor by androidx.compose.animation.animateColorAsState(
        targetValue = if (isPressed) pressedColor else baseColor,
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 150),
        label = "iconBgColor"
    )
    val animatedIconColor by androidx.compose.animation.animateColorAsState(
        targetValue = if (isPressed) pressedIconTint else iconTint,
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 150),
        label = "iconColor"
    )

    Surface(
        modifier = modifier
            .size(44.dp)
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(12.dp),
        color = animatedBgColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
        shadowElevation = if (isPressed) 0.dp else 2.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = animatedIconColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun ThreeDChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    icon: String? = null,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    activePressedColor: Color = Color(0xFF00E5FF),
    inactiveColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    inactivePressedColor: Color = MaterialTheme.colorScheme.primaryContainer
) {
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val animatedOffsetY by androidx.compose.animation.core.animateDpAsState(
        targetValue = if (isPressed) 2.dp else 0.dp,
        animationSpec = androidx.compose.animation.core.spring(stiffness = androidx.compose.animation.core.Spring.StiffnessMediumLow),
        label = "chipOffsetY"
    )
    val animatedScale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isPressed) 0.93f else 1.0f,
        animationSpec = androidx.compose.animation.core.spring(stiffness = androidx.compose.animation.core.Spring.StiffnessMediumLow),
        label = "chipScale"
    )
    val animatedBgColor by androidx.compose.animation.animateColorAsState(
        targetValue = when {
            isPressed && selected -> activePressedColor
            isPressed && !selected -> inactivePressedColor
            selected -> activeColor
            else -> inactiveColor
        },
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 150),
        label = "chipBgColor"
    )
    val animatedTextColor by androidx.compose.animation.animateColorAsState(
        targetValue = when {
            isPressed && selected -> Slate950
            selected -> MaterialTheme.colorScheme.onPrimary
            isPressed -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.onSurface
        },
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 150),
        label = "chipTextColor"
    )

    Surface(
        modifier = modifier
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
            }
            .offset(y = animatedOffsetY)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(12.dp),
        color = animatedBgColor,
        border = androidx.compose.foundation.BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = if (selected) Color.White.copy(alpha = 0.35f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
        ),
        shadowElevation = if (selected && !isPressed) 3.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Text(icon, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = animatedTextColor
            )
        }
    }
}

@Composable
fun ThreeDCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: RoundedCornerShape = RoundedCornerShape(14.dp),
    baseColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    pressedColor: Color = Color(0xFF00E5FF).copy(alpha = 0.15f),
    borderColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
    pressedBorderColor: Color = Color(0xFF00E5FF),
    depthColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
    elevation: androidx.compose.ui.unit.Dp = 3.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val animatedElevation by androidx.compose.animation.core.animateDpAsState(
        targetValue = if (isPressed) 1.dp else elevation,
        animationSpec = androidx.compose.animation.core.spring(stiffness = androidx.compose.animation.core.Spring.StiffnessMediumLow),
        label = "cardElevation"
    )
    val animatedOffsetY by androidx.compose.animation.core.animateDpAsState(
        targetValue = if (isPressed) 2.dp else 0.dp,
        animationSpec = androidx.compose.animation.core.spring(stiffness = androidx.compose.animation.core.Spring.StiffnessMediumLow),
        label = "cardOffsetY"
    )
    val animatedScale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1.0f,
        animationSpec = androidx.compose.animation.core.spring(stiffness = androidx.compose.animation.core.Spring.StiffnessMediumLow),
        label = "cardScale"
    )
    val animatedBgColor by androidx.compose.animation.animateColorAsState(
        targetValue = if (isPressed) pressedColor else baseColor,
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 150),
        label = "cardBgColor"
    )
    val animatedBorderColor by androidx.compose.animation.animateColorAsState(
        targetValue = if (isPressed) pressedBorderColor else borderColor,
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 150),
        label = "cardBorderColor"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
            }
            .clip(shape)
            .background(depthColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .padding(bottom = animatedElevation)
    ) {
        Surface(
            modifier = Modifier
                .offset(y = animatedOffsetY)
                .fillMaxWidth(),
            shape = shape,
            color = animatedBgColor,
            border = androidx.compose.foundation.BorderStroke(if (isPressed) 1.5.dp else 1.dp, animatedBorderColor),
            shadowElevation = if (isPressed) 0.dp else 2.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                content()
            }
        }
    }
}

@Composable
fun NumericInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    unitSuffix: String = "",
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Decimal
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        trailingIcon = {
            if (unitSuffix.isNotEmpty()) {
                Text(
                    text = unitSuffix,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 12.dp)
                )
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedLabelColor = MaterialTheme.colorScheme.primary
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun SafetyDisclaimerBanner(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = "Safety",
                tint = ElectricAmber,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "PowerCalc calculations assist engineering judgment. Always verify with local electrical codes (NEC/IEC), manufacturer specs, and de-energize circuits before servicing.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )
        }
    }
}
