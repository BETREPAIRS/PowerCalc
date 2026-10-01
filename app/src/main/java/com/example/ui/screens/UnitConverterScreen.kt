package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.ConversionCategory
import com.example.engine.ConversionUnit
import com.example.engine.UnitConverterEngine
import com.example.ui.ElectricianViewModel
import com.example.ui.components.*
import com.example.ui.theme.*
import java.util.Locale
import kotlin.math.roundToInt

enum class ConverterSection(val id: String, val title: String, val icon: String) {
    ELECTRICAL("electrical", "Electrical", "⚡"),
    POWER_SYSTEM("power_system", "Power System", "🔌"),
    BATTERY("battery", "Battery", "🔋"),
    SOLAR("solar", "Solar PV", "☀️"),
    CABLE("cable", "Cable & Wire", "🔧"),
    GENERAL("general", "General", "🌡️"),
    ELECTRONICS("electronics", "Electronics / RF", "📡")
}

@Composable
fun UnitConverterScreen(
    viewModel: ElectricianViewModel,
    modifier: Modifier = Modifier
) {
    var activeSection by remember { mutableStateOf(ConverterSection.ELECTRICAL) }
    val defaultV by viewModel.defaultVoltage.collectAsState()
    val defaultFreq by viewModel.defaultFrequency.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Screen Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "ELECTRICAL UNIT CONVERTER",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Comprehensive multi-domain engineering conversion utility",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Top Category Bar
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(ConverterSection.values()) { sec ->
                com.example.ui.components.ThreeDChip(
                    selected = activeSection == sec,
                    onClick = { activeSection = sec },
                    icon = sec.icon,
                    label = sec.title,
                    modifier = Modifier.testTag("converter_tab_${sec.id}")
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Section Content
        Box(modifier = Modifier.weight(1f)) {
            when (activeSection) {
                ConverterSection.ELECTRICAL -> ElectricalSection(defaultFreq = defaultFreq)
                ConverterSection.POWER_SYSTEM -> PowerSystemSection(defaultV = defaultV, defaultFreq = defaultFreq)
                ConverterSection.BATTERY -> BatterySection()
                ConverterSection.SOLAR -> SolarSection()
                ConverterSection.CABLE -> CableSection()
                ConverterSection.GENERAL -> GeneralSection()
                ConverterSection.ELECTRONICS -> ElectronicsSection()
            }
        }
    }
}

// =============================================================================
// 1. ⚡ ELECTRICAL SECTION
// =============================================================================
@Composable
fun ElectricalSection(defaultFreq: Double) {
    val subOptions = listOf(
        "Voltage", "Current", "Resistance", "Power", "Energy",
        "Frequency", "Capacitance", "Inductance", "Conductance", "Impedance"
    )
    var selectedSub by remember { mutableStateOf("Voltage") }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(subOptions) { opt ->
                SuggestionChip(
                    onClick = { selectedSub = opt },
                    label = { Text(opt, fontSize = 12.sp, fontWeight = if (selectedSub == opt) FontWeight.Bold else FontWeight.Normal) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = if (selectedSub == opt) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        when (selectedSub) {
            "Voltage" -> StandardUnitConverterCard(category = UnitConverterEngine.standardCategories.first { it.id == "voltage" })
            "Current" -> StandardUnitConverterCard(category = UnitConverterEngine.standardCategories.first { it.id == "current" })
            "Resistance" -> StandardUnitConverterCard(category = UnitConverterEngine.standardCategories.first { it.id == "resistance" })
            "Power" -> StandardUnitConverterCard(category = UnitConverterEngine.standardCategories.first { it.id == "power" })
            "Energy" -> StandardUnitConverterCard(category = UnitConverterEngine.standardCategories.first { it.id == "energy" })
            "Frequency" -> StandardUnitConverterCard(category = UnitConverterEngine.standardCategories.first { it.id == "frequency" })
            "Capacitance" -> StandardUnitConverterCard(category = UnitConverterEngine.standardCategories.first { it.id == "capacitance" })
            "Inductance" -> StandardUnitConverterCard(category = UnitConverterEngine.standardCategories.first { it.id == "inductance" })
            "Conductance" -> StandardUnitConverterCard(category = UnitConverterEngine.standardCategories.first { it.id == "conductance" })
            "Impedance" -> ImpedanceConverterCard(defaultFreq = defaultFreq)
        }
    }
}

@Composable
fun ImpedanceConverterCard(defaultFreq: Double) {
    var mode by remember { mutableIntStateOf(0) } // 0: Rectangular to Polar, 1: Polar to Rect, 2: Reactance from L&C

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ThreeDChip(
                    selected = mode == 0,
                    onClick = { mode = 0 },
                    label = "R + jX ➔ |Z|∠θ"
                )
                ThreeDChip(
                    selected = mode == 1,
                    onClick = { mode = 1 },
                    label = "|Z|∠θ ➔ R + jX"
                )
                ThreeDChip(
                    selected = mode == 2,
                    onClick = { mode = 2 },
                    label = "L & C Reactance"
                )
            }
        }

        if (mode == 0) {
            item {
                var rText by remember { mutableStateOf("10.0") }
                var xText by remember { mutableStateOf("5.0") }

                val rVal = rText.toDoubleOrNull() ?: 0.0
                val xVal = xText.toDoubleOrNull() ?: 0.0
                val polar = UnitConverterEngine.rectangularToPolar(rVal, xVal)

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Rectangular to Polar Impedance", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text("Convert resistance (R) and reactance (X) to magnitude |Z| and phase angle θ", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            NumericInputField(
                                value = rText,
                                onValueChange = { rText = it },
                                label = "Resistance R (Ω)",
                                modifier = Modifier.weight(1f)
                            )
                            NumericInputField(
                                value = xText,
                                onValueChange = { xText = it },
                                label = "Reactance X (Ω)",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        ConvertedResultBanner(
                            primary = "${String.format(Locale.US, "%.3f", polar.magnitude)} Ω ∠ ${String.format(Locale.US, "%.2f", polar.angleDegrees)}°",
                            label = "Total Impedance |Z| ∠ θ",
                            subtext = "Z = ${String.format(Locale.US, "%.2f", polar.magnitude)} Ω, Phase angle = ${String.format(Locale.US, "%.2f", polar.angleDegrees)}° (${if (xVal >= 0) "Inductive / Lagging" else "Capacitive / Leading"})"
                        )
                    }
                }
            }
        } else if (mode == 1) {
            item {
                var magText by remember { mutableStateOf("11.18") }
                var degText by remember { mutableStateOf("26.57") }

                val magVal = magText.toDoubleOrNull() ?: 0.0
                val degVal = degText.toDoubleOrNull() ?: 0.0
                val rect = UnitConverterEngine.polarToRectangular(magVal, degVal)

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Polar to Rectangular Impedance", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text("Convert magnitude |Z| and angle θ into real R and imaginary X", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            NumericInputField(
                                value = magText,
                                onValueChange = { magText = it },
                                label = "Magnitude |Z| (Ω)",
                                modifier = Modifier.weight(1f)
                            )
                            NumericInputField(
                                value = degText,
                                onValueChange = { degText = it },
                                label = "Angle θ (°)",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        val sign = if (rect.imagX >= 0) "+" else "-"
                        ConvertedResultBanner(
                            primary = "${String.format(Locale.US, "%.3f", rect.realR)} $sign j${String.format(Locale.US, "%.3f", kotlin.math.abs(rect.imagX))} Ω",
                            label = "Complex Impedance R + jX",
                            subtext = "Resistance R = ${String.format(Locale.US, "%.3f", rect.realR)} Ω, Reactance X = ${String.format(Locale.US, "%.3f", rect.imagX)} Ω"
                        )
                    }
                }
            }
        } else {
            item {
                var lMhText by remember { mutableStateOf("50.0") } // mH
                var cUfText by remember { mutableStateOf("20.0") } // µF
                var freqText by remember { mutableStateOf(defaultFreq.toString()) }

                val lMh = lMhText.toDoubleOrNull() ?: 0.0
                val cUf = cUfText.toDoubleOrNull() ?: 0.0
                val freq = freqText.toDoubleOrNull() ?: 50.0

                val reactance = UnitConverterEngine.calculateReactance(
                    lHenry = lMh * 1e-3,
                    cFarad = cUf * 1e-6,
                    freqHz = freq
                )

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Inductive & Capacitive Reactance", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text("Calculate XL, XC, Net Reactance, and Resonance frequency f0", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            NumericInputField(
                                value = lMhText,
                                onValueChange = { lMhText = it },
                                label = "Inductance L (mH)",
                                modifier = Modifier.weight(1f)
                            )
                            NumericInputField(
                                value = cUfText,
                                onValueChange = { cUfText = it },
                                label = "Capacitance C (µF)",
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        NumericInputField(
                            value = freqText,
                            onValueChange = { freqText = it },
                            label = "Frequency f (Hz)",
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        ConvertedResultBanner(
                            primary = "XL = ${String.format(Locale.US, "%.2f", reactance.xlOhm)} Ω | XC = ${String.format(Locale.US, "%.2f", reactance.xcOhm)} Ω",
                            label = "Net X = ${String.format(Locale.US, "%.2f", reactance.netX)} Ω (${if (reactance.netX >= 0) "Inductive" else "Capacitive"})",
                            subtext = "Resonant frequency f0 = 1/(2π√LC) = ${String.format(Locale.US, "%.1f", reactance.resonantFreqHz)} Hz"
                        )
                    }
                }
            }
        }
    }
}

// =============================================================================
// 2. 🔌 POWER SYSTEM SECTION
// =============================================================================
@Composable
fun PowerSystemSection(defaultV: Double, defaultFreq: Double) {
    val subOptions = listOf(
        "kW ↔ HP", "kW ↔ kVA", "kVA ↔ MVA", "kvar ↔ kVAR", "PF ↔ Angle",
        "Single Phase", "Three Phase"
    )
    var selectedSub by remember { mutableStateOf("kW ↔ HP") }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(subOptions) { opt ->
                SuggestionChip(
                    onClick = { selectedSub = opt },
                    label = { Text(opt, fontSize = 12.sp, fontWeight = if (selectedSub == opt) FontWeight.Bold else FontWeight.Normal) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = if (selectedSub == opt) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            when (selectedSub) {
                "kW ↔ HP" -> item { KwHpCard() }
                "kW ↔ kVA" -> item { KwKvaCard() }
                "kVA ↔ MVA" -> item { KvaMvaCard() }
                "kvar ↔ kVAR" -> item { KvarCapacitorCard(defaultV = defaultV, defaultFreq = defaultFreq) }
                "PF ↔ Angle" -> item { PfAngleCard() }
                "Single Phase" -> item { SinglePhaseConverterCard(defaultV = defaultV) }
                "Three Phase" -> item { ThreePhaseConverterCard(defaultV = defaultV) }
            }
        }
    }
}

@Composable
fun KwHpCard() {
    var kwInput by remember { mutableStateOf("15.0") }
    var hpInput by remember { mutableStateOf("20.11") }
    var activeInput by remember { mutableStateOf("kw") } // "kw" or "hp"
    var hpType by remember { mutableStateOf(UnitConverterEngine.HpType.MECHANICAL) }

    val kwVal = kwInput.toDoubleOrNull() ?: 0.0
    val hpVal = hpInput.toDoubleOrNull() ?: 0.0

    val calculatedHp = UnitConverterEngine.kwToHp(kwVal, hpType)
    val calculatedKw = UnitConverterEngine.hpToKw(hpVal, hpType)

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("kW ↔ Horsepower (HP)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text("Industrial electric motor rating conversion", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                UnitConverterEngine.HpType.values().forEach { t ->
                    FilterChip(
                        selected = hpType == t,
                        onClick = { hpType = t },
                        label = { Text(if (t == UnitConverterEngine.HpType.MECHANICAL) "Mechanical" else if (t == UnitConverterEngine.HpType.ELECTRICAL) "Electrical" else "Metric", fontSize = 11.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                NumericInputField(
                    value = kwInput,
                    onValueChange = {
                        kwInput = it
                        activeInput = "kw"
                        it.toDoubleOrNull()?.let { v ->
                            hpInput = String.format(Locale.US, "%.2f", UnitConverterEngine.kwToHp(v, hpType))
                        }
                    },
                    label = "Power (kW)",
                    modifier = Modifier.weight(1f)
                )

                NumericInputField(
                    value = hpInput,
                    onValueChange = {
                        hpInput = it
                        activeInput = "hp"
                        it.toDoubleOrNull()?.let { v ->
                            kwInput = String.format(Locale.US, "%.2f", UnitConverterEngine.hpToKw(v, hpType))
                        }
                    },
                    label = "Horsepower (HP)",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            if (activeInput == "kw") {
                ConvertedResultBanner(
                    primary = "${String.format(Locale.US, "%.3f", calculatedHp)} HP",
                    label = "$kwInput kW = ${String.format(Locale.US, "%.3f", calculatedHp)} HP (${hpType.label})",
                    subtext = "Factor: 1 HP = ${hpType.wattsPerHp} W | 1 kW = ${String.format(Locale.US, "%.4f", 1000.0 / hpType.wattsPerHp)} HP"
                )
            } else {
                ConvertedResultBanner(
                    primary = "${String.format(Locale.US, "%.3f", calculatedKw)} kW",
                    label = "$hpInput HP = ${String.format(Locale.US, "%.3f", calculatedKw)} kW (${hpType.label})",
                    subtext = "Factor: 1 HP = ${hpType.wattsPerHp} W | 1 kW = ${String.format(Locale.US, "%.4f", 1000.0 / hpType.wattsPerHp)} HP"
                )
            }
        }
    }
}

@Composable
fun KwKvaCard() {
    var kwInput by remember { mutableStateOf("100.0") }
    var pfInput by remember { mutableStateOf("0.85") }
    val kwVal = kwInput.toDoubleOrNull() ?: 0.0
    val pfVal = pfInput.toDoubleOrNull() ?: 0.85

    val res = UnitConverterEngine.kwToKva(kwVal, pfVal)

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("kW ↔ kVA (Real to Apparent Power)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text("kVA = kW / PF | kW = kVA × PF | kVAR = √(kVA² - kW²)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                NumericInputField(
                    value = kwInput,
                    onValueChange = { kwInput = it },
                    label = "Real Power (kW)",
                    modifier = Modifier.weight(1.2f)
                )
                NumericInputField(
                    value = pfInput,
                    onValueChange = { pfInput = it },
                    label = "Power Factor (cos φ)",
                    modifier = Modifier.weight(0.8f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            ConvertedResultBanner(
                primary = "${String.format(Locale.US, "%.2f", res.kva)} kVA",
                label = "Apparent Power S = ${String.format(Locale.US, "%.2f", res.kva)} kVA",
                subtext = "Reactive Power Q = ${String.format(Locale.US, "%.2f", res.kvar)} kVAR | Phase angle θ = ${String.format(Locale.US, "%.1f", Math.toDegrees(kotlin.math.acos(res.pf)))}°"
            )
        }
    }
}

@Composable
fun KvaMvaCard() {
    var kvaInput by remember { mutableStateOf("2500.0") }
    val kvaVal = kvaInput.toDoubleOrNull() ?: 0.0
    val mvaVal = kvaVal / 1000.0
    val vaVal = kvaVal * 1000.0

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("kVA ↔ MVA Transformer Scaling", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))

            NumericInputField(
                value = kvaInput,
                onValueChange = { kvaInput = it },
                label = "Apparent Power (kVA)",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))
            ConvertedResultBanner(
                primary = "${String.format(Locale.US, "%.4f", mvaVal)} MVA",
                label = "Substation Equivalent: ${String.format(Locale.US, "%.4f", mvaVal)} MVA",
                subtext = "= ${String.format(Locale.US, "%,.0f", vaVal)} VA | = ${String.format(Locale.US, "%.2f", kvaVal)} kVA"
            )
        }
    }
}

@Composable
fun KvarCapacitorCard(defaultV: Double, defaultFreq: Double) {
    var kvarInput by remember { mutableStateOf("50.0") }
    var voltInput by remember { mutableStateOf(defaultV.toString()) }
    var freqInput by remember { mutableStateOf(defaultFreq.toString()) }

    val kvarVal = kvarInput.toDoubleOrNull() ?: 0.0
    val voltVal = voltInput.toDoubleOrNull() ?: defaultV
    val freqVal = freqInput.toDoubleOrNull() ?: defaultFreq

    val microfarads = UnitConverterEngine.kvarToMicrofarads(kvarVal, voltVal, freqVal)

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("kVAR ↔ Capacitor Bank (µF)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text("Calculate capacitor rating in microfarads for power factor correction", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(12.dp))

            NumericInputField(
                value = kvarInput,
                onValueChange = { kvarInput = it },
                label = "Reactive Power (kVAR)",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                NumericInputField(
                    value = voltInput,
                    onValueChange = { voltInput = it },
                    label = "System Voltage (V)",
                    modifier = Modifier.weight(1f)
                )
                NumericInputField(
                    value = freqInput,
                    onValueChange = { freqInput = it },
                    label = "Grid Freq (Hz)",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            ConvertedResultBanner(
                primary = "${String.format(Locale.US, "%.1f", microfarads)} µF",
                label = "Required Bank Capacitance: ${String.format(Locale.US, "%.1f", microfarads)} µF",
                subtext = "Formula: C = (kVAR × 10⁹) / (2π · f · V²) at ${voltVal.toInt()} V, ${freqVal.toInt()} Hz"
            )
        }
    }
}

@Composable
fun PfAngleCard() {
    var pfInput by remember { mutableStateOf("0.85") }
    val pfVal = pfInput.toDoubleOrNull() ?: 0.85
    val res = UnitConverterEngine.pfToAngle(pfVal)

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Power Factor ↔ Phase Angle", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text("cos φ, sin φ (reactive factor), tan φ, and angle in degrees / radians", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(12.dp))

            NumericInputField(
                value = pfInput,
                onValueChange = { pfInput = it },
                label = "Power Factor (0.00 – 1.00)",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))
            ConvertedResultBanner(
                primary = "θ = ${String.format(Locale.US, "%.2f", res.angleDeg)}° (${String.format(Locale.US, "%.4f", res.angleRad)} rad)",
                label = "Phase Angle θ: ${String.format(Locale.US, "%.2f", res.angleDeg)}°",
                subtext = "sin φ (Reactive Factor) = ${String.format(Locale.US, "%.4f", res.sinPhi)} | tan φ = ${String.format(Locale.US, "%.4f", res.tanPhi)}"
            )
        }
    }
}

@Composable
fun SinglePhaseConverterCard(defaultV: Double) {
    var wattsInput by remember { mutableStateOf("3000.0") }
    var voltInput by remember { mutableStateOf(defaultV.toString()) }
    var pfInput by remember { mutableStateOf("0.90") }

    val wVal = wattsInput.toDoubleOrNull() ?: 0.0
    val vVal = voltInput.toDoubleOrNull() ?: defaultV
    val pfVal = pfInput.toDoubleOrNull() ?: 0.90

    val amps = UnitConverterEngine.singlePhaseWattsToAmps(wVal, vVal, pfVal)
    val kva = UnitConverterEngine.singlePhaseAmpsToKva(amps, vVal)

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Single Phase: Watts ↔ Amps ↔ kVA", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text("Formula: I = P / (V × PF) | S = (V × I) / 1000", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(12.dp))

            NumericInputField(
                value = wattsInput,
                onValueChange = { wattsInput = it },
                label = "Active Power (Watts)",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                NumericInputField(
                    value = voltInput,
                    onValueChange = { voltInput = it },
                    label = "Voltage (V)",
                    modifier = Modifier.weight(1f)
                )
                NumericInputField(
                    value = pfInput,
                    onValueChange = { pfInput = it },
                    label = "Power Factor (PF)",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            ConvertedResultBanner(
                primary = "${String.format(Locale.US, "%.2f", amps)} Amperes (A)",
                label = "Current Draw: ${String.format(Locale.US, "%.2f", amps)} A",
                subtext = "Apparent Power: ${String.format(Locale.US, "%.2f", kva)} kVA at ${vVal.toInt()} V"
            )
        }
    }
}

@Composable
fun ThreePhaseConverterCard(defaultV: Double) {
    var wattsInput by remember { mutableStateOf("15000.0") }
    var voltLineInput by remember { mutableStateOf(if (defaultV < 250) "380.0" else defaultV.toString()) }
    var pfInput by remember { mutableStateOf("0.85") }

    val wVal = wattsInput.toDoubleOrNull() ?: 0.0
    val vLineVal = voltLineInput.toDoubleOrNull() ?: 380.0
    val pfVal = pfInput.toDoubleOrNull() ?: 0.85

    val amps = UnitConverterEngine.threePhaseWattsToAmps(wVal, vLineVal, pfVal)
    val kva = UnitConverterEngine.threePhaseAmpsToKva(amps, vLineVal)

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Three Phase: Watts ↔ Amps ↔ kVA", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text("Formula: I = P / (√3 × VL × PF) | S = (√3 × VL × I) / 1000", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(12.dp))

            NumericInputField(
                value = wattsInput,
                onValueChange = { wattsInput = it },
                label = "Active Power (Watts)",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                NumericInputField(
                    value = voltLineInput,
                    onValueChange = { voltLineInput = it },
                    label = "Line Voltage VL (V)",
                    modifier = Modifier.weight(1f)
                )
                NumericInputField(
                    value = pfInput,
                    onValueChange = { pfInput = it },
                    label = "Power Factor (PF)",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            ConvertedResultBanner(
                primary = "${String.format(Locale.US, "%.2f", amps)} A per Line",
                label = "Three-Phase Line Current: ${String.format(Locale.US, "%.2f", amps)} A",
                subtext = "Apparent Power: ${String.format(Locale.US, "%.2f", kva)} kVA at ${vLineVal.toInt()} V Line-to-Line"
            )
        }
    }
}

// =============================================================================
// 3. 🔋 BATTERY SECTION
// =============================================================================
@Composable
fun BatterySection() {
    val subOptions = listOf("Ah ↔ mAh", "Wh ↔ kWh", "Battery Runtime", "C-Rate", "Series / Parallel")
    var selectedSub by remember { mutableStateOf("Ah ↔ mAh") }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(subOptions) { opt ->
                SuggestionChip(
                    onClick = { selectedSub = opt },
                    label = { Text(opt, fontSize = 12.sp, fontWeight = if (selectedSub == opt) FontWeight.Bold else FontWeight.Normal) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = if (selectedSub == opt) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            when (selectedSub) {
                "Ah ↔ mAh" -> item { AhMahCard() }
                "Wh ↔ kWh" -> item { WhKwhCard() }
                "Battery Runtime" -> item { BatteryRuntimeCard() }
                "C-Rate" -> item { CRateCard() }
                "Series / Parallel" -> item { BatteryPackCard() }
            }
        }
    }
}

@Composable
fun AhMahCard() {
    var ahInput by remember { mutableStateOf("100.0") }
    val ahVal = ahInput.toDoubleOrNull() ?: 0.0
    val mahVal = ahVal * 1000.0
    val coulombs = ahVal * 3600.0

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Ampere-Hour (Ah) ↔ Milliampere-Hour (mAh)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))

            NumericInputField(
                value = ahInput,
                onValueChange = { ahInput = it },
                label = "Capacity in Ah",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))
            ConvertedResultBanner(
                primary = "${String.format(Locale.US, "%,.0f", mahVal)} mAh",
                label = "$ahInput Ah = ${String.format(Locale.US, "%,.0f", mahVal)} mAh",
                subtext = "Stored Charge = ${String.format(Locale.US, "%,.0f", coulombs)} Coulombs (C)"
            )
        }
    }
}

@Composable
fun WhKwhCard() {
    var whInput by remember { mutableStateOf("2400.0") }
    var voltInput by remember { mutableStateOf("24.0") }
    val whVal = whInput.toDoubleOrNull() ?: 0.0
    val voltVal = voltInput.toDoubleOrNull() ?: 24.0

    val kwhVal = whVal / 1000.0
    val ahVal = if (voltVal > 0) whVal / voltVal else 0.0

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Watt-Hour (Wh) ↔ Kilowatt-Hour (kWh)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))

            NumericInputField(
                value = whInput,
                onValueChange = { whInput = it },
                label = "Energy in Wh",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))
            NumericInputField(
                value = voltInput,
                onValueChange = { voltInput = it },
                label = "Battery Nominal Voltage (V)",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))
            ConvertedResultBanner(
                primary = "${String.format(Locale.US, "%.3f", kwhVal)} kWh",
                label = "$whInput Wh = ${String.format(Locale.US, "%.3f", kwhVal)} kWh",
                subtext = "Equivalent Capacity at ${voltVal}V: ${String.format(Locale.US, "%.1f", ahVal)} Ah"
            )
        }
    }
}

@Composable
fun BatteryRuntimeCard() {
    var ahInput by remember { mutableStateOf("100.0") }
    var vInput by remember { mutableStateOf("12.0") }
    var loadWattsInput by remember { mutableStateOf("150.0") }
    var dodInput by remember { mutableStateOf("80.0") }
    var effInput by remember { mutableStateOf("90.0") }

    val ah = ahInput.toDoubleOrNull() ?: 100.0
    val v = vInput.toDoubleOrNull() ?: 12.0
    val loadW = loadWattsInput.toDoubleOrNull() ?: 150.0
    val dod = dodInput.toDoubleOrNull() ?: 80.0
    val eff = effInput.toDoubleOrNull() ?: 90.0

    val res = UnitConverterEngine.calculateBatteryRuntime(ah, v, loadW, dod, eff)

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Battery Backup Runtime Estimator", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text("Considers nominal voltage, usable DoD, and inverter efficiency", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                NumericInputField(
                    value = ahInput,
                    onValueChange = { ahInput = it },
                    label = "Capacity (Ah)",
                    modifier = Modifier.weight(1f)
                )
                NumericInputField(
                    value = vInput,
                    onValueChange = { vInput = it },
                    label = "Voltage (V)",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            NumericInputField(
                value = loadWattsInput,
                onValueChange = { loadWattsInput = it },
                label = "Load Power (Watts)",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                NumericInputField(
                    value = dodInput,
                    onValueChange = { dodInput = it },
                    label = "DoD % (e.g. 80)",
                    modifier = Modifier.weight(1f)
                )
                NumericInputField(
                    value = effInput,
                    onValueChange = { effInput = it },
                    label = "Efficiency %",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            ConvertedResultBanner(
                primary = res.runTimeString,
                label = "Estimated Backup Runtime: ${res.runTimeString}",
                subtext = "Usable Energy = ${String.format(Locale.US, "%.0f", res.usableEnergyWh)} Wh | Discharge Current = ${String.format(Locale.US, "%.1f", res.loadAmps)} A"
            )
        }
    }
}

@Composable
fun CRateCard() {
    var ahInput by remember { mutableStateOf("50.0") }
    var currentInput by remember { mutableStateOf("25.0") }

    val ah = ahInput.toDoubleOrNull() ?: 50.0
    val curr = currentInput.toDoubleOrNull() ?: 25.0
    val res = UnitConverterEngine.calculateCRate(ah, curr)

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Battery C-Rate Calculator", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text("C-Rate = Current (A) / Capacity (Ah)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                NumericInputField(
                    value = ahInput,
                    onValueChange = { ahInput = it },
                    label = "Capacity (Ah)",
                    modifier = Modifier.weight(1f)
                )
                NumericInputField(
                    value = currentInput,
                    onValueChange = { currentInput = it },
                    label = "Current (A)",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            ConvertedResultBanner(
                primary = "${String.format(Locale.US, "%.2f", res.cRate)} C",
                label = "Discharge / Charge Rate: ${String.format(Locale.US, "%.2f", res.cRate)} C",
                subtext = "Nominal charge / discharge duration = ${String.format(Locale.US, "%.1f", res.nominalChargeTimeHours * 60.0)} minutes (${String.format(Locale.US, "%.2f", res.nominalChargeTimeHours)} hours)"
            )
        }
    }
}

@Composable
fun BatteryPackCard() {
    var sInput by remember { mutableStateOf("16") } // 16S (e.g. 48V LiFePO4)
    var pInput by remember { mutableStateOf("2") }  // 2P
    var vCellInput by remember { mutableStateOf("3.2") } // 3.2V LiFePO4
    var ahCellInput by remember { mutableStateOf("100.0") }

    val sVal = sInput.toIntOrNull() ?: 16
    val pVal = pInput.toIntOrNull() ?: 2
    val vCell = vCellInput.toDoubleOrNull() ?: 3.2
    val ahCell = ahCellInput.toDoubleOrNull() ?: 100.0

    val res = UnitConverterEngine.calculateBatteryPack(sVal, pVal, vCell, ahCell)

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Series / Parallel Pack Configurator", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text("Pack Voltage = Ns × Vcell | Pack Capacity = Np × Ah_cell", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                NumericInputField(
                    value = sInput,
                    onValueChange = { sInput = it },
                    label = "Series (Ns)",
                    modifier = Modifier.weight(1f)
                )
                NumericInputField(
                    value = pInput,
                    onValueChange = { pInput = it },
                    label = "Parallel (Np)",
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                NumericInputField(
                    value = vCellInput,
                    onValueChange = { vCellInput = it },
                    label = "Cell V (e.g. 3.2)",
                    modifier = Modifier.weight(1f)
                )
                NumericInputField(
                    value = ahCellInput,
                    onValueChange = { ahCellInput = it },
                    label = "Cell Ah",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            ConvertedResultBanner(
                primary = "${String.format(Locale.US, "%.1f", res.packVoltage)} V @ ${String.format(Locale.US, "%.1f", res.packCapacityAh)} Ah",
                label = "Total Pack Capacity: ${String.format(Locale.US, "%.2f", res.packEnergyWh / 1000.0)} kWh",
                subtext = "Pack Energy = ${String.format(Locale.US, "%,.0f", res.packEnergyWh)} Wh | Total Cells = ${res.totalCells} (${sVal}S${pVal}P)"
            )
        }
    }
}

// =============================================================================
// 4. ☀️ SOLAR SECTION
// =============================================================================
@Composable
fun SolarSection() {
    val subOptions = listOf("PV Power", "PV Voltage", "PV Current", "Irradiance", "Panel Series/Parallel")
    var selectedSub by remember { mutableStateOf("PV Power") }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(subOptions) { opt ->
                SuggestionChip(
                    onClick = { selectedSub = opt },
                    label = { Text(opt, fontSize = 12.sp, fontWeight = if (selectedSub == opt) FontWeight.Bold else FontWeight.Normal) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = if (selectedSub == opt) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            when (selectedSub) {
                "PV Power" -> item { SolarPowerCard() }
                "PV Voltage" -> item { SolarVoltageCard() }
                "PV Current" -> item { SolarCurrentCard() }
                "Irradiance" -> item { StandardUnitConverterCard(category = UnitConverterEngine.standardCategories.first { it.id == "irradiance" }) }
                "Panel Series/Parallel" -> item { SolarArrayCard() }
            }
        }
    }
}

@Composable
fun SolarPowerCard() {
    var areaInput by remember { mutableStateOf("30.0") } // m²
    var effInput by remember { mutableStateOf("21.5") }   // %
    var irrInput by remember { mutableStateOf("1000.0") } // W/m² STC

    val area = areaInput.toDoubleOrNull() ?: 30.0
    val eff = effInput.toDoubleOrNull() ?: 21.5
    val irr = irrInput.toDoubleOrNull() ?: 1000.0

    val wp = area * irr * (eff / 100.0)
    val kwp = wp / 1000.0

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("PV Power & Surface Area Conversion", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text("Power (Wp) = Area (m²) × Irradiance (W/m²) × Module Efficiency", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                NumericInputField(
                    value = areaInput,
                    onValueChange = { areaInput = it },
                    label = "Area (m²)",
                    modifier = Modifier.weight(1f)
                )
                NumericInputField(
                    value = effInput,
                    onValueChange = { effInput = it },
                    label = "Efficiency (%)",
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            NumericInputField(
                value = irrInput,
                onValueChange = { irrInput = it },
                label = "Irradiance (W/m²)",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))
            ConvertedResultBanner(
                primary = "${String.format(Locale.US, "%.2f", kwp)} kWp (${String.format(Locale.US, "%,.0f", wp)} Wp)",
                label = "Peak Output: ${String.format(Locale.US, "%.2f", kwp)} kWp",
                subtext = "Array Area = $area m² | Module Efficiency = $eff % at $irr W/m²"
            )
        }
    }
}

@Composable
fun SolarVoltageCard() {
    var vocInput by remember { mutableStateOf("49.8") }
    var vmpInput by remember { mutableStateOf("41.6") }
    var coeffInput by remember { mutableStateOf("-0.28") } // %/°C
    var minTempInput by remember { mutableStateOf("-10.0") }
    var maxTempInput by remember { mutableStateOf("65.0") }

    val voc = vocInput.toDoubleOrNull() ?: 49.8
    val vmp = vmpInput.toDoubleOrNull() ?: 41.6
    val coeff = coeffInput.toDoubleOrNull() ?: -0.28
    val minT = minTempInput.toDoubleOrNull() ?: -10.0
    val maxT = maxTempInput.toDoubleOrNull() ?: 65.0

    val res = UnitConverterEngine.calculateSolarTempVoltage(voc, vmp, coeff, minT, maxT)

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("PV Voltage Temperature Coefficient Correction", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text("Calculate maximum Voc at minimum cold temperature (NEC 690.7 compliance)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                NumericInputField(
                    value = vocInput,
                    onValueChange = { vocInput = it },
                    label = "STC Voc (V)",
                    modifier = Modifier.weight(1f)
                )
                NumericInputField(
                    value = vmpInput,
                    onValueChange = { vmpInput = it },
                    label = "STC Vmp (V)",
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            NumericInputField(
                value = coeffInput,
                onValueChange = { coeffInput = it },
                label = "Temp Coeff Voc (%/°C)",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                NumericInputField(
                    value = minTempInput,
                    onValueChange = { minTempInput = it },
                    label = "Min Temp (°C)",
                    modifier = Modifier.weight(1f)
                )
                NumericInputField(
                    value = maxTempInput,
                    onValueChange = { maxTempInput = it },
                    label = "Max Temp (°C)",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            ConvertedResultBanner(
                primary = "Max Voc = ${String.format(Locale.US, "%.2f", res.vocAtMinTemp)} V",
                label = "Max Cold Voc: ${String.format(Locale.US, "%.2f", res.vocAtMinTemp)} V (at $minT°C)",
                subtext = "Min Hot Vmp = ${String.format(Locale.US, "%.2f", res.vmpAtMaxTemp)} V (at $maxT°C) | ΔVoc = +${String.format(Locale.US, "%.2f", res.deltaVoc)} V"
            )
        }
    }
}

@Composable
fun SolarCurrentCard() {
    var iscInput by remember { mutableStateOf("13.9") }
    var coeffInput by remember { mutableStateOf("0.05") } // %/°C
    var maxTempInput by remember { mutableStateOf("70.0") }

    val isc = iscInput.toDoubleOrNull() ?: 13.9
    val coeff = coeffInput.toDoubleOrNull() ?: 0.05
    val maxT = maxTempInput.toDoubleOrNull() ?: 70.0

    val res = UnitConverterEngine.calculateSolarTempCurrent(isc, coeff, maxT)

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("PV Current Temperature Correction", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text("Calculate maximum Isc at high temperature and NEC 1.56x continuous ampacity", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(12.dp))

            NumericInputField(
                value = iscInput,
                onValueChange = { iscInput = it },
                label = "STC Isc (A)",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                NumericInputField(
                    value = coeffInput,
                    onValueChange = { coeffInput = it },
                    label = "Temp Coeff Isc (%/°C)",
                    modifier = Modifier.weight(1f)
                )
                NumericInputField(
                    value = maxTempInput,
                    onValueChange = { maxTempInput = it },
                    label = "Max Temp (°C)",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            ConvertedResultBanner(
                primary = "Max Isc = ${String.format(Locale.US, "%.2f", res.iscAtMaxTemp)} A",
                label = "Maximum Operating Isc: ${String.format(Locale.US, "%.2f", res.iscAtMaxTemp)} A",
                subtext = "NEC Continuous Ampacity (1.56x) = ${String.format(Locale.US, "%.2f", res.necSafeAmpacity)} A for conductor / breaker sizing"
            )
        }
    }
}

@Composable
fun SolarArrayCard() {
    var seriesInput by remember { mutableStateOf("12") }
    var parallelInput by remember { mutableStateOf("2") }
    var wpInput by remember { mutableStateOf("450.0") }
    var vocInput by remember { mutableStateOf("49.5") }
    var vmpInput by remember { mutableStateOf("41.5") }
    var iscInput by remember { mutableStateOf("11.6") }
    var impInput by remember { mutableStateOf("10.8") }

    val nS = seriesInput.toIntOrNull() ?: 12
    val nP = parallelInput.toIntOrNull() ?: 2
    val wp = wpInput.toDoubleOrNull() ?: 450.0
    val voc = vocInput.toDoubleOrNull() ?: 49.5
    val vmp = vmpInput.toDoubleOrNull() ?: 41.5
    val isc = iscInput.toDoubleOrNull() ?: 11.6
    val imp = impInput.toDoubleOrNull() ?: 10.8

    val res = UnitConverterEngine.calculateSolarArray(nS, nP, wp, voc, vmp, isc, imp)

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Solar PV Array String & Parallel Calculator", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                NumericInputField(
                    value = seriesInput,
                    onValueChange = { seriesInput = it },
                    label = "Panels / String (Series)",
                    modifier = Modifier.weight(1f)
                )
                NumericInputField(
                    value = parallelInput,
                    onValueChange = { parallelInput = it },
                    label = "Strings (Parallel)",
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                NumericInputField(
                    value = wpInput,
                    onValueChange = { wpInput = it },
                    label = "Module Wp",
                    modifier = Modifier.weight(1f)
                )
                NumericInputField(
                    value = vocInput,
                    onValueChange = { vocInput = it },
                    label = "Module Voc (V)",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            ConvertedResultBanner(
                primary = "${String.format(Locale.US, "%.2f", res.totalPeakKw)} kWp (${res.totalModules} Panels)",
                label = "Total System Peak Power: ${String.format(Locale.US, "%.2f", res.totalPeakKw)} kWp",
                subtext = "String Voc = ${String.format(Locale.US, "%.1f", res.stringVoc)} V | String Vmp = ${String.format(Locale.US, "%.1f", res.stringVmp)} V | Array Isc = ${String.format(Locale.US, "%.1f", res.arrayIsc)} A"
            )
        }
    }
}

// =============================================================================
// 5. 🔧 CABLE SECTION
// =============================================================================
@Composable
fun CableSection() {
    val subOptions = listOf("mm² ↔ AWG", "mm² ↔ Circular Mil", "m ↔ ft", "Ω/km ↔ Ω/mile")
    var selectedSub by remember { mutableStateOf("mm² ↔ AWG") }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(subOptions) { opt ->
                SuggestionChip(
                    onClick = { selectedSub = opt },
                    label = { Text(opt, fontSize = 12.sp, fontWeight = if (selectedSub == opt) FontWeight.Bold else FontWeight.Normal) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = if (selectedSub == opt) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            when (selectedSub) {
                "mm² ↔ AWG" -> item { AwgMm2Card() }
                "mm² ↔ Circular Mil" -> item { Mm2CircularMilCard() }
                "m ↔ ft" -> item { StandardUnitConverterCard(category = UnitConverterEngine.standardCategories.first { it.id == "length" }) }
                "Ω/km ↔ Ω/mile" -> item { StandardUnitConverterCard(category = UnitConverterEngine.standardCategories.first { it.id == "cable_resistance" }) }
            }
        }
    }
}

@Composable
fun AwgMm2Card() {
    val allAwgs = UnitConverterEngine.getAllAwgDetails()
    var selectedAwgIdx by remember { mutableIntStateOf(11) } // 12 AWG
    var customMm2Input by remember { mutableStateOf("4.0") }
    var conversionMode by remember { mutableIntStateOf(0) } // 0: AWG to mm2, 1: mm2 to AWG

    val sel = allAwgs[selectedAwgIdx]

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("American Wire Gauge (AWG) ↔ Metric mm²", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text("Precise gauge area, diameter, circular mils, and copper resistance lookup", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = conversionMode == 0,
                    onClick = { conversionMode = 0 },
                    label = { Text("Select AWG ➔ Metric mm²") }
                )
                FilterChip(
                    selected = conversionMode == 1,
                    onClick = { conversionMode = 1 },
                    label = { Text("Enter mm² ➔ Closest AWG") }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (conversionMode == 0) {
                Text("Select Gauge Size:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(allAwgs.indices.toList()) { idx ->
                        val item = allAwgs[idx]
                        FilterChip(
                            selected = selectedAwgIdx == idx,
                            onClick = { selectedAwgIdx = idx },
                            label = { Text(item.gaugeName, fontSize = 12.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                ConvertedResultBanner(
                    primary = "${sel.gaugeName} = ${sel.areaMm2} mm²",
                    label = "Conductor Area: ${sel.areaMm2} mm² (${String.format(Locale.US, "%,.0f", sel.circularMils)} cmil)",
                    subtext = "Diameter: ${sel.diameterMm} mm (${String.format(Locale.US, "%.4f", sel.diameterMm / 25.4)} in) | Resistance: ${sel.copperResistanceOhmPerKm} Ω/km (${String.format(Locale.US, "%.3f", sel.copperResistanceOhmPerKm * 0.3048)} Ω/kft)"
                )
            } else {
                NumericInputField(
                    value = customMm2Input,
                    onValueChange = { customMm2Input = it },
                    label = "Cross-Section Area in mm²",
                    modifier = Modifier.fillMaxWidth()
                )

                val targetMm2 = customMm2Input.toDoubleOrNull() ?: 4.0
                val closest = UnitConverterEngine.findClosestAwg(targetMm2)
                val diffPct = ((closest.areaMm2 - targetMm2) / targetMm2) * 100.0

                Spacer(modifier = Modifier.height(14.dp))
                ConvertedResultBanner(
                    primary = "Closest: ${closest.gaugeName} (${closest.areaMm2} mm²)",
                    label = "Matched AWG: ${closest.gaugeName}",
                    subtext = "Exact AWG Area = ${closest.areaMm2} mm² (${String.format(Locale.US, "%+.1f", diffPct)}% variance) | Resistance = ${closest.copperResistanceOhmPerKm} Ω/km"
                )
            }
        }
    }
}

@Composable
fun Mm2CircularMilCard() {
    var mm2Input by remember { mutableStateOf("50.0") }
    val mm2Val = mm2Input.toDoubleOrNull() ?: 0.0
    val cmil = UnitConverterEngine.mm2ToCircularMil(mm2Val)
    val kcmil = cmil / 1000.0

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("mm² ↔ Circular Mil (kcmil / MCM)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text("1 kcmil (MCM) = 1,000 cmil = 0.5067 mm²", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(12.dp))

            NumericInputField(
                value = mm2Input,
                onValueChange = { mm2Input = it },
                label = "Area in mm²",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))
            ConvertedResultBanner(
                primary = "${String.format(Locale.US, "%.2f", kcmil)} kcmil (MCM)",
                label = "$mm2Input mm² = ${String.format(Locale.US, "%.2f", kcmil)} kcmil",
                subtext = "= ${String.format(Locale.US, "%,.0f", cmil)} Circular Mils (cmil)"
            )
        }
    }
}

// =============================================================================
// 6. 🌡️ GENERAL SECTION
// =============================================================================
@Composable
fun GeneralSection() {
    val subOptions = listOf("Temperature", "Length", "Area", "Volume", "Mass", "Pressure", "Time")
    var selectedSub by remember { mutableStateOf("Temperature") }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(subOptions) { opt ->
                SuggestionChip(
                    onClick = { selectedSub = opt },
                    label = { Text(opt, fontSize = 12.sp, fontWeight = if (selectedSub == opt) FontWeight.Bold else FontWeight.Normal) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = if (selectedSub == opt) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        when (selectedSub) {
            "Temperature" -> TemperatureConverterCard()
            "Length" -> StandardUnitConverterCard(category = UnitConverterEngine.standardCategories.first { it.id == "length" })
            "Area" -> StandardUnitConverterCard(category = UnitConverterEngine.standardCategories.first { it.id == "area_gen" })
            "Volume" -> StandardUnitConverterCard(category = UnitConverterEngine.standardCategories.first { it.id == "volume" })
            "Mass" -> StandardUnitConverterCard(category = UnitConverterEngine.standardCategories.first { it.id == "mass" })
            "Pressure" -> StandardUnitConverterCard(category = UnitConverterEngine.standardCategories.first { it.id == "pressure" })
            "Time" -> StandardUnitConverterCard(category = UnitConverterEngine.standardCategories.first { it.id == "time" })
        }
    }
}

@Composable
fun TemperatureConverterCard() {
    var valInput by remember { mutableStateOf("75.0") }
    var fromUnit by remember { mutableStateOf("C") } // "C", "F", "K", "R"
    var toUnit by remember { mutableStateOf("F") }

    val tempVal = valInput.toDoubleOrNull() ?: 0.0
    val converted = UnitConverterEngine.convertTemperature(tempVal, fromUnit, toUnit)

    val units = listOf(
        Pair("C", "Celsius (°C)"),
        Pair("F", "Fahrenheit (°F)"),
        Pair("K", "Kelvin (K)"),
        Pair("R", "Rankine (°R)")
    )

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Temperature Converter", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text("Essential for conductor ampacity temperature deratings (60°C, 75°C, 90°C)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(12.dp))

            NumericInputField(
                value = valInput,
                onValueChange = { valInput = it },
                label = "Input Temperature",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("From Unit", style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        units.forEach { (id, _) ->
                            FilterChip(
                                selected = fromUnit == id,
                                onClick = { fromUnit = id },
                                label = { Text(id, fontSize = 12.sp) }
                            )
                        }
                    }
                }

                IconButton(onClick = {
                    val temp = fromUnit
                    fromUnit = toUnit
                    toUnit = temp
                }) {
                    Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = "Swap")
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("To Unit", style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        units.forEach { (id, _) ->
                            FilterChip(
                                selected = toUnit == id,
                                onClick = { toUnit = id },
                                label = { Text(id, fontSize = 12.sp) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            ConvertedResultBanner(
                primary = "${String.format(Locale.US, "%.2f", converted)} °$toUnit",
                label = "$tempVal °$fromUnit = ${String.format(Locale.US, "%.2f", converted)} °$toUnit",
                subtext = "Formula: ${when {
                    fromUnit == "C" && toUnit == "F" -> "(°C × 9/5) + 32"
                    fromUnit == "F" && toUnit == "C" -> "(°F - 32) × 5/9"
                    fromUnit == "C" && toUnit == "K" -> "°C + 273.15"
                    else -> "Standard Thermodynamic Conversion"
                }}"
            )
        }
    }
}

// =============================================================================
// 7. 📡 ELECTRONICS SECTION
// =============================================================================
@Composable
fun ElectronicsSection() {
    val subOptions = listOf("dB", "dBm", "dBW", "dBV", "Frequency")
    var selectedSub by remember { mutableStateOf("dB") }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(subOptions) { opt ->
                SuggestionChip(
                    onClick = { selectedSub = opt },
                    label = { Text(opt, fontSize = 12.sp, fontWeight = if (selectedSub == opt) FontWeight.Bold else FontWeight.Normal) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = if (selectedSub == opt) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            when (selectedSub) {
                "dB" -> item { DecibelRatioCard() }
                "dBm" -> item { DbmCard() }
                "dBW" -> item { DbwCard() }
                "dBV" -> item { DbvCard() }
                "Frequency" -> item { WavelengthCard() }
            }
        }
    }
}

@Composable
fun DecibelRatioCard() {
    var ratioType by remember { mutableIntStateOf(0) } // 0: Power, 1: Voltage
    var p2Text by remember { mutableStateOf("100.0") }
    var p1Text by remember { mutableStateOf("1.0") }

    val v1 = p1Text.toDoubleOrNull() ?: 1.0
    val v2 = p2Text.toDoubleOrNull() ?: 100.0

    val db = if (ratioType == 0) UnitConverterEngine.powerRatioToDb(v2, v1) else UnitConverterEngine.voltageRatioToDb(v2, v1)

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Decibel (dB) Gain & Loss Ratio", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text("Power: dB = 10 log10(P2/P1) | Voltage: dB = 20 log10(V2/V1)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = ratioType == 0,
                    onClick = { ratioType = 0 },
                    label = { Text("Power Ratio (10 log)") }
                )
                FilterChip(
                    selected = ratioType == 1,
                    onClick = { ratioType = 1 },
                    label = { Text("Voltage / Field Ratio (20 log)") }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                NumericInputField(
                    value = p2Text,
                    onValueChange = { p2Text = it },
                    label = if (ratioType == 0) "Output Power P2" else "Output Voltage V2",
                    modifier = Modifier.weight(1f)
                )
                NumericInputField(
                    value = p1Text,
                    onValueChange = { p1Text = it },
                    label = if (ratioType == 0) "Input Power P1" else "Input Voltage V1",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            ConvertedResultBanner(
                primary = "${String.format(Locale.US, "%+.2f", db)} dB",
                label = "Gain / Attenuation: ${String.format(Locale.US, "%+.2f", db)} dB",
                subtext = "Ratio = ${String.format(Locale.US, "%.3f", if (v1 > 0) v2 / v1 else 0.0)}x (${if (db >= 0) "Gain" else "Attenuation"})"
            )
        }
    }
}

@Composable
fun DbmCard() {
    var dbmInput by remember { mutableStateOf("0.0") } // 0 dBm = 1 mW
    val dbmVal = dbmInput.toDoubleOrNull() ?: 0.0
    val res = UnitConverterEngine.dbmToAll(dbmVal)

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("dBm (Decibel-Milliwatts)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text("Convert dBm to mW, Watts, and RMS Voltage in 50 Ω RF circuits", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(12.dp))

            NumericInputField(
                value = dbmInput,
                onValueChange = { dbmInput = it },
                label = "Power in dBm",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))
            ConvertedResultBanner(
                primary = "${String.format(Locale.US, "%.3f", res.milliwatts)} mW",
                label = "$dbmInput dBm = ${String.format(Locale.US, "%.3f", res.milliwatts)} mW",
                subtext = "= ${String.format(Locale.US, "%.6f", res.watts)} W | V_RMS (50Ω) = ${String.format(Locale.US, "%.4f", res.voltsRms50Ohm)} V | V_pp = ${String.format(Locale.US, "%.4f", res.voltsPeakToPeak50Ohm)} V"
            )
        }
    }
}

@Composable
fun DbwCard() {
    var dbwInput by remember { mutableStateOf("30.0") } // 30 dBW = 1000 W = 1 kW
    val dbwVal = dbwInput.toDoubleOrNull() ?: 30.0
    val watts = UnitConverterEngine.dbwToWatts(dbwVal)
    val dbm = dbwVal + 30.0

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("dBW (Decibel-Watts)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text("P(W) = 10^(dBW / 10) | dBm = dBW + 30", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(12.dp))

            NumericInputField(
                value = dbwInput,
                onValueChange = { dbwInput = it },
                label = "Power in dBW",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))
            ConvertedResultBanner(
                primary = "${String.format(Locale.US, "%,.2f", watts)} Watts",
                label = "$dbwInput dBW = ${String.format(Locale.US, "%,.2f", watts)} W",
                subtext = "= ${String.format(Locale.US, "%.3f", watts / 1000.0)} kW | = ${String.format(Locale.US, "%.1f", dbm)} dBm"
            )
        }
    }
}

@Composable
fun DbvCard() {
    var dbvInput by remember { mutableStateOf("0.0") } // 0 dBV = 1.0 V
    val dbvVal = dbvInput.toDoubleOrNull() ?: 0.0
    val volts = UnitConverterEngine.dbvToVolts(dbvVal)
    val dbuV = dbvVal + 120.0

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("dBV (Decibel-Volts)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text("V_RMS = 10^(dBV / 20) relative to 1 Volt RMS", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(12.dp))

            NumericInputField(
                value = dbvInput,
                onValueChange = { dbvInput = it },
                label = "Signal in dBV",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))
            ConvertedResultBanner(
                primary = "${String.format(Locale.US, "%.4f", volts)} Volts RMS",
                label = "$dbvInput dBV = ${String.format(Locale.US, "%.4f", volts)} V RMS",
                subtext = "= ${String.format(Locale.US, "%.1f", volts * 1000.0)} mV | = ${String.format(Locale.US, "%.1f", dbuV)} dBµV"
            )
        }
    }
}

@Composable
fun WavelengthCard() {
    var freqInput by remember { mutableStateOf("100.0") } // MHz
    var unitMult by remember { mutableStateOf(1e6) } // MHz multiplier

    val fVal = freqInput.toDoubleOrNull() ?: 100.0
    val totalHz = fVal * unitMult
    val res = UnitConverterEngine.frequencyToWavelength(totalHz)

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Frequency ↔ Wavelength (λ) & Antenna Length", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text("Speed of light c = 299,792,458 m/s | λ = c / f", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(
                    Pair(1.0, "Hz"),
                    Pair(1e3, "kHz"),
                    Pair(1e6, "MHz"),
                    Pair(1e9, "GHz")
                ).forEach { (mult, label) ->
                    FilterChip(
                        selected = unitMult == mult,
                        onClick = { unitMult = mult },
                        label = { Text(label, fontSize = 12.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            NumericInputField(
                value = freqInput,
                onValueChange = { freqInput = it },
                label = "Frequency",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))
            ConvertedResultBanner(
                primary = "λ = ${String.format(Locale.US, "%.3f", res.wavelengthMeters)} m (${String.format(Locale.US, "%.2f", res.wavelengthFeet)} ft)",
                label = "Full Wavelength: ${String.format(Locale.US, "%.3f", res.wavelengthMeters)} meters",
                subtext = "Half-Wave Dipole (λ/2) = ${String.format(Locale.US, "%.3f", res.halfWaveDipoleMeters)} m | Quarter-Wave Whip (λ/4) = ${String.format(Locale.US, "%.3f", res.quarterWaveWhipMeters)} m"
            )
        }
    }
}

// =============================================================================
// REUSABLE STANDARD CONVERTER COMPONENT FOR GENERAL UNITS
// =============================================================================
@Composable
fun StandardUnitConverterCard(category: ConversionCategory) {
    var valInput by remember { mutableStateOf("1.0") }
    var fromIdx by remember { mutableIntStateOf(0) }
    var toIdx by remember { mutableIntStateOf(if (category.units.size > 1) 1 else 0) }

    val fromUnit = category.units.getOrElse(fromIdx) { category.units.first() }
    val toUnit = category.units.getOrElse(toIdx) { category.units.last() }

    val rawVal = valInput.toDoubleOrNull() ?: 0.0
    val convertedVal = UnitConverterEngine.convert(rawVal, fromUnit, toUnit)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "${category.name} Converter",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    NumericInputField(
                        value = valInput,
                        onValueChange = { valInput = it },
                        label = "Enter Value (${fromUnit.symbol})",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("From Unit", style = MaterialTheme.typography.labelSmall)
                            Spacer(modifier = Modifier.height(4.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                items(category.units.indices.toList()) { idx ->
                                    val u = category.units[idx]
                                    FilterChip(
                                        selected = fromIdx == idx,
                                        onClick = { fromIdx = idx },
                                        label = { Text(u.symbol, fontSize = 12.sp) }
                                    )
                                }
                            }
                        }

                        IconButton(onClick = {
                            val temp = fromIdx
                            fromIdx = toIdx
                            toIdx = temp
                        }) {
                            Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = "Swap")
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text("To Unit", style = MaterialTheme.typography.labelSmall)
                            Spacer(modifier = Modifier.height(4.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                items(category.units.indices.toList()) { idx ->
                                    val u = category.units[idx]
                                    FilterChip(
                                        selected = toIdx == idx,
                                        onClick = { toIdx = idx },
                                        label = { Text(u.symbol, fontSize = 12.sp) }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val formatted = if (convertedVal >= 10000 || (convertedVal > 0 && convertedVal < 0.001)) {
                        String.format(Locale.US, "%.4e", convertedVal)
                    } else {
                        String.format(Locale.US, "%.4f", convertedVal).trimEnd('0').trimEnd('.')
                    }

                    ConvertedResultBanner(
                        primary = "$formatted ${toUnit.symbol}",
                        label = "$valInput ${fromUnit.symbol} = $formatted ${toUnit.symbol}",
                        subtext = "Factor: 1 ${fromUnit.symbol} = ${String.format(Locale.US, "%.6g", fromUnit.toBaseMultiplier / toUnit.toBaseMultiplier)} ${toUnit.symbol}"
                    )
                }
            }
        }

        // Live equivalence across all units in this category
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Equivalent Values Across Units",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    category.units.forEach { targetUnit ->
                        val targetVal = UnitConverterEngine.convert(rawVal, fromUnit, targetUnit)
                        val targetFormatted = if (targetVal >= 100000 || (targetVal > 0 && targetVal < 0.0001)) {
                            String.format(Locale.US, "%.4e", targetVal)
                        } else {
                            String.format(Locale.US, "%.4f", targetVal).trimEnd('0').trimEnd('.')
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(targetUnit.name, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "$targetFormatted ${targetUnit.symbol}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}

@Composable
fun ConvertedResultBanner(
    primary: String,
    label: String,
    subtext: String
) {
    val context = LocalContext.current

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                com.example.ui.components.ThreeDIconButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Converter Result", primary)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                    },
                    icon = Icons.Default.ContentCopy,
                    contentDescription = "Copy",
                    pressedColor = Color(0xFF00E5FF)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = primary,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtext,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
