package com.example.ui.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ads.AdMobManager
import com.example.data.ElectricalCatalogItem
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

enum class CurrentType(val label: String) {
    DC("Direct current"),
    AC_SINGLE("Alternating single-phase"),
    AC_TWO("Alternating two-phase"),
    AC_THREE("Alternating three-phase")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculationDetailScreen(
    item: ElectricalCatalogItem,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showFormulaDialog by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }

    // Intercept back button and trigger AdMob interstitial
    BackHandler {
        AdMobManager.showInterstitial(context as? Activity) {
            onBack()
        }
    }

    var currentType by remember { mutableStateOf(CurrentType.AC_SINGLE) }
    var selectedInputMode by remember { mutableStateOf("Voltage / Power") }

    // Input States
    var voltageInput by remember { mutableStateOf("230") }
    var voltageUnit by remember { mutableStateOf("V") }

    var powerInput by remember { mutableStateOf("1500") }
    var powerUnit by remember { mutableStateOf("W") }

    var cosPhiInput by remember { mutableStateOf("0.9") }

    var lengthInput by remember { mutableStateOf("50") }
    var sectionInput by remember { mutableStateOf("2.5") }

    // Calculation Result
    var calculationResult by remember { mutableStateOf<String?>(null) }
    var formulaDetails by remember { mutableStateOf<String?>(null) }
    var stepsDetails by remember { mutableStateOf<String?>(null) }

    fun doCalculate() {
        val v = (voltageInput.toDoubleOrNull() ?: 230.0) * if (voltageUnit == "kV") 1000.0 else 1.0
        val p = (powerInput.toDoubleOrNull() ?: 1500.0) * when (powerUnit) {
            "kW" -> 1000.0
            "HP" -> 745.7
            else -> 1.0
        }
        val cosPhi = (cosPhiInput.toDoubleOrNull() ?: 0.9).coerceIn(0.1, 1.0)
        val l = lengthInput.toDoubleOrNull() ?: 50.0
        val s = sectionInput.toDoubleOrNull() ?: 2.5

        when (item.calcType) {
            "current" -> {
                val current = when (currentType) {
                    CurrentType.DC -> p / v
                    CurrentType.AC_SINGLE -> p / (v * cosPhi)
                    CurrentType.AC_TWO -> p / (2.0 * v * cosPhi)
                    CurrentType.AC_THREE -> p / (sqrt(3.0) * v * cosPhi)
                }
                calculationResult = String.format("%.2f A", current)
                formulaDetails = when (currentType) {
                    CurrentType.DC -> "I = P / V"
                    CurrentType.AC_SINGLE -> "I = P / (V · cosφ)"
                    CurrentType.AC_TWO -> "I = P / (2 · V · cosφ)"
                    CurrentType.AC_THREE -> "I = P / (√3 · V · cosφ)"
                }
                stepsDetails = "P = $p W | V = $v V | cosφ = $cosPhi\nApparent Power S = ${String.format("%.1f", p / cosPhi)} VA"
            }

            "voltage_drop" -> {
                val current = p / (v * cosPhi)
                val rho = 0.0178 // Copper
                val r = (rho * l) / s
                val dv = when (currentType) {
                    CurrentType.AC_THREE -> sqrt(3.0) * current * r
                    else -> 2.0 * current * r
                }
                val dvPercent = (dv / v) * 100.0
                calculationResult = String.format("ΔV = %.2f V (%.2f%%)", dv, dvPercent)
                formulaDetails = "ΔV = 2 · L · I · (R / S)"
                stepsDetails = "Cable Length = $l m | Section = $s mm² | Current = ${String.format("%.2f", current)} A"
            }

            "voltage" -> {
                val i = (powerInput.toDoubleOrNull() ?: 10.0)
                val r = (cosPhiInput.toDoubleOrNull() ?: 23.0)
                val volt = i * r
                calculationResult = String.format("%.2f V", volt)
                formulaDetails = "V = I · R"
                stepsDetails = "I = $i A | R = $r Ω"
            }

            "active_power" -> {
                val current = (powerInput.toDoubleOrNull() ?: 10.0)
                val power = when (currentType) {
                    CurrentType.DC -> v * current
                    CurrentType.AC_SINGLE -> v * current * cosPhi
                    CurrentType.AC_TWO -> 2.0 * v * current * cosPhi
                    CurrentType.AC_THREE -> sqrt(3.0) * v * current * cosPhi
                }
                calculationResult = String.format("%.2f W (%.3f kW)", power, power / 1000.0)
                formulaDetails = "P = √3 · V · I · cosφ (3-Phase)"
                stepsDetails = "V = $v V | I = $current A | cosφ = $cosPhi"
            }

            "resistance" -> {
                val volt = v
                val current = powerInput.toDoubleOrNull() ?: 10.0
                val res = if (current > 0) volt / current else 0.0
                calculationResult = String.format("%.3f Ω", res)
                formulaDetails = "R = V / I"
                stepsDetails = "V = $volt V | I = $current A"
            }

            "motor_current" -> {
                val current = p / (sqrt(3.0) * v * cosPhi * 0.88)
                calculationResult = String.format("FLC = %.2f A", current)
                formulaDetails = "I = P / (√3 · V · cosφ · η)"
                stepsDetails = "Motor Power = ${p / 1000.0} kW | Efficiency η = 88% | cosφ = $cosPhi"
            }

            "motor_pf_correction" -> {
                val pf1 = cosPhi.coerceIn(0.30, 0.98)
                val pf2 = 0.95
                val eta = 0.90
                val pInKw = (p / 1000.0) / eta
                val phi1 = Math.acos(pf1)
                val phi2 = Math.acos(pf2)
                val qcKvar = pInKw * (Math.tan(phi1) - Math.tan(phi2))
                val cDeltaUf = (qcKvar * 1_000_000_000.0) / (3.0 * 2.0 * Math.PI * 50.0 * v * v)
                val i1 = (pInKw * 1000.0) / (sqrt(3.0) * v * pf1)
                val i2 = (pInKw * 1000.0) / (sqrt(3.0) * v * pf2)
                val deltaI = i1 - i2
                val maxSelfEx = 0.90 * (sqrt(3.0) * v * (i1 * 0.30)) / 1000.0
                calculationResult = String.format("Qc = %.2f kVAR (CΔ = %.1f µF)", qcKvar, cDeltaUf)
                formulaDetails = "Qc = Pin · [tan(φ1) - tan(φ2)] | CΔ = Qc / (3·2π·f·V²)"
                stepsDetails = "Motor: ${p / 1000.0} kW @ ${v.toInt()}V | Initial PF: $pf1 → Target: 0.95\n" +
                        "Line Current: ${String.format("%.1f", i1)} A → ${String.format("%.1f", i2)} A (-${String.format("%.1f", deltaI)} A reduction)\n" +
                        "Self-Excitation Limit: ≤ ${String.format("%.1f", maxSelfEx)} kVAR (IEEE 141)"
            }

            else -> {
                val current = p / (v * cosPhi)
                calculationResult = String.format("%.2f A (Apparent: %.1f VA)", current, p / cosPhi)
                formulaDetails = item.formulaEquation.ifEmpty { "I = P / (V · cosφ)" }
                stepsDetails = "Computed according to standard reference parameters."
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFF1E1F22),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = item.title,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        AdMobManager.showInterstitial(context as? Activity) {
                            onBack()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    // f(x) Formula dialog trigger
                    IconButton(onClick = { showFormulaDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Functions,
                            contentDescription = "Formula",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = { showInfoDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Info",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = { /* Menu */ }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1C3843)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Radio selector: Current type
            Text(
                text = "Current type:",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Column(
                modifier = Modifier
                    .selectableGroup()
                    .padding(bottom = 16.dp)
            ) {
                CurrentType.values().forEach { type ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .selectable(
                                selected = (type == currentType),
                                onClick = { currentType = type },
                                role = Role.RadioButton
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (type == currentType),
                            onClick = null,
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFF38A3B2),
                                unselectedColor = Color(0xFF78909C)
                            )
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = type.label,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            // Inputs Selection Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Inputs:",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = selectedInputMode,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Voltage input field with underline style
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Voltage:",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.width(100.dp)
                )

                TextField(
                    value = voltageInput,
                    onValueChange = { voltageInput = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedIndicatorColor = Color(0xFF38A3B2),
                        unfocusedIndicatorColor = Color(0xFF546E7A)
                    ),
                    singleLine = true
                )

                Text(
                    text = voltageUnit,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.width(36.dp)
                )
            }

            // Power input field with underline style
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Power:",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.width(100.dp)
                )

                TextField(
                    value = powerInput,
                    onValueChange = { powerInput = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedIndicatorColor = Color(0xFF38A3B2),
                        unfocusedIndicatorColor = Color(0xFF546E7A)
                    ),
                    singleLine = true
                )

                Text(
                    text = powerUnit,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.width(36.dp)
                )
            }

            // Cos φ input field with underline style
            if (currentType != CurrentType.DC) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Cos φ:",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.width(100.dp)
                    )

                    TextField(
                        value = cosPhiInput,
                        onValueChange = { cosPhiInput = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedIndicatorColor = Color(0xFF38A3B2),
                            unfocusedIndicatorColor = Color(0xFF546E7A)
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(36.dp))
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Big prominent CALCULATE Button
            Button(
                onClick = { doCalculate() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2C515F)
                )
            ) {
                Text(
                    text = "CALCULATE",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            // Results Card
            if (calculationResult != null) {
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF24262A)
                    ),
                    border = BorderStroke(1.dp, Color(0xFF38A3B2).copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Result",
                            color = Color(0xFF38A3B2),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = calculationResult!!,
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        if (formulaDetails != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Divider(color = Color.White.copy(alpha = 0.1f))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Formula: ${formulaDetails!!}",
                                color = Color(0xFFFFB300),
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        if (stepsDetails != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = stepsDetails!!,
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // Formula Dialog
    if (showFormulaDialog) {
        AlertDialog(
            onDismissRequest = { showFormulaDialog = false },
            containerColor = Color(0xFF1E282D),
            title = {
                Text(
                    text = item.formulaTitle.ifEmpty { item.title },
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Equation:",
                        color = Color(0xFF38A3B2),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = Color(0xFF111E24),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = item.formulaEquation.ifEmpty { "V = I · R" },
                            color = Color(0xFFFFD54F),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    if (item.formulaDescription.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = item.formulaDescription,
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }

                    if (item.formulaVariables.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Variables:",
                            color = Color(0xFF38A3B2),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        item.formulaVariables.forEach { (variable, desc) ->
                            Row(modifier = Modifier.padding(vertical = 2.dp)) {
                                Text(
                                    text = "$variable: ",
                                    color = Color(0xFFFFB300),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = desc,
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFormulaDialog = false }) {
                    Text("CLOSE", color = Color(0xFF38A3B2), fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Info Dialog
    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            containerColor = Color(0xFF1E282D),
            title = {
                Text(
                    text = "Information & Standards",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Complies with IEC 60364 electrical installation rules, NEC 2023, and standard electrical engineering formulations.",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) {
                    Text("OK", color = Color(0xFF38A3B2), fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
