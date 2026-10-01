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
fun ApparentPowerContent(viewModel: ElectricianViewModel, defaultV: Double) {
    var voltage by remember { mutableStateOf(defaultV.toString()) }
    var current by remember { mutableStateOf("25") }
    var isThreePhase by remember { mutableStateOf(true) }

    val vD = voltage.toDoubleOrNull() ?: defaultV
    val iD = current.toDoubleOrNull() ?: 25.0

    val result = remember(vD, iD, isThreePhase) {
        AdvancedElectricalEngine.calculateApparentPower(vD, iD, isThreePhase)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ThreeDChip(selected = isThreePhase, onClick = { isThreePhase = true }, label = "3-Phase AC", modifier = Modifier.weight(1f))
            ThreeDChip(selected = !isThreePhase, onClick = { isThreePhase = false }, label = "1-Phase AC", modifier = Modifier.weight(1f))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = voltage, onValueChange = { voltage = it }, label = "Voltage", unitSuffix = "V", modifier = Modifier.weight(1f))
            NumericInputField(value = current, onValueChange = { current = it }, label = "Current", unitSuffix = "A", modifier = Modifier.weight(1f))
        }

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "Calculation of Apparent Power",
                inputSummary = "V = $voltage V, I = $current A (${if (isThreePhase) "3-Phase" else "1-Phase"})",
                onSave = { res ->
                    viewModel.saveCalculation("apparent_power", "S (${res.primaryValue})", "V = $voltage V, I = $current A", "S = ${res.primaryValue}", res.formula)
                },
                onReset = { voltage = defaultV.toString(); current = "25"; isThreePhase = true }
            )
        }
    }
}

@Composable
fun ReactivePowerContent(viewModel: ElectricianViewModel, defaultV: Double) {
    var voltage by remember { mutableStateOf(defaultV.toString()) }
    var current by remember { mutableStateOf("30") }
    var pf by remember { mutableStateOf("0.85") }
    var isThreePhase by remember { mutableStateOf(true) }

    val vD = voltage.toDoubleOrNull() ?: defaultV
    val iD = current.toDoubleOrNull() ?: 30.0
    val pfD = pf.toDoubleOrNull() ?: 0.85

    val result = remember(vD, iD, pfD, isThreePhase) {
        AdvancedElectricalEngine.calculateReactivePower(vD, iD, pfD, isThreePhase)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ThreeDChip(selected = isThreePhase, onClick = { isThreePhase = true }, label = "3-Phase AC", modifier = Modifier.weight(1f))
            ThreeDChip(selected = !isThreePhase, onClick = { isThreePhase = false }, label = "1-Phase AC", modifier = Modifier.weight(1f))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = voltage, onValueChange = { voltage = it }, label = "Voltage", unitSuffix = "V", modifier = Modifier.weight(1f))
            NumericInputField(value = current, onValueChange = { current = it }, label = "Current", unitSuffix = "A", modifier = Modifier.weight(1f))
        }

        NumericInputField(value = pf, onValueChange = { pf = it }, label = "Power Factor (cosφ)", unitSuffix = "0-1")

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "Calculation of Reactive Power",
                inputSummary = "V = $voltage V, I = $current A, PF = $pf",
                onSave = { res ->
                    viewModel.saveCalculation("reactive_power", "Q (${res.primaryValue})", "V = $voltage V, I = $current A, PF=$pf", "Q = ${res.primaryValue}", res.formula)
                },
                onReset = { voltage = defaultV.toString(); current = "30"; pf = "0.85"; isThreePhase = true }
            )
        }
    }
}

@Composable
fun NeutralCurrentContent(viewModel: ElectricianViewModel) {
    var iL1 by remember { mutableStateOf("45") }
    var iL2 by remember { mutableStateOf("38") }
    var iL3 by remember { mutableStateOf("22") }
    var thd3rd by remember { mutableStateOf("15") } // 15% 3rd harmonic

    val i1 = iL1.toDoubleOrNull() ?: 45.0
    val i2 = iL2.toDoubleOrNull() ?: 38.0
    val i3 = iL3.toDoubleOrNull() ?: 22.0
    val thd = thd3rd.toDoubleOrNull() ?: 15.0

    val result = remember(i1, i2, i3, thd) {
        AdvancedElectricalEngine.calculateNeutralCurrent(i1, i2, i3, thd)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            NumericInputField(value = iL1, onValueChange = { iL1 = it }, label = "Phase L1", unitSuffix = "A", modifier = Modifier.weight(1f))
            NumericInputField(value = iL2, onValueChange = { iL2 = it }, label = "Phase L2", unitSuffix = "A", modifier = Modifier.weight(1f))
            NumericInputField(value = iL3, onValueChange = { iL3 = it }, label = "Phase L3", unitSuffix = "A", modifier = Modifier.weight(1f))
        }

        NumericInputField(value = thd3rd, onValueChange = { thd3rd = it }, label = "Triplen 3rd Harmonic Content", unitSuffix = "%")

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "Neutral Current (Unbalanced Loads)",
                inputSummary = "L1 = $iL1 A, L2 = $iL2 A, L3 = $iL3 A",
                onSave = { res ->
                    viewModel.saveCalculation("neutral_current", "Neutral (${res.primaryValue})", "L1=$iL1, L2=$iL2, L3=$iL3", "IN = ${res.primaryValue}", res.formula)
                },
                onReset = { iL1 = "45"; iL2 = "38"; iL3 = "22"; thd3rd = "15" }
            )
        }
    }
}

@Composable
fun CapacitorDifferentVoltageContent(viewModel: ElectricianViewModel) {
    var nominalQ by remember { mutableStateOf("25") }
    var nominalV by remember { mutableStateOf("400") }
    var actualV by remember { mutableStateOf("380") }

    val qD = nominalQ.toDoubleOrNull() ?: 25.0
    val nvD = nominalV.toDoubleOrNull() ?: 400.0
    val avD = actualV.toDoubleOrNull() ?: 380.0

    val result = remember(qD, nvD, avD) {
        AdvancedElectricalEngine.calculateCapacitorAtDifferentVoltage(qD, nvD, avD)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        NumericInputField(value = nominalQ, onValueChange = { nominalQ = it }, label = "Capacitor Nameplate Output", unitSuffix = "kVAR")

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = nominalV, onValueChange = { nominalV = it }, label = "Rated Voltage (V₁)", unitSuffix = "V", modifier = Modifier.weight(1f))
            NumericInputField(value = actualV, onValueChange = { actualV = it }, label = "Operating Voltage (V₂)", unitSuffix = "V", modifier = Modifier.weight(1f))
        }

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "Capacitor Power at Different Voltage",
                inputSummary = "Q1 = $nominalQ kVAR @ $nominalV V, Operating: $actualV V",
                onSave = { res ->
                    viewModel.saveCalculation("capacitor_voltage_power", "Capacitor (${res.primaryValue})", "Rated: $nominalQ kVAR @ $nominalV V", "Q2 = ${res.primaryValue}", res.formula)
                },
                onReset = { nominalQ = "25"; nominalV = "400"; actualV = "380" }
            )
        }
    }
}

@Composable
fun AnalogSignalConverterContent(viewModel: ElectricianViewModel) {
    var inputVal by remember { mutableStateOf("12") }
    var inMin by remember { mutableStateOf("4") }
    var inMax by remember { mutableStateOf("20") }
    var outMin by remember { mutableStateOf("0") }
    var outMax by remember { mutableStateOf("100") }

    val ivD = inputVal.toDoubleOrNull() ?: 12.0
    val iminD = inMin.toDoubleOrNull() ?: 4.0
    val imaxD = inMax.toDoubleOrNull() ?: 20.0
    val ominD = outMin.toDoubleOrNull() ?: 0.0
    val omaxD = outMax.toDoubleOrNull() ?: 100.0

    val result = remember(ivD, iminD, imaxD, ominD, omaxD) {
        AdvancedElectricalEngine.calculateAnalogSignalScale(ivD, iminD, imaxD, ominD, omaxD)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            ThreeDChip(selected = inMin == "4" && inMax == "20", onClick = { inMin = "4"; inMax = "20"; inputVal = "12" }, label = "4–20 mA Input", modifier = Modifier.weight(1f))
            ThreeDChip(selected = inMin == "0" && inMax == "10", onClick = { inMin = "0"; inMax = "10"; inputVal = "5" }, label = "0–10 V Input", modifier = Modifier.weight(1f))
        }

        NumericInputField(value = inputVal, onValueChange = { inputVal = it }, label = "Current Input Signal Value", unitSuffix = "Signal")

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = outMin, onValueChange = { outMin = it }, label = "Engineering Unit Min", unitSuffix = "Min", modifier = Modifier.weight(1f))
            NumericInputField(value = outMax, onValueChange = { outMax = it }, label = "Engineering Unit Max", unitSuffix = "Max", modifier = Modifier.weight(1f))
        }

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "Analog Signal Values Conversion",
                inputSummary = "Input: $inputVal ($inMin–$inMax) -> Output: ($outMin–$outMax)",
                onSave = { res ->
                    viewModel.saveCalculation("analog_signal_converter", "Analog (${res.primaryValue})", "In: $inputVal ($inMin-$inMax)", "Out = ${res.primaryValue}", res.formula)
                },
                onReset = { inputVal = "12"; inMin = "4"; inMax = "20"; outMin = "0"; outMax = "100" }
            )
        }
    }
}

@Composable
fun AntennaLengthContent(viewModel: ElectricianViewModel) {
    var freqMhz by remember { mutableStateOf("433.92") }

    val fD = freqMhz.toDoubleOrNull() ?: 433.92

    val result = remember(fD) {
        AdvancedElectricalEngine.calculateAntennaLength(fD)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        NumericInputField(value = freqMhz, onValueChange = { freqMhz = it }, label = "Operating Frequency", unitSuffix = "MHz")

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("868.0", "433.92", "2400.0", "1575.42").forEach { sample ->
                ThreeDChip(
                    selected = freqMhz == sample,
                    onClick = { freqMhz = sample },
                    label = when (sample) {
                        "433.92" -> "433 MHz"
                        "868.0" -> "868 MHz"
                        "2400.0" -> "2.4 GHz"
                        else -> "GPS"
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "Antenna Length Calculation",
                inputSummary = "f = $freqMhz MHz",
                onSave = { res ->
                    viewModel.saveCalculation("antenna_length", "Antenna (${res.primaryValue})", "f = $freqMhz MHz", "λ/4 = ${res.primaryValue}", res.formula)
                },
                onReset = { freqMhz = "433.92" }
            )
        }
    }
}

@Composable
fun CctvStorageContent(viewModel: ElectricianViewModel) {
    var cameraCount by remember { mutableIntStateOf(8) }
    var retentionDays by remember { mutableIntStateOf(30) }
    var resolution by remember { mutableStateOf("1080p") }
    var compression by remember { mutableStateOf("H.265") }

    val result = remember(cameraCount, retentionDays, resolution, compression) {
        AdvancedElectricalEngine.calculateCctvStorage(cameraCount, retentionDays, resolution, compression)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("1080p", "4MP (2K)", "4K (8MP)").forEach { res ->
                ThreeDChip(
                    selected = resolution == res,
                    onClick = { resolution = res },
                    label = res,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ThreeDChip(selected = compression == "H.265", onClick = { compression = "H.265" }, label = "H.265 (High Efficiency)", modifier = Modifier.weight(1f))
            ThreeDChip(selected = compression == "H.264", onClick = { compression = "H.264" }, label = "H.264 Standard", modifier = Modifier.weight(1f))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = cameraCount.toString(), onValueChange = { cameraCount = it.toIntOrNull() ?: 1 }, label = "Cameras Count", unitSuffix = "cams", modifier = Modifier.weight(1f))
            NumericInputField(value = retentionDays.toString(), onValueChange = { retentionDays = it.toIntOrNull() ?: 1 }, label = "Recording Days", unitSuffix = "days", modifier = Modifier.weight(1f))
        }

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "CCTV Hard Disk & Bandwidth Sizing",
                inputSummary = "$cameraCount cams × $retentionDays days @ $resolution ($compression)",
                onSave = { res ->
                    viewModel.saveCalculation("cctv_storage_calc", "HDD Storage (${res.primaryValue})", "$cameraCount cams × $retentionDays d", "Storage = ${res.primaryValue}", res.formula)
                },
                onReset = { cameraCount = 8; retentionDays = 30; resolution = "1080p"; compression = "H.265" }
            )
        }
    }
}
