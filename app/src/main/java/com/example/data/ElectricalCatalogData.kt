package com.example.data

import androidx.compose.ui.graphics.Color

enum class CatalogCategory(val displayName: String) {
    MAIN("Main"),
    MOTOR("Motor"),
    CONVERSIONS("Conversions"),
    RESOURCES("Resources"),
    PINOUT("Pinout"),
    FORMULAS("Formulas"),
    FAVORITES("Favorites")
}

enum class BadgeType {
    TEXT_CIRCLE,     // Circular badge with text like "ΔV", "A", "V", "W", "VA", "var", "cosφ", "Ω", "Lmax"
    BOOK_FORMULA,    // Red book badge with white "f(x)"
    SQUARE_SYMBOL,   // Square badge with text like "X", "Z", "K²S²"
    CABLE_3CORE,     // 3-colored cable cross section badge
    SCHEMATIC_SYMBOL,// Schematic badge (star-delta, earth ground, inductor, resistor, antenna)
    ICON_SYMBOL,     // Icon-based badge (battery, transformer, hard drive, thermometer, shield, etc.)
    CHIP_RESISTOR,   // SMD resistor "102"
    CAPACITOR_DISK,  // Orange ceramic capacitor "102"
    CONNECTOR_BADGE  // Connector emblem
}

data class PinoutPin(
    val pinNumber: String,
    val name: String,
    val colorName: String,
    val colorHex: Long,
    val description: String,
    val voltage: String = ""
)

data class ElectricalCatalogItem(
    val id: String,
    val title: String,
    val category: CatalogCategory,
    val badgeType: BadgeType,
    val badgeText: String = "",
    val badgeColor: Long = 0xFF38A3B2,
    val isLocked: Boolean = false,
    val formulaTitle: String = "",
    val formulaEquation: String = "",
    val formulaDescription: String = "",
    val formulaVariables: List<Pair<String, String>> = emptyList(),
    val pinoutPins: List<PinoutPin> = emptyList(),
    val calcType: String? = null
)

object ElectricalCatalogData {

    // ==========================================
    // 1. MAIN SCREEN ITEMS (Reference images 1-5)
    // ==========================================
    val mainItems: List<ElectricalCatalogItem> = listOf(
        ElectricalCatalogItem(
            id = "main_conductor_sizing",
            title = "Conductor sizing",
            category = CatalogCategory.MAIN,
            badgeType = BadgeType.CABLE_3CORE,
            badgeColor = 0xFF4CAF50,
            isLocked = false,
            calcType = "conductor_sizing",
            formulaTitle = "Conductor Cross-Section Sizing",
            formulaEquation = "S = (2 · ρ · L · I) / ΔV  (1-Phase)\nS = (√3 · ρ · L · I) / ΔV  (3-Phase)",
            formulaDescription = "Sizes electrical conductors based on allowable voltage drop and current carrying capacity according to IEC 60364-5-52 and NEC Table 310.16.",
            formulaVariables = listOf(
                "S" to "Conductor cross-section area (mm²)",
                "ρ" to "Resistivity (Copper: 0.0178 Ω·mm²/m, Aluminum: 0.0282 Ω·mm²/m)",
                "L" to "Conductor length in meters (m)",
                "I" to "Design current in Amperes (A)",
                "ΔV" to "Allowable voltage drop in Volts (V)"
            )
        ),
        ElectricalCatalogItem(
            id = "main_conductor_sizing_protection",
            title = "Conductor sizing and protective device coordination",
            category = CatalogCategory.MAIN,
            badgeType = BadgeType.ICON_SYMBOL,
            badgeText = "MCB",
            badgeColor = 0xFFEF5350,
            isLocked = true,
            calcType = "coordination",
            formulaTitle = "Protection Coordination (IEC 60364-4-43)",
            formulaEquation = "I_b ≤ I_n ≤ I_z\nI_2 ≤ 1.45 · I_z",
            formulaDescription = "Ensures the rated current of the protective device (In) is between the load design current (Ib) and the cable continuous current capacity (Iz).",
            formulaVariables = listOf(
                "I_b" to "Design operating load current (A)",
                "I_n" to "Nominal rating of protective device / MCB (A)",
                "I_z" to "Continuous current carrying capacity of cable (A)",
                "I_2" to "Conventional tripping current (1.45 · I_n for MCB)"
            )
        ),
        ElectricalCatalogItem(
            id = "main_voltage_drop",
            title = "Calculation of voltage drop",
            category = CatalogCategory.MAIN,
            badgeType = BadgeType.TEXT_CIRCLE,
            badgeText = "ΔV",
            badgeColor = 0xFFE53935, // Red circle
            isLocked = false,
            calcType = "voltage_drop",
            formulaTitle = "Voltage Drop Calculation",
            formulaEquation = "ΔV = 2 · L · I · (R · cosφ + X · sinφ)  (1-Phase)\nΔV = √3 · L · I · (R · cosφ + X · sinφ)  (3-Phase)\nΔV% = (ΔV / V_nominal) · 100%",
            formulaDescription = "Calculates voltage drop along cables considering conductor AC resistance and reactance at operating temperature.",
            formulaVariables = listOf(
                "ΔV" to "Voltage drop in Volts (V)",
                "L" to "Circuit length (km)",
                "I" to "Load current (A)",
                "R" to "AC resistance per unit length (Ω/km)",
                "X" to "Conductor inductive reactance (Ω/km)",
                "cosφ" to "Load power factor"
            )
        ),
        ElectricalCatalogItem(
            id = "main_current",
            title = "Calculation of current",
            category = CatalogCategory.MAIN,
            badgeType = BadgeType.TEXT_CIRCLE,
            badgeText = "A",
            badgeColor = 0xFF43A047, // Green circle
            isLocked = false,
            calcType = "current",
            formulaTitle = "Calculation of Current",
            formulaEquation = "I = P / V  (Direct Current)\nI = P / (V · cosφ)  (AC Single-Phase)\nI = P / (2 · V · cosφ)  (AC Two-Phase)\nI = P / (√3 · V · cosφ)  (AC Three-Phase)",
            formulaDescription = "Calculates electrical current drawn by single-phase, two-phase, three-phase, or DC electrical circuits.",
            formulaVariables = listOf(
                "I" to "Electrical current (Amperes)",
                "P" to "Active power (Watts)",
                "V" to "Voltage (Volts)",
                "cosφ" to "Power factor (dimensionless, 0.0 to 1.0)"
            )
        ),
        ElectricalCatalogItem(
            id = "main_voltage",
            title = "Calculation of voltage",
            category = CatalogCategory.MAIN,
            badgeType = BadgeType.TEXT_CIRCLE,
            badgeText = "V",
            badgeColor = 0xFFE91E63, // Coral pink circle
            isLocked = false,
            calcType = "voltage",
            formulaTitle = "Calculation of Voltage",
            formulaEquation = "V = I · R  (Ohm's Law)\nV = P / (I · cosφ)  (1-Phase AC)\nV = P / (√3 · I · cosφ)  (3-Phase AC)",
            formulaDescription = "Calculates supply voltage or potential difference required for a known electrical load.",
            formulaVariables = listOf(
                "V" to "Voltage (Volts)",
                "P" to "Active power (Watts)",
                "I" to "Current (Amperes)",
                "cosφ" to "Power factor"
            )
        ),
        ElectricalCatalogItem(
            id = "main_active_power",
            title = "Calculation of active power",
            category = CatalogCategory.MAIN,
            badgeType = BadgeType.TEXT_CIRCLE,
            badgeText = "W",
            badgeColor = 0xFF8E24AA, // Purple circle
            isLocked = false,
            calcType = "active_power",
            formulaTitle = "Active Power (Watts)",
            formulaEquation = "P = V · I  (DC)\nP = V · I · cosφ  (1-Phase AC)\nP = √3 · V_L · I_L · cosφ  (3-Phase AC)",
            formulaDescription = "Calculates true active electrical power consumed by an electrical system performing work.",
            formulaVariables = listOf(
                "P" to "Active power (Watts or kW)",
                "V" to "Voltage (Volts)",
                "I" to "Current (Amperes)",
                "cosφ" to "Power factor"
            )
        ),
        ElectricalCatalogItem(
            id = "main_apparent_power",
            title = "Calculation of apparent power",
            category = CatalogCategory.MAIN,
            badgeType = BadgeType.TEXT_CIRCLE,
            badgeText = "VA",
            badgeColor = 0xFF1E88E5, // Blue circle
            isLocked = false,
            calcType = "apparent_power",
            formulaTitle = "Apparent Power (Volt-Amperes)",
            formulaEquation = "S = V · I  (1-Phase AC)\nS = √3 · V_L · I_L  (3-Phase AC)\nS = √(P² + Q²)",
            formulaDescription = "Determines apparent total power in AC systems, used for sizing transformers, generators, and cabling.",
            formulaVariables = listOf(
                "S" to "Apparent power (VA or kVA)",
                "P" to "Active power (W or kW)",
                "Q" to "Reactive power (var or kvar)",
                "V" to "RMS voltage (V)",
                "I" to "RMS current (A)"
            )
        ),
        ElectricalCatalogItem(
            id = "main_reactive_power",
            title = "Calculation of reactive power",
            category = CatalogCategory.MAIN,
            badgeType = BadgeType.TEXT_CIRCLE,
            badgeText = "var",
            badgeColor = 0xFF00ACC1, // Teal/cyan circle
            isLocked = false,
            calcType = "reactive_power",
            formulaTitle = "Reactive Power (var)",
            formulaEquation = "Q = V · I · sinφ  (1-Phase)\nQ = √3 · V_L · I_L · sinφ  (3-Phase)\nQ = √(S² - P²) = P · tanφ",
            formulaDescription = "Calculates inductive or capacitive reactive power needed for alternating magnetic and electric fields.",
            formulaVariables = listOf(
                "Q" to "Reactive power (var or kvar)",
                "sinφ" to "Reactive factor = √(1 - cos²φ)",
                "P" to "Active power (W)",
                "S" to "Apparent power (VA)"
            )
        ),
        ElectricalCatalogItem(
            id = "main_power_factor",
            title = "Calculation of power factor",
            category = CatalogCategory.MAIN,
            badgeType = BadgeType.TEXT_CIRCLE,
            badgeText = "cosφ",
            badgeColor = 0xFFD84315, // Orange/brown circle
            isLocked = false,
            calcType = "power_factor",
            formulaTitle = "Power Factor (cosφ)",
            formulaEquation = "cosφ = P / S = P / √(P² + Q²)\nφ = arccos(P / S)\ntanφ = Q / P",
            formulaDescription = "Calculates the ratio of active power to apparent power in an AC circuit.",
            formulaVariables = listOf(
                "cosφ" to "Power factor (0.00 to 1.00)",
                "P" to "Active power (W)",
                "S" to "Apparent power (VA)",
                "Q" to "Reactive power (var)"
            )
        ),
        ElectricalCatalogItem(
            id = "main_resistance",
            title = "Calculation of resistance",
            category = CatalogCategory.MAIN,
            badgeType = BadgeType.TEXT_CIRCLE,
            badgeText = "Ω",
            badgeColor = 0xFFFBC02D, // Yellow circle
            isLocked = false,
            calcType = "resistance",
            formulaTitle = "Calculation of Resistance",
            formulaEquation = "R = V / I  (Ohm's Law)\nR = ρ · (L / S)  (Conductor Resistance)\nR = V² / P",
            formulaDescription = "Determines electrical resistance of a circuit element or wire conductor.",
            formulaVariables = listOf(
                "R" to "Resistance in Ohms (Ω)",
                "V" to "Voltage in Volts (V)",
                "I" to "Current in Amperes (A)",
                "ρ" to "Material resistivity (Ω·mm²/m)",
                "L" to "Length (m)",
                "S" to "Cross-sectional area (mm²)"
            )
        ),
        ElectricalCatalogItem(
            id = "main_impedance",
            title = "Calculation of impedance",
            category = CatalogCategory.MAIN,
            badgeType = BadgeType.TEXT_CIRCLE,
            badgeText = "Ω",
            badgeColor = 0xFFF57F17, // Golden amber circle
            isLocked = false,
            calcType = "impedance",
            formulaTitle = "Total AC Impedance (Z)",
            formulaEquation = "Z = √(R² + X²) = √(R² + (X_L - X_C)²)\nZ = V / I",
            formulaDescription = "Calculates total opposition to alternating current composed of resistance (R) and net reactance (X).",
            formulaVariables = listOf(
                "Z" to "Impedance in Ohms (Ω)",
                "R" to "Resistance in Ohms (Ω)",
                "X_L" to "Inductive reactance = 2·π·f·L (Ω)",
                "X_C" to "Capacitive reactance = 1 / (2·π·f·C) (Ω)"
            )
        ),
        ElectricalCatalogItem(
            id = "main_max_wire_length_dv",
            title = "Maximum wire length (ΔV)",
            category = CatalogCategory.MAIN,
            badgeType = BadgeType.TEXT_CIRCLE,
            badgeText = "Lmax",
            badgeColor = 0xFF43A047, // Green Lmax circle
            isLocked = false,
            calcType = "max_length_dv",
            formulaTitle = "Maximum Wire Length for Allowable Voltage Drop",
            formulaEquation = "L_max = (ΔV_allow · S) / (2 · ρ · I)  (1-Phase)\nL_max = (ΔV_allow · S) / (√3 · ρ · I)  (3-Phase)",
            formulaDescription = "Calculates maximum allowable cable distance before voltage drop exceeds prescribed maximum (e.g. 3% or 5%).",
            formulaVariables = listOf(
                "L_max" to "Maximum route length (m)",
                "ΔV_allow" to "Allowable voltage drop limit (V)",
                "S" to "Conductor cross section (mm²)",
                "I" to "Design current (A)",
                "ρ" to "Conductor resistivity (Ω·mm²/m)"
            )
        ),
        ElectricalCatalogItem(
            id = "main_max_wire_length_isc",
            title = "Maximum wire length (Isc)",
            category = CatalogCategory.MAIN,
            badgeType = BadgeType.TEXT_CIRCLE,
            badgeText = "Lmax",
            badgeColor = 0xFF00ACC1, // Cyan Lmax circle
            isLocked = false,
            calcType = "max_length_isc",
            formulaTitle = "Maximum Wire Length for Short-Circuit Protection",
            formulaEquation = "L_max = (0.8 · U_0 · S) / (2 · ρ · I_a)",
            formulaDescription = "Determines maximum line length that ensures sufficient fault current to instantaneously trip magnetic protection (Ia) under TN/TT earthing systems.",
            formulaVariables = listOf(
                "U_0" to "Phase-to-neutral voltage (230V)",
                "I_a" to "Trip current for instantaneous magnetic release (A)",
                "S" to "Conductor cross-section area (mm²)",
                "ρ" to "Resistivity at fault temperature (Ω·mm²/m)"
            )
        ),
        ElectricalCatalogItem(
            id = "main_current_capacity_insulated",
            title = "Current carrying capacity of insulated conductors",
            category = CatalogCategory.MAIN,
            badgeType = BadgeType.ICON_SYMBOL,
            badgeText = "Cu",
            badgeColor = 0xFF1976D2,
            isLocked = true,
            formulaTitle = "Cable Ampacity (IEC 60364-5-52)",
            formulaEquation = "I_z = I_0 · k_temp · k_group · k_soil",
            formulaDescription = "Calculates derated continuous current capacity considering ambient temperature, grouping, and installation method.",
            formulaVariables = listOf(
                "I_0" to "Base tabulated ampacity at reference temperature",
                "k_temp" to "Ambient temperature correction factor",
                "k_group" to "Grouping reduction factor for adjacent cables",
                "I_z" to "Permissible derated ampacity (A)"
            )
        ),
        ElectricalCatalogItem(
            id = "main_current_capacity_bare",
            title = "Current carrying capacity of bare / mineral-insulated conductors",
            category = CatalogCategory.MAIN,
            badgeType = BadgeType.ICON_SYMBOL,
            badgeText = "Bare",
            badgeColor = 0xFFD84315,
            isLocked = true,
            formulaTitle = "Bare Conductor Capacity",
            formulaEquation = "I = A · C · √[ (T_max - T_amb) / R_ac ]",
            formulaDescription = "Determines ampacity of overhead bare copper and aluminum conductors and mineral-insulated cables based on thermal dissipation balance.",
            formulaVariables = listOf(
                "T_max" to "Maximum conductor design temperature (°C)",
                "T_amb" to "Ambient temperature (°C)",
                "R_ac" to "AC resistance at operating temperature (Ω/m)"
            )
        ),
        ElectricalCatalogItem(
            id = "main_busbar_capacity",
            title = "Current carrying capacity of busbar",
            category = CatalogCategory.MAIN,
            badgeType = BadgeType.ICON_SYMBOL,
            badgeText = "Bus",
            badgeColor = 0xFFF57C00,
            isLocked = true,
            formulaTitle = "Copper & Aluminum Busbar Capacity (DIN 43671)",
            formulaEquation = "I = k · A^0.5 · p^0.39",
            formulaDescription = "Sizes rectangular and tubular electrical busbars for distribution switchboards and substations.",
            formulaVariables = listOf(
                "A" to "Busbar cross-sectional area (mm²)",
                "p" to "Perimeter of the busbar section (mm)",
                "k" to "Material coefficient (Copper ~ 3.12, Aluminum ~ 2.45)"
            )
        ),
        ElectricalCatalogItem(
            id = "main_conduit_sizing",
            title = "Conduit and cable tray sizing",
            category = CatalogCategory.MAIN,
            badgeType = BadgeType.ICON_SYMBOL,
            badgeText = "Tray",
            badgeColor = 0xFF78909C,
            isLocked = true,
            calcType = "conduit_fill",
            formulaTitle = "Conduit & Cable Tray Fill (NEC Chapter 9 Table 1)",
            formulaEquation = "Fill% = (∑ A_cables / A_internal) · 100% ≤ 40%",
            formulaDescription = "Calculates maximum permissible conduit fill (53% for 1 wire, 31% for 2 wires, 40% for 3+ wires) and tray loading.",
            formulaVariables = listOf(
                "A_cables" to "Total cross-sectional outer area of installed cables",
                "A_internal" to "Conduit internal cross-sectional area"
            )
        ),
        ElectricalCatalogItem(
            id = "main_protective_device_sizing",
            title = "Protective device sizing",
            category = CatalogCategory.MAIN,
            badgeType = BadgeType.ICON_SYMBOL,
            badgeText = "Breaker",
            badgeColor = 0xFF607D8B,
            isLocked = true,
            calcType = "breaker_sizing",
            formulaTitle = "Circuit Breaker & Fuse Sizing",
            formulaEquation = "I_n ≥ I_continuous · 1.25 + I_noncontinuous\nI_cu ≥ I_sc,max",
            formulaDescription = "Determines circuit breaker continuous frame rating and short-circuit breaking capacity (kA).",
            formulaVariables = listOf(
                "I_n" to "Nominal trip rating of MCB / MCCB (A)",
                "I_cu" to "Ultimate short-circuit breaking capacity (kA)",
                "I_sc,max" to "Prospective maximum fault current at device location (kA)"
            )
        ),
        ElectricalCatalogItem(
            id = "main_cable_protection_short_circuit",
            title = "Cable protection from short-circuit",
            category = CatalogCategory.MAIN,
            badgeType = BadgeType.ICON_SYMBOL,
            badgeText = "⚡",
            badgeColor = 0xFFFFB300,
            isLocked = true,
            formulaTitle = "Adiabatic Short-Circuit Protection (IEC 60364-5-54)",
            formulaEquation = "t ≤ (k² · S²) / I²\nI² · t ≤ k² · S²",
            formulaDescription = "Verifies that protective device disconnects short circuits before conductor temperature exceeds insulation limits (PVC: 160°C, XLPE: 250°C).",
            formulaVariables = listOf(
                "t" to "Fault clearing time in seconds (s)",
                "k" to "Material thermal constant (Cu/PVC: 115, Cu/XLPE: 143)",
                "S" to "Conductor cross-sectional area (mm²)",
                "I" to "Prospective short-circuit current (A)"
            )
        ),
        ElectricalCatalogItem(
            id = "main_admissible_energy_k2s2",
            title = "Admissible specific energy of the cable (K²S²)",
            category = CatalogCategory.MAIN,
            badgeType = BadgeType.SQUARE_SYMBOL,
            badgeText = "K²S²",
            badgeColor = 0xFF1976D2,
            isLocked = true,
            formulaTitle = "Specific Joule Energy (K²S²)",
            formulaEquation = "K² · S² = C · ρ_20 · ln[(θ_f + β) / (θ_i + β)] · S²",
            formulaDescription = "Calculates maximum thermal withstand energy (A²s) for copper and aluminum conductors.",
            formulaVariables = listOf(
                "K²S²" to "Allowable specific thermal energy (A²s)",
                "θ_i" to "Initial operating conductor temperature (°C)",
                "θ_f" to "Final short-circuit limit temperature (°C)",
                "β" to "Reciprocal of temperature coefficient of resistance (Cu: 234.5)"
            )
        ),
        ElectricalCatalogItem(
            id = "main_design_current",
            title = "Design current",
            category = CatalogCategory.MAIN,
            badgeType = BadgeType.ICON_SYMBOL,
            badgeText = "Ib",
            badgeColor = 0xFFE53935,
            isLocked = false,
            calcType = "current",
            formulaTitle = "Circuit Design Operating Current (Ib)",
            formulaEquation = "I_b = P / (√3 · V · cosφ · η)",
            formulaDescription = "Calculates continuous steady-state current for single or multi-load circuits including diversity and utilization factors.",
            formulaVariables = listOf(
                "I_b" to "Design current in Amperes (A)",
                "P" to "Active output power in Watts (W)",
                "η" to "Operating efficiency"
            )
        ),
        ElectricalCatalogItem(
            id = "main_reactance",
            title = "Reactance",
            category = CatalogCategory.MAIN,
            badgeType = BadgeType.SQUARE_SYMBOL,
            badgeText = "X",
            badgeColor = 0xFFFF9800,
            isLocked = true,
            formulaTitle = "Inductive & Capacitive Reactance",
            formulaEquation = "X_L = 2 · π · f · L  (Inductive)\nX_C = 1 / (2 · π · f · C)  (Capacitive)\nX_net = X_L - X_C",
            formulaDescription = "Calculates electrical reactance at AC system frequency (50Hz / 60Hz).",
            formulaVariables = listOf(
                "f" to "AC system frequency in Hertz (Hz)",
                "L" to "Inductance in Henrys (H)",
                "C" to "Capacitance in Farads (F)",
                "X" to "Reactance in Ohms (Ω)"
            )
        ),
        ElectricalCatalogItem(
            id = "main_impedance_rz",
            title = "Impedance from resistance and reactance",
            category = CatalogCategory.MAIN,
            badgeType = BadgeType.SQUARE_SYMBOL,
            badgeText = "Z",
            badgeColor = 0xFFFFB300,
            isLocked = true,
            formulaTitle = "Complex Impedance",
            formulaEquation = "Z = R + jX\n|Z| = √(R² + X²)\nθ = arctan(X / R)",
            formulaDescription = "Calculates magnitude and phase angle of complex electrical impedance.",
            formulaVariables = listOf(
                "Z" to "Total impedance magnitude (Ω)",
                "R" to "Resistance (Ω)",
                "X" to "Net reactance (Ω)",
                "θ" to "Phase angle in degrees (°)"
            )
        ),
        ElectricalCatalogItem(
            id = "main_power_factor_correction",
            title = "Power factor correction",
            category = CatalogCategory.MAIN,
            badgeType = BadgeType.ICON_SYMBOL,
            badgeText = "kvar",
            badgeColor = 0xFF43A047,
            isLocked = true,
            calcType = "power_factor_correction",
            formulaTitle = "Capacitor Bank Sizing for Power Factor Correction",
            formulaEquation = "Q_c = P · (tanφ_1 - tanφ_2)\nC = Q_c / (2 · π · f · V²)",
            formulaDescription = "Calculates required reactive power rating (kvar) and capacitance (μF) to improve power factor to target (e.g. 0.95 or 0.98).",
            formulaVariables = listOf(
                "Q_c" to "Required capacitor bank reactive power (kvar)",
                "P" to "Active load power (kW)",
                "tanφ_1" to "Initial tanφ = tan(arccos(cosφ_1))",
                "tanφ_2" to "Target tanφ = tan(arccos(cosφ_2))",
                "C" to "Capacitance in Farads (F)"
            )
        ),
        ElectricalCatalogItem(
            id = "main_pfc_transformers",
            title = "Power factor correction of MV/LV transformers",
            category = CatalogCategory.MAIN,
            badgeType = BadgeType.ICON_SYMBOL,
            badgeText = "Xfmr",
            badgeColor = 0xFF00ACC1,
            isLocked = true,
            formulaTitle = "No-Load Transformer Power Factor Correction",
            formulaEquation = "Q_c0 ≈ I_0% · S_n\nQ_c0 = (I_0 / 100) · S_n",
            formulaDescription = "Determines fixed capacitor bank rating to compensate for reactive magnetizing current of distribution transformers.",
            formulaVariables = listOf(
                "Q_c0" to "Fixed capacitor rating (kvar)",
                "I_0%" to "Transformer no-load current percentage (typically 1% - 2%)",
                "S_n" to "Transformer nominal rated apparent power (kVA)"
            )
        ),
        ElectricalCatalogItem(
            id = "main_capacitor_diff_voltage",
            title = "Capacitor power at a different voltage",
            category = CatalogCategory.MAIN,
            badgeType = BadgeType.ICON_SYMBOL,
            badgeText = "400V",
            badgeColor = 0xFF5C6BC0,
            isLocked = true,
            formulaTitle = "Capacitor Output at Different Operating Voltage",
            formulaEquation = "Q_2 = Q_1 · (V_2 / V_1)²",
            formulaDescription = "Calculates actual reactive power output of a power capacitor bank operated at a voltage different from its nameplate rating.",
            formulaVariables = listOf(
                "Q_1" to "Nominal rated reactive power at nameplate voltage V1 (kvar)",
                "Q_2" to "Actual reactive power at actual operating voltage V2 (kvar)",
                "V_1" to "Rated nominal voltage (V)",
                "V_2" to "Actual operating voltage (V)"
            )
        ),
        ElectricalCatalogItem(
            id = "main_earthing_system",
            title = "Earthing system and coordination with residual current device",
            category = CatalogCategory.MAIN,
            badgeType = BadgeType.SCHEMATIC_SYMBOL,
            badgeText = "⏚",
            badgeColor = 0xFFFFD600,
            isLocked = false,
            formulaTitle = "Earthing Resistance & RCD Coordination (TT System)",
            formulaEquation = "R_A · I_Δn ≤ U_L (50V)\nR_A ≤ 50V / I_Δn",
            formulaDescription = "Calculates maximum allowable earth electrode resistance (RA) for touch voltage safety (50V dry, 25V wet) with RCD sensitivity.",
            formulaVariables = listOf(
                "R_A" to "Earth electrode resistance in Ohms (Ω)",
                "I_Δn" to "Rated residual operating current of RCD (e.g. 0.03A, 0.3A)",
                "U_L" to "Conventional touch voltage limit (50V AC)"
            )
        ),
        ElectricalCatalogItem(
            id = "main_short_circuit_point",
            title = "Short-circuit current at a specific point of the line",
            category = CatalogCategory.MAIN,
            badgeType = BadgeType.SCHEMATIC_SYMBOL,
            badgeText = "⚡",
            badgeColor = 0xFFFFB300,
            isLocked = true,
            formulaTitle = "Prospective Short-Circuit Current (IEC 60909)",
            formulaEquation = "I_sc = (c · U_n) / (√3 · Z_k)\nZ_k = √(R_total² + X_total²)",
            formulaDescription = "Calculates symmetrical 3-phase short-circuit current at any designated distribution point along a feeder line.",
            formulaVariables = listOf(
                "c" to "Voltage factor (typically 1.05 or 1.10)",
                "U_n" to "Nominal line-to-line system voltage (V)",
                "Z_k" to "Total upstream short-circuit impedance (Ω)"
            )
        ),
        ElectricalCatalogItem(
            id = "main_short_circuit_transformer",
            title = "Short-circuit current with transformer substation",
            category = CatalogCategory.MAIN,
            badgeType = BadgeType.ICON_SYMBOL,
            badgeText = "⚡XF",
            badgeColor = 0xFFFFB300,
            isLocked = true,
            formulaTitle = "Transformer Secondary Short-Circuit Current",
            formulaEquation = "I_sc,sec = S_rT / (√3 · U_n · u_kr%)\nZ_T = (u_kr% / 100) · (U_n² / S_rT)",
            formulaDescription = "Calculates maximum fault current at secondary terminals of MV/LV distribution transformers.",
            formulaVariables = listOf(
                "S_rT" to "Transformer nominal apparent power (kVA)",
                "U_n" to "Secondary nominal line voltage (V)",
                "u_kr%" to "Short-circuit impedance percentage (typically 4% or 6%)"
            )
        ),
        ElectricalCatalogItem(
            id = "main_min_short_circuit",
            title = "Minimum short-circuit current",
            category = CatalogCategory.MAIN,
            badgeType = BadgeType.SCHEMATIC_SYMBOL,
            badgeText = "⚡min",
            badgeColor = 0xFFFFC107,
            isLocked = false,
            formulaTitle = "Minimum Short-Circuit Current at Cable End",
            formulaEquation = "I_sc,min = (0.95 · U_0) / (2 · Z_loop)\nZ_loop = √(R_loop² + X_loop²)",
            formulaDescription = "Calculates minimum line-to-neutral or line-to-earth fault current at the farthest end of a circuit to verify magnetic breaker tripping.",
            formulaVariables = listOf(
                "U_0" to "Nominal phase-to-neutral voltage (230V)",
                "Z_loop" to "Total fault loop impedance (phase + PE/neutral conductors)"
            )
        ),
        ElectricalCatalogItem(
            id = "main_cable_rxz",
            title = "Cable resistance, reactance and impedance",
            category = CatalogCategory.MAIN,
            badgeType = BadgeType.SQUARE_SYMBOL,
            badgeText = "RXZ",
            badgeColor = 0xFFFFD54F,
            isLocked = false,
            formulaTitle = "Cable Line Parameters (R, X, Z)",
            formulaEquation = "R = (ρ_20 · L / S) · [1 + α · (θ - 20)]\nX = 2 · π · f · L_ind · L\nZ = √(R² + X²)",
            formulaDescription = "Computes line resistance, inductive reactance, and resulting impedance for single-core and multicore power cables.",
            formulaVariables = listOf(
                "R" to "Conductor resistance at temperature θ (Ω)",
                "X" to "Conductor inductive reactance (Ω)",
                "Z" to "Total cable impedance (Ω)"
            )
        ),
        ElectricalCatalogItem(
            id = "main_cable_temp",
            title = "Calculation of the cable temperature",
            category = CatalogCategory.MAIN,
            badgeType = BadgeType.ICON_SYMBOL,
            badgeText = "🌡",
            badgeColor = 0xFFFF5722,
            isLocked = true,
            formulaTitle = "Operating Conductor Temperature",
            formulaEquation = "T_c = T_amb + (T_max - T_amb) · (I / I_z)²",
            formulaDescription = "Estimates steady-state conductor operating temperature at actual running load current I.",
            formulaVariables = listOf(
                "T_c" to "Conductor operating temperature (°C)",
                "T_amb" to "Ambient air/ground temperature (°C)",
                "T_max" to "Maximum allowable insulation temperature (70°C PVC, 90°C XLPE)",
                "I" to "Actual load current (A)",
                "I_z" to "Permissible cable ampacity (A)"
            )
        ),
        ElectricalCatalogItem(
            id = "main_cable_losses",
            title = "Power losses in cables",
            category = CatalogCategory.MAIN,
            badgeType = BadgeType.ICON_SYMBOL,
            badgeText = "Loss",
            badgeColor = 0xFF7E57C2,
            isLocked = true,
            formulaTitle = "Joule Heat Losses in Cables",
            formulaEquation = "P_loss = 2 · R · I²  (1-Phase)\nP_loss = 3 · R · I²  (3-Phase)\nE_loss = P_loss · t  (kWh)",
            formulaDescription = "Calculates electrical energy and financial loss dissipation due to Joule heating along cables.",
            formulaVariables = listOf(
                "P_loss" to "Power dissipation loss in Watts (W)",
                "R" to "Single conductor resistance (Ω)",
                "I" to "Operating load current (A)",
                "E_loss" to "Energy lost over time t (kWh)"
            )
        ),
        ElectricalCatalogItem(
            id = "main_neutral_current",
            title = "Neutral current",
            category = CatalogCategory.MAIN,
            badgeType = BadgeType.SCHEMATIC_SYMBOL,
            badgeText = "I_N",
            badgeColor = 0xFF26A69A,
            isLocked = true,
            formulaTitle = "Neutral Conductor Current with Unbalanced Loads",
            formulaEquation = "I_N = √(I_A² + I_B² + I_C² - I_A·I_B - I_B·I_C - I_C·I_A)",
            formulaDescription = "Determines return neutral current in 3-phase 4-wire systems under unbalanced resistive loading.",
            formulaVariables = listOf(
                "I_A, I_B, I_C" to "Phase currents (Amperes)",
                "I_N" to "Neutral conductor return current (Amperes)"
            )
        ),
        ElectricalCatalogItem(
            id = "main_voltage_drop_distributed",
            title = "Voltage drop with distributed loads",
            category = CatalogCategory.MAIN,
            badgeType = BadgeType.TEXT_CIRCLE,
            badgeText = "ΔV",
            badgeColor = 0xFFE53935,
            isLocked = true,
            formulaTitle = "Voltage Drop Along Distributed Branch Line",
            formulaEquation = "ΔV_total = ∑ [ (2 · ρ · L_i · I_cum,i) / S_i ]",
            formulaDescription = "Calculates cumulative voltage drop for street lighting, conveyor belts, or daisy-chained subpanels.",
            formulaVariables = listOf(
                "L_i" to "Segment length between taps",
                "I_cum,i" to "Cumulative downstream current carried by segment i"
            )
        ),
        ElectricalCatalogItem(
            id = "main_resistor_color_code",
            title = "Resistor color code",
            category = CatalogCategory.MAIN,
            badgeType = BadgeType.ICON_SYMBOL,
            badgeText = "4/5B",
            badgeColor = 0xFF8D6E63,
            isLocked = false,
            calcType = "resistor_color",
            formulaTitle = "4-Band & 5-Band Resistor Color Bands",
            formulaEquation = "R = (D_1 · 10 + D_2) · 10^M ± Tol%  (4-Band)\nR = (D_1 · 100 + D_2 · 10 + D_3) · 10^M ± Tol%  (5-Band)",
            formulaDescription = "Decodes and calculates resistor value and tolerance from standard color rings (Black, Brown, Red, Orange, Yellow, Green, Blue, Violet, Gray, White).",
            formulaVariables = listOf(
                "D_1, D_2, D_3" to "Significant digit bands",
                "M" to "Multiplier power of 10",
                "Tol" to "Tolerance percentage (Gold 5%, Silver 10%, Brown 1%)"
            )
        ),
        ElectricalCatalogItem(
            id = "main_inductor_color_code",
            title = "Inductor color code",
            category = CatalogCategory.MAIN,
            badgeType = BadgeType.ICON_SYMBOL,
            badgeText = "μH",
            badgeColor = 0xFF2E7D32,
            isLocked = false,
            calcType = "inductor_color",
            formulaTitle = "Inductor Color Code (μH)",
            formulaEquation = "L = (D_1 · 10 + D_2) · 10^M ± Tol% (μH)",
            formulaDescription = "Decodes molded axial RF and power inductor inductance values in microhenries (μH) from color bands.",
            formulaVariables = listOf(
                "L" to "Inductance in microhenries (μH)",
                "D_1, D_2" to "First and second value digits",
                "M" to "Decimal multiplier band"
            )
        ),
        ElectricalCatalogItem(
            id = "main_smd_resistor",
            title = "SMD resistor code",
            category = CatalogCategory.MAIN,
            badgeType = BadgeType.CHIP_RESISTOR,
            badgeText = "102",
            badgeColor = 0xFF455A64,
            isLocked = false,
            formulaTitle = "SMD Chip Resistor Marking (3-Digit, 4-Digit, EIA-96)",
            formulaEquation = "\"102\" = 10 · 10² = 1,000 Ω (1 kΩ)\n\"4702\" = 470 · 10² = 47,000 Ω (47 kΩ)\n\"R050\" = 0.050 Ω",
            formulaDescription = "Decodes surface-mount SMD resistor alphanumeric codes and EIA-96 1% precision markings.",
            formulaVariables = listOf(
                "First digits" to "Significant value digits",
                "Last digit" to "Number of zeros multiplier",
                "R" to "Decimal point position (e.g. 4R7 = 4.7Ω)"
            )
        ),
        ElectricalCatalogItem(
            id = "main_capacitor_code",
            title = "Capacitor code",
            category = CatalogCategory.MAIN,
            badgeType = BadgeType.CAPACITOR_DISK,
            badgeText = "102",
            badgeColor = 0xFFFF8A65,
            isLocked = false,
            formulaTitle = "Capacitor 3-Digit Code (pF)",
            formulaEquation = "\"104\" = 10 · 10⁴ pF = 100,000 pF = 100 nF = 0.1 μF",
            formulaDescription = "Decodes ceramic, film, and tantalum capacitor 3-digit marking codes into pF, nF, and μF.",
            formulaVariables = listOf(
                "Base unit" to "Picofarads (pF)",
                "104" to "10 followed by 4 zeros = 100,000 pF = 100 nF",
                "Letters (J, K, M)" to "Tolerance: J=±5%, K=±10%, M=±20%"
            )
        ),
        ElectricalCatalogItem(
            id = "main_sum_resistors",
            title = "Sum of resistors",
            category = CatalogCategory.MAIN,
            badgeType = BadgeType.ICON_SYMBOL,
            badgeText = "R+R",
            badgeColor = 0xFFFFB300,
            isLocked = false,
            calcType = "resistors_sum",
            formulaTitle = "Series & Parallel Resistors",
            formulaEquation = "R_series = R_1 + R_2 + ... + R_n\n1 / R_parallel = 1/R_1 + 1/R_2 + ... + 1/R_n\nR_p = (R_1 · R_2) / (R_1 + R_2)",
            formulaDescription = "Calculates equivalent resistance of networks connected in series, parallel, or mixed configurations.",
            formulaVariables = listOf(
                "R_series" to "Sum of individual series resistances",
                "R_parallel" to "Equivalent reciprocal sum of parallel resistances"
            )
        ),
        ElectricalCatalogItem(
            id = "main_sum_capacitors",
            title = "Sum of capacitors",
            category = CatalogCategory.MAIN,
            badgeType = BadgeType.ICON_SYMBOL,
            badgeText = "C+C",
            badgeColor = 0xFF00ACC1,
            isLocked = false,
            calcType = "capacitors_sum",
            formulaTitle = "Series & Parallel Capacitors",
            formulaEquation = "C_parallel = C_1 + C_2 + ... + C_n\n1 / C_series = 1/C_1 + 1/C_2 + ... + 1/C_n\nC_s = (C_1 · C_2) / (C_1 + C_2)",
            formulaDescription = "Calculates equivalent capacitance of parallel and series capacitor arrangements.",
            formulaVariables = listOf(
                "C_parallel" to "Sum of parallel capacitances (values add directly)",
                "C_series" to "Series equivalent capacitance (total is less than smallest)"
            )
        ),
        ElectricalCatalogItem(
            id = "main_resonant_frequency",
            title = "Resonant frequency",
            category = CatalogCategory.MAIN,
            badgeType = BadgeType.SCHEMATIC_SYMBOL,
            badgeText = "f₀",
            badgeColor = 0xFF42A5F5,
            isLocked = true,
            formulaTitle = "LC Resonant Frequency (Thomson Formula)",
            formulaEquation = "f_0 = 1 / (2 · π · √(L · C))\nω_0 = 1 / √(L · C)",
            formulaDescription = "Calculates resonant frequency where inductive reactance equals capacitive reactance in LC tuned circuits.",
            formulaVariables = listOf(
                "f_0" to "Resonant frequency in Hertz (Hz)",
                "L" to "Inductance in Henrys (H)",
                "C" to "Capacitance in Farads (F)"
            )
        ),
        ElectricalCatalogItem(
            id = "main_voltage_divider",
            title = "Voltage divider",
            category = CatalogCategory.MAIN,
            badgeType = BadgeType.SCHEMATIC_SYMBOL,
            badgeText = "V_div",
            badgeColor = 0xFF8D6E63,
            isLocked = true,
            calcType = "voltage_divider",
            formulaTitle = "Resistive Voltage Divider",
            formulaEquation = "V_out = V_in · [ R_2 / (R_1 + R_2) ]",
            formulaDescription = "Calculates output voltage across R2 for an unloaded resistive voltage divider.",
            formulaVariables = listOf(
                "V_out" to "Divided output voltage across R2 (V)",
                "V_in" to "Input supply voltage (V)",
                "R_1" to "Top resistor value (Ω)",
                "R_2" to "Bottom grounded resistor value (Ω)"
            )
        ),
        ElectricalCatalogItem(
            id = "main_current_divider",
            title = "Current divider",
            category = CatalogCategory.MAIN,
            badgeType = BadgeType.SCHEMATIC_SYMBOL,
            badgeText = "I_div",
            badgeColor = 0xFFEF5350,
            isLocked = true,
            formulaTitle = "Parallel Current Divider",
            formulaEquation = "I_1 = I_total · [ R_2 / (R_1 + R_2) ]\nI_2 = I_total · [ R_1 / (R_1 + R_2) ]",
            formulaDescription = "Calculates current branching through parallel resistive paths.",
            formulaVariables = listOf(
                "I_total" to "Total incoming current (A)",
                "I_1" to "Current flowing through resistor R1 (A)",
                "I_2" to "Current flowing through resistor R2 (A)"
            )
        ),
        ElectricalCatalogItem(
            id = "main_battery_life",
            title = "Battery life",
            category = CatalogCategory.MAIN,
            badgeType = BadgeType.ICON_SYMBOL,
            badgeText = "🔋",
            badgeColor = 0xFF43A047,
            isLocked = false,
            calcType = "battery_life",
            formulaTitle = "Battery Run Time & Autonomy",
            formulaEquation = "Time (hours) = [ Battery Capacity (Ah) · DoD · 0.85 ] / Load Current (A)\nEnergy (Wh) = Capacity (Ah) · Voltage (V)",
            formulaDescription = "Estimates operational run time for battery banks considering depth of discharge (DoD) and inverter/system conversion losses.",
            formulaVariables = listOf(
                "Capacity" to "Battery nominal capacity in Ampere-hours (Ah)",
                "DoD" to "Depth of discharge (e.g. 50% for Lead-Acid, 85% for LiFePO4)",
                "Load Current" to "Average continuous current drawn by load (A)"
            )
        ),
        ElectricalCatalogItem(
            id = "main_joule_effect",
            title = "Joule effect",
            category = CatalogCategory.MAIN,
            badgeType = BadgeType.ICON_SYMBOL,
            badgeText = "J",
            badgeColor = 0xFF42A5F5,
            isLocked = false,
            formulaTitle = "Joule's Law of Thermal Energy",
            formulaEquation = "Q = I² · R · t  (Joules)\nP = I² · R  (Watts)",
            formulaDescription = "Calculates heat energy dissipated by electric current passing through a resistive conductor over time.",
            formulaVariables = listOf(
                "Q" to "Heat energy produced in Joules (J)",
                "I" to "Current in Amperes (A)",
                "R" to "Resistance in Ohms (Ω)",
                "t" to "Duration in seconds (s)"
            )
        )
    )

    // ==========================================
    // 2. MOTOR SCREEN ITEMS (Reference image 15)
    // ==========================================
    val motorItems: List<ElectricalCatalogItem> = listOf(
        ElectricalCatalogItem(
            id = "motor_connections",
            title = "Motor connections",
            category = CatalogCategory.MOTOR,
            badgeType = BadgeType.SCHEMATIC_SYMBOL,
            badgeText = "λ / Δ",
            badgeColor = 0xFF26A69A,
            isLocked = false,
            formulaTitle = "Star (Y) & Delta (Δ) Motor Windings",
            formulaEquation = "Star: V_phase = V_line / √3, I_phase = I_line\nDelta: V_phase = V_line, I_phase = I_line / √3\nStarting Torque: T_start,star = 1/3 · T_start,delta",
            formulaDescription = "Details 6-terminal motor connections (U1-V1-W1 to U2-V2-W2) for low/high voltage supply and Star-Delta starting reduced inrush current.",
            formulaVariables = listOf(
                "Star connection" to "Links W2-U2-V2 together with jumpers; supply to U1-V1-W1",
                "Delta connection" to "Jumpers connect U1-W2, V1-U2, W1-V2; supply to U1-V1-W1"
            )
        ),
        ElectricalCatalogItem(
            id = "motor_terminals_marking",
            title = "Motor terminals marking",
            category = CatalogCategory.MOTOR,
            badgeType = BadgeType.SQUARE_SYMBOL,
            badgeText = "U1 V1 W1",
            badgeColor = 0xFFD7CCC8,
            isLocked = false,
            formulaTitle = "IEC 60034-8 & NEMA Motor Terminal Markings",
            formulaEquation = "IEC: U1-U2 (Phase 1), V1-V2 (Phase 2), W1-W2 (Phase 3)\nNEMA: T1-T4, T2-T5, T3-T6 (9-lead / 12-lead)",
            formulaDescription = "Terminal block designations and wiring configurations for single-speed, dual-voltage, and multi-tap induction motors.",
            formulaVariables = listOf(
                "Phase 1" to "Terminals U1 and U2",
                "Phase 2" to "Terminals V1 and V2",
                "Phase 3" to "Terminals W1 and W2"
            )
        ),
        ElectricalCatalogItem(
            id = "motor_insulation_class",
            title = "Insulation class of the motor",
            category = CatalogCategory.MOTOR,
            badgeType = BadgeType.ICON_SYMBOL,
            badgeText = "Class",
            badgeColor = 0xFF42A5F5,
            isLocked = false,
            formulaTitle = "NEMA / IEC Motor Insulation Classes",
            formulaEquation = "Class A: 105°C limit (60°C rise)\nClass B: 130°C limit (80°C rise)\nClass F: 155°C limit (105°C rise)\nClass H: 180°C limit (125°C rise)",
            formulaDescription = "Specifies maximum operating winding temperature ratings for motor winding varnish, slot insulation, and enamels at 40°C ambient.",
            formulaVariables = listOf(
                "Class F" to "Most common modern standard (155°C peak limit)",
                "Class H" to "Heavy duty / high ambient industrial motor rating (180°C limit)"
            )
        ),
        ElectricalCatalogItem(
            id = "motor_current",
            title = "Motor current",
            category = CatalogCategory.MOTOR,
            badgeType = BadgeType.TEXT_CIRCLE,
            badgeText = "I_m",
            badgeColor = 0xFF43A047,
            isLocked = false,
            calcType = "motor_current",
            formulaTitle = "Full Load Current (FLC)",
            formulaEquation = "I = (P_kW · 1000) / (√3 · V_L · cosφ · η)  (3-Phase)\nI = (P_kW · 1000) / (V · cosφ · η)  (1-Phase)\nI_HP = (HP · 746) / (√3 · V_L · cosφ · η)",
            formulaDescription = "Calculates full-load operating current for single-phase and 3-phase AC electric induction motors.",
            formulaVariables = listOf(
                "P" to "Shaft mechanical output power (kW or HP)",
                "V" to "Line-to-line voltage (V)",
                "cosφ" to "Motor power factor (typically 0.80 to 0.88)",
                "η" to "Motor efficiency (typically 0.82 to 0.95)"
            )
        ),
        ElectricalCatalogItem(
            id = "motor_power",
            title = "Motor power",
            category = CatalogCategory.MOTOR,
            badgeType = BadgeType.TEXT_CIRCLE,
            badgeText = "P_m",
            badgeColor = 0xFF8E24AA,
            isLocked = false,
            formulaTitle = "Motor Shaft Power & Input Power",
            formulaEquation = "P_mech = (2 · π · n · T) / 60  (Watts)\nP_mech(kW) = (n · T) / 9550\nP_electrical = √3 · V · I · cosφ",
            formulaDescription = "Calculates mechanical output power from rotational speed and torque, and input electrical power.",
            formulaVariables = listOf(
                "n" to "Rotor rotational speed in RPM",
                "T" to "Motor output torque in Newton-meters (N·m)",
                "P_mech" to "Mechanical shaft power (kW)"
            )
        ),
        ElectricalCatalogItem(
            id = "motor_pf_correction",
            title = "Power factor correction of motor",
            category = CatalogCategory.MOTOR,
            badgeType = BadgeType.ICON_SYMBOL,
            badgeText = "kVAR",
            badgeColor = 0xFF00ACC1,
            isLocked = false,
            calcType = "motor_pf_correction",
            formulaTitle = "Motor Power Factor Correction (Capacitor Bank Sizing)",
            formulaEquation = "Q_c = P_in · [tan(φ₁) - tan(φ₂)]  (kVAR)\nC_Δ (μF) = (Q_c · 10⁹) / (3 · 2π · f · V²)\nQ_c,max ≤ 0.90 · Q_no-load  (Self-Excitation Limit)",
            formulaDescription = "Determines required individual capacitor bank kVAR and per-phase microfarad (μF) ratings to correct inductive motor power factor to 0.95+, line current relief, cable I²R loss reduction, and self-excitation safety limits per IEC 60831 and IEEE 141.",
            formulaVariables = listOf(
                "Q_c" to "Required capacitor reactive power in kVAR",
                "P_in" to "Motor electrical active input power (kW) = P_shaft / η",
                "cosφ₁" to "Initial uncorrected motor power factor (e.g. 0.80)",
                "cosφ₂" to "Desired target power factor (e.g. 0.95)",
                "C_Δ" to "Delta-connected capacitor capacitance (μF/phase)",
                "C_Y" to "Star-connected capacitor capacitance (μF/phase)",
                "Q_c,max" to "Maximum self-excitation safe limit (≤ 90% of motor no-load kVAR)"
            )
        ),
        ElectricalCatalogItem(
            id = "motor_efficiency",
            title = "Motor efficiency",
            category = CatalogCategory.MOTOR,
            badgeType = BadgeType.ICON_SYMBOL,
            badgeText = "η",
            badgeColor = 0xFF43A047,
            isLocked = false,
            formulaTitle = "Motor Efficiency (IEC 60034-30-1 IE Classes)",
            formulaEquation = "η = (P_out / P_in) · 100%\nη = P_shaft / (√3 · V · I · cosφ) · 100%",
            formulaDescription = "Determines motor efficiency class: IE1 (Standard), IE2 (High), IE3 (Premium), IE4 (Super Premium).",
            formulaVariables = listOf(
                "P_out" to "Mechanical output shaft power (kW)",
                "P_in" to "Electrical input power (kW)",
                "η" to "Efficiency percentage (%)"
            )
        ),
        ElectricalCatalogItem(
            id = "motor_speed",
            title = "Motor speed",
            category = CatalogCategory.MOTOR,
            badgeType = BadgeType.ICON_SYMBOL,
            badgeText = "RPM",
            badgeColor = 0xFF1E88E5,
            isLocked = false,
            formulaTitle = "Synchronous & Rotor Speed",
            formulaEquation = "n_s = (120 · f) / p\nn = n_s · (1 - s)",
            formulaDescription = "Calculates stator magnetic synchronous speed and actual rotor shaft speed based on frequency and pole count.",
            formulaVariables = listOf(
                "n_s" to "Synchronous speed in RPM (e.g. 1500 RPM for 4 poles at 50Hz, 1800 RPM at 60Hz)",
                "f" to "Supply frequency (50Hz / 60Hz)",
                "p" to "Number of magnetic poles (2, 4, 6, 8)",
                "s" to "Motor slip (typically 2% to 6%)"
            )
        ),
        ElectricalCatalogItem(
            id = "motor_slip",
            title = "Motor slip",
            category = CatalogCategory.MOTOR,
            badgeType = BadgeType.ICON_SYMBOL,
            badgeText = "s%",
            badgeColor = 0xFFFF9800,
            isLocked = false,
            formulaTitle = "Induction Motor Slip",
            formulaEquation = "s = (n_s - n) / n_s\ns% = [ (n_s - n) / n_s ] · 100%\nf_rotor = s · f",
            formulaDescription = "Calculates relative difference between stator magnetic field speed and rotor mechanical speed.",
            formulaVariables = listOf(
                "n_s" to "Synchronous speed (RPM)",
                "n" to "Actual shaft speed under load (RPM)",
                "s%" to "Percentage slip",
                "f_rotor" to "Induced rotor current frequency (Hz)"
            )
        ),
        ElectricalCatalogItem(
            id = "motor_torque",
            title = "Power / Maximum torque",
            category = CatalogCategory.MOTOR,
            badgeType = BadgeType.ICON_SYMBOL,
            badgeText = "N·m",
            badgeColor = 0xFF5E35B1,
            isLocked = false,
            formulaTitle = "Motor Shaft Rated & Breakdown Torque",
            formulaEquation = "T = (9550 · P_kW) / n_rpm  (N·m)\nT_lbft = (5252 · HP) / n_rpm  (lb·ft)\nT_max ≈ 2.0 to 3.0 · T_rated",
            formulaDescription = "Calculates rated continuous torque in N·m and breakdown maximum stall torque.",
            formulaVariables = listOf(
                "T" to "Rated shaft torque (N·m)",
                "P_kW" to "Motor rated power (kW)",
                "n_rpm" to "Full load rotor speed (RPM)"
            )
        ),
        ElectricalCatalogItem(
            id = "motor_three_to_single_phase",
            title = "Motor from three-phase to single-phase",
            category = CatalogCategory.MOTOR,
            badgeType = BadgeType.ICON_SYMBOL,
            badgeText = "3φ→1φ",
            badgeColor = 0xFFD84315,
            isLocked = false,
            formulaTitle = "Steinmetz Three-Phase to Single-Phase Connection",
            formulaEquation = "C_run (μF) ≈ 70 · P_kW · (230 / V)² · (50 / f)\nC_start ≈ 2.5 · C_run\nP_available ≈ 70% to 80% · P_nominal",
            formulaDescription = "Sizes run and start capacitors to operate a standard 3-phase induction motor from a single-phase AC supply using the Steinmetz circuit.",
            formulaVariables = listOf(
                "C_run" to "Continuous running capacitor value in microfarads (μF)",
                "C_start" to "Starting electrolytic capacitor value (μF)",
                "P_kW" to "Nameplate motor power in kilowatts"
            )
        ),
        ElectricalCatalogItem(
            id = "motor_run_capacitor",
            title = "Single-phase motor run capacitor",
            category = CatalogCategory.MOTOR,
            badgeType = BadgeType.ICON_SYMBOL,
            badgeText = "C_run",
            badgeColor = 0xFF00897B,
            isLocked = false,
            formulaTitle = "Permanent Split Capacitor (PSC) Sizing",
            formulaEquation = "C (μF) = (I_aux · sinφ · 10⁶) / (2 · π · f · V)\nC ≈ (30 to 40) · P_HP  (Rule of Thumb for 230V)",
            formulaDescription = "Calculates run capacitor capacitance for single-phase capacitor-start / capacitor-run motors.",
            formulaVariables = listOf(
                "C" to "Run capacitance in microfarads (μF)",
                "V" to "Supply voltage (V)",
                "f" to "Mains frequency (Hz)"
            )
        )
    )

    // ==========================================
    // 3. PINOUT SCREEN ITEMS (Reference images 16-18)
    // ==========================================
    val pinoutItems: List<ElectricalCatalogItem> = listOf(
        ElectricalCatalogItem(
            id = "pinout_rj45",
            title = "Ethernet wiring (RJ-45)",
            category = CatalogCategory.PINOUT,
            badgeType = BadgeType.CONNECTOR_BADGE,
            badgeText = "RJ45",
            badgeColor = 0xFF1976D2,
            formulaTitle = "T568A and T568B Modular 8P8C Ethernet Wiring",
            formulaEquation = "T568B: W/Orange, Orange, W/Green, Blue, W/Blue, Green, W/Brown, Brown",
            formulaDescription = "Standard pin assignments for gigabit and 100Base-TX twisted pair network cabling.",
            pinoutPins = listOf(
                PinoutPin("1", "TX+ / BI_DA+", "White/Orange", 0xFFFF9800, "Transmit Data +", "1.5V to 2.5V"),
                PinoutPin("2", "TX- / BI_DA-", "Orange", 0xFFF57C00, "Transmit Data -", "1.5V to 2.5V"),
                PinoutPin("3", "RX+ / BI_DB+", "White/Green", 0xFF81C784, "Receive Data +", "1.5V to 2.5V"),
                PinoutPin("4", "BI_DC+", "Blue", 0xFF2196F3, "Bidirectional C+", "PoE +48V"),
                PinoutPin("5", "BI_DC-", "White/Blue", 0xFF64B5F6, "Bidirectional C-", "PoE +48V"),
                PinoutPin("6", "RX- / BI_DB-", "Green", 0xFF4CAF50, "Receive Data -", "1.5V to 2.5V"),
                PinoutPin("7", "BI_DD+", "White/Brown", 0xFFA1887F, "Bidirectional D+", "PoE -48V"),
                PinoutPin("8", "BI_DD-", "Brown", 0xFF795548, "Bidirectional D-", "PoE -48V")
            )
        ),
        ElectricalCatalogItem(
            id = "pinout_poe",
            title = "Pinout Ethernet with PoE",
            category = CatalogCategory.PINOUT,
            badgeType = BadgeType.CONNECTOR_BADGE,
            badgeText = "PoE",
            badgeColor = 0xFF0288D1,
            formulaTitle = "IEEE 802.3af / 802.3at / 802.3bt Power over Ethernet",
            formulaEquation = "Mode A: Power on Data Pins 1,2 (+) & 3,6 (-)\nMode B: Power on Spare Pins 4,5 (+) & 7,8 (-)",
            formulaDescription = "48V DC power injection over twisted pair for IP cameras, access points, and VoIP phones.",
            pinoutPins = listOf(
                PinoutPin("1", "Data + / DC+", "White/Orange", 0xFFFF9800, "Mode A Power (+) / TX+", "44 - 57V DC"),
                PinoutPin("2", "Data - / DC+", "Orange", 0xFFF57C00, "Mode A Power (+) / TX-", "44 - 57V DC"),
                PinoutPin("3", "Data + / DC-", "White/Green", 0xFF81C784, "Mode A Power (-) / RX+", "0V Return"),
                PinoutPin("4", "Spare / DC+", "Blue", 0xFF2196F3, "Mode B Power (+) / Gigabit", "44 - 57V DC"),
                PinoutPin("5", "Spare / DC+", "White/Blue", 0xFF64B5F6, "Mode B Power (+) / Gigabit", "44 - 57V DC"),
                PinoutPin("6", "Data - / DC-", "Green", 0xFF4CAF50, "Mode A Power (-) / RX-", "0V Return"),
                PinoutPin("7", "Spare / DC-", "White/Brown", 0xFFA1887F, "Mode B Power (-) / Gigabit", "0V Return"),
                PinoutPin("8", "Spare / DC-", "Brown", 0xFF795548, "Mode B Power (-) / Gigabit", "0V Return")
            )
        ),
        ElectricalCatalogItem(
            id = "pinout_rj_modular",
            title = "RJ-9,11,14,25,48",
            category = CatalogCategory.PINOUT,
            badgeType = BadgeType.CONNECTOR_BADGE,
            badgeText = "RJ11",
            badgeColor = 0xFF78909C,
            formulaTitle = "Modular Telephone & ISDN Connectors (4P4C, 6P2C, 6P4C, 6P6C)",
            formulaEquation = "RJ-11: 6P2C (Pins 3, 4 Tip & Ring)\nRJ-14: 6P4C (Line 1 & Line 2)\nRJ-48: T1 / E1 Balanced Data",
            formulaDescription = "Telecommunication and serial instrument connection pinouts.",
            pinoutPins = listOf(
                PinoutPin("1", "Line 3 Tip", "White", 0xFFECEFF1, "RJ-25 Line 3 Tip", "+48V On-hook"),
                PinoutPin("2", "Line 2 Tip", "Black", 0xFF212121, "RJ-14/25 Line 2 Tip", "+48V On-hook"),
                PinoutPin("3", "Line 1 Ring", "Red", 0xFFE53935, "RJ-11/14/25 Line 1 Ring", "-48V Battery"),
                PinoutPin("4", "Line 1 Tip", "Green", 0xFF4CAF50, "RJ-11/14/25 Line 1 Tip", "Ground / Return"),
                PinoutPin("5", "Line 2 Ring", "Yellow", 0xFFFFEB3B, "RJ-14/25 Line 2 Ring", "-48V Battery"),
                PinoutPin("6", "Line 3 Ring", "Blue", 0xFF2196F3, "RJ-25 Line 3 Ring", "-48V Battery")
            )
        ),
        ElectricalCatalogItem(
            id = "pinout_usb",
            title = "Pinout USB",
            category = CatalogCategory.PINOUT,
            badgeType = BadgeType.CONNECTOR_BADGE,
            badgeText = "USB",
            badgeColor = 0xFF0288D1,
            formulaTitle = "USB 2.0, 3.0 and USB Type-C Pinouts",
            formulaEquation = "USB 2.0: Pin 1 (VBUS +5V), Pin 2 (D-), Pin 3 (D+), Pin 4 (GND)",
            formulaDescription = "Pinouts for Type-A, Type-B, Micro-USB, Mini-USB, and 24-pin reversible Type-C.",
            pinoutPins = listOf(
                PinoutPin("1", "VBUS", "Red", 0xFFE53935, "+5V DC Power (Up to 20V with PD)", "+5.0V"),
                PinoutPin("2", "D-", "White", 0xFFECEFF1, "High-Speed USB Data -", "0 to 3.3V"),
                PinoutPin("3", "D+", "Green", 0xFF4CAF50, "High-Speed USB Data +", "0 to 3.3V"),
                PinoutPin("4", "GND", "Black", 0xFF212121, "Ground return", "0V"),
                PinoutPin("CC1/CC2", "Config Channel", "Blue", 0xFF2196F3, "USB-C Cable Orientation & Power Delivery", "0 to 5V"),
                PinoutPin("TX+/TX-", "SuperSpeed TX", "Yellow", 0xFFFFD54F, "USB 3.0 / 3.2 5Gbps+ SuperSpeed Transmit", "Differential")
            )
        ),
        ElectricalCatalogItem(
            id = "pinout_hdmi",
            title = "Pinout HDMI",
            category = CatalogCategory.PINOUT,
            badgeType = BadgeType.CONNECTOR_BADGE,
            badgeText = "HDMI",
            badgeColor = 0xFF212121,
            formulaTitle = "HDMI Type A (19-Pin) Standard Pinout",
            formulaEquation = "TMDS Channels 0, 1, 2 + Clock + DDC + CEC + 5V Power",
            formulaDescription = "High-Definition Multimedia Interface digital audio/video connection pins.",
            pinoutPins = listOf(
                PinoutPin("1", "TMDS Data2+", "Orange", 0xFFFF9800, "Digital Video Red Data +", "TMDS"),
                PinoutPin("2", "TMDS Data2 Shield", "Silver", 0xFFB0BEC5, "Ground Shield", "0V"),
                PinoutPin("3", "TMDS Data2-", "Orange/White", 0xFFFFB74D, "Digital Video Red Data -", "TMDS"),
                PinoutPin("4", "TMDS Data1+", "Green", 0xFF4CAF50, "Digital Video Green Data +", "TMDS"),
                PinoutPin("7", "TMDS Data0+", "Blue", 0xFF2196F3, "Digital Video Blue Data +", "TMDS"),
                PinoutPin("10", "TMDS Clock+", "Brown", 0xFF795548, "Pixel Clock +", "TMDS"),
                PinoutPin("13", "CEC", "Purple", 0xFF9C27B0, "Consumer Electronics Control", "3.3V"),
                PinoutPin("15", "SCL (DDC)", "Yellow", 0xFFFFEB3B, "I2C Display Data Clock", "5V"),
                PinoutPin("16", "SDA (DDC)", "Yellow", 0xFFFFEB3B, "I2C Display Data Line", "5V"),
                PinoutPin("18", "+5V Power", "Red", 0xFFE53935, "Standby 5V Supply (Min 55mA)", "+5.0V"),
                PinoutPin("19", "Hot Plug Detect", "Pink", 0xFFE91E63, "Monitor presence detection", "0V / 5V")
            )
        ),
        ElectricalCatalogItem(
            id = "pinout_vga",
            title = "Pinout VGA",
            category = CatalogCategory.PINOUT,
            badgeType = BadgeType.CONNECTOR_BADGE,
            badgeText = "VGA",
            badgeColor = 0xFF1565C0,
            formulaTitle = "VGA DE-15 (15-Pin) Analog Video Pinout",
            formulaEquation = "RGB Analog Video (75Ω impedance, 0.7V p-p) + H-Sync / V-Sync + DDC",
            formulaDescription = "Standard 15-pin analog display connector.",
            pinoutPins = listOf(
                PinoutPin("1", "RED", "Red", 0xFFE53935, "Red Video (0.7 V p-p, 75Ω)", "0.7V"),
                PinoutPin("2", "GREEN", "Green", 0xFF4CAF50, "Green Video (0.7 V p-p, 75Ω)", "0.7V"),
                PinoutPin("3", "BLUE", "Blue", 0xFF2196F3, "Blue Video (0.7 V p-p, 75Ω)", "0.7V"),
                PinoutPin("5", "GND", "Black", 0xFF212121, "Digital Ground", "0V"),
                PinoutPin("6", "RGND", "Red/Gnd", 0xFFEF9A9A, "Red Video Ground Return", "0V"),
                PinoutPin("7", "GGND", "Green/Gnd", 0xFFA5D6A7, "Green Video Ground Return", "0V"),
                PinoutPin("8", "BGND", "Blue/Gnd", 0xFF90CAF9, "Blue Video Ground Return", "0V"),
                PinoutPin("9", "+5V", "Orange", 0xFFFF9800, "+5V DC for DDC EEPROM", "+5.0V"),
                PinoutPin("13", "HSYNC", "White", 0xFFECEFF1, "Horizontal Sync (TTL)", "TTL (0-5V)"),
                PinoutPin("14", "VSYNC", "Yellow", 0xFFFFEB3B, "Vertical Sync (TTL)", "TTL (0-5V)")
            )
        ),
        ElectricalCatalogItem(
            id = "pinout_rs232",
            title = "Pinout RS-232",
            category = CatalogCategory.PINOUT,
            badgeType = BadgeType.CONNECTOR_BADGE,
            badgeText = "DB9",
            badgeColor = 0xFF616161,
            formulaTitle = "RS-232 Serial Port (DB-9 Female/Male)",
            formulaEquation = "Bipolar Voltage Levels: Logic '1' = -3V to -15V, Logic '0' = +3V to +15V",
            formulaDescription = "Standard asynchronous serial communication port pin assignments.",
            pinoutPins = listOf(
                PinoutPin("1", "DCD", "Black", 0xFF212121, "Data Carrier Detect", "Input"),
                PinoutPin("2", "RXD", "Brown", 0xFF795548, "Receive Data", "Input (±12V)"),
                PinoutPin("3", "TXD", "Red", 0xFFE53935, "Transmit Data", "Output (±12V)"),
                PinoutPin("4", "DTR", "Orange", 0xFFFF9800, "Data Terminal Ready", "Output"),
                PinoutPin("5", "GND", "Green", 0xFF4CAF50, "Signal Ground", "0V"),
                PinoutPin("6", "DSR", "Blue", 0xFF2196F3, "Data Set Ready", "Input"),
                PinoutPin("7", "RTS", "Purple", 0xFF9C27B0, "Request To Send", "Output"),
                PinoutPin("8", "CTS", "Gray", 0xFF9E9E9E, "Clear To Send", "Input"),
                PinoutPin("9", "RI", "White", 0xFFECEFF1, "Ring Indicator", "Input")
            )
        ),
        ElectricalCatalogItem(
            id = "pinout_raspberry_pi",
            title = "Pinout Raspberry Pi",
            category = CatalogCategory.PINOUT,
            badgeType = BadgeType.CONNECTOR_BADGE,
            badgeText = "GPIO",
            badgeColor = 0xFFD81B60,
            formulaTitle = "Raspberry Pi 40-Pin GPIO Header (Pi 3, 4, 5)",
            formulaEquation = "3.3V Logic Level (NEVER apply 5V to GPIO pins directly)",
            formulaDescription = "Standard 40-pin GPIO pin header with I2C, SPI, UART, and PWM channels.",
            pinoutPins = listOf(
                PinoutPin("1", "3V3", "Orange", 0xFFFF9800, "3.3V DC Power (Max 50mA)", "3.3V"),
                PinoutPin("2", "5V", "Red", 0xFFE53935, "5V DC Power (From USB-C)", "5.0V"),
                PinoutPin("3", "GPIO 2", "Blue", 0xFF2196F3, "I2C1 SDA (Data)", "3.3V Logic"),
                PinoutPin("5", "GPIO 3", "Blue", 0xFF2196F3, "I2C1 SCL (Clock)", "3.3V Logic"),
                PinoutPin("6", "GND", "Black", 0xFF212121, "Ground", "0V"),
                PinoutPin("8", "GPIO 14", "Green", 0xFF4CAF50, "UART0 TXD (Serial Transmit)", "3.3V Logic"),
                PinoutPin("10", "GPIO 15", "Green", 0xFF4CAF50, "UART0 RXD (Serial Receive)", "3.3V Logic"),
                PinoutPin("12", "GPIO 18", "Cyan", 0xFF00BCD4, "Hardware PWM 0", "3.3V Logic")
            )
        ),
        ElectricalCatalogItem(
            id = "pinout_obd2",
            title = "Pinout OBD II",
            category = CatalogCategory.PINOUT,
            badgeType = BadgeType.CONNECTOR_BADGE,
            badgeText = "OBD2",
            badgeColor = 0xFF455A64,
            formulaTitle = "SAE J1962 OBD-II Diagnostic Vehicle Port",
            formulaEquation = "CAN Bus: CAN High (Pin 6) + CAN Low (Pin 14) at 500 kbps",
            formulaDescription = "16-pin vehicle onboard diagnostic port found on all modern motor vehicles.",
            pinoutPins = listOf(
                PinoutPin("4", "Chassis Ground", "Black", 0xFF212121, "Vehicle chassis metal ground", "0V"),
                PinoutPin("5", "Signal Ground", "Brown", 0xFF795548, "ECU sensor signal ground", "0V"),
                PinoutPin("6", "CAN High", "Yellow", 0xFFFFEB3B, "ISO 15765-4 High Speed CAN (H)", "2.5V to 3.5V"),
                PinoutPin("7", "K-Line", "Green", 0xFF4CAF50, "ISO 9141-2 / KWP2000 Diagnostic Line", "12V"),
                PinoutPin("14", "CAN Low", "Green/Yel", 0xFF8BC34A, "ISO 15765-4 High Speed CAN (L)", "1.5V to 2.5V"),
                PinoutPin("16", "Battery +12V", "Red", 0xFFE53935, "Permanent unswitched battery power", "+12.0V")
            )
        ),
        ElectricalCatalogItem(
            id = "pinout_xlr",
            title = "Pinout XLR (Audio/DMX)",
            category = CatalogCategory.PINOUT,
            badgeType = BadgeType.CONNECTOR_BADGE,
            badgeText = "XLR",
            badgeColor = 0xFF37474F,
            formulaTitle = "XLR 3-Pin Audio & 5-Pin DMX512 Lighting",
            formulaEquation = "Pin 1: Ground/Shield | Pin 2: Hot (+) | Pin 3: Cold (-)\nPhantom Power: +48V DC between Pins 2/3 and Pin 1",
            formulaDescription = "Balanced professional audio microphone cables and RS-485 DMX512 stage lighting controls.",
            pinoutPins = listOf(
                PinoutPin("1", "Chassis Shield", "Silver", 0xFFB0BEC5, "Cable Shield / Earth Ground", "0V"),
                PinoutPin("2", "Hot (+)", "Red", 0xFFE53935, "Balanced Signal Positive Phase (Audio) / Data + (DMX)", "+48V Phantom"),
                PinoutPin("3", "Cold (-)", "Black", 0xFF212121, "Balanced Signal Inverted Phase (Audio) / Data - (DMX)", "+48V Phantom")
            )
        ),
        ElectricalCatalogItem(
            id = "pinout_sim_card",
            title = "Pinout Sim Card",
            category = CatalogCategory.PINOUT,
            badgeType = BadgeType.CONNECTOR_BADGE,
            badgeText = "SIM",
            badgeColor = 0xFFFBC02D,
            formulaTitle = "ISO/IEC 7816-2 Smart Card & SIM Card Interface",
            formulaEquation = "C1: VCC (1.8V / 3V) | C2: RST | C3: CLK | C5: GND | C6: VPP | C7: I/O",
            formulaDescription = "Standard contact pads for Standard, Micro, and Nano SIM subscriber identity modules.",
            pinoutPins = listOf(
                PinoutPin("C1", "VCC", "Red", 0xFFE53935, "Supply Voltage (1.8V, 3.0V, or 5.0V)", "1.8V / 3.0V"),
                PinoutPin("C2", "RST", "Orange", 0xFFFF9800, "Reset signal from mobile terminal", "Logic Level"),
                PinoutPin("C3", "CLK", "Green", 0xFF4CAF50, "Clock signal (typically 3.25 to 5 MHz)", "Clock"),
                PinoutPin("C5", "GND", "Black", 0xFF212121, "Power and signal ground return", "0V"),
                PinoutPin("C6", "VPP", "Purple", 0xFF9C27B0, "Programming voltage (Obsolete, connected to VCC)", "N/C"),
                PinoutPin("C7", "I/O", "Blue", 0xFF2196F3, "Bidirectional asynchronous serial data line", "Logic Level")
            )
        ),
        ElectricalCatalogItem(
            id = "pinout_lcd_16x2",
            title = "Pinout display LCD 16x2",
            category = CatalogCategory.PINOUT,
            badgeType = BadgeType.CONNECTOR_BADGE,
            badgeText = "LCD",
            badgeColor = 0xFF2E7D32,
            formulaTitle = "Hitachi HD44780 16-Pin Parallel Alphanumeric LCD",
            formulaEquation = "Power (VSS, VDD, V0) + Control (RS, RW, E) + Data Bus (D0-D7) + Backlight (A, K)",
            formulaDescription = "Standard 16-pin interface for 1602 and 2004 character LCD displays.",
            pinoutPins = listOf(
                PinoutPin("1", "VSS", "Black", 0xFF212121, "Ground (0V)", "0V"),
                PinoutPin("2", "VDD", "Red", 0xFFE53935, "+5V Power Supply", "+5.0V"),
                PinoutPin("3", "V0", "Orange", 0xFFFF9800, "Liquid Crystal Contrast Adjust (Potentiometer)", "0 - 5V"),
                PinoutPin("4", "RS", "Yellow", 0xFFFFEB3B, "Register Select: 0=Instruction, 1=Data", "TTL"),
                PinoutPin("5", "R/W", "Green", 0xFF4CAF50, "Read/Write: 0=Write, 1=Read", "TTL"),
                PinoutPin("6", "E", "Blue", 0xFF2196F3, "Enable Strobing Clock", "TTL"),
                PinoutPin("7-14", "D0-D7", "Purple", 0xFF9C27B0, "8-Bit Bidirectional Data Bus (D4-D7 used in 4-bit mode)", "TTL"),
                PinoutPin("15", "LED+", "Red", 0xFFE53935, "Backlight Anode (+5V through resistor)", "+4.2V - 5V"),
                PinoutPin("16", "LED-", "Black", 0xFF212121, "Backlight Cathode (Ground)", "0V")
            )
        )
    )

    // ==========================================
    // 4. FORMULAS SCREEN ITEMS (Reference images 9-14)
    // All items styled with Red Book badge and "f(x)"
    // ==========================================
    val formulaItems: List<ElectricalCatalogItem> = listOf(
        ElectricalCatalogItem(
            id = "formula_first_ohm",
            title = "First Ohm's Law",
            category = CatalogCategory.FORMULAS,
            badgeType = BadgeType.BOOK_FORMULA,
            badgeText = "f(x)",
            badgeColor = 0xFFC62828,
            isLocked = false,
            formulaTitle = "First Ohm's Law (Fundamental Relationship)",
            formulaEquation = "V = I · R\nI = V / R\nR = V / I",
            formulaDescription = "States that current flowing through a conductor between two points is directly proportional to the voltage across the two points and inversely proportional to resistance.",
            formulaVariables = listOf(
                "V" to "Electric potential difference / Voltage in Volts (V)",
                "I" to "Electric current intensity in Amperes (A)",
                "R" to "Electrical resistance in Ohms (Ω)"
            )
        ),
        ElectricalCatalogItem(
            id = "formula_second_ohm",
            title = "Second Ohm's Law",
            category = CatalogCategory.FORMULAS,
            badgeType = BadgeType.BOOK_FORMULA,
            badgeText = "f(x)",
            badgeColor = 0xFFC62828,
            isLocked = false,
            formulaTitle = "Second Ohm's Law (Conductor Physical Resistance)",
            formulaEquation = "R = ρ · (L / S)\nS = ρ · (L / R)\nL = (R · S) / ρ",
            formulaDescription = "Calculates the electrical resistance of a uniform conductor based on its material resistivity, physical length, and cross-sectional area.",
            formulaVariables = listOf(
                "R" to "Conductor resistance in Ohms (Ω)",
                "ρ" to "Specific resistivity of material (Copper: 0.0178 Ω·mm²/m at 20°C, Aluminum: 0.0282)",
                "L" to "Length of conductor in meters (m)",
                "S" to "Cross-sectional area of conductor in square millimeters (mm²)"
            )
        ),
        ElectricalCatalogItem(
            id = "formula_current",
            title = "Current",
            category = CatalogCategory.FORMULAS,
            badgeType = BadgeType.BOOK_FORMULA,
            badgeText = "f(x)",
            badgeColor = 0xFFC62828,
            isLocked = false,
            formulaTitle = "Calculation of Current (DC, 1-Phase, 3-Phase)",
            formulaEquation = "I = P / V  (Direct Current)\nI = P / (V · cosφ)  (Single-Phase AC)\nI = P / (√3 · V_L · cosφ)  (Three-Phase AC)",
            formulaDescription = "Calculates electric current for various circuit topologies under continuous operating conditions.",
            formulaVariables = listOf(
                "I" to "Current (Amperes)",
                "P" to "Active power (Watts)",
                "V" to "Voltage (Volts)",
                "cosφ" to "Power factor"
            )
        ),
        ElectricalCatalogItem(
            id = "formula_power",
            title = "Power",
            category = CatalogCategory.FORMULAS,
            badgeType = BadgeType.BOOK_FORMULA,
            badgeText = "f(x)",
            badgeColor = 0xFFC62828,
            isLocked = false,
            formulaTitle = "Electrical Power Equations",
            formulaEquation = "P = V · I = I² · R = V² / R  (DC)\nP = V · I · cosφ  (1-Phase AC)\nP = √3 · V_L · I_L · cosφ  (3-Phase AC)",
            formulaDescription = "Calculates active power in direct current and alternating current networks.",
            formulaVariables = listOf(
                "P" to "Electric power in Watts (W)",
                "V" to "Voltage (V)",
                "I" to "Current (A)",
                "R" to "Resistance (Ω)",
                "cosφ" to "Power factor"
            )
        ),
        ElectricalCatalogItem(
            id = "formula_voltage",
            title = "Voltage",
            category = CatalogCategory.FORMULAS,
            badgeType = BadgeType.BOOK_FORMULA,
            badgeText = "f(x)",
            badgeColor = 0xFFC62828,
            isLocked = false,
            formulaTitle = "Calculation of Voltage",
            formulaEquation = "V = I · R\nV = P / I  (DC)\nV = P / (I · cosφ)  (1-Phase)\nV_L = P / (√3 · I_L · cosφ)  (3-Phase)",
            formulaDescription = "Formulas for calculating potential difference across electric circuit loads.",
            formulaVariables = listOf(
                "V" to "Voltage in Volts (V)",
                "I" to "Current in Amperes (A)",
                "R" to "Resistance in Ohms (Ω)"
            )
        ),
        ElectricalCatalogItem(
            id = "formula_resistance",
            title = "Resistance",
            category = CatalogCategory.FORMULAS,
            badgeType = BadgeType.BOOK_FORMULA,
            badgeText = "f(x)",
            badgeColor = 0xFFC62828,
            isLocked = false,
            formulaTitle = "Resistance Equations & Temperature Coefficient",
            formulaEquation = "R = V / I = V² / P = P / I²\nR_θ = R_20 · [1 + α · (θ - 20°C)]",
            formulaDescription = "Calculates resistance and temperature compensation coefficient (Copper α = 0.00393 /°C).",
            formulaVariables = listOf(
                "R_θ" to "Resistance at temperature θ (°C)",
                "R_20" to "Resistance at reference 20°C",
                "α" to "Temperature coefficient of resistance"
            )
        ),
        ElectricalCatalogItem(
            id = "formula_impedance",
            title = "Impedance",
            category = CatalogCategory.FORMULAS,
            badgeType = BadgeType.BOOK_FORMULA,
            badgeText = "f(x)",
            badgeColor = 0xFFC62828,
            isLocked = false,
            formulaTitle = "Complex AC Impedance",
            formulaEquation = "Z = √(R² + X²) = √(R² + (X_L - X_C)²)\nZ = V / I",
            formulaDescription = "Total AC circuit opposition combining resistance and reactive components.",
            formulaVariables = listOf(
                "Z" to "Impedance magnitude in Ohms (Ω)",
                "R" to "Resistance in Ohms (Ω)",
                "X_L" to "2 · π · f · L (Inductive reactance in Ω)",
                "X_C" to "1 / (2 · π · f · C) (Capacitive reactance in Ω)"
            )
        ),
        ElectricalCatalogItem(
            id = "formula_active_power",
            title = "Active power",
            category = CatalogCategory.FORMULAS,
            badgeType = BadgeType.BOOK_FORMULA,
            badgeText = "f(x)",
            badgeColor = 0xFFC62828,
            isLocked = false,
            formulaTitle = "Active Power (Watts)",
            formulaEquation = "P = S · cosφ = √(S² - Q²)\nP = √3 · V_L · I_L · cosφ",
            formulaDescription = "Real electrical power that performs actual work in heating, lighting, and mechanical torque.",
            formulaVariables = listOf(
                "P" to "Active power in Watts (W)",
                "S" to "Apparent power in Volt-Amperes (VA)",
                "cosφ" to "System power factor"
            )
        ),
        ElectricalCatalogItem(
            id = "formula_reactive_power",
            title = "Reactive power",
            category = CatalogCategory.FORMULAS,
            badgeType = BadgeType.BOOK_FORMULA,
            badgeText = "f(x)",
            badgeColor = 0xFFC62828,
            isLocked = false,
            formulaTitle = "Reactive Power (var)",
            formulaEquation = "Q = S · sinφ = √(S² - P²) = P · tanφ\nQ = √3 · V_L · I_L · sinφ",
            formulaDescription = "Power oscillating between source and reactive loads (inductors and capacitors).",
            formulaVariables = listOf(
                "Q" to "Reactive power (var)",
                "sinφ" to "√(1 - cos²φ)"
            )
        ),
        ElectricalCatalogItem(
            id = "formula_apparent_power",
            title = "Apparent power",
            category = CatalogCategory.FORMULAS,
            badgeType = BadgeType.BOOK_FORMULA,
            badgeText = "f(x)",
            badgeColor = 0xFFC62828,
            isLocked = false,
            formulaTitle = "Apparent Power (Volt-Amperes)",
            formulaEquation = "S = V · I  (1-Phase)\nS = √3 · V_L · I_L  (3-Phase)\nS = √(P² + Q²)",
            formulaDescription = "Vector sum of active and reactive power, used for generator and transformer sizing.",
            formulaVariables = listOf(
                "S" to "Apparent power (VA)"
            )
        ),
        ElectricalCatalogItem(
            id = "formula_power_factor",
            title = "Power factor",
            category = CatalogCategory.FORMULAS,
            badgeType = BadgeType.BOOK_FORMULA,
            badgeText = "f(x)",
            badgeColor = 0xFFC62828,
            isLocked = false,
            formulaTitle = "Power Factor (cosφ)",
            formulaEquation = "cosφ = P / S = P / √(P² + Q²)\ntanφ = Q / P\nTHD Derating: PF_true = cosφ_displacement / √(1 + THD_i²)",
            formulaDescription = "Ratio of useful active power to total apparent power supplied by utility.",
            formulaVariables = listOf(
                "cosφ" to "Displacement power factor",
                "THD_i" to "Total harmonic current distortion"
            )
        ),
        ElectricalCatalogItem(
            id = "formula_conductor_sizing",
            title = "Conductor sizing",
            category = CatalogCategory.FORMULAS,
            badgeType = BadgeType.BOOK_FORMULA,
            badgeText = "f(x)",
            badgeColor = 0xFFC62828,
            isLocked = false,
            formulaTitle = "Conductor Cross Section Area (S)",
            formulaEquation = "S = (2 · ρ · L · I) / ΔV  (1-Phase)\nS = (√3 · ρ · L · I) / ΔV  (3-Phase)",
            formulaDescription = "Required wire gauge cross-section (mm²) based on maximum permissible voltage drop and load current.",
            formulaVariables = listOf(
                "S" to "Minimum cross section in mm²",
                "ρ" to "Resistivity (Ω·mm²/m)",
                "L" to "One-way line length in meters",
                "I" to "Circuit current in Amperes",
                "ΔV" to "Allowable voltage drop in Volts"
            )
        ),
        ElectricalCatalogItem(
            id = "formula_voltage_drop",
            title = "Voltage drop",
            category = CatalogCategory.FORMULAS,
            badgeType = BadgeType.BOOK_FORMULA,
            badgeText = "f(x)",
            badgeColor = 0xFFC62828,
            isLocked = false,
            formulaTitle = "Exact AC Voltage Drop Formulation",
            formulaEquation = "ΔV = 2 · L · I · (R · cosφ + X · sinφ)  (1-Phase)\nΔV = √3 · L · I · (R · cosφ + X · sinφ)  (3-Phase)",
            formulaDescription = "IEC 60364 voltage drop formulation incorporating conductor resistance and reactance.",
            formulaVariables = listOf(
                "R" to "Cable resistance (Ω/km)",
                "X" to "Cable reactance (Ω/km)"
            )
        ),
        ElectricalCatalogItem(
            id = "formula_pfc",
            title = "Power factor correction",
            category = CatalogCategory.FORMULAS,
            badgeType = BadgeType.BOOK_FORMULA,
            badgeText = "f(x)",
            badgeColor = 0xFFC62828,
            isLocked = false,
            formulaTitle = "Power Factor Correction Capacitor Sizing",
            formulaEquation = "Q_c = P · (tanφ_1 - tanφ_2)\nC (μF) = (Q_c · 10⁹) / (2 · π · f · V²)",
            formulaDescription = "Formula for calculating capacitor bank size (kvar and microfarads) to eliminate utility reactive power penalties.",
            formulaVariables = listOf(
                "Q_c" to "Capacitor reactive power (kvar)",
                "tanφ_1" to "Initial tangent = tan(arccos(PF_initial))",
                "tanφ_2" to "Target tangent = tan(arccos(PF_target))"
            )
        ),
        ElectricalCatalogItem(
            id = "formula_motor_speed",
            title = "Motor speed",
            category = CatalogCategory.FORMULAS,
            badgeType = BadgeType.BOOK_FORMULA,
            badgeText = "f(x)",
            badgeColor = 0xFFC62828,
            isLocked = false,
            formulaTitle = "Synchronous and Full Load Motor Speed",
            formulaEquation = "n_s = (120 · f) / p\nn = n_s · (1 - s)",
            formulaDescription = "Equations connecting electrical frequency, physical poles, and shaft rotational RPM.",
            formulaVariables = listOf(
                "n_s" to "Synchronous speed (RPM)",
                "f" to "Frequency in Hz",
                "p" to "Number of poles (2, 4, 6, 8)"
            )
        ),
        ElectricalCatalogItem(
            id = "formula_motor_torque",
            title = "Power / Maximum torque",
            category = CatalogCategory.FORMULAS,
            badgeType = BadgeType.BOOK_FORMULA,
            badgeText = "f(x)",
            badgeColor = 0xFFC62828,
            isLocked = false,
            formulaTitle = "Torque-Speed Power Equations",
            formulaEquation = "T (N·m) = (9550 · P_kW) / n_rpm\nP_kW = (T · n_rpm) / 9550",
            formulaDescription = "Conversion between mechanical power, shaft rotational speed, and torque output in Newton-meters.",
            formulaVariables = listOf(
                "T" to "Torque in Newton-meters (N·m)",
                "P_kW" to "Power in Kilowatts",
                "n_rpm" to "Rotational speed in RPM"
            )
        ),
        ElectricalCatalogItem(
            id = "formula_delta_star",
            title = "Delta-star conversion",
            category = CatalogCategory.FORMULAS,
            badgeType = BadgeType.BOOK_FORMULA,
            badgeText = "f(x)",
            badgeColor = 0xFFC62828,
            isLocked = false,
            formulaTitle = "Delta to Star (Wye) & Star to Delta Conversion",
            formulaEquation = "R_A = (R_AB · R_CA) / (R_AB + R_BC + R_CA)\nR_AB = R_A + R_B + (R_A · R_B) / R_C",
            formulaDescription = "Kennelly's Delta-Star transformation formulas for 3-terminal resistive networks.",
            formulaVariables = listOf(
                "R_A, R_B, R_C" to "Star (Wye) branch resistances",
                "R_AB, R_BC, R_CA" to "Delta loop resistances"
            )
        )
    )

    // ==========================================
    // 5. CONVERSIONS SCREEN ITEMS (Reference images 7-8)
    // ==========================================
    val conversionItems: List<ElectricalCatalogItem> = listOf(
        ElectricalCatalogItem(
            id = "conv_power",
            title = "Power conversion",
            category = CatalogCategory.CONVERSIONS,
            badgeType = BadgeType.ICON_SYMBOL,
            badgeText = "W",
            badgeColor = 0xFF8E24AA,
            isLocked = false,
            formulaTitle = "Power Unit Conversions",
            formulaEquation = "1 kW = 1000 W = 1.34102 HP\n1 HP (Imperial) = 745.7 W\n1 HP (Metric / PS) = 735.5 W\n1 BTU/h = 0.293071 W",
            formulaDescription = "Converts between Watts, Kilowatts, Horsepower (HP), BTU/h, and kcal/h."
        ),
        ElectricalCatalogItem(
            id = "conv_awg",
            title = "AWG conversion table",
            category = CatalogCategory.CONVERSIONS,
            badgeType = BadgeType.SQUARE_SYMBOL,
            badgeText = "AWG",
            badgeColor = 0xFF0288D1,
            isLocked = false,
            formulaTitle = "American Wire Gauge (AWG) to mm² Formula",
            formulaEquation = "d_n = 0.127 · 92^((36 - n) / 39)  (mm)\nArea (mm²) = (π / 4) · d_n²",
            formulaDescription = "Exact mathematical definition and cross-reference table for AWG wire sizes to metric mm²."
        ),
        ElectricalCatalogItem(
            id = "conv_swg",
            title = "SWG conversion table",
            category = CatalogCategory.CONVERSIONS,
            badgeType = BadgeType.SQUARE_SYMBOL,
            badgeText = "SWG",
            badgeColor = 0xFF00ACC1,
            isLocked = false,
            formulaTitle = "Standard Wire Gauge (British SWG)",
            formulaEquation = "Converts British Standard Wire Gauge (SWG 7/0 to 50) to millimeters and cross-sectional area mm².",
            formulaDescription = "British Imperial wire gauge lookup and conductor comparison."
        ),
        ElectricalCatalogItem(
            id = "conv_wire_size",
            title = "Wire size conversion",
            category = CatalogCategory.CONVERSIONS,
            badgeType = BadgeType.ICON_SYMBOL,
            badgeText = "AWG↔mm²",
            badgeColor = 0xFF795548,
            isLocked = false,
            formulaTitle = "Conductor Size Metric & Imperial Conversion",
            formulaEquation = "1 kcmil (MCM) = 0.5067 mm²\n1 mm² = 1.9735 kcmil",
            formulaDescription = "Converts between AWG, kcmil (MCM), circular mils, and metric square millimeters."
        ),
        ElectricalCatalogItem(
            id = "conv_length",
            title = "Length conversion",
            category = CatalogCategory.CONVERSIONS,
            badgeType = BadgeType.ICON_SYMBOL,
            badgeText = "m↔ft",
            badgeColor = 0xFFFBC02D,
            isLocked = false,
            formulaTitle = "Distance & Cable Length Conversion",
            formulaEquation = "1 meter = 3.28084 feet = 39.3701 inches\n1 foot = 0.3048 meters\n1 yard = 0.9144 meters\n1 mile = 1.60934 km",
            formulaDescription = "Length and conduit run unit converter."
        ),
        ElectricalCatalogItem(
            id = "conv_voltage_rms_peak",
            title = "Voltage conversion (RMS and peak values)",
            category = CatalogCategory.CONVERSIONS,
            badgeType = BadgeType.SCHEMATIC_SYMBOL,
            badgeText = "∿",
            badgeColor = 0xFF42A5F5,
            isLocked = false,
            formulaTitle = "AC Sinusoidal Voltage Conversions",
            formulaEquation = "V_peak = √2 · V_rms ≈ 1.4142 · V_rms\nV_pp = 2 · V_peak = 2.8284 · V_rms\nV_avg = (2 / π) · V_peak ≈ 0.6366 · V_peak = 0.9003 · V_rms",
            formulaDescription = "Converts between root-mean-square (RMS), peak, peak-to-peak, and average voltage for pure AC sine waves."
        ),
        ElectricalCatalogItem(
            id = "conv_energy",
            title = "Energy conversion",
            category = CatalogCategory.CONVERSIONS,
            badgeType = BadgeType.ICON_SYMBOL,
            badgeText = "⚡",
            badgeColor = 0xFFFFB300,
            isLocked = false,
            formulaTitle = "Electrical Energy Conversion",
            formulaEquation = "1 kWh = 3.6 · 10⁶ Joules (3.6 MJ) = 859.845 kcal\n1 Joule = 1 Watt-second = 0.239 calories = 0.0009478 BTU",
            formulaDescription = "Converts between Joules (J), Kilowatt-hours (kWh), calories, and BTUs."
        ),
        ElectricalCatalogItem(
            id = "conv_temperature",
            title = "Temperature conversion",
            category = CatalogCategory.CONVERSIONS,
            badgeType = BadgeType.ICON_SYMBOL,
            badgeText = "°C↔°F",
            badgeColor = 0xFFE53935,
            isLocked = false,
            formulaTitle = "Temperature Scale Formulas",
            formulaEquation = "°F = (°C · 1.8) + 32\n°C = (°F - 32) / 1.8\nK = °C + 273.15",
            formulaDescription = "Converts between Celsius (°C), Fahrenheit (°F), Kelvin (K), and Rankine (°R)."
        ),
        ElectricalCatalogItem(
            id = "conv_ah_kwh",
            title = "Ah - kWh conversion",
            category = CatalogCategory.CONVERSIONS,
            badgeType = BadgeType.ICON_SYMBOL,
            badgeText = "Ah/kWh",
            badgeColor = 0xFFE53935,
            isLocked = false,
            formulaTitle = "Battery Capacity Ah to Energy kWh",
            formulaEquation = "Energy (kWh) = [ Capacity (Ah) · Voltage (V) ] / 1000\nCapacity (Ah) = [ Energy (kWh) · 1000 ] / Voltage (V)",
            formulaDescription = "Converts battery bank amp-hours to total stored kilowatt-hours at battery voltage."
        ),
        ElectricalCatalogItem(
            id = "conv_var_uf",
            title = "var / μF conversion",
            category = CatalogCategory.CONVERSIONS,
            badgeType = BadgeType.ICON_SYMBOL,
            badgeText = "var/μF",
            badgeColor = 0xFF00ACC1,
            isLocked = true,
            formulaTitle = "Reactive Power to Capacitance",
            formulaEquation = "C (μF) = [ Q (var) · 10⁶ ] / (2 · π · f · V²)\nQ (var) = 2 · π · f · C · V² · 10⁻⁶",
            formulaDescription = "Converts capacitor bank rating in reactive volt-amperes (var) directly to capacitance in microfarads (μF)."
        ),
        ElectricalCatalogItem(
            id = "conv_rpm_rads",
            title = "RPM - rad/s - m/s conversion",
            category = CatalogCategory.CONVERSIONS,
            badgeType = BadgeType.ICON_SYMBOL,
            badgeText = "RPM",
            badgeColor = 0xFF1E88E5,
            isLocked = false,
            formulaTitle = "Rotational & Angular Velocity",
            formulaEquation = "ω (rad/s) = (2 · π · RPM) / 60 ≈ RPM · 0.10472\nRPM = [ ω · 60 ] / (2 · π)\nv (m/s) = ω · r = (2 · π · r · RPM) / 60",
            formulaDescription = "Converts revolutions per minute (RPM) to angular speed (rad/s) and linear tangential velocity (m/s)."
        ),
        ElectricalCatalogItem(
            id = "conv_torque",
            title = "Torque conversion",
            category = CatalogCategory.CONVERSIONS,
            badgeType = BadgeType.ICON_SYMBOL,
            badgeText = "Torque",
            badgeColor = 0xFF546E7A,
            isLocked = false,
            formulaTitle = "Mechanical Torque Conversion",
            formulaEquation = "1 N·m = 0.737562 lb·ft = 8.85075 lb·in = 0.101972 kgf·m\n1 lb·ft = 1.355818 N·m",
            formulaDescription = "Converts torque between Newton-meters, pound-feet, pound-inches, and kilogram-force meters."
        )
    )

    fun getItemsForCategory(category: CatalogCategory): List<ElectricalCatalogItem> {
        return when (category) {
            CatalogCategory.MAIN -> mainItems
            CatalogCategory.MOTOR -> motorItems
            CatalogCategory.PINOUT -> pinoutItems
            CatalogCategory.FORMULAS -> formulaItems
            CatalogCategory.CONVERSIONS -> conversionItems
            CatalogCategory.RESOURCES -> mainItems.filter { it.isLocked || it.badgeType == BadgeType.SQUARE_SYMBOL }
            CatalogCategory.FAVORITES -> mainItems.take(5) + motorItems.take(2) + formulaItems.take(3)
        }
    }

    fun findItemById(id: String): ElectricalCatalogItem? {
        return (mainItems + motorItems + pinoutItems + formulaItems + conversionItems).firstOrNull { it.id == id }
    }
}
