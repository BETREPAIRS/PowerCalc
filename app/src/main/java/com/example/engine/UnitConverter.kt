package com.example.engine

import java.util.Locale
import kotlin.math.*

data class ConversionUnit(
    val id: String,
    val name: String,
    val symbol: String,
    val toBaseMultiplier: Double
)

data class ConversionCategory(
    val id: String,
    val name: String,
    val group: String,
    val baseUnit: String,
    val units: List<ConversionUnit>
)

object UnitConverterEngine {

    // =========================================================================
    // STANDARD CATEGORIES FOR GENERAL AND ELECTRICAL CONVERSIONS
    // =========================================================================
    val standardCategories: List<ConversionCategory> = listOf(
        // ⚡ ELECTRICAL
        ConversionCategory(
            id = "voltage",
            name = "Voltage",
            group = "ELECTRICAL",
            baseUnit = "V",
            units = listOf(
                ConversionUnit("uv", "Microvolt", "µV", 1e-6),
                ConversionUnit("mv", "Millivolt", "mV", 1e-3),
                ConversionUnit("v", "Volt", "V", 1.0),
                ConversionUnit("kv", "Kilovolt", "kV", 1e3),
                ConversionUnit("mv_mega", "Megavolt", "MV", 1e6)
            )
        ),
        ConversionCategory(
            id = "current",
            name = "Current",
            group = "ELECTRICAL",
            baseUnit = "A",
            units = listOf(
                ConversionUnit("ua", "Microampere", "µA", 1e-6),
                ConversionUnit("ma", "Milliampere", "mA", 1e-3),
                ConversionUnit("a", "Ampere", "A", 1.0),
                ConversionUnit("ka", "Kiloampere", "kA", 1e3)
            )
        ),
        ConversionCategory(
            id = "resistance",
            name = "Resistance",
            group = "ELECTRICAL",
            baseUnit = "Ω",
            units = listOf(
                ConversionUnit("uohm", "Microohm", "µΩ", 1e-6),
                ConversionUnit("mohm", "Milliohm", "mΩ", 1e-3),
                ConversionUnit("ohm", "Ohm", "Ω", 1.0),
                ConversionUnit("kohm", "Kilohm", "kΩ", 1e3),
                ConversionUnit("megohm", "Megaohm", "MΩ", 1e6),
                ConversionUnit("gigaohm", "Gigaohm", "GΩ", 1e9)
            )
        ),
        ConversionCategory(
            id = "power",
            name = "Power",
            group = "ELECTRICAL",
            baseUnit = "W",
            units = listOf(
                ConversionUnit("mw_milli", "Milliwatt", "mW", 1e-3),
                ConversionUnit("w", "Watt", "W", 1.0),
                ConversionUnit("kw", "Kilowatt", "kW", 1e3),
                ConversionUnit("mw", "Megawatt", "MW", 1e6),
                ConversionUnit("gw", "Gigawatt", "GW", 1e9),
                ConversionUnit("hp_mech", "Horsepower (Mech)", "hp(I)", 745.69987),
                ConversionUnit("hp_elec", "Horsepower (Elec)", "hp(E)", 746.0),
                ConversionUnit("hp_metric", "Horsepower (Metric)", "hp(M)/PS", 735.49875),
                ConversionUnit("btu_hr", "BTU per hour", "BTU/h", 0.293071),
                ConversionUnit("ft_lb_s", "Foot-pound/sec", "ft·lb/s", 1.355818)
            )
        ),
        ConversionCategory(
            id = "energy",
            name = "Energy",
            group = "ELECTRICAL",
            baseUnit = "Wh",
            units = listOf(
                ConversionUnit("joule", "Joule", "J", 1.0 / 3600.0),
                ConversionUnit("kj", "Kilojoule", "kJ", 1000.0 / 3600.0),
                ConversionUnit("mj", "Megajoule", "MJ", 1e6 / 3600.0),
                ConversionUnit("wh", "Watt-hour", "Wh", 1.0),
                ConversionUnit("kwh", "Kilowatt-hour", "kWh", 1e3),
                ConversionUnit("mwh", "Megawatt-hour", "MWh", 1e6),
                ConversionUnit("gwh", "Gigawatt-hour", "GWh", 1e9),
                ConversionUnit("btu", "BTU", "BTU", 0.293071),
                ConversionUnit("cal", "Calorie", "cal", 0.0011622),
                ConversionUnit("kcal", "Kilocalorie", "kcal", 1.16222)
            )
        ),
        ConversionCategory(
            id = "frequency",
            name = "Frequency",
            group = "ELECTRICAL",
            baseUnit = "Hz",
            units = listOf(
                ConversionUnit("mhz_milli", "Millihertz", "mHz", 1e-3),
                ConversionUnit("hz", "Hertz", "Hz", 1.0),
                ConversionUnit("khz", "Kilohertz", "kHz", 1e3),
                ConversionUnit("mhz", "Megahertz", "MHz", 1e6),
                ConversionUnit("ghz", "Gigahertz", "GHz", 1e9),
                ConversionUnit("rpm", "Revolutions / min", "RPM", 1.0 / 60.0),
                ConversionUnit("rad_s", "Radians / second", "rad/s", 1.0 / (2.0 * Math.PI))
            )
        ),
        ConversionCategory(
            id = "capacitance",
            name = "Capacitance",
            group = "ELECTRICAL",
            baseUnit = "F",
            units = listOf(
                ConversionUnit("pf", "Picofarad", "pF", 1e-12),
                ConversionUnit("nf", "Nanofarad", "nF", 1e-9),
                ConversionUnit("uf", "Microfarad", "µF", 1e-6),
                ConversionUnit("mf", "Millifarad", "mF", 1e-3),
                ConversionUnit("f", "Farad", "F", 1.0),
                ConversionUnit("kf", "Kilofarad (Supercap)", "kF", 1e3)
            )
        ),
        ConversionCategory(
            id = "inductance",
            name = "Inductance",
            group = "ELECTRICAL",
            baseUnit = "H",
            units = listOf(
                ConversionUnit("nh", "Nanohenry", "nH", 1e-9),
                ConversionUnit("uh", "Microhenry", "µH", 1e-6),
                ConversionUnit("mh", "Millihenry", "mH", 1e-3),
                ConversionUnit("h", "Henry", "H", 1.0)
            )
        ),
        ConversionCategory(
            id = "conductance",
            name = "Conductance",
            group = "ELECTRICAL",
            baseUnit = "S",
            units = listOf(
                ConversionUnit("us", "Microsiemens", "µS", 1e-6),
                ConversionUnit("ms", "Millisiemens", "mS", 1e-3),
                ConversionUnit("s", "Siemens", "S", 1.0),
                ConversionUnit("mho", "Mho", "℧", 1.0)
            )
        ),

        // 🔧 CABLE
        ConversionCategory(
            id = "cable_resistance",
            name = "Cable Resistance per Length",
            group = "CABLE",
            baseUnit = "Ω/km",
            units = listOf(
                ConversionUnit("ohm_km", "Ohm / kilometer", "Ω/km", 1.0),
                ConversionUnit("mohm_m", "Milliohm / meter", "mΩ/m", 1.0),
                ConversionUnit("ohm_1000ft", "Ohm / 1,000 feet", "Ω/kft", 3.28084),
                ConversionUnit("ohm_mile", "Ohm / mile", "Ω/mi", 0.621371),
                ConversionUnit("ohm_100m", "Ohm / 100 meters", "Ω/100m", 10.0)
            )
        ),
        ConversionCategory(
            id = "area_cable",
            name = "Cable Cross-Section Area",
            group = "CABLE",
            baseUnit = "mm²",
            units = listOf(
                ConversionUnit("mm2", "Square Millimeter", "mm²", 1.0),
                ConversionUnit("cmil", "Circular Mil", "cmil", 0.000506707),
                ConversionUnit("kcmil", "kcmil (MCM)", "kcmil", 0.506707),
                ConversionUnit("sq_inch", "Square Inch", "sq in", 645.16),
                ConversionUnit("sq_mil", "Square Mil", "sq mil", 0.00064516)
            )
        ),

        // 🌡️ GENERAL
        ConversionCategory(
            id = "length",
            name = "Length & Distance",
            group = "GENERAL",
            baseUnit = "m",
            units = listOf(
                ConversionUnit("um", "Micrometer", "µm", 1e-6),
                ConversionUnit("mm", "Millimeter", "mm", 1e-3),
                ConversionUnit("cm", "Centimeter", "cm", 1e-2),
                ConversionUnit("m", "Meter", "m", 1.0),
                ConversionUnit("km", "Kilometer", "km", 1e3),
                ConversionUnit("in", "Inch", "in", 0.0254),
                ConversionUnit("ft", "Foot", "ft", 0.3048),
                ConversionUnit("yd", "Yard", "yd", 0.9144),
                ConversionUnit("mi", "Mile", "mi", 1609.344),
                ConversionUnit("nm_sea", "Nautical Mile", "NM", 1852.0)
            )
        ),
        ConversionCategory(
            id = "area_gen",
            name = "Area",
            group = "GENERAL",
            baseUnit = "m²",
            units = listOf(
                ConversionUnit("mm2", "Square Millimeter", "mm²", 1e-6),
                ConversionUnit("cm2", "Square Centimeter", "cm²", 1e-4),
                ConversionUnit("m2", "Square Meter", "m²", 1.0),
                ConversionUnit("ha", "Hectare", "ha", 1e4),
                ConversionUnit("sq_ft", "Square Foot", "sq ft", 0.092903),
                ConversionUnit("sq_in", "Square Inch", "sq in", 0.00064516),
                ConversionUnit("sq_yd", "Square Yard", "sq yd", 0.836127),
                ConversionUnit("acre", "Acre", "acre", 4046.86)
            )
        ),
        ConversionCategory(
            id = "volume",
            name = "Volume",
            group = "GENERAL",
            baseUnit = "L",
            units = listOf(
                ConversionUnit("ml", "Milliliter", "mL", 1e-3),
                ConversionUnit("l", "Liter", "L", 1.0),
                ConversionUnit("m3", "Cubic Meter", "m³", 1000.0),
                ConversionUnit("gal_us", "US Gallon", "gal (US)", 3.78541),
                ConversionUnit("gal_uk", "UK Imperial Gallon", "gal (UK)", 4.54609),
                ConversionUnit("cu_ft", "Cubic Foot", "cu ft", 28.3168),
                ConversionUnit("cu_in", "Cubic Inch", "cu in", 0.016387)
            )
        ),
        ConversionCategory(
            id = "mass",
            name = "Mass & Weight",
            group = "GENERAL",
            baseUnit = "kg",
            units = listOf(
                ConversionUnit("mg", "Milligram", "mg", 1e-6),
                ConversionUnit("g", "Gram", "g", 1e-3),
                ConversionUnit("kg", "Kilogram", "kg", 1.0),
                ConversionUnit("ton_metric", "Metric Ton", "t", 1e3),
                ConversionUnit("lb", "Pound", "lb", 0.453592),
                ConversionUnit("oz", "Ounce", "oz", 0.0283495),
                ConversionUnit("ton_us", "Short Ton (US)", "ton (US)", 907.185)
            )
        ),
        ConversionCategory(
            id = "pressure",
            name = "Pressure",
            group = "GENERAL",
            baseUnit = "kPa",
            units = listOf(
                ConversionUnit("pa", "Pascal", "Pa", 1e-3),
                ConversionUnit("kpa", "Kilopascal", "kPa", 1.0),
                ConversionUnit("mpa", "Megapascal", "MPa", 1e3),
                ConversionUnit("bar", "Bar", "bar", 100.0),
                ConversionUnit("mbar", "Millibar", "mbar", 0.1),
                ConversionUnit("psi", "Pounds / sq inch", "PSI", 6.89476),
                ConversionUnit("atm", "Standard Atmosphere", "atm", 101.325),
                ConversionUnit("mmhg", "Torr / mmHg", "mmHg", 0.133322)
            )
        ),
        ConversionCategory(
            id = "time",
            name = "Time",
            group = "GENERAL",
            baseUnit = "s",
            units = listOf(
                ConversionUnit("us", "Microsecond", "µs", 1e-6),
                ConversionUnit("ms", "Millisecond", "ms", 1e-3),
                ConversionUnit("s", "Second", "s", 1.0),
                ConversionUnit("min", "Minute", "min", 60.0),
                ConversionUnit("hr", "Hour", "h", 3600.0),
                ConversionUnit("day", "Day", "d", 86400.0)
            )
        ),
        ConversionCategory(
            id = "irradiance",
            name = "Solar Irradiance",
            group = "SOLAR",
            baseUnit = "W/m²",
            units = listOf(
                ConversionUnit("w_m2", "Watt / m²", "W/m²", 1.0),
                ConversionUnit("kw_m2", "Kilowatt / m²", "kW/m²", 1000.0),
                ConversionUnit("btu_ft2_hr", "BTU / ft² / hr", "BTU/ft²·h", 3.15459),
                ConversionUnit("langley_min", "Langley / min", "cal/cm²·min", 697.8)
            )
        )
    )

    // Legacy categories list reference for backward compatibility
    val categories: List<ConversionCategory> = standardCategories

    fun convert(value: Double, fromUnit: ConversionUnit, toUnit: ConversionUnit): Double {
        val baseValue = value * fromUnit.toBaseMultiplier
        return baseValue / toUnit.toBaseMultiplier
    }

    fun convertTemperature(value: Double, fromUnit: String, toUnit: String): Double {
        val celsius = when (fromUnit) {
            "C" -> value
            "F" -> (value - 32.0) * (5.0 / 9.0)
            "K" -> value - 273.15
            "R" -> (value - 491.67) * (5.0 / 9.0)
            else -> value
        }
        return when (toUnit) {
            "C" -> celsius
            "F" -> (celsius * (9.0 / 5.0)) + 32.0
            "K" -> celsius + 273.15
            "R" -> (celsius + 273.15) * (9.0 / 5.0)
            else -> celsius
        }
    }

    // =========================================================================
    // 🔌 POWER SYSTEM UTILITIES
    // =========================================================================

    enum class HpType(val label: String, val wattsPerHp: Double) {
        MECHANICAL("Mechanical / Imperial (745.7 W)", 745.69987),
        ELECTRICAL("Electrical (746.0 W)", 746.0),
        METRIC("Metric / PS / CV (735.5 W)", 735.49875)
    }

    fun kwToHp(kw: Double, type: HpType = HpType.MECHANICAL): Double {
        return (kw * 1000.0) / type.wattsPerHp
    }

    fun hpToKw(hp: Double, type: HpType = HpType.MECHANICAL): Double {
        return (hp * type.wattsPerHp) / 1000.0
    }

    data class KwKvaResult(val kw: Double, val kva: Double, val kvar: Double, val pf: Double)

    fun kwToKva(kw: Double, pf: Double): KwKvaResult {
        val safePf = pf.coerceIn(0.01, 1.0)
        val kva = kw / safePf
        val kvar = sqrt(max(0.0, kva * kva - kw * kw))
        return KwKvaResult(kw = kw, kva = kva, kvar = kvar, pf = safePf)
    }

    fun kvaToKw(kva: Double, pf: Double): KwKvaResult {
        val safePf = pf.coerceIn(0.01, 1.0)
        val kw = kva * safePf
        val kvar = sqrt(max(0.0, kva * kva - kw * kw))
        return KwKvaResult(kw = kw, kva = kva, kvar = kvar, pf = safePf)
    }

    data class PfAngleResult(val pf: Double, val angleDeg: Double, val angleRad: Double, val sinPhi: Double, val tanPhi: Double)

    fun pfToAngle(pf: Double): PfAngleResult {
        val safePf = pf.coerceIn(0.0, 1.0)
        val rad = acos(safePf)
        val deg = Math.toDegrees(rad)
        val sinP = sin(rad)
        val tanP = if (safePf > 0.0001) sinP / safePf else Double.POSITIVE_INFINITY
        return PfAngleResult(pf = safePf, angleDeg = deg, angleRad = rad, sinPhi = sinP, tanPhi = tanP)
    }

    fun angleToPf(deg: Double): PfAngleResult {
        val rad = Math.toRadians(deg)
        val pf = cos(rad).coerceIn(0.0, 1.0)
        val sinP = sin(rad)
        val tanP = if (pf > 0.0001) sinP / pf else Double.POSITIVE_INFINITY
        return PfAngleResult(pf = pf, angleDeg = deg, angleRad = rad, sinPhi = sinP, tanPhi = tanP)
    }

    fun kvarToMicrofarads(kvar: Double, voltageVolts: Double, freqHz: Double = 50.0): Double {
        if (voltageVolts <= 0.0 || freqHz <= 0.0) return 0.0
        // C = Q / (2 * pi * f * V^2)
        val varReactive = kvar * 1000.0
        val cFarad = varReactive / (2.0 * Math.PI * freqHz * voltageVolts * voltageVolts)
        return cFarad * 1e6 // microfarads
    }

    fun microfaradsToKvar(microfarads: Double, voltageVolts: Double, freqHz: Double = 50.0): Double {
        if (voltageVolts <= 0.0 || freqHz <= 0.0) return 0.0
        val cFarad = microfarads * 1e-6
        val varReactive = 2.0 * Math.PI * freqHz * cFarad * voltageVolts * voltageVolts
        return varReactive / 1000.0
    }

    // Single phase conversions
    fun singlePhaseWattsToAmps(watts: Double, voltage: Double, pf: Double = 1.0): Double {
        val safePf = pf.coerceIn(0.1, 1.0)
        return if (voltage > 0) watts / (voltage * safePf) else 0.0
    }

    fun singlePhaseAmpsToWatts(amps: Double, voltage: Double, pf: Double = 1.0): Double {
        val safePf = pf.coerceIn(0.1, 1.0)
        return voltage * amps * safePf
    }

    fun singlePhaseKvaToAmps(kva: Double, voltage: Double): Double {
        return if (voltage > 0) (kva * 1000.0) / voltage else 0.0
    }

    fun singlePhaseAmpsToKva(amps: Double, voltage: Double): Double {
        return (voltage * amps) / 1000.0
    }

    // Three phase conversions
    fun threePhaseWattsToAmps(watts: Double, voltageLine: Double, pf: Double = 0.85): Double {
        val safePf = pf.coerceIn(0.1, 1.0)
        val denom = sqrt(3.0) * voltageLine * safePf
        return if (denom > 0) watts / denom else 0.0
    }

    fun threePhaseAmpsToWatts(amps: Double, voltageLine: Double, pf: Double = 0.85): Double {
        val safePf = pf.coerceIn(0.1, 1.0)
        return sqrt(3.0) * voltageLine * amps * safePf
    }

    fun threePhaseKvaToAmps(kva: Double, voltageLine: Double): Double {
        val denom = sqrt(3.0) * voltageLine
        return if (denom > 0) (kva * 1000.0) / denom else 0.0
    }

    fun threePhaseAmpsToKva(amps: Double, voltageLine: Double): Double {
        return (sqrt(3.0) * voltageLine * amps) / 1000.0
    }

    // =========================================================================
    // 🔋 BATTERY ENGINEERING UTILITIES
    // =========================================================================

    data class BatteryRuntimeResult(
        val totalCapacityWh: Double,
        val usableEnergyWh: Double,
        val runTimeHours: Double,
        val runTimeString: String,
        val loadAmps: Double
    )

    fun calculateBatteryRuntime(
        capacityAh: Double,
        voltage: Double,
        loadWatts: Double,
        depthOfDischargePercent: Double = 80.0,
        efficiencyPercent: Double = 90.0
    ): BatteryRuntimeResult {
        val totalWh = capacityAh * voltage
        val dod = depthOfDischargePercent.coerceIn(10.0, 100.0) / 100.0
        val eff = efficiencyPercent.coerceIn(10.0, 100.0) / 100.0
        val usableWh = totalWh * dod * eff
        val runHours = if (loadWatts > 0.0) usableWh / loadWatts else 0.0
        val loadAmps = if (voltage > 0.0) loadWatts / voltage else 0.0

        val wholeHours = runHours.toInt()
        val minutes = ((runHours - wholeHours) * 60.0).roundToInt()
        val formattedTime = if (wholeHours > 0) "${wholeHours}h ${minutes}m" else "${minutes}m"

        return BatteryRuntimeResult(
            totalCapacityWh = totalWh,
            usableEnergyWh = usableWh,
            runTimeHours = runHours,
            runTimeString = formattedTime,
            loadAmps = loadAmps
        )
    }

    data class CRateResult(val cRate: Double, val currentAmps: Double, val nominalChargeTimeHours: Double)

    fun calculateCRate(capacityAh: Double, currentAmps: Double): CRateResult {
        val cRate = if (capacityAh > 0.0) currentAmps / capacityAh else 0.0
        val timeHrs = if (cRate > 0.0) 1.0 / cRate else 0.0
        return CRateResult(cRate = cRate, currentAmps = currentAmps, nominalChargeTimeHours = timeHrs)
    }

    data class BatteryPackResult(
        val packVoltage: Double,
        val packCapacityAh: Double,
        val packEnergyWh: Double,
        val totalCells: Int
    )

    fun calculateBatteryPack(
        seriesCells: Int,
        parallelStrings: Int,
        cellVoltage: Double,
        cellCapacityAh: Double
    ): BatteryPackResult {
        val packV = seriesCells * cellVoltage
        val packAh = parallelStrings * cellCapacityAh
        val packWh = packV * packAh
        return BatteryPackResult(
            packVoltage = packV,
            packCapacityAh = packAh,
            packEnergyWh = packWh,
            totalCells = seriesCells * parallelStrings
        )
    }

    // =========================================================================
    // ☀️ SOLAR PV UTILITIES
    // =========================================================================

    data class SolarTempVoltageResult(val vocAtMinTemp: Double, val vmpAtMaxTemp: Double, val deltaVoc: Double)

    fun calculateSolarTempVoltage(
        vocStc: Double,
        vmpStc: Double,
        tempCoeffVocPercentPerC: Double, // e.g. -0.28 %/°C
        minAmbientTempC: Double,
        maxAmbientTempC: Double
    ): SolarTempVoltageResult {
        val deltaTCold = minAmbientTempC - 25.0
        val deltaTHot = maxAmbientTempC - 25.0
        val coeffDec = tempCoeffVocPercentPerC / 100.0

        val maxVocCold = vocStc * (1.0 + (coeffDec * deltaTCold))
        val minVmpHot = vmpStc * (1.0 + (coeffDec * deltaTHot))

        return SolarTempVoltageResult(
            vocAtMinTemp = maxVocCold,
            vmpAtMaxTemp = minVmpHot,
            deltaVoc = maxVocCold - vocStc
        )
    }

    data class SolarTempCurrentResult(val iscAtMaxTemp: Double, val necSafeAmpacity: Double)

    fun calculateSolarTempCurrent(
        iscStc: Double,
        tempCoeffIscPercentPerC: Double, // e.g. +0.05 %/°C
        maxAmbientTempC: Double
    ): SolarTempCurrentResult {
        val deltaTHot = maxAmbientTempC - 25.0
        val coeffDec = tempCoeffIscPercentPerC / 100.0
        val maxIsc = iscStc * (1.0 + (coeffDec * deltaTHot))
        val necAmpacity = maxIsc * 1.25 * 1.25 // Continuous load + irradiance enhancement factor

        return SolarTempCurrentResult(iscAtMaxTemp = maxIsc, necSafeAmpacity = necAmpacity)
    }

    data class SolarArrayResult(
        val stringVoc: Double,
        val stringVmp: Double,
        val arrayIsc: Double,
        val arrayImp: Double,
        val totalPeakKw: Double,
        val totalModules: Int
    )

    fun calculateSolarArray(
        modulesPerString: Int,
        parallelStrings: Int,
        panelWp: Double,
        panelVoc: Double,
        panelVmp: Double,
        panelIsc: Double,
        panelImp: Double
    ): SolarArrayResult {
        val strVoc = modulesPerString * panelVoc
        val strVmp = modulesPerString * panelVmp
        val arrIsc = parallelStrings * panelIsc
        val arrImp = parallelStrings * panelImp
        val totKw = (modulesPerString * parallelStrings * panelWp) / 1000.0

        return SolarArrayResult(
            stringVoc = strVoc,
            stringVmp = strVmp,
            arrayIsc = arrIsc,
            arrayImp = arrImp,
            totalPeakKw = totKw,
            totalModules = modulesPerString * parallelStrings
        )
    }

    // =========================================================================
    // 🔧 CABLE CONVERSIONS (AWG ↔ mm² ↔ Circular Mil)
    // =========================================================================

    data class AwgDetail(
        val gaugeName: String,
        val gaugeNum: Int,
        val diameterMm: Double,
        val areaMm2: Double,
        val circularMils: Double,
        val copperResistanceOhmPerKm: Double
    )

    private val awgList: List<AwgDetail> = listOf(
        AwgDetail("4/0 (0000)", -3, 11.684, 107.22, 211600.0, 0.161),
        AwgDetail("3/0 (000)", -2, 10.404, 85.01, 167800.0, 0.203),
        AwgDetail("2/0 (00)", -1, 9.266, 67.43, 133100.0, 0.256),
        AwgDetail("1/0 (0)", 0, 8.252, 53.49, 105600.0, 0.323),
        AwgDetail("1 AWG", 1, 7.348, 42.41, 83690.0, 0.407),
        AwgDetail("2 AWG", 2, 6.544, 33.62, 66360.0, 0.513),
        AwgDetail("3 AWG", 3, 5.827, 26.67, 52620.0, 0.647),
        AwgDetail("4 AWG", 4, 5.189, 21.15, 41740.0, 0.815),
        AwgDetail("6 AWG", 6, 4.115, 13.30, 26240.0, 1.296),
        AwgDetail("8 AWG", 8, 3.264, 8.367, 16510.0, 2.061),
        AwgDetail("10 AWG", 10, 2.588, 5.261, 10380.0, 3.277),
        AwgDetail("12 AWG", 12, 2.053, 3.309, 6530.0, 5.211),
        AwgDetail("14 AWG", 14, 1.628, 2.081, 4110.0, 8.286),
        AwgDetail("16 AWG", 16, 1.291, 1.309, 2580.0, 13.17),
        AwgDetail("18 AWG", 18, 1.024, 0.823, 1620.0, 20.95),
        AwgDetail("20 AWG", 20, 0.812, 0.518, 1020.0, 33.31),
        AwgDetail("22 AWG", 22, 0.644, 0.326, 640.0, 52.96),
        AwgDetail("24 AWG", 24, 0.511, 0.205, 404.0, 84.22),
        AwgDetail("26 AWG", 26, 0.405, 0.129, 253.0, 133.9),
        AwgDetail("28 AWG", 28, 0.321, 0.081, 159.0, 212.9),
        AwgDetail("30 AWG", 30, 0.255, 0.051, 100.0, 338.6)
    )

    fun getAllAwgDetails(): List<AwgDetail> = awgList

    fun findClosestAwg(targetMm2: Double): AwgDetail {
        return awgList.minByOrNull { abs(it.areaMm2 - targetMm2) } ?: awgList[11] // default 12 AWG
    }

    fun mm2ToCircularMil(mm2: Double): Double = mm2 / 0.000506707479

    fun circularMilToMm2(cmil: Double): Double = cmil * 0.000506707479

    // =========================================================================
    // 📡 ELECTRONICS & RF (dB, dBm, dBW, dBV, Wavelength)
    // =========================================================================

    data class DecibelPowerResult(val powerRatio: Double, val db: Double)
    data class DecibelVoltageResult(val voltageRatio: Double, val db: Double)

    fun powerRatioToDb(p2: Double, p1: Double): Double {
        return if (p1 > 0.0 && p2 > 0.0) 10.0 * log10(p2 / p1) else 0.0
    }

    fun dbToPowerRatio(db: Double): Double {
        return 10.0.pow(db / 10.0)
    }

    fun voltageRatioToDb(v2: Double, v1: Double): Double {
        return if (v1 > 0.0 && v2 > 0.0) 20.0 * log10(v2 / v1) else 0.0
    }

    fun dbToVoltageRatio(db: Double): Double {
        return 10.0.pow(db / 20.0)
    }

    data class DbmConversionResult(
        val dbm: Double,
        val milliwatts: Double,
        val watts: Double,
        val voltsRms50Ohm: Double,
        val voltsPeakToPeak50Ohm: Double
    )

    fun dbmToAll(dbm: Double): DbmConversionResult {
        val mw = 10.0.pow(dbm / 10.0)
        val w = mw / 1000.0
        val vRms = sqrt(max(0.0, w * 50.0))
        val vPp = vRms * 2.0 * sqrt(2.0)
        return DbmConversionResult(
            dbm = dbm,
            milliwatts = mw,
            watts = w,
            voltsRms50Ohm = vRms,
            voltsPeakToPeak50Ohm = vPp
        )
    }

    fun milliwattsToDbm(mw: Double): Double {
        return if (mw > 0.0) 10.0 * log10(mw) else Double.NEGATIVE_INFINITY
    }

    fun dbwToWatts(dbw: Double): Double = 10.0.pow(dbw / 10.0)
    fun wattsToDbw(watts: Double): Double = if (watts > 0.0) 10.0 * log10(watts) else Double.NEGATIVE_INFINITY

    fun dbvToVolts(dbv: Double): Double = 10.0.pow(dbv / 20.0)
    fun voltsToDbv(volts: Double): Double = if (volts > 0.0) 20.0 * log10(volts) else Double.NEGATIVE_INFINITY

    data class WavelengthResult(
        val frequencyHz: Double,
        val wavelengthMeters: Double,
        val halfWaveDipoleMeters: Double,
        val quarterWaveWhipMeters: Double,
        val wavelengthFeet: Double
    )

    fun frequencyToWavelength(freqHz: Double): WavelengthResult {
        val c = 299792458.0 // Speed of light m/s
        val lambda = if (freqHz > 0.0) c / freqHz else 0.0
        val half = lambda * 0.5 * 0.95 // Velocity factor ~0.95 for copper antenna
        val quarter = lambda * 0.25 * 0.95
        val feet = lambda * 3.28084
        return WavelengthResult(
            frequencyHz = freqHz,
            wavelengthMeters = lambda,
            halfWaveDipoleMeters = half,
            quarterWaveWhipMeters = quarter,
            wavelengthFeet = feet
        )
    }

    // =========================================================================
    // ⚡ COMPLEX IMPEDANCE (Rectangular ↔ Polar, Reactance)
    // =========================================================================

    data class ComplexPolar(val magnitude: Double, val angleDegrees: Double)
    data class ComplexRectangular(val realR: Double, val imagX: Double)

    fun rectangularToPolar(r: Double, x: Double): ComplexPolar {
        val mag = hypot(r, x)
        val deg = Math.toDegrees(atan2(x, r))
        return ComplexPolar(magnitude = mag, angleDegrees = deg)
    }

    fun polarToRectangular(mag: Double, deg: Double): ComplexRectangular {
        val rad = Math.toRadians(deg)
        return ComplexRectangular(realR = mag * cos(rad), imagX = mag * sin(rad))
    }

    data class ReactanceResult(val xlOhm: Double, val xcOhm: Double, val netX: Double, val resonantFreqHz: Double)

    fun calculateReactance(lHenry: Double, cFarad: Double, freqHz: Double): ReactanceResult {
        val xl = if (freqHz > 0 && lHenry > 0) 2.0 * Math.PI * freqHz * lHenry else 0.0
        val xc = if (freqHz > 0 && cFarad > 0) 1.0 / (2.0 * Math.PI * freqHz * cFarad) else 0.0
        val net = xl - xc
        val f0 = if (lHenry > 0 && cFarad > 0) 1.0 / (2.0 * Math.PI * sqrt(lHenry * cFarad)) else 0.0
        return ReactanceResult(xlOhm = xl, xcOhm = xc, netX = net, resonantFreqHz = f0)
    }
}
