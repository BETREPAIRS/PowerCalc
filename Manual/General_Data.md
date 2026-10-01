# General Electrical Technical Data Manual
**PowerCalc Engineering Reference Suite**
*Compiled from Reference Inspection Data: Basic Electrical Formulas, Cable Sizing, Voltage Drop, Conductor Codes & IP Ratings*

---

## 1. Fundamentals: Ohm's Law & AC/DC Power Circle Formulas

### Direct Current (DC) & Single-Phase AC Unity PF:
- $V = I \cdot R$
- $I = \frac{V}{R} = \frac{P}{V} = \sqrt{\frac{P}{R}}$
- $R = \frac{V}{I} = \frac{V^2}{P} = \frac{P}{I^2}$
- $P = V \cdot I = I^2 \cdot R = \frac{V^2}{R}$

### Alternating Current (AC) Single-Phase:
- **Active Power**: $P = V \cdot I \cdot \cos\varphi \quad [\text{W or kW}]$
- **Reactive Power**: $Q = V \cdot I \cdot \sin\varphi \quad [\text{VAR or kVAR}]$
- **Apparent Power**: $S = V \cdot I \quad [\text{VA or kVA}]$
- **Power Factor**: $\cos\varphi = \frac{P}{S} = \frac{\text{kW}}{\text{kVA}}$

### Alternating Current (AC) Three-Phase:
- **Active Power**: $P = \sqrt{3} \cdot V_{LL} \cdot I_L \cdot \cos\varphi$
- **Reactive Power**: $Q = \sqrt{3} \cdot V_{LL} \cdot I_L \cdot \sin\varphi$
- **Apparent Power**: $S = \sqrt{3} \cdot V_{LL} \cdot I_L$
- **Line Current**: $I_L = \frac{P}{\sqrt{3} \cdot V_{LL} \cdot \cos\varphi} = \frac{S}{\sqrt{3} \cdot V_{LL}}$

---

## 2. Voltage Drop Calculation Equations (IEC & NEC)

### Single-Phase (2-Wire AC or DC):
$$\Delta V = \frac{2 \cdot L \cdot I \cdot (R \cos\varphi + X \sin\varphi)}{1000} \quad [\text{V}]$$
$$\% \Delta V = \frac{\Delta V}{V_n} \times 100\%$$

### Three-Phase (3-Wire or 4-Wire Balanced AC):
$$\Delta V = \frac{\sqrt{3} \cdot L \cdot I \cdot (R \cos\varphi + X \sin\varphi)}{1000} \quad [\text{V}]$$

*Where:*
- $L$ = Length of run in meters (one-way)
- $I$ = Design load current in Amperes
- $R$ = Conductor resistance in $\Omega/\text{km}$ at operating temperature (typically 70°C for PVC, 90°C for XLPE)
- $X$ = Conductor reactance in $\Omega/\text{km}$ (typically $0.08 \, \Omega/\text{km}$ for multicore cables)
- Maximum allowable drop per standard: $\le 3\%$ for branch circuits, $\le 5\%$ total service feeder + branch.

---

## 3. Copper Conductor Current Ampacities (IEC 60364-5-52 / 30°C Ambient)

| Conductor Size (mm²) | Method B1/B2 (In Conduit / Trunking) (A) | Method C (Clipped Direct on Wall) (A) | Method E (Perforated Cable Tray) (A) | Method D (Direct in Ground) (A) | Copper Resistance $R_{20}$ (Ω/km) |
|---|---|---|---|---|---|
| **1.5** | 15.5 | 19.5 | 22 | 22 | 12.10 |
| **2.5** | 21 | 27 | 30 | 29 | 7.41 |
| **4.0** | 28 | 36 | 40 | 38 | 4.61 |
| **6.0** | 36 | 46 | 52 | 47 | 3.08 |
| **10.0** | 50 | 63 | 71 | 63 | 1.83 |
| **16.0** | 68 | 85 | 96 | 81 | 1.15 |
| **25.0** | 89 | 112 | 119 | 104 | 0.727 |
| **35.0** | 110 | 138 | 147 | 125 | 0.524 |
| **50.0** | 134 | 168 | 179 | 148 | 0.387 |
| **70.0** | 171 | 213 | 229 | 183 | 0.268 |
| **95.0** | 207 | 258 | 278 | 216 | 0.193 |
| **120.0** | 239 | 299 | 322 | 246 | 0.153 |
| **150.0** | 272 | 344 | 371 | 278 | 0.124 |
| **185.0** | 310 | 392 | 424 | 312 | 0.0991 |
| **240.0** | 364 | 461 | 500 | 361 | 0.0754 |
| **300.0** | 419 | 530 | 576 | 408 | 0.0601 |

---

## 4. International Conductor Wiring Color Codes

### IEC Standard (Europe, UK post-2004, International, Australia):
- **Phase 1 (L1)**: Brown
- **Phase 2 (L2)**: Black
- **Phase 3 (L3)**: Grey
- **Neutral (N)**: Blue
- **Protective Earth (PE)**: Green with Yellow stripe

### NEC Standard (USA / North America 120V/208V/240V):
- **Phase 1 (A)**: Black
- **Phase 2 (B)**: Red
- **Phase 3 (C)**: Blue
- **Neutral (N)**: White
- **Ground (GND)**: Bare or Green / Green with Yellow stripe

### NEC Standard (USA / North America 277V/480V):
- **Phase 1 (A)**: Brown
- **Phase 2 (B)**: Orange
- **Phase 3 (C)**: Yellow
- **Neutral (N)**: Grey
- **Ground (GND)**: Green / Green with Yellow stripe

---

## 5. Ingress Protection (IP) Code Quick Reference (IEC 60529)

### First Digit: Protection Against Solid Objects & Dust
- **IP0x**: No protection
- **IP1x**: Protected against objects $> 50\text{ mm}$ (back of hand)
- **IP2x**: Protected against objects $> 12.5\text{ mm}$ (fingers)
- **IP3x**: Protected against objects $> 2.5\text{ mm}$ (tools, thick wires)
- **IP4x**: Protected against objects $> 1.0\text{ mm}$ (fine wires, screws)
- **IP5x**: Dust protected (ingress not entirely prevented, but no interference with operation)
- **IP6x**: Dust tight (zero dust ingress under continuous negative pressure)

### Second Digit: Protection Against Water & Moisture
- **IPx0**: No protection
- **IPx1**: Vertically falling water drops (condensation)
- **IPx2**: Water drops falling at up to 15° inclination
- **IPx3**: Water spray up to 60° from vertical
- **IPx4**: Splash water from any direction
- **IPx5**: Water jets (6.3 mm nozzle from any angle)
- **IPx6**: Powerful water jets (12.5 mm nozzle, heavy seas)
- **IPx7**: Temporary immersion in water up to 1 meter depth for 30 minutes
- **IPx8**: Continuous submersion under specified pressure and depth
- **IPx9K**: High-pressure, high-temperature washdown jets (steam cleaning, 80°C at 100 bar)
