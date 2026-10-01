package com.example

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ElectricianViewModel
import com.example.ui.MainNavTab
import com.example.ui.components.ThreeDButton
import com.example.ui.screens.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {

    private val viewModel: ElectricianViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Initialize GMA Next-Gen SDK on background thread
        com.example.ads.AdMobManager.initialize(this)
        setContent {
            val isDarkTheme by viewModel.darkTheme.collectAsState()

            PowerCalcTheme(darkTheme = isDarkTheme) {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppScreen(viewModel: ElectricianViewModel) {
    val context = LocalContext.current
    val currentTab by viewModel.currentTab.collectAsState()
    val activeCalcId by viewModel.activeCalculatorId.collectAsState()
    var showCloseAppDialog by remember { mutableStateOf(false) }

    // Intercept hardware and gesture back button
    BackHandler {
        if (showCloseAppDialog) {
            showCloseAppDialog = false
        } else if (activeCalcId != null) {
            com.example.ads.AdMobManager.showInterstitial(context as? Activity) {
                viewModel.closeCalculator()
            }
        } else if (currentTab != MainNavTab.HOME) {
            // Navigate back to previous screen or home page
            viewModel.navigateBack()
        } else {
            // On Home page root -> Prompt confirmation dialogue to close application
            showCloseAppDialog = true
        }
    }

    if (showCloseAppDialog) {
        CloseApplicationDialog(
            onConfirm = {
                showCloseAppDialog = false
                (context as? Activity)?.finish()
            },
            onDismiss = {
                showCloseAppDialog = false
            }
        )
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("app_scaffold"),
        contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top),
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // AdMob Banner Test Ad anchored above bottom navigation bar
                com.example.ads.AdMobManager.BannerAd(modifier = Modifier.fillMaxWidth())
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("main_navigation_bar"),
                    color = Color.White,
                    shadowElevation = 8.dp,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(NavigationBarDefaults.windowInsets)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PowerCalcNavItem(
                        selected = currentTab == MainNavTab.HOME,
                        onClick = { viewModel.setTab(MainNavTab.HOME) },
                        icon = Icons.Outlined.Home,
                        selectedIcon = Icons.Filled.Home,
                        label = "Home",
                        testTag = "nav_item_home"
                    )

                    PowerCalcNavItem(
                        selected = currentTab == MainNavTab.CALCULATORS,
                        onClick = { viewModel.setTab(MainNavTab.CALCULATORS) },
                        icon = Icons.Outlined.Calculate,
                        selectedIcon = Icons.Filled.Calculate,
                        label = "Calculator",
                        testTag = "nav_item_calculators"
                    )

                    PowerCalcNavItem(
                        selected = currentTab == MainNavTab.TOOLS,
                        onClick = { viewModel.setTab(MainNavTab.TOOLS) },
                        icon = Icons.Outlined.Build,
                        selectedIcon = Icons.Filled.Build,
                        label = "Tools",
                        testTag = "nav_item_tools"
                    )

                    PowerCalcNavItem(
                        selected = currentTab == MainNavTab.SETTINGS,
                        onClick = { viewModel.setTab(MainNavTab.SETTINGS) },
                        icon = Icons.Outlined.Settings,
                        selectedIcon = Icons.Filled.Settings,
                        label = "Settings",
                        testTag = "nav_item_settings"
                    )
                }
            }
        }
    }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
        ) {
            when (currentTab) {
                MainNavTab.HOME -> HomeScreen(viewModel = viewModel)
                MainNavTab.CALCULATORS -> CalculatorsScreen(viewModel = viewModel)
                MainNavTab.TOOLS -> FieldToolsScreen(viewModel = viewModel)
                MainNavTab.SETTINGS -> SettingsScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun RowScope.PowerCalcNavItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    testTag: String
) {
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val animatedScale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1.0f,
        animationSpec = androidx.compose.animation.core.spring(stiffness = androidx.compose.animation.core.Spring.StiffnessMediumLow),
        label = "navScale"
    )

    val activeColor = Color(0xFFD97706)
    val inactiveColor = Color(0xFF64748B)

    Box(
        modifier = Modifier
            .weight(1f)
            .testTag(testTag)
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Rounded active pill background matching the screenshot
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = if (selected) Color(0xFFFFEDD5) else Color.Transparent
            ) {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)) {
                    Icon(
                        imageVector = if (selected) selectedIcon else icon,
                        contentDescription = label,
                        tint = if (selected) activeColor else inactiveColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = label,
                fontSize = 11.5.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) activeColor else inactiveColor
            )
        }
    }
}

@Composable
fun CloseApplicationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("close_app_dialog"),
        icon = {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE53935).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Exit App",
                    tint = Color(0xFFE53935),
                    modifier = Modifier.size(30.dp)
                )
            }
        },
        title = {
            Text(
                text = "Close The Application",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().testTag("close_app_dialog_title")
            )
        },
        text = {
            Text(
                text = "Are you sure you want to close PowerCalc?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().testTag("close_app_dialog_text")
            )
        },
        confirmButton = {
            ThreeDButton(
                onClick = onConfirm,
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .testTag("close_app_yes_button"),
                baseColor = Color(0xFFE53935),
                contentColor = Color.White,
                depthColor = Color(0xFFB71C1C)
            ) {
                Text("Yes", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .testTag("close_app_no_button")
            ) {
                Text(
                    text = "No",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(20.dp)
    )
}

