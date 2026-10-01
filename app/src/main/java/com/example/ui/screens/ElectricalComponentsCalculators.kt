package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.engine.AdvancedElectricalEngine
import com.example.engine.ElectricalFormulas
import com.example.ui.ElectricianViewModel
import com.example.ui.components.*

@Composable
fun VoltageDividerContent(viewModel: ElectricianViewModel) {
    var vin by remember { mutableStateOf("12") }
    var r1 by remember { mutableStateOf("1000") }
    var r2 by remember { mutableStateOf("2200") }

    val vinD = vin.toDoubleOrNull() ?: 12.0
    val r1D = r1.toDoubleOrNull() ?: 1000.0
    val r2D = r2.toDoubleOrNull() ?: 2200.0

    val result = remember(vinD, r1D, r2D) {
        AdvancedElectricalEngine.calculateVoltageDivider(vinD, r1D, r2D)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        NumericInputField(value = vin, onValueChange = { vin = it }, label = "Input Voltage (Vin)", unitSuffix = "V")

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = r1, onValueChange = { r1 = it }, label = "Top Resistor (R1)", unitSuffix = "Ω", modifier = Modifier.weight(1f))
            NumericInputField(value = r2, onValueChange = { r2 = it }, label = "Bottom Resistor (R2)", unitSuffix = "Ω", modifier = Modifier.weight(1f))
        }

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "Voltage Divider",
                inputSummary = "Vin = $vin V, R1 = $r1 Ω, R2 = $r2 Ω",
                onSave = { res ->
                    viewModel.saveCalculation("voltage_divider", "Vout (${res.primaryValue})", "Vin = $vin V, R1=$r1, R2=$r2", "Vout = ${res.primaryValue}", res.formula)
                },
                onReset = { vin = "12"; r1 = "1000"; r2 = "2200" }
            )
        }
    }
}

@Composable
fun CurrentDividerContent(viewModel: ElectricianViewModel) {
    var itotal by remember { mutableStateOf("10") }
    var r1 by remember { mutableStateOf("100") }
    var r2 by remember { mutableStateOf("220") }

    val itD = itotal.toDoubleOrNull() ?: 10.0
    val r1D = r1.toDoubleOrNull() ?: 100.0
    val r2D = r2.toDoubleOrNull() ?: 220.0

    val result = remember(itD, r1D, r2D) {
        AdvancedElectricalEngine.calculateCurrentDivider(itD, r1D, r2D)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        NumericInputField(value = itotal, onValueChange = { itotal = it }, label = "Total Entering Current", unitSuffix = "A")

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = r1, onValueChange = { r1 = it }, label = "Branch 1 (R1)", unitSuffix = "Ω", modifier = Modifier.weight(1f))
            NumericInputField(value = r2, onValueChange = { r2 = it }, label = "Branch 2 (R2)", unitSuffix = "Ω", modifier = Modifier.weight(1f))
        }

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "Current Divider",
                inputSummary = "Itotal = $itotal A, R1 = $r1 Ω, R2 = $r2 Ω",
                onSave = { res ->
                    viewModel.saveCalculation("current_divider", "I1 (${res.primaryValue})", "Itotal = $itotal A", "I1 = ${res.primaryValue}", res.formula)
                },
                onReset = { itotal = "10"; r1 = "100"; r2 = "220" }
            )
        }
    }
}

@Composable
fun ResonantFrequencyContent(viewModel: ElectricianViewModel) {
    var inductanceMh by remember { mutableStateOf("10") }
    var capacitanceUf by remember { mutableStateOf("1.0") }

    val lD = inductanceMh.toDoubleOrNull() ?: 10.0
    val cD = capacitanceUf.toDoubleOrNull() ?: 1.0

    val result = remember(lD, cD) {
        AdvancedElectricalEngine.calculateResonantFrequency(lD, cD)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = inductanceMh, onValueChange = { inductanceMh = it }, label = "Inductance (L)", unitSuffix = "mH", modifier = Modifier.weight(1f))
            NumericInputField(value = capacitanceUf, onValueChange = { capacitanceUf = it }, label = "Capacitance (C)", unitSuffix = "µF", modifier = Modifier.weight(1f))
        }

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "Resonant Frequency",
                inputSummary = "L = $inductanceMh mH, C = $capacitanceUf µF",
                onSave = { res ->
                    viewModel.saveCalculation("resonant_frequency", "Resonance (${res.primaryValue})", "L = $inductanceMh mH, C = $capacitanceUf µF", "f0 = ${res.primaryValue}", res.formula)
                },
                onReset = { inductanceMh = "10"; capacitanceUf = "1.0" }
            )
        }
    }
}

@Composable
fun JouleEffectContent(viewModel: ElectricianViewModel) {
    var current by remember { mutableStateOf("16") }
    var resistance by remember { mutableStateOf("1.5") }
    var timeSec by remember { mutableStateOf("60") }

    val iD = current.toDoubleOrNull() ?: 16.0
    val rD = resistance.toDoubleOrNull() ?: 1.5
    val tD = timeSec.toDoubleOrNull() ?: 60.0

    val result = remember(iD, rD, tD) {
        AdvancedElectricalEngine.calculateJouleEffect(iD, rD, tD)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = current, onValueChange = { current = it }, label = "Current (I)", unitSuffix = "A", modifier = Modifier.weight(1f))
            NumericInputField(value = resistance, onValueChange = { resistance = it }, label = "Resistance (R)", unitSuffix = "Ω", modifier = Modifier.weight(1f))
        }

        NumericInputField(value = timeSec, onValueChange = { timeSec = it }, label = "Time Duration", unitSuffix = "sec")

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "Joule Effect (Thermal Energy)",
                inputSummary = "I = $current A, R = $resistance Ω, t = $timeSec s",
                onSave = { res ->
                    viewModel.saveCalculation("joule_effect", "Thermal Energy (${res.primaryValue})", "I = $current A, R = $resistance Ω", "Q = ${res.primaryValue}", res.formula)
                },
                onReset = { current = "16"; resistance = "1.5"; timeSec = "60" }
            )
        }
    }
}

@Composable
fun ZenerRegulatorContent(viewModel: ElectricianViewModel) {
    var vinMin by remember { mutableStateOf("12") }
    var vinMax by remember { mutableStateOf("15") }
    var vz by remember { mutableStateOf("5.1") }
    var iloadMax by remember { mutableStateOf("50") }

    val vMinD = vinMin.toDoubleOrNull() ?: 12.0
    val vMaxD = vinMax.toDoubleOrNull() ?: 15.0
    val vzD = vz.toDoubleOrNull() ?: 5.1
    val ilD = iloadMax.toDoubleOrNull() ?: 50.0

    val result = remember(vMinD, vMaxD, vzD, ilD) {
        AdvancedElectricalEngine.calculateZenerRegulator(vMinD, vMaxD, vzD, ilD)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = vinMin, onValueChange = { vinMin = it }, label = "Vin Min", unitSuffix = "V", modifier = Modifier.weight(1f))
            NumericInputField(value = vinMax, onValueChange = { vinMax = it }, label = "Vin Max", unitSuffix = "V", modifier = Modifier.weight(1f))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = vz, onValueChange = { vz = it }, label = "Zener Voltage (Vz)", unitSuffix = "V", modifier = Modifier.weight(1f))
            NumericInputField(value = iloadMax, onValueChange = { iloadMax = it }, label = "Max Load Current", unitSuffix = "mA", modifier = Modifier.weight(1f))
        }

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "Zener Diode as Voltage Stabilizer",
                inputSummary = "Vin: $vinMin–$vinMax V, Vz = $vz V, Iload = $iloadMax mA",
                onSave = { res ->
                    viewModel.saveCalculation("zener_regulator", "Zener Rs (${res.primaryValue})", "Vz = $vz V, Iload = $iloadMax mA", "Rs = ${res.primaryValue}", res.formula)
                },
                onReset = { vinMin = "12"; vinMax = "15"; vz = "5.1"; iloadMax = "50" }
            )
        }
    }
}

@Composable
fun CapacitorCodeDecoderContent(viewModel: ElectricianViewModel) {
    var code by remember { mutableStateOf("104") }

    val result = remember(code) {
        ElectricalFormulas.decodeCapacitorCode(code)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = code,
            onValueChange = { code = it },
            label = { Text("Capacitor 3-Digit Code (e.g. 104, 473, 222)") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("102", "103", "104", "473", "224").forEach { sample ->
                ThreeDChip(
                    selected = code == sample,
                    onClick = { code = sample },
                    label = sample,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "Capacitor Code",
                inputSummary = "Code: $code",
                onSave = { res ->
                    viewModel.saveCalculation("capacitor_code", "Capacitor (${res.primaryValue})", "Code: $code", "C = ${res.primaryValue}", res.formula)
                },
                onReset = { code = "104" }
            )
        }
    }
}
