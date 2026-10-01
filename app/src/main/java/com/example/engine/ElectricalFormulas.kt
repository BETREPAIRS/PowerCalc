package com.example.engine

import java.util.Locale
import kotlin.math.*

enum class ConductorMaterial(val displayName: String, val resistivity: Double) {
    COPPER("Copper (Cu)", 0.0175),      // ohm * mm^2 / m at 20°C
    ALUMINIUM("Aluminium (Al)", 0.028)  // ohm * mm^2 / m at 20°C
}

enum class ThreePhaseConnection(val displayName: String) {
    STAR("Star (Y)"),
    DELTA("Delta (Δ)")
}

data class CalculationResult(
    val primaryValue: String,
    val primaryUnit: String,
    val secondaryValues: Map<String, String> = emptyMap(),
    val formula: String,
    val substitution: String,
    val steps: List<String> = emptyList(),
    val standardNote: String,
    val warning: String? = null,
    val isSuccess: Boolean = true,
    val errorMessage: String? = null
)

object ElectricalFormulas {

    private fun formatNum(value: Double, decimals: Int = 2): String {
        if (value.isNaN() || value.isInfinite()) return "0"
        return String.format(Locale.US, "%.${decimals}f", value)
    }

    // -------------------------------------------------------------
    // 1. OHM'S LAW & BASIC DC/AC
    // -------------------------------------------------------------
    fun calculateVoltageFromIR(current: Double, resistance: Double): CalculationResult {
        if (current <= 0 || resistance <= 0) {
            return CalculationResult("", "", formula = "V = I × R", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Current and Resistance must be greater than zero.")
        }
        val v = current * resistance
        val p = current * current * resistance
        return CalculationResult(
            primaryValue = formatNum(v),
            primaryUnit = "V",
            secondaryValues = mapOf(
                "Power (P = I²R)" to "${formatNum(p)} W",
                "Power in kW" to "${formatNum(p / 1000.0, 3)} kW"
            ),
            formula = "V = I × R",
            substitution = "V = ${formatNum(current)} A × ${formatNum(resistance)} Ω",
            steps = listOf(
                "Step 1: Identify current I = ${formatNum(current)} A",
                "Step 2: Identify resistance R = ${formatNum(resistance)} Ω",
                "Step 3: Multiply current by resistance: V = ${formatNum(current)} × ${formatNum(resistance)} = ${formatNum(v)} V"
            ),
            standardNote = "Ohm's Law: Fundamental relation for linear DC and pure resistive AC circuits."
        )
    }

    fun calculateCurrentFromVR(voltage: Double, resistance: Double): CalculationResult {
        if (voltage <= 0 || resistance <= 0) {
            return CalculationResult("", "", formula = "I = V / R", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Voltage and Resistance must be greater than zero.")
        }
        val i = voltage / resistance
        val p = (voltage * voltage) / resistance
        return CalculationResult(
            primaryValue = formatNum(i),
            primaryUnit = "A",
            secondaryValues = mapOf(
                "Power (P = V²/R)" to "${formatNum(p)} W",
                "Current in mA" to "${formatNum(i * 1000.0, 1)} mA"
            ),
            formula = "I = V / R",
            substitution = "I = ${formatNum(voltage)} V / ${formatNum(resistance)} Ω",
            steps = listOf(
                "Step 1: Voltage V = ${formatNum(voltage)} V",
                "Step 2: Resistance R = ${formatNum(resistance)} Ω",
                "Step 3: Divide voltage by resistance: I = ${formatNum(voltage)} / ${formatNum(resistance)} = ${formatNum(i)} A"
            ),
            standardNote = "Ohm's Law: Valid for constant resistance under steady-state conditions."
        )
    }

    fun calculateResistanceFromVI(voltage: Double, current: Double): CalculationResult {
        if (voltage <= 0 || current <= 0) {
            return CalculationResult("", "", formula = "R = V / I", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Voltage and Current must be greater than zero.")
        }
        val r = voltage / current
        val p = voltage * current
        return CalculationResult(
            primaryValue = formatNum(r),
            primaryUnit = "Ω",
            secondaryValues = mapOf(
                "Power Dissipation" to "${formatNum(p)} W",
                "Conductance (G = 1/R)" to "${formatNum(1.0 / r, 4)} S (Siemens)"
            ),
            formula = "R = V / I",
            substitution = "R = ${formatNum(voltage)} V / ${formatNum(current)} A",
            steps = listOf(
                "Step 1: Voltage V = ${formatNum(voltage)} V",
                "Step 2: Current I = ${formatNum(current)} A",
                "Step 3: Divide voltage by current: R = ${formatNum(voltage)} / ${formatNum(current)} = ${formatNum(r)} Ω"
            ),
            standardNote = "Ohm's Law resistance evaluation."
        )
    }

    fun calculateDCPower(voltage: Double, current: Double): CalculationResult {
        if (voltage <= 0 || current <= 0) {
            return CalculationResult("", "", formula = "P = V × I", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Voltage and Current must be greater than zero.")
        }
        val p = voltage * current
        val r = voltage / current
        val hp = p / 745.7
        return CalculationResult(
            primaryValue = formatNum(p),
            primaryUnit = "W",
            secondaryValues = mapOf(
                "Power in kW" to "${formatNum(p / 1000.0, 3)} kW",
                "Equivalent Horsepower (HP)" to "${formatNum(hp, 2)} HP",
                "Load Resistance" to "${formatNum(r)} Ω"
            ),
            formula = "P = V × I",
            substitution = "P = ${formatNum(voltage)} V × ${formatNum(current)} A",
            steps = listOf(
                "Step 1: V = ${formatNum(voltage)} V, I = ${formatNum(current)} A",
                "Step 2: P = V × I = ${formatNum(voltage)} × ${formatNum(current)} = ${formatNum(p)} W",
                "Step 3: kW = P / 1000 = ${formatNum(p / 1000.0, 3)} kW"
            ),
            standardNote = "Joule's / Electrical Power Law for DC circuits and pure resistive AC circuits."
        )
    }

    // -------------------------------------------------------------
    // 2. AC SINGLE PHASE & THREE PHASE
    // -------------------------------------------------------------
    fun calculateACSinglePhase(voltage: Double, current: Double, pf: Double, frequency: Double = 50.0): CalculationResult {
        if (voltage <= 0 || current <= 0) {
            return CalculationResult("", "", formula = "P = V × I × PF", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Voltage and Current must be positive.")
        }
        val clampedPf = pf.coerceIn(0.01, 1.0)
        val s = voltage * current // Apparent Power VA
        val p = s * clampedPf    // Active Power W
        val phi = acos(clampedPf)
        val q = s * sin(phi)     // Reactive Power VAR

        val warning = if (clampedPf < 0.85) "Low power factor (PF < 0.85). Capacitive compensation recommended to avoid utility penalties." else null

        return CalculationResult(
            primaryValue = formatNum(p / 1000.0, 3),
            primaryUnit = "kW",
            secondaryValues = mapOf(
                "Active Power (P)" to "${formatNum(p)} W",
                "Apparent Power (S)" to "${formatNum(s / 1000.0, 3)} kVA (${formatNum(s)} VA)",
                "Reactive Power (Q)" to "${formatNum(q / 1000.0, 3)} kVAR",
                "Power Factor (PF)" to formatNum(clampedPf),
                "Phase Angle (φ)" to "${formatNum(Math.toDegrees(phi), 1)}°",
                "Peak Voltage" to "${formatNum(voltage * sqrt(2.0), 1)} V_pk"
            ),
            formula = "P = V × I × PF,  S = V × I,  Q = V × I × sin(φ)",
            substitution = "P = ${formatNum(voltage)} V × ${formatNum(current)} A × ${formatNum(clampedPf)} = ${formatNum(p)} W",
            steps = listOf(
                "Step 1: Calculate Apparent Power S = V × I = ${formatNum(voltage)} × ${formatNum(current)} = ${formatNum(s)} VA (${formatNum(s/1000.0, 3)} kVA)",
                "Step 2: Calculate Active Power P = S × PF = ${formatNum(s)} × ${formatNum(clampedPf)} = ${formatNum(p)} W (${formatNum(p/1000.0, 3)} kW)",
                "Step 3: Calculate Reactive Power Q = S × √(1 - PF²) = ${formatNum(q/1000.0, 3)} kVAR",
                "Step 4: Phase angle φ = arccos(${formatNum(clampedPf)}) = ${formatNum(Math.toDegrees(phi), 1)}°"
            ),
            standardNote = "IEC 60038 / IEEE 1459 Single-phase AC power standard formulas.",
            warning = warning
        )
    }

    fun calculateACThreePhase(vLine: Double, iLine: Double, pf: Double, connection: ThreePhaseConnection): CalculationResult {
        if (vLine <= 0 || iLine <= 0) {
            return CalculationResult("", "", formula = "P = √3 × V_L × I_L × PF", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Line Voltage and Line Current must be positive.")
        }
        val clampedPf = pf.coerceIn(0.01, 1.0)
        val s = sqrt(3.0) * vLine * iLine // VA
        val p = s * clampedPf             // W
        val phi = acos(clampedPf)
        val q = s * sin(phi)              // VAR

        val (vPhase, iPhase) = when (connection) {
            ThreePhaseConnection.STAR -> Pair(vLine / sqrt(3.0), iLine)
            ThreePhaseConnection.DELTA -> Pair(vLine, iLine / sqrt(3.0))
        }

        return CalculationResult(
            primaryValue = formatNum(p / 1000.0, 3),
            primaryUnit = "kW",
            secondaryValues = mapOf(
                "Active Power (P)" to "${formatNum(p)} W",
                "Apparent Power (S)" to "${formatNum(s / 1000.0, 3)} kVA",
                "Reactive Power (Q)" to "${formatNum(q / 1000.0, 3)} kVAR",
                "Phase Voltage (V_ph)" to "${formatNum(vPhase, 1)} V",
                "Phase Current (I_ph)" to "${formatNum(iPhase, 2)} A",
                "Connection Mode" to connection.displayName
            ),
            formula = "P = √3 × V_line × I_line × PF",
            substitution = "P = 1.732 × ${formatNum(vLine)} V × ${formatNum(iLine)} A × ${formatNum(clampedPf)}",
            steps = listOf(
                "Step 1: Apparent Power S = √3 × V_L × I_L = 1.732 × ${formatNum(vLine)} × ${formatNum(iLine)} = ${formatNum(s/1000.0, 3)} kVA",
                "Step 2: Active Power P = S × PF = ${formatNum(s/1000.0, 3)} × ${formatNum(clampedPf)} = ${formatNum(p/1000.0, 3)} kW",
                "Step 3: Reactive Power Q = √3 × V_L × I_L × sin(φ) = ${formatNum(q/1000.0, 3)} kVAR",
                "Step 4: Connection (${connection.displayName}): V_ph = ${formatNum(vPhase, 1)} V, I_ph = ${formatNum(iPhase, 2)} A"
            ),
            standardNote = "Symmetrical three-phase balanced system (IEC 60038 / IEEE Standards)."
        )
    }

    // -------------------------------------------------------------
    // 3. POWER FACTOR CORRECTION
    // -------------------------------------------------------------
    fun calculatePFCorrection(
        activePowerKW: Double,
        currentPF: Double,
        targetPF: Double,
        voltage: Double,
        frequency: Double = 50.0,
        isThreePhase: Boolean = true
    ): CalculationResult {
        if (activePowerKW <= 0 || voltage <= 0 || frequency <= 0) {
            return CalculationResult("", "", formula = "Q_c = P × [tan(φ1) - tan(φ2)]", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Power, Voltage and Frequency must be positive.")
        }
        val pf1 = currentPF.coerceIn(0.2, 0.99)
        val pf2 = targetPF.coerceIn(pf1, 1.0)

        val tanPhi1 = tan(acos(pf1))
        val tanPhi2 = tan(acos(pf2))
        val deltaQkvar = activePowerKW * (tanPhi1 - tanPhi2) // in kVAR

        // Capacitance calculation
        // For 3-phase delta capacitor bank: Q_c = 3 * (2 * pi * f * C * V^2)
        // C_per_phase in microfarads:
        val cMicroFarads = if (isThreePhase) {
            // Delta connection across line voltage V
            (deltaQkvar * 1000.0 * 1000000.0) / (3.0 * 2.0 * Math.PI * frequency * voltage * voltage)
        } else {
            (deltaQkvar * 1000.0 * 1000000.0) / (2.0 * Math.PI * frequency * voltage * voltage)
        }

        // Current reduction
        val iInitial = if (isThreePhase) (activePowerKW * 1000.0) / (sqrt(3.0) * voltage * pf1) else (activePowerKW * 1000.0) / (voltage * pf1)
        val iFinal = if (isThreePhase) (activePowerKW * 1000.0) / (sqrt(3.0) * voltage * pf2) else (activePowerKW * 1000.0) / (voltage * pf2)
        val currentSaved = iInitial - iFinal

        return CalculationResult(
            primaryValue = formatNum(deltaQkvar, 2),
            primaryUnit = "kVAR",
            secondaryValues = mapOf(
                "Required Capacitance" to "${formatNum(cMicroFarads, 1)} µF ${if (isThreePhase) "/ phase (Δ)" else ""}",
                "Initial Line Current" to "${formatNum(iInitial, 1)} A",
                "New Line Current" to "${formatNum(iFinal, 1)} A",
                "Current Reduction" to "-${formatNum(currentSaved, 1)} A (${formatNum((currentSaved / iInitial) * 100, 1)}%)",
                "Initial PF" to formatNum(pf1),
                "Target PF" to formatNum(pf2)
            ),
            formula = "Q_c = P × [tan(arccos(PF₁)) - tan(arccos(PF₂))]",
            substitution = "Q_c = ${formatNum(activePowerKW)} kW × [${formatNum(tanPhi1, 3)} - ${formatNum(tanPhi2, 3)}]",
            steps = listOf(
                "Step 1: Calculate tan(φ1) = tan(arccos(${formatNum(pf1)})) = ${formatNum(tanPhi1, 4)}",
                "Step 2: Calculate tan(φ2) = tan(arccos(${formatNum(pf2)})) = ${formatNum(tanPhi2, 4)}",
                "Step 3: Q_c = ${formatNum(activePowerKW)} × (${formatNum(tanPhi1, 4)} - ${formatNum(tanPhi2, 4)}) = ${formatNum(deltaQkvar, 2)} kVAR",
                "Step 4: C = Q_c / (2πf × V²) = ${formatNum(cMicroFarads, 1)} µF"
            ),
            standardNote = "Power Factor Improvement (IEC 61921 / IEEE 18 Standard for Shunt Power Capacitors).",
            warning = if (targetPF > 0.98) "Target PF > 0.98 risks over-compensation / harmonic resonance under light load." else null
        )
    }

    // -------------------------------------------------------------
    // 4. CABLE SIZING & VOLTAGE DROP
    // -------------------------------------------------------------
    val standardMetricSizes = listOf(1.5, 2.5, 4.0, 6.0, 10.0, 16.0, 25.0, 35.0, 50.0, 70.0, 95.0, 120.0, 150.0, 185.0, 240.0, 300.0)

    // Baseline current carrying capacity for standard PVC/XLPE copper conductors (clipped reference IEC 60364-5-52 Method B2 / C)
    private val standardCopperAmpacity = mapOf(
        1.5 to 17.5,
        2.5 to 24.0,
        4.0 to 32.0,
        6.0 to 41.0,
        10.0 to 57.0,
        16.0 to 76.0,
        25.0 to 101.0,
        35.0 to 125.0,
        50.0 to 151.0,
        70.0 to 192.0,
        95.0 to 232.0,
        120.0 to 269.0,
        150.0 to 309.0,
        185.0 to 353.0,
        240.0 to 415.0,
        300.0 to 477.0
    )

    fun calculateCableSizing(
        loadCurrent: Double,
        lengthM: Double,
        systemVoltage: Double,
        maxDropPct: Double = 3.0,
        material: ConductorMaterial = ConductorMaterial.COPPER,
        isThreePhase: Boolean = false,
        ambientTempC: Double = 30.0,
        groupedCircuits: Int = 1
    ): CalculationResult {
        if (loadCurrent <= 0 || lengthM <= 0 || systemVoltage <= 0) {
            return CalculationResult("", "", formula = "V_drop = (k × L × I × ρ) / A", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Load current, length, and voltage must be positive.")
        }

        // Temp derating factor approx (IEC 60364-5-52) for PVC 70°C:
        // C_t = sqrt((70 - temp) / 40)
        val tempFactor = sqrt(((70.0 - ambientTempC.coerceIn(10.0, 65.0)) / 40.0)).coerceIn(0.5, 1.2)
        // Grouping derating factor approx
        val groupingFactor = when (groupedCircuits) {
            1 -> 1.0
            2 -> 0.8
            3 -> 0.7
            4 -> 0.65
            else -> 0.6
        }
        val totalDerating = tempFactor * groupingFactor
        val designCurrent = loadCurrent / totalDerating

        val maxAllowedDropV = systemVoltage * (maxDropPct / 100.0)
        val k = if (isThreePhase) sqrt(3.0) else 2.0

        // Find candidate cable size that satisfies both thermal ampacity and voltage drop
        var recommendedSize = standardMetricSizes.last()
        var actualDropV = 0.0
        var actualDropPct = 0.0
        var cableResistance = 0.0

        for (size in standardMetricSizes) {
            val baseAmpacity = standardCopperAmpacity[size] ?: 10.0
            val materialAmpacity = if (material == ConductorMaterial.ALUMINIUM) baseAmpacity * 0.78 else baseAmpacity

            // Voltage drop: V_drop = (k * L * I * rho) / A
            val drop = (k * lengthM * loadCurrent * material.resistivity) / size
            val dropPct = (drop / systemVoltage) * 100.0

            if (materialAmpacity >= designCurrent && dropPct <= maxDropPct) {
                recommendedSize = size
                actualDropV = drop
                actualDropPct = dropPct
                cableResistance = (material.resistivity * lengthM) / size
                break
            }
        }

        // In case last size was chosen and still exceeded drop:
        if (actualDropV == 0.0) {
            actualDropV = (k * lengthM * loadCurrent * material.resistivity) / recommendedSize
            actualDropPct = (actualDropV / systemVoltage) * 100.0
            cableResistance = (material.resistivity * lengthM) / recommendedSize
        }

        val powerLoss = if (isThreePhase) 3.0 * loadCurrent * loadCurrent * cableResistance else 2.0 * loadCurrent * loadCurrent * cableResistance

        val warning = if (actualDropPct > maxDropPct) {
            "Warning: Calculated voltage drop (${formatNum(actualDropPct, 2)}%) exceeds user limit (${formatNum(maxDropPct, 1)}%). Consider parallel conductors."
        } else null

        return CalculationResult(
            primaryValue = "${recommendedSize} mm²",
            primaryUnit = "Conductor",
            secondaryValues = mapOf(
                "Design Current (Ib)" to "${formatNum(loadCurrent, 1)} A",
                "Derated Ampacity Req." to "${formatNum(designCurrent, 1)} A",
                "Voltage Drop" to "${formatNum(actualDropV, 2)} V (${formatNum(actualDropPct, 2)}%)",
                "End Terminal Voltage" to "${formatNum(systemVoltage - actualDropV, 1)} V",
                "Cable Resistance (per core)" to "${formatNum(cableResistance, 3)} Ω",
                "Total Cable Power Loss (I²R)" to "${formatNum(powerLoss, 1)} W",
                "Conductor Material" to material.displayName,
                "Standard Derating Factor" to formatNum(totalDerating, 3)
            ),
            formula = "A_min = (${if (isThreePhase) "√3" else "2"} × L × I × ρ) / V_drop_allowed",
            substitution = "A_min = (${formatNum(k, 3)} × ${formatNum(lengthM)} m × ${formatNum(loadCurrent)} A × ${material.resistivity}) / ${formatNum(maxAllowedDropV, 2)} V",
            steps = listOf(
                "Step 1: Calculate total derating factor: Temp (${formatNum(tempFactor, 2)}) × Grouping (${formatNum(groupingFactor, 2)}) = ${formatNum(totalDerating, 2)}",
                "Step 2: Effective design current Iz = ${formatNum(loadCurrent, 1)} / ${formatNum(totalDerating, 2)} = ${formatNum(designCurrent, 1)} A",
                "Step 3: Allowable voltage drop = ${formatNum(maxDropPct)}% of ${formatNum(systemVoltage)} V = ${formatNum(maxAllowedDropV, 2)} V",
                "Step 4: Selected standard size ${recommendedSize} mm² provides ${formatNum(actualDropV, 2)} V drop (${formatNum(actualDropPct, 2)}% <= ${formatNum(maxDropPct)}%)"
            ),
            standardNote = "Based on IEC 60364-5-52 / NEC Article 310. Always verify against local installation conditions.",
            warning = warning
        )
    }

    /**
     * Calculates the full load current carrying capacity (ampacity) and load power rating
     * for a given cable size in mm², taking into account conductor material, ambient temperature,
     * installation method, and system voltage/phase.
     */
    fun calculateCableCapacityFromSize(
        sizeMm2: Double,
        material: ConductorMaterial = ConductorMaterial.COPPER,
        isThreePhase: Boolean = false,
        systemVoltage: Double = 230.0,
        ambientTempC: Double = 30.0,
        installationMethod: String = "conduit", // "conduit", "clipped", "tray", "underground"
        groupedCircuits: Int = 1,
        powerFactor: Double = 0.90
    ): CalculationResult {
        if (sizeMm2 <= 0 || systemVoltage <= 0) {
            return CalculationResult(
                primaryValue = "",
                primaryUnit = "",
                formula = "Iz = I_base × Ct × Cg",
                substitution = "",
                standardNote = "",
                isSuccess = false,
                errorMessage = "Cable size (mm²) and voltage must be positive values."
            )
        }

        // Multipliers based on installation method (IEC 60364-5-52)
        // Baseline table standardCopperAmpacity is Method B2 (in conduit/trunking)
        val methodMultiplier = when (installationMethod) {
            "clipped" -> 1.15   // Method C: Clipped direct to wall/surface
            "tray" -> 1.25      // Method E/F: Free air / perforated cable tray
            "underground" -> 1.10 // Method D: Buried in ground / ducts
            else -> 1.0         // Method B2: In conduit / trunking inside wall
        }

        // Find baseline copper ampacity (Method B2) with interpolation for custom sizes
        val baseAmpacityCu = if (standardCopperAmpacity.containsKey(sizeMm2)) {
            standardCopperAmpacity[sizeMm2]!!
        } else {
            val sorted = standardMetricSizes.sorted()
            if (sizeMm2 < sorted.first()) {
                (standardCopperAmpacity[sorted.first()]!! / sorted.first()) * sizeMm2
            } else if (sizeMm2 > sorted.last()) {
                val last = sorted.last()
                val lastAmp = standardCopperAmpacity[last]!!
                lastAmp * Math.pow(sizeMm2 / last, 0.62)
            } else {
                var lower = sorted.first()
                var upper = sorted.last()
                for (i in 0 until sorted.size - 1) {
                    if (sizeMm2 >= sorted[i] && sizeMm2 <= sorted[i + 1]) {
                        lower = sorted[i]
                        upper = sorted[i + 1]
                        break
                    }
                }
                val iLow = standardCopperAmpacity[lower]!!
                val iHigh = standardCopperAmpacity[upper]!!
                val t = (sizeMm2 - lower) / (upper - lower)
                iLow + t * (iHigh - iLow)
            }
        }

        val materialFactor = if (material == ConductorMaterial.ALUMINIUM) 0.78 else 1.0
        val baseAmpacity = baseAmpacityCu * methodMultiplier * materialFactor

        // Temperature correction factor for 70°C PVC (IEC 60364-5-52 Table B.52.14)
        val tempFactor = sqrt(((70.0 - ambientTempC.coerceIn(10.0, 65.0)) / 40.0)).coerceIn(0.4, 1.25)

        // Grouping factor
        val groupingFactor = when (groupedCircuits) {
            1 -> 1.0
            2 -> 0.8
            3 -> 0.7
            4 -> 0.65
            else -> 0.6
        }

        val totalDerating = tempFactor * groupingFactor
        val fullLoadAmpacity = baseAmpacity * totalDerating
        val continuousSafeLoad = fullLoadAmpacity * 0.80 // 80% continuous duty rating

        // Full load power capacity in kW and kVA
        val fullLoadKVA = if (isThreePhase) {
            (sqrt(3.0) * systemVoltage * fullLoadAmpacity) / 1000.0
        } else {
            (systemVoltage * fullLoadAmpacity) / 1000.0
        }
        val fullLoadKW = fullLoadKVA * powerFactor
        val contKW = fullLoadKW * 0.80

        // Recommended Standard MCB / Breaker Rating (<= fullLoadAmpacity)
        val standardBreakers = listOf(6, 10, 16, 20, 25, 32, 40, 50, 63, 80, 100, 125, 160, 200, 250, 315, 400, 500, 630)
        val recommendedMcb = standardBreakers.filter { it.toDouble() <= fullLoadAmpacity }.lastOrNull()
            ?: standardBreakers.first()

        val resistancePerKm = (material.resistivity * 1000.0) / sizeMm2

        val methodLabel = when (installationMethod) {
            "clipped" -> "Clipped Direct / Surface (Method C)"
            "tray" -> "Cable Tray / Perforated (Method E/F)"
            "underground" -> "Direct Buried / Ground (Method D)"
            else -> "In Conduit / Trunking (Method B2)"
        }

        return CalculationResult(
            primaryValue = "${formatNum(fullLoadAmpacity, 1)} A",
            primaryUnit = "Full Load Capacity",
            secondaryValues = mapOf(
                "Safe Continuous Load (80%)" to "${formatNum(continuousSafeLoad, 1)} A",
                "Full Load Active Power" to "${formatNum(fullLoadKW, 2)} kW (@ PF ${formatNum(powerFactor, 2)})",
                "Apparent Power Capacity" to "${formatNum(fullLoadKVA, 2)} kVA",
                "Continuous Power (80%)" to "${formatNum(contKW, 2)} kW",
                "Base Ampacity (Underrated)" to "${formatNum(baseAmpacity, 1)} A",
                "Recommended MCB / Fuse" to "$recommendedMcb A",
                "Conductor Material" to material.displayName,
                "Installation Method" to methodLabel,
                "Resistance @ 20°C" to "${formatNum(resistancePerKm, 3)} Ω/km",
                "Temp & Group Derating" to "${formatNum(totalDerating, 2)} (T: ${formatNum(tempFactor, 2)} × G: ${formatNum(groupingFactor, 2)})"
            ),
            formula = if (isThreePhase) {
                "P_max = √3 × V × Iz × PF  |  Iz = I_base × Ct × Cg"
            } else {
                "P_max = V × Iz × PF  |  Iz = I_base × Ct × Cg"
            },
            substitution = "Iz = ${formatNum(baseAmpacity, 1)} A × ${formatNum(totalDerating, 2)} = ${formatNum(fullLoadAmpacity, 1)} A | P = ${formatNum(fullLoadKW, 2)} kW",
            steps = listOf(
                "Step 1: Baseline current capacity for ${formatNum(sizeMm2, 1)} mm² ${material.displayName} under $methodLabel = ${formatNum(baseAmpacity, 1)} A",
                "Step 2: Apply temperature derating Ct(${formatNum(tempFactor, 2)}) at ${formatNum(ambientTempC, 1)}°C and grouping factor Cg(${formatNum(groupingFactor, 2)})",
                "Step 3: Effective Full Load Current Iz = ${formatNum(baseAmpacity, 1)} × ${formatNum(totalDerating, 2)} = ${formatNum(fullLoadAmpacity, 1)} A",
                "Step 4: Continuous safe operating current (80% rule) = ${formatNum(continuousSafeLoad, 1)} A",
                "Step 5: Maximum power capacity = ${formatNum(fullLoadKW, 2)} kW (${formatNum(fullLoadKVA, 2)} kVA) at ${formatNum(systemVoltage, 0)} V ${if (isThreePhase) "3-Phase" else "1-Phase"}"
            ),
            standardNote = "Current carrying capacity based on IEC 60364-5-52 / NEC Table 310.16. Continuous loading restricted to 80% of circuit protective device rating per NEC 210.20."
        )
    }

    fun calculateVoltageDrop(
        voltage: Double,
        current: Double,
        lengthM: Double,
        sizeMm2: Double,
        material: ConductorMaterial = ConductorMaterial.COPPER,
        isThreePhase: Boolean = false,
        pf: Double = 0.95
    ): CalculationResult {
        if (voltage <= 0 || current <= 0 || lengthM <= 0 || sizeMm2 <= 0) {
            return CalculationResult("", "", formula = "V_drop = (k × L × I × ρ) / A", substitution = "", standardNote = "", isSuccess = false, errorMessage = "All parameters must be positive.")
        }
        val k = if (isThreePhase) sqrt(3.0) else 2.0
        val rPerCore = (material.resistivity * lengthM) / sizeMm2
        val dropV = k * current * rPerCore * pf
        val dropPct = (dropV / voltage) * 100.0
        val endVoltage = voltage - dropV
        val lossW = (if (isThreePhase) 3.0 else 2.0) * current * current * rPerCore

        val warning = if (dropPct > 5.0) "Excessive voltage drop (> 5%). May cause equipment malfunction or motor stalls." else null

        return CalculationResult(
            primaryValue = formatNum(dropV, 2),
            primaryUnit = "V",
            secondaryValues = mapOf(
                "Voltage Drop %" to "${formatNum(dropPct, 2)} %",
                "End of Line Voltage" to "${formatNum(endVoltage, 1)} V",
                "Cable Resistance" to "${formatNum(rPerCore, 4)} Ω",
                "Cable Loss" to "${formatNum(lossW, 1)} W",
                "System Type" to if (isThreePhase) "3-Phase AC" else "1-Phase / DC"
            ),
            formula = if (isThreePhase) "V_drop = √3 × I × L × ρ / A" else "V_drop = 2 × I × L × ρ / A",
            substitution = "V_drop = ${formatNum(k, 3)} × ${formatNum(current)} A × ${formatNum(lengthM)} m × ${material.resistivity} / ${formatNum(sizeMm2)} mm²",
            steps = listOf(
                "Step 1: Resistance per conductor R = ρ × L / A = ${material.resistivity} × ${formatNum(lengthM)} / ${formatNum(sizeMm2)} = ${formatNum(rPerCore, 4)} Ω",
                "Step 2: Voltage drop V_drop = ${formatNum(k, 3)} × ${formatNum(current)} A × ${formatNum(rPerCore, 4)} Ω = ${formatNum(dropV, 2)} V",
                "Step 3: Percentage drop = (${formatNum(dropV, 2)} / ${formatNum(voltage)}) × 100 = ${formatNum(dropPct, 2)}%",
                "Step 4: End Voltage = ${formatNum(voltage)} - ${formatNum(dropV, 2)} = ${formatNum(endVoltage, 1)} V"
            ),
            standardNote = "NEC 210.19(A) recommends max 3% for branch circuit, max 5% total system.",
            warning = warning
        )
    }

    fun calculateMaxCableLength(
        voltage: Double,
        current: Double,
        sizeMm2: Double,
        maxDropPct: Double = 3.0,
        material: ConductorMaterial = ConductorMaterial.COPPER,
        isThreePhase: Boolean = false
    ): CalculationResult {
        if (voltage <= 0 || current <= 0 || sizeMm2 <= 0) {
            return CalculationResult("", "", formula = "L_max = (V_drop × A) / (k × I × ρ)", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Parameters must be positive.")
        }
        val allowedDropV = voltage * (maxDropPct / 100.0)
        val k = if (isThreePhase) sqrt(3.0) else 2.0
        val lMax = (allowedDropV * sizeMm2) / (k * current * material.resistivity)

        return CalculationResult(
            primaryValue = formatNum(lMax, 1),
            primaryUnit = "Meters",
            secondaryValues = mapOf(
                "In Feet" to "${formatNum(lMax * 3.28084, 1)} ft",
                "Allowed Drop Voltage" to "${formatNum(allowedDropV, 2)} V",
                "Max Allowable Drop" to "${formatNum(maxDropPct, 1)} %",
                "Conductor Cross Section" to "${formatNum(sizeMm2)} mm²"
            ),
            formula = "L_max = (V_drop_allowed × A) / (k × I × ρ)",
            substitution = "L_max = (${formatNum(allowedDropV, 2)} V × ${formatNum(sizeMm2)} mm²) / (${formatNum(k, 3)} × ${formatNum(current)} A × ${material.resistivity})",
            steps = listOf(
                "Step 1: Calculate maximum permissible voltage drop = ${formatNum(maxDropPct)}% × ${formatNum(voltage)} V = ${formatNum(allowedDropV, 2)} V",
                "Step 2: Solve length formula: L = (${formatNum(allowedDropV, 2)} × ${formatNum(sizeMm2)}) / (${formatNum(k, 3)} × ${formatNum(current)} × ${material.resistivity})",
                "Step 3: Resulting maximum distance = ${formatNum(lMax, 1)} meters"
            ),
            standardNote = "Maximum circuit route distance before exceeding voltage regulation limits."
        )
    }

    // AWG to mm2 converter
    fun convertAwgToMm2(awgStr: String): CalculationResult {
        val n = when (awgStr.trim().uppercase()) {
            "0000", "4/0" -> -3
            "000", "3/0" -> -2
            "00", "2/0" -> -1
            "0", "1/0" -> 0
            else -> awgStr.toIntOrNull() ?: 12
        }
        // Formula: d_n = 0.127 * 92^((36-n)/39) in mm
        val dMm = 0.127 * 92.0.pow((36.0 - n) / 39.0)
        val areaMm2 = (Math.PI / 4.0) * dMm * dMm
        val resistanceOhmPerKm = (0.0175 * 1000.0) / areaMm2

        return CalculationResult(
            primaryValue = formatNum(areaMm2, 3),
            primaryUnit = "mm²",
            secondaryValues = mapOf(
                "Conductor Diameter" to "${formatNum(dMm, 3)} mm (${formatNum(dMm / 25.4, 4)} in)",
                "Copper Resistance @ 20°C" to "${formatNum(resistanceOhmPerKm, 2)} Ω/km",
                "AWG Gauge" to awgStr
            ),
            formula = "d = 0.127 × 92^((36-n)/39),  A = (π/4) × d²",
            substitution = "d = 0.127 × 92^((36 - ($n)) / 39) = ${formatNum(dMm, 3)} mm",
            steps = listOf(
                "Step 1: Determine gauge exponent n = $n",
                "Step 2: Calculate diameter d = ${formatNum(dMm, 3)} mm",
                "Step 3: Compute cross-sectional area A = π × (d/2)² = ${formatNum(areaMm2, 3)} mm²"
            ),
            standardNote = "ASTM B258 standard nominal diameters and cross-sectional areas of AWG sizes."
        )
    }

    // -------------------------------------------------------------
    // 5. BREAKER & PROTECTION COORDINATION
    // -------------------------------------------------------------
    val standardMcbRatings = listOf(6, 10, 16, 20, 25, 32, 40, 50, 63, 80, 100, 125, 160, 200, 250, 400, 630)

    fun calculateBreakerSelection(
        loadCurrent: Double,
        isContinuous: Boolean = true,
        cableAmpacity: Double = 0.0,
        loadType: String = "General"
    ): CalculationResult {
        if (loadCurrent <= 0) {
            return CalculationResult("", "", formula = "In >= 1.25 × I_load (continuous)", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Load current must be positive.")
        }

        // NEC 210.20 requires 125% rating for continuous loads (>3 hours)
        val designCurrent = if (isContinuous) loadCurrent * 1.25 else loadCurrent
        val recommendedRating = standardMcbRatings.firstOrNull { it >= designCurrent } ?: standardMcbRatings.last()

        val recommendedCurve = when (loadType.lowercase()) {
            "motor", "compressor", "air conditioner", "pump" -> "C-Curve (5-10× In) or D-Curve (10-20× In) for high inrush"
            "lighting", "resistive", "heater" -> "B-Curve (3-5× In) or C-Curve (5-10× In)"
            "transformer", "welder", "x-ray" -> "D-Curve (10-20× In)"
            else -> "C-Curve (standard commercial / mixed loads)"
        }

        var warning: String? = null
        if (cableAmpacity > 0 && cableAmpacity < recommendedRating) {
            warning = "CRITICAL COORDINATION WARNING: Cable ampacity (${formatNum(cableAmpacity, 1)} A) is LESS than the breaker rating (${recommendedRating} A). Cable will not be protected against overload! Increase cable size or reduce breaker rating."
        }

        return CalculationResult(
            primaryValue = "$recommendedRating A",
            primaryUnit = "MCB / MCCB",
            secondaryValues = mapOf(
                "Calculated Design Current" to "${formatNum(designCurrent, 1)} A",
                "Continuous Factor Applied" to if (isContinuous) "125% (NEC rule)" else "100%",
                "Recommended Trip Curve" to recommendedCurve,
                "Coordination Status" to if (warning != null) "FAIL: Cable Underprotected" else "PASS: Coordinated"
            ),
            formula = if (isContinuous) "In >= 1.25 × I_load" else "In >= I_load",
            substitution = "In >= ${if (isContinuous) "1.25 × " else ""}${formatNum(loadCurrent)} A = ${formatNum(designCurrent, 1)} A",
            steps = listOf(
                "Step 1: Determine continuous load requirement (NEC continuous factor = ${if (isContinuous) "1.25" else "1.0"})",
                "Step 2: Calculated minimum trip threshold = ${formatNum(designCurrent, 1)} A",
                "Step 3: Select next standard industrial breaker size = $recommendedRating A",
                "Step 4: Check cable protection coordination (Iz >= In >= Ib)"
            ),
            standardNote = "IEC 60898 / IEC 60947-2 / NEC 240 Overcurrent Protection rules.",
            warning = warning
        )
    }

    // -------------------------------------------------------------
    // 6. MOTOR CALCULATIONS
    // -------------------------------------------------------------
    fun calculateMotorCurrent(
        powerValue: Double,
        isHp: Boolean = true,
        voltage: Double = 400.0,
        pf: Double = 0.85,
        efficiencyPct: Double = 90.0,
        isThreePhase: Boolean = true
    ): CalculationResult {
        if (powerValue <= 0 || voltage <= 0 || pf <= 0 || efficiencyPct <= 0) {
            return CalculationResult("", "", formula = "I = P / (k × V × η × PF)", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Parameters must be positive.")
        }
        val powerKw = if (isHp) powerValue * 0.7457 else powerValue
        val powerHp = if (isHp) powerValue else powerValue / 0.7457
        val eta = efficiencyPct / 100.0

        val flc = if (isThreePhase) {
            (powerKw * 1000.0) / (sqrt(3.0) * voltage * eta * pf)
        } else {
            (powerKw * 1000.0) / (voltage * eta * pf)
        }

        val dolStartingCurrent = flc * 6.5
        val starDeltaStartingCurrent = dolStartingCurrent / 3.0
        val vfdStartingCurrent = flc * 1.2

        // Thermal overload setting: typically 1.0 to 1.15 x FLC
        val overloadSetting = flc * 1.15

        return CalculationResult(
            primaryValue = formatNum(flc, 1),
            primaryUnit = "A (FLC)",
            secondaryValues = mapOf(
                "Motor Mechanical Output" to "${formatNum(powerKw, 2)} kW (${formatNum(powerHp, 2)} HP)",
                "DOL Starting Current (6.5x)" to "${formatNum(dolStartingCurrent, 1)} A",
                "Star-Delta Starting (2.2x)" to "${formatNum(starDeltaStartingCurrent, 1)} A",
                "VFD Starting Current (1.2x)" to "${formatNum(vfdStartingCurrent, 1)} A",
                "Thermal Overload Range" to "${formatNum(flc, 1)} A – ${formatNum(overloadSetting, 1)} A",
                "System Type" to if (isThreePhase) "3-Phase AC" else "1-Phase AC"
            ),
            formula = if (isThreePhase) "I_FLC = (P_kW × 1000) / (√3 × V × η × PF)" else "I_FLC = (P_kW × 1000) / (V × η × PF)",
            substitution = "I_FLC = (${formatNum(powerKw, 2)} × 1000) / (${if (isThreePhase) "1.732 × " else ""}${formatNum(voltage)} × ${formatNum(eta)} × ${formatNum(pf)})",
            steps = listOf(
                "Step 1: Convert mechanical output power = ${formatNum(powerKw, 2)} kW",
                "Step 2: Electrical input power Pin = Pout / η = ${formatNum(powerKw / eta, 2)} kW",
                "Step 3: Full Load Current I_flc = Pin / (${if (isThreePhase) "√3 × V × PF" else "V × PF"}) = ${formatNum(flc, 1)} A",
                "Step 4: Estimate inrush starting currents and thermal protection ranges"
            ),
            standardNote = "NEMA MG-1 / IEC 60034-1 Standard for Three-Phase Induction Motors."
        )
    }

    fun calculateMotorTorque(powerKw: Double, rpm: Double): CalculationResult {
        if (powerKw <= 0 || rpm <= 0) {
            return CalculationResult("", "", formula = "T = (9550 × P_kW) / RPM", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Power and RPM must be positive.")
        }
        val torqueNm = (9550.0 * powerKw) / rpm
        val torqueLbFt = torqueNm * 0.737562
        val hp = powerKw / 0.7457

        return CalculationResult(
            primaryValue = formatNum(torqueNm, 1),
            primaryUnit = "N·m",
            secondaryValues = mapOf(
                "Imperial Torque" to "${formatNum(torqueLbFt, 1)} lb-ft",
                "Power" to "${formatNum(powerKw, 2)} kW (${formatNum(hp, 2)} HP)",
                "Shaft Speed" to "${formatNum(rpm, 0)} RPM"
            ),
            formula = "T = (9550 × P_kW) / RPM  |  T = (5252 × HP) / RPM",
            substitution = "T = (9550 × ${formatNum(powerKw, 2)}) / ${formatNum(rpm, 0)}",
            steps = listOf(
                "Step 1: Mechanical power P = ${formatNum(powerKw, 2)} kW",
                "Step 2: Angular velocity ω = 2π × (RPM / 60) = ${formatNum((2.0 * Math.PI * rpm) / 60.0, 1)} rad/s",
                "Step 3: Torque T = P / ω = (9550 × P) / RPM = ${formatNum(torqueNm, 1)} N·m"
            ),
            standardNote = "Mechanical motor torque standard relationship."
        )
    }

    fun calculateMotorSlip(frequency: Double, poles: Int, actualRpm: Double): CalculationResult {
        if (frequency <= 0 || poles <= 0 || actualRpm <= 0) {
            return CalculationResult("", "", formula = "Ns = (120 × f) / P", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Parameters must be positive.")
        }
        val syncSpeed = (120.0 * frequency) / poles
        val slipRpm = syncSpeed - actualRpm
        val slipPct = (slipRpm / syncSpeed) * 100.0

        return CalculationResult(
            primaryValue = formatNum(slipPct, 2),
            primaryUnit = "%",
            secondaryValues = mapOf(
                "Synchronous Speed (Ns)" to "${formatNum(syncSpeed, 0)} RPM",
                "Actual Rotor Speed (Nr)" to "${formatNum(actualRpm, 0)} RPM",
                "Slip Speed" to "${formatNum(slipRpm, 0)} RPM",
                "Poles Count" to "$poles poles"
            ),
            formula = "Ns = (120 × f) / P,  Slip = ((Ns - Nr) / Ns) × 100%",
            substitution = "Ns = (120 × ${formatNum(frequency)}) / $poles = ${formatNum(syncSpeed, 0)} RPM",
            steps = listOf(
                "Step 1: Calculate synchronous magnetic field speed Ns = (120 × $frequency) / $poles = ${formatNum(syncSpeed, 0)} RPM",
                "Step 2: Calculate difference (slip speed) = ${formatNum(syncSpeed, 0)} - ${formatNum(actualRpm, 0)} = ${formatNum(slipRpm, 0)} RPM",
                "Step 3: Slip percentage = (${formatNum(slipRpm, 0)} / ${formatNum(syncSpeed, 0)}) × 100 = ${formatNum(slipPct, 2)}%"
            ),
            standardNote = "Induction motor electromagnetic slip formulation."
        )
    }

    fun calculateMotorPower(
        voltage: Double,
        current: Double,
        powerFactor: Double = 0.85,
        efficiencyPct: Double = 90.0,
        isThreePhase: Boolean = true
    ): CalculationResult {
        if (voltage <= 0 || current <= 0) {
            return CalculationResult("", "", formula = "P = k × V × I × cosφ × η", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Voltage and Current must be positive.")
        }
        val pf = powerFactor.coerceIn(0.1, 1.0)
        val eta = (efficiencyPct.coerceIn(10.0, 100.0)) / 100.0
        val k = if (isThreePhase) sqrt(3.0) else 1.0

        val inputPowerKw = (k * voltage * current * pf) / 1000.0
        val shaftPowerKw = inputPowerKw * eta
        val shaftPowerHp = shaftPowerKw / 0.7457
        val apparentPowerKva = (k * voltage * current) / 1000.0
        val reactivePowerKvar = apparentPowerKva * sin(acos(pf))

        return CalculationResult(
            primaryValue = formatNum(shaftPowerKw, 2),
            primaryUnit = "kW (Shaft Output)",
            secondaryValues = mapOf(
                "Mechanical Output (HP)" to "${formatNum(shaftPowerHp, 2)} HP",
                "Electrical Input (Pin)" to "${formatNum(inputPowerKw, 2)} kW",
                "Apparent Power (S)" to "${formatNum(apparentPowerKva, 2)} kVA",
                "Reactive Power (Q)" to "${formatNum(reactivePowerKvar, 2)} kVAR",
                "Efficiency (η)" to "${formatNum(efficiencyPct, 1)} %",
                "Power Factor (cosφ)" to formatNum(pf, 2),
                "Configuration" to if (isThreePhase) "3-Phase AC" else "1-Phase AC"
            ),
            formula = if (isThreePhase) "P_out = √3 × V × I × cosφ × η" else "P_out = V × I × cosφ × η",
            substitution = "P_out = (${formatNum(k, 3)} × ${formatNum(voltage)} V × ${formatNum(current)} A × ${formatNum(pf, 2)} × ${formatNum(eta, 2)}) / 1000",
            steps = listOf(
                "Step 1: Calculate apparent power S = ${formatNum(k, 3)} × ${formatNum(voltage)} × ${formatNum(current)} / 1000 = ${formatNum(apparentPowerKva, 2)} kVA",
                "Step 2: Calculate electrical active power input Pin = S × cosφ = ${formatNum(inputPowerKw, 2)} kW",
                "Step 3: Calculate mechanical shaft output Pout = Pin × η = ${formatNum(shaftPowerKw, 2)} kW (${formatNum(shaftPowerHp, 2)} HP)"
            ),
            standardNote = "IEC 60034-1 / NEMA MG-1 Standard Motor Power Equations."
        )
    }

    fun calculateMotorVoltage(
        powerKw: Double,
        current: Double,
        powerFactor: Double = 0.85,
        efficiencyPct: Double = 90.0,
        isThreePhase: Boolean = true
    ): CalculationResult {
        if (powerKw <= 0 || current <= 0) {
            return CalculationResult("", "", formula = "V = (P × 1000) / (k × I × cosφ × η)", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Power and Current must be positive.")
        }
        val pf = powerFactor.coerceIn(0.1, 1.0)
        val eta = (efficiencyPct.coerceIn(10.0, 100.0)) / 100.0
        val k = if (isThreePhase) sqrt(3.0) else 1.0

        val voltage = (powerKw * 1000.0) / (k * current * pf * eta)
        val phaseVoltage = if (isThreePhase) voltage / sqrt(3.0) else voltage

        // Nearest standard industrial voltage
        val standardVoltages = listOf(115.0, 120.0, 208.0, 220.0, 230.0, 240.0, 380.0, 400.0, 415.0, 460.0, 480.0, 575.0, 690.0)
        val nearestStd = standardVoltages.minByOrNull { Math.abs(it - voltage) } ?: 400.0

        return CalculationResult(
            primaryValue = formatNum(voltage, 1),
            primaryUnit = "V (Line Voltage)",
            secondaryValues = mapOf(
                "Phase Voltage (Vph)" to "${formatNum(phaseVoltage, 1)} V",
                "Nearest Standard Rating" to "${formatNum(nearestStd, 0)} V",
                "Power Factor" to formatNum(pf, 2),
                "Assumed Efficiency" to "${formatNum(efficiencyPct, 1)} %",
                "System Type" to if (isThreePhase) "3-Phase AC" else "1-Phase AC"
            ),
            formula = if (isThreePhase) "V = (P_kW × 1000) / (√3 × I × cosφ × η)" else "V = (P_kW × 1000) / (I × cosφ × η)",
            substitution = "V = (${formatNum(powerKw, 2)} × 1000) / (${formatNum(k, 3)} × ${formatNum(current)} A × ${formatNum(pf, 2)} × ${formatNum(eta, 2)})",
            steps = listOf(
                "Step 1: Calculate total electrical input required Pin = Pout / η = ${formatNum(powerKw / eta, 2)} kW",
                "Step 2: Calculate line voltage V = (${formatNum(powerKw / eta, 2)} × 1000) / (${formatNum(k, 3)} × ${formatNum(current)} A × ${formatNum(pf, 2)}) = ${formatNum(voltage, 1)} V",
                "Step 3: Phase voltage in Star/Wye = V / √3 = ${formatNum(phaseVoltage, 1)} V"
            ),
            standardNote = "Derived from electrical power conservation in AC induction machines."
        )
    }

    fun calculateMotorPowerFactor(
        powerKw: Double,
        voltage: Double,
        current: Double,
        efficiencyPct: Double = 90.0,
        isThreePhase: Boolean = true
    ): CalculationResult {
        if (powerKw <= 0 || voltage <= 0 || current <= 0) {
            return CalculationResult("", "", formula = "cosφ = (P × 1000) / (k × V × I × η)", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Parameters must be positive.")
        }
        val eta = (efficiencyPct.coerceIn(10.0, 100.0)) / 100.0
        val k = if (isThreePhase) sqrt(3.0) else 1.0

        val apparentPowerKva = (k * voltage * current) / 1000.0
        val inputPowerKw = powerKw / eta
        val rawPf = inputPowerKw / apparentPowerKva
        val pf = rawPf.coerceIn(0.1, 1.0)
        val angleDeg = Math.toDegrees(acos(pf))
        val reactiveKvar = apparentPowerKva * sin(Math.toRadians(angleDeg))

        val warning = if (rawPf > 1.0) "Calculated power factor exceeded 1.0. Check if current or voltage was understated for this power rating." else null

        return CalculationResult(
            primaryValue = formatNum(pf, 3),
            primaryUnit = "cos φ",
            secondaryValues = mapOf(
                "Phase Angle (φ)" to "${formatNum(angleDeg, 1)}°",
                "Apparent Power (S)" to "${formatNum(apparentPowerKva, 2)} kVA",
                "Reactive Power (Q)" to "${formatNum(reactiveKvar, 2)} kVAR",
                "Electrical Input (Pin)" to "${formatNum(inputPowerKw, 2)} kW",
                "Efficiency (η)" to "${formatNum(efficiencyPct, 1)} %"
            ),
            formula = if (isThreePhase) "cosφ = (P_out × 1000) / (√3 × V × I × η)" else "cosφ = (P_out × 1000) / (V × I × η)",
            substitution = "cosφ = (${formatNum(powerKw, 2)} × 1000) / (${formatNum(k, 3)} × ${formatNum(voltage)} V × ${formatNum(current)} A × ${formatNum(eta, 2)})",
            steps = listOf(
                "Step 1: Calculate apparent power S = ${formatNum(k, 3)} × ${formatNum(voltage)} × ${formatNum(current)} / 1000 = ${formatNum(apparentPowerKva, 2)} kVA",
                "Step 2: Electrical input power Pin = Pout / η = ${formatNum(powerKw, 2)} / ${formatNum(eta, 2)} = ${formatNum(inputPowerKw, 2)} kW",
                "Step 3: Power Factor cosφ = Pin / S = ${formatNum(inputPowerKw, 2)} / ${formatNum(apparentPowerKva, 2)} = ${formatNum(pf, 3)}"
            ),
            standardNote = "Motor power factor represents the ratio of active real power to total apparent power drawn.",
            warning = warning
        )
    }

    fun calculateMotorEfficiency(
        powerOutKw: Double,
        voltage: Double,
        current: Double,
        powerFactor: Double = 0.85,
        isThreePhase: Boolean = true
    ): CalculationResult {
        if (powerOutKw <= 0 || voltage <= 0 || current <= 0) {
            return CalculationResult("", "", formula = "η = (P_out / P_in) × 100%", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Parameters must be positive.")
        }
        val pf = powerFactor.coerceIn(0.1, 1.0)
        val k = if (isThreePhase) sqrt(3.0) else 1.0

        val powerInKw = (k * voltage * current * pf) / 1000.0
        val rawEta = (powerOutKw / powerInKw) * 100.0
        val etaClamped = rawEta.coerceIn(10.0, 99.5)
        val lossesKw = Math.max(0.0, powerInKw - powerOutKw)

        val ieClass = when {
            etaClamped >= 95.0 -> "IE4 (Super Premium Efficiency)"
            etaClamped >= 92.5 -> "IE3 (Premium Efficiency)"
            etaClamped >= 88.0 -> "IE2 (High Efficiency)"
            etaClamped >= 82.0 -> "IE1 (Standard Efficiency)"
            else -> "Below Standard (High losses)"
        }

        val warning = if (rawEta > 100.0) "Calculated efficiency exceeded 100%. Measured current or voltage is too low for stated output power." else null

        return CalculationResult(
            primaryValue = "${formatNum(etaClamped, 1)} %",
            primaryUnit = "Efficiency (η)",
            secondaryValues = mapOf(
                "Energy Efficiency Class" to ieClass,
                "Electrical Input (Pin)" to "${formatNum(powerInKw, 2)} kW",
                "Mechanical Output (Pout)" to "${formatNum(powerOutKw, 2)} kW (${formatNum(powerOutKw / 0.7457, 2)} HP)",
                "Total Machine Losses" to "${formatNum(lossesKw, 2)} kW (${formatNum((lossesKw / powerInKw) * 100.0, 1)}%)",
                "Input Apparent Power" to "${formatNum((k * voltage * current) / 1000.0, 2)} kVA"
            ),
            formula = if (isThreePhase) "η = P_out / (√3 × V × I × cosφ)" else "η = P_out / (V × I × cosφ)",
            substitution = "η = ${formatNum(powerOutKw, 2)} kW / ((${formatNum(k, 3)} × ${formatNum(voltage)} V × ${formatNum(current)} A × ${formatNum(pf, 2)}) / 1000)",
            steps = listOf(
                "Step 1: Calculate active electrical input power Pin = ${formatNum(k, 3)} × ${formatNum(voltage)} × ${formatNum(current)} × ${formatNum(pf, 2)} / 1000 = ${formatNum(powerInKw, 2)} kW",
                "Step 2: Motor efficiency η = (Pout / Pin) × 100 = (${formatNum(powerOutKw, 2)} / ${formatNum(powerInKw, 2)}) × 100 = ${formatNum(etaClamped, 1)}%",
                "Step 3: Total machine losses Ploss = Pin - Pout = ${formatNum(lossesKw, 2)} kW"
            ),
            standardNote = "IEC 60034-30-1 standard for energy efficiency classes of low-voltage AC motors.",
            warning = warning
        )
    }

    fun calculateSteinmetzConnection(
        powerKw: Double,
        voltage: Double = 230.0,
        frequency: Double = 50.0,
        isDeltaConnection: Boolean = true
    ): CalculationResult {
        if (powerKw <= 0 || voltage <= 0 || frequency <= 0) {
            return CalculationResult("", "", formula = "C_run = (P × 10^6) / (2π × f × V^2)", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Parameters must be positive.")
        }
        // Steinmetz formula:
        // For Delta connection: C_run ≈ 70 * P_kW * (230/V)^2 (at 50Hz) or C = (P * 10^6)/(2*pi*f*V^2) * factor
        // For Star connection: C_run ≈ 48 * P_kW * (230/V)^2 (at 50Hz)
        val freqFactor = 50.0 / frequency
        val voltRatio = 230.0 / voltage
        val voltFactor = voltRatio * voltRatio

        val baseUfPerKw = if (isDeltaConnection) 70.0 else 48.0
        val runCapUf = baseUfPerKw * powerKw * voltFactor * freqFactor
        val startCapUf = runCapUf * 2.5
        val minCapVoltage = voltage * 1.35
        val deratedPowerKw = powerKw * (if (isDeltaConnection) 0.75 else 0.70)
        val deratedHp = deratedPowerKw / 0.7457

        return CalculationResult(
            primaryValue = "${formatNum(runCapUf, 1)} µF",
            primaryUnit = "Run Capacitor (C_run)",
            secondaryValues = mapOf(
                "Start Capacitor (C_start)" to "${formatNum(startCapUf, 0)} µF (2.5x C_run)",
                "Minimum Capacitor Voltage" to "≥ ${formatNum(minCapVoltage, 0)} V AC (Use 450V AC)",
                "Derated Single-Phase Output" to "${formatNum(deratedPowerKw, 2)} kW (${formatNum(deratedHp, 2)} HP, ~75%)",
                "Starting Torque" to "~30% – 45% of 3-Phase Nominal",
                "Winding Connection" to if (isDeltaConnection) "Delta (Δ) — Recommended for 230V" else "Star (Y)",
                "Supply Voltage" to "${formatNum(voltage, 0)} V 1-Phase (${formatNum(frequency, 0)} Hz)"
            ),
            formula = if (isDeltaConnection) "C_run ≈ 70 × P_kW × (230/V)² × (50/f)" else "C_run ≈ 48 × P_kW × (230/V)² × (50/f)",
            substitution = "C_run = ${baseUfPerKw} × ${formatNum(powerKw, 2)} × (${formatNum(voltRatio, 2)})² × (${formatNum(freqFactor, 2)})",
            steps = listOf(
                "Step 1: Winding configuration: ${if (isDeltaConnection) "Delta (Δ) connection delivers higher starting torque on 230V single-phase" else "Star (Y) connection"}",
                "Step 2: Calculate continuous run capacitance C_run = ${formatNum(runCapUf, 1)} µF at rated line voltage ${formatNum(voltage, 0)} V",
                "Step 3: Start capacitor (if high starting torque required) C_start = 2.0 to 2.5 × C_run = ${formatNum(startCapUf, 0)} µF with centrifugal switch or timer",
                "Step 4: Capacitor voltage rating must withstand AC back-EMF: Minimum rating 450V AC metallized polypropylene (MKP)",
                "Step 5: Derated mechanical power output: Expect ~70-80% of nominal 3-phase nameplate = ${formatNum(deratedPowerKw, 2)} kW"
            ),
            standardNote = "Steinmetz circuit conversion for operating 3-phase induction motors on single-phase AC supplies."
        )
    }

    fun calculateSinglePhaseRunCapacitor(
        powerKw: Double,
        voltage: Double = 230.0,
        frequency: Double = 50.0,
        efficiencyPct: Double = 80.0,
        powerFactor: Double = 0.85
    ): CalculationResult {
        if (powerKw <= 0 || voltage <= 0 || frequency <= 0) {
            return CalculationResult("", "", formula = "C = (P × 10^6) / (2π × f × V^2 × cosφ)", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Parameters must be positive.")
        }
        val pf = powerFactor.coerceIn(0.2, 1.0)
        val eta = (efficiencyPct.coerceIn(10.0, 100.0)) / 100.0

        // Standard PSC motor formula: C (µF) = (P_kW * 10^6) / (2 * π * f * V^2 * cosφ)
        val capUf = (powerKw * 1_000_000.0) / (2.0 * Math.PI * frequency * voltage * voltage * pf)
        val minCapVoltage = voltage * 1.4 // Back EMF during operation
        val flc = (powerKw * 1000.0) / (voltage * pf * eta)

        return CalculationResult(
            primaryValue = "${formatNum(capUf, 1)} µF",
            primaryUnit = "Run Capacitor",
            secondaryValues = mapOf(
                "Capacitor Voltage Rating" to "≥ ${formatNum(minCapVoltage, 0)} V AC (Use 450V AC class)",
                "Motor Full Load Current" to "${formatNum(flc, 1)} A",
                "Mechanical Power" to "${formatNum(powerKw, 2)} kW (${formatNum(powerKw / 0.7457, 2)} HP)",
                "Capacitor Type" to "Metallized Polypropylene (MKP) Continuous Run",
                "Supply Voltage & Freq" to "${formatNum(voltage, 0)} V AC @ ${formatNum(frequency, 0)} Hz"
            ),
            formula = "C = (P_kW × 10⁶) / (2π × f × V² × cosφ)",
            substitution = "C = (${formatNum(powerKw, 2)} × 10⁶) / (2π × ${formatNum(frequency, 0)} × ${formatNum(voltage, 0)}² × ${formatNum(pf, 2)})",
            steps = listOf(
                "Step 1: Calculate required 90° auxiliary winding phase displacement reactive power",
                "Step 2: Continuous run capacitance C = (${formatNum(powerKw, 2)} × 10⁶) / (6.283 × ${formatNum(frequency, 0)} × ${formatNum(voltage*voltage, 0)} × ${formatNum(pf, 2)}) = ${formatNum(capUf, 1)} µF",
                "Step 3: Recommended dielectric rating: Minimum 400V - 450V AC continuously rated capacitor",
                "Step 4: Approximate full load current of the motor = ${formatNum(flc, 1)} A"
            ),
            standardNote = "Permanent Split Capacitor (PSC) and capacitor-run single-phase motor sizing standard."
        )
    }

    fun calculateMotorSpeed(
        frequency: Double = 50.0,
        poles: Int = 4,
        slipPct: Double = 4.0
    ): CalculationResult {
        if (frequency <= 0 || poles <= 0) {
            return CalculationResult("", "", formula = "Ns = (120 × f) / P", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Frequency and Poles must be positive.")
        }
        val syncSpeed = (120.0 * frequency) / poles
        val s = (slipPct.coerceIn(0.0, 30.0)) / 100.0
        val rotorSpeed = syncSpeed * (1.0 - s)
        val slipRpm = syncSpeed - rotorSpeed
        val angularSpeedRadS = (2.0 * Math.PI * rotorSpeed) / 60.0
        val slipFrequency = s * frequency

        return CalculationResult(
            primaryValue = "${formatNum(rotorSpeed, 0)} RPM",
            primaryUnit = "Rotor Speed (Nr)",
            secondaryValues = mapOf(
                "Synchronous Speed (Ns)" to "${formatNum(syncSpeed, 0)} RPM",
                "Slip RPM (Ns - Nr)" to "${formatNum(slipRpm, 0)} RPM",
                "Slip Percentage" to "${formatNum(slipPct, 2)} %",
                "Angular Speed (ω)" to "${formatNum(angularSpeedRadS, 1)} rad/s",
                "Rotor Slip Frequency" to "${formatNum(slipFrequency, 2)} Hz",
                "Stator Poles" to "$poles Poles (${poles / 2} pole pairs)"
            ),
            formula = "Ns = (120 × f) / P  |  Nr = Ns × (1 - s)",
            substitution = "Ns = (120 × ${formatNum(frequency, 0)}) / $poles = ${formatNum(syncSpeed, 0)} RPM | Nr = ${formatNum(syncSpeed, 0)} × (1 - ${formatNum(s, 3)})",
            steps = listOf(
                "Step 1: Calculate rotating magnetic stator field speed Ns = (120 × $frequency) / $poles = ${formatNum(syncSpeed, 0)} RPM",
                "Step 2: Apply rotor electromagnetic slip ($slipPct%)",
                "Step 3: Full load mechanical shaft speed Nr = ${formatNum(syncSpeed, 0)} × (1 - ${formatNum(s, 3)}) = ${formatNum(rotorSpeed, 0)} RPM",
                "Step 4: Angular velocity ω = (2π × Nr) / 60 = ${formatNum(angularSpeedRadS, 1)} rad/s"
            ),
            standardNote = "IEC 60034-1 Synchronous and asynchronous shaft speed standard formulation."
        )
    }

    fun calculateMotorMaxTorque(
        powerKw: Double,
        rpm: Double,
        breakdownRatio: Double = 2.5
    ): CalculationResult {
        if (powerKw <= 0 || rpm <= 0) {
            return CalculationResult("", "", formula = "T_fl = (9550 × P) / RPM, T_max = T_fl × Ratio", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Power and RPM must be positive.")
        }
        val nominalTorqueNm = (9550.0 * powerKw) / rpm
        val ratio = breakdownRatio.coerceIn(1.5, 4.0)
        val maxTorqueNm = nominalTorqueNm * ratio

        val nominalTorqueLbFt = nominalTorqueNm * 0.737562
        val maxTorqueLbFt = maxTorqueNm * 0.737562
        val hp = powerKw / 0.7457
        val startingTorqueEst = nominalTorqueNm * 1.5 // typical NEMA B / Design N

        return CalculationResult(
            primaryValue = "${formatNum(nominalTorqueNm, 1)} N·m",
            primaryUnit = "Full-Load Torque (T_fl)",
            secondaryValues = mapOf(
                "Maximum / Breakdown Torque (T_max)" to "${formatNum(maxTorqueNm, 1)} N·m (${formatNum(maxTorqueLbFt, 1)} lb-ft)",
                "Full-Load Torque (Imperial)" to "${formatNum(nominalTorqueLbFt, 1)} lb-ft",
                "Breakdown Torque Ratio (T_max/T_fl)" to "${formatNum(ratio, 2)}x",
                "Estimated Starting Torque" to "~${formatNum(startingTorqueEst, 1)} N·m (1.5x T_fl)",
                "Shaft Output Power" to "${formatNum(powerKw, 2)} kW (${formatNum(hp, 2)} HP)",
                "Rated Speed" to "${formatNum(rpm, 0)} RPM"
            ),
            formula = "T_fl = (9550 × P_kW) / RPM  |  T_max = T_fl × (T_max/T_fl)",
            substitution = "T_fl = (9550 × ${formatNum(powerKw, 2)}) / ${formatNum(rpm, 0)} = ${formatNum(nominalTorqueNm, 1)} N·m | T_max = ${formatNum(nominalTorqueNm, 1)} × ${formatNum(ratio, 2)}",
            steps = listOf(
                "Step 1: Calculate nominal rated full-load torque T_fl = (9550 × ${formatNum(powerKw, 2)}) / ${formatNum(rpm, 0)} = ${formatNum(nominalTorqueNm, 1)} N·m",
                "Step 2: Convert to imperial units T_fl = ${formatNum(nominalTorqueLbFt, 1)} lb-ft",
                "Step 3: Calculate maximum breakdown stall torque T_max = ${formatNum(nominalTorqueNm, 1)} × ${formatNum(ratio, 2)} = ${formatNum(maxTorqueNm, 1)} N·m (${formatNum(maxTorqueLbFt, 1)} lb-ft)",
                "Step 4: NEMA Design B motors typically deliver 200% to 280% breakdown torque before stalling."
            ),
            standardNote = "NEMA MG-1 Part 12 / IEC 60034-1 Induction Motor Torque Characteristics."
        )
    }

    fun calculateMotorFLC_NEC(
        hp: Double,
        voltage: Double = 460.0,
        isThreePhase: Boolean = true
    ): CalculationResult {
        if (hp <= 0) {
            return CalculationResult("", "", formula = "NEC Table 430.248 / 430.250", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Horsepower must be positive.")
        }

        // NEC Table 430.250 (3-Phase) and Table 430.248 (1-Phase)
        val nec3PhaseTable = mapOf(
            0.5 to mapOf(208.0 to 2.4, 230.0 to 2.2, 460.0 to 1.1, 575.0 to 0.9),
            0.75 to mapOf(208.0 to 3.5, 230.0 to 3.2, 460.0 to 1.6, 575.0 to 1.3),
            1.0 to mapOf(208.0 to 4.6, 230.0 to 4.2, 460.0 to 2.1, 575.0 to 1.7),
            1.5 to mapOf(208.0 to 6.6, 230.0 to 6.0, 460.0 to 3.0, 575.0 to 2.4),
            2.0 to mapOf(208.0 to 7.5, 230.0 to 6.8, 460.0 to 3.4, 575.0 to 2.7),
            3.0 to mapOf(208.0 to 10.6, 230.0 to 9.6, 460.0 to 4.8, 575.0 to 3.9),
            5.0 to mapOf(208.0 to 16.7, 230.0 to 15.2, 460.0 to 7.6, 575.0 to 6.1),
            7.5 to mapOf(208.0 to 24.2, 230.0 to 22.0, 460.0 to 11.0, 575.0 to 9.0),
            10.0 to mapOf(208.0 to 30.8, 230.0 to 28.0, 460.0 to 14.0, 575.0 to 11.0),
            15.0 to mapOf(208.0 to 46.2, 230.0 to 42.0, 460.0 to 21.0, 575.0 to 17.0),
            20.0 to mapOf(208.0 to 59.4, 230.0 to 54.0, 460.0 to 27.0, 575.0 to 22.0),
            25.0 to mapOf(208.0 to 74.8, 230.0 to 68.0, 460.0 to 34.0, 575.0 to 27.0),
            30.0 to mapOf(208.0 to 88.0, 230.0 to 80.0, 460.0 to 40.0, 575.0 to 32.0),
            40.0 to mapOf(208.0 to 114.0, 230.0 to 104.0, 460.0 to 52.0, 575.0 to 41.0),
            50.0 to mapOf(208.0 to 143.0, 230.0 to 130.0, 460.0 to 65.0, 575.0 to 52.0),
            60.0 to mapOf(208.0 to 169.0, 230.0 to 154.0, 460.0 to 77.0, 575.0 to 62.0),
            75.0 to mapOf(208.0 to 211.0, 230.0 to 192.0, 460.0 to 96.0, 575.0 to 77.0),
            100.0 to mapOf(208.0 to 273.0, 230.0 to 248.0, 460.0 to 124.0, 575.0 to 99.0),
            125.0 to mapOf(208.0 to 343.0, 230.0 to 312.0, 460.0 to 156.0, 575.0 to 125.0),
            150.0 to mapOf(208.0 to 396.0, 230.0 to 360.0, 460.0 to 180.0, 575.0 to 144.0),
            200.0 to mapOf(208.0 to 528.0, 230.0 to 480.0, 460.0 to 240.0, 575.0 to 192.0)
        )

        val nec1PhaseTable = mapOf(
            0.166 to mapOf(115.0 to 4.4, 230.0 to 2.2),
            0.25 to mapOf(115.0 to 5.8, 230.0 to 2.9),
            0.33 to mapOf(115.0 to 7.2, 230.0 to 3.6),
            0.5 to mapOf(115.0 to 9.8, 230.0 to 4.9),
            0.75 to mapOf(115.0 to 13.8, 230.0 to 6.9),
            1.0 to mapOf(115.0 to 16.0, 208.0 to 8.8, 230.0 to 8.0),
            1.5 to mapOf(115.0 to 20.0, 208.0 to 11.0, 230.0 to 10.0),
            2.0 to mapOf(115.0 to 24.0, 208.0 to 13.2, 230.0 to 12.0),
            3.0 to mapOf(115.0 to 34.0, 208.0 to 18.7, 230.0 to 17.0),
            5.0 to mapOf(115.0 to 56.0, 208.0 to 30.8, 230.0 to 28.0),
            7.5 to mapOf(208.0 to 44.0, 230.0 to 40.0),
            10.0 to mapOf(208.0 to 55.0, 230.0 to 50.0)
        )

        val table = if (isThreePhase) nec3PhaseTable else nec1PhaseTable
        val standardHps = table.keys.sorted()
        val closestHp = standardHps.minByOrNull { Math.abs(it - hp) } ?: 5.0
        val hpData = table[closestHp] ?: emptyMap()

        val voltages = hpData.keys.toList()
        val closestVolt = voltages.minByOrNull { Math.abs(it - voltage) } ?: (if (isThreePhase) 460.0 else 230.0)

        val flc = hpData[closestVolt] ?: ((hp * 746.0) / ((if (isThreePhase) sqrt(3.0) else 1.0) * voltage * 0.85 * 0.88))

        // NEC sizing rules
        val minConductorAmpacity = flc * 1.25 // NEC 430.22
        val maxInverseTimeBreaker = flc * 2.50 // NEC 430.52 (Inverse time breaker 250%)
        val maxDualElementFuse = flc * 1.75 // NEC 430.52 (Dual element time delay fuse 175%)
        val maxNonTimeDelayFuse = flc * 3.00 // NEC 430.52 (Non-time delay fuse 300%)
        val maxOverload = flc * 1.15 // NEC 430.32 (Motors with 1.15 SF = 125%, standard = 115%)

        return CalculationResult(
            primaryValue = "${formatNum(flc, 1)} A",
            primaryUnit = "NEC Full Load Current (FLC)",
            secondaryValues = mapOf(
                "Min Conductor Ampacity (125%)" to "${formatNum(minConductorAmpacity, 1)} A (NEC 430.22)",
                "Max Inverse-Time Breaker (250%)" to "${formatNum(maxInverseTimeBreaker, 1)} A (NEC 430.52)",
                "Max Dual-Element Fuse (175%)" to "${formatNum(maxDualElementFuse, 1)} A (NEC 430.52)",
                "Max Non-Time Delay Fuse (300%)" to "${formatNum(maxNonTimeDelayFuse, 1)} A",
                "Thermal Overload Trip (115%)" to "${formatNum(maxOverload, 1)} A (NEC 430.32)",
                "Selected Motor Size" to "$closestHp HP (${formatNum(closestHp * 0.7457, 2)} kW)",
                "System Voltage" to "${formatNum(closestVolt, 0)} V ${if (isThreePhase) "3-Phase" else "1-Phase"}"
            ),
            formula = if (isThreePhase) "NEC Table 430.250 (3-Phase FLC)" else "NEC Table 430.248 (1-Phase FLC)",
            substitution = "Motor: $closestHp HP @ ${formatNum(closestVolt, 0)} V -> FLC = ${formatNum(flc, 1)} A",
            steps = listOf(
                "Step 1: Look up official full-load current from NEC Table ${if (isThreePhase) "430.250" else "430.248"}: $closestHp HP at ${formatNum(closestVolt, 0)}V = ${formatNum(flc, 1)} A",
                "Step 2: Size branch circuit conductors at 125% of FLC per NEC 430.22 = 1.25 × ${formatNum(flc, 1)} A = ${formatNum(minConductorAmpacity, 1)} A",
                "Step 3: Size branch short-circuit protection: Inverse-time breaker allowed up to 250% = ${formatNum(maxInverseTimeBreaker, 1)} A (NEC 430.52)",
                "Step 4: Dual-element time-delay fuses allowed up to 175% = ${formatNum(maxDualElementFuse, 1)} A",
                "Step 5: Motor overload protection set between 115% and 125% of nameplate rating = ${formatNum(maxOverload, 1)} A"
            ),
            standardNote = "National Electrical Code (NEC / NFPA 70) Article 430: Motors, Motor Circuits, and Controllers."
        )
    }

    fun calculateMotorPFCorrection(
        motorPowerKw: Double,
        voltage: Double = 400.0,
        frequency: Double = 50.0,
        initialPf: Double = 0.80,
        targetPf: Double = 0.95,
        efficiencyPct: Double = 90.0,
        isThreePhase: Boolean = true
    ): CalculationResult {
        if (motorPowerKw <= 0 || voltage <= 0 || frequency <= 0) {
            return CalculationResult("", "", formula = "Qc = Pin × [tan(φ1) - tan(φ2)]", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Parameters must be positive.")
        }
        val pf1 = initialPf.coerceIn(0.3, 0.98)
        val pf2 = targetPf.coerceIn(pf1 + 0.01, 1.0)
        val eta = (efficiencyPct.coerceIn(50.0, 99.0)) / 100.0
        val k = if (isThreePhase) sqrt(3.0) else 1.0

        val pInKw = motorPowerKw / eta
        val phi1 = acos(pf1)
        val phi2 = acos(pf2)
        val tanPhi1 = tan(phi1)
        val tanPhi2 = tan(phi2)

        val qcKvar = pInKw * (tanPhi1 - tanPhi2)

        // Standard commercial capacitor bank ratings (kVAR)
        val standardSteps = listOf(1.0, 1.5, 2.0, 2.5, 3.0, 5.0, 7.5, 10.0, 12.5, 15.0, 20.0, 25.0, 30.0, 40.0, 50.0, 60.0, 75.0, 100.0)
        val nearestStep = standardSteps.minByOrNull { Math.abs(it - qcKvar) } ?: qcKvar

        // Capacitance calculations
        // 3-Phase Delta connection: Qc = 3 * 2 * pi * f * C_delta * V_L^2
        val cDeltaUf = if (isThreePhase) {
            (qcKvar * 1_000_000_000.0) / (3.0 * 2.0 * Math.PI * frequency * voltage * voltage)
        } else {
            (qcKvar * 1_000_000_000.0) / (2.0 * Math.PI * frequency * voltage * voltage)
        }
        val cStarUf = cDeltaUf * 3.0

        // Current calculations
        val i1 = (pInKw * 1000.0) / (k * voltage * pf1)
        val i2 = (pInKw * 1000.0) / (k * voltage * pf2)
        val deltaI = i1 - i2
        val currentSavedPct = (deltaI / i1) * 100.0

        // Cable Joule loss reduction % = (1 - (I2/I1)^2) * 100%
        val lossReductionPct = (1.0 - (i2 / i1).pow(2.0)) * 100.0

        // Apparent power released
        val s1Kva = pInKw / pf1
        val s2Kva = pInKw / pf2
        val releasedKva = s1Kva - s2Kva

        // Capacitor current
        val icAmps = (qcKvar * 1000.0) / (k * voltage)

        // Motor no-load current estimation & Self-Excitation Overvoltage Limit (IEEE 141 / IEC 60831)
        // No-load current of standard 2-8 pole induction motors is ~30% of rated FLC
        val noLoadCurrentEst = i1 * 0.30
        val maxSelfExcitationQc = 0.90 * (k * voltage * noLoadCurrentEst) / 1000.0
        val isSafeSelfExcitation = qcKvar <= maxSelfExcitationQc

        val warning = if (!isSafeSelfExcitation) {
            "SELF-EXCITATION RISK: Required Qc (${formatNum(qcKvar, 1)} kVAR) exceeds 90% of motor no-load magnetizing reactive power (${formatNum(maxSelfExcitationQc, 1)} kVAR). Connecting capacitors directly to motor terminals may cause dangerous transient overvoltages during switch-off! Recommendation: Install a dedicated contactor opening simultaneously with motor starter, or limit terminal bank to ${formatNum(maxSelfExcitationQc, 1)} kVAR and place remaining correction on the main bus."
        } else null

        return CalculationResult(
            primaryValue = "${formatNum(qcKvar, 2)} kVAR",
            primaryUnit = "Capacitor Bank Size (Qc)",
            secondaryValues = mapOf(
                "Delta (Δ) Capacitance" to "${formatNum(cDeltaUf, 1)} µF / phase",
                "Star (Y) Capacitance" to "${formatNum(cStarUf, 1)} µF / phase",
                "Nearest Standard Rating" to "$nearestStep kVAR",
                "Line Current Before" to "${formatNum(i1, 1)} A (@ PF ${formatNum(pf1, 2)})",
                "Line Current After" to "${formatNum(i2, 1)} A (@ PF ${formatNum(pf2, 2)})",
                "Current Reduction (ΔI)" to "-${formatNum(deltaI, 1)} A (${formatNum(currentSavedPct, 1)}% relief)",
                "Cable I²R Loss Reduction" to "-${formatNum(lossReductionPct, 1)} % thermal losses",
                "Released Capacity (kVA)" to "${formatNum(releasedKva, 1)} kVA (${formatNum(s1Kva, 1)} → ${formatNum(s2Kva, 1)} kVA)",
                "Capacitor Reactive Current" to "${formatNum(icAmps, 1)} A",
                "Max Self-Excitation Limit" to "≤ ${formatNum(maxSelfExcitationQc, 1)} kVAR (${if (isSafeSelfExcitation) "SAFE" else "EXCEEDED"})"
            ),
            formula = "Q_c = P_in × [tan(φ₁) - tan(φ₂)]   |   C_Δ = Q_c / (3 × 2πf × V²)",
            substitution = "Q_c = ${formatNum(pInKw, 2)} kW × [${formatNum(tanPhi1, 3)} - ${formatNum(tanPhi2, 3)}] = ${formatNum(qcKvar, 2)} kVAR",
            steps = listOf(
                "Step 1: Calculate active electrical input power Pin = P_shaft / η = ${formatNum(motorPowerKw, 2)} / ${formatNum(eta, 2)} = ${formatNum(pInKw, 2)} kW",
                "Step 2: Reactive power demand Qc = Pin × [tan(arccos(${formatNum(pf1, 2)})) - tan(arccos(${formatNum(pf2, 2)}))] = ${formatNum(qcKvar, 2)} kVAR",
                "Step 3: Three-phase Delta capacitance per phase C_Δ = (${formatNum(qcKvar, 2)} × 10⁹) / (3 × 2π × ${formatNum(frequency, 0)} × ${formatNum(voltage, 0)}²) = ${formatNum(cDeltaUf, 1)} µF",
                "Step 4: Line current reduces from ${formatNum(i1, 1)} A down to ${formatNum(i2, 1)} A (saving ${formatNum(deltaI, 1)} A, releasing ${formatNum(releasedKva, 1)} kVA of upstream transformer capacity)",
                "Step 5: Upstream cable and breaker thermal I²R dissipation is reduced by ${formatNum(lossReductionPct, 1)}%",
                "Step 6: Self-excitation check: Maximum safe terminal capacitance Qc_max = 90% of motor no-load kVAR = ${formatNum(maxSelfExcitationQc, 1)} kVAR (${if (isSafeSelfExcitation) "Passed safe limit" else "Warning: Over-excitation hazard"})"
            ),
            standardNote = "IEC 60831 / IEEE 141 (Red Book) Electric Power Distribution for Industrial Plants - Motor Power Factor Correction.",
            warning = warning
        )
    }

    // -------------------------------------------------------------
    fun calculateTransformer(
        kva: Double,
        vPrimary: Double,
        vSecondary: Double,
        percentZ: Double = 4.0,
        isThreePhase: Boolean = true
    ): CalculationResult {
        if (kva <= 0 || vPrimary <= 0 || vSecondary <= 0) {
            return CalculationResult("", "", formula = "I = (kVA × 1000) / (k × V)", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Rating and Voltages must be positive.")
        }
        val k = if (isThreePhase) sqrt(3.0) else 1.0
        val iPrimary = (kva * 1000.0) / (k * vPrimary)
        val iSecondary = (kva * 1000.0) / (k * vSecondary)
        val turnsRatio = vPrimary / vSecondary

        // Prospective short circuit current on secondary:
        val clampedZ = percentZ.coerceIn(1.0, 15.0)
        val iscSecondary = iSecondary / (clampedZ / 100.0)
        val faultMva = (kva / 1000.0) / (clampedZ / 100.0)

        return CalculationResult(
            primaryValue = formatNum(iSecondary, 1),
            primaryUnit = "A (Sec FLC)",
            secondaryValues = mapOf(
                "Primary Full Load Current" to "${formatNum(iPrimary, 1)} A",
                "Secondary Full Load Current" to "${formatNum(iSecondary, 1)} A",
                "Turns / Voltage Ratio (N1/N2)" to "${formatNum(turnsRatio, 2)} : 1",
                "Secondary Fault Current (Isc)" to "${formatNum(iscSecondary / 1000.0, 2)} kA (${formatNum(iscSecondary, 0)} A)",
                "Fault Power" to "${formatNum(faultMva, 2)} MVA",
                "Assumed Impedance (%Z)" to "${formatNum(clampedZ, 2)} %"
            ),
            formula = if (isThreePhase) "I = (kVA × 1000) / (√3 × V),  I_sc = I_sec / (%Z/100)" else "I = (kVA × 1000) / V,  I_sc = I_sec / (%Z/100)",
            substitution = "I_sec = (${formatNum(kva)} × 1000) / (${formatNum(k, 3)} × ${formatNum(vSecondary)})",
            steps = listOf(
                "Step 1: Primary FLC = (${formatNum(kva)} × 1000) / (${formatNum(k, 3)} × ${formatNum(vPrimary)}) = ${formatNum(iPrimary, 1)} A",
                "Step 2: Secondary FLC = (${formatNum(kva)} × 1000) / (${formatNum(k, 3)} × ${formatNum(vSecondary)}) = ${formatNum(iSecondary, 1)} A",
                "Step 3: Turns ratio = ${formatNum(vPrimary)} / ${formatNum(vSecondary)} = ${formatNum(turnsRatio, 2)}",
                "Step 4: Prospective Fault Current Isc = ${formatNum(iSecondary, 1)} / (${formatNum(clampedZ)} / 100) = ${formatNum(iscSecondary / 1000.0, 2)} kA"
            ),
            standardNote = "IEEE C57.12 / IEC 60076 Standard for Power & Distribution Transformers."
        )
    }

    // -------------------------------------------------------------
    // 8. BATTERY & STORAGE CALCULATIONS
    // -------------------------------------------------------------
    fun calculateBatteryRuntime(
        capacityAh: Double,
        batteryVoltage: Double,
        loadWatts: Double,
        dodPct: Double = 80.0,
        inverterEfficiencyPct: Double = 90.0
    ): CalculationResult {
        if (capacityAh <= 0 || batteryVoltage <= 0 || loadWatts <= 0) {
            return CalculationResult("", "", formula = "Hours = (Ah × V × DoD × η) / P_load", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Parameters must be positive.")
        }
        val totalEnergyWh = capacityAh * batteryVoltage
        val usableEnergyWh = totalEnergyWh * (dodPct / 100.0) * (inverterEfficiencyPct / 100.0)
        val runtimeHours = usableEnergyWh / loadWatts
        val hours = runtimeHours.toInt()
        val minutes = ((runtimeHours - hours) * 60).toInt()
        val dischargeCurrent = loadWatts / (batteryVoltage * (inverterEfficiencyPct / 100.0))

        return CalculationResult(
            primaryValue = if (hours > 0) "${hours}h ${minutes}m" else "${minutes} mins",
            primaryUnit = "Runtime",
            secondaryValues = mapOf(
                "Total Bank Energy" to "${formatNum(totalEnergyWh / 1000.0, 2)} kWh (${formatNum(totalEnergyWh, 0)} Wh)",
                "Usable Energy" to "${formatNum(usableEnergyWh / 1000.0, 2)} kWh",
                "Discharge Rate" to "${formatNum(dischargeCurrent, 1)} A (approx ${formatNum(capacityAh / dischargeCurrent, 1)}C)",
                "Depth of Discharge (DoD)" to "${formatNum(dodPct, 0)} %",
                "Inverter Efficiency" to "${formatNum(inverterEfficiencyPct, 0)} %"
            ),
            formula = "Runtime = (Ah × V_bat × DoD × η) / P_load",
            substitution = "Hours = (${formatNum(capacityAh)} × ${formatNum(batteryVoltage)} × ${formatNum(dodPct/100.0, 2)} × ${formatNum(inverterEfficiencyPct/100.0, 2)}) / ${formatNum(loadWatts)} W",
            steps = listOf(
                "Step 1: Calculate total bank gross capacity = ${formatNum(capacityAh)} Ah × ${formatNum(batteryVoltage)} V = ${formatNum(totalEnergyWh, 0)} Wh",
                "Step 2: Apply DoD (${formatNum(dodPct, 0)}%) and conversion efficiency (${formatNum(inverterEfficiencyPct, 0)}%) = ${formatNum(usableEnergyWh, 0)} Wh",
                "Step 3: Divide usable energy by load: ${formatNum(usableEnergyWh, 0)} / ${formatNum(loadWatts)} = ${formatNum(runtimeHours, 2)} hours"
            ),
            standardNote = "Battery discharge autonomy model taking into account depth-of-discharge and inverter losses."
        )
    }

    // -------------------------------------------------------------
    // 9. SOLAR / PV SYSTEM SIZING
    // -------------------------------------------------------------
    fun calculatePVSizing(
        dailyEnergyKWh: Double,
        peakSunHours: Double = 4.5,
        systemLossPct: Double = 20.0
    ): CalculationResult {
        if (dailyEnergyKWh <= 0 || peakSunHours <= 0) {
            return CalculationResult("", "", formula = "kWp = Daily_kWh / (PSH × (1 - Losses))", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Daily consumption and Sun hours must be positive.")
        }
        val derate = 1.0 - (systemLossPct.coerceIn(5.0, 40.0) / 100.0)
        val requiredKWp = dailyEnergyKWh / (peakSunHours * derate)
        val panels400W = ceil((requiredKWp * 1000.0) / 400.0).toInt()
        val panels550W = ceil((requiredKWp * 1000.0) / 550.0).toInt()
        val recommendedInverterKW = requiredKWp / 1.2 // 120% DC/AC oversizing ratio typical

        return CalculationResult(
            primaryValue = formatNum(requiredKWp, 2),
            primaryUnit = "kWp (PV Array)",
            secondaryValues = mapOf(
                "Panels (400W each)" to "$panels400W modules",
                "Panels (550W each)" to "$panels550W modules",
                "Suggested Inverter Size" to "${formatNum(recommendedInverterKW, 1)} kW AC",
                "Daily Generation Estimate" to "${formatNum(requiredKWp * peakSunHours * derate, 1)} kWh/day",
                "Estimated Roof Area Required" to "${formatNum(requiredKWp * 5.5, 0)} m² (${formatNum(requiredKWp * 59.2, 0)} sq ft)"
            ),
            formula = "kWp = Daily_kWh / (Peak_Sun_Hours × (1 - System_Losses))",
            substitution = "kWp = ${formatNum(dailyEnergyKWh)} / (${formatNum(peakSunHours)} × ${formatNum(derate, 2)})",
            steps = listOf(
                "Step 1: Daily target energy demand = ${formatNum(dailyEnergyKWh)} kWh",
                "Step 2: Effective daily solar yield factor = ${formatNum(peakSunHours)} PSH × ${formatNum(derate, 2)} = ${formatNum(peakSunHours * derate, 2)} kWh/kWp",
                "Step 3: Required DC array peak rating = ${formatNum(dailyEnergyKWh)} / ${formatNum(peakSunHours * derate, 2)} = ${formatNum(requiredKWp, 2)} kWp",
                "Step 4: Calculate module count and recommended inverter capacity"
            ),
            standardNote = "NREL / IEC 61724 Standard Photovoltaic System Performance Guidelines."
        )
    }

    // -------------------------------------------------------------
    // 10. LIGHTING & LUX
    // -------------------------------------------------------------
    fun calculateLighting(
        areaSqM: Double,
        targetLux: Double,
        fixtureLumens: Double,
        utilizationFactor: Double = 0.6,
        maintenanceFactor: Double = 0.8
    ): CalculationResult {
        if (areaSqM <= 0 || targetLux <= 0 || fixtureLumens <= 0) {
            return CalculationResult("", "", formula = "N = (E × A) / (F × CU × MF)", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Parameters must be positive.")
        }
        val totalLumensRequired = (targetLux * areaSqM) / (utilizationFactor * maintenanceFactor)
        val fixtureCount = ceil(totalLumensRequired / fixtureLumens).toInt()
        val actualLux = (fixtureCount * fixtureLumens * utilizationFactor * maintenanceFactor) / areaSqM

        return CalculationResult(
            primaryValue = "$fixtureCount",
            primaryUnit = "Fixtures",
            secondaryValues = mapOf(
                "Total Lumens Required" to "${formatNum(totalLumensRequired, 0)} lm",
                "Provided Illuminance" to "${formatNum(actualLux, 0)} Lux",
                "Target Illuminance" to "${formatNum(targetLux, 0)} Lux",
                "Room Area" to "${formatNum(areaSqM, 1)} m²"
            ),
            formula = "N = (E × A) / (Φ × CU × MF)",
            substitution = "N = (${formatNum(targetLux)} × ${formatNum(areaSqM)}) / (${formatNum(fixtureLumens)} × ${formatNum(utilizationFactor)} × ${formatNum(maintenanceFactor)})",
            steps = listOf(
                "Step 1: Calculate total luminous flux required = (${formatNum(targetLux)} Lux × ${formatNum(areaSqM)} m²) / (${formatNum(utilizationFactor)} × ${formatNum(maintenanceFactor)}) = ${formatNum(totalLumensRequired, 0)} lm",
                "Step 2: Divide by lamp lumen rating = ${formatNum(totalLumensRequired, 0)} / ${formatNum(fixtureLumens)} = ${formatNum(totalLumensRequired / fixtureLumens, 2)}",
                "Step 3: Round up to nearest whole fixture = $fixtureCount fixtures"
            ),
            standardNote = "CIBSE / IESNA Lumen Method / British Standard EN 12464-1 for interior lighting."
        )
    }

    // -------------------------------------------------------------
    // 11. LED SERIES RESISTOR
    // -------------------------------------------------------------
    fun calculateLedResistor(
        supplyVoltage: Double,
        ledForwardVoltage: Double,
        ledCurrentMa: Double
    ): CalculationResult {
        if (supplyVoltage <= ledForwardVoltage || ledCurrentMa <= 0) {
            return CalculationResult("", "", formula = "R = (Vs - Vf) / I_led", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Supply voltage must be strictly greater than LED forward voltage.")
        }
        val currentA = ledCurrentMa / 1000.0
        val vDiff = supplyVoltage - ledForwardVoltage
        val resistanceOhm = vDiff / currentA
        val powerWatts = vDiff * currentA
        val recommendedRating = when {
            powerWatts < 0.125 -> "0.25 W (1/4 Watt)"
            powerWatts < 0.25 -> "0.5 W (1/2 Watt)"
            powerWatts < 0.5 -> "1.0 Watt"
            powerWatts < 1.0 -> "2.0 Watt"
            else -> "${ceil(powerWatts * 2.0).toInt()} W Power Resistor"
        }

        return CalculationResult(
            primaryValue = formatNum(resistanceOhm, 1),
            primaryUnit = "Ω",
            secondaryValues = mapOf(
                "Actual Resistor Power Dissipation" to "${formatNum(powerWatts * 1000.0, 1)} mW (${formatNum(powerWatts, 3)} W)",
                "Recommended Resistor Wattage" to recommendedRating,
                "Voltage Drop Across Resistor" to "${formatNum(vDiff, 2)} V",
                "LED Current" to "${formatNum(ledCurrentMa, 1)} mA"
            ),
            formula = "R = (V_supply - V_forward) / I_led,  P_R = (V_supply - V_forward) × I_led",
            substitution = "R = (${formatNum(supplyVoltage)} V - ${formatNum(ledForwardVoltage)} V) / ${formatNum(currentA, 4)} A = ${formatNum(resistanceOhm, 1)} Ω",
            steps = listOf(
                "Step 1: Calculate voltage dropped across resistor = ${formatNum(supplyVoltage)} - ${formatNum(ledForwardVoltage)} = ${formatNum(vDiff, 2)} V",
                "Step 2: Apply Ohm's law: R = ${formatNum(vDiff, 2)} / ${formatNum(currentA, 4)} = ${formatNum(resistanceOhm, 1)} Ω",
                "Step 3: Power dissipation P = V_diff × I = ${formatNum(vDiff, 2)} × ${formatNum(currentA, 4)} = ${formatNum(powerWatts, 3)} W",
                "Step 4: Select standard resistor power rating with 2x thermal safety margin ($recommendedRating)"
            ),
            standardNote = "LED current limiting engineering recommendation with 2x thermal safety margin."
        )
    }

    // -------------------------------------------------------------
    // 12. PHASE BALANCING (3-PHASE LOAD IMBALANCE)
    // -------------------------------------------------------------
    fun calculatePhaseBalance(l1Watts: Double, l2Watts: Double, l3Watts: Double): CalculationResult {
        if (l1Watts < 0 || l2Watts < 0 || l3Watts < 0 || (l1Watts + l2Watts + l3Watts) == 0.0) {
            return CalculationResult("", "", formula = "Imbalance% = (MaxDev / Avg) × 100%", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Phase loads cannot be negative.")
        }
        val totalWatts = l1Watts + l2Watts + l3Watts
        val avgWatts = totalWatts / 3.0
        val dev1 = abs(l1Watts - avgWatts)
        val dev2 = abs(l2Watts - avgWatts)
        val dev3 = abs(l3Watts - avgWatts)
        val maxDev = max(dev1, max(dev2, dev3))
        val imbalancePct = (maxDev / avgWatts) * 100.0

        val warning = when {
            imbalancePct > 20.0 -> "HIGH IMBALANCE CRITICAL: Over 20% phase imbalance causes severe neutral overheating and transformer derating. Reallocate single-phase branch circuits."
            imbalancePct > 10.0 -> "Moderate imbalance (> 10%). Consider redistributing circuits across phases for improved efficiency."
            else -> null
        }

        return CalculationResult(
            primaryValue = formatNum(imbalancePct, 1),
            primaryUnit = "% Imbalance",
            secondaryValues = mapOf(
                "Total 3-Phase Load" to "${formatNum(totalWatts / 1000.0, 2)} kW (${formatNum(totalWatts, 0)} W)",
                "Average Load / Phase" to "${formatNum(avgWatts / 1000.0, 2)} kW",
                "Phase 1 (L1)" to "${formatNum(l1Watts / 1000.0, 2)} kW (${formatNum((l1Watts / totalWatts) * 100, 1)}%)",
                "Phase 2 (L2)" to "${formatNum(l2Watts / 1000.0, 2)} kW (${formatNum((l2Watts / totalWatts) * 100, 1)}%)",
                "Phase 3 (L3)" to "${formatNum(l3Watts / 1000.0, 2)} kW (${formatNum((l3Watts / totalWatts) * 100, 1)}%)",
                "Status" to if (imbalancePct <= 10.0) "Well Balanced" else if (imbalancePct <= 20.0) "Acceptable" else "Action Required"
            ),
            formula = "Imbalance % = [max(|L_n - L_avg|) / L_avg] × 100%",
            substitution = "Imbalance % = [${formatNum(maxDev, 1)} / ${formatNum(avgWatts, 1)}] × 100%",
            steps = listOf(
                "Step 1: Total connected power = ${formatNum(totalWatts, 0)} W",
                "Step 2: Average phase power = ${formatNum(totalWatts, 0)} / 3 = ${formatNum(avgWatts, 1)} W",
                "Step 3: Max deviation from average = ${formatNum(maxDev, 1)} W",
                "Step 4: NEMA / IEEE Voltage and Current Imbalance Ratio = ${formatNum(imbalancePct, 1)}%"
            ),
            standardNote = "NEMA MG-1 and IEEE Standard 141 Phase Unbalance evaluation.",
            warning = warning
        )
    }

    // -------------------------------------------------------------
    // 13. ENERGY CONSUMPTION & APPLIANCE COST
    // -------------------------------------------------------------
    fun calculateEnergyCost(
        watts: Double,
        hoursPerDay: Double,
        daysPerMonth: Double = 30.0,
        ratePerKWh: Double = 0.15
    ): CalculationResult {
        if (watts <= 0 || hoursPerDay <= 0 || ratePerKWh < 0) {
            return CalculationResult("", "", formula = "Cost = (Watts × Hours / 1000) × Tariff", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Parameters must be positive.")
        }
        val dailyKWh = (watts * hoursPerDay) / 1000.0
        val monthlyKWh = dailyKWh * daysPerMonth
        val annualKWh = dailyKWh * 365.0

        val dailyCost = dailyKWh * ratePerKWh
        val monthlyCost = monthlyKWh * ratePerKWh
        val annualCost = annualKWh * ratePerKWh

        return CalculationResult(
            primaryValue = "$${formatNum(monthlyCost, 2)}",
            primaryUnit = "Per Month",
            secondaryValues = mapOf(
                "Daily Cost" to "$${formatNum(dailyCost, 2)}",
                "Annual Cost" to "$${formatNum(annualCost, 2)}",
                "Daily Consumption" to "${formatNum(dailyKWh, 2)} kWh",
                "Monthly Consumption" to "${formatNum(monthlyKWh, 1)} kWh",
                "Annual Consumption" to "${formatNum(annualKWh, 0)} kWh"
            ),
            formula = "Energy (kWh) = (Watts × Hours) / 1000,  Cost = kWh × Tariff Rate",
            substitution = "Monthly kWh = (${formatNum(watts)} W × ${formatNum(hoursPerDay)} h × ${formatNum(daysPerMonth)} d) / 1000 = ${formatNum(monthlyKWh, 1)} kWh",
            steps = listOf(
                "Step 1: Daily kWh = (${formatNum(watts)} × ${formatNum(hoursPerDay)}) / 1000 = ${formatNum(dailyKWh, 2)} kWh",
                "Step 2: Monthly kWh = ${formatNum(dailyKWh, 2)} × ${formatNum(daysPerMonth)} = ${formatNum(monthlyKWh, 1)} kWh",
                "Step 3: Monthly cost = ${formatNum(monthlyKWh, 1)} kWh × $${formatNum(ratePerKWh, 3)}/kWh = $${formatNum(monthlyCost, 2)}"
            ),
            standardNote = "Utility electrical energy consumption metering calculation."
        )
    }

    // -------------------------------------------------------------
    // 14. COMPONENT DECODERS: RESISTOR COLOR CODE & SMD
    // -------------------------------------------------------------
    data class ResistorColorBand(val name: String, val colorHex: Long, val digit: Int?, val multiplier: Double, val tolerance: Double?)

    val colorBands = listOf(
        ResistorColorBand("Black", 0xFF1E1E1E, 0, 1.0, null),
        ResistorColorBand("Brown", 0xFF795548, 1, 10.0, 1.0),
        ResistorColorBand("Red", 0xFFE53935, 2, 100.0, 2.0),
        ResistorColorBand("Orange", 0xFFFF9800, 3, 1000.0, null),
        ResistorColorBand("Yellow", 0xFFFFEB3B, 4, 10000.0, null),
        ResistorColorBand("Green", 0xFF4CAF50, 5, 100000.0, 0.5),
        ResistorColorBand("Blue", 0xFF2196F3, 6, 1000000.0, 0.25),
        ResistorColorBand("Violet", 0xFF9C27B0, 7, 10000000.0, 0.1),
        ResistorColorBand("Grey", 0xFF9E9E9E, 8, 100000000.0, 0.05),
        ResistorColorBand("White", 0xFFFFFFFF, 9, 1000000000.0, null),
        ResistorColorBand("Gold", 0xFFFFD700, null, 0.1, 5.0),
        ResistorColorBand("Silver", 0xFFC0C0C0, null, 0.01, 10.0)
    )

    fun decodeResistor4Band(band1Idx: Int, band2Idx: Int, multIdx: Int, tolIdx: Int): CalculationResult {
        val b1 = colorBands.getOrNull(band1Idx)?.digit ?: 1
        val b2 = colorBands.getOrNull(band2Idx)?.digit ?: 0
        val mult = colorBands.getOrNull(multIdx)?.multiplier ?: 1.0
        val tol = colorBands.getOrNull(tolIdx)?.tolerance ?: 5.0

        val baseResistance = ((b1 * 10) + b2) * mult
        val tolValue = baseResistance * (tol / 100.0)
        val minR = baseResistance - tolValue
        val maxR = baseResistance + tolValue

        val formattedResistance = when {
            baseResistance >= 1000000.0 -> "${formatNum(baseResistance / 1000000.0, 2)} MΩ"
            baseResistance >= 1000.0 -> "${formatNum(baseResistance / 1000.0, 2)} kΩ"
            else -> "${formatNum(baseResistance, 1)} Ω"
        }

        return CalculationResult(
            primaryValue = formattedResistance,
            primaryUnit = "±$tol%",
            secondaryValues = mapOf(
                "Exact Ohms" to "${formatNum(baseResistance, 1)} Ω",
                "Tolerance Range" to "${formatNum(minR, 1)} Ω – ${formatNum(maxR, 1)} Ω",
                "Tolerance" to "±$tol%"
            ),
            formula = "R = [(Band1 × 10) + Band2] × Multiplier",
            substitution = "R = [($b1 × 10) + $b2] × $mult = $baseResistance Ω",
            steps = listOf(
                "Band 1 (${colorBands[band1Idx].name}) = $b1",
                "Band 2 (${colorBands[band2Idx].name}) = $b2",
                "Multiplier (${colorBands[multIdx].name}) = ×$mult",
                "Tolerance (${colorBands[tolIdx].name}) = ±$tol%"
            ),
            standardNote = "IEC 60062 Marking codes for resistors and capacitors."
        )
    }

    fun decodeCapacitorCode(code: String): CalculationResult {
        val clean = code.trim().filter { it.isDigit() }
        if (clean.length < 3) {
            return CalculationResult("", "", formula = "Code: Digits 1-2 × 10^Digit3 pF", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Capacitor code must be at least 3 digits (e.g. 104, 473).")
        }
        val sig = clean.substring(0, 2).toDoubleOrNull() ?: 10.0
        val exp = clean[2].digitToIntOrNull() ?: 0
        val pF = sig * 10.0.pow(exp)
        val nF = pF / 1000.0
        val uF = pF / 1000000.0

        val primary = when {
            uF >= 1.0 -> "${formatNum(uF, 2)} µF"
            nF >= 1.0 -> "${formatNum(nF, 1)} nF"
            else -> "${formatNum(pF, 0)} pF"
        }

        return CalculationResult(
            primaryValue = primary,
            primaryUnit = "Capacitance",
            secondaryValues = mapOf(
                "In PicoFarads (pF)" to "${formatNum(pF, 0)} pF",
                "In NanoFarads (nF)" to "${formatNum(nF, 2)} nF",
                "In MicroFarads (µF)" to "${formatNum(uF, 4)} µF",
                "Input Code" to clean
            ),
            formula = "C = (D1 D2) × 10^(D3) pF",
            substitution = "C = $sig × 10^$exp = ${formatNum(pF, 0)} pF",
            steps = listOf(
                "Step 1: First two digits represent significant figures = $sig",
                "Step 2: Third digit is exponent multiplier 10^$exp",
                "Step 3: Result in pF = ${formatNum(pF, 0)} pF = ${formatNum(nF, 2)} nF = ${formatNum(uF, 4)} µF"
            ),
            standardNote = "EIA-198 Capacitor 3-digit marking code standard."
        )
    }
}
