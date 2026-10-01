package com.example.ui.screens

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.ElectricalFormulas
import com.example.engine.UnitConverterEngine
import com.example.reference.ReferenceData
import com.example.reference.TroubleshootingGuide
import com.example.ui.ElectricianViewModel
import com.example.ui.MainNavTab
import com.example.ui.components.NumericInputField
import com.example.ui.components.ResultCard
import com.example.ui.components.ThreeDButton
import com.example.ui.components.ThreeDChip
import com.example.ui.components.ThreeDIconButton
import com.example.ui.theme.*

@Composable
fun FieldToolsScreen(
    viewModel: ElectricianViewModel,
    modifier: Modifier = Modifier
) {
    val selectedSubTool by viewModel.selectedToolTab.collectAsState()
    val isFlashlightOn by viewModel.isFlashlightOn.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "FIELD TOOLBOX",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Converters, reference manuals, diagnostic assistants & decoders",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Quick Flashlight 3D Toggle Button
            ThreeDIconButton(
                onClick = { viewModel.toggleFlashlight() },
                icon = if (isFlashlightOn) Icons.Default.FlashlightOn else Icons.Default.FlashlightOff,
                contentDescription = "Flashlight",
                baseColor = if (isFlashlightOn) ElectricAmber else MaterialTheme.colorScheme.surfaceVariant,
                pressedColor = Color(0xFF00E5FF),
                iconTint = if (isFlashlightOn) Slate950 else MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tool selector 3D tabs
        val subTools = listOf(
            Pair("manuals", "📁 PDF Manuals"),
            Pair("converter", "⚡ Converter"),
            Pair("reference", "📖 Reference"),
            Pair("bending", "📐 Bending"),
            Pair("troubleshoot", "Troubleshooting"),
            Pair("resistor", "Resistor Code"),
            Pair("capacitor", "Capacitor Code"),
            Pair("continuity", "Continuity Beep")
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(subTools) { (id, label) ->
                ThreeDChip(
                    selected = selectedSubTool == id,
                    onClick = { viewModel.setSelectedToolTab(id) },
                    label = label,
                    modifier = Modifier.testTag("tool_chip_$id")
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Active tool content
        Box(modifier = Modifier.weight(1f)) {
            when (selectedSubTool) {
                "manuals" -> PdfManualsScreen(viewModel = viewModel)
                "bending" -> ConduitBendingScreen(
                    viewModel = viewModel,
                    onBack = { viewModel.setTab(MainNavTab.CALCULATORS) }
                )
                "converter" -> UnitConverterScreen(viewModel = viewModel)
                "reference" -> ReferenceScreen(viewModel = viewModel)
                "resistor" -> ResistorColorCodeTool()
                "capacitor" -> CapacitorDecoderTool()
                "troubleshoot" -> TroubleshootingAssistantTool()
                "continuity" -> ContinuityTesterTool()
                else -> PdfManualsScreen(viewModel = viewModel)
            }
        }
    }
}

// -------------------------------------------------------------------------
// 1. RESISTOR COLOR CODE TOOL WITH VISUAL RESISTOR PREVIEW
// -------------------------------------------------------------------------
@Composable
fun ResistorColorCodeTool() {
    var band1Idx by remember { mutableIntStateOf(1) } // Brown (1)
    var band2Idx by remember { mutableIntStateOf(0) } // Black (0)
    var multIdx by remember { mutableIntStateOf(2) }  // Red (x100) -> 1 kΩ
    var tolIdx by remember { mutableIntStateOf(10) }  // Gold (±5%)

    val result = remember(band1Idx, band2Idx, multIdx, tolIdx) {
        ElectricalFormulas.decodeResistor4Band(band1Idx, band2Idx, multIdx, tolIdx)
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 80.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Visual Graphical Resistor
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "RESISTOR PREVIEW",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // Graphic representation
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Left wire lead
                        Box(
                            modifier = Modifier
                                .width(30.dp)
                                .height(4.dp)
                                .background(Color(0xFFC0C0C0))
                        )
                        // Resistor Body (Beige ceramic)
                        Box(
                            modifier = Modifier
                                .width(180.dp)
                                .height(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFD7CCC8))
                                .border(1.dp, Color(0xFF8D6E63), RoundedCornerShape(12.dp))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Band 1
                                Box(
                                    modifier = Modifier
                                        .width(10.dp)
                                        .fillMaxHeight()
                                        .background(Color(ElectricalFormulas.colorBands[band1Idx].colorHex))
                                )
                                // Band 2
                                Box(
                                    modifier = Modifier
                                        .width(10.dp)
                                        .fillMaxHeight()
                                        .background(Color(ElectricalFormulas.colorBands[band2Idx].colorHex))
                                )
                                // Multiplier Band
                                Box(
                                    modifier = Modifier
                                        .width(10.dp)
                                        .fillMaxHeight()
                                        .background(Color(ElectricalFormulas.colorBands[multIdx].colorHex))
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                // Tolerance Band
                                Box(
                                    modifier = Modifier
                                        .width(10.dp)
                                        .fillMaxHeight()
                                        .background(Color(ElectricalFormulas.colorBands[tolIdx].colorHex))
                                )
                            }
                        }
                        // Right wire lead
                        Box(
                            modifier = Modifier
                                .width(30.dp)
                                .height(4.dp)
                                .background(Color(0xFFC0C0C0))
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "${result.primaryValue}  ${result.primaryUnit}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Range: ${result.secondaryValues["Tolerance Range"] ?: ""}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Band 1 Selector
        item {
            BandColorPickerRow(
                title = "1st Digit (Band 1)",
                selectedIndex = band1Idx,
                filterDigitsOnly = true,
                onSelect = { band1Idx = it }
            )
        }

        // Band 2 Selector
        item {
            BandColorPickerRow(
                title = "2nd Digit (Band 2)",
                selectedIndex = band2Idx,
                filterDigitsOnly = true,
                onSelect = { band2Idx = it }
            )
        }

        // Multiplier Selector
        item {
            BandColorPickerRow(
                title = "Multiplier",
                selectedIndex = multIdx,
                filterDigitsOnly = false,
                onSelect = { multIdx = it }
            )
        }

        // Tolerance Selector
        item {
            BandColorPickerRow(
                title = "Tolerance",
                selectedIndex = tolIdx,
                filterToleranceOnly = true,
                onSelect = { tolIdx = it }
            )
        }
    }
}

@Composable
fun BandColorPickerRow(
    title: String,
    selectedIndex: Int,
    filterDigitsOnly: Boolean = false,
    filterToleranceOnly: Boolean = false,
    onSelect: (Int) -> Unit
) {
    Column {
        Text(text = title, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val list = ElectricalFormulas.colorBands.mapIndexed { idx, band -> Pair(idx, band) }
                .filter { (_, band) ->
                    if (filterDigitsOnly) band.digit != null
                    else if (filterToleranceOnly) band.tolerance != null
                    else true
                }

            items(list) { (idx, band) ->
                val isSelected = idx == selectedIndex
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                    border = if (isSelected) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary)) else null,
                    modifier = Modifier.clickable { onSelect(idx) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(Color(band.colorHex))
                                .border(1.dp, Color.Gray, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = band.name, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// 2. CAPACITOR 3-DIGIT DECODER
// -------------------------------------------------------------------------
@Composable
fun CapacitorDecoderTool() {
    var code by remember { mutableStateOf("104") }
    val result = remember(code) {
        ElectricalFormulas.decodeCapacitorCode(code)
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "EIA-198 Capacitor 3-Digit Code Decoder",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Enter standard 3-digit ceramic or film capacitor marking code (e.g. 104, 473, 222).",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        NumericInputField(
            value = code,
            onValueChange = { code = it },
            label = "Capacitor Code",
            unitSuffix = "Code",
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
        )

        // Preset quick buttons
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("102", "103", "104", "473", "224", "105").forEach { preset ->
                FilterChip(
                    selected = code == preset,
                    onClick = { code = preset },
                    label = { Text(preset) }
                )
            }
        }

        if (result.isSuccess) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "DECODED VALUE",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = result.primaryValue,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(10.dp))
                    result.secondaryValues.forEach { (label, value) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// 3. UNIVERSAL ELECTRICAL & PHYSICAL UNIT CONVERTER
// -------------------------------------------------------------------------
@Composable
fun UniversalUnitConverterTool() {
    val categories = UnitConverterEngine.categories
    var selectedCat by remember { mutableStateOf(categories.first()) }
    var fromUnit by remember { mutableStateOf(selectedCat.units.first()) }
    var toUnit by remember { mutableStateOf(selectedCat.units.getOrElse(1) { selectedCat.units.first() }) }
    var inputValue by remember { mutableStateOf("10") }

    val converted = remember(inputValue, fromUnit, toUnit) {
        val d = inputValue.toDoubleOrNull() ?: 0.0
        UnitConverterEngine.convert(d, fromUnit, toUnit)
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 80.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text("Category:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat ->
                    FilterChip(
                        selected = cat.id == selectedCat.id,
                        onClick = {
                            selectedCat = cat
                            fromUnit = cat.units.first()
                            toUnit = cat.units.getOrElse(1) { cat.units.first() }
                        },
                        label = { Text(cat.name) }
                    )
                }
            }
        }

        item {
            NumericInputField(
                value = inputValue,
                onValueChange = { inputValue = it },
                label = "Input Value",
                unitSuffix = fromUnit.symbol
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("From Unit", style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    selectedCat.units.forEach { u ->
                        FilterChip(
                            selected = u.id == fromUnit.id,
                            onClick = { fromUnit = u },
                            label = { Text("${u.name} (${u.symbol})", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                IconButton(
                    onClick = {
                        val temp = fromUnit
                        fromUnit = toUnit
                        toUnit = temp
                    }
                ) {
                    Icon(Icons.Default.SwapHoriz, contentDescription = "Swap")
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("To Unit", style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    selectedCat.units.forEach { u ->
                        FilterChip(
                            selected = u.id == toUnit.id,
                            onClick = { toUnit = u },
                            label = { Text("${u.name} (${u.symbol})", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "CONVERTED RESULT",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = String.format(java.util.Locale.US, "%.4f %s", converted, toUnit.symbol),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$inputValue ${fromUnit.symbol} = ${String.format(java.util.Locale.US, "%.4f", converted)} ${toUnit.symbol}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// 4. TROUBLESHOOTING ASSISTANT DECISION TREE
// -------------------------------------------------------------------------
@Composable
fun TroubleshootingAssistantTool() {
    val guides = ReferenceData.troubleshootingGuides
    var selectedGuide by remember { mutableStateOf(guides.first()) }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 80.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text("Select Symptom / Fault Category:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(guides) { g ->
                    FilterChip(
                        selected = g.id == selectedGuide.id,
                        onClick = { selectedGuide = g },
                        label = { Text(g.title) }
                    )
                }
            }
        }

        item {
            Text(
                text = selectedGuide.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(selectedGuide.steps) { step ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = step.question,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "What to Check:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    step.checks.forEach { c ->
                        Text(
                            text = "• $c",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = HighVisOrange.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "Safety: ${step.safetyWarning}",
                            style = MaterialTheme.typography.labelSmall,
                            color = HighVisOrange,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Recommended Action: ${step.possibleRemedy}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// 5. CONTINUITY BEEP & HAPTIC TESTER SIMULATOR
// -------------------------------------------------------------------------
@Composable
fun ContinuityTesterTool() {
    val context = LocalContext.current
    var isBeeping by remember { mutableStateOf(false) }

    fun playBeepTone() {
        try {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(150)
            }
        } catch (_: Exception) {}
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Continuity Tone & Haptic Test",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Simulates multimeter continuity buzzer (< 30Ω) with acoustic/haptic feedback for testing in noisy environments.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(if (isBeeping) HighVisGreen else MaterialTheme.colorScheme.surfaceVariant)
                .clickable {
                    isBeeping = true
                    playBeepTone()
                },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Continuity",
                    tint = if (isBeeping) Slate950 else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isBeeping) "CLOSED CIRCUIT\n0.2 Ω" else "TAP PROBE\n(TEST)",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isBeeping) Slate950 else MaterialTheme.colorScheme.onSurface,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }

        if (isBeeping) {
            Button(
                onClick = { isBeeping = false },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Release Probes")
            }
        }
    }
}
