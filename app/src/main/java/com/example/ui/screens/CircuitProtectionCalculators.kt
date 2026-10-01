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
fun CableShortCircuitAdiabaticContent(viewModel: ElectricianViewModel) {
    var faultCurrentKa by remember { mutableStateOf("6.0") }
    var faultDurationSec by remember { mutableStateOf("0.1") }
    var cableSize by remember { mutableStateOf("16") }
    var isCopper by remember { mutableStateOf(true) }
    var isXlpe by remember { mutableStateOf(true) }

    val ifKa = faultCurrentKa.toDoubleOrNull() ?: 6.0
    val tSec = faultDurationSec.toDoubleOrNull() ?: 0.1
    val sMm2 = cableSize.toDoubleOrNull() ?: 16.0

    val result = remember(ifKa, tSec, sMm2, isCopper, isXlpe) {
        AdvancedElectricalEngine.calculateCableShortCircuitAdiabatic(ifKa, tSec, sMm2, isCopper, isXlpe)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ThreeDChip(selected = isCopper, onClick = { isCopper = true }, label = "Copper Conductor", modifier = Modifier.weight(1f))
            ThreeDChip(selected = !isCopper, onClick = { isCopper = false }, label = "Aluminium Conductor", modifier = Modifier.weight(1f))
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ThreeDChip(selected = isXlpe, onClick = { isXlpe = true }, label = "XLPE / EPR (k = ${if (isCopper) 143 else 94})", modifier = Modifier.weight(1f))
            ThreeDChip(selected = !isXlpe, onClick = { isXlpe = false }, label = "PVC (k = ${if (isCopper) 115 else 76})", modifier = Modifier.weight(1f))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = faultCurrentKa, onValueChange = { faultCurrentKa = it }, label = "Fault Current (Isc)", unitSuffix = "kA", modifier = Modifier.weight(1f))
            NumericInputField(value = faultDurationSec, onValueChange = { faultDurationSec = it }, label = "Trip Time (t)", unitSuffix = "s", modifier = Modifier.weight(1f))
        }

        NumericInputField(value = cableSize, onValueChange = { cableSize = it }, label = "Cable Cross-Section", unitSuffix = "mm²")

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "Cable Protection from Short-Circuit (k²S²)",
                inputSummary = "Isc = $faultCurrentKa kA, t = $faultDurationSec s, S = $cableSize mm²",
                onSave = { res ->
                    viewModel.saveCalculation("cable_short_circuit", "k²S² (${res.primaryValue})", "Isc = $faultCurrentKa kA, S = $cableSize mm²", "I²t vs k²S² = ${res.primaryValue}", res.formula)
                },
                onReset = { faultCurrentKa = "6.0"; faultDurationSec = "0.1"; cableSize = "16"; isCopper = true; isXlpe = true }
            )
        }
    }
}

@Composable
fun BreakerCableCoordinationContent(viewModel: ElectricianViewModel) {
    var designCurrentIb by remember { mutableStateOf("28") }
    var breakerRatingIn by remember { mutableStateOf("32") }
    var cableAmpacityIz by remember { mutableStateOf("38") }

    val ibD = designCurrentIb.toDoubleOrNull() ?: 28.0
    val inD = breakerRatingIn.toDoubleOrNull() ?: 32.0
    val izD = cableAmpacityIz.toDoubleOrNull() ?: 38.0

    val result = remember(ibD, inD, izD) {
        AdvancedElectricalEngine.calculateBreakerCableCoordination(ibD, inD, izD)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = designCurrentIb, onValueChange = { designCurrentIb = it }, label = "Design Load (Ib)", unitSuffix = "A", modifier = Modifier.weight(1f))
            NumericInputField(value = breakerRatingIn, onValueChange = { breakerRatingIn = it }, label = "Breaker Rating (In)", unitSuffix = "A", modifier = Modifier.weight(1f))
        }

        NumericInputField(value = cableAmpacityIz, onValueChange = { cableAmpacityIz = it }, label = "Derated Cable Ampacity (Iz)", unitSuffix = "A")

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "Conductor & Protective Device Coordination",
                inputSummary = "Ib = $designCurrentIb A, In = $breakerRatingIn A, Iz = $cableAmpacityIz A",
                onSave = { res ->
                    viewModel.saveCalculation("coordination_breaker_cable", "Coordination (${res.primaryValue})", "Ib=$designCurrentIb, In=$breakerRatingIn, Iz=$cableAmpacityIz", "Coordination: ${res.primaryValue}", res.formula)
                },
                onReset = { designCurrentIb = "28"; breakerRatingIn = "32"; cableAmpacityIz = "38" }
            )
        }
    }
}

@Composable
fun EarthingRcdContent(viewModel: ElectricianViewModel) {
    var earthResistanceRa by remember { mutableStateOf("25") }
    var rcdRatingMa by remember { mutableDoubleStateOf(30.0) }
    var isWetEnvironment by remember { mutableStateOf(false) }

    val raD = earthResistanceRa.toDoubleOrNull() ?: 25.0
    val maxTouchV = if (isWetEnvironment) 25.0 else 50.0

    val result = remember(raD, rcdRatingMa, maxTouchV) {
        AdvancedElectricalEngine.calculateEarthingRCD(raD, rcdRatingMa, maxTouchV)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Select RCD Sensitivity (IΔn):", style = MaterialTheme.typography.labelMedium)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(10.0, 30.0, 100.0, 300.0, 500.0).forEach { rating ->
                ThreeDChip(
                    selected = rcdRatingMa == rating,
                    onClick = { rcdRatingMa = rating },
                    label = "${rating.toInt()} mA",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ThreeDChip(selected = !isWetEnvironment, onClick = { isWetEnvironment = false }, label = "Normal Dry (UL = 50V)", modifier = Modifier.weight(1f))
            ThreeDChip(selected = isWetEnvironment, onClick = { isWetEnvironment = true }, label = "Wet / Agricultural (UL = 25V)", modifier = Modifier.weight(1f))
        }

        NumericInputField(value = earthResistanceRa, onValueChange = { earthResistanceRa = it }, label = "Earth Electrode Resistance (Ra)", unitSuffix = "Ω")

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "Earthing System and RCD Coordination",
                inputSummary = "Ra = $earthResistanceRa Ω, IΔn = ${rcdRatingMa.toInt()} mA, UL = ${maxTouchV.toInt()}V",
                onSave = { res ->
                    viewModel.saveCalculation("earthing_rcd", "Touch Voltage (${res.primaryValue})", "Ra = $earthResistanceRa Ω, IΔn = ${rcdRatingMa.toInt()} mA", "U_touch = ${res.primaryValue}", res.formula)
                },
                onReset = { earthResistanceRa = "25"; rcdRatingMa = 30.0; isWetEnvironment = false }
            )
        }
    }
}
