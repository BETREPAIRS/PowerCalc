package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.CalculationResult
import com.example.engine.ElectricalFormulas
import com.example.ui.ElectricianViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

// -------------------------------------------------------------
// 1. MOTOR POWER CALCULATOR
// -------------------------------------------------------------
@Composable
fun MotorPowerContent(viewModel: ElectricianViewModel, defaultV: Double) {
    var voltage by remember { mutableStateOf(defaultV.toString()) }
    var current by remember { mutableStateOf("15") }
    var powerFactor by remember { mutableStateOf("0.85") }
    var efficiency by remember { mutableStateOf("90") }
    var isThreePhase by remember { mutableStateOf(true) }

    val voltD = voltage.toDoubleOrNull() ?: defaultV
    val currD = current.toDoubleOrNull() ?: 15.0
    val pfD = powerFactor.toDoubleOrNull() ?: 0.85
    val etaD = efficiency.toDoubleOrNull() ?: 90.0

    val result = remember(voltD, currD, pfD, etaD, isThreePhase) {
        ElectricalFormulas.calculateMotorPower(voltD, currD, pfD, etaD, isThreePhase)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ThreeDChip(
                selected = isThreePhase,
                onClick = { isThreePhase = true },
                label = "3-Phase AC",
                modifier = Modifier.weight(1f)
            )
            ThreeDChip(
                selected = !isThreePhase,
                onClick = { isThreePhase = false },
                label = "1-Phase AC",
                modifier = Modifier.weight(1f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = voltage, onValueChange = { voltage = it }, label = "Line Voltage", unitSuffix = "V", modifier = Modifier.weight(1f))
            NumericInputField(value = current, onValueChange = { current = it }, label = "Full-Load Current", unitSuffix = "A", modifier = Modifier.weight(1f))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = powerFactor, onValueChange = { powerFactor = it }, label = "Power Factor", unitSuffix = "cosφ", modifier = Modifier.weight(1f))
            NumericInputField(value = efficiency, onValueChange = { efficiency = it }, label = "Efficiency (η)", unitSuffix = "%", modifier = Modifier.weight(1f))
        }

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "Motor Power",
                inputSummary = "V = $voltage V, I = $current A, PF = $powerFactor, η = $efficiency%",
                onSave = { res ->
                    viewModel.saveCalculation("motor_power", "Power (${res.primaryValue})", "V = $voltage V, I = $current A", "P = ${res.primaryValue}", res.formula)
                },
                onReset = { voltage = defaultV.toString(); current = "15"; powerFactor = "0.85"; efficiency = "90" }
            )
        }
    }
}

// -------------------------------------------------------------
// 2. MOTOR VOLTAGE CALCULATOR
// -------------------------------------------------------------
@Composable
fun MotorVoltageContent(viewModel: ElectricianViewModel) {
    var powerKw by remember { mutableStateOf("7.5") }
    var current by remember { mutableStateOf("14.5") }
    var powerFactor by remember { mutableStateOf("0.85") }
    var efficiency by remember { mutableStateOf("89") }
    var isThreePhase by remember { mutableStateOf(true) }

    val pD = powerKw.toDoubleOrNull() ?: 7.5
    val iD = current.toDoubleOrNull() ?: 14.5
    val pfD = powerFactor.toDoubleOrNull() ?: 0.85
    val etaD = efficiency.toDoubleOrNull() ?: 89.0

    val result = remember(pD, iD, pfD, etaD, isThreePhase) {
        ElectricalFormulas.calculateMotorVoltage(pD, iD, pfD, etaD, isThreePhase)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ThreeDChip(selected = isThreePhase, onClick = { isThreePhase = true }, label = "3-Phase AC", modifier = Modifier.weight(1f))
            ThreeDChip(selected = !isThreePhase, onClick = { isThreePhase = false }, label = "1-Phase AC", modifier = Modifier.weight(1f))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = powerKw, onValueChange = { powerKw = it }, label = "Shaft Power", unitSuffix = "kW", modifier = Modifier.weight(1f))
            NumericInputField(value = current, onValueChange = { current = it }, label = "Rated Current", unitSuffix = "A", modifier = Modifier.weight(1f))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = powerFactor, onValueChange = { powerFactor = it }, label = "Power Factor", unitSuffix = "cosφ", modifier = Modifier.weight(1f))
            NumericInputField(value = efficiency, onValueChange = { efficiency = it }, label = "Efficiency (η)", unitSuffix = "%", modifier = Modifier.weight(1f))
        }

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "Motor Voltage",
                inputSummary = "P = $powerKw kW, I = $current A, PF = $powerFactor",
                onSave = { res ->
                    viewModel.saveCalculation("motor_voltage", "Voltage (${res.primaryValue})", "P = $powerKw kW, I = $current A", "V = ${res.primaryValue}", res.formula)
                },
                onReset = { powerKw = "7.5"; current = "14.5"; powerFactor = "0.85"; efficiency = "89" }
            )
        }
    }
}

// -------------------------------------------------------------
// 3. MOTOR POWER FACTOR CALCULATOR
// -------------------------------------------------------------
@Composable
fun MotorPowerFactorContent(viewModel: ElectricianViewModel, defaultV: Double) {
    var powerKw by remember { mutableStateOf("11") }
    var voltage by remember { mutableStateOf(defaultV.toString()) }
    var current by remember { mutableStateOf("21") }
    var efficiency by remember { mutableStateOf("91") }
    var isThreePhase by remember { mutableStateOf(true) }

    val pD = powerKw.toDoubleOrNull() ?: 11.0
    val vD = voltage.toDoubleOrNull() ?: defaultV
    val iD = current.toDoubleOrNull() ?: 21.0
    val etaD = efficiency.toDoubleOrNull() ?: 91.0

    val result = remember(pD, vD, iD, etaD, isThreePhase) {
        ElectricalFormulas.calculateMotorPowerFactor(pD, vD, iD, etaD, isThreePhase)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ThreeDChip(selected = isThreePhase, onClick = { isThreePhase = true }, label = "3-Phase AC", modifier = Modifier.weight(1f))
            ThreeDChip(selected = !isThreePhase, onClick = { isThreePhase = false }, label = "1-Phase AC", modifier = Modifier.weight(1f))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = powerKw, onValueChange = { powerKw = it }, label = "Shaft Power", unitSuffix = "kW", modifier = Modifier.weight(1f))
            NumericInputField(value = voltage, onValueChange = { voltage = it }, label = "Line Voltage", unitSuffix = "V", modifier = Modifier.weight(1f))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = current, onValueChange = { current = it }, label = "Measured Current", unitSuffix = "A", modifier = Modifier.weight(1f))
            NumericInputField(value = efficiency, onValueChange = { efficiency = it }, label = "Efficiency (η)", unitSuffix = "%", modifier = Modifier.weight(1f))
        }

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "Motor Power Factor",
                inputSummary = "P = $powerKw kW, V = $voltage V, I = $current A",
                onSave = { res ->
                    viewModel.saveCalculation("motor_power_factor", "PF (${res.primaryValue})", "P = $powerKw kW, V = $voltage V, I = $current A", "cosφ = ${res.primaryValue}", res.formula)
                },
                onReset = { powerKw = "11"; voltage = defaultV.toString(); current = "21"; efficiency = "91" }
            )
        }
    }
}

// -------------------------------------------------------------
// 4. MOTOR EFFICIENCY CALCULATOR
// -------------------------------------------------------------
@Composable
fun MotorEfficiencyContent(viewModel: ElectricianViewModel, defaultV: Double) {
    var powerOutKw by remember { mutableStateOf("15") }
    var voltage by remember { mutableStateOf(defaultV.toString()) }
    var current by remember { mutableStateOf("28") }
    var powerFactor by remember { mutableStateOf("0.86") }
    var isThreePhase by remember { mutableStateOf(true) }

    val pD = powerOutKw.toDoubleOrNull() ?: 15.0
    val vD = voltage.toDoubleOrNull() ?: defaultV
    val iD = current.toDoubleOrNull() ?: 28.0
    val pfD = powerFactor.toDoubleOrNull() ?: 0.86

    val result = remember(pD, vD, iD, pfD, isThreePhase) {
        ElectricalFormulas.calculateMotorEfficiency(pD, vD, iD, pfD, isThreePhase)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ThreeDChip(selected = isThreePhase, onClick = { isThreePhase = true }, label = "3-Phase AC", modifier = Modifier.weight(1f))
            ThreeDChip(selected = !isThreePhase, onClick = { isThreePhase = false }, label = "1-Phase AC", modifier = Modifier.weight(1f))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = powerOutKw, onValueChange = { powerOutKw = it }, label = "Shaft Output", unitSuffix = "kW", modifier = Modifier.weight(1f))
            NumericInputField(value = voltage, onValueChange = { voltage = it }, label = "Supply Voltage", unitSuffix = "V", modifier = Modifier.weight(1f))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = current, onValueChange = { current = it }, label = "Input Current", unitSuffix = "A", modifier = Modifier.weight(1f))
            NumericInputField(value = powerFactor, onValueChange = { powerFactor = it }, label = "Power Factor", unitSuffix = "cosφ", modifier = Modifier.weight(1f))
        }

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "Motor Efficiency",
                inputSummary = "Pout = $powerOutKw kW, V = $voltage V, I = $current A, PF = $powerFactor",
                onSave = { res ->
                    viewModel.saveCalculation("motor_efficiency", "Efficiency (${res.primaryValue})", "Pout = $powerOutKw kW, V = $voltage V", "η = ${res.primaryValue}", res.formula)
                },
                onReset = { powerOutKw = "15"; voltage = defaultV.toString(); current = "28"; powerFactor = "0.86" }
            )
        }
    }
}

// -------------------------------------------------------------
// 5. MOTOR 3-PHASE TO SINGLE-PHASE (STEINMETZ)
// -------------------------------------------------------------
@Composable
fun MotorThreeToSinglePhaseContent(viewModel: ElectricianViewModel, defaultFreq: Double) {
    var powerKw by remember { mutableStateOf("1.5") }
    var voltage by remember { mutableStateOf("230") }
    var frequency by remember { mutableStateOf(defaultFreq.toString()) }
    var isDeltaConnection by remember { mutableStateOf(true) }

    val pD = powerKw.toDoubleOrNull() ?: 1.5
    val vD = voltage.toDoubleOrNull() ?: 230.0
    val fD = frequency.toDoubleOrNull() ?: defaultFreq

    val result = remember(pD, vD, fD, isDeltaConnection) {
        ElectricalFormulas.calculateSteinmetzConnection(pD, vD, fD, isDeltaConnection)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ThreeDChip(
                selected = isDeltaConnection,
                onClick = { isDeltaConnection = true },
                label = "Delta (Δ) — 230V Best Torque",
                modifier = Modifier.weight(1f)
            )
            ThreeDChip(
                selected = !isDeltaConnection,
                onClick = { isDeltaConnection = false },
                label = "Star (Y) Connection",
                modifier = Modifier.weight(1f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = powerKw, onValueChange = { powerKw = it }, label = "3-Phase Motor Power", unitSuffix = "kW", modifier = Modifier.weight(1f))
            NumericInputField(value = voltage, onValueChange = { voltage = it }, label = "1-Phase Voltage", unitSuffix = "V", modifier = Modifier.weight(1f))
        }

        NumericInputField(value = frequency, onValueChange = { frequency = it }, label = "Supply Frequency", unitSuffix = "Hz")

        // Practical Wiring Advice Box
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Slate800,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Steinmetz Circuit Notes:",
                    color = ElectricGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "• Connect L and N to two motor terminals (e.g. U1 and V1).\n• Connect the Run Capacitor between the 3rd terminal (W1) and either L or N (determines rotation direction).\n• Use capacitors rated ≥ 450V AC continuous metallized polypropylene.",
                    color = Slate300,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "3-Phase to Single-Phase",
                inputSummary = "P = $powerKw kW, V = $voltage V, Conn = ${if (isDeltaConnection) "Delta" else "Star"}",
                onSave = { res ->
                    viewModel.saveCalculation("motor_three_to_single_phase", "Steinmetz (${res.primaryValue})", "P = $powerKw kW @ $voltage V", "C_run = ${res.primaryValue}", res.formula)
                },
                onReset = { powerKw = "1.5"; voltage = "230"; frequency = defaultFreq.toString(); isDeltaConnection = true }
            )
        }
    }
}

// -------------------------------------------------------------
// 6. SINGLE-PHASE MOTOR RUN CAPACITOR
// -------------------------------------------------------------
@Composable
fun SinglePhaseMotorRunCapacitorContent(viewModel: ElectricianViewModel, defaultFreq: Double) {
    var powerKw by remember { mutableStateOf("0.75") }
    var voltage by remember { mutableStateOf("230") }
    var frequency by remember { mutableStateOf(defaultFreq.toString()) }
    var powerFactor by remember { mutableStateOf("0.85") }
    var efficiency by remember { mutableStateOf("80") }

    val pD = powerKw.toDoubleOrNull() ?: 0.75
    val vD = voltage.toDoubleOrNull() ?: 230.0
    val fD = frequency.toDoubleOrNull() ?: defaultFreq
    val pfD = powerFactor.toDoubleOrNull() ?: 0.85
    val etaD = efficiency.toDoubleOrNull() ?: 80.0

    val result = remember(pD, vD, fD, pfD, etaD) {
        ElectricalFormulas.calculateSinglePhaseRunCapacitor(pD, vD, fD, etaD, pfD)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = powerKw, onValueChange = { powerKw = it }, label = "Motor Power", unitSuffix = "kW", modifier = Modifier.weight(1f))
            NumericInputField(value = voltage, onValueChange = { voltage = it }, label = "Line Voltage", unitSuffix = "V", modifier = Modifier.weight(1f))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = frequency, onValueChange = { frequency = it }, label = "Frequency", unitSuffix = "Hz", modifier = Modifier.weight(1f))
            NumericInputField(value = powerFactor, onValueChange = { powerFactor = it }, label = "Power Factor", unitSuffix = "cosφ", modifier = Modifier.weight(1f))
        }

        NumericInputField(value = efficiency, onValueChange = { efficiency = it }, label = "Motor Efficiency", unitSuffix = "%")

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "Single-Phase Run Capacitor",
                inputSummary = "P = $powerKw kW, V = $voltage V, f = $frequency Hz",
                onSave = { res ->
                    viewModel.saveCalculation("motor_single_phase_capacitor", "Run Cap (${res.primaryValue})", "P = $powerKw kW @ $voltage V", "C = ${res.primaryValue}", res.formula)
                },
                onReset = { powerKw = "0.75"; voltage = "230"; frequency = defaultFreq.toString(); powerFactor = "0.85"; efficiency = "80" }
            )
        }
    }
}

// -------------------------------------------------------------
// 7. MOTOR SPEED CALCULATOR
// -------------------------------------------------------------
@Composable
fun MotorSpeedContent(viewModel: ElectricianViewModel, defaultFreq: Double) {
    var frequency by remember { mutableStateOf(defaultFreq.toString()) }
    var poles by remember { mutableIntStateOf(4) }
    var slipPct by remember { mutableStateOf("4.0") }

    val fD = frequency.toDoubleOrNull() ?: defaultFreq
    val sD = slipPct.toDoubleOrNull() ?: 4.0

    val result = remember(fD, poles, sD) {
        ElectricalFormulas.calculateMotorSpeed(fD, poles, sD)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = frequency, onValueChange = { frequency = it }, label = "Frequency", unitSuffix = "Hz", modifier = Modifier.weight(1f))
            NumericInputField(value = slipPct, onValueChange = { slipPct = it }, label = "Nominal Slip", unitSuffix = "%", modifier = Modifier.weight(1f))
        }

        Text("Select Stator Poles:", style = MaterialTheme.typography.labelMedium, color = Slate400)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(2, 4, 6, 8).forEach { p ->
                ThreeDChip(
                    selected = poles == p,
                    onClick = { poles = p },
                    label = "$p Poles",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "Motor Speed",
                inputSummary = "f = $frequency Hz, Poles = $poles, Slip = $slipPct%",
                onSave = { res ->
                    viewModel.saveCalculation("motor_speed", "Speed (${res.primaryValue})", "f = $frequency Hz, $poles Poles", "Nr = ${res.primaryValue}", res.formula)
                },
                onReset = { frequency = defaultFreq.toString(); poles = 4; slipPct = "4.0" }
            )
        }
    }
}

// -------------------------------------------------------------
// 8. POWER / MAXIMUM TORQUE
// -------------------------------------------------------------
@Composable
fun MotorMaxTorqueContent(viewModel: ElectricianViewModel) {
    var powerKw by remember { mutableStateOf("11") }
    var rpm by remember { mutableStateOf("1450") }
    var breakdownRatio by remember { mutableStateOf("2.6") }

    val pD = powerKw.toDoubleOrNull() ?: 11.0
    val rpmD = rpm.toDoubleOrNull() ?: 1450.0
    val rD = breakdownRatio.toDoubleOrNull() ?: 2.6

    val result = remember(pD, rpmD, rD) {
        ElectricalFormulas.calculateMotorMaxTorque(pD, rpmD, rD)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = powerKw, onValueChange = { powerKw = it }, label = "Shaft Power", unitSuffix = "kW", modifier = Modifier.weight(1f))
            NumericInputField(value = rpm, onValueChange = { rpm = it }, label = "Rated Speed", unitSuffix = "RPM", modifier = Modifier.weight(1f))
        }

        NumericInputField(value = breakdownRatio, onValueChange = { breakdownRatio = it }, label = "Breakdown Torque Ratio (T_max / T_fl)", unitSuffix = "x")

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "Power / Maximum Torque",
                inputSummary = "P = $powerKw kW, Speed = $rpm RPM, Ratio = ${breakdownRatio}x",
                onSave = { res ->
                    viewModel.saveCalculation("motor_torque", "Torque (${res.primaryValue})", "P = $powerKw kW @ $rpm RPM", "T_fl = ${res.primaryValue}", res.formula)
                },
                onReset = { powerKw = "11"; rpm = "1450"; breakdownRatio = "2.6" }
            )
        }
    }
}

// -------------------------------------------------------------
// 9. MOTOR FULL LOAD CURRENT (NEC TABLES)
// -------------------------------------------------------------
@Composable
fun MotorNecFlcContent(viewModel: ElectricianViewModel) {
    var selectedHp by remember { mutableDoubleStateOf(5.0) }
    var selectedVoltage by remember { mutableDoubleStateOf(460.0) }
    var isThreePhase by remember { mutableStateOf(true) }

    val hpOptions3Phase = listOf(0.5, 1.0, 2.0, 3.0, 5.0, 7.5, 10.0, 15.0, 20.0, 25.0, 30.0, 40.0, 50.0, 75.0, 100.0)
    val hpOptions1Phase = listOf(0.25, 0.33, 0.5, 0.75, 1.0, 1.5, 2.0, 3.0, 5.0, 7.5, 10.0)
    val voltageOptions3Phase = listOf(208.0, 230.0, 460.0, 575.0)
    val voltageOptions1Phase = listOf(115.0, 208.0, 230.0)

    val currentVoltages = if (isThreePhase) voltageOptions3Phase else voltageOptions1Phase
    if (!currentVoltages.contains(selectedVoltage)) {
        selectedVoltage = currentVoltages.first()
    }

    val result = remember(selectedHp, selectedVoltage, isThreePhase) {
        ElectricalFormulas.calculateMotorFLC_NEC(selectedHp, selectedVoltage, isThreePhase)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ThreeDChip(selected = isThreePhase, onClick = { isThreePhase = true }, label = "3-Phase (NEC 430.250)", modifier = Modifier.weight(1f))
            ThreeDChip(selected = !isThreePhase, onClick = { isThreePhase = false }, label = "1-Phase (NEC 430.248)", modifier = Modifier.weight(1f))
        }

        Text("Select Motor Horsepower (HP):", style = MaterialTheme.typography.labelMedium, color = Slate400)
        val activeHpList = if (isThreePhase) hpOptions3Phase else hpOptions1Phase
        androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(activeHpList.size) { idx ->
                val hp = activeHpList[idx]
                ThreeDChip(
                    selected = selectedHp == hp,
                    onClick = { selectedHp = hp },
                    label = if (hp < 1.0) "$hp HP" else "${hp.toInt()} HP"
                )
            }
        }

        Text("Select System Voltage:", style = MaterialTheme.typography.labelMedium, color = Slate400)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            currentVoltages.forEach { v ->
                ThreeDChip(
                    selected = selectedVoltage == v,
                    onClick = { selectedVoltage = v },
                    label = "${v.toInt()} V",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "Motor Full-Load Current (NEC)",
                inputSummary = "$selectedHp HP @ ${selectedVoltage.toInt()}V ${if (isThreePhase) "3-Phase" else "1-Phase"}",
                onSave = { res ->
                    viewModel.saveCalculation("motor_flc_nec", "NEC FLC (${res.primaryValue})", "$selectedHp HP @ ${selectedVoltage.toInt()}V", "FLC = ${res.primaryValue}", res.formula)
                },
                onReset = { selectedHp = 5.0; selectedVoltage = 460.0; isThreePhase = true }
            )
        }
    }
}

// -------------------------------------------------------------
// 10. 3-PHASE MOTOR WIRING DIAGRAM (6 TERMINALS)
// -------------------------------------------------------------
@Composable
fun MotorDiagram6TerminalsContent() {
    var selectedConnection by remember { mutableStateOf("star") } // "star" or "delta"

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ThreeDChip(
                selected = selectedConnection == "star",
                onClick = { selectedConnection = "star" },
                label = "Star (Y) Connection — High Voltage",
                modifier = Modifier.weight(1f)
            )
            ThreeDChip(
                selected = selectedConnection == "delta",
                onClick = { selectedConnection = "delta" },
                label = "Delta (Δ) Connection — Low Voltage",
                modifier = Modifier.weight(1f)
            )
        }

        // Terminal Block Graphic Representation
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Slate900,
            border = BorderStroke(1.5.dp, if (selectedConnection == "star") BlueprintBlue else ElectricGold),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (selectedConnection == "star") "STAR (Y) CONNECTION (e.g. 400V)" else "DELTA (Δ) CONNECTION (e.g. 230V)",
                    color = if (selectedConnection == "star") Color(0xFF60A5FA) else ElectricGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(14.dp))

                // Terminal Grid (Top row: W2, U2, V2; Bottom row: U1, V1, W1)
                Column(
                    modifier = Modifier
                        .background(Slate800, RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    // Top Row: W2, U2, V2
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TerminalPeg(label = "W2", note = if (selectedConnection == "star") "Short" else "L1 jumper")
                        TerminalPeg(label = "U2", note = if (selectedConnection == "star") "Short" else "L2 jumper")
                        TerminalPeg(label = "V2", note = if (selectedConnection == "star") "Short" else "L3 jumper")
                    }

                    // Links / Jumper Bars
                    if (selectedConnection == "star") {
                        // Horizontal jumper bridging W2 - U2 - V2
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .height(8.dp),
                            color = ElectricAmber,
                            shape = RoundedCornerShape(4.dp)
                        ) {}
                        Text("Horizontal Link Plates (W2-U2-V2)", fontSize = 11.sp, color = ElectricAmber, fontWeight = FontWeight.Bold)
                    } else {
                        // Vertical jumpers bridging W2-U1, U2-V1, V2-W1
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Surface(modifier = Modifier.width(10.dp).height(24.dp), color = ElectricAmber, shape = RoundedCornerShape(3.dp)) {}
                            Surface(modifier = Modifier.width(10.dp).height(24.dp), color = ElectricAmber, shape = RoundedCornerShape(3.dp)) {}
                            Surface(modifier = Modifier.width(10.dp).height(24.dp), color = ElectricAmber, shape = RoundedCornerShape(3.dp)) {}
                        }
                        Text("Vertical Link Plates (W2-U1, U2-V1, V2-W1)", fontSize = 11.sp, color = ElectricAmber, fontWeight = FontWeight.Bold)
                    }

                    // Bottom Row: U1, V1, W1
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TerminalPeg(label = "U1", note = "Line L1")
                        TerminalPeg(label = "V1", note = "Line L2")
                        TerminalPeg(label = "W1", note = "Line L3")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Explanatory breakdown
                Text(
                    text = if (selectedConnection == "star") {
                        "• Connect incoming 3-phase supply lines: L1 to U1, L2 to V1, L3 to W1.\n• Insert horizontal brass link plates across W2, U2, and V2 to form the neutral star point.\n• Used for higher rated line voltage (e.g. 400V on a 230/400V motor).\n• Starting torque and current are 1/3 of Delta."
                    } else {
                        "• Connect incoming 3-phase supply lines: L1 to U1, L2 to V1, L3 to W1.\n• Insert 3 vertical brass link plates connecting W2-U1, U2-V1, and V2-W1.\n• Used for lower rated line voltage (e.g. 230V on a 230/400V motor) or full-speed Delta running."
                    },
                    color = Slate300,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 11. 3-PHASE MOTOR WIRING DIAGRAM (9 TERMINALS)
// -------------------------------------------------------------
@Composable
fun MotorDiagram9TerminalsContent() {
    var isWyeMotor by remember { mutableStateOf(true) } // Wye vs Delta 9-lead
    var isLowVoltage by remember { mutableStateOf(true) } // Low (230V) vs High (460V)

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ThreeDChip(selected = isWyeMotor, onClick = { isWyeMotor = true }, label = "Wye (Y) 9-Lead Motor", modifier = Modifier.weight(1f))
            ThreeDChip(selected = !isWyeMotor, onClick = { isWyeMotor = false }, label = "Delta (Δ) 9-Lead Motor", modifier = Modifier.weight(1f))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ThreeDChip(selected = isLowVoltage, onClick = { isLowVoltage = true }, label = "Low Voltage (208V / 230V)", modifier = Modifier.weight(1f))
            ThreeDChip(selected = !isLowVoltage, onClick = { isLowVoltage = false }, label = "High Voltage (460V / 480V)", modifier = Modifier.weight(1f))
        }

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Slate900,
            border = BorderStroke(1.5.dp, ElectricCyan),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "${if (isWyeMotor) "WYE" else "DELTA"} 9-LEAD — ${if (isLowVoltage) "LOW VOLTAGE (230V PARALLEL)" else "HIGH VOLTAGE (460V SERIES)"}",
                    color = ElectricGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (isWyeMotor) {
                    if (isLowVoltage) {
                        WiringGuideBox(
                            lineConnections = listOf("Line 1 (L1) -> T1 & T7", "Line 2 (L2) -> T2 & T8", "Line 3 (L3) -> T3 & T9"),
                            tiePoints = listOf("Tie together & insulate: T4, T5, T6 (Internal Star Point)"),
                            note = "NEMA Low-Voltage Parallel Wye connection. Each winding receives full phase voltage."
                        )
                    } else {
                        WiringGuideBox(
                            lineConnections = listOf("Line 1 (L1) -> T1", "Line 2 (L2) -> T2", "Line 3 (L3) -> T3"),
                            tiePoints = listOf("Tie together & insulate: T4 & T7", "Tie together & insulate: T5 & T8", "Tie together & insulate: T6 & T9"),
                            note = "NEMA High-Voltage Series Wye connection. T7, T8, T9 internal neutral star remains untouched."
                        )
                    }
                } else {
                    if (isLowVoltage) {
                        WiringGuideBox(
                            lineConnections = listOf("Line 1 (L1) -> T1, T6, T7", "Line 2 (L2) -> T2, T4, T8", "Line 3 (L3) -> T3, T5, T9"),
                            tiePoints = listOf("No isolated tie points. All leads connected to supply lines in parallel."),
                            note = "NEMA Low-Voltage Parallel Delta connection."
                        )
                    } else {
                        WiringGuideBox(
                            lineConnections = listOf("Line 1 (L1) -> T1", "Line 2 (L2) -> T2", "Line 3 (L3) -> T3"),
                            tiePoints = listOf("Tie together & insulate: T4 & T7", "Tie together & insulate: T5 & T8", "Tie together & insulate: T6 & T9"),
                            note = "NEMA High-Voltage Series Delta connection."
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 12. 3-PHASE MOTOR WIRING DIAGRAM (12 TERMINALS)
// -------------------------------------------------------------
@Composable
fun MotorDiagram12TerminalsContent() {
    var selectedMode by remember { mutableIntStateOf(0) } // 0: Low Wye, 1: High Wye, 2: Low Delta, 3: High Delta

    val modes = listOf("Low Wye (230V)", "High Wye (460V)", "Low Delta (230V)", "High Delta (460V)")

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(modes.size) { idx ->
                ThreeDChip(
                    selected = selectedMode == idx,
                    onClick = { selectedMode = idx },
                    label = modes[idx]
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Slate900,
            border = BorderStroke(1.5.dp, ElectricGold),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "12-LEAD TERMINAL CONFIGURATION: ${modes[selectedMode].uppercase()}",
                    color = ElectricGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                when (selectedMode) {
                    0 -> WiringGuideBox(
                        lineConnections = listOf("L1 -> T1, T7", "L2 -> T2, T8", "L3 -> T3, T9"),
                        tiePoints = listOf("Tie & insulate: T4, T5, T6", "Tie & insulate: T10, T11, T12"),
                        note = "Low Voltage Parallel Wye (208V - 230V). Two independent star points."
                    )
                    1 -> WiringGuideBox(
                        lineConnections = listOf("L1 -> T1", "L2 -> T2", "L3 -> T3"),
                        tiePoints = listOf("Tie & insulate: T4 & T7", "Tie & insulate: T5 & T8", "Tie & insulate: T6 & T9", "Tie & insulate: T10, T11, T12 (Star point)"),
                        note = "High Voltage Series Wye (460V - 480V)."
                    )
                    2 -> WiringGuideBox(
                        lineConnections = listOf("L1 -> T1, T6, T7, T12", "L2 -> T2, T4, T8, T10", "L3 -> T3, T5, T9, T11"),
                        tiePoints = listOf("No isolated tie points. All 12 leads connect to the 3 incoming lines."),
                        note = "Low Voltage Parallel Delta (230V). Delivers maximum starting torque."
                    )
                    3 -> WiringGuideBox(
                        lineConnections = listOf("L1 -> T1, T12", "L2 -> T2, T10", "L3 -> T3, T11"),
                        tiePoints = listOf("Tie & insulate: T4 & T7", "Tie & insulate: T5 & T8", "Tie & insulate: T6 & T9"),
                        note = "High Voltage Series Delta (460V). Also used in Star-Delta starters on 230V supplies."
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 13. MOTOR CONNECTIONS (THEORY & REVERSING)
// -------------------------------------------------------------
@Composable
fun MotorConnectionsContent() {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Slate900,
            border = BorderStroke(1.dp, Slate700),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("STAR (WYE) vs DELTA (Δ) SUMMARY", color = ElectricGold, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(Slate800, RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Text("STAR (Y)", color = Color(0xFF60A5FA), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("• V_phase = V_line / √3\n• I_line = I_phase\n• Torque = 1/3 of Delta\n• Inrush = 1/3 of Delta\n• Ideal for starting large loads", fontSize = 11.sp, color = Slate300, lineHeight = 16.sp)
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(Slate800, RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Text("DELTA (Δ)", color = ElectricAmber, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("• V_phase = V_line\n• I_line = √3 × I_phase\n• Full rated torque\n• Full power capacity\n• Standard continuous running", fontSize = 11.sp, color = Slate300, lineHeight = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Slate800)
                Spacer(modifier = Modifier.height(12.dp))

                Text("HOW TO REVERSE MOTOR ROTATION", color = ElectricCyan, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• 3-Phase Induction Motors: Swap ANY TWO incoming supply lines (e.g. interchange Line 1 and Line 2). This reverses the direction of the rotating magnetic stator field.\n\n• Single-Phase Capacitor Motors: Interchange the connections of the Auxiliary / Start winding (leads Z1 and Z2 or T5 and T8) with respect to the main winding.",
                    color = Slate300,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 14. MOTOR TERMINALS MARKING (IEC vs NEMA)
// -------------------------------------------------------------
@Composable
fun MotorTerminalsMarkingContent() {
    val mappings = listOf(
        Triple("Phase 1 Start", "U1", "T1"),
        Triple("Phase 1 Finish", "U2", "T4"),
        Triple("Phase 2 Start", "V1", "T2"),
        Triple("Phase 2 Finish", "V2", "T5"),
        Triple("Phase 3 Start", "W1", "T3"),
        Triple("Phase 3 Finish", "W2", "T6"),
        Triple("Phase 1 Coil 2 Start", "U5", "T7"),
        Triple("Phase 1 Coil 2 Finish", "U6", "T10"),
        Triple("Phase 2 Coil 2 Start", "V5", "T8"),
        Triple("Phase 2 Coil 2 Finish", "V6", "T11"),
        Triple("Phase 3 Coil 2 Start", "W5", "T9"),
        Triple("Phase 3 Coil 2 Finish", "W6", "T12"),
        Triple("Auxiliary Start Winding", "Z1 / Z2", "T5 / T8 (1-Phase)"),
        Triple("Thermistor / PTC Sensor", "TP1 / TP2", "P1 / P2"),
        Triple("Anticondensation Heater", "HE1 / HE2", "H1 / H2")
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Slate900,
            border = BorderStroke(1.dp, Slate800),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("IEC 60034-8 vs NEMA MG-1 TERMINAL EQUIVALENCE", color = ElectricGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(10.dp))

                // Table Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Slate800, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Text("Function / Lead", color = Slate400, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1.4f))
                    Text("IEC (European)", color = ElectricCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1f))
                    Text("NEMA (USA)", color = ElectricAmber, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(6.dp))

                mappings.forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (index % 2 == 0) Color.Transparent else Slate800.copy(alpha = 0.4f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(item.first, color = Slate200, fontSize = 11.sp, modifier = Modifier.weight(1.4f))
                        Text(item.second, color = ElectricCyan, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, modifier = Modifier.weight(1f))
                        Text(item.third, color = ElectricAmber, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 15. INSULATION CLASS OF THE MOTOR
// -------------------------------------------------------------
@Composable
fun MotorInsulationClassContent() {
    val classes = listOf(
        MotorInsulationData("Class A", 105, 60, 5, 40, "Fractional HP, domestic appliances"),
        MotorInsulationData("Class B", 130, 80, 10, 40, "Standard legacy industrial motors"),
        MotorInsulationData("Class F", 155, 105, 10, 40, "Modern standard industrial motors (Most common)"),
        MotorInsulationData("Class H", 180, 125, 15, 40, "Severe duty, high ambient, crane & traction")
    )

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        classes.forEach { item ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Slate900,
                border = BorderStroke(1.dp, if (item.name == "Class F") ElectricCyan else Slate800),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(item.name, color = if (item.name == "Class F") ElectricCyan else ElectricGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Slate800,
                            modifier = Modifier.padding(2.dp)
                        ) {
                            Text("Max Temp: ${item.maxTemp}°C", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Max Ambient: ${item.ambient}°C", color = Slate400, fontSize = 11.sp)
                        Text("Allowable Rise: ${item.rise}°C", color = Slate400, fontSize = 11.sp)
                        Text("Hot Spot Margin: ${item.hotspot}°C", color = Slate400, fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Application: ${item.application}", color = Slate300, fontSize = 11.sp)
                }
            }
        }

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Slate800,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Insulation Rule of Thumb (Arrhenius Law):", color = ElectricAmber, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Operating a motor 10°C below its insulation thermal rating DOUBLES its winding insulation lifespan. Conversely, operating continuously 10°C above halves its insulation life.",
                    color = Slate300,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

private data class MotorInsulationData(
    val name: String,
    val maxTemp: Int,
    val rise: Int,
    val hotspot: Int,
    val ambient: Int,
    val application: String
)

// -------------------------------------------------------------
// 16. MOTOR POWER FACTOR CORRECTION (INDUCTIVE LOADS)
// -------------------------------------------------------------
@Composable
fun MotorPFCorrectionContent(viewModel: ElectricianViewModel, defaultV: Double, defaultFreq: Double) {
    var powerInput by remember { mutableStateOf("15") }
    var powerUnit by remember { mutableStateOf("kW") } // "kW" or "HP"
    var voltage by remember { mutableStateOf(defaultV.toString()) }
    var frequency by remember { mutableStateOf(defaultFreq.toString()) }
    var initialPf by remember { mutableStateOf("0.80") }
    var targetPf by remember { mutableStateOf("0.95") }
    var efficiency by remember { mutableStateOf("91") }
    var isThreePhase by remember { mutableStateOf(true) }
    var detunedReactorPct by remember { mutableStateOf("0") } // "0" (None), "7" (7%), "14" (14%)

    val rawPower = powerInput.toDoubleOrNull() ?: 15.0
    val pKw = if (powerUnit == "HP") rawPower * 0.7457 else rawPower
    val vD = voltage.toDoubleOrNull() ?: defaultV
    val fD = frequency.toDoubleOrNull() ?: defaultFreq
    val pf1D = (initialPf.toDoubleOrNull() ?: 0.80).coerceIn(0.25, 0.98)
    val pf2D = (targetPf.toDoubleOrNull() ?: 0.95).coerceIn(pf1D + 0.01, 1.0)
    val etaD = (efficiency.toDoubleOrNull() ?: 91.0).coerceIn(50.0, 99.0)
    val reactorPct = detunedReactorPct.toDoubleOrNull() ?: 0.0

    val result = remember(pKw, vD, fD, pf1D, pf2D, etaD, isThreePhase) {
        ElectricalFormulas.calculateMotorPFCorrection(pKw, vD, fD, pf1D, pf2D, etaD, isThreePhase)
    }

    // Secondary engineering computations
    val kPhase = if (isThreePhase) kotlin.math.sqrt(3.0) else 1.0
    val pInKw = pKw / (etaD / 100.0)
    val i1 = (pInKw * 1000.0) / (kPhase * vD * pf1D)
    val i2 = (pInKw * 1000.0) / (kPhase * vD * pf2D)
    val relayDialSetting = i1 * (pf1D / pf2D)
    val noLoadAmpsEst = i1 * 0.30
    val maxSafeQc = 0.90 * (kPhase * vD * noLoadAmpsEst) / 1000.0
    val pDetuning = reactorPct / 100.0
    val requiredCapacitorVoltage = if (pDetuning > 0) vD / (1.0 - pDetuning) else vD

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Phase Mode & Power Unit Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ThreeDChip(
                selected = isThreePhase,
                onClick = { isThreePhase = true },
                label = "3-Phase Induction",
                modifier = Modifier.weight(1f)
            )
            ThreeDChip(
                selected = !isThreePhase,
                onClick = { isThreePhase = false },
                label = "1-Phase Inductive",
                modifier = Modifier.weight(1f)
            )
        }

        // Power Unit Selection
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ThreeDChip(
                selected = powerUnit == "kW",
                onClick = {
                    if (powerUnit != "kW") {
                        val hp = powerInput.toDoubleOrNull() ?: 20.0
                        powerInput = String.format("%.1f", hp * 0.7457)
                        powerUnit = "kW"
                    }
                },
                label = "Metric Power (kW)",
                modifier = Modifier.weight(1f)
            )
            ThreeDChip(
                selected = powerUnit == "HP",
                onClick = {
                    if (powerUnit != "HP") {
                        val kw = powerInput.toDoubleOrNull() ?: 15.0
                        powerInput = String.format("%.1f", kw / 0.7457)
                        powerUnit = "HP"
                    }
                },
                label = "Horsepower (HP)",
                modifier = Modifier.weight(1f)
            )
        }

        // Standard motor rating quick selection chips
        Text(
            text = "Standard Motor Sizes (${powerUnit}):",
            style = MaterialTheme.typography.labelMedium,
            color = Slate400
        )
        androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            val ratings = if (powerUnit == "kW") {
                listOf("4", "5.5", "7.5", "11", "15", "18.5", "22", "30", "37", "45", "55", "75", "90", "110", "132")
            } else {
                listOf("5", "7.5", "10", "15", "20", "25", "30", "40", "50", "60", "75", "100", "125", "150", "200")
            }
            items(ratings.size) { idx ->
                val rating = ratings[idx]
                ThreeDChip(
                    selected = powerInput == rating,
                    onClick = { powerInput = rating },
                    label = "$rating $powerUnit"
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(
                value = powerInput,
                onValueChange = { powerInput = it },
                label = "Motor Shaft Rating",
                unitSuffix = powerUnit,
                modifier = Modifier.weight(1f)
            )
            NumericInputField(
                value = voltage,
                onValueChange = { voltage = it },
                label = "Line Voltage",
                unitSuffix = "V",
                modifier = Modifier.weight(1f)
            )
        }

        // Typical Load & Power Factor Selector
        Text(
            text = "Operating Load Level (cosφ₁ Preset):",
            style = MaterialTheme.typography.labelMedium,
            color = Slate400
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val loads = listOf(
                "100% Load" to "0.84",
                "75% Load" to "0.78",
                "50% Load" to "0.68"
            )
            loads.forEach { (label, pfVal) ->
                ThreeDChip(
                    selected = initialPf == pfVal,
                    onClick = { initialPf = pfVal },
                    label = label,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(
                value = initialPf,
                onValueChange = { initialPf = it },
                label = "Initial Motor PF (cosφ₁)",
                unitSuffix = "cosφ",
                modifier = Modifier.weight(1f)
            )
            NumericInputField(
                value = targetPf,
                onValueChange = { targetPf = it },
                label = "Target Desired PF (cosφ₂)",
                unitSuffix = "cosφ",
                modifier = Modifier.weight(1f)
            )
        }

        // Target PF Quick Selectors
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val targets = listOf("0.92", "0.95", "0.98", "1.00")
            targets.forEach { tVal ->
                ThreeDChip(
                    selected = targetPf == tVal,
                    onClick = { targetPf = tVal },
                    label = "Target $tVal",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(
                value = efficiency,
                onValueChange = { efficiency = it },
                label = "Motor Efficiency (η)",
                unitSuffix = "%",
                modifier = Modifier.weight(1f)
            )
            NumericInputField(
                value = frequency,
                onValueChange = { frequency = it },
                label = "Grid Frequency",
                unitSuffix = "Hz",
                modifier = Modifier.weight(1f)
            )
        }

        // Harmonic Detuned Filter Reactor Selection
        Text(
            text = "Harmonic Detuning Reactor (Series Anti-Resonance):",
            style = MaterialTheme.typography.labelMedium,
            color = Slate400
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val reactors = listOf(
                "0" to "Standard (0%)",
                "7" to "7% (189 Hz)",
                "14" to "14% (134 Hz)"
            )
            reactors.forEach { (pct, lbl) ->
                ThreeDChip(
                    selected = detunedReactorPct == pct,
                    onClick = { detunedReactorPct = pct },
                    label = lbl,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Motor Engineering & Overload Relay Adjustment Card
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Slate900,
            border = BorderStroke(1.dp, Slate800),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Engineering Notes",
                        tint = ElectricCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Motor Engineering Sizing & Safety Rules",
                        color = ElectricCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Text(
                    text = "• Thermal Overload Relay Setting: If capacitors are wired downstream of the overload relay (between starter and motor), the relay must be turned down from ${String.format("%.1f", i1)} A to ${String.format("%.1f", relayDialSetting)} A (ratio cosφ₁/cosφ₂). If upstream of the relay, leave at motor nameplate FLC.",
                    color = Color.White,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )

                Text(
                    text = "• Self-Excitation Limit: Maximum safe terminal capacitor bank is ${String.format("%.1f", maxSafeQc)} kVAR (90% of motor no-load kVAR per IEEE 141). Exceeding this risks extreme transient overvoltages during coast-down when the motor acts as a self-excited induction generator.",
                    color = if (result.primaryValue.replace(" kVAR", "").toDoubleOrNull() ?: 0.0 > maxSafeQc) ElectricAmber else Slate300,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    fontWeight = if (result.primaryValue.replace(" kVAR", "").toDoubleOrNull() ?: 0.0 > maxSafeQc) FontWeight.SemiBold else FontWeight.Normal
                )

                if (pDetuning > 0) {
                    Text(
                        text = "• Detuned Reactor Voltage Rise: Series reactor (p = $detunedReactorPct%) raises fundamental voltage across capacitor terminals to ${String.format("%.0f", requiredCapacitorVoltage)} V! Standard 400V capacitors will fail; specify capacitors rated for minimum ${if (pDetuning >= 0.14) "525V" else "480V"}.",
                        color = ElectricAmber,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }

                Text(
                    text = "• VFD Proscription: NEVER connect power factor correction capacitors directly between a Variable Frequency Drive (VFD) or electronic soft-starter and the motor!",
                    color = Color(0xFFEF5350),
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "Motor Power Factor Correction",
                inputSummary = "Motor: $powerInput $powerUnit (${String.format("%.1f", pKw)} kW) @ $voltage V | PF: $initialPf → $targetPf",
                onSave = { res ->
                    viewModel.saveCalculation(
                        "motor_pf_correction",
                        "Motor PFC (${res.primaryValue})",
                        "$powerInput $powerUnit (${String.format("%.1f", pKw)} kW) @ $voltage V (PF $initialPf → $targetPf)",
                        "Qc = ${res.primaryValue}",
                        res.formula
                    )
                },
                onReset = {
                    powerInput = "15"
                    powerUnit = "kW"
                    voltage = defaultV.toString()
                    initialPf = "0.80"
                    targetPf = "0.95"
                    efficiency = "91"
                    detunedReactorPct = "0"
                }
            )
        }
    }
}

// Helper Composables for Wiring diagrams
@Composable
private fun TerminalPeg(label: String, note: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            shape = CircleShape,
            color = ElectricGold,
            border = BorderStroke(2.dp, Color.White),
            modifier = Modifier.size(34.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(label, color = Slate950, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(note, color = Slate400, fontSize = 10.sp)
    }
}

@Composable
private fun WiringGuideBox(
    lineConnections: List<String>,
    tiePoints: List<String>,
    note: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Power Line Connections:", color = ElectricCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        lineConnections.forEach { conn ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("• ", color = ElectricCyan, fontSize = 12.sp)
                Text(conn, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text("Internal Tie Points:", color = ElectricAmber, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        tiePoints.forEach { tie ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("• ", color = ElectricAmber, fontSize = 12.sp)
                Text(tie, color = Slate200, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(note, color = Slate400, fontSize = 11.sp, lineHeight = 16.sp)
    }
}
