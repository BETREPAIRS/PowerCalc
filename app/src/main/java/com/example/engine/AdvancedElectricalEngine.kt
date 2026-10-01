package com.example.engine

import kotlin.math.*

object AdvancedElectricalEngine {

    private fun formatNum(value: Double, decimals: Int = 2): String {
        return if (value.isNaN() || value.isInfinite()) "0" else String.format("%.${decimals}f", value)
    }

    // -------------------------------------------------------------
    // 1. CABLE & WIRING ADVANCED CALCULATIONS
    // -------------------------------------------------------------

    fun calculateMaxLengthIsc(
        voltage: Double,
        cableSizeMm2: Double,
        breakerIa: Double,
        isCopper: Boolean = true
    ): CalculationResult {
        if (voltage <= 0 || cableSizeMm2 <= 0 || breakerIa <= 0) {
            return CalculationResult("", "", formula = "L_max = (0.8 × U₀ × S) / (2 × ρ × Ia)", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Parameters must be positive.")
        }
        val rho = if (isCopper) 0.0225 else 0.036 // Rho at operating temp 70°C/90°C (IEC 60364-4-41)
        val lMax = (0.8 * voltage * cableSizeMm2) / (2.0 * rho * breakerIa)

        return CalculationResult(
            primaryValue = "${formatNum(lMax, 1)} m",
            primaryUnit = "Max Length (Isc)",
            secondaryValues = mapOf(
                "Max Length (Feet)" to "${formatNum(lMax * 3.28084, 1)} ft",
                "Magnetic Trip Current (Ia)" to "${formatNum(breakerIa, 1)} A",
                "Conductor Area" to "${formatNum(cableSizeMm2, 1)} mm²",
                "Phase Voltage (U₀)" to "${formatNum(voltage, 0)} V",
                "Material" to if (isCopper) "Copper" else "Aluminium"
            ),
            formula = "L_max = (0.8 × U₀ × S) / (2 × ρ × Ia)",
            substitution = "L_max = (0.8 × ${formatNum(voltage, 0)} × ${formatNum(cableSizeMm2, 1)}) / (2 × $rho × ${formatNum(breakerIa, 1)})",
            steps = listOf(
                "Step 1: Determine tripping current Ia for protective device (e.g. Type B = 5×In, Type C = 10×In, Type D = 20×In)",
                "Step 2: Factor in 80% voltage drop during fault and thermal resistivity at fault temp",
                "Step 3: Calculate maximum cable length L_max = ${formatNum(lMax, 1)} meters to guarantee instantaneous trip"
            ),
            standardNote = "IEC 60364-4-41 / BS 7671 Rule for Automatic Disconnection of Supply (ADS)."
        )
    }

    fun calculateCablePowerLosses(
        current: Double,
        lengthM: Double,
        cableSizeMm2: Double,
        isThreePhase: Boolean = true,
        hoursPerDay: Double = 12.0,
        costPerKWh: Double = 0.15,
        isCopper: Boolean = true
    ): CalculationResult {
        if (current <= 0 || lengthM <= 0 || cableSizeMm2 <= 0) {
            return CalculationResult("", "", formula = "ΔP = k × I² × R", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Parameters must be positive.")
        }
        val rho = if (isCopper) 0.0175 else 0.028
        val rPerConductor = (rho * lengthM) / cableSizeMm2
        val k = if (isThreePhase) 3.0 else 2.0
        val lossWatts = k * current * current * rPerConductor
        val lossKw = lossWatts / 1000.0
        val annualHours = hoursPerDay.coerceIn(1.0, 24.0) * 365.0
        val annualKWh = lossKw * annualHours
        val annualCost = annualKWh * costPerKWh

        return CalculationResult(
            primaryValue = "${formatNum(lossWatts, 1)} W",
            primaryUnit = "Total Cable Heat Loss",
            secondaryValues = mapOf(
                "Power Loss (kW)" to "${formatNum(lossKw, 3)} kW",
                "Annual Energy Dissipated" to "${formatNum(annualKWh, 1)} kWh/yr",
                "Annual Financial Cost" to "$${formatNum(annualCost, 2)}/yr",
                "Resistance per Phase" to "${formatNum(rPerConductor, 4)} Ω",
                "System Type" to if (isThreePhase) "3-Phase (3 Cores)" else "1-Phase / DC (2 Cores)"
            ),
            formula = if (isThreePhase) "ΔP = 3 × I² × R" else "ΔP = 2 × I² × R",
            substitution = "ΔP = ${k.toInt()} × (${formatNum(current, 1)} A)² × ${formatNum(rPerConductor, 4)} Ω",
            steps = listOf(
                "Step 1: Calculate single conductor resistance R = (ρ × L) / S = ${formatNum(rPerConductor, 4)} Ω",
                "Step 2: Active Joule power dissipation = ${k.toInt()} × I² × R = ${formatNum(lossWatts, 1)} Watts",
                "Step 3: Annual energy waste = ${formatNum(lossKw, 3)} kW × ${annualHours.toInt()} h = ${formatNum(annualKWh, 1)} kWh",
                "Step 4: Energy cost of cable losses at $costPerKWh/kWh = $${formatNum(annualCost, 2)} per year"
            ),
            standardNote = "Joule cable dissipation economic assessment according to IEC 60287."
        )
    }

    fun calculateCableOperatingTemperature(
        ambientTempC: Double = 30.0,
        loadCurrentA: Double,
        ratedAmpacityIz: Double,
        maxPermissibleTempC: Double = 70.0
    ): CalculationResult {
        if (ratedAmpacityIz <= 0 || loadCurrentA < 0) {
            return CalculationResult("", "", formula = "Tc = Ta + (Tmax - Ta) × (I / Iz)²", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Ampacity must be positive.")
        }
        val ratio = (loadCurrentA / ratedAmpacityIz)
        val tempRise = (maxPermissibleTempC - ambientTempC) * (ratio * ratio)
        val coreTemp = ambientTempC + tempRise

        val status = when {
            coreTemp > maxPermissibleTempC -> "OVERHEATING! Exceeds insulation limit."
            coreTemp > maxPermissibleTempC * 0.9 -> "High Thermal Stress (Near maximum capacity)"
            else -> "Safe Normal Operating Range"
        }

        return CalculationResult(
            primaryValue = "${formatNum(coreTemp, 1)} °C",
            primaryUnit = "Operating Core Temp",
            secondaryValues = mapOf(
                "Thermal Status" to status,
                "Temperature Rise (ΔT)" to "+${formatNum(tempRise, 1)} °C",
                "Current Loading Ratio" to "${formatNum(ratio * 100.0, 1)} %",
                "Ambient Temperature" to "${formatNum(ambientTempC, 1)} °C",
                "Max Permissible Temp" to "${formatNum(maxPermissibleTempC, 0)} °C (${if (maxPermissibleTempC >= 90.0) "XLPE/EPR" else "PVC"})"
            ),
            formula = "T_core = T_amb + (T_max - T_amb) × (I_load / I_z)²",
            substitution = "T_core = ${formatNum(ambientTempC, 1)} + (${formatNum(maxPermissibleTempC, 0)} - ${formatNum(ambientTempC, 1)}) × (${formatNum(loadCurrentA, 1)} / ${formatNum(ratedAmpacityIz, 1)})²",
            steps = listOf(
                "Step 1: Calculate thermal load ratio (I / Iz) = ${formatNum(ratio, 3)}",
                "Step 2: Quadratic heating factor = (${formatNum(ratio, 3)})² = ${formatNum(ratio * ratio, 3)}",
                "Step 3: Conductor core operating temperature = ${formatNum(coreTemp, 1)} °C"
            ),
            standardNote = "Steady-state conductor thermal model per IEC 60287-1-1."
        )
    }

    fun calculateCableImpedance(
        cableSizeMm2: Double,
        lengthM: Double,
        isCopper: Boolean = true,
        reactancePerKm: Double = 0.08
    ): CalculationResult {
        if (cableSizeMm2 <= 0 || lengthM <= 0) {
            return CalculationResult("", "", formula = "Z = √(R² + X²)", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Parameters must be positive.")
        }
        val rho = if (isCopper) 0.018 else 0.029
        val rPerKm = (rho * 1000.0) / cableSizeMm2
        val rTotal = (rPerKm * lengthM) / 1000.0
        val xTotal = (reactancePerKm * lengthM) / 1000.0
        val zTotal = sqrt(rTotal * rTotal + xTotal * xTotal)
        val phaseAngleDeg = Math.toDegrees(atan2(xTotal, rTotal))

        return CalculationResult(
            primaryValue = "${formatNum(zTotal, 4)} Ω",
            primaryUnit = "Total Impedance (Z)",
            secondaryValues = mapOf(
                "Total Resistance (R)" to "${formatNum(rTotal, 4)} Ω",
                "Total Reactance (X)" to "${formatNum(xTotal, 4)} Ω",
                "R per km" to "${formatNum(rPerKm, 4)} Ω/km",
                "X per km" to "${formatNum(reactancePerKm, 4)} Ω/km",
                "Impedance Phase Angle" to "${formatNum(phaseAngleDeg, 1)}°",
                "Cable Length" to "${formatNum(lengthM, 1)} m"
            ),
            formula = "R = (ρ × L) / S,  X = x₀ × L,  Z = √(R² + X²)",
            substitution = "Z = √[(${formatNum(rTotal, 4)})² + (${formatNum(xTotal, 4)})²] = ${formatNum(zTotal, 4)} Ω",
            steps = listOf(
                "Step 1: Conductor DC/AC resistance R = (ρ × L) / S = ${formatNum(rTotal, 4)} Ω",
                "Step 2: Inductive line reactance X = x₀ × L = ${formatNum(xTotal, 4)} Ω",
                "Step 3: Total complex loop impedance Z = √(R² + X²) = ${formatNum(zTotal, 4)} Ω"
            ),
            standardNote = "IEC 60909-2 Cable electrical parameters for short-circuit and load flow."
        )
    }

    fun calculateDistributedVoltageDrop(
        systemVoltage: Double = 230.0,
        cableSizeMm2: Double = 4.0,
        loadsSummary: String = "10A@20m, 8A@40m, 6A@60m",
        isThreePhase: Boolean = false,
        isCopper: Boolean = true
    ): CalculationResult {
        if (systemVoltage <= 0 || cableSizeMm2 <= 0) {
            return CalculationResult("", "", formula = "ΔV = (k × ρ / S) × Σ(Iᵢ × Lᵢ)", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Parameters must be positive.")
        }
        val rho = if (isCopper) 0.0175 else 0.028
        val k = if (isThreePhase) sqrt(3.0) else 2.0

        // Parse loads: pairs of (Amps, MetersFromOrigin)
        var sumMoments = 0.0
        var totalCurrent = 0.0
        val entries = loadsSummary.split(",").map { it.trim() }
        for (entry in entries) {
            val parts = entry.split("@")
            if (parts.size == 2) {
                val amps = parts[0].filter { it.isDigit() || it == '.' }.toDoubleOrNull() ?: 0.0
                val dist = parts[1].filter { it.isDigit() || it == '.' }.toDoubleOrNull() ?: 0.0
                sumMoments += amps * dist
                totalCurrent += amps
            }
        }
        if (sumMoments <= 0.0) sumMoments = 10.0 * 20.0 + 8.0 * 40.0 + 6.0 * 60.0

        val deltaV = (k * rho / cableSizeMm2) * sumMoments
        val dropPct = (deltaV / systemVoltage) * 100.0
        val endVoltage = systemVoltage - deltaV

        return CalculationResult(
            primaryValue = "${formatNum(deltaV, 2)} V",
            primaryUnit = "Total Drop at End",
            secondaryValues = mapOf(
                "Drop Percentage" to "${formatNum(dropPct, 2)} %",
                "Voltage at Far End" to "${formatNum(endVoltage, 1)} V",
                "Total Connected Current" to "${formatNum(totalCurrent, 1)} A",
                "Electrical Moment (Σ I·L)" to "${formatNum(sumMoments, 0)} A·m",
                "Conductor Area" to "${formatNum(cableSizeMm2, 1)} mm²",
                "Supply Voltage" to "${formatNum(systemVoltage, 0)} V"
            ),
            formula = "ΔV = (k × ρ / S) × Σ(I_i × L_i)",
            substitution = "ΔV = (${formatNum(k, 3)} × $rho / ${formatNum(cableSizeMm2, 1)}) × ${formatNum(sumMoments, 0)}",
            steps = listOf(
                "Step 1: Calculate total electrical moment sum = Σ(I_i × L_i) = ${formatNum(sumMoments, 0)} A·m",
                "Step 2: Apply conductor cross-section resistivity factor (k × ρ / S)",
                "Step 3: Total cumulative voltage drop at farthest load = ${formatNum(deltaV, 2)} V (${formatNum(dropPct, 2)}%)"
            ),
            standardNote = "Moment calculation method for distributed branched lines (streetlights, conveyor outlets)."
        )
    }

    fun calculateBusbarAmpacity(
        widthMm: Double = 50.0,
        thicknessMm: Double = 5.0,
        isCopper: Boolean = true,
        isPainted: Boolean = false,
        ambientTempC: Double = 35.0
    ): CalculationResult {
        if (widthMm <= 0 || thicknessMm <= 0) {
            return CalculationResult("", "", formula = "DIN 43671 Busbar Standard", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Dimensions must be positive.")
        }
        val crossSectionMm2 = widthMm * thicknessMm
        val perimeterMm = 2.0 * (widthMm + thicknessMm)

        // Baseline continuous current according to DIN 43671 / IEC 60439
        // Approximation: I = k_mat * (crossSection)^0.5 * (perimeter)^0.39 * k_painted * k_temp
        val baseFactor = if (isCopper) 14.5 else 9.2
        val paintedFactor = if (isPainted) 1.15 else 1.0 // Painted black improves radiation cooling by 15%
        val tempFactor = sqrt((65.0 - ambientTempC.coerceIn(10.0, 55.0)) / 30.0).coerceIn(0.6, 1.3)

        val continuousAmpacity = baseFactor * crossSectionMm2.pow(0.55) * paintedFactor * tempFactor
        val maxKw3Phase = (sqrt(3.0) * 400.0 * continuousAmpacity * 0.9) / 1000.0

        return CalculationResult(
            primaryValue = "${formatNum(continuousAmpacity, 0)} A",
            primaryUnit = "Continuous Ampacity",
            secondaryValues = mapOf(
                "Cross Section" to "${formatNum(crossSectionMm2, 0)} mm²",
                "Perimeter" to "${formatNum(perimeterMm, 0)} mm",
                "Material" to if (isCopper) "Electrolytic Copper (Cu-ETP)" else "Aluminium (Al)",
                "Surface Finish" to if (isPainted) "Painted / Matte (Emissivity ~0.9)" else "Bare Metallic (Emissivity ~0.3)",
                "3-Phase Power @ 400V" to "${formatNum(maxKw3Phase, 1)} kW",
                "Ambient Temperature" to "${formatNum(ambientTempC, 0)} °C"
            ),
            formula = "I = k_mat × S^0.55 × k_finish × k_temp",
            substitution = "I = $baseFactor × (${formatNum(crossSectionMm2, 0)})^0.55 × $paintedFactor × ${formatNum(tempFactor, 2)}",
            steps = listOf(
                "Step 1: Calculate rectangular busbar area S = $widthMm × $thicknessMm = ${formatNum(crossSectionMm2, 0)} mm²",
                "Step 2: Surface dissipation perimeter = 2 × ($widthMm + $thicknessMm) = ${formatNum(perimeterMm, 0)} mm",
                "Step 3: Radiation emissivity factor (${if (isPainted) "1.15 for painted/anodized" else "1.0 bare"})",
                "Step 4: Continuous permissible current = ${formatNum(continuousAmpacity, 0)} A"
            ),
            standardNote = "DIN 43671 / IEC 60439 for busbars in switchgear and controlgear."
        )
    }

    fun calculateConduitTrayFill(
        conduitTradeSizeInches: Double = 1.0,
        conductorCount: Int = 4,
        conductorDiameterMm: Double = 6.5
    ): CalculationResult {
        if (conduitTradeSizeInches <= 0 || conductorCount <= 0 || conductorDiameterMm <= 0) {
            return CalculationResult("", "", formula = "Fill% = (Σ A_wire / A_conduit) × 100", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Parameters must be positive.")
        }
        // Conduit internal diameter in mm (approx standard EMT)
        val conduitIdMm = when (conduitTradeSizeInches) {
            0.5 -> 15.8
            0.75 -> 20.9
            1.0 -> 26.6
            1.25 -> 35.1
            1.5 -> 40.9
            2.0 -> 52.5
            2.5 -> 62.7
            3.0 -> 77.9
            4.0 -> 102.3
            else -> conduitTradeSizeInches * 25.4 * 0.95
        }
        val conduitAreaMm2 = Math.PI * (conduitIdMm / 2.0).pow(2.0)
        val singleWireAreaMm2 = Math.PI * (conductorDiameterMm / 2.0).pow(2.0)
        val totalWireAreaMm2 = singleWireAreaMm2 * conductorCount
        val fillPct = (totalWireAreaMm2 / conduitAreaMm2) * 100.0

        val maxAllowedFillPct = when (conductorCount) {
            1 -> 53.0
            2 -> 31.0
            else -> 40.0 // NEC Chapter 9 Table 1 40% fill rule for 3 or more conductors
        }

        val compliant = fillPct <= maxAllowedFillPct

        return CalculationResult(
            primaryValue = "${formatNum(fillPct, 1)} %",
            primaryUnit = "Conduit Fill",
            secondaryValues = mapOf(
                "NEC Compliance" to if (compliant) "PASSED (≤ ${maxAllowedFillPct.toInt()}%)" else "FAILED (> ${maxAllowedFillPct.toInt()}% - Jam Risk)",
                "Conduit Internal Area" to "${formatNum(conduitAreaMm2, 1)} mm²",
                "Total Cables Area" to "${formatNum(totalWireAreaMm2, 1)} mm²",
                "Max Permissible Area" to "${formatNum(conduitAreaMm2 * (maxAllowedFillPct / 100.0), 1)} mm²",
                "Trade Size EMT" to "$conduitTradeSizeInches\" (ID: ${formatNum(conduitIdMm, 1)} mm)",
                "Number of Conductors" to "$conductorCount wires"
            ),
            formula = "Fill% = (N × π × (d/2)²) / (π × (D/2)²) × 100",
            substitution = "Fill% = ($conductorCount × ${formatNum(singleWireAreaMm2, 1)} mm²) / ${formatNum(conduitAreaMm2, 1)} mm²",
            steps = listOf(
                "Step 1: Calculate total cross-sectional area of $conductorCount conductors = ${formatNum(totalWireAreaMm2, 1)} mm²",
                "Step 2: Internal cross-sectional area of $conduitTradeSizeInches\" conduit = ${formatNum(conduitAreaMm2, 1)} mm²",
                "Step 3: Fill ratio = ${formatNum(fillPct, 1)}% (Maximum allowable per NEC Chapter 9 Table 1 is ${maxAllowedFillPct.toInt()}%)"
            ),
            standardNote = "NEC Chapter 9 Table 1 / NFPA 70 Conduit and Tubing Fill Restrictions."
        )
    }

    // -------------------------------------------------------------
    // 2. CIRCUIT PROTECTION & SHORT-CIRCUITS
    // -------------------------------------------------------------

    fun calculateCableShortCircuitAdiabatic(
        faultCurrentKa: Double,
        faultDurationSec: Double = 0.1,
        conductorSizeMm2: Double = 16.0,
        isCopper: Boolean = true,
        isXlpe: Boolean = true
    ): CalculationResult {
        if (faultCurrentKa <= 0 || faultDurationSec <= 0 || conductorSizeMm2 <= 0) {
            return CalculationResult("", "", formula = "t ≤ (k² × S²) / I²", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Parameters must be positive.")
        }
        // Constant k according to IEC 60364-5-54 Table 54.2
        val k = when {
            isCopper && isXlpe -> 143.0 // Copper with XLPE (90°C to 250°C)
            isCopper && !isXlpe -> 115.0 // Copper with PVC (70°C to 160°C)
            !isCopper && isXlpe -> 94.0 // Aluminium with XLPE
            else -> 76.0 // Aluminium with PVC
        }

        val faultCurrentA = faultCurrentKa * 1000.0
        val maxEnergyKs2 = (k * conductorSizeMm2).pow(2.0)
        val actualEnergyI2t = faultCurrentA * faultCurrentA * faultDurationSec
        val minRequiredSizeMm2 = (faultCurrentA * sqrt(faultDurationSec)) / k
        val maxAllowedTimeSec = maxEnergyKs2 / (faultCurrentA * faultCurrentA)

        val isSafe = actualEnergyI2t <= maxEnergyKs2

        return CalculationResult(
            primaryValue = if (isSafe) "PROTECTED" else "THERMAL RISK",
            primaryUnit = "Cable Safety",
            secondaryValues = mapOf(
                "Admissible Energy (k²S²)" to "${formatNum(maxEnergyKs2 / 1_000_000.0, 2)} × 10⁶ A²s",
                "Let-Through Energy (I²t)" to "${formatNum(actualEnergyI2t / 1_000_000.0, 2)} × 10⁶ A²s",
                "Min Conductor Area Needed" to "${formatNum(minRequiredSizeMm2, 2)} mm²",
                "Max Permissible Clearance Time" to "${formatNum(maxAllowedTimeSec * 1000.0, 1)} ms",
                "Conductor Material Factor (k)" to "$k (${if (isCopper) "Copper" else "Aluminium"} / ${if (isXlpe) "XLPE 90°C" else "PVC 70°C"})"
            ),
            formula = "I²t ≤ k² × S²  |  S_min = (I × √t) / k",
            substitution = "(${formatNum(faultCurrentA, 0)} A)² × ${formatNum(faultDurationSec, 3)} s vs (${k.toInt()} × ${formatNum(conductorSizeMm2, 1)})²",
            steps = listOf(
                "Step 1: Determine thermal constant k = $k per IEC 60364-5-54",
                "Step 2: Calculate maximum specific energy let-through k²S² = ${formatNum(maxEnergyKs2 / 1_000_000.0, 2)} MA²s",
                "Step 3: Calculate actual fault let-through energy I²t = ${formatNum(actualEnergyI2t / 1_000_000.0, 2)} MA²s",
                "Step 4: Clearance check: ${if (isSafe) "Passed! Protective device clears within safe thermal limits." else "Failed! Conductor will overheat during short-circuit."}"
            ),
            standardNote = "Adiabatic short-circuit withstand equation IEC 60364-4-43."
        )
    }

    fun calculateBreakerCableCoordination(
        designCurrentIb: Double,
        breakerRatingIn: Double,
        cableAmpacityIz: Double
    ): CalculationResult {
        if (designCurrentIb <= 0 || breakerRatingIn <= 0 || cableAmpacityIz <= 0) {
            return CalculationResult("", "", formula = "Ib ≤ In ≤ Iz  &  I2 ≤ 1.45 × Iz", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Parameters must be positive.")
        }
        val cond1 = designCurrentIb <= breakerRatingIn // Overload prevention
        val cond2 = breakerRatingIn <= cableAmpacityIz // Cable protection
        val i2 = breakerRatingIn * 1.45 // Conventional tripping current for standard MCBs
        val cond3 = i2 <= 1.45 * cableAmpacityIz

        val fullyCoordinated = cond1 && cond2 && cond3

        return CalculationResult(
            primaryValue = if (fullyCoordinated) "COORDINATED" else "MISMATCH",
            primaryUnit = "Protection Rule",
            secondaryValues = mapOf(
                "Rule 1 (Ib ≤ In)" to if (cond1) "PASS: ${formatNum(designCurrentIb, 1)} A ≤ ${formatNum(breakerRatingIn, 1)} A" else "FAIL (Breaker will nuisance trip)",
                "Rule 2 (In ≤ Iz)" to if (cond2) "PASS: ${formatNum(breakerRatingIn, 1)} A ≤ ${formatNum(cableAmpacityIz, 1)} A" else "FAIL (Cable underprotected!)",
                "Rule 3 (I2 ≤ 1.45·Iz)" to if (cond3) "PASS: ${formatNum(i2, 1)} A ≤ ${formatNum(1.45 * cableAmpacityIz, 1)} A" else "FAIL",
                "Tripping Current (I2)" to "${formatNum(i2, 1)} A (1.45 × In)",
                "Cable Safe Limit (1.45·Iz)" to "${formatNum(1.45 * cableAmpacityIz, 1)} A"
            ),
            formula = "I_b ≤ I_n ≤ I_z   and   I_2 ≤ 1.45 × I_z",
            substitution = "${formatNum(designCurrentIb, 1)} A ≤ ${formatNum(breakerRatingIn, 1)} A ≤ ${formatNum(cableAmpacityIz, 1)} A",
            steps = listOf(
                "Step 1: Check continuous load current: Design current Ib (${formatNum(designCurrentIb, 1)}A) must not exceed breaker nominal rating In (${formatNum(breakerRatingIn, 1)}A)",
                "Step 2: Check conductor protection: Breaker In (${formatNum(breakerRatingIn, 1)}A) must not exceed cable derated ampacity Iz (${formatNum(cableAmpacityIz, 1)}A)",
                "Step 3: Check conventional overload tripping threshold I2 (1.45 × In = ${formatNum(i2, 1)}A) ≤ 1.45 × Iz (${formatNum(1.45 * cableAmpacityIz, 1)}A)"
            ),
            standardNote = "IEC 60364-4-43 Clause 433.1 Fundamental rules for overload protection coordination."
        )
    }

    fun calculateEarthingRCD(
        earthResistanceRa: Double = 25.0,
        rcdRatingMa: Double = 30.0,
        maxTouchVoltageV: Double = 50.0
    ): CalculationResult {
        if (earthResistanceRa <= 0 || rcdRatingMa <= 0) {
            return CalculationResult("", "", formula = "Ra ≤ UL / IΔn", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Parameters must be positive.")
        }
        val iDeltaN = rcdRatingMa / 1000.0
        val touchVoltage = earthResistanceRa * iDeltaN
        val maxAllowedRa = maxTouchVoltageV / iDeltaN
        val isSafe = earthResistanceRa <= maxAllowedRa

        return CalculationResult(
            primaryValue = "${formatNum(touchVoltage, 2)} V",
            primaryUnit = "Prospective Touch Voltage",
            secondaryValues = mapOf(
                "Compliance Status" to if (isSafe) "SAFE (≤ ${maxTouchVoltageV.toInt()}V)" else "DANGEROUS (> ${maxTouchVoltageV.toInt()}V)",
                "Max Allowable Earth Resistance" to "≤ ${formatNum(maxAllowedRa, 0)} Ω",
                "Actual Earth Resistance (Ra)" to "${formatNum(earthResistanceRa, 1)} Ω",
                "RCD Sensitivity (IΔn)" to "${rcdRatingMa.toInt()} mA",
                "Limit of Touch Voltage (UL)" to "${maxTouchVoltageV.toInt()} V (${if (maxTouchVoltageV == 25.0) "Wet/Agricultural" else "Normal Dry"})"
            ),
            formula = "U_touch = R_a × I_Δn   [Condition: R_a ≤ U_L / I_Δn]",
            substitution = "U_touch = ${formatNum(earthResistanceRa, 1)} Ω × ${formatNum(iDeltaN, 3)} A = ${formatNum(touchVoltage, 2)} V",
            steps = listOf(
                "Step 1: RCD residual tripping current IΔn = ${rcdRatingMa.toInt()} mA = ${formatNum(iDeltaN, 3)} A",
                "Step 2: Maximum touch voltage generated before trip = Ra × IΔn = ${formatNum(touchVoltage, 2)} V",
                "Step 3: Verification: ${if (isSafe) "Compliant with safety rules for indirect contact protection." else "Exceeds permissible touch voltage limit. Reduce grounding resistance."}"
            ),
            standardNote = "IEC 60364-4-41 Protection against electric shock in TT earthing systems."
        )
    }

    // -------------------------------------------------------------
    // 3. POWER & CAPACITORS
    // -------------------------------------------------------------

    fun calculateApparentPower(voltage: Double, current: Double, isThreePhase: Boolean = true): CalculationResult {
        if (voltage <= 0 || current <= 0) return CalculationResult("", "", formula = "S = k × V × I", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Parameters must be positive.")
        val k = if (isThreePhase) sqrt(3.0) else 1.0
        val sVa = k * voltage * current
        val sKva = sVa / 1000.0

        return CalculationResult(
            primaryValue = "${formatNum(sKva, 2)} kVA",
            primaryUnit = "Apparent Power (S)",
            secondaryValues = mapOf(
                "Power in Volt-Amperes" to "${formatNum(sVa, 0)} VA",
                "Line Current" to "${formatNum(current, 1)} A",
                "Voltage" to "${formatNum(voltage, 0)} V",
                "System Type" to if (isThreePhase) "3-Phase AC" else "1-Phase AC"
            ),
            formula = if (isThreePhase) "S = √3 × V_line × I_line" else "S = V × I",
            substitution = "S = ${formatNum(k, 3)} × ${formatNum(voltage, 0)} V × ${formatNum(current, 1)} A = ${formatNum(sVa, 0)} VA",
            steps = listOf(
                "Step 1: Calculate apparent power S = ${formatNum(k, 3)} × $voltage × $current = ${formatNum(sVa, 0)} VA",
                "Step 2: Convert to kilovolt-amperes = ${formatNum(sKva, 2)} kVA"
            ),
            standardNote = "AC Electrical Apparent Power Fundamental Formulation."
        )
    }

    fun calculateReactivePower(voltage: Double, current: Double, powerFactor: Double = 0.85, isThreePhase: Boolean = true): CalculationResult {
        if (voltage <= 0 || current <= 0) return CalculationResult("", "", formula = "Q = k × V × I × sinφ", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Parameters must be positive.")
        val pf = powerFactor.coerceIn(0.01, 1.0)
        val sinPhi = sin(acos(pf))
        val k = if (isThreePhase) sqrt(3.0) else 1.0
        val qVar = k * voltage * current * sinPhi
        val qKvar = qVar / 1000.0
        val pKw = (k * voltage * current * pf) / 1000.0

        return CalculationResult(
            primaryValue = "${formatNum(qKvar, 2)} kVAR",
            primaryUnit = "Reactive Power (Q)",
            secondaryValues = mapOf(
                "Active Power (P)" to "${formatNum(pKw, 2)} kW",
                "Apparent Power (S)" to "${formatNum((k * voltage * current) / 1000.0, 2)} kVA",
                "sin φ" to formatNum(sinPhi, 3),
                "Power Factor (cos φ)" to formatNum(pf, 2)
            ),
            formula = if (isThreePhase) "Q = √3 × V × I × sinφ" else "Q = V × I × sinφ",
            substitution = "Q = ${formatNum(k, 3)} × ${formatNum(voltage, 0)} × ${formatNum(current, 1)} × ${formatNum(sinPhi, 3)}",
            steps = listOf(
                "Step 1: Calculate reactive factor sinφ = √(1 - cos²φ) = ${formatNum(sinPhi, 3)}",
                "Step 2: Calculate reactive power Q = ${formatNum(qKvar, 2)} kVAR"
            ),
            standardNote = "AC Reactive Power Calculation."
        )
    }

    fun calculateNeutralCurrent(iL1: Double, iL2: Double, iL3: Double, thirdHarmonicPct: Double = 0.0): CalculationResult {
        val i1 = iL1.coerceAtLeast(0.0)
        val i2 = iL2.coerceAtLeast(0.0)
        val i3 = iL3.coerceAtLeast(0.0)

        // Fundamental neutral current from 120° phase displacement:
        // I_N = sqrt(I1^2 + I2^2 + I3^2 - I1*I2 - I2*I3 - I3*I1)
        val fundSquared = i1*i1 + i2*i2 + i3*i3 - (i1*i2 + i2*i3 + i3*i1)
        val fundIn = sqrt(fundSquared.coerceAtLeast(0.0))

        // Triplen 3rd harmonic adds directly in phase on the neutral:
        val h3Current = (i1 + i2 + i3) * (thirdHarmonicPct.coerceIn(0.0, 100.0) / 100.0)
        val totalIn = sqrt(fundIn * fundIn + h3Current * h3Current)

        return CalculationResult(
            primaryValue = "${formatNum(totalIn, 1)} A",
            primaryUnit = "Neutral Conductor Current",
            secondaryValues = mapOf(
                "Fundamental 50/60Hz Neutral" to "${formatNum(fundIn, 1)} A",
                "Triplen 3rd Harmonics Component" to "${formatNum(h3Current, 1)} A (${thirdHarmonicPct.toInt()}%)",
                "Phase 1 Load" to "${formatNum(i1, 1)} A",
                "Phase 2 Load" to "${formatNum(i2, 1)} A",
                "Phase 3 Load" to "${formatNum(i3, 1)} A"
            ),
            formula = "I_N = √[I₁² + I₂² + I₃² - (I₁I₂ + I₂I₃ + I₃I₁)] + I_h3",
            substitution = "I_N = √[${formatNum(i1, 0)}² + ${formatNum(i2, 0)}² + ${formatNum(i3, 0)}² - (...)] = ${formatNum(totalIn, 1)} A",
            steps = listOf(
                "Step 1: Calculate fundamental 50/60Hz imbalance current = ${formatNum(fundIn, 1)} A",
                "Step 2: Add triplen harmonics returning via neutral in non-linear loads",
                "Step 3: Total RMS neutral current = ${formatNum(totalIn, 1)} A"
            ),
            standardNote = "IEC 60364-5-52 Neutral sizing and non-linear harmonic assessment."
        )
    }

    fun calculateCapacitorAtDifferentVoltage(
        nominalQkvar: Double = 25.0,
        nominalVoltage: Double = 400.0,
        actualVoltage: Double = 380.0,
        nominalFreq: Double = 50.0,
        actualFreq: Double = 50.0
    ): CalculationResult {
        if (nominalQkvar <= 0 || nominalVoltage <= 0 || actualVoltage <= 0) {
            return CalculationResult("", "", formula = "Q₂ = Q₁ × (V₂/V₁)² × (f₂/f₁)", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Parameters must be positive.")
        }
        val voltRatio = actualVoltage / nominalVoltage
        val freqRatio = actualFreq / nominalFreq
        val actualQkvar = nominalQkvar * (voltRatio * voltRatio) * freqRatio
        val pctOutput = (actualQkvar / nominalQkvar) * 100.0

        return CalculationResult(
            primaryValue = "${formatNum(actualQkvar, 2)} kVAR",
            primaryUnit = "Actual Reactive Output",
            secondaryValues = mapOf(
                "Percentage of Nameplate" to "${formatNum(pctOutput, 1)} %",
                "Nameplate Rating" to "${formatNum(nominalQkvar, 1)} kVAR @ ${formatNum(nominalVoltage, 0)} V",
                "Operating Voltage" to "${formatNum(actualVoltage, 0)} V",
                "Capacity Reduction" to "${formatNum(100.0 - pctOutput, 1)} % drop"
            ),
            formula = "Q_actual = Q_rated × (V_actual / V_rated)² × (f_actual / f_rated)",
            substitution = "Q = ${formatNum(nominalQkvar, 1)} × (${formatNum(actualVoltage, 0)} / ${formatNum(nominalVoltage, 0)})² = ${formatNum(actualQkvar, 2)} kVAR",
            steps = listOf(
                "Step 1: Capacitor power is strictly proportional to the square of voltage (Q ∝ V²)",
                "Step 2: Voltage ratio = ${formatNum(actualVoltage, 0)} / ${formatNum(nominalVoltage, 0)} = ${formatNum(voltRatio, 3)}",
                "Step 3: Effective kVAR delivered = ${formatNum(nominalQkvar, 1)} × (${formatNum(voltRatio, 3)})² = ${formatNum(actualQkvar, 2)} kVAR"
            ),
            standardNote = "IEC 60831 Shunt power capacitors for AC systems."
        )
    }

    // -------------------------------------------------------------
    // 4. COMPONENTS, DIVIDERS & NETWORKS
    // -------------------------------------------------------------

    fun calculateVoltageDivider(
        vin: Double,
        r1: Double,
        r2: Double,
        loadResistance: Double? = null
    ): CalculationResult {
        if (r1 <= 0 || r2 <= 0) return CalculationResult("", "", formula = "Vout = Vin × R2 / (R1 + R2)", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Resistances must be positive.")
        val effectiveR2 = if (loadResistance != null && loadResistance > 0) {
            (r2 * loadResistance) / (r2 + loadResistance)
        } else r2

        val vout = vin * (effectiveR2 / (r1 + effectiveR2))
        val totalCurrentMa = (vin / (r1 + effectiveR2)) * 1000.0
        val pR1 = ((vin - vout) * (vin - vout)) / r1
        val pR2 = (vout * vout) / r2

        return CalculationResult(
            primaryValue = "${formatNum(vout, 2)} V",
            primaryUnit = "Output Voltage (Vout)",
            secondaryValues = mapOf(
                "Quiescent Current" to "${formatNum(totalCurrentMa, 2)} mA",
                "Power on R1" to "${formatNum(pR1 * 1000.0, 1)} mW (${formatNum(pR1, 3)} W)",
                "Power on R2" to "${formatNum(pR2 * 1000.0, 1)} mW",
                "Voltage Ratio (Vout/Vin)" to formatNum(vout / vin, 4),
                "Loading Condition" to if (loadResistance != null) "Loaded (${formatNum(loadResistance, 0)} Ω)" else "Unloaded"
            ),
            formula = "V_out = V_in × [R₂ / (R₁ + R₂)]",
            substitution = "V_out = ${formatNum(vin, 1)} V × [${formatNum(effectiveR2, 1)} / (${formatNum(r1, 1)} + ${formatNum(effectiveR2, 1)})] = ${formatNum(vout, 2)} V",
            steps = listOf(
                "Step 1: Calculate equivalent lower branch resistance = ${formatNum(effectiveR2, 1)} Ω",
                "Step 2: Voltage divider ratio = ${formatNum(effectiveR2 / (r1 + effectiveR2), 4)}",
                "Step 3: Output voltage Vout = ${formatNum(vout, 2)} V"
            ),
            standardNote = "Ohmic potential divider network."
        )
    }

    fun calculateCurrentDivider(totalCurrentA: Double, r1: Double, r2: Double): CalculationResult {
        if (r1 <= 0 || r2 <= 0) return CalculationResult("", "", formula = "I1 = Itotal × R2 / (R1 + R2)", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Resistances must be positive.")
        val i1 = totalCurrentA * (r2 / (r1 + r2))
        val i2 = totalCurrentA * (r1 / (r1 + r2))

        return CalculationResult(
            primaryValue = "${formatNum(i1, 2)} A",
            primaryUnit = "Current through R1 (I1)",
            secondaryValues = mapOf(
                "Current through R2 (I2)" to "${formatNum(i2, 2)} A",
                "Equivalent Parallel R" to "${formatNum((r1 * r2) / (r1 + r2), 2)} Ω",
                "Total Current" to "${formatNum(totalCurrentA, 2)} A"
            ),
            formula = "I₁ = I_total × [R₂ / (R₁ + R₂)],  I₂ = I_total × [R₁ / (R₁ + R₂)]",
            substitution = "I₁ = ${formatNum(totalCurrentA, 2)} A × [${formatNum(r2, 1)} / (${formatNum(r1, 1)} + ${formatNum(r2, 1)})] = ${formatNum(i1, 2)} A",
            steps = listOf(
                "Step 1: Current divides inversely proportional to branch resistance",
                "Step 2: Branch 1 gets: ${formatNum(i1, 2)} A",
                "Step 3: Branch 2 gets: ${formatNum(i2, 2)} A"
            ),
            standardNote = "Kirchhoff Current Law parallel branch distribution."
        )
    }

    fun calculateResonantFrequency(inductanceMh: Double, capacitanceUf: Double): CalculationResult {
        if (inductanceMh <= 0 || capacitanceUf <= 0) return CalculationResult("", "", formula = "f₀ = 1 / (2π√(LC))", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Parameters must be positive.")
        val lHenries = inductanceMh / 1000.0
        val cFarads = capacitanceUf / 1_000_000.0
        val f0Hz = 1.0 / (2.0 * Math.PI * sqrt(lHenries * cFarads))
        val omega0 = 2.0 * Math.PI * f0Hz
        val charZ = sqrt(lHenries / cFarads) // Characteristic impedance Z0 = √(L/C)

        return CalculationResult(
            primaryValue = if (f0Hz >= 1000.0) "${formatNum(f0Hz / 1000.0, 2)} kHz" else "${formatNum(f0Hz, 1)} Hz",
            primaryUnit = "Resonant Frequency (f₀)",
            secondaryValues = mapOf(
                "Exact Frequency" to "${formatNum(f0Hz, 2)} Hz",
                "Angular Frequency (ω₀)" to "${formatNum(omega0, 1)} rad/s",
                "Characteristic Impedance (Z₀)" to "${formatNum(charZ, 1)} Ω",
                "Inductance (L)" to "${formatNum(inductanceMh, 2)} mH",
                "Capacitance (C)" to "${formatNum(capacitanceUf, 2)} µF"
            ),
            formula = "f₀ = 1 / (2π × √(L × C))",
            substitution = "f₀ = 1 / (2π × √($lHenries H × $cFarads F)) = ${formatNum(f0Hz, 1)} Hz",
            steps = listOf(
                "Step 1: Convert L to Henries ($lHenries H) and C to Farads ($cFarads F)",
                "Step 2: Product LC = ${formatNum(lHenries * cFarads, 10)} s²",
                "Step 3: Resonant frequency f₀ = ${formatNum(f0Hz, 1)} Hz"
            ),
            standardNote = "LC tank circuit natural electromagnetic resonance."
        )
    }

    fun calculateJouleEffect(currentA: Double, resistanceOhm: Double, timeSec: Double = 60.0): CalculationResult {
        if (currentA <= 0 || resistanceOhm <= 0 || timeSec <= 0) return CalculationResult("", "", formula = "Q = I² × R × t", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Parameters must be positive.")
        val joules = currentA * currentA * resistanceOhm * timeSec
        val calories = joules * 0.239006
        val kwh = joules / 3_600_000.0
        val powerWatts = currentA * currentA * resistanceOhm

        return CalculationResult(
            primaryValue = if (joules >= 1_000_000.0) "${formatNum(joules / 1_000_000.0, 3)} MJ" else "${formatNum(joules, 0)} J",
            primaryUnit = "Thermal Heat Energy",
            secondaryValues = mapOf(
                "Energy in Calories" to "${formatNum(calories, 1)} cal (${formatNum(calories / 1000.0, 2)} kcal)",
                "Energy in kWh" to "${formatNum(kwh, 4)} kWh",
                "Instantaneous Power" to "${formatNum(powerWatts, 1)} W",
                "Duration" to "${formatNum(timeSec, 0)} seconds"
            ),
            formula = "Q = I² × R × t (Joules)",
            substitution = "Q = (${formatNum(currentA, 1)} A)² × ${formatNum(resistanceOhm, 2)} Ω × ${formatNum(timeSec, 0)} s = ${formatNum(joules, 0)} J",
            steps = listOf(
                "Step 1: Dissipated electrical power P = I²R = ${formatNum(powerWatts, 1)} W",
                "Step 2: Integrate over duration t: Q = P × t = ${formatNum(joules, 0)} Joules",
                "Step 3: Thermal calorie conversion = ${formatNum(calories / 1000.0, 2)} kcal"
            ),
            standardNote = "Joule's first law of electrothermal dissipation."
        )
    }

    fun calculateZenerRegulator(
        vinMin: Double = 12.0,
        vinMax: Double = 15.0,
        vz: Double = 5.1,
        loadCurrentMaxMa: Double = 50.0,
        zenerMinCurrentMa: Double = 5.0
    ): CalculationResult {
        if (vinMin <= vz) return CalculationResult("", "", formula = "Rs = (Vin_min - Vz) / (Iload_max + Iz_min)", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Vin must be greater than Zener voltage Vz.")
        val iLoadMaxA = loadCurrentMaxMa / 1000.0
        val iZminA = zenerMinCurrentMa / 1000.0
        val rsOhms = (vinMin - vz) / (iLoadMaxA + iZminA)

        // Max power dissipated in Zener occurs when load is disconnected and Vin is Max:
        val maxZenerCurrentA = (vinMax - vz) / rsOhms
        val maxZenerPowerWatts = vz * maxZenerCurrentA
        val maxRsPowerWatts = ((vinMax - vz) * (vinMax - vz)) / rsOhms

        return CalculationResult(
            primaryValue = "${formatNum(rsOhms, 0)} Ω",
            primaryUnit = "Series Resistor (Rs)",
            secondaryValues = mapOf(
                "Recommended Resistor" to "${formatNum(rsOhms, 0)} Ω (${formatNum(maxRsPowerWatts * 2.0, 1)} W rating)",
                "Max Zener Dissipation" to "${formatNum(maxZenerPowerWatts * 1000.0, 0)} mW (Use 1W or 1.3W Zener)",
                "Regulated Output Vz" to "$vz V",
                "Max Zener Current" to "${formatNum(maxZenerCurrentA * 1000.0, 1)} mA",
                "Load Current Range" to "0 – ${formatNum(loadCurrentMaxMa, 0)} mA"
            ),
            formula = "R_s = (V_in,min - V_z) / (I_load,max + I_z,min)",
            substitution = "R_s = (${formatNum(vinMin, 1)} - $vz) / (${formatNum(iLoadMaxA, 3)} + ${formatNum(iZminA, 3)}) = ${formatNum(rsOhms, 0)} Ω",
            steps = listOf(
                "Step 1: Calculate minimum total supply current required = ${formatNum(iLoadMaxA + iZminA, 3)} A",
                "Step 2: Size series ballast resistor Rs = ${formatNum(rsOhms, 0)} Ω",
                "Step 3: Check worst-case Zener dissipation at Vin_max with no load = ${formatNum(maxZenerPowerWatts * 1000.0, 0)} mW"
            ),
            standardNote = "Zener diode shunt voltage regulation standard."
        )
    }

    fun calculateAnalogSignalScale(
        inVal: Double = 12.0,
        inMin: Double = 4.0,
        inMax: Double = 20.0,
        outMin: Double = 0.0,
        outMax: Double = 100.0
    ): CalculationResult {
        if (inMax == inMin) return CalculationResult("", "", formula = "Linear Interpolation", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Input Max and Min cannot be equal.")
        val fraction = (inVal - inMin) / (inMax - inMin)
        val outVal = outMin + fraction * (outMax - outMin)

        return CalculationResult(
            primaryValue = formatNum(outVal, 2),
            primaryUnit = "Scaled Output",
            secondaryValues = mapOf(
                "Input Value" to "$inVal (${formatNum(fraction * 100.0, 1)}% span)",
                "Input Range" to "$inMin – $inMax",
                "Output Range" to "$outMin – $outMax",
                "Span Resolution" to "${formatNum(fraction, 4)}"
            ),
            formula = "Y = Y_min + [(X - X_min) / (X_max - X_min)] × (Y_max - Y_min)",
            substitution = "Y = $outMin + [($inVal - $inMin) / ($inMax - $inMin)] × ($outMax - $outMin) = ${formatNum(outVal, 2)}",
            steps = listOf(
                "Step 1: Calculate input span percentage = (${formatNum(inVal, 1)} - $inMin) / ($inMax - $inMin) = ${formatNum(fraction * 100.0, 1)}%",
                "Step 2: Linear map onto engineering output span [$outMin to $outMax]",
                "Step 3: Resulting process value = ${formatNum(outVal, 2)}"
            ),
            standardNote = "4-20mA / 0-10V industrial analog instrumentation scaling standard."
        )
    }

    fun calculateAntennaLength(frequencyMhz: Double = 433.0): CalculationResult {
        if (frequencyMhz <= 0) return CalculationResult("", "", formula = "λ = c / f", substitution = "", standardNote = "", isSuccess = false, errorMessage = "Frequency must be positive.")
        val speedOfLight = 299.792 // Mm/s (approx 300)
        val fullWaveM = speedOfLight / frequencyMhz
        val halfWaveM = (fullWaveM / 2.0) * 0.95 // 0.95 velocity factor for standard wire
        val quarterWaveM = halfWaveM / 2.0

        return CalculationResult(
            primaryValue = "${formatNum(quarterWaveM * 100.0, 1)} cm",
            primaryUnit = "Quarter-Wave (λ/4)",
            secondaryValues = mapOf(
                "Quarter-Wave in Inches" to "${formatNum(quarterWaveM * 39.3701, 1)} in",
                "Half-Wave Dipole (λ/2)" to "${formatNum(halfWaveM * 100.0, 1)} cm (${formatNum(halfWaveM * 39.3701, 1)} in)",
                "Full Wavelength (λ)" to "${formatNum(fullWaveM, 3)} m",
                "Frequency" to "$frequencyMhz MHz",
                "Assumed Velocity Factor" to "0.95 (standard insulated wire)"
            ),
            formula = "λ/4 = (300 / f_MHz) × 0.25 × 0.95",
            substitution = "λ/4 = (300 / $frequencyMhz) × 0.2375 = ${formatNum(quarterWaveM, 3)} m",
            steps = listOf(
                "Step 1: Electromagnetic wavelength in free space λ = 300 / $frequencyMhz = ${formatNum(fullWaveM, 3)} m",
                "Step 2: Apply 0.95 velocity factor for copper wire antenna",
                "Step 3: Quarter-wave whip element length = ${formatNum(quarterWaveM * 100.0, 1)} cm"
            ),
            standardNote = "RF antenna resonance and wire dipole design equations."
        )
    }

    fun calculateCctvStorage(
        camerasCount: Int = 8,
        days: Int = 30,
        resolution: String = "1080p",
        compression: String = "H.265"
    ): CalculationResult {
        // Typical bitrates in Mbps
        val baseBitrateMbps = when (resolution) {
            "720p" -> 1.5
            "1080p" -> 3.0
            "4MP (2K)" -> 5.0
            "4K (8MP)" -> 10.0
            else -> 3.0
        }
        val compressionMultiplier = if (compression == "H.265") 0.55 else 1.0
        val effectiveBitratePerCamMbps = baseBitrateMbps * compressionMultiplier

        // Total bits per day per camera = Mbps * 3600 * 24 = bits / 8 = Bytes
        val gbPerDayPerCam = (effectiveBitratePerCamMbps * 3600.0 * 24.0) / (8.0 * 1024.0)
        val totalStorageGb = gbPerDayPerCam * camerasCount * days
        val totalStorageTb = totalStorageGb / 1024.0

        return CalculationResult(
            primaryValue = "${formatNum(totalStorageTb, 2)} TB",
            primaryUnit = "Required HDD Storage",
            secondaryValues = mapOf(
                "Storage in Gigabytes" to "${formatNum(totalStorageGb, 0)} GB",
                "Per Camera Daily Storage" to "${formatNum(gbPerDayPerCam, 1)} GB/day",
                "Bitrate per Camera" to "${formatNum(effectiveBitratePerCamMbps, 2)} Mbps",
                "Total Network Bandwidth" to "${formatNum(effectiveBitratePerCamMbps * camerasCount, 1)} Mbps",
                "Configuration" to "$camerasCount cams × $days days @ $resolution ($compression)"
            ),
            formula = "Storage_GB = (N_cams × Bitrate_Mbps × 3600 × 24 × Days) / (8 × 1024)",
            substitution = "Storage = ($camerasCount × ${formatNum(effectiveBitratePerCamMbps, 2)} × 86400 × $days) / 8192",
            steps = listOf(
                "Step 1: Effective bitrate per stream for $resolution $compression = ${formatNum(effectiveBitratePerCamMbps, 2)} Mbps",
                "Step 2: Total recording bandwidth for $camerasCount cameras = ${formatNum(effectiveBitratePerCamMbps * camerasCount, 1)} Mbps",
                "Step 3: Daily storage needed = ${formatNum(gbPerDayPerCam * camerasCount, 1)} GB/day",
                "Step 4: Total hard disk capacity needed for $days days = ${formatNum(totalStorageTb, 2)} TB (Recommend ${ceil(totalStorageTb).toInt() + 1}TB drive)"
            ),
            standardNote = "CCTV IP surveillance storage and network streaming bandwidth estimation."
        )
    }
}
