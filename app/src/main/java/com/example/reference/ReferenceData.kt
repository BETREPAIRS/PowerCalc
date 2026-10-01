package com.example.reference

data class AwgWireEntry(
    val awg: String,
    val diameterMm: Double,
    val areaMm2: Double,
    val resistanceOhmPerKm: Double,
    val ampacity75C: Int,
    val typicalApplication: String
)

data class WireColorStandard(
    val standardName: String,
    val region: String,
    val line1: String,
    val line2: String,
    val line3: String,
    val neutral: String,
    val earth: String,
    val dcPositive: String,
    val dcNegative: String
)

data class IpRatingCode(
    val digit: Int,
    val category: String, // "Solid" or "Liquid"
    val level: String,
    val protectionDescription: String
)

data class NemaType(
    val type: String,
    val environment: String,
    val features: String,
    val typicalUses: String
)

data class MotorInsulation(
    val className: String,
    val maxTempC: Int,
    val allowableRiseC: Int,
    val hotSpotMarginC: Int,
    val application: String
)

data class WorldVoltage(
    val country: String,
    val singlePhaseV: String,
    val threePhaseV: String,
    val frequencyHz: String,
    val plugTypes: String
)

data class ElectricalSymbol(
    val name: String,
    val category: String,
    val symbolChar: String,
    val description: String,
    val standardCode: String
)

data class TroubleshootingStep(
    val question: String,
    val checks: List<String>,
    val safetyWarning: String,
    val possibleRemedy: String
)

data class TroubleshootingGuide(
    val id: String,
    val title: String,
    val description: String,
    val steps: List<TroubleshootingStep>
)

object ReferenceData {

    val awgTable = listOf(
        AwgWireEntry("14 AWG", 1.628, 2.08, 8.286, 15, "Residential lighting & 15A branch circuits"),
        AwgWireEntry("12 AWG", 2.053, 3.31, 5.211, 20, "20A general appliance outlets & kitchen circuits"),
        AwgWireEntry("10 AWG", 2.588, 5.26, 3.277, 30, "30A water heaters, dryers & AC condensing units"),
        AwgWireEntry("8 AWG", 3.264, 8.37, 2.061, 50, "40A-50A electric ranges, small sub-panels"),
        AwgWireEntry("6 AWG", 4.115, 13.30, 1.296, 65, "60A heavy appliances, sub-panel feeds"),
        AwgWireEntry("4 AWG", 5.189, 21.15, 0.815, 85, "Residential 100A service feeders (with 310.12 allowance)"),
        AwgWireEntry("2 AWG", 6.544, 33.62, 0.513, 115, "100A sub-panels & heavy commercial feeders"),
        AwgWireEntry("1/0 AWG", 8.251, 53.49, 0.322, 150, "150A main entrance services"),
        AwgWireEntry("2/0 AWG", 9.266, 67.43, 0.256, 175, "200A residential main service entrance"),
        AwgWireEntry("3/0 AWG", 10.40, 85.01, 0.203, 200, "Commercial 200A service entrance feeds"),
        AwgWireEntry("4/0 AWG", 11.68, 107.2, 0.161, 230, "Heavy industrial & main switchboard bus feeds")
    )

    val wireColorStandards = listOf(
        WireColorStandard(
            standardName = "IEC 60446 (International / European Union / UK)",
            region = "Europe, UK, International",
            line1 = "Brown",
            line2 = "Black",
            line3 = "Grey",
            neutral = "Blue",
            earth = "Green / Yellow striped",
            dcPositive = "Red or Brown",
            dcNegative = "Black or Blue"
        ),
        WireColorStandard(
            standardName = "NEC 120/240V (United States & Canada Standard)",
            region = "USA, Canada, Mexico",
            line1 = "Black",
            line2 = "Red",
            line3 = "Blue (or High Leg Orange)",
            neutral = "White",
            earth = "Bare Copper or Green",
            dcPositive = "Red",
            dcNegative = "Black"
        ),
        WireColorStandard(
            standardName = "NEC 277/480V (Industrial Commercial Three-Phase)",
            region = "USA Commercial",
            line1 = "Brown",
            line2 = "Orange",
            line3 = "Yellow",
            neutral = "Gray",
            earth = "Green",
            dcPositive = "Red",
            dcNegative = "Black"
        ),
        WireColorStandard(
            standardName = "AS/NZS 3000 (Australia & New Zealand)",
            region = "Australia, New Zealand",
            line1 = "Red or Brown",
            line2 = "White or Black",
            line3 = "Dark Blue or Grey",
            neutral = "Black or Light Blue",
            earth = "Green / Yellow striped",
            dcPositive = "Red",
            dcNegative = "Black"
        )
    )

    val ipSolidRatings = listOf(
        IpRatingCode(0, "Solid", "Non-protected", "No special protection against contact or ingress of solid objects."),
        IpRatingCode(1, "Solid", "Objects > 50 mm", "Protection against large surface areas (e.g. back of hand)."),
        IpRatingCode(2, "Solid", "Objects > 12.5 mm", "Protection against fingers or small tools up to 80mm long."),
        IpRatingCode(3, "Solid", "Objects > 2.5 mm", "Protection against tools, thick wires, screws."),
        IpRatingCode(4, "Solid", "Objects > 1.0 mm", "Protection against small wires, slender screws, insects."),
        IpRatingCode(5, "Solid", "Dust Protected", "Ingress of dust is not entirely prevented, but must not interfere with equipment."),
        IpRatingCode(6, "Solid", "Dust Tight", "No ingress of dust; complete protection against contact (vacuum tested).")
    )

    val ipLiquidRatings = listOf(
        IpRatingCode(0, "Liquid", "Non-protected", "No protection against water penetration."),
        IpRatingCode(1, "Liquid", "Dripping water", "Vertically falling water drops shall have no harmful effect."),
        IpRatingCode(2, "Liquid", "Dripping at 15°", "Vertically dripping water when enclosure is tilted up to 15°."),
        IpRatingCode(3, "Liquid", "Spraying water", "Water falling as a spray at any angle up to 60° from the vertical."),
        IpRatingCode(4, "Liquid", "Splashing water", "Water splashing against the enclosure from any direction."),
        IpRatingCode(5, "Liquid", "Water jets", "Water projected by a 6.3mm nozzle against enclosure from any direction."),
        IpRatingCode(6, "Liquid", "Powerful jets", "Water projected in powerful 12.5mm jets against enclosure."),
        IpRatingCode(7, "Liquid", "Immersion up to 1m", "Ingress of water in harmful quantity not possible when immersed (30 min)."),
        IpRatingCode(8, "Liquid", "Continuous immersion", "Submersion under specified depth/pressure continuously (e.g. submersible pump).")
    )

    val nemaTypes = listOf(
        NemaType("Type 1", "Indoor", "General purpose; protects against falling dirt and accidental contact.", "Indoor distribution boards, lighting controls"),
        NemaType("Type 3R", "Outdoor", "Rainproof, sleet resistant, ice external undamaged.", "Outdoor meter sockets, disconnect switches, HVAC units"),
        NemaType("Type 4", "Indoor / Outdoor", "Watertight, dusttight, hose-down water blast proof.", "Dairies, food processing, washdown areas"),
        NemaType("Type 4X", "Indoor / Outdoor", "Watertight and corrosion resistant (stainless steel or heavy polymer).", "Marine docks, chemical plants, wastewater treatment"),
        NemaType("Type 12", "Indoor", "Dusttight, drip-proof, oil and non-corrosive coolant seepage resistant.", "Industrial machining centers, factory floor control panels"),
        NemaType("Type 13", "Indoor", "Oil-tight, dusttight, resists oil spraying and splashing.", "Heavy tooling stations and machine tool consoles")
    )

    val motorInsulationClasses = listOf(
        MotorInsulation("Class A", 105, 60, 5, "Legacy motors, low duty cycle fractional horsepower"),
        MotorInsulation("Class B", 130, 80, 10, "Standard general-purpose industrial motors"),
        MotorInsulation("Class F", 155, 105, 10, "Modern standard industrial motors and inverter-duty drives"),
        MotorInsulation("Class H", 180, 125, 15, "High-temperature, severe-duty, traction, and crane motors")
    )

    val worldVoltages = listOf(
        WorldVoltage("United States", "120 V", "208 V / 480 V", "60 Hz", "Type A, B"),
        WorldVoltage("Canada", "120 V", "208 V / 600 V", "60 Hz", "Type A, B"),
        WorldVoltage("United Kingdom", "230 V", "400 V", "50 Hz", "Type G"),
        WorldVoltage("Germany / France / EU", "230 V", "400 V", "50 Hz", "Type C, E, F"),
        WorldVoltage("Australia / New Zealand", "230 V", "400 V", "50 Hz", "Type I"),
        WorldVoltage("Japan", "100 V", "200 V", "50 / 60 Hz", "Type A, B"),
        WorldVoltage("India / Pakistan", "230 V", "415 V", "50 Hz", "Type C, D, M"),
        WorldVoltage("Saudi Arabia / UAE", "220 V / 230 V", "380 V / 400 V", "60 Hz / 50 Hz", "Type G"),
        WorldVoltage("Brazil", "127 V / 220 V", "220 V / 380 V", "60 Hz", "Type N")
    )

    val electricalSymbols = listOf(
        ElectricalSymbol("Single Pole Switch", "Switches", "⚲", "Disconnects one energized line conductor.", "IEC 60617"),
        ElectricalSymbol("Circuit Breaker (MCB)", "Protection", "⎶", "Automatic thermal-magnetic overcurrent protective device.", "IEC 60617-7"),
        ElectricalSymbol("Fuse", "Protection", "━▭━", "Sacrificial overcurrent protective device.", "IEC 60617-7"),
        ElectricalSymbol("Earth / Ground", "Earthing", "⏚", "Protective conductor connection directly to earth.", "IEC 60417-5017"),
        ElectricalSymbol("Chassis / Frame Ground", "Earthing", "⏛", "Conductive frame of equipment not necessarily connected to earth.", "IEC 60417-5020"),
        ElectricalSymbol("Transformer", "Machines", "⧉", "Electromagnetic voltage stepping device.", "IEC 60617-6"),
        ElectricalSymbol("3-Phase AC Motor", "Motors", "Ⓜ 3~", "Three-phase squirrel cage or wound rotor induction motor.", "IEC 60617-6"),
        ElectricalSymbol("Capacitor", "Components", "┫┣", "Electrical electrostatic energy storage element.", "IEC 60617-4"),
        ElectricalSymbol("Inductor / Choke", "Components", "∿∿", "Electromagnetic reactive element.", "IEC 60617-4"),
        ElectricalSymbol("Normally Open Contact (NO)", "Control", "—/ —", "Closes when coil or relay is energized.", "IEC 60617-7"),
        ElectricalSymbol("Normally Closed Contact (NC)", "Control", "—/\\—", "Opens when coil or relay is energized.", "IEC 60617-7"),
        ElectricalSymbol("Contactor / Relay Coil", "Control", "[  ]", "Electromagnetic operating actuator.", "IEC 60617-7")
    )

    val troubleshootingGuides = listOf(
        TroubleshootingGuide(
            id = "no_power",
            title = "No Power at Outlet or Equipment",
            description = "Systematic isolation guide when a circuit is completely dead.",
            steps = listOf(
                TroubleshootingStep(
                    question = "1. Is the Main Distribution Breaker in the ON position?",
                    checks = listOf("Visually inspect main breaker handle position", "Check for upstream utility grid outage with neighbors"),
                    safetyWarning = "Verify absence of voltage with a calibrated CAT III/IV two-pole voltage tester before touching busbars.",
                    possibleRemedy = "Reset main breaker once if tripped. If it trips immediately, do not force it."
                ),
                TroubleshootingStep(
                    question = "2. Is the branch MCB or RCD tripped?",
                    checks = listOf("Check branch breaker handle", "Test RCD test button: if it doesn't pop, RCD may have failed or supply is missing"),
                    safetyWarning = "Do not repeatedly reset a breaker into a potential dead short.",
                    possibleRemedy = "Turn off all downstream loads, reset breaker, then turn loads on one by one."
                ),
                TroubleshootingStep(
                    question = "3. Measure Line to Neutral and Line to Earth voltage",
                    checks = listOf("L-N should be ~230V (or ~120V US)", "L-E should match L-N", "N-E should be less than 2-3V"),
                    safetyWarning = "Ensure multimeter probes are in V/Ω jacks, NEVER in Amp jacks when measuring voltage.",
                    possibleRemedy = "If L-E is normal but L-N is 0V, you have an open/disconnected Neutral conductor."
                )
            )
        ),
        TroubleshootingGuide(
            id = "breaker_trips",
            title = "Breaker Trips Repeatedly",
            description = "Differentiating between overload, short circuit, and earth leakage.",
            steps = listOf(
                TroubleshootingStep(
                    question = "1. Does the breaker trip IMMEDIATELY upon reset, or after minutes?",
                    checks = listOf("Instant trip (< 0.1s): Indicates a dead short circuit or earth fault", "Delayed trip (3-20 minutes): Indicates thermal overload from excessive current"),
                    safetyWarning = "Repeatedly hammering a breaker into a dead short creates severe arc flash hazards.",
                    possibleRemedy = "For delayed trips: measure load with clamp meter and compare to breaker rated current (In)."
                ),
                TroubleshootingStep(
                    question = "2. Is an RCD / GFCI tripping instead of standard MCB?",
                    checks = listOf("RCD trips on residual earth leakage current (typically 30mA)", "Unplug appliances with heating elements (ovens, water heaters, irons)"),
                    safetyWarning = "Disconnect main power before performing 500V insulation resistance (Megger) tests.",
                    possibleRemedy = "Perform Megger insulation test between conductors and earth. Acceptable value > 1 MΩ."
                )
            )
        ),
        TroubleshootingGuide(
            id = "motor_issues",
            title = "Motor Fails to Start or Hums Loudly",
            description = "Diagnosing phase loss, capacitor failure, or mechanical jamming.",
            steps = listOf(
                TroubleshootingStep(
                    question = "1. Is the motor shaft free to rotate manually?",
                    checks = listOf("With power locked out, attempt to spin the shaft by hand", "Listen for bearing grinding or mechanical binding"),
                    safetyWarning = "LOCKOUT/TAGOUT: Ensure power is physically disconnected before touching mechanical shafts.",
                    possibleRemedy = "If shaft is seized, mechanical bearing or coupling replacement is required."
                ),
                TroubleshootingStep(
                    question = "2. For 3-Phase: Are all 3 phase voltages equal?",
                    checks = listOf("Measure L1-L2, L2-L3, L1-L3 voltage at motor terminal box", "Phase imbalance must not exceed 2%"),
                    safetyWarning = "Single-phasing an induction motor will burn out the windings within minutes.",
                    possibleRemedy = "Check contactor contacts for pitting, burnt poles, or loose terminal screws."
                ),
                TroubleshootingStep(
                    question = "3. For 1-Phase: Has the start/run capacitor degraded?",
                    checks = listOf("Discharge capacitor safely through a 10kΩ resistor", "Measure capacitance with a DMM in capacitance mode"),
                    safetyWarning = "Always discharge capacitors before testing; stored energy can cause electric shock.",
                    possibleRemedy = "Replace capacitor if measured value is below 90% of rated microfarads."
                )
            )
        ),
        TroubleshootingGuide(
            id = "flickering_lights",
            title = "Flickering Lights & Voltage Fluctuations",
            description = "Tracing loose neutral joints, motor starting dips, or poor utility connection.",
            steps = listOf(
                TroubleshootingStep(
                    question = "1. Do lights flicker when a major appliance starts?",
                    checks = listOf("AC compressor, pump, or welder starting causing inrush voltage drop"),
                    safetyWarning = "Thermal imaging or non-contact thermometer can spot overheated loose lugs.",
                    possibleRemedy = "Install soft starter on motor or upgrade feeder wire gauge."
                ),
                TroubleshootingStep(
                    question = "2. Do some lights get brighter while others get dimmer simultaneously?",
                    checks = listOf("CRITICAL WARNING: This indicates a floating/broken shared neutral conductor!"),
                    safetyWarning = "A floating neutral can send 400V across 230V appliances, causing immediate equipment fires.",
                    possibleRemedy = "De-energize main supply immediately. Inspect main neutral busbar and utility service connection."
                )
            )
        )
    )
}
