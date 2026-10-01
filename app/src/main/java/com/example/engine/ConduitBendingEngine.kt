package com.example.engine

import java.util.Locale
import kotlin.math.*

object ConduitBendingEngine {

    private fun formatNum(value: Double, decimals: Int = 2): String {
        if (value.isNaN() || value.isInfinite()) return "0.0"
        return String.format(Locale.US, "%.${decimals}f", value)
    }

    // Convert decimal inches to approximate standard fraction (e.g. 5.375 -> 5 3/8")
    fun toFractionString(inches: Double): String {
        val whole = inches.toInt()
        val remainder = inches - whole
        val sixteenths = (remainder * 16.0).roundToInt()
        if (sixteenths == 0) return "$whole\""
        if (sixteenths == 16) return "${whole + 1}\""

        // Simplify fraction
        val gcd = gcd(sixteenths, 16)
        val num = sixteenths / gcd
        val den = 16 / gcd
        return if (whole > 0) "$whole $num/$den\"" else "$num/$den\""
    }

    private fun gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)

    // Multiplier for bend angles
    fun getMultiplier(angleDeg: Double): Double {
        val rad = Math.toRadians(angleDeg)
        val sinVal = sin(rad)
        return if (sinVal > 0.001) 1.0 / sinVal else 2.0
    }

    // Shrink per unit of rise: (1 - cos(theta)) / sin(theta)
    fun getShrinkPerUnit(angleDeg: Double): Double {
        val rad = Math.toRadians(angleDeg)
        val sinVal = sin(rad)
        return if (sinVal > 0.001) (1.0 - cos(rad)) / sinVal else 0.25
    }

    // 1. Rolling Offset
    fun calculateRollingOffset(
        verticalRise: Double,
        horizontalRoll: Double,
        angleDeg: Double
    ): CalculationResult {
        if (verticalRise <= 0 || horizontalRoll <= 0 || angleDeg <= 0) {
            return CalculationResult(
                primaryValue = "",
                primaryUnit = "",
                formula = "True Offset T = √(V² + H²)",
                substitution = "",
                standardNote = "Conduit Bending Standards",
                isSuccess = false,
                errorMessage = "Rise, Roll, and Bend Angle must be greater than zero."
            )
        }

        val trueOffset = sqrt(verticalRise * verticalRise + horizontalRoll * horizontalRoll)
        val multiplier = getMultiplier(angleDeg)
        val distanceBetweenBends = trueOffset * multiplier
        val shrink = trueOffset * getShrinkPerUnit(angleDeg)
        val rollAngle = Math.toDegrees(atan2(horizontalRoll, verticalRise))

        return CalculationResult(
            primaryValue = formatNum(distanceBetweenBends),
            primaryUnit = "in (Distance between marks)",
            secondaryValues = mapOf(
                "True Offset (T)" to "${formatNum(trueOffset)} in (${toFractionString(trueOffset)})",
                "Distance Between Bends (D)" to "${formatNum(distanceBetweenBends)} in (${toFractionString(distanceBetweenBends)})",
                "Total Conduit Shrink" to "${formatNum(shrink)} in (${toFractionString(shrink)})",
                "Roll Angle (Rotation)" to "${formatNum(rollAngle, 1)}°",
                "Bend Angle Selected" to "${formatNum(angleDeg, 1)}° (Multiplier: ${formatNum(multiplier, 3)})"
            ),
            formula = "T = √(V² + H²),  Distance = T × csc(θ),  Shrink = T × ((1 - cos θ) / sin θ)",
            substitution = "T = √(${formatNum(verticalRise)}² + ${formatNum(horizontalRoll)}²) = ${formatNum(trueOffset)} in\nDistance = ${formatNum(trueOffset)} × ${formatNum(multiplier, 2)} = ${formatNum(distanceBetweenBends)} in",
            steps = listOf(
                "Step 1: Calculate True Offset T = √(${formatNum(verticalRise)}² + ${formatNum(horizontalRoll)}²) = ${formatNum(trueOffset)} in",
                "Step 2: Bend Multiplier for ${formatNum(angleDeg, 0)}° is 1/sin(${formatNum(angleDeg, 0)}°) = ${formatNum(multiplier, 2)}",
                "Step 3: Distance Between Marks = ${formatNum(trueOffset)} × ${formatNum(multiplier, 2)} = ${formatNum(distanceBetweenBends)} in (${toFractionString(distanceBetweenBends)})",
                "Step 4: Rotate pipe by ${formatNum(rollAngle, 1)}° before making the second bend.",
                "Step 5: Account for total shrink of ${formatNum(shrink)} in (${toFractionString(shrink)})."
            ),
            standardNote = "NEC 358 / 344 conduit bending guidelines. Always mark from the bender arrow."
        )
    }

    // 2. Parallel Offset
    fun calculateParallelOffset(
        conduitSpacing: Double,
        angleDeg: Double,
        offsetRise: Double
    ): CalculationResult {
        if (conduitSpacing <= 0 || angleDeg <= 0 || offsetRise <= 0) {
            return CalculationResult(
                primaryValue = "",
                primaryUnit = "",
                formula = "Setback = Spacing × tan(θ / 2)",
                substitution = "",
                standardNote = "Conduit Bending Standards",
                isSuccess = false,
                errorMessage = "Spacing, Angle, and Rise must be positive values."
            )
        }

        val radHalf = Math.toRadians(angleDeg / 2.0)
        val setback = conduitSpacing * tan(radHalf)
        val multiplier = getMultiplier(angleDeg)
        val distanceBetweenBends = offsetRise * multiplier
        val shrink = offsetRise * getShrinkPerUnit(angleDeg)

        return CalculationResult(
            primaryValue = formatNum(setback),
            primaryUnit = "in (Start adjustment / Setback)",
            secondaryValues = mapOf(
                "Start Stagger (Setback)" to "${formatNum(setback)} in (${toFractionString(setback)})",
                "Distance Between Bends" to "${formatNum(distanceBetweenBends)} in (${toFractionString(distanceBetweenBends)})",
                "Shrink per Conduit" to "${formatNum(shrink)} in (${toFractionString(shrink)})",
                "Conduit Center Spacing" to "${formatNum(conduitSpacing)} in",
                "Bend Angle" to "${formatNum(angleDeg, 0)}°"
            ),
            formula = "Setback = Spacing × tan(θ / 2),  Distance = Rise × csc(θ)",
            substitution = "Setback = ${formatNum(conduitSpacing)} × tan(${formatNum(angleDeg / 2.0, 1)}°) = ${formatNum(setback)} in",
            steps = listOf(
                "Step 1: Calculate outside pipe start stagger: ${formatNum(conduitSpacing)} × tan(${formatNum(angleDeg / 2.0, 1)}°) = ${formatNum(setback)} in (${toFractionString(setback)})",
                "Step 2: Add this setback to the starting mark of each subsequent outside conduit.",
                "Step 3: Keep mark-to-mark distance identical on all conduits: ${formatNum(distanceBetweenBends)} in (${toFractionString(distanceBetweenBends)}).",
                "Step 4: All offsets will maintain uniform, aesthetically parallel spacing throughout the turn."
            ),
            standardNote = "Maintains constant center-to-center clearance between parallel runs without binding."
        )
    }

    // 3. Matching Centers Offset
    fun calculateMatchingCenters(
        obstacleRise: Double,
        adjacentDistance: Double
    ): CalculationResult {
        if (obstacleRise <= 0 || adjacentDistance <= 0) {
            return CalculationResult(
                primaryValue = "",
                primaryUnit = "",
                formula = "θ = arctan(Rise / Adjacent)",
                substitution = "",
                standardNote = "Conduit Bending Standards",
                isSuccess = false,
                errorMessage = "Rise and Adjacent distance must be greater than zero."
            )
        }

        val exactAngle = Math.toDegrees(atan2(obstacleRise, adjacentDistance))
        val hypotenuse = sqrt(obstacleRise * obstacleRise + adjacentDistance * adjacentDistance)
        val shrink = hypotenuse - adjacentDistance

        // Find nearest standard bend angle (10, 22.5, 30, 45, 60)
        val standardAngles = listOf(10.0, 22.5, 30.0, 45.0, 60.0)
        val nearestStd = standardAngles.minByOrNull { abs(it - exactAngle) } ?: 30.0

        return CalculationResult(
            primaryValue = "${formatNum(exactAngle, 1)}°",
            primaryUnit = "(Exact Bend Angle)",
            secondaryValues = mapOf(
                "Exact Bend Angle" to "${formatNum(exactAngle, 1)}°",
                "Nearest Standard Bender Angle" to "${formatNum(nearestStd, 1)}°",
                "Distance Between Marks" to "${formatNum(hypotenuse)} in (${toFractionString(hypotenuse)})",
                "Total Shrink" to "${formatNum(shrink)} in (${toFractionString(shrink)})",
                "Required Rise" to "${formatNum(obstacleRise)} in",
                "Available Run" to "${formatNum(adjacentDistance)} in"
            ),
            formula = "θ = arctan(Rise / Run),  Distance = √(Rise² + Run²),  Shrink = Distance - Run",
            substitution = "θ = arctan(${formatNum(obstacleRise)} / ${formatNum(adjacentDistance)}) = ${formatNum(exactAngle, 1)}°\nDistance = √(${formatNum(obstacleRise)}² + ${formatNum(adjacentDistance)}²) = ${formatNum(hypotenuse)} in",
            steps = listOf(
                "Step 1: Calculate exact angle θ = arctan(${formatNum(obstacleRise)} / ${formatNum(adjacentDistance)}) = ${formatNum(exactAngle, 1)}°",
                "Step 2: Distance between marks D = √(${formatNum(obstacleRise)}² + ${formatNum(adjacentDistance)}²) = ${formatNum(hypotenuse)} in (${toFractionString(hypotenuse)})",
                "Step 3: Total conduit shrink = ${formatNum(shrink)} in (${toFractionString(shrink)}).",
                "Step 4: If using standard shoe notch, nearest standard is ${formatNum(nearestStd, 1)}°."
            ),
            standardNote = "Used when an offset must fit strictly within a constrained longitudinal window."
        )
    }

    // 4. Matching Bends Offset
    fun calculateMatchingBends(
        existingDistance: Double,
        measuredRise: Double
    ): CalculationResult {
        if (existingDistance <= 0 || measuredRise <= 0) {
            return CalculationResult(
                primaryValue = "",
                primaryUnit = "",
                formula = "sin(θ) = Rise / Distance",
                substitution = "",
                standardNote = "Conduit Bending Standards",
                isSuccess = false,
                errorMessage = "Distance and Rise must be greater than zero."
            )
        }

        if (measuredRise > existingDistance) {
            return CalculationResult(
                primaryValue = "",
                primaryUnit = "",
                formula = "sin(θ) = Rise / Distance",
                substitution = "",
                standardNote = "Conduit Bending Standards",
                isSuccess = false,
                errorMessage = "Rise cannot exceed the distance between bend marks."
            )
        }

        val sinVal = measuredRise / existingDistance
        val angleDeg = Math.toDegrees(asin(sinVal))
        val multiplier = existingDistance / measuredRise
        val shrink = measuredRise * getShrinkPerUnit(angleDeg)

        return CalculationResult(
            primaryValue = "${formatNum(angleDeg, 1)}°",
            primaryUnit = "(Matching Bend Angle)",
            secondaryValues = mapOf(
                "Calculated Bend Angle" to "${formatNum(angleDeg, 1)}°",
                "Bend Multiplier (D/R)" to "${formatNum(multiplier, 2)}",
                "Distance Between Bends" to "${formatNum(existingDistance)} in",
                "Measured Rise" to "${formatNum(measuredRise)} in",
                "Conduit Shrink" to "${formatNum(shrink)} in (${toFractionString(shrink)})"
            ),
            formula = "θ = arcsin(Rise / Distance),  Multiplier = Distance / Rise",
            substitution = "θ = arcsin(${formatNum(measuredRise)} / ${formatNum(existingDistance)}) = ${formatNum(angleDeg, 1)}°",
            steps = listOf(
                "Step 1: Calculate sin(θ) = ${formatNum(measuredRise)} / ${formatNum(existingDistance)} = ${formatNum(sinVal, 3)}",
                "Step 2: Calculated bend angle is arcsin(${formatNum(sinVal, 3)}) = ${formatNum(angleDeg, 1)}°",
                "Step 3: Multiplier = ${formatNum(multiplier, 2)}",
                "Step 4: Bend shrink for this offset is ${formatNum(shrink)} in (${toFractionString(shrink)})."
            ),
            standardNote = "Enables precise duplication of existing field offsets when angle is unknown."
        )
    }

    // 5. Three-Point Saddle
    fun calculateThreePointSaddle(
        obstacleHeight: Double,
        centerAngleDeg: Double = 45.0
    ): CalculationResult {
        if (obstacleHeight <= 0) {
            return CalculationResult(
                primaryValue = "",
                primaryUnit = "",
                formula = "Distance to side marks = Rise × Multiplier",
                substitution = "",
                standardNote = "Conduit Bending Standards",
                isSuccess = false,
                errorMessage = "Obstacle height must be greater than zero."
            )
        }

        // Standard 3-point saddle uses:
        // 45° center -> 22.5° sides (multiplier 2.5 or 2.6, shrink 3/16" per inch = 0.1875)
        // 30° center -> 15° sides (multiplier 2.0, shrink 1/8" per inch = 0.125)
        // 60° center -> 30° sides (multiplier 2.0, shrink 5/16" per inch = 0.3125)
        val sideAngleDeg = centerAngleDeg / 2.0
        val sideMultiplier = getMultiplier(sideAngleDeg)
        val distanceToSides = obstacleHeight * sideMultiplier
        val shrinkPerInch = when (centerAngleDeg.toInt()) {
            30 -> 0.125     // 1/8"
            60 -> 0.3125    // 5/16"
            else -> 0.1875  // 3/16" for 45°
        }
        val totalShrink = obstacleHeight * shrinkPerInch

        return CalculationResult(
            primaryValue = formatNum(distanceToSides),
            primaryUnit = "in (Center mark to side marks)",
            secondaryValues = mapOf(
                "Center Bend Angle" to "${formatNum(centerAngleDeg, 0)}°",
                "Side Bend Angles" to "${formatNum(sideAngleDeg, 1)}° (each side)",
                "Distance to Side Marks" to "${formatNum(distanceToSides)} in (${toFractionString(distanceToSides)})",
                "Center Mark Shift" to "${formatNum(totalShrink)} in (${toFractionString(totalShrink)})",
                "Total Conduit Shrink" to "${formatNum(totalShrink)} in (${toFractionString(totalShrink)})",
                "Obstacle Height" to "${formatNum(obstacleHeight)} in"
            ),
            formula = "Side Marks Distance = Height × csc(θ_side),  Shrink = Height × ShrinkRate",
            substitution = "Distance = ${formatNum(obstacleHeight)} × ${formatNum(sideMultiplier, 2)} = ${formatNum(distanceToSides)} in\nShrink = ${formatNum(obstacleHeight)} × ${formatNum(shrinkPerInch, 4)} = ${formatNum(totalShrink)} in",
            steps = listOf(
                "Step 1: Locate center of obstacle on conduit and add shrink shift of ${formatNum(totalShrink)} in (${toFractionString(totalShrink)}).",
                "Step 2: Make Center Mark (Bend ${formatNum(centerAngleDeg, 0)}° at notch/rim).",
                "Step 3: Measure out ${formatNum(distanceToSides)} in (${toFractionString(distanceToSides)}) in BOTH directions from the center mark for the two side marks.",
                "Step 4: Bend both side marks at ${formatNum(sideAngleDeg, 1)}° in opposite direction of the center bend (using arrow on shoe).",
                "Step 5: Check saddle for straight alignment across the obstruction."
            ),
            standardNote = "Saddle clears cylindrical or rectangular obstructions like crossing conduits or pipes."
        )
    }
}
