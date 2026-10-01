package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BadgeType
import com.example.data.ElectricalCatalogItem

@Composable
fun CalcItemBadge(
    item: ElectricalCatalogItem,
    modifier: Modifier = Modifier
) {
    val badgeColor = Color(item.badgeColor)

    Box(
        modifier = modifier
            .size(40.dp),
        contentAlignment = Alignment.Center
    ) {
        when (item.badgeType) {
            BadgeType.TEXT_CIRCLE -> {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(badgeColor),
                    contentAlignment = Alignment.Center
                ) {
                    val fontSize = when {
                        item.badgeText.length >= 4 -> 10.sp
                        item.badgeText.length == 3 -> 12.sp
                        else -> 14.sp
                    }
                    Text(
                        text = item.badgeText,
                        color = Color.White,
                        fontSize = fontSize,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            BadgeType.BOOK_FORMULA -> {
                // Red open book with white f(x)
                Box(
                    modifier = Modifier
                        .width(36.dp)
                        .height(38.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFB71C1C), Color(0xFFD32F2F), Color(0xFFE53935))
                            )
                        )
                        .border(1.dp, Color(0xFFFFCDD2).copy(alpha = 0.5f), RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "f(x)",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Serif
                    )
                }
            }

            BadgeType.SQUARE_SYMBOL -> {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeColor),
                    contentAlignment = Alignment.Center
                ) {
                    val fontSize = if (item.badgeText.length > 3) 10.sp else 13.sp
                    Text(
                        text = item.badgeText,
                        color = Color.White,
                        fontSize = fontSize,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            BadgeType.CABLE_3CORE -> {
                // 3-core cable cross-section (green-yellow, blue, brown)
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF263238))
                        .border(1.5.dp, Color(0xFF78909C), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Top core: Green-Yellow Ground
                        Box(
                            modifier = Modifier
                                .size(11.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF43A047))
                                .border(1.dp, Color(0xFFFFEB3B), CircleShape)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            // Left core: Neutral Blue
                            Box(
                                modifier = Modifier
                                    .size(11.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF1976D2))
                            )
                            // Right core: Phase Brown
                            Box(
                                modifier = Modifier
                                    .size(11.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF6D4C41))
                            )
                        }
                    }
                }
            }

            BadgeType.SCHEMATIC_SYMBOL -> {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeColor.copy(alpha = 0.2f))
                        .border(1.dp, badgeColor.copy(alpha = 0.6f), RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.badgeText,
                        color = badgeColor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            BadgeType.CHIP_RESISTOR -> {
                // SMD resistor 102
                Box(
                    modifier = Modifier
                        .width(36.dp)
                        .height(24.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFF212121))
                        .border(1.5.dp, Color(0xFFB0BEC5), RoundedCornerShape(2.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.badgeText,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            BadgeType.CAPACITOR_DISK -> {
                // Orange ceramic disk capacitor
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF8A65))
                        .border(1.dp, Color(0xFFD84315), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.badgeText,
                        color = Color.Black,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            BadgeType.CONNECTOR_BADGE -> {
                // Connector emblem
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(badgeColor.copy(alpha = 0.25f))
                        .border(1.dp, badgeColor.copy(alpha = 0.6f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.badgeText,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            BadgeType.ICON_SYMBOL -> {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(badgeColor.copy(alpha = 0.22f))
                        .border(1.dp, badgeColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.badgeText,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
