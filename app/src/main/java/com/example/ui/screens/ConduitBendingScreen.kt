package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.ConduitBendingEngine
import com.example.ui.ElectricianViewModel
import com.example.ui.components.*
import com.example.ui.theme.AppBgLight
import com.example.ui.theme.AppBorderLight
import com.example.ui.theme.AppTextDark
import com.example.ui.theme.AppTextMuted
import com.example.ui.theme.BlueprintBlue

data class BendCalcItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val iconType: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConduitBendingScreen(
    viewModel: ElectricianViewModel,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var activeBendCalc by remember { mutableStateOf<String?>(null) }

    val allBends = remember {
        listOf(
            BendCalcItem(
                id = "rolling_offset",
                title = "Rolling Offset",
                subtitle = "Calculate an offset changing both vertical and horizontal position",
                iconType = "rolling"
            ),
            BendCalcItem(
                id = "parallel_offset",
                title = "Parallel Offset",
                subtitle = "Calculate the starting length adjustment for bending parallel offsets based on spacing",
                iconType = "parallel"
            ),
            BendCalcItem(
                id = "matching_centers",
                title = "Matching Centers Offset",
                subtitle = "Calculate offset parameters using adjacent distance and rise.",
                iconType = "matching_centers"
            ),
            BendCalcItem(
                id = "matching_bends",
                title = "Matching Bends Offset",
                subtitle = "Calculate the bend angle and shrink for an existing offset",
                iconType = "matching_bends"
            ),
            BendCalcItem(
                id = "three_point_saddle",
                title = "Three Point Saddle",
                subtitle = "Calculate bend marks for a three-point saddle to cross over an obstruction",
                iconType = "three_point"
            )
        )
    }

    val filteredBends = remember(searchQuery) {
        if (searchQuery.isBlank()) allBends
        else allBends.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
            it.subtitle.contains(searchQuery, ignoreCase = true)
        }
    }

    // Detail Sheet if one is active
    if (activeBendCalc != null) {
        BendCalculatorDetail(
            bendId = activeBendCalc!!,
            onBack = { activeBendCalc = null },
            viewModel = viewModel
        )
        return
    }

    // Main List View matching the user's reference image
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBgLight)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Top Navigation Bar: < Back on left, Conduit Bending in center
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            // < Back Button
            Row(
                modifier = Modifier
                    .align(Alignment.CenterStart)
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

            // Screen Title (Centered)
            Text(
                text = "Conduit Bending",
                color = BlueprintBlue,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar matching screenshot (Rounded white box with light border and blue search icon)
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
                    onValueChange = { searchQuery = it },
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
                        .testTag("bend_search_input")
                )
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { searchQuery = "" },
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

        Spacer(modifier = Modifier.height(16.dp))

        // Section Title: "Bend Calculators"
        Text(
            text = "Bend Calculators",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = AppTextDark
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Cards List matching the screenshot
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 80.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredBends, key = { it.id }) { item ->
                ConduitCardItem(
                    item = item,
                    onClick = { activeBendCalc = item.id }
                )
            }
        }
    }
}

@Composable
fun ConduitCardItem(
    item: BendCalcItem,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
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
            .testTag("bend_card_${item.id}"),
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
            // Left Icon Container (Light blue square with custom conduit diagram)
            ConduitIconContainer {
                when (item.iconType) {
                    "rolling" -> RollingOffsetIcon()
                    "parallel" -> ParallelOffsetIcon()
                    "matching_centers" -> MatchingCentersIcon()
                    "matching_bends" -> MatchingBendsIcon()
                    "three_point" -> ThreePointSaddleIcon()
                    else -> RollingOffsetIcon()
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Text Info: Bold Blue Title + Slate Subtitle
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    fontSize = 15.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = BlueprintBlue
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = item.subtitle,
                    fontSize = 12.sp,
                    color = AppTextMuted,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

// =============================================================================
// Detail Calculator Component for Conduit Bends
// =============================================================================
@Composable
fun BendCalculatorDetail(
    bendId: String,
    onBack: () -> Unit,
    viewModel: ElectricianViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBgLight)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
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

            Text(
                text = when (bendId) {
                    "rolling_offset" -> "Rolling Offset"
                    "parallel_offset" -> "Parallel Offset"
                    "matching_centers" -> "Matching Centers Offset"
                    "matching_bends" -> "Matching Bends Offset"
                    "three_point_saddle" -> "Three Point Saddle"
                    else -> "Conduit Bend"
                },
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = BlueprintBlue
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 80.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            when (bendId) {
                "rolling_offset" -> item { RollingOffsetCalculator(viewModel) }
                "parallel_offset" -> item { ParallelOffsetCalculator(viewModel) }
                "matching_centers" -> item { MatchingCentersCalculator(viewModel) }
                "matching_bends" -> item { MatchingBendsCalculator(viewModel) }
                "three_point_saddle" -> item { ThreePointSaddleCalculator(viewModel) }
            }
        }
    }
}

// 1. Rolling Offset Calculator
@Composable
fun RollingOffsetCalculator(viewModel: ElectricianViewModel) {
    var riseText by remember { mutableStateOf("10.0") }
    var rollText by remember { mutableStateOf("12.0") }
    var selectedAngle by remember { mutableStateOf(30.0) }

    val rise = riseText.toDoubleOrNull() ?: 0.0
    val roll = rollText.toDoubleOrNull() ?: 0.0

    val result = remember(rise, roll, selectedAngle) {
        ConduitBendingEngine.calculateRollingOffset(rise, roll, selectedAngle)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, AppBorderLight)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ConduitIconContainer { RollingOffsetIcon() }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Rolling Offset Parameters", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AppTextDark)
                    Text("Combines vertical rise and horizontal roll", fontSize = 12.sp, color = AppTextMuted)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            NumericInputField(
                value = riseText,
                onValueChange = { riseText = it },
                label = "Vertical Rise (V)",
                unitSuffix = "in"
            )

            Spacer(modifier = Modifier.height(10.dp))

            NumericInputField(
                value = rollText,
                onValueChange = { rollText = it },
                label = "Horizontal Roll (H)",
                unitSuffix = "in"
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text("Bend Angle", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = AppTextDark)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(10.0, 22.5, 30.0, 45.0, 60.0).forEach { angle ->
                    ThreeDChip(
                        selected = selectedAngle == angle,
                        onClick = { selectedAngle = angle },
                        label = "${if (angle % 1.0 == 0.0) angle.toInt() else angle}°"
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(14.dp))

    ResultCard(
        result = result,
        calculatorTitle = "Rolling Offset",
        inputSummary = "Rise: ${rise}in, Roll: ${roll}in, Angle: ${selectedAngle}°",
        onSave = { res ->
            viewModel.saveCalculation(
                calculatorId = "rolling_offset",
                title = "Rolling Offset",
                inputSummary = "Rise: ${rise}in, Roll: ${roll}in, Angle: ${selectedAngle}°",
                resultSummary = "${res.primaryValue} ${res.primaryUnit}",
                formulaUsed = res.formula
            )
        },
        onReset = {
            riseText = "10.0"
            rollText = "12.0"
            selectedAngle = 30.0
        }
    )
}

// 2. Parallel Offset Calculator
@Composable
fun ParallelOffsetCalculator(viewModel: ElectricianViewModel) {
    var spacingText by remember { mutableStateOf("2.0") }
    var riseText by remember { mutableStateOf("6.0") }
    var selectedAngle by remember { mutableStateOf(30.0) }

    val spacing = spacingText.toDoubleOrNull() ?: 0.0
    val rise = riseText.toDoubleOrNull() ?: 0.0

    val result = remember(spacing, selectedAngle, rise) {
        ConduitBendingEngine.calculateParallelOffset(spacing, selectedAngle, rise)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, AppBorderLight)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ConduitIconContainer { ParallelOffsetIcon() }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Parallel Offset Parameters", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AppTextDark)
                    Text("Equal spacing around bends without binding", fontSize = 12.sp, color = AppTextMuted)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            NumericInputField(
                value = spacingText,
                onValueChange = { spacingText = it },
                label = "Conduit Center-to-Center Spacing",
                unitSuffix = "in"
            )

            Spacer(modifier = Modifier.height(10.dp))

            NumericInputField(
                value = riseText,
                onValueChange = { riseText = it },
                label = "Offset Rise (R)",
                unitSuffix = "in"
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text("Bend Angle", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = AppTextDark)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(15.0, 22.5, 30.0, 45.0).forEach { angle ->
                    ThreeDChip(
                        selected = selectedAngle == angle,
                        onClick = { selectedAngle = angle },
                        label = "${if (angle % 1.0 == 0.0) angle.toInt() else angle}°"
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(14.dp))

    ResultCard(
        result = result,
        calculatorTitle = "Parallel Offset",
        inputSummary = "Spacing: ${spacing}in, Rise: ${rise}in, Angle: ${selectedAngle}°",
        onSave = { res ->
            viewModel.saveCalculation(
                calculatorId = "parallel_offset",
                title = "Parallel Offset",
                inputSummary = "Spacing: ${spacing}in, Rise: ${rise}in, Angle: ${selectedAngle}°",
                resultSummary = "${res.primaryValue} ${res.primaryUnit}",
                formulaUsed = res.formula
            )
        },
        onReset = {
            spacingText = "2.0"
            riseText = "6.0"
            selectedAngle = 30.0
        }
    )
}

// 3. Matching Centers Offset Calculator
@Composable
fun MatchingCentersCalculator(viewModel: ElectricianViewModel) {
    var riseText by remember { mutableStateOf("6.0") }
    var adjacentText by remember { mutableStateOf("10.0") }

    val rise = riseText.toDoubleOrNull() ?: 0.0
    val adjacent = adjacentText.toDoubleOrNull() ?: 0.0

    val result = remember(rise, adjacent) {
        ConduitBendingEngine.calculateMatchingCenters(rise, adjacent)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, AppBorderLight)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ConduitIconContainer { MatchingCentersIcon() }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Matching Centers Parameters", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AppTextDark)
                    Text("Finds bend angle from fixed rise & available run", fontSize = 12.sp, color = AppTextMuted)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            NumericInputField(
                value = riseText,
                onValueChange = { riseText = it },
                label = "Obstacle Rise (R)",
                unitSuffix = "in"
            )

            Spacer(modifier = Modifier.height(10.dp))

            NumericInputField(
                value = adjacentText,
                onValueChange = { adjacentText = it },
                label = "Available Adjacent Run (A)",
                unitSuffix = "in"
            )
        }
    }

    Spacer(modifier = Modifier.height(14.dp))

    ResultCard(
        result = result,
        calculatorTitle = "Matching Centers Offset",
        inputSummary = "Rise: ${rise}in, Run: ${adjacent}in",
        onSave = { res ->
            viewModel.saveCalculation(
                calculatorId = "matching_centers",
                title = "Matching Centers Offset",
                inputSummary = "Rise: ${rise}in, Run: ${adjacent}in",
                resultSummary = "${res.primaryValue} ${res.primaryUnit}",
                formulaUsed = res.formula
            )
        },
        onReset = {
            riseText = "6.0"
            adjacentText = "10.0"
        }
    )
}

// 4. Matching Bends Offset Calculator
@Composable
fun MatchingBendsCalculator(viewModel: ElectricianViewModel) {
    var distanceText by remember { mutableStateOf("12.0") }
    var riseText by remember { mutableStateOf("6.0") }

    val distance = distanceText.toDoubleOrNull() ?: 0.0
    val rise = riseText.toDoubleOrNull() ?: 0.0

    val result = remember(distance, rise) {
        ConduitBendingEngine.calculateMatchingBends(distance, rise)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, AppBorderLight)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ConduitIconContainer { MatchingBendsIcon() }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Matching Bends Parameters", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AppTextDark)
                    Text("Replicates an existing field offset", fontSize = 12.sp, color = AppTextMuted)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            NumericInputField(
                value = distanceText,
                onValueChange = { distanceText = it },
                label = "Distance Between Existing Marks",
                unitSuffix = "in"
            )

            Spacer(modifier = Modifier.height(10.dp))

            NumericInputField(
                value = riseText,
                onValueChange = { riseText = it },
                label = "Measured Rise",
                unitSuffix = "in"
            )
        }
    }

    Spacer(modifier = Modifier.height(14.dp))

    ResultCard(
        result = result,
        calculatorTitle = "Matching Bends Offset",
        inputSummary = "Distance: ${distance}in, Rise: ${rise}in",
        onSave = { res ->
            viewModel.saveCalculation(
                calculatorId = "matching_bends",
                title = "Matching Bends Offset",
                inputSummary = "Distance: ${distance}in, Rise: ${rise}in",
                resultSummary = "${res.primaryValue} ${res.primaryUnit}",
                formulaUsed = res.formula
            )
        },
        onReset = {
            distanceText = "12.0"
            riseText = "6.0"
        }
    )
}

// 5. Three-Point Saddle Calculator
@Composable
fun ThreePointSaddleCalculator(viewModel: ElectricianViewModel) {
    var heightText by remember { mutableStateOf("2.5") }
    var centerAngle by remember { mutableStateOf(45.0) }

    val height = heightText.toDoubleOrNull() ?: 0.0

    val result = remember(height, centerAngle) {
        ConduitBendingEngine.calculateThreePointSaddle(height, centerAngle)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, AppBorderLight)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ConduitIconContainer { ThreePointSaddleIcon() }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Three-Point Saddle Parameters", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AppTextDark)
                    Text("Bridges conduit over round/square obstruction", fontSize = 12.sp, color = AppTextMuted)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            NumericInputField(
                value = heightText,
                onValueChange = { heightText = it },
                label = "Obstacle Height / Rise (H)",
                unitSuffix = "in"
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text("Center Bend Angle", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = AppTextDark)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(30.0, 45.0, 60.0).forEach { angle ->
                    ThreeDChip(
                        selected = centerAngle == angle,
                        onClick = { centerAngle = angle },
                        label = "${angle.toInt()}° Center"
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(14.dp))

    ResultCard(
        result = result,
        calculatorTitle = "Three-Point Saddle",
        inputSummary = "Height: ${height}in, Center Angle: ${centerAngle}°",
        onSave = { res ->
            viewModel.saveCalculation(
                calculatorId = "three_point_saddle",
                title = "Three-Point Saddle",
                inputSummary = "Height: ${height}in, Center Angle: ${centerAngle}°",
                resultSummary = "${res.primaryValue} ${res.primaryUnit}",
                formulaUsed = res.formula
            )
        },
        onReset = {
            heightText = "2.5"
            centerAngle = 45.0
        }
    )
}
