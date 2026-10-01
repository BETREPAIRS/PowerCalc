package com.example.ui

import android.app.Application
import android.content.Context
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.engine.*
import com.example.reference.ReferenceData
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class MainNavTab(val title: String) {
    HOME("Home"),
    CALCULATORS("Calculators"),
    TOOLS("Tools"),
    SETTINGS("Settings")
}

data class CalculatorMeta(
    val id: String,
    val title: String,
    val category: String,
    val shortDescription: String,
    val formulaSymbol: String
)

class ElectricianViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ElectricianRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = ElectricianRepository(db)
    }

    // State flows
    val allCalculations: StateFlow<List<CalculationRecord>> = repository.allCalculations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteCalculations: StateFlow<List<CalculationRecord>> = repository.favoriteCalculations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProjects: StateFlow<List<Project>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Navigation State
    private val prefs = application.getSharedPreferences("powercalc_prefs", Context.MODE_PRIVATE)

    // Startup tab strictly defaults to MainNavTab.HOME (the official Electrical Calculations layout)
    private val _currentTab = MutableStateFlow(MainNavTab.HOME)
    val currentTab: StateFlow<MainNavTab> = _currentTab.asStateFlow()

    init {
        // Ensure official layout is active
        if (prefs.contains("startup_layout")) {
            prefs.edit().remove("startup_layout").apply()
        }
    }

    private val _activeCalculatorId = MutableStateFlow<String?>(null)
    val activeCalculatorId: StateFlow<String?> = _activeCalculatorId.asStateFlow()

    private val _selectedCatalogItemId = MutableStateFlow<String?>(null)
    val selectedCatalogItemId: StateFlow<String?> = _selectedCatalogItemId.asStateFlow()

    fun setSelectedCatalogItemId(id: String?) {
        _selectedCatalogItemId.value = id
    }

    private val _selectedProjectId = MutableStateFlow<Long?>(null)
    val selectedProjectId: StateFlow<Long?> = _selectedProjectId.asStateFlow()

    private val _selectedToolTab = MutableStateFlow("converter")
    val selectedToolTab: StateFlow<String> = _selectedToolTab.asStateFlow()

    fun setSelectedToolTab(tab: String) {
        _selectedToolTab.value = tab
    }

    fun openConverter() {
        _selectedToolTab.value = "converter"
        setTab(MainNavTab.TOOLS)
    }

    fun openReference() {
        _selectedToolTab.value = "reference"
        setTab(MainNavTab.TOOLS)
    }

    fun openManuals() {
        _selectedToolTab.value = "manuals"
        setTab(MainNavTab.TOOLS)
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow("All")
    val selectedCategoryFilter: StateFlow<String> = _selectedCategoryFilter.asStateFlow()

    // Settings
    private val _defaultVoltage = MutableStateFlow(220.0)
    val defaultVoltage: StateFlow<Double> = _defaultVoltage.asStateFlow()

    private val _defaultFrequency = MutableStateFlow(50.0)
    val defaultFrequency: StateFlow<Double> = _defaultFrequency.asStateFlow()

    private val _defaultStandard = MutableStateFlow("IEC")
    val defaultStandard: StateFlow<String> = _defaultStandard.asStateFlow()

    private val _darkTheme = MutableStateFlow(false)
    val darkTheme: StateFlow<Boolean> = _darkTheme.asStateFlow()

    private val _isFlashlightOn = MutableStateFlow(false)
    val isFlashlightOn: StateFlow<Boolean> = _isFlashlightOn.asStateFlow()

    // Calculator Catalog
    val calculatorsList: List<CalculatorMeta> = listOf(
        CalculatorMeta("cable_size", "Cable Sizing Assistant", "Cable & Wiring", "Derated ampacity, voltage drop & conductor sizing", "A = (k·L·I·ρ)/ΔV"),
        CalculatorMeta("voltage_drop", "Advanced Voltage Drop", "Cable & Wiring", "Drop in Volts, percentage, end voltage & loss", "ΔV = k·I·R·cosφ"),
        CalculatorMeta("max_length", "Maximum Cable Length", "Cable & Wiring", "Permissible run distance before exceeding drop limit", "L = (ΔV·A)/(k·I·ρ)"),
        CalculatorMeta("max_length_isc", "Maximum Wire Length (Isc)", "Cable & Wiring", "Max route length to trip magnetic protection on short-circuit", "L_max = (0.8·U0·S)/(2·ρ·Ia)"),
        CalculatorMeta("cable_losses", "Power Losses in Cables", "Cable & Wiring", "Joule energy dissipation, annual kWh loss & utility cost", "ΔP = k·I²·R"),
        CalculatorMeta("cable_temperature", "Calculation of Cable Temperature", "Cable & Wiring", "Conductor operating core temperature under load current", "Tc = Ta + (Tmax-Ta)·(I/Iz)²"),
        CalculatorMeta("cable_impedance", "Cable Resistance, Reactance & Z", "Cable & Wiring", "Loop resistance R, inductive reactance X & total Z per km", "Z = √(R² + X²)"),
        CalculatorMeta("distributed_voltage_drop", "Voltage Drop with Distributed Loads", "Cable & Wiring", "Cumulative moment drop for streetlights & branch outlets", "ΔV = (k·ρ/S)·Σ(I·L)"),
        CalculatorMeta("busbar_ampacity", "Current Carrying Capacity of Busbar", "Cable & Wiring", "DIN 43671 rectangular copper/aluminum busbar rating", "I = k_mat·S^0.55"),
        CalculatorMeta("conduit_tray_fill", "Conduit & Cable Tray Sizing", "Cable & Wiring", "Conduit fill percentage and NEC Chapter 9 40% rule", "Fill% = (ΣA_wire/A_conduit)·100"),
        CalculatorMeta("awg_conversion", "AWG ↔ mm² Converter", "Cable & Wiring", "American Wire Gauge to cross-sectional area & resistance", "d = 0.127·92^((36-n)/39)"),

        CalculatorMeta("ohms_voltage", "Voltage (Ohm's Law)", "Basic Electrical", "Calculate V from current and resistance", "V = I × R"),
        CalculatorMeta("ohms_current", "Current (Ohm's Law)", "Basic Electrical", "Calculate I from voltage and resistance", "I = V / R"),
        CalculatorMeta("ohms_resistance", "Resistance (Ohm's Law)", "Basic Electrical", "Calculate R from voltage and current", "R = V / I"),
        CalculatorMeta("dc_power", "DC & Resistive Power", "Basic Electrical", "Active power, kW, and equivalent horsepower", "P = V × I"),
        CalculatorMeta("voltage_divider", "Voltage Divider", "Basic Electrical", "Calculate attenuated output voltage across resistor R2", "Vout = Vin·R2/(R1+R2)"),
        CalculatorMeta("current_divider", "Current Divider", "Basic Electrical", "Parallel branch current division according to inverse resistance", "I1 = Itotal·R2/(R1+R2)"),
        CalculatorMeta("joule_effect", "Joule Effect (Thermal Energy)", "Basic Electrical", "Heat energy produced by electrical current over time", "Q = I²·R·t"),
        CalculatorMeta("energy_cost", "Energy Consumption & Cost", "Basic Electrical", "Daily, monthly, annual kWh and utility billing", "Cost = kWh × Tariff"),

        CalculatorMeta("ac_single_phase", "AC Single Phase Power", "Power & Circuits", "P, S, Q, Power Factor & phase angle", "P = V·I·PF"),
        CalculatorMeta("ac_three_phase", "AC Three Phase Power", "Power & Circuits", "Star / Delta, Line & Phase quantities", "P = √3·VL·IL·PF"),
        CalculatorMeta("apparent_power", "Calculation of Apparent Power", "Power & Circuits", "Total vector apparent power in VA and kVA", "S = √3·V·I"),
        CalculatorMeta("reactive_power", "Calculation of Reactive Power", "Power & Circuits", "Magnetizing reactive power in VAR and kVAR", "Q = √3·V·I·sinφ"),
        CalculatorMeta("neutral_current", "Neutral Current (Unbalanced)", "Power & Circuits", "Neutral return current with unbalanced phases and triplen harmonics", "IN = √[ΣI² - Σ(I1I2)]"),

        CalculatorMeta("pf_correction", "Power Factor Correction", "Power Factor", "Capacitor bank kVAR & capacitance (µF) requirement", "Qc = P·(tanφ1 - tanφ2)"),
        CalculatorMeta("capacitor_voltage_power", "Capacitor at Different Voltage", "Power Factor", "Actual reactive kVAR output when operating off-nominal voltage", "Q2 = Q1·(V2/V1)²"),

        CalculatorMeta("breaker_size", "Breaker & Fuse Selection", "Protection", "125% continuous load rule, candidate ratings & curves", "In >= 1.25·Ib"),
        CalculatorMeta("cable_short_circuit", "Cable Short-Circuit (k²S²)", "Protection", "Adiabatic thermal withstand energy vs prospective fault I²t", "I²t ≤ k²·S²"),
        CalculatorMeta("coordination_breaker_cable", "Protective Device Coordination", "Protection", "Overload coordination verification: Ib ≤ In ≤ Iz & I2 ≤ 1.45·Iz", "Ib ≤ In ≤ Iz"),
        CalculatorMeta("earthing_rcd", "Earthing System & RCD", "Protection", "Loop impedance, earth resistance Ra and touch voltage safety", "Ra ≤ UL/IΔn"),

        // Motors Section (Matching reference images 1 & 2)
        CalculatorMeta("motor_current", "Motor Current", "Motors", "FLC, DOL & Star-Delta starting currents, overload range", "I = P/(√3·V·η·PF)"),
        CalculatorMeta("motor_power", "Motor Power", "Motors", "Active shaft output power (kW/HP) & input apparent kVA", "P = √3·V·I·cosφ·η"),
        CalculatorMeta("motor_voltage", "Motor Voltage", "Motors", "Required line and phase operating voltage calculation", "V = P/(√3·I·cosφ·η)"),
        CalculatorMeta("motor_power_factor", "Motor Power Factor", "Motors", "Calculate cosφ, phase angle & reactive kVAR requirement", "cosφ = P/(√3·V·I·η)"),
        CalculatorMeta("motor_pf_correction", "Motor Power Factor Correction", "Motors", "Capacitor bank kVAR, µF sizing & self-excitation safety limit", "Qc = Pin·(tanφ1 - tanφ2)"),
        CalculatorMeta("motor_efficiency", "Motor Efficiency", "Motors", "Machine efficiency percentage & IEC energy efficiency class (IE1-IE4)", "η = (Pout/Pin)·100%"),
        CalculatorMeta("motor_three_to_single_phase", "Motor from 3-Phase to 1-Phase", "Motors", "Steinmetz circuit run & start capacitor sizing for 230V", "C_run ≈ 70·P·(230/V)²"),
        CalculatorMeta("motor_single_phase_capacitor", "Single-Phase Motor Run Capacitor", "Motors", "Run capacitor µF sizing for PSC single-phase motors", "C = (P·10⁶)/(2π·f·V²·cosφ)"),
        CalculatorMeta("motor_speed", "Motor Speed", "Motors", "Synchronous stator speed Ns and actual rotor speed Nr from poles", "Ns = (120·f)/P"),
        CalculatorMeta("motor_slip", "Motor Slip", "Motors", "Rotor slip percentage and electromagnetic slip frequency", "s = ((Ns-Nr)/Ns)·100%"),
        CalculatorMeta("motor_torque", "Power / Maximum Torque", "Motors", "Rated full-load torque & breakdown stall torque in N·m / lb-ft", "T = (9550·P)/RPM"),
        CalculatorMeta("motor_flc_nec", "Motor Full-Load Current (NEC)", "Motors", "NEC Table 430.248 & 430.250 FLC, conductor & breaker rules", "NEC Art. 430 Tables"),
        CalculatorMeta("motor_diagram_6_terminals", "3-Phase Motor Wiring (6 Terminals)", "Motors", "Star (Y) vs Delta (Δ) terminal bridging and jumper links", "U1-V1-W1 / W2-U2-V2"),
        CalculatorMeta("motor_diagram_9_terminals", "3-Phase Motor Wiring (9 Terminals)", "Motors", "NEMA dual voltage series & parallel Wye / Delta diagrams", "T1 through T9"),
        CalculatorMeta("motor_diagram_12_terminals", "3-Phase Motor Wiring (12 Terminals)", "Motors", "Multi-voltage 230V/460V Wye, Delta & Star-Delta starter leads", "T1 through T12"),
        CalculatorMeta("motor_connections", "Motor Connections", "Motors", "Star vs Delta theory, voltage/current ratios & rotation reversal", "VY = VΔ/√3, TY = TΔ/3"),
        CalculatorMeta("motor_terminals_marking", "Motor Terminals Marking", "Motors", "IEC 60034-8 vs NEMA MG-1 terminal translation reference", "U-V-W vs T1-T12"),
        CalculatorMeta("motor_insulation_class", "Insulation Class of the Motor", "Motors", "Class A, B, F, H thermal ratings, rise limits & Arrhenius rule", "105°C – 180°C"),

        CalculatorMeta("transformer", "Transformer Sizing & Fault", "Transformers", "Primary/secondary current, turns ratio, secondary Isc", "I = kVA/(√3·V)"),
        CalculatorMeta("battery_runtime", "Battery Runtime & Energy", "Batteries", "Usable Wh, backup hours, discharge C-rate", "Hours = (Ah·V·DoD·η)/P"),
        CalculatorMeta("solar_pv", "Solar PV System Sizing", "Solar/PV", "Peak kWp, panel count, inverter sizing from daily kWh", "kWp = kWh/(PSH·η)"),
        CalculatorMeta("phase_balance", "Phase Balancing", "Industrial", "L1/L2/L3 loads, percentage imbalance, neutral risk", "Imb% = (MaxDev/Avg)·100"),
        CalculatorMeta("analog_signal_converter", "Analog Signal Values (4-20mA, 0-10V)", "Industrial", "Linear scaling between process transmitters and engineering units", "Y = Ymin + ΔY·(X-Xmin)/ΔX"),
        CalculatorMeta("antenna_length", "Antenna Length (λ/4, λ/2)", "Industrial", "Quarter-wave and dipole antenna lengths for RF telemetry", "λ/4 = (300/f)·0.25·0.95"),
        CalculatorMeta("cctv_storage_calc", "CCTV Storage & Bandwidth", "Industrial", "NVR storage in TB & network bitrate for H.265/H.264", "TB = (N·Mbps·86400·Days)/8192"),

        CalculatorMeta("lighting_lux", "Lighting Lumens & Fixtures", "Lighting", "Number of fixtures required from target lux and room area", "N = (E·A)/(Φ·CU·MF)"),

        CalculatorMeta("led_resistor", "LED Current Limiting Resistor", "Components", "Resistor ohms and recommended wattage with 2x safety", "R = (Vs - Vf)/I"),
        CalculatorMeta("voltage_divider", "Voltage Divider", "Components", "Unloaded and loaded potential divider calculations", "Vout = Vin·R2/(R1+R2)"),
        CalculatorMeta("current_divider", "Current Divider", "Components", "Branch currents inversely proportional to parallel resistors", "I1 = Itotal·R2/(R1+R2)"),
        CalculatorMeta("resonant_frequency", "Resonant Frequency (LC)", "Components", "Series/parallel LC tank natural resonance and characteristic impedance", "f0 = 1/(2π√LC)"),
        CalculatorMeta("zener_regulator", "Zener Diode Voltage Stabilizer", "Components", "Shunt voltage regulator series resistor Rs and wattage", "Rs = (Vin-Vz)/(IL+Iz)"),
        CalculatorMeta("capacitor_code", "Capacitor 3-Digit Code", "Components", "Decode 3-digit ceramic and film capacitor values in pF, nF, µF", "C = D1D2·10^D3 pF")
    )

    // Navigation history stack for back button traversal
    private val tabHistory = mutableListOf<MainNavTab>()

    fun setTab(tab: MainNavTab) {
        if (_currentTab.value != tab) {
            if (tabHistory.isEmpty() || tabHistory.last() != _currentTab.value) {
                tabHistory.add(_currentTab.value)
            }
            _currentTab.value = tab
        }
        if (tab != MainNavTab.CALCULATORS) {
            _activeCalculatorId.value = null
        }
    }

    fun openCalculator(calcId: String) {
        if (_currentTab.value != MainNavTab.CALCULATORS) {
            if (tabHistory.isEmpty() || tabHistory.last() != _currentTab.value) {
                tabHistory.add(_currentTab.value)
            }
        }
        _activeCalculatorId.value = calcId
        _currentTab.value = MainNavTab.CALCULATORS
    }

    fun closeCalculator() {
        _activeCalculatorId.value = null
    }

    fun navigateBack(): Boolean {
        // If a calculator detail view is active, exit to the calculator list first
        if (_activeCalculatorId.value != null) {
            _activeCalculatorId.value = null
            return true
        }

        // Navigate backwards through previous tabs in history
        while (tabHistory.isNotEmpty()) {
            val prev = tabHistory.removeAt(tabHistory.size - 1)
            if (prev != _currentTab.value) {
                _currentTab.value = prev
                if (prev != MainNavTab.CALCULATORS) {
                    _activeCalculatorId.value = null
                }
                return true
            }
        }

        // If history is exhausted but not yet on HOME, return to HOME
        if (_currentTab.value != MainNavTab.HOME) {
            _currentTab.value = MainNavTab.HOME
            _activeCalculatorId.value = null
            return true
        }

        // Root of HOME reached
        return false
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategoryFilter(category: String) {
        _selectedCategoryFilter.value = category
    }

    fun setSelectedProject(projectId: Long?) {
        _selectedProjectId.value = projectId
    }

    fun setDefaultVoltage(v: Double) {
        _defaultVoltage.value = v
    }

    fun setDefaultFrequency(f: Double) {
        _defaultFrequency.value = f
    }

    fun setDefaultStandard(std: String) {
        _defaultStandard.value = std
    }

    fun setDarkTheme(dark: Boolean) {
        _darkTheme.value = dark
    }

    fun saveCalculation(
        calculatorId: String,
        title: String,
        inputSummary: String,
        resultSummary: String,
        formulaUsed: String,
        projectId: Long? = null
    ) {
        viewModelScope.launch {
            repository.saveCalculation(
                CalculationRecord(
                    calculatorId = calculatorId,
                    title = title,
                    inputSummary = inputSummary,
                    resultSummary = resultSummary,
                    formulaUsed = formulaUsed,
                    projectId = projectId
                )
            )
            vibrateDevice(50)
        }
    }

    fun toggleFavorite(record: CalculationRecord) {
        viewModelScope.launch {
            repository.toggleFavorite(record.id, !record.isFavorite)
        }
    }

    fun deleteCalculation(record: CalculationRecord) {
        viewModelScope.launch {
            repository.deleteCalculation(record.id)
        }
    }

    fun createProject(name: String, client: String, location: String, voltage: Double) {
        viewModelScope.launch {
            val id = repository.saveProject(
                Project(
                    name = name,
                    clientName = client,
                    location = location,
                    systemVoltage = voltage,
                    defaultStandard = _defaultStandard.value
                )
            )
            _selectedProjectId.value = id
        }
    }

    fun deleteProject(project: Project) {
        viewModelScope.launch {
            repository.deleteProject(project)
            if (_selectedProjectId.value == project.id) {
                _selectedProjectId.value = null
            }
        }
    }

    fun addCircuitToProject(projectId: Long, name: String, type: String, phase: String, watts: Double, voltage: Double, breakerA: Int) {
        viewModelScope.launch {
            repository.addCircuit(
                LoadCircuit(
                    projectId = projectId,
                    circuitName = name,
                    loadType = type,
                    phase = phase,
                    watts = watts,
                    voltage = voltage,
                    breakerRatingA = breakerA
                )
            )
        }
    }

    fun deleteCircuit(circuit: LoadCircuit) {
        viewModelScope.launch {
            repository.deleteCircuit(circuit)
        }
    }

    fun getCircuitsForProject(projectId: Long): Flow<List<LoadCircuit>> {
        return repository.getCircuitsForProject(projectId)
    }

    fun toggleFlashlight() {
        val context = getApplication<Application>()
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager ?: return
        try {
            val cameraId = cameraManager.cameraIdList.firstOrNull() ?: return
            val newState = !_isFlashlightOn.value
            cameraManager.setTorchMode(cameraId, newState)
            _isFlashlightOn.value = newState
            vibrateDevice(40)
        } catch (_: Exception) {
            // Flashlight might not be present in emulator, gracefully fallback
            _isFlashlightOn.value = !_isFlashlightOn.value
        }
    }

    private fun vibrateDevice(ms: Long) {
        try {
            val vibrator = getApplication<Application>().getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(ms)
            }
        } catch (_: Exception) {}
    }
}
