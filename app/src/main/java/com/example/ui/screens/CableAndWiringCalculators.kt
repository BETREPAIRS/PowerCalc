package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.engine.AdvancedElectricalEngine
import com.example.ui.ElectricianViewModel
import com.example.ui.components.*

@Composable
fun MaxLengthIscContent(viewModel: ElectricianViewModel, defaultV: Double) {
    var voltage by remember { mutableStateOf(defaultV.toString()) }
    var cableSize by remember { mutableStateOf("2.5") }
    var breakerIa by remember { mutableStateOf("160") } // e.g. 16A Type C = 10x = 160A
    var isCopper by remember { mutableStateOf(true) }

    val vD = voltage.toDoubleOrNull() ?: defaultV
    val sD = cableSize.toDoubleOrNull() ?: 2.5
    val iaD = breakerIa.toDoubleOrNull() ?: 160.0

    val result = remember(vD, sD, iaD, isCopper) {
        AdvancedElectricalEngine.calculateMaxLengthIsc(vD, sD, iaD, isCopper)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ThreeDChip(selected = isCopper, onClick = { isCopper = true }, label = "Copper Conductor", modifier = Modifier.weight(1f))
            ThreeDChip(selected = !isCopper, onClick = { isCopper = false }, label = "Aluminium Conductor", modifier = Modifier.weight(1f))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = voltage, onValueChange = { voltage = it }, label = "Phase Voltage (U₀)", unitSuffix = "V", modifier = Modifier.weight(1f))
            NumericInputField(value = cableSize, onValueChange = { cableSize = it }, label = "Conductor Size", unitSuffix = "mm²", modifier = Modifier.weight(1f))
        }

        NumericInputField(value = breakerIa, onValueChange = { breakerIa = it }, label = "Magnetic Trip Current (Ia)", unitSuffix = "A")

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "Maximum Wire Length (Isc)",
                inputSummary = "U₀ = $voltage V, S = $cableSize mm², Ia = $breakerIa A",
                onSave = { res ->
                    viewModel.saveCalculation("max_length_isc", "Max Length Isc (${res.primaryValue})", "S = $cableSize mm², Ia = $breakerIa A", "L_max = ${res.primaryValue}", res.formula)
                },
                onReset = { voltage = defaultV.toString(); cableSize = "2.5"; breakerIa = "160"; isCopper = true }
            )
        }
    }
}

@Composable
fun CablePowerLossesContent(viewModel: ElectricianViewModel) {
    var current by remember { mutableStateOf("32") }
    var lengthM by remember { mutableStateOf("50") }
    var cableSize by remember { mutableStateOf("6") }
    var isThreePhase by remember { mutableStateOf(true) }
    var hoursPerDay by remember { mutableStateOf("12") }

    val iD = current.toDoubleOrNull() ?: 32.0
    val lD = lengthM.toDoubleOrNull() ?: 50.0
    val sD = cableSize.toDoubleOrNull() ?: 6.0
    val hD = hoursPerDay.toDoubleOrNull() ?: 12.0

    val result = remember(iD, lD, sD, isThreePhase, hD) {
        AdvancedElectricalEngine.calculateCablePowerLosses(iD, lD, sD, isThreePhase, hD)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ThreeDChip(selected = isThreePhase, onClick = { isThreePhase = true }, label = "3-Phase (3 Cores)", modifier = Modifier.weight(1f))
            ThreeDChip(selected = !isThreePhase, onClick = { isThreePhase = false }, label = "1-Phase (2 Cores)", modifier = Modifier.weight(1f))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = current, onValueChange = { current = it }, label = "Operating Current", unitSuffix = "A", modifier = Modifier.weight(1f))
            NumericInputField(value = lengthM, onValueChange = { lengthM = it }, label = "Route Length", unitSuffix = "m", modifier = Modifier.weight(1f))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = cableSize, onValueChange = { cableSize = it }, label = "Conductor Size", unitSuffix = "mm²", modifier = Modifier.weight(1f))
            NumericInputField(value = hoursPerDay, onValueChange = { hoursPerDay = it }, label = "Hours / Day", unitSuffix = "h", modifier = Modifier.weight(1f))
        }

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "Power Losses in Cables",
                inputSummary = "I = $current A, L = $lengthM m, S = $cableSize mm²",
                onSave = { res ->
                    viewModel.saveCalculation("cable_losses", "Losses (${res.primaryValue})", "I = $current A @ $lengthM m", "Loss = ${res.primaryValue}", res.formula)
                },
                onReset = { current = "32"; lengthM = "50"; cableSize = "6"; isThreePhase = true }
            )
        }
    }
}

@Composable
fun CableOperatingTemperatureContent(viewModel: ElectricianViewModel) {
    var loadCurrent by remember { mutableStateOf("45") }
    var ratedAmpacity by remember { mutableStateOf("50") }
    var ambientTemp by remember { mutableStateOf("30") }
    var isXlpe by remember { mutableStateOf(false) }

    val iD = loadCurrent.toDoubleOrNull() ?: 45.0
    val izD = ratedAmpacity.toDoubleOrNull() ?: 50.0
    val taD = ambientTemp.toDoubleOrNull() ?: 30.0
    val maxTemp = if (isXlpe) 90.0 else 70.0

    val result = remember(iD, izD, taD, maxTemp) {
        AdvancedElectricalEngine.calculateCableOperatingTemperature(taD, iD, izD, maxTemp)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ThreeDChip(selected = !isXlpe, onClick = { isXlpe = false }, label = "PVC (Max 70°C)", modifier = Modifier.weight(1f))
            ThreeDChip(selected = isXlpe, onClick = { isXlpe = true }, label = "XLPE / EPR (Max 90°C)", modifier = Modifier.weight(1f))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = loadCurrent, onValueChange = { loadCurrent = it }, label = "Load Current (I)", unitSuffix = "A", modifier = Modifier.weight(1f))
            NumericInputField(value = ratedAmpacity, onValueChange = { ratedAmpacity = it }, label = "Cable Ampacity (Iz)", unitSuffix = "A", modifier = Modifier.weight(1f))
        }

        NumericInputField(value = ambientTemp, onValueChange = { ambientTemp = it }, label = "Ambient Temperature", unitSuffix = "°C")

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "Calculation of Cable Temperature",
                inputSummary = "I = $loadCurrent A, Iz = $ratedAmpacity A, Ta = $ambientTemp°C",
                onSave = { res ->
                    viewModel.saveCalculation("cable_temperature", "Core Temp (${res.primaryValue})", "I = $loadCurrent A / $ratedAmpacity A", "T_core = ${res.primaryValue}", res.formula)
                },
                onReset = { loadCurrent = "45"; ratedAmpacity = "50"; ambientTemp = "30"; isXlpe = false }
            )
        }
    }
}

@Composable
fun CableImpedanceContent(viewModel: ElectricianViewModel) {
    var cableSize by remember { mutableStateOf("16") }
    var lengthM by remember { mutableStateOf("100") }
    var isCopper by remember { mutableStateOf(true) }

    val sD = cableSize.toDoubleOrNull() ?: 16.0
    val lD = lengthM.toDoubleOrNull() ?: 100.0

    val result = remember(sD, lD, isCopper) {
        AdvancedElectricalEngine.calculateCableImpedance(sD, lD, isCopper)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ThreeDChip(selected = isCopper, onClick = { isCopper = true }, label = "Copper Cores", modifier = Modifier.weight(1f))
            ThreeDChip(selected = !isCopper, onClick = { isCopper = false }, label = "Aluminium Cores", modifier = Modifier.weight(1f))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = cableSize, onValueChange = { cableSize = it }, label = "Conductor Size", unitSuffix = "mm²", modifier = Modifier.weight(1f))
            NumericInputField(value = lengthM, onValueChange = { lengthM = it }, label = "Length", unitSuffix = "m", modifier = Modifier.weight(1f))
        }

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "Cable Resistance, Reactance & Impedance",
                inputSummary = "S = $cableSize mm², L = $lengthM m (${if (isCopper) "Cu" else "Al"})",
                onSave = { res ->
                    viewModel.saveCalculation("cable_impedance", "Impedance (${res.primaryValue})", "S = $cableSize mm², L = $lengthM m", "Z = ${res.primaryValue}", res.formula)
                },
                onReset = { cableSize = "16"; lengthM = "100"; isCopper = true }
            )
        }
    }
}

@Composable
fun DistributedVoltageDropContent(viewModel: ElectricianViewModel, defaultV: Double) {
    var voltage by remember { mutableStateOf(defaultV.toString()) }
    var cableSize by remember { mutableStateOf("4.0") }
    var loadsText by remember { mutableStateOf("10A@20m, 8A@40m, 6A@60m") }
    var isThreePhase by remember { mutableStateOf(false) }

    val vD = voltage.toDoubleOrNull() ?: defaultV
    val sD = cableSize.toDoubleOrNull() ?: 4.0

    val result = remember(vD, sD, loadsText, isThreePhase) {
        AdvancedElectricalEngine.calculateDistributedVoltageDrop(vD, sD, loadsText, isThreePhase)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ThreeDChip(selected = !isThreePhase, onClick = { isThreePhase = false }, label = "1-Phase Branch", modifier = Modifier.weight(1f))
            ThreeDChip(selected = isThreePhase, onClick = { isThreePhase = true }, label = "3-Phase Branch", modifier = Modifier.weight(1f))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = voltage, onValueChange = { voltage = it }, label = "System Voltage", unitSuffix = "V", modifier = Modifier.weight(1f))
            NumericInputField(value = cableSize, onValueChange = { cableSize = it }, label = "Conductor Size", unitSuffix = "mm²", modifier = Modifier.weight(1f))
        }

        OutlinedTextField(
            value = loadsText,
            onValueChange = { loadsText = it },
            label = { Text("Distributed Loads (e.g. 10A@20m, 8A@40m)") },
            modifier = Modifier.fillMaxWidth()
        )

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "Voltage Drop with Distributed Loads",
                inputSummary = "V = $voltage V, S = $cableSize mm², Loads: $loadsText",
                onSave = { res ->
                    viewModel.saveCalculation("distributed_voltage_drop", "Distributed Drop (${res.primaryValue})", "Loads: $loadsText", "ΔV = ${res.primaryValue}", res.formula)
                },
                onReset = { voltage = defaultV.toString(); cableSize = "4.0"; loadsText = "10A@20m, 8A@40m, 6A@60m"; isThreePhase = false }
            )
        }
    }
}

@Composable
fun BusbarAmpacityContent(viewModel: ElectricianViewModel) {
    var widthMm by remember { mutableStateOf("50") }
    var thicknessMm by remember { mutableStateOf("5") }
    var isCopper by remember { mutableStateOf(true) }
    var isPainted by remember { mutableStateOf(false) }

    val wD = widthMm.toDoubleOrNull() ?: 50.0
    val tD = thicknessMm.toDoubleOrNull() ?: 5.0

    val result = remember(wD, tD, isCopper, isPainted) {
        AdvancedElectricalEngine.calculateBusbarAmpacity(wD, tD, isCopper, isPainted)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ThreeDChip(selected = isCopper, onClick = { isCopper = true }, label = "Copper (Cu-ETP)", modifier = Modifier.weight(1f))
            ThreeDChip(selected = !isCopper, onClick = { isCopper = false }, label = "Aluminium (Al)", modifier = Modifier.weight(1f))
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ThreeDChip(selected = !isPainted, onClick = { isPainted = false }, label = "Bare Metal", modifier = Modifier.weight(1f))
            ThreeDChip(selected = isPainted, onClick = { isPainted = true }, label = "Painted / Coated (+15%)", modifier = Modifier.weight(1f))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = widthMm, onValueChange = { widthMm = it }, label = "Width", unitSuffix = "mm", modifier = Modifier.weight(1f))
            NumericInputField(value = thicknessMm, onValueChange = { thicknessMm = it }, label = "Thickness", unitSuffix = "mm", modifier = Modifier.weight(1f))
        }

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "Current Carrying Capacity of Busbar",
                inputSummary = "${widthMm}x${thicknessMm} mm (${if (isCopper) "Copper" else "Aluminium"})",
                onSave = { res ->
                    viewModel.saveCalculation("busbar_ampacity", "Busbar (${res.primaryValue})", "${widthMm}x${thicknessMm} mm", "Iz = ${res.primaryValue}", res.formula)
                },
                onReset = { widthMm = "50"; thicknessMm = "5"; isCopper = true; isPainted = false }
            )
        }
    }
}

@Composable
fun ConduitTrayFillContent(viewModel: ElectricianViewModel) {
    var conduitSize by remember { mutableDoubleStateOf(1.0) } // 1.0 inch
    var wireCount by remember { mutableIntStateOf(4) }
    var wireDiameterMm by remember { mutableStateOf("6.5") }

    val dD = wireDiameterMm.toDoubleOrNull() ?: 6.5

    val result = remember(conduitSize, wireCount, dD) {
        AdvancedElectricalEngine.calculateConduitTrayFill(conduitSize, wireCount, dD)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Conduit Trade Size (EMT):", style = MaterialTheme.typography.labelMedium)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(0.75, 1.0, 1.25, 1.5, 2.0).forEach { size ->
                ThreeDChip(
                    selected = conduitSize == size,
                    onClick = { conduitSize = size },
                    label = if (size == 0.75) "3/4\"" else if (size == 1.25) "1-1/4\"" else if (size == 1.5) "1-1/2\"" else "${size.toInt()}\"",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = wireCount.toString(), onValueChange = { wireCount = it.toIntOrNull() ?: 1 }, label = "Number of Wires", unitSuffix = "qty", modifier = Modifier.weight(1f))
            NumericInputField(value = wireDiameterMm, onValueChange = { wireDiameterMm = it }, label = "Outer Cable OD", unitSuffix = "mm", modifier = Modifier.weight(1f))
        }

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "Conduit and Cable Tray Sizing",
                inputSummary = "$wireCount wires of OD $wireDiameterMm mm in $conduitSize\" conduit",
                onSave = { res ->
                    viewModel.saveCalculation("conduit_tray_fill", "Conduit Fill (${res.primaryValue})", "$wireCount wires in $conduitSize\"", "Fill = ${res.primaryValue}", res.formula)
                },
                onReset = { conduitSize = 1.0; wireCount = 4; wireDiameterMm = "6.5" }
            )
        }
    }
}
