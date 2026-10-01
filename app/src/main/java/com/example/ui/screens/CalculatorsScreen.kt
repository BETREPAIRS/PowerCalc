package com.example.ui.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ads.AdMobManager
import com.example.engine.*
import com.example.ui.CalculatorMeta
import com.example.ui.ElectricianViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorsScreen(
    viewModel: ElectricianViewModel,
    modifier: Modifier = Modifier
) {
    val activeCalcId by viewModel.activeCalculatorId.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val categoryFilter by viewModel.selectedCategoryFilter.collectAsState()
    val defaultV by viewModel.defaultVoltage.collectAsState()
    val context = LocalContext.current

    BackHandler(enabled = activeCalcId != null) {
        AdMobManager.showInterstitial(context as? Activity) {
            viewModel.closeCalculator()
        }
    }

    if (activeCalcId != null) {
        CalculatorDetailView(
            calcId = activeCalcId!!,
            viewModel = viewModel,
            onBack = {
                AdMobManager.showInterstitial(context as? Activity) {
                    viewModel.closeCalculator()
                }
            }
        )
    } else {
        // List Mode
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(AppBgLight)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Screen Header Title
            Text(
                text = "Electrical Calculators",
                color = BlueprintBlue,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar matching reference image
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                border = BorderStroke(1.dp, AppBorderLight),
                shadowElevation = 0.5.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = BlueprintBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    TextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = {
                            Text(
                                text = "Search",
                                color = AppTextMuted,
                                fontSize = 15.sp
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("calculator_search_input")
                    )
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { viewModel.setSearchQuery("") },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = AppTextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Category Filter Pills
            val categories = listOf("All", "Cable & Wiring", "Basic Electrical", "Power & Circuits", "Power Factor", "Protection", "Motors", "Transformers", "Batteries", "Solar/PV", "Industrial", "Lighting", "Components")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = cat == categoryFilter
                    ThreeDChip(
                        selected = isSelected,
                        onClick = { viewModel.setCategoryFilter(cat) },
                        label = cat,
                        modifier = Modifier.testTag("cat_chip_$cat")
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Section Title
            Text(
                text = if (categoryFilter == "All") "Engineering Calculators" else "$categoryFilter Calculators",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = AppTextDark
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Filtered calculators list
            val filteredList = remember(searchQuery, categoryFilter, viewModel.calculatorsList) {
                viewModel.calculatorsList.filter { calc ->
                    val matchesSearch = searchQuery.isEmpty() ||
                            calc.title.contains(searchQuery, ignoreCase = true) ||
                            calc.category.contains(searchQuery, ignoreCase = true) ||
                            calc.shortDescription.contains(searchQuery, ignoreCase = true)
                    val matchesCategory = categoryFilter == "All" || calc.category.equals(categoryFilter, ignoreCase = true)
                    matchesSearch && matchesCategory
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(top = 4.dp, bottom = 80.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredList, key = { it.id }) { calc ->
                    CalculatorItemCard(
                        calculator = calc,
                        onClick = { viewModel.openCalculator(calc.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun CalculatorItemCard(
    calculator: CalculatorMeta,
    onClick: () -> Unit
) {
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1.0f,
        animationSpec = androidx.compose.animation.core.spring(stiffness = androidx.compose.animation.core.Spring.StiffnessMediumLow),
        label = "cardScale"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .testTag("calc_card_${calculator.id}"),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, if (isPressed) BlueprintBlue else AppBorderLight),
        shadowElevation = if (isPressed) 0.dp else 1.5.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFEBF3FC)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (calculator.category) {
                        "Cable & Wiring" -> "⚡"
                        "Motors" -> "Ⓜ"
                        "Protection" -> "⎶"
                        "Transformers" -> "⧉"
                        "Solar/PV" -> "☀"
                        "Batteries" -> "🔋"
                        "Lighting" -> "💡"
                        else -> "Ω"
                    },
                    fontSize = 22.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = calculator.title,
                    fontSize = 15.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = BlueprintBlue
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = calculator.shortDescription,
                    fontSize = 12.sp,
                    color = AppTextMuted,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Open",
                tint = AppTextMuted.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun CalculatorDetailView(
    calcId: String,
    viewModel: ElectricianViewModel,
    onBack: () -> Unit
) {
    val meta = viewModel.calculatorsList.firstOrNull { it.id == calcId } ?: return
    val defaultV by viewModel.defaultVoltage.collectAsState()
    val defaultFreq by viewModel.defaultFrequency.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .clickable { onBack() }
                    .padding(vertical = 4.dp, horizontal = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = BlueprintBlue,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Back",
                    color = BlueprintBlue,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = meta.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = BlueprintBlue
                )
                Text(
                    text = meta.category,
                    fontSize = 12.sp,
                    color = AppTextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Route to specific calculator implementation
        when (calcId) {
            "cable_size" -> CableSizeCalculatorContent(viewModel, defaultV)
            "voltage_drop" -> VoltageDropCalculatorContent(viewModel, defaultV)
            "max_length" -> MaxLengthCalculatorContent(viewModel, defaultV)
            "max_length_isc" -> MaxLengthIscContent(viewModel, defaultV)
            "cable_losses" -> CablePowerLossesContent(viewModel)
            "cable_temperature" -> CableOperatingTemperatureContent(viewModel)
            "cable_impedance" -> CableImpedanceContent(viewModel)
            "distributed_voltage_drop" -> DistributedVoltageDropContent(viewModel, defaultV)
            "busbar_ampacity" -> BusbarAmpacityContent(viewModel)
            "conduit_tray_fill" -> ConduitTrayFillContent(viewModel)
            "breaker_size" -> BreakerSelectionContent(viewModel)
            "cable_short_circuit" -> CableShortCircuitAdiabaticContent(viewModel)
            "coordination_breaker_cable" -> BreakerCableCoordinationContent(viewModel)
            "earthing_rcd" -> EarthingRcdContent(viewModel)
            "ohms_voltage" -> OhmsVoltageContent(viewModel)
            "ohms_current" -> OhmsCurrentContent(viewModel)
            "ohms_resistance" -> OhmsResistanceContent(viewModel)
            "dc_power" -> DcPowerContent(viewModel, defaultV)
            "voltage_divider" -> VoltageDividerContent(viewModel)
            "current_divider" -> CurrentDividerContent(viewModel)
            "joule_effect" -> JouleEffectContent(viewModel)
            "ac_single_phase" -> AcSinglePhaseContent(viewModel, defaultV, defaultFreq)
            "ac_three_phase" -> AcThreePhaseContent(viewModel, defaultFreq)
            "apparent_power" -> ApparentPowerContent(viewModel, defaultV)
            "reactive_power" -> ReactivePowerContent(viewModel, defaultV)
            "neutral_current" -> NeutralCurrentContent(viewModel)
            "pf_correction" -> PfCorrectionContent(viewModel, defaultV, defaultFreq)
            "capacitor_voltage_power" -> CapacitorDifferentVoltageContent(viewModel)
            "motor_current" -> MotorCurrentContent(viewModel)
            "motor_power" -> MotorPowerContent(viewModel, defaultV)
            "motor_voltage" -> MotorVoltageContent(viewModel)
            "motor_power_factor" -> MotorPowerFactorContent(viewModel, defaultV)
            "motor_pf_correction" -> MotorPFCorrectionContent(viewModel, defaultV, defaultFreq)
            "motor_efficiency" -> MotorEfficiencyContent(viewModel, defaultV)
            "motor_three_to_single_phase" -> MotorThreeToSinglePhaseContent(viewModel, defaultFreq)
            "motor_single_phase_capacitor" -> SinglePhaseMotorRunCapacitorContent(viewModel, defaultFreq)
            "motor_speed" -> MotorSpeedContent(viewModel, defaultFreq)
            "motor_slip" -> MotorSlipContent(viewModel, defaultFreq)
            "motor_torque" -> MotorMaxTorqueContent(viewModel)
            "motor_flc_nec" -> MotorNecFlcContent(viewModel)
            "motor_diagram_6_terminals" -> MotorDiagram6TerminalsContent()
            "motor_diagram_9_terminals" -> MotorDiagram9TerminalsContent()
            "motor_diagram_12_terminals" -> MotorDiagram12TerminalsContent()
            "motor_connections" -> MotorConnectionsContent()
            "motor_terminals_marking" -> MotorTerminalsMarkingContent()
            "motor_insulation_class" -> MotorInsulationClassContent()
            "transformer" -> TransformerContent(viewModel)
            "battery_runtime" -> BatteryRuntimeContent(viewModel)
            "solar_pv" -> SolarPvContent(viewModel)
            "phase_balance" -> PhaseBalanceContent(viewModel)
            "analog_signal_converter" -> AnalogSignalConverterContent(viewModel)
            "antenna_length" -> AntennaLengthContent(viewModel)
            "cctv_storage_calc" -> CctvStorageContent(viewModel)
            "lighting_lux" -> LightingLuxContent(viewModel)
            "led_resistor" -> LedResistorContent(viewModel)
            "resonant_frequency" -> ResonantFrequencyContent(viewModel)
            "zener_regulator" -> ZenerRegulatorContent(viewModel)
            "capacitor_code" -> CapacitorCodeDecoderContent(viewModel)
            "energy_cost" -> EnergyCostContent(viewModel)
            "awg_conversion" -> AwgConversionContent(viewModel)
            else -> Text("Calculator under construction: $calcId")
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

// -------------------------------------------------------------------------
// SPECIFIC CALCULATOR SHEET IMPLEMENTATIONS
// -------------------------------------------------------------------------

@Composable
fun CableSizeCalculatorContent(viewModel: ElectricianViewModel, defaultV: Double) {
    var modeIndex by remember { mutableIntStateOf(0) } // 0 = Find Size from Load, 1 = Full Load Capacity by mm²

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Mode Selector: Calculate Size vs Full Load Capacity by mm²
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { modeIndex = 0 },
                shape = RoundedCornerShape(8.dp),
                color = if (modeIndex == 0) MaterialTheme.colorScheme.primary else Color.Transparent
            ) {
                Text(
                    text = "Find Size from Load",
                    modifier = Modifier.padding(vertical = 10.dp),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = if (modeIndex == 0) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { modeIndex = 1 },
                shape = RoundedCornerShape(8.dp),
                color = if (modeIndex == 1) MaterialTheme.colorScheme.primary else Color.Transparent
            ) {
                Text(
                    text = "Full Load Capacity (mm²)",
                    modifier = Modifier.padding(vertical = 10.dp),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = if (modeIndex == 1) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (modeIndex == 0) {
            CableSizingFromLoadMode(viewModel = viewModel, defaultV = defaultV)
        } else {
            CableCapacityByMm2Content(viewModel = viewModel, defaultV = defaultV)
        }
    }
}

@Composable
fun CableSizingFromLoadMode(viewModel: ElectricianViewModel, defaultV: Double) {
    var loadCurrent by remember { mutableStateOf("25") }
    var lengthM by remember { mutableStateOf("40") }
    var voltage by remember { mutableStateOf(defaultV.toString()) }
    var maxDropPct by remember { mutableStateOf("3.0") }
    var isCopper by remember { mutableStateOf(true) }
    var isThreePhase by remember { mutableStateOf(false) }
    var ambientTemp by remember { mutableStateOf("30") }
    var grouping by remember { mutableStateOf("1") }

    val currentD = loadCurrent.toDoubleOrNull() ?: 0.0
    val lengthD = lengthM.toDoubleOrNull() ?: 0.0
    val voltD = voltage.toDoubleOrNull() ?: defaultV
    val dropD = maxDropPct.toDoubleOrNull() ?: 3.0
    val tempD = ambientTemp.toDoubleOrNull() ?: 30.0
    val groupI = grouping.toIntOrNull() ?: 1

    val result = remember(currentD, lengthD, voltD, dropD, isCopper, isThreePhase, tempD, groupI) {
        ElectricalFormulas.calculateCableSizing(
            loadCurrent = currentD,
            lengthM = lengthD,
            systemVoltage = voltD,
            maxDropPct = dropD,
            material = if (isCopper) ConductorMaterial.COPPER else ConductorMaterial.ALUMINIUM,
            isThreePhase = isThreePhase,
            ambientTempC = tempD,
            groupedCircuits = groupI
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(
                value = loadCurrent,
                onValueChange = { loadCurrent = it },
                label = "Load Current",
                unitSuffix = "A",
                modifier = Modifier.weight(1f)
            )
            NumericInputField(
                value = lengthM,
                onValueChange = { lengthM = it },
                label = "Run Length",
                unitSuffix = "m",
                modifier = Modifier.weight(1f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(
                value = voltage,
                onValueChange = { voltage = it },
                label = "Voltage",
                unitSuffix = "V",
                modifier = Modifier.weight(1f)
            )
            NumericInputField(
                value = maxDropPct,
                onValueChange = { maxDropPct = it },
                label = "Max Drop",
                unitSuffix = "%",
                modifier = Modifier.weight(1f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(
                value = ambientTemp,
                onValueChange = { ambientTemp = it },
                label = "Ambient Temp",
                unitSuffix = "°C",
                modifier = Modifier.weight(1f)
            )
            NumericInputField(
                value = grouping,
                onValueChange = { grouping = it },
                label = "Grouped Circuits",
                unitSuffix = "qty",
                modifier = Modifier.weight(1f)
            )
        }

        // Toggles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Conductor:", fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = isCopper,
                    onClick = { isCopper = true },
                    label = { Text("Copper (Cu)") }
                )
                FilterChip(
                    selected = !isCopper,
                    onClick = { isCopper = false },
                    label = { Text("Aluminium (Al)") }
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("System Phase:", fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = !isThreePhase,
                    onClick = { isThreePhase = false },
                    label = { Text("1-Phase") }
                )
                FilterChip(
                    selected = isThreePhase,
                    onClick = { isThreePhase = true },
                    label = { Text("3-Phase") }
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "Cable Sizing",
                inputSummary = "I = $loadCurrent A, L = $lengthM m, V = $voltage V, MaxDrop = $maxDropPct%, ${if (isCopper) "Copper" else "Alu"}",
                onSave = { res ->
                    viewModel.saveCalculation(
                        calculatorId = "cable_size",
                        title = "Cable Sizing (${res.primaryValue})",
                        inputSummary = "I = $loadCurrent A, L = $lengthM m, V = $voltage V, MaxDrop = $maxDropPct%",
                        resultSummary = "${res.primaryValue} | Drop: ${res.secondaryValues["Voltage Drop"] ?: ""}",
                        formulaUsed = res.formula
                    )
                },
                onReset = {
                    loadCurrent = "25"
                    lengthM = "40"
                    voltage = defaultV.toString()
                    maxDropPct = "3.0"
                }
            )
        } else {
            Text(result.errorMessage ?: "Calculation Error", color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun CableCapacityByMm2Content(
    viewModel: ElectricianViewModel,
    defaultV: Double
) {
    var sizeMm2 by remember { mutableStateOf("4") }
    var voltage by remember { mutableStateOf(defaultV.toString()) }
    var isCopper by remember { mutableStateOf(true) }
    var isThreePhase by remember { mutableStateOf(false) }
    var ambientTemp by remember { mutableStateOf("30") }
    var installationMethod by remember { mutableStateOf("conduit") } // "conduit", "clipped", "tray", "underground"
    var grouping by remember { mutableStateOf("1") }
    var powerFactor by remember { mutableStateOf("0.90") }

    val sizeD = sizeMm2.toDoubleOrNull() ?: 0.0
    val voltD = voltage.toDoubleOrNull() ?: defaultV
    val tempD = ambientTemp.toDoubleOrNull() ?: 30.0
    val groupI = grouping.toIntOrNull() ?: 1
    val pfD = powerFactor.toDoubleOrNull() ?: 0.90

    val result = remember(sizeD, isCopper, isThreePhase, voltD, tempD, installationMethod, groupI, pfD) {
        ElectricalFormulas.calculateCableCapacityFromSize(
            sizeMm2 = sizeD,
            material = if (isCopper) ConductorMaterial.COPPER else ConductorMaterial.ALUMINIUM,
            isThreePhase = isThreePhase,
            systemVoltage = voltD,
            ambientTempC = tempD,
            installationMethod = installationMethod,
            groupedCircuits = groupI,
            powerFactor = pfD
        )
    }

    val standardSizes = listOf(1.5, 2.5, 4.0, 6.0, 10.0, 16.0, 25.0, 35.0, 50.0, 70.0, 95.0, 120.0, 150.0, 185.0, 240.0, 300.0)

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Size Input Field
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(
                value = sizeMm2,
                onValueChange = { sizeMm2 = it },
                label = "Cable Size",
                unitSuffix = "mm²",
                modifier = Modifier.weight(1.2f)
            )
            NumericInputField(
                value = voltage,
                onValueChange = { voltage = it },
                label = "Voltage",
                unitSuffix = "V",
                modifier = Modifier.weight(1f)
            )
        }

        // Quick Size Selector Chips
        Text(
            text = "Quick Size Selection (mm²):",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(standardSizes) { s ->
                val sStr = if (s % 1.0 == 0.0) s.toInt().toString() else s.toString()
                FilterChip(
                    selected = sizeMm2 == sStr,
                    onClick = { sizeMm2 = sStr },
                    label = { Text("$sStr mm²") }
                )
            }
        }

        // Conductor Material
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Conductor:", fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = isCopper,
                    onClick = { isCopper = true },
                    label = { Text("Copper (Cu)") }
                )
                FilterChip(
                    selected = !isCopper,
                    onClick = { isCopper = false },
                    label = { Text("Aluminium (Al)") }
                )
            }
        }

        // System Phase
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("System Phase:", fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = !isThreePhase,
                    onClick = {
                        isThreePhase = false
                        if (voltage == "400" || voltage == "415") voltage = "230"
                    },
                    label = { Text("1-Phase") }
                )
                FilterChip(
                    selected = isThreePhase,
                    onClick = {
                        isThreePhase = true
                        if (voltage == "230" || voltage == "120") voltage = "400"
                    },
                    label = { Text("3-Phase") }
                )
            }
        }

        // Installation Method
        Text(
            text = "Installation Method (IEC 60364):",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = installationMethod == "conduit",
                onClick = { installationMethod = "conduit" },
                label = { Text("Conduit (B2)") },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = installationMethod == "clipped",
                onClick = { installationMethod = "clipped" },
                label = { Text("Clipped (C)") },
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = installationMethod == "tray",
                onClick = { installationMethod = "tray" },
                label = { Text("Cable Tray (E)") },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = installationMethod == "underground",
                onClick = { installationMethod = "underground" },
                label = { Text("Buried (D)") },
                modifier = Modifier.weight(1f)
            )
        }

        // Environmental Conditions: Ambient Temp and Grouping
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(
                value = ambientTemp,
                onValueChange = { ambientTemp = it },
                label = "Ambient Temp",
                unitSuffix = "°C",
                modifier = Modifier.weight(1f)
            )
            NumericInputField(
                value = grouping,
                onValueChange = { grouping = it },
                label = "Grouped Circuits",
                unitSuffix = "qty",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "Cable Full Load Capacity",
                inputSummary = "Size = $sizeMm2 mm², V = $voltage V, ${if (isCopper) "Copper" else "Alu"}, ${if (isThreePhase) "3-Phase" else "1-Phase"}",
                onSave = { res ->
                    viewModel.saveCalculation(
                        calculatorId = "cable_size",
                        title = "Cable Capacity ($sizeMm2 mm² -> ${res.primaryValue})",
                        inputSummary = "Size = $sizeMm2 mm², V = $voltage V, ${if (isCopper) "Copper" else "Alu"}",
                        resultSummary = "Capacity: ${res.primaryValue} | Power: ${res.secondaryValues["Full Load Active Power"] ?: ""}",
                        formulaUsed = res.formula
                    )
                },
                onReset = {
                    sizeMm2 = "4"
                    voltage = defaultV.toString()
                    ambientTemp = "30"
                    grouping = "1"
                    installationMethod = "conduit"
                }
            )
        } else {
            Text(result.errorMessage ?: "Calculation Error", color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun VoltageDropCalculatorContent(viewModel: ElectricianViewModel, defaultV: Double) {
    var voltage by remember { mutableStateOf(defaultV.toString()) }
    var current by remember { mutableStateOf("20") }
    var lengthM by remember { mutableStateOf("50") }
    var sizeMm2 by remember { mutableStateOf("4.0") }
    var isCopper by remember { mutableStateOf(true) }
    var isThreePhase by remember { mutableStateOf(false) }

    val voltD = voltage.toDoubleOrNull() ?: defaultV
    val currD = current.toDoubleOrNull() ?: 0.0
    val lenD = lengthM.toDoubleOrNull() ?: 0.0
    val sizeD = sizeMm2.toDoubleOrNull() ?: 4.0

    val result = remember(voltD, currD, lenD, sizeD, isCopper, isThreePhase) {
        ElectricalFormulas.calculateVoltageDrop(
            voltage = voltD,
            current = currD,
            lengthM = lenD,
            sizeMm2 = sizeD,
            material = if (isCopper) ConductorMaterial.COPPER else ConductorMaterial.ALUMINIUM,
            isThreePhase = isThreePhase
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = voltage, onValueChange = { voltage = it }, label = "System Voltage", unitSuffix = "V", modifier = Modifier.weight(1f))
            NumericInputField(value = current, onValueChange = { current = it }, label = "Load Current", unitSuffix = "A", modifier = Modifier.weight(1f))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = lengthM, onValueChange = { lengthM = it }, label = "Run Length", unitSuffix = "m", modifier = Modifier.weight(1f))
            NumericInputField(value = sizeMm2, onValueChange = { sizeMm2 = it }, label = "Cable Size", unitSuffix = "mm²", modifier = Modifier.weight(1f))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = isCopper, onClick = { isCopper = true }, label = { Text("Copper") })
            FilterChip(selected = !isCopper, onClick = { isCopper = false }, label = { Text("Aluminium") })
            Spacer(modifier = Modifier.width(10.dp))
            FilterChip(selected = !isThreePhase, onClick = { isThreePhase = false }, label = { Text("1-Phase") })
            FilterChip(selected = isThreePhase, onClick = { isThreePhase = true }, label = { Text("3-Phase") })
        }

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "Voltage Drop",
                inputSummary = "V = $voltage V, I = $current A, L = $lengthM m, Size = $sizeMm2 mm²",
                onSave = { res ->
                    viewModel.saveCalculation(
                        calculatorId = "voltage_drop",
                        title = "Voltage Drop (${res.primaryValue} ${res.primaryUnit})",
                        inputSummary = "V = $voltage V, I = $current A, L = $lengthM m, Size = $sizeMm2 mm²",
                        resultSummary = "Drop: ${res.primaryValue} V (${res.secondaryValues["Voltage Drop %"] ?: ""})",
                        formulaUsed = res.formula
                    )
                },
                onReset = {
                    voltage = defaultV.toString()
                    current = "20"
                    lengthM = "50"
                    sizeMm2 = "4.0"
                }
            )
        }
    }
}

@Composable
fun MaxLengthCalculatorContent(viewModel: ElectricianViewModel, defaultV: Double) {
    var voltage by remember { mutableStateOf(defaultV.toString()) }
    var current by remember { mutableStateOf("16") }
    var sizeMm2 by remember { mutableStateOf("2.5") }
    var maxDropPct by remember { mutableStateOf("3.0") }
    var isCopper by remember { mutableStateOf(true) }
    var isThreePhase by remember { mutableStateOf(false) }

    val voltD = voltage.toDoubleOrNull() ?: defaultV
    val currD = current.toDoubleOrNull() ?: 0.0
    val sizeD = sizeMm2.toDoubleOrNull() ?: 2.5
    val dropD = maxDropPct.toDoubleOrNull() ?: 3.0

    val result = remember(voltD, currD, sizeD, dropD, isCopper, isThreePhase) {
        ElectricalFormulas.calculateMaxCableLength(
            voltage = voltD,
            current = currD,
            sizeMm2 = sizeD,
            maxDropPct = dropD,
            material = if (isCopper) ConductorMaterial.COPPER else ConductorMaterial.ALUMINIUM,
            isThreePhase = isThreePhase
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = voltage, onValueChange = { voltage = it }, label = "Voltage", unitSuffix = "V", modifier = Modifier.weight(1f))
            NumericInputField(value = current, onValueChange = { current = it }, label = "Current", unitSuffix = "A", modifier = Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = sizeMm2, onValueChange = { sizeMm2 = it }, label = "Conductor Size", unitSuffix = "mm²", modifier = Modifier.weight(1f))
            NumericInputField(value = maxDropPct, onValueChange = { maxDropPct = it }, label = "Max Drop Limit", unitSuffix = "%", modifier = Modifier.weight(1f))
        }

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "Max Cable Length",
                inputSummary = "V = $voltage V, I = $current A, Size = $sizeMm2 mm², Drop = $maxDropPct%",
                onSave = { res ->
                    viewModel.saveCalculation(
                        calculatorId = "max_length",
                        title = "Max Cable Run (${res.primaryValue} m)",
                        inputSummary = "V = $voltage V, I = $current A, Size = $sizeMm2 mm²",
                        resultSummary = "Max Run: ${res.primaryValue} m (${res.secondaryValues["In Feet"] ?: ""})",
                        formulaUsed = res.formula
                    )
                },
                onReset = { current = "16"; sizeMm2 = "2.5" }
            )
        }
    }
}

@Composable
fun BreakerSelectionContent(viewModel: ElectricianViewModel) {
    var loadCurrent by remember { mutableStateOf("28") }
    var isContinuous by remember { mutableStateOf(true) }
    var cableAmpacity by remember { mutableStateOf("36") }
    var loadType by remember { mutableStateOf("General") }

    val currD = loadCurrent.toDoubleOrNull() ?: 0.0
    val cableD = cableAmpacity.toDoubleOrNull() ?: 0.0

    val result = remember(currD, isContinuous, cableD, loadType) {
        ElectricalFormulas.calculateBreakerSelection(
            loadCurrent = currD,
            isContinuous = isContinuous,
            cableAmpacity = cableD,
            loadType = loadType
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = loadCurrent, onValueChange = { loadCurrent = it }, label = "Load Current", unitSuffix = "A", modifier = Modifier.weight(1f))
            NumericInputField(value = cableAmpacity, onValueChange = { cableAmpacity = it }, label = "Cable Ampacity", unitSuffix = "A", modifier = Modifier.weight(1f))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Continuous Load (125%):", fontWeight = FontWeight.SemiBold)
            Switch(checked = isContinuous, onCheckedChange = { isContinuous = it })
        }

        Text("Load Profile:", fontWeight = FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("General", "Motor", "Lighting", "Transformer").forEach { type ->
                FilterChip(
                    selected = loadType == type,
                    onClick = { loadType = type },
                    label = { Text(type) }
                )
            }
        }

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "Breaker Selection",
                inputSummary = "Load = $loadCurrent A, Continuous = $isContinuous, Cable = $cableAmpacity A",
                onSave = { res ->
                    viewModel.saveCalculation(
                        calculatorId = "breaker_size",
                        title = "Breaker Rating (${res.primaryValue})",
                        inputSummary = "Load = $loadCurrent A, Type = $loadType",
                        resultSummary = "Breaker: ${res.primaryValue} | ${res.secondaryValues["Recommended Trip Curve"] ?: ""}",
                        formulaUsed = res.formula
                    )
                },
                onReset = { loadCurrent = "28"; cableAmpacity = "36" }
            )
        }
    }
}

@Composable
fun OhmsVoltageContent(viewModel: ElectricianViewModel) {
    var current by remember { mutableStateOf("10") }
    var resistance by remember { mutableStateOf("23") }

    val currD = current.toDoubleOrNull() ?: 0.0
    val resD = resistance.toDoubleOrNull() ?: 0.0

    val result = remember(currD, resD) {
        ElectricalFormulas.calculateVoltageFromIR(currD, resD)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = current, onValueChange = { current = it }, label = "Current (I)", unitSuffix = "A", modifier = Modifier.weight(1f))
            NumericInputField(value = resistance, onValueChange = { resistance = it }, label = "Resistance (R)", unitSuffix = "Ω", modifier = Modifier.weight(1f))
        }

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "Voltage (Ohm's Law)",
                inputSummary = "I = $current A, R = $resistance Ω",
                onSave = { res ->
                    viewModel.saveCalculation("ohms_voltage", "Voltage (${res.primaryValue} V)", "I = $current A, R = $resistance Ω", "V = ${res.primaryValue} V | P = ${res.secondaryValues["Power (P = I²R)"] ?: ""}", res.formula)
                },
                onReset = { current = "10"; resistance = "23" }
            )
        }
    }
}

@Composable
fun OhmsCurrentContent(viewModel: ElectricianViewModel) {
    var voltage by remember { mutableStateOf("230") }
    var resistance by remember { mutableStateOf("46") }

    val voltD = voltage.toDoubleOrNull() ?: 0.0
    val resD = resistance.toDoubleOrNull() ?: 0.0

    val result = remember(voltD, resD) {
        ElectricalFormulas.calculateCurrentFromVR(voltD, resD)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = voltage, onValueChange = { voltage = it }, label = "Voltage (V)", unitSuffix = "V", modifier = Modifier.weight(1f))
            NumericInputField(value = resistance, onValueChange = { resistance = it }, label = "Resistance (R)", unitSuffix = "Ω", modifier = Modifier.weight(1f))
        }

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "Current (Ohm's Law)",
                inputSummary = "V = $voltage V, R = $resistance Ω",
                onSave = { res ->
                    viewModel.saveCalculation("ohms_current", "Current (${res.primaryValue} A)", "V = $voltage V, R = $resistance Ω", "I = ${res.primaryValue} A", res.formula)
                },
                onReset = { voltage = "230"; resistance = "46" }
            )
        }
    }
}

@Composable
fun OhmsResistanceContent(viewModel: ElectricianViewModel) {
    var voltage by remember { mutableStateOf("230") }
    var current by remember { mutableStateOf("5") }

    val voltD = voltage.toDoubleOrNull() ?: 0.0
    val currD = current.toDoubleOrNull() ?: 0.0

    val result = remember(voltD, currD) {
        ElectricalFormulas.calculateResistanceFromVI(voltD, currD)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = voltage, onValueChange = { voltage = it }, label = "Voltage (V)", unitSuffix = "V", modifier = Modifier.weight(1f))
            NumericInputField(value = current, onValueChange = { current = it }, label = "Current (I)", unitSuffix = "A", modifier = Modifier.weight(1f))
        }

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "Resistance (Ohm's Law)",
                inputSummary = "V = $voltage V, I = $current A",
                onSave = { res ->
                    viewModel.saveCalculation("ohms_resistance", "Resistance (${res.primaryValue} Ω)", "V = $voltage V, I = $current A", "R = ${res.primaryValue} Ω", res.formula)
                },
                onReset = { voltage = "230"; current = "5" }
            )
        }
    }
}

@Composable
fun DcPowerContent(viewModel: ElectricianViewModel, defaultV: Double) {
    var voltage by remember { mutableStateOf(defaultV.toString()) }
    var current by remember { mutableStateOf("12") }

    val voltD = voltage.toDoubleOrNull() ?: defaultV
    val currD = current.toDoubleOrNull() ?: 0.0

    val result = remember(voltD, currD) {
        ElectricalFormulas.calculateDCPower(voltD, currD)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = voltage, onValueChange = { voltage = it }, label = "Voltage (V)", unitSuffix = "V", modifier = Modifier.weight(1f))
            NumericInputField(value = current, onValueChange = { current = it }, label = "Current (I)", unitSuffix = "A", modifier = Modifier.weight(1f))
        }

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "DC Power",
                inputSummary = "V = $voltage V, I = $current A",
                onSave = { res ->
                    viewModel.saveCalculation("dc_power", "Power (${res.primaryValue} W)", "V = $voltage V, I = $current A", "P = ${res.primaryValue} W (${res.secondaryValues["Power in kW"] ?: ""})", res.formula)
                },
                onReset = { voltage = defaultV.toString(); current = "12" }
            )
        }
    }
}

@Composable
fun AcSinglePhaseContent(viewModel: ElectricianViewModel, defaultV: Double, defaultFreq: Double) {
    var voltage by remember { mutableStateOf(defaultV.toString()) }
    var current by remember { mutableStateOf("16") }
    var pf by remember { mutableStateOf("0.85") }

    val voltD = voltage.toDoubleOrNull() ?: defaultV
    val currD = current.toDoubleOrNull() ?: 0.0
    val pfD = pf.toDoubleOrNull() ?: 0.85

    val result = remember(voltD, currD, pfD) {
        ElectricalFormulas.calculateACSinglePhase(voltD, currD, pfD, defaultFreq)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = voltage, onValueChange = { voltage = it }, label = "RMS Voltage", unitSuffix = "V", modifier = Modifier.weight(1f))
            NumericInputField(value = current, onValueChange = { current = it }, label = "RMS Current", unitSuffix = "A", modifier = Modifier.weight(1f))
        }
        NumericInputField(value = pf, onValueChange = { pf = it }, label = "Power Factor (0.01 - 1.0)", unitSuffix = "cosφ")

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "AC Single Phase",
                inputSummary = "V = $voltage V, I = $current A, PF = $pf",
                onSave = { res ->
                    viewModel.saveCalculation("ac_single_phase", "AC 1-Phase (${res.primaryValue} kW)", "V = $voltage V, I = $current A, PF = $pf", "P = ${res.primaryValue} kW | S = ${res.secondaryValues["Apparent Power (S)"] ?: ""}", res.formula)
                },
                onReset = { voltage = defaultV.toString(); current = "16"; pf = "0.85" }
            )
        }
    }
}

@Composable
fun AcThreePhaseContent(viewModel: ElectricianViewModel, defaultFreq: Double) {
    var lineVoltage by remember { mutableStateOf("400") }
    var lineCurrent by remember { mutableStateOf("32") }
    var pf by remember { mutableStateOf("0.88") }
    var isStar by remember { mutableStateOf(true) }

    val voltD = lineVoltage.toDoubleOrNull() ?: 400.0
    val currD = lineCurrent.toDoubleOrNull() ?: 0.0
    val pfD = pf.toDoubleOrNull() ?: 0.88

    val result = remember(voltD, currD, pfD, isStar) {
        ElectricalFormulas.calculateACThreePhase(
            vLine = voltD,
            iLine = currD,
            pf = pfD,
            connection = if (isStar) ThreePhaseConnection.STAR else ThreePhaseConnection.DELTA
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = lineVoltage, onValueChange = { lineVoltage = it }, label = "Line Voltage (VL)", unitSuffix = "V", modifier = Modifier.weight(1f))
            NumericInputField(value = lineCurrent, onValueChange = { lineCurrent = it }, label = "Line Current (IL)", unitSuffix = "A", modifier = Modifier.weight(1f))
        }
        NumericInputField(value = pf, onValueChange = { pf = it }, label = "Power Factor", unitSuffix = "cosφ")

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = isStar, onClick = { isStar = true }, label = { Text("Star (Y) Connection") })
            FilterChip(selected = !isStar, onClick = { isStar = false }, label = { Text("Delta (Δ) Connection") })
        }

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "AC 3-Phase Power",
                inputSummary = "VL = $lineVoltage V, IL = $lineCurrent A, PF = $pf, ${if (isStar) "Star" else "Delta"}",
                onSave = { res ->
                    viewModel.saveCalculation("ac_three_phase", "3-Phase Power (${res.primaryValue} kW)", "VL = $lineVoltage V, IL = $lineCurrent A, PF = $pf", "P = ${res.primaryValue} kW | S = ${res.secondaryValues["Apparent Power (S)"] ?: ""}", res.formula)
                },
                onReset = { lineVoltage = "400"; lineCurrent = "32"; pf = "0.88" }
            )
        }
    }
}

@Composable
fun PfCorrectionContent(viewModel: ElectricianViewModel, defaultV: Double, defaultFreq: Double) {
    var activeKW by remember { mutableStateOf("50") }
    var currentPf by remember { mutableStateOf("0.75") }
    var targetPf by remember { mutableStateOf("0.95") }
    var voltage by remember { mutableStateOf("400") }
    var isThreePhase by remember { mutableStateOf(true) }

    val kwD = activeKW.toDoubleOrNull() ?: 0.0
    val cPfD = currentPf.toDoubleOrNull() ?: 0.75
    val tPfD = targetPf.toDoubleOrNull() ?: 0.95
    val voltD = voltage.toDoubleOrNull() ?: 400.0

    val result = remember(kwD, cPfD, tPfD, voltD, isThreePhase) {
        ElectricalFormulas.calculatePFCorrection(
            activePowerKW = kwD,
            currentPF = cPfD,
            targetPF = tPfD,
            voltage = voltD,
            frequency = defaultFreq,
            isThreePhase = isThreePhase
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = activeKW, onValueChange = { activeKW = it }, label = "Active Load Power", unitSuffix = "kW", modifier = Modifier.weight(1f))
            NumericInputField(value = voltage, onValueChange = { voltage = it }, label = "Voltage", unitSuffix = "V", modifier = Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = currentPf, onValueChange = { currentPf = it }, label = "Current PF", unitSuffix = "cosφ", modifier = Modifier.weight(1f))
            NumericInputField(value = targetPf, onValueChange = { targetPf = it }, label = "Target PF", unitSuffix = "cosφ", modifier = Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = isThreePhase, onClick = { isThreePhase = true }, label = { Text("3-Phase Bank (Δ)") })
            FilterChip(selected = !isThreePhase, onClick = { isThreePhase = false }, label = { Text("1-Phase Bank") })
        }

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "PF Correction",
                inputSummary = "P = $activeKW kW, PF1 = $currentPf -> PF2 = $targetPf, V = $voltage V",
                onSave = { res ->
                    viewModel.saveCalculation("pf_correction", "Capacitor Bank (${res.primaryValue} kVAR)", "P = $activeKW kW, PF $currentPf -> $targetPf", "Bank: ${res.primaryValue} kVAR (${res.secondaryValues["Required Capacitance"] ?: ""})", res.formula)
                },
                onReset = { activeKW = "50"; currentPf = "0.75"; targetPf = "0.95" }
            )
        }
    }
}

@Composable
fun MotorCurrentContent(viewModel: ElectricianViewModel) {
    var powerVal by remember { mutableStateOf("15") }
    var isHp by remember { mutableStateOf(true) }
    var voltage by remember { mutableStateOf("400") }
    var pf by remember { mutableStateOf("0.85") }
    var efficiency by remember { mutableStateOf("91") }
    var isThreePhase by remember { mutableStateOf(true) }

    val powD = powerVal.toDoubleOrNull() ?: 0.0
    val voltD = voltage.toDoubleOrNull() ?: 400.0
    val pfD = pf.toDoubleOrNull() ?: 0.85
    val effD = efficiency.toDoubleOrNull() ?: 91.0

    val result = remember(powD, isHp, voltD, pfD, effD, isThreePhase) {
        ElectricalFormulas.calculateMotorCurrent(
            powerValue = powD,
            isHp = isHp,
            voltage = voltD,
            pf = pfD,
            efficiencyPct = effD,
            isThreePhase = isThreePhase
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = powerVal, onValueChange = { powerVal = it }, label = "Motor Power", unitSuffix = if (isHp) "HP" else "kW", modifier = Modifier.weight(1f))
            NumericInputField(value = voltage, onValueChange = { voltage = it }, label = "Voltage", unitSuffix = "V", modifier = Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = pf, onValueChange = { pf = it }, label = "Power Factor", unitSuffix = "cosφ", modifier = Modifier.weight(1f))
            NumericInputField(value = efficiency, onValueChange = { efficiency = it }, label = "Efficiency (η)", unitSuffix = "%", modifier = Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = isHp, onClick = { isHp = true }, label = { Text("Horsepower (HP)") })
            FilterChip(selected = !isHp, onClick = { isHp = false }, label = { Text("Kilowatts (kW)") })
            Spacer(modifier = Modifier.width(6.dp))
            FilterChip(selected = isThreePhase, onClick = { isThreePhase = true }, label = { Text("3-Phase") })
            FilterChip(selected = !isThreePhase, onClick = { isThreePhase = false }, label = { Text("1-Phase") })
        }

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "Motor Full Load Current",
                inputSummary = "P = $powerVal ${if (isHp) "HP" else "kW"}, V = $voltage V, η = $efficiency%",
                onSave = { res ->
                    viewModel.saveCalculation("motor_current", "Motor FLC (${res.primaryValue})", "P = $powerVal ${if (isHp) "HP" else "kW"}, V = $voltage V", "FLC: ${res.primaryValue} | DOL Inrush: ${res.secondaryValues["DOL Starting Current (6.5x)"] ?: ""}", res.formula)
                },
                onReset = { powerVal = "15"; isHp = true }
            )
        }
    }
}

@Composable
fun MotorTorqueContent(viewModel: ElectricianViewModel) {
    var powerKw by remember { mutableStateOf("11") }
    var rpm by remember { mutableStateOf("1450") }

    val powD = powerKw.toDoubleOrNull() ?: 0.0
    val rpmD = rpm.toDoubleOrNull() ?: 1450.0

    val result = remember(powD, rpmD) {
        ElectricalFormulas.calculateMotorTorque(powD, rpmD)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = powerKw, onValueChange = { powerKw = it }, label = "Mechanical Power", unitSuffix = "kW", modifier = Modifier.weight(1f))
            NumericInputField(value = rpm, onValueChange = { rpm = it }, label = "Shaft Speed", unitSuffix = "RPM", modifier = Modifier.weight(1f))
        }

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "Motor Torque",
                inputSummary = "P = $powerKw kW, RPM = $rpm",
                onSave = { res ->
                    viewModel.saveCalculation("motor_torque", "Motor Torque (${res.primaryValue} N·m)", "P = $powerKw kW, RPM = $rpm", "Torque: ${res.primaryValue} N·m (${res.secondaryValues["Imperial Torque"] ?: ""})", res.formula)
                },
                onReset = { powerKw = "11"; rpm = "1450" }
            )
        }
    }
}

@Composable
fun MotorSlipContent(viewModel: ElectricianViewModel, defaultFreq: Double) {
    var frequency by remember { mutableStateOf(defaultFreq.toString()) }
    var poles by remember { mutableStateOf("4") }
    var actualRpm by remember { mutableStateOf("1440") }

    val fD = frequency.toDoubleOrNull() ?: defaultFreq
    val pI = poles.toIntOrNull() ?: 4
    val rD = actualRpm.toDoubleOrNull() ?: 1440.0

    val result = remember(fD, pI, rD) {
        ElectricalFormulas.calculateMotorSlip(fD, pI, rD)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = frequency, onValueChange = { frequency = it }, label = "Frequency", unitSuffix = "Hz", modifier = Modifier.weight(1f))
            NumericInputField(value = actualRpm, onValueChange = { actualRpm = it }, label = "Rotor RPM", unitSuffix = "RPM", modifier = Modifier.weight(1f))
        }
        Text("Poles Count:", fontWeight = FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("2", "4", "6", "8").forEach { poleStr ->
                FilterChip(
                    selected = poles == poleStr,
                    onClick = { poles = poleStr },
                    label = { Text("$poleStr Poles") }
                )
            }
        }

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "Motor Slip & Speed",
                inputSummary = "f = $frequency Hz, Poles = $poles, Actual = $actualRpm RPM",
                onSave = { res ->
                    viewModel.saveCalculation("motor_slip", "Motor Slip (${res.primaryValue})", "Poles = $poles, RPM = $actualRpm", "Slip: ${res.primaryValue} | Sync: ${res.secondaryValues["Synchronous Speed (Ns)"] ?: ""}", res.formula)
                },
                onReset = { poles = "4"; actualRpm = "1440" }
            )
        }
    }
}

@Composable
fun TransformerContent(viewModel: ElectricianViewModel) {
    var kva by remember { mutableStateOf("500") }
    var vPrimary by remember { mutableStateOf("11000") }
    var vSecondary by remember { mutableStateOf("400") }
    var percentZ by remember { mutableStateOf("4.5") }
    var isThreePhase by remember { mutableStateOf(true) }

    val kvaD = kva.toDoubleOrNull() ?: 0.0
    val vpD = vPrimary.toDoubleOrNull() ?: 11000.0
    val vsD = vSecondary.toDoubleOrNull() ?: 400.0
    val zD = percentZ.toDoubleOrNull() ?: 4.5

    val result = remember(kvaD, vpD, vsD, zD, isThreePhase) {
        ElectricalFormulas.calculateTransformer(kvaD, vpD, vsD, zD, isThreePhase)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = kva, onValueChange = { kva = it }, label = "Transformer Rating", unitSuffix = "kVA", modifier = Modifier.weight(1f))
            NumericInputField(value = percentZ, onValueChange = { percentZ = it }, label = "Impedance (%Z)", unitSuffix = "%", modifier = Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = vPrimary, onValueChange = { vPrimary = it }, label = "Primary Voltage", unitSuffix = "V", modifier = Modifier.weight(1f))
            NumericInputField(value = vSecondary, onValueChange = { vSecondary = it }, label = "Secondary Voltage", unitSuffix = "V", modifier = Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = isThreePhase, onClick = { isThreePhase = true }, label = { Text("3-Phase Transformer") })
            FilterChip(selected = !isThreePhase, onClick = { isThreePhase = false }, label = { Text("1-Phase Transformer") })
        }

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "Transformer Calculations",
                inputSummary = "kVA = $kva, V1 = $vPrimary V, V2 = $vSecondary V, %Z = $percentZ%",
                onSave = { res ->
                    viewModel.saveCalculation("transformer", "Transformer (${res.primaryValue})", "Rating = $kva kVA, V = $vPrimary / $vSecondary V", "Sec FLC: ${res.primaryValue} | Fault Isc: ${res.secondaryValues["Secondary Fault Current (Isc)"] ?: ""}", res.formula)
                },
                onReset = { kva = "500"; vPrimary = "11000"; vSecondary = "400"; percentZ = "4.5" }
            )
        }
    }
}

@Composable
fun BatteryRuntimeContent(viewModel: ElectricianViewModel) {
    var ah by remember { mutableStateOf("200") }
    var voltage by remember { mutableStateOf("24") }
    var loadWatts by remember { mutableStateOf("600") }
    var dodPct by remember { mutableStateOf("80") }

    val ahD = ah.toDoubleOrNull() ?: 0.0
    val voltD = voltage.toDoubleOrNull() ?: 24.0
    val loadD = loadWatts.toDoubleOrNull() ?: 0.0
    val dodD = dodPct.toDoubleOrNull() ?: 80.0

    val result = remember(ahD, voltD, loadD, dodD) {
        ElectricalFormulas.calculateBatteryRuntime(ahD, voltD, loadD, dodD, 90.0)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = ah, onValueChange = { ah = it }, label = "Battery Capacity", unitSuffix = "Ah", modifier = Modifier.weight(1f))
            NumericInputField(value = voltage, onValueChange = { voltage = it }, label = "Bank Voltage", unitSuffix = "V", modifier = Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = loadWatts, onValueChange = { loadWatts = it }, label = "Inverter Load", unitSuffix = "W", modifier = Modifier.weight(1f))
            NumericInputField(value = dodPct, onValueChange = { dodPct = it }, label = "Depth of Discharge", unitSuffix = "%", modifier = Modifier.weight(1f))
        }

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "Battery Runtime",
                inputSummary = "Capacity = $ah Ah @ $voltage V, Load = $loadWatts W, DoD = $dodPct%",
                onSave = { res ->
                    viewModel.saveCalculation("battery_runtime", "Battery Runtime (${res.primaryValue})", "Capacity = $ah Ah, Load = $loadWatts W", "Runtime: ${res.primaryValue} | Usable: ${res.secondaryValues["Usable Energy"] ?: ""}", res.formula)
                },
                onReset = { ah = "200"; voltage = "24"; loadWatts = "600" }
            )
        }
    }
}

@Composable
fun SolarPvContent(viewModel: ElectricianViewModel) {
    var dailyKwh by remember { mutableStateOf("24") }
    var peakSunHours by remember { mutableStateOf("4.5") }
    var lossPct by remember { mutableStateOf("20") }

    val kwhD = dailyKwh.toDoubleOrNull() ?: 0.0
    val pshD = peakSunHours.toDoubleOrNull() ?: 4.5
    val lossD = lossPct.toDoubleOrNull() ?: 20.0

    val result = remember(kwhD, pshD, lossD) {
        ElectricalFormulas.calculatePVSizing(kwhD, pshD, lossD)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = dailyKwh, onValueChange = { dailyKwh = it }, label = "Daily Energy Need", unitSuffix = "kWh/d", modifier = Modifier.weight(1f))
            NumericInputField(value = peakSunHours, onValueChange = { peakSunHours = it }, label = "Peak Sun Hours", unitSuffix = "PSH", modifier = Modifier.weight(1f))
        }
        NumericInputField(value = lossPct, onValueChange = { lossPct = it }, label = "System Loss Factor", unitSuffix = "%")

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "Solar PV Sizing",
                inputSummary = "Demand = $dailyKwh kWh/day, Sun = $peakSunHours PSH, Loss = $lossPct%",
                onSave = { res ->
                    viewModel.saveCalculation("solar_pv", "Solar PV (${res.primaryValue} kWp)", "Demand = $dailyKwh kWh/day, Sun = $peakSunHours PSH", "Array: ${res.primaryValue} kWp | ${res.secondaryValues["Panels (550W each)"] ?: ""}", res.formula)
                },
                onReset = { dailyKwh = "24"; peakSunHours = "4.5" }
            )
        }
    }
}

@Composable
fun PhaseBalanceContent(viewModel: ElectricianViewModel) {
    var l1 by remember { mutableStateOf("12000") }
    var l2 by remember { mutableStateOf("11500") }
    var l3 by remember { mutableStateOf("8500") }

    val l1D = l1.toDoubleOrNull() ?: 0.0
    val l2D = l2.toDoubleOrNull() ?: 0.0
    val l3D = l3.toDoubleOrNull() ?: 0.0

    val result = remember(l1D, l2D, l3D) {
        ElectricalFormulas.calculatePhaseBalance(l1D, l2D, l3D)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = l1, onValueChange = { l1 = it }, label = "Phase L1 Load", unitSuffix = "W", modifier = Modifier.weight(1f))
            NumericInputField(value = l2, onValueChange = { l2 = it }, label = "Phase L2 Load", unitSuffix = "W", modifier = Modifier.weight(1f))
        }
        NumericInputField(value = l3, onValueChange = { l3 = it }, label = "Phase L3 Load", unitSuffix = "W")

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "3-Phase Balance",
                inputSummary = "L1 = $l1 W, L2 = $l2 W, L3 = $l3 W",
                onSave = { res ->
                    viewModel.saveCalculation("phase_balance", "Phase Balance (${res.primaryValue})", "L1=$l1, L2=$l2, L3=$l3 W", "Imbalance: ${res.primaryValue} | Status: ${res.secondaryValues["Status"] ?: ""}", res.formula)
                },
                onReset = { l1 = "12000"; l2 = "11500"; l3 = "8500" }
            )
        }
    }
}

@Composable
fun LightingLuxContent(viewModel: ElectricianViewModel) {
    var area by remember { mutableStateOf("45") }
    var targetLux by remember { mutableStateOf("500") }
    var lumensPerFixture by remember { mutableStateOf("4000") }

    val areaD = area.toDoubleOrNull() ?: 0.0
    val luxD = targetLux.toDoubleOrNull() ?: 500.0
    val lumD = lumensPerFixture.toDoubleOrNull() ?: 4000.0

    val result = remember(areaD, luxD, lumD) {
        ElectricalFormulas.calculateLighting(areaD, luxD, lumD)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = area, onValueChange = { area = it }, label = "Room Floor Area", unitSuffix = "m²", modifier = Modifier.weight(1f))
            NumericInputField(value = targetLux, onValueChange = { targetLux = it }, label = "Target Lux (lx)", unitSuffix = "Lux", modifier = Modifier.weight(1f))
        }
        NumericInputField(value = lumensPerFixture, onValueChange = { lumensPerFixture = it }, label = "Fixture Lumens Output", unitSuffix = "lm")

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "Lighting Layout",
                inputSummary = "Area = $area m², Target = $targetLux Lux, Fixture = $lumensPerFixture lm",
                onSave = { res ->
                    viewModel.saveCalculation("lighting_lux", "Lighting (${res.primaryValue} Fixtures)", "Area = $area m², Lux = $targetLux", "Fixtures: ${res.primaryValue} | Total: ${res.secondaryValues["Total Lumens Required"] ?: ""}", res.formula)
                },
                onReset = { area = "45"; targetLux = "500" }
            )
        }
    }
}

@Composable
fun LedResistorContent(viewModel: ElectricianViewModel) {
    var supplyV by remember { mutableStateOf("12") }
    var ledVf by remember { mutableStateOf("3.2") }
    var currentMa by remember { mutableStateOf("20") }

    val supD = supplyV.toDoubleOrNull() ?: 12.0
    val vfD = ledVf.toDoubleOrNull() ?: 3.2
    val maD = currentMa.toDoubleOrNull() ?: 20.0

    val result = remember(supD, vfD, maD) {
        ElectricalFormulas.calculateLedResistor(supD, vfD, maD)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = supplyV, onValueChange = { supplyV = it }, label = "Supply Voltage", unitSuffix = "V", modifier = Modifier.weight(1f))
            NumericInputField(value = ledVf, onValueChange = { ledVf = it }, label = "LED Forward Voltage (Vf)", unitSuffix = "V", modifier = Modifier.weight(1f))
        }
        NumericInputField(value = currentMa, onValueChange = { currentMa = it }, label = "LED Forward Current", unitSuffix = "mA")

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "LED Series Resistor",
                inputSummary = "Vs = $supplyV V, Vf = $ledVf V, I = $currentMa mA",
                onSave = { res ->
                    viewModel.saveCalculation("led_resistor", "LED Resistor (${res.primaryValue} Ω)", "Vs = $supplyV V, Vf = $ledVf V, I = $currentMa mA", "Resistor: ${res.primaryValue} Ω | Power: ${res.secondaryValues["Recommended Resistor Wattage"] ?: ""}", res.formula)
                },
                onReset = { supplyV = "12"; ledVf = "3.2"; currentMa = "20" }
            )
        }
    }
}

@Composable
fun EnergyCostContent(viewModel: ElectricianViewModel) {
    var watts by remember { mutableStateOf("1500") }
    var hoursPerDay by remember { mutableStateOf("6") }
    var tariff by remember { mutableStateOf("0.16") }

    val wD = watts.toDoubleOrNull() ?: 0.0
    val hD = hoursPerDay.toDoubleOrNull() ?: 6.0
    val tD = tariff.toDoubleOrNull() ?: 0.16

    val result = remember(wD, hD, tD) {
        ElectricalFormulas.calculateEnergyCost(wD, hD, ratePerKWh = tD)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumericInputField(value = watts, onValueChange = { watts = it }, label = "Appliance Power", unitSuffix = "W", modifier = Modifier.weight(1f))
            NumericInputField(value = hoursPerDay, onValueChange = { hoursPerDay = it }, label = "Usage per Day", unitSuffix = "h/day", modifier = Modifier.weight(1f))
        }
        NumericInputField(value = tariff, onValueChange = { tariff = it }, label = "Electricity Tariff Rate", unitSuffix = "$/kWh")

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "Energy Consumption & Cost",
                inputSummary = "P = $watts W, Hours = $hoursPerDay h/d, Rate = $$tariff/kWh",
                onSave = { res ->
                    viewModel.saveCalculation("energy_cost", "Energy Cost (${res.primaryValue}/mo)", "P = $watts W, $hoursPerDay h/d", "Monthly: ${res.primaryValue} | Daily: ${res.secondaryValues["Daily Cost"] ?: ""}", res.formula)
                },
                onReset = { watts = "1500"; hoursPerDay = "6"; tariff = "0.16" }
            )
        }
    }
}

@Composable
fun AwgConversionContent(viewModel: ElectricianViewModel) {
    var selectedAwg by remember { mutableStateOf("12") }

    val awgOptions = listOf("14", "12", "10", "8", "6", "4", "2", "1/0", "2/0", "3/0", "4/0")
    val result = remember(selectedAwg) {
        ElectricalFormulas.convertAwgToMm2(selectedAwg)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Select American Wire Gauge (AWG):", fontWeight = FontWeight.SemiBold)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(awgOptions) { awg ->
                FilterChip(
                    selected = selectedAwg == awg,
                    onClick = { selectedAwg = awg },
                    label = { Text("$awg AWG") }
                )
            }
        }

        if (result.isSuccess) {
            ResultCard(
                result = result,
                calculatorTitle = "AWG to mm² Conversion",
                inputSummary = "Gauge = $selectedAwg AWG",
                onSave = { res ->
                    viewModel.saveCalculation("awg_conversion", "$selectedAwg AWG = ${res.primaryValue} mm²", "Gauge: $selectedAwg AWG", "Area: ${res.primaryValue} mm² | Diameter: ${res.secondaryValues["Conductor Diameter"] ?: ""}", res.formula)
                },
                onReset = { selectedAwg = "12" }
            )
        }
    }
}
