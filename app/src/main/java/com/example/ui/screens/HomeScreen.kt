package com.example.ui.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ads.AdMobManager
import com.example.ui.ElectricianViewModel
import com.example.ui.MainNavTab

// Quick Calculator Item Data
data class QuickCalcItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val iconType: String
)

// Category Item Data
data class HomeCategoryItem(
    val name: String,
    val iconType: String
)

@Composable
fun HomeScreen(
    viewModel: ElectricianViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var showProDialog by remember { mutableStateOf(false) }

    val quickCalculators = remember {
        listOf(
            QuickCalcItem("cable_size", "Cable Sizing", "Ampacity & drop", "lightning"),
            QuickCalcItem("voltage_drop", "Voltage Drop", "Max run distance", "graph"),
            QuickCalcItem("breaker_size", "Breaker Sizing", "125% rule & curves", "breaker"),
            QuickCalcItem("motor_current", "Motor Current", "FLC & starting", "motor"),
            QuickCalcItem("pf_correction", "Power Factor", "Capacitor kVAR", "cosphi"),
            QuickCalcItem("conduit_tray_fill", "Conduit Fill", "NEC 40% rule", "conduit")
        )
    }

    val categories = remember {
        listOf(
            HomeCategoryItem("Cable & Wiring", "lightning"),
            HomeCategoryItem("Basic Electrical", "omega"),
            HomeCategoryItem("Power & Circuits", "plug"),
            HomeCategoryItem("Power Factor", "cosphi"),
            HomeCategoryItem("Protection", "breaker"),
            HomeCategoryItem("Motors", "motor"),
            HomeCategoryItem("Transformers", "transformer"),
            HomeCategoryItem("Batteries", "battery"),
            HomeCategoryItem("Solar/PV", "solar"),
            HomeCategoryItem("Lighting", "lighting"),
            HomeCategoryItem("Industrial", "gear"),
            HomeCategoryItem("Components", "components")
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .verticalScroll(scrollState)
            .padding(bottom = 24.dp)
            .testTag("home_screen_content")
    ) {
        // Top Space
        Spacer(modifier = Modifier.height(10.dp))

        // 1. TOP HERO HEADER CARD (Matching reference image)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .testTag("home_hero_card"),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2E)),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Top row with Emblem, Title & Info badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Circular Emblem
                    Surface(
                        modifier = Modifier.size(42.dp),
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 2.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Image(
                                painter = painterResource(id = R.drawable.powercalc_emblem),
                                contentDescription = "PowerCalc Emblem",
                                modifier = Modifier.size(34.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Title & Subtitle column
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "POWERCALC",
                            color = Color(0xFFF59E0B),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(1.dp))
                        Text(
                            text = "Standard: IEC • Offline Ready • Engineering Suite",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Top-right action badge (Ad-Free / Pro mode indicator)
                    Surface(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { showProDialog = true }
                            .testTag("home_ad_free_badge"),
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White.copy(alpha = 0.12f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.Campaign,
                                contentDescription = "Ad-free / Info",
                                tint = Color(0xFFCBD5E1),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Main Headline
                Text(
                    text = "Electrician's Field Companion",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.3).sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Description
                Text(
                    text = "Engineering calculations, cable ampacity, motor diagnostics & load schedules.",
                    color = Color(0xFF94A3B8),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 2. FIELD TOOLBOX & UTILITIES SECTION (Matching reference image)
        Text(
            text = "FIELD TOOLBOX & UTILITIES",
            color = Color(0xFFD97706),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 3 Pill Buttons Row: Manuals, Converter, Reference
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Button 1: Manuals (Solid Amber)
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { viewModel.openManuals() }
                    .testTag("home_btn_manuals"),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFFFA000),
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📒",
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Manuals",
                        color = Color(0xFF1E1E1E),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            // Button 2: Converter (White Card)
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { viewModel.openConverter() }
                    .testTag("home_btn_converter"),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shadowElevation = 0.5.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Bolt,
                        contentDescription = "Converter",
                        tint = Color(0xFFFFA000),
                        modifier = Modifier.size(19.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Converter",
                        color = Color(0xFF0F172A),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }

            // Button 3: Reference (White Card)
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { viewModel.openReference() }
                    .testTag("home_btn_reference"),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shadowElevation = 0.5.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📖",
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Reference",
                        color = Color(0xFF0F172A),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // 3. QUICK CALCULATORS SECTION (Matching reference image)
        Text(
            text = "QUICK CALCULATORS",
            color = Color(0xFFD97706),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(quickCalculators, key = { it.id }) { item ->
                QuickCalcCard(
                    item = item,
                    onClick = {
                        AdMobManager.showInterstitial(context as? Activity) {
                            viewModel.openCalculator(item.id)
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 4. CALCULATOR CATEGORIES SECTION (Matching reference image)
        Text(
            text = "CALCULATOR CATEGORIES",
            color = Color(0xFFD97706),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 2-Column Grid of Categories
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val chunked = categories.chunked(2)
            chunked.forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowItems.forEach { catItem ->
                        CategoryGridCard(
                            item = catItem,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                viewModel.setCategoryFilter(catItem.name)
                                viewModel.setTab(MainNavTab.CALCULATORS)
                            }
                        )
                    }
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }

    if (showProDialog) {
        AlertDialog(
            onDismissRequest = { showProDialog = false },
            title = {
                Text(
                    text = "PowerCalc Pro Suite",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Complete Electrician & Electrical Engineer Toolkit with 35+ verified calculations.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Standard: IEC / NEC compliant\n• 100% Offline calculation engine\n• Printable PDF manuals & field tables\n• Unit conversion & reference data",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showProDialog = false }) {
                    Text("OK", fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                }
            }
        )
    }
}

@Composable
fun QuickCalcCard(
    item: QuickCalcItem,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Surface(
        modifier = Modifier
            .width(152.dp)
            .height(118.dp)
            .graphicsLayer {
                val scale = if (isPressed) 0.96f else 1.0f
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(),
                onClick = onClick
            )
            .testTag("quick_calc_${item.id}"),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shadowElevation = 0.5.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Icon
            Box(
                modifier = Modifier.size(34.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                when (item.iconType) {
                    "lightning" -> {
                        Icon(
                            imageVector = Icons.Filled.Bolt,
                            contentDescription = item.title,
                            tint = Color(0xFFFFA000),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    "graph" -> {
                        VoltageDropGraphIcon()
                    }
                    "breaker" -> {
                        BreakerSymbolIcon()
                    }
                    "motor" -> {
                        MotorSymbolIcon()
                    }
                    "cosphi" -> {
                        Text(
                            text = "cosφ",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = Color(0xFF0F172A)
                        )
                    }
                    else -> {
                        Icon(
                            imageVector = Icons.Filled.Cable,
                            contentDescription = item.title,
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Title & Subtitle
            Column {
                Text(
                    text = item.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.5.sp,
                    color = Color(0xFF0F172A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.subtitle,
                    fontSize = 11.5.sp,
                    color = Color(0xFF64748B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun CategoryGridCard(
    item: HomeCategoryItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Surface(
        modifier = modifier
            .height(58.dp)
            .graphicsLayer {
                val scale = if (isPressed) 0.97f else 1.0f
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(),
                onClick = onClick
            )
            .testTag("cat_card_${item.name.replace(" ", "_")}"),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shadowElevation = 0.5.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Icon/Symbol
            Box(
                modifier = Modifier.size(26.dp),
                contentAlignment = Alignment.Center
            ) {
                when (item.iconType) {
                    "lightning" -> {
                        Icon(
                            imageVector = Icons.Filled.Bolt,
                            contentDescription = item.name,
                            tint = Color(0xFFFFA000),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    "omega" -> {
                        Text(
                            text = "Ω",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF0F172A)
                        )
                    }
                    "plug" -> {
                        Icon(
                            imageVector = Icons.Filled.Power,
                            contentDescription = item.name,
                            tint = Color(0xFF334155),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    "cosphi" -> {
                        Text(
                            text = "cosφ",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF0F172A)
                        )
                    }
                    "breaker" -> {
                        BreakerSymbolIcon()
                    }
                    "motor" -> {
                        MotorSymbolIcon()
                    }
                    "transformer" -> {
                        TransformerSymbolIcon()
                    }
                    "battery" -> {
                        Text(text = "🔋", fontSize = 17.sp)
                    }
                    "solar" -> {
                        Text(text = "☀️", fontSize = 17.sp)
                    }
                    "lighting" -> {
                        Text(text = "💡", fontSize = 17.sp)
                    }
                    "gear" -> {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = item.name,
                            tint = Color(0xFF334155),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    "components" -> {
                        Icon(
                            imageVector = Icons.Outlined.Memory,
                            contentDescription = item.name,
                            tint = Color(0xFF334155),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    else -> {
                        Icon(
                            imageVector = Icons.Outlined.Folder,
                            contentDescription = item.name,
                            tint = Color(0xFF334155),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = item.name,
                fontWeight = FontWeight.Bold,
                fontSize = 13.5.sp,
                color = Color(0xFF0F172A),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// Custom crisp Voltage Drop Grid Chart Icon matching the reference image
@Composable
fun VoltageDropGraphIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(28.dp)) {
        val w = size.width
        val h = size.height

        // Background light blue grid lines
        val gridColor = Color(0xFFBAE6FD)
        val strokeGrid = 1.dp.toPx()

        // 3 horizontal grid lines
        drawLine(gridColor, Offset(0f, 0f), Offset(w, 0f), strokeWidth = strokeGrid)
        drawLine(gridColor, Offset(0f, h * 0.5f), Offset(w, h * 0.5f), strokeWidth = strokeGrid)
        drawLine(gridColor, Offset(0f, h), Offset(w, h), strokeWidth = strokeGrid)

        // 3 vertical grid lines
        drawLine(gridColor, Offset(0f, 0f), Offset(0f, h), strokeWidth = strokeGrid)
        drawLine(gridColor, Offset(w * 0.5f, 0f), Offset(w * 0.5f, h), strokeWidth = strokeGrid)
        drawLine(gridColor, Offset(w, 0f), Offset(w, h), strokeWidth = strokeGrid)

        // Zig-zag / trending chart line
        val path = Path().apply {
            moveTo(0f, h * 0.3f)
            lineTo(w * 0.35f, h * 0.25f)
            lineTo(w * 0.6f, h * 0.7f)
            lineTo(w * 0.85f, h * 0.45f)
            lineTo(w, h * 0.55f)
        }

        drawPath(
            path = path,
            color = Color(0xFF0284C7),
            style = Stroke(
                width = 2.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}

// Circuit Breaker Symbol Icon
@Composable
fun BreakerSymbolIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(24.dp)) {
        val w = size.width
        val h = size.height
        val stroke = 2.2.dp.toPx()
        val color = Color(0xFF1E293B)

        // Horizontal connecting lines and vertical contacts "H"
        drawLine(color, Offset(0f, h * 0.5f), Offset(w * 0.25f, h * 0.5f), strokeWidth = stroke)
        drawLine(color, Offset(w * 0.25f, h * 0.25f), Offset(w * 0.25f, h * 0.75f), strokeWidth = stroke)

        drawLine(color, Offset(w * 0.75f, h * 0.25f), Offset(w * 0.75f, h * 0.75f), strokeWidth = stroke)
        drawLine(color, Offset(w * 0.75f, h * 0.5f), Offset(w, h * 0.5f), strokeWidth = stroke)

        // Connecting bar / cross-line
        drawLine(color, Offset(w * 0.25f, h * 0.5f), Offset(w * 0.75f, h * 0.5f), strokeWidth = stroke)
    }
}

// Motor Symbol Icon: Enclosed "M"
@Composable
fun MotorSymbolIcon(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.size(22.dp),
        shape = CircleShape,
        color = Color.Transparent,
        border = BorderStroke(1.8.dp, Color(0xFF1E293B))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = "M",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 11.5.sp,
                color = Color(0xFF1E293B),
                textAlign = TextAlign.Center
            )
        }
    }
}

// Transformer Symbol Icon: Two overlapping squares
@Composable
fun TransformerSymbolIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(22.dp)) {
        val stroke = 1.8.dp.toPx()
        val color = Color(0xFF1E293B)

        // Box 1 (upper-left)
        drawRect(
            color = color,
            topLeft = Offset(0f, 0f),
            size = androidx.compose.ui.geometry.Size(size.width * 0.65f, size.height * 0.65f),
            style = Stroke(width = stroke)
        )

        // Box 2 (lower-right)
        drawRect(
            color = color,
            topLeft = Offset(size.width * 0.35f, size.height * 0.35f),
            size = androidx.compose.ui.geometry.Size(size.width * 0.65f, size.height * 0.65f),
            style = Stroke(width = stroke)
        )
    }
}
