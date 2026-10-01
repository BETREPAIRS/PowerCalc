package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.ElectricianViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun SettingsScreen(
    viewModel: ElectricianViewModel,
    modifier: Modifier = Modifier
) {
    val defaultV by viewModel.defaultVoltage.collectAsState()
    val defaultFreq by viewModel.defaultFrequency.collectAsState()
    val defaultStd by viewModel.defaultStandard.collectAsState()
    val isDarkTheme by viewModel.darkTheme.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "APPLICATION SETTINGS",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Configure regional electrical standards & default calculation parameters",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Standard Selector
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Electrical Code / Standard", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    Text("Governs ampacity, continuous load factors, and conductor tables", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("IEC", "NEC", "BS").forEach { std ->
                            ThreeDChip(
                                selected = defaultStd == std,
                                onClick = { viewModel.setDefaultStandard(std) },
                                label = when (std) {
                                    "IEC" -> "IEC 60364"
                                    "NEC" -> "NFPA 70 / NEC"
                                    else -> "BS 7671"
                                },
                                modifier = Modifier.testTag("std_chip_$std")
                            )
                        }
                    }
                }
            }
        }

        // System Voltage Default
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Default Supply Voltage", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    Text("Select standard voltage or enter custom value", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    val voltageOptions = listOf(110.0, 220.0, 380.0, 690.0)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        voltageOptions.forEach { v ->
                            ThreeDChip(
                                selected = defaultV == v,
                                onClick = { viewModel.setDefaultVoltage(v) },
                                label = "${v.toInt()} V",
                                modifier = Modifier.testTag("voltage_chip_${v.toInt()}")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    var customVoltageText by remember(defaultV) {
                        mutableStateOf(if (defaultV % 1.0 == 0.0) defaultV.toInt().toString() else defaultV.toString())
                    }

                    OutlinedTextField(
                        value = customVoltageText,
                        onValueChange = { newVal ->
                            customVoltageText = newVal
                            newVal.toDoubleOrNull()?.let { v ->
                                if (v > 0) {
                                    viewModel.setDefaultVoltage(v)
                                }
                            }
                        },
                        label = { Text("Custom Voltage (V)") },
                        trailingIcon = {
                            Text(
                                text = "V",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                        },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // AC Frequency Default
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("AC Grid Frequency", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf(50.0, 60.0).forEach { f ->
                            ThreeDChip(
                                selected = defaultFreq == f,
                                onClick = { viewModel.setDefaultFrequency(f) },
                                label = "${f.toInt()} Hz",
                                modifier = Modifier.testTag("freq_chip_${f.toInt()}")
                            )
                        }
                    }
                }
            }
        }

        // Display Theme
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Dark Mode", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Text("Enable dark theme for dim panel rooms (Light mode by default)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { viewModel.setDarkTheme(it) }
                    )
                }
            }
        }

        // About & Safety
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.powercalc_emblem),
                            contentDescription = "PowerCalc Logo",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("PowerCalc", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                            Text("Version 1.0.0 (Production Field Edition)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Developed for master electricians, electrical engineers, technicians, and apprentices. Complies with international engineering formulations (IEC, IEEE, NEC).",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Safety Notice: Calculations provide theoretical estimates. Always inspect local jobsite conditions, conduit groupings, derating factors, and use calibrated test instruments before energizing.",
                        style = MaterialTheme.typography.bodySmall,
                        color = HighVisOrange,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
