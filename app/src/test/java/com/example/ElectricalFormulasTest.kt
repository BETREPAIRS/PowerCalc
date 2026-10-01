package com.example

import com.example.engine.*
import org.junit.Assert.*
import org.junit.Test

class ElectricalFormulasTest {

    @Test
    fun testOhmsLawVoltage() {
        val res = ElectricalFormulas.calculateVoltageFromIR(10.0, 23.0)
        assertTrue(res.isSuccess)
        assertEquals("230.00", res.primaryValue)
        assertEquals("V", res.primaryUnit)
        assertTrue(res.secondaryValues.containsKey("Power (P = I²R)"))
    }

    @Test
    fun testOhmsLawCurrent() {
        val res = ElectricalFormulas.calculateCurrentFromVR(230.0, 46.0)
        assertTrue(res.isSuccess)
        assertEquals("5.00", res.primaryValue)
        assertEquals("A", res.primaryUnit)
    }

    @Test
    fun testOhmsLawResistance() {
        val res = ElectricalFormulas.calculateResistanceFromVI(230.0, 5.0)
        assertTrue(res.isSuccess)
        assertEquals("46.00", res.primaryValue)
        assertEquals("Ω", res.primaryUnit)
    }

    @Test
    fun testThreePhasePower() {
        val res = ElectricalFormulas.calculateACThreePhase(
            vLine = 400.0,
            iLine = 32.0,
            pf = 0.85,
            connection = ThreePhaseConnection.STAR
        )
        assertTrue(res.isSuccess)
        val kw = res.primaryValue.toDouble()
        // sqrt(3) * 400 * 32 * 0.85 / 1000 = ~18.85 kW
        assertEquals(18.847, kw, 0.05)
    }

    @Test
    fun testPowerFactorCorrection() {
        val res = ElectricalFormulas.calculatePFCorrection(
            activePowerKW = 50.0,
            currentPF = 0.75,
            targetPF = 0.95,
            voltage = 400.0,
            frequency = 50.0,
            isThreePhase = true
        )
        assertTrue(res.isSuccess)
        val kvar = res.primaryValue.toDouble()
        // 50 * (tan(acos(0.75)) - tan(acos(0.95))) = 50 * (0.8819 - 0.3287) = ~27.66 kVAR
        assertEquals(27.66, kvar, 0.1)
    }

    @Test
    fun testVoltageDrop() {
        val res = ElectricalFormulas.calculateVoltageDrop(
            voltage = 230.0,
            current = 20.0,
            lengthM = 50.0,
            sizeMm2 = 4.0,
            material = ConductorMaterial.COPPER,
            isThreePhase = false
        )
        assertTrue(res.isSuccess)
        // 2 * 20 * (0.0175 * 50 / 4) * 0.95 = ~8.31 V
        val dropV = res.primaryValue.toDouble()
        assertEquals(8.31, dropV, 0.2)
    }

    @Test
    fun testMotorFullLoadCurrent() {
        val res = ElectricalFormulas.calculateMotorCurrent(
            powerValue = 15.0, // HP
            isHp = true,
            voltage = 400.0,
            pf = 0.85,
            efficiencyPct = 91.0,
            isThreePhase = true
        )
        assertTrue(res.isSuccess)
        val flc = res.primaryValue.toDouble()
        // 15 HP = 11.1855 kW
        // 11185.5 / (sqrt(3) * 400 * 0.91 * 0.85) = ~20.87 A
        assertEquals(20.9, flc, 0.2)
    }

    @Test
    fun testMotorTorque() {
        val res = ElectricalFormulas.calculateMotorTorque(11.0, 1450.0)
        assertTrue(res.isSuccess)
        // 9550 * 11 / 1450 = ~72.44 N.m
        assertEquals(72.4, res.primaryValue.toDouble(), 0.2)
    }

    @Test
    fun testTransformerCurrent() {
        val res = ElectricalFormulas.calculateTransformer(
            kva = 500.0,
            vPrimary = 11000.0,
            vSecondary = 400.0,
            percentZ = 4.5,
            isThreePhase = true
        )
        assertTrue(res.isSuccess)
        // Sec FLC = 500000 / (sqrt(3) * 400) = ~721.7 A
        assertEquals(721.7, res.primaryValue.toDouble(), 0.5)
    }

    @Test
    fun testAwgConversion() {
        val res = ElectricalFormulas.convertAwgToMm2("12")
        assertTrue(res.isSuccess)
        // 12 AWG is approx 3.31 mm²
        assertEquals(3.31, res.primaryValue.toDouble(), 0.05)
    }

    @Test
    fun testPhaseBalancing() {
        val res = ElectricalFormulas.calculatePhaseBalance(12000.0, 11500.0, 8500.0)
        assertTrue(res.isSuccess)
        // Avg = 32000 / 3 = 10666.7
        // Max dev = |8500 - 10666.7| = 2166.7
        // Imbalance = (2166.7 / 10666.7) * 100 = ~20.3%
        val imb = res.primaryValue.toDouble()
        assertEquals(20.3, imb, 0.2)
        assertNotNull(res.warning)
    }

    @Test
    fun testResistorColorBands() {
        // Brown (1), Black (0), Red (x100), Gold (5%) -> 1000 ohm = 1.00 kΩ ±5%
        val res = ElectricalFormulas.decodeResistor4Band(1, 0, 2, 10)
        assertTrue(res.isSuccess)
        assertEquals("1.00 kΩ", res.primaryValue)
    }

    @Test
    fun testCapacitorCode() {
        val res = ElectricalFormulas.decodeCapacitorCode("104")
        assertTrue(res.isSuccess)
        // 10 * 10^4 pF = 100,000 pF = 100 nF = 0.1 µF
        assertEquals("100.0 nF", res.primaryValue)
    }

    @Test
    fun testUnitConverter() {
        val powerCat = UnitConverterEngine.categories.first { it.id == "power" }
        val hpUnit = powerCat.units.first { it.id == "hp_mech" }
        val kwUnit = powerCat.units.first { it.symbol == "kW" }
        val convertedKw = UnitConverterEngine.convert(10.0, hpUnit, kwUnit)
        assertEquals(7.457, convertedKw, 0.01)
    }

    @Test
    fun testWattsToAmpsConversions() {
        // Single Phase: 2300W at 230V, PF 1.0 -> 10.0 A
        val singleAmps = UnitConverterEngine.singlePhaseWattsToAmps(2300.0, 230.0, 1.0)
        assertEquals(10.0, singleAmps, 0.01)

        // Three Phase: 15,000W at 400V line, PF 0.85 -> 15000 / (sqrt(3) * 400 * 0.85) = ~25.47 A
        val threeAmps = UnitConverterEngine.threePhaseWattsToAmps(15000.0, 400.0, 0.85)
        assertEquals(25.47, threeAmps, 0.05)

        // kVA to Amps Single Phase: 10 kVA at 230V -> 10,000 / 230 = 43.48 A
        val kvaSingleAmps = UnitConverterEngine.singlePhaseKvaToAmps(10.0, 230.0)
        assertEquals(43.48, kvaSingleAmps, 0.05)

        // kVA to Amps Three Phase: 100 kVA at 400V -> 100,000 / (sqrt(3) * 400) = 144.34 A
        val kvaThreeAmps = UnitConverterEngine.threePhaseKvaToAmps(100.0, 400.0)
        assertEquals(144.34, kvaThreeAmps, 0.1)
    }

    @Test
    fun testHpToKwConversions() {
        // 20 HP Mechanical -> kW
        val kw = UnitConverterEngine.hpToKw(20.0, UnitConverterEngine.HpType.MECHANICAL)
        assertEquals(14.914, kw, 0.02)

        // 15 kW -> HP
        val hp = UnitConverterEngine.kwToHp(15.0, UnitConverterEngine.HpType.MECHANICAL)
        assertEquals(20.115, hp, 0.02)
    }

    @Test
    fun testKwToKvaAndKvarCapacitor() {
        // 80 kW at 0.8 PF -> 100 kVA, 60 kVAR
        val res = UnitConverterEngine.kwToKva(80.0, 0.8)
        assertEquals(100.0, res.kva, 0.01)
        assertEquals(60.0, res.kvar, 0.01)

        // 50 kVAR at 400V 50Hz -> C = 50,000 / (2 * pi * 50 * 400^2) = ~994.7 µF
        val uf = UnitConverterEngine.kvarToMicrofarads(50.0, 400.0, 50.0)
        assertEquals(994.7, uf, 1.0)
    }

    @Test
    fun testBatteryAndSolarConversions() {
        // Battery Runtime: 100 Ah, 12V = 1200 Wh. 80% DoD, 90% eff = 864 Wh. 144W load -> 6.0 hours
        val bRes = UnitConverterEngine.calculateBatteryRuntime(100.0, 12.0, 144.0, 80.0, 90.0)
        assertEquals(6.0, bRes.runTimeHours, 0.05)

        // C-Rate: 50 Ah at 25A = 0.5C
        val cRate = UnitConverterEngine.calculateCRate(50.0, 25.0)
        assertEquals(0.5, cRate.cRate, 0.01)

        // Solar Voc Temp: STC 48V, -0.3%/°C at -10°C (deltaT = -35°C) -> 48 * (1 + (-0.003 * -35)) = 48 * 1.105 = 53.04 V
        val sVolt = UnitConverterEngine.calculateSolarTempVoltage(48.0, 40.0, -0.3, -10.0, 60.0)
        assertEquals(53.04, sVolt.vocAtMinTemp, 0.05)
    }

    @Test
    fun testElectronicsAndAwgConversions() {
        // 0 dBm = 1.0 mW
        val dbmRes = UnitConverterEngine.dbmToAll(0.0)
        assertEquals(1.0, dbmRes.milliwatts, 0.001)
        assertEquals(0.001, dbmRes.watts, 0.0001)

        // 30 dBm = 1000 mW = 1.0 W, V_RMS (50 ohm) = sqrt(50) = 7.071 V
        val dbm30 = UnitConverterEngine.dbmToAll(30.0)
        assertEquals(1000.0, dbm30.milliwatts, 0.1)
        assertEquals(7.071, dbm30.voltsRms50Ohm, 0.01)

        // Wavelength at 100 MHz: c / 1e8 = 2.998 meters
        val wave = UnitConverterEngine.frequencyToWavelength(100e6)
        assertEquals(2.998, wave.wavelengthMeters, 0.01)

        // AWG mm2 to circular mil: 12 AWG is 3.309 mm2 -> 6530 cmil
        val cmil = UnitConverterEngine.mm2ToCircularMil(3.309)
        assertEquals(6530.4, cmil, 5.0)

        // Temperature: 100°C = 212°F
        val fDeg = UnitConverterEngine.convertTemperature(100.0, "C", "F")
        assertEquals(212.0, fDeg, 0.01)
    }
}
