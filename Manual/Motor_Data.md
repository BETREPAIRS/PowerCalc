# Motor Engineering Technical Data Manual
**PowerCalc Engineering Reference Suite**
*Compiled from Reference Inspection Data: Motor Systems, Nameplates, Connections & Power Factor Correction*

---

## 1. Three-Phase Induction Motor Full Load Current (FLC / FLA) Table
*Reference standard: IEC 60034-1 & NEC Table 430.250 at 50Hz/60Hz, 4-Pole 1500/1800 RPM (Typical)*

| Motor Power (kW) | Power (HP) | 220V / 230V (A) | 380V / 400V (A) | 415V (A) | 460V / 480V (A) | 690V (A) | Efficiency (η) | Cos φ (PF) |
|---|---|---|---|---|---|---|---|---|
| **0.37** | 0.5 | 1.85 | 1.05 | 1.01 | 0.90 | 0.60 | 66.0% | 0.72 |
| **0.55** | 0.75 | 2.55 | 1.45 | 1.40 | 1.25 | 0.84 | 70.0% | 0.75 |
| **0.75** | 1.0 | 3.25 | 1.85 | 1.78 | 1.60 | 1.07 | 73.0% | 0.76 |
| **1.1** | 1.5 | 4.60 | 2.65 | 2.55 | 2.25 | 1.53 | 76.5% | 0.78 |
| **1.5** | 2.0 | 6.00 | 3.45 | 3.30 | 2.95 | 2.00 | 78.5% | 0.79 |
| **2.2** | 3.0 | 8.50 | 4.90 | 4.70 | 4.20 | 2.83 | 81.0% | 0.81 |
| **3.0** | 4.0 | 11.20 | 6.40 | 6.15 | 5.50 | 3.70 | 82.5% | 0.82 |
| **4.0** | 5.5 | 14.80 | 8.50 | 8.15 | 7.20 | 4.91 | 84.5% | 0.83 |
| **5.5** | 7.5 | 19.80 | 11.40 | 10.90 | 9.70 | 6.58 | 86.0% | 0.84 |
| **7.5** | 10.0 | 26.50 | 15.20 | 14.60 | 13.00 | 8.78 | 87.5% | 0.85 |
| **11.0** | 15.0 | 38.00 | 22.00 | 21.00 | 18.80 | 12.70 | 89.0% | 0.86 |
| **15.0** | 20.0 | 51.00 | 29.50 | 28.20 | 25.20 | 17.00 | 90.0% | 0.86 |
| **18.5** | 25.0 | 62.50 | 36.00 | 34.50 | 30.80 | 20.80 | 90.8% | 0.87 |
| **22.0** | 30.0 | 73.50 | 42.50 | 40.50 | 36.20 | 24.50 | 91.5% | 0.87 |
| **30.0** | 40.0 | 99.00 | 57.00 | 54.80 | 48.80 | 32.90 | 92.5% | 0.88 |
| **37.0** | 50.0 | 121.00 | 70.00 | 67.00 | 59.50 | 40.40 | 93.0% | 0.88 |
| **45.0** | 60.0 | 146.00 | 84.50 | 81.00 | 72.00 | 48.80 | 93.4% | 0.88 |
| **55.0** | 75.0 | 177.00 | 102.00 | 98.00 | 87.00 | 58.90 | 93.8% | 0.89 |
| **75.0** | 100.0 | 239.00 | 138.00 | 132.00 | 118.00 | 79.70 | 94.2% | 0.89 |
| **90.0** | 125.0 | 285.00 | 165.00 | 158.00 | 140.00 | 95.30 | 94.6% | 0.89 |
| **110.0** | 150.0 | 348.00 | 201.00 | 192.00 | 171.00 | 116.00 | 95.0% | 0.90 |
| **132.0** | 180.0 | 415.00 | 240.00 | 230.00 | 204.00 | 138.60 | 95.2% | 0.90 |
| **160.0** | 220.0 | 500.00 | 290.00 | 277.00 | 246.00 | 167.40 | 95.5% | 0.90 |
| **200.0** | 270.0 | 622.00 | 360.00 | 345.00 | 306.00 | 207.80 | 95.8% | 0.91 |

---

## 2. Motor Terminal Connections & Lead Markings (IEC 60034-8)

### Star (Wye - Y) Connection (High Voltage):
- Used for starting or when supply line voltage matches the motor's higher voltage rating (e.g., 400V on a 230V/400V motor).
- **Terminal Bridges**: Horizontal bridge linking terminals **W2 - U2 - V2** (Star neutral point).
- **Line Infeed**: L1 to **U1**, L2 to **V1**, L3 to **W1**.
- Phase Voltage across each winding: $V_{phase} = V_{line} / \sqrt{3} \approx 0.577 \times V_{line}$.
- Phase Current equals Line Current: $I_{phase} = I_{line}$.

### Delta (Δ) Connection (Low Voltage / Run Mode):
- Used for run configuration on Star-Delta starters or when line voltage matches motor delta rating (e.g., 400V on a 400V/690V motor).
- **Terminal Bridges**: Three vertical bridges: **U1 to W2**, **V1 to U2**, **W1 to V2**.
- **Line Infeed**: L1 to **U1/W2**, L2 to **V1/U2**, L3 to **W1/V2**.
- Phase Voltage across each winding: $V_{phase} = V_{line}$.
- Line Current is $\sqrt{3} \times$ Phase Current: $I_{line} = \sqrt{3} \cdot I_{phase}$.

---

## 3. Motor Power Factor Correction & Capacitor Bank Sizing

### Sizing Equation:
$$Q_c = P_{in} \cdot \left(\tan(\varphi_1) - \tan(\varphi_2)\right) = \frac{P_{shaft}}{\eta} \cdot \left(\tan(\arccos(PF_1)) - \tan(\arccos(PF_2))\right)$$

### Capacitance Required per Phase:
- **Delta (Δ) Bank**: $C_\Delta = \frac{Q_c \times 10^9}{3 \cdot 2\pi f \cdot V_{LL}^2} \quad [\mu\text{F}]$
- **Star (Y) Bank**: $C_Y = \frac{Q_c \times 10^9}{2\pi f \cdot V_{LL}^2} \quad [\mu\text{F}] = 3 \cdot C_\Delta$

### Critical Overload Relay Sizing Rule:
When power factor correction capacitors are installed on the motor side (downstream) of the overload relay:
- The line current is reduced: $I_{corrected} = I_{uncorrected} \cdot \frac{PF_1}{PF_2}$.
- **Warning**: Overload relay setting must be adjusted down to the corrected current to ensure full thermal overload protection of the motor windings.

---

## 4. Single-Phase Motor Capacitor Values (Starting & Running)
- **Permanent Split Capacitor (PSC)**: $30 - 50 \, \mu\text{F}$ per kW at 230V.
- **Capacitor Start / Induction Run (CSIR)**: $150 - 300 \, \mu\text{F}$ per kW at 230V (electrolytic, duty-rated).
- **Centrifugal switch disconnect speed**: Typically 75% to 80% of synchronous speed.
