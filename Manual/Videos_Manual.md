# Practical Field Videos Technical Manual
**PowerCalc Engineering Reference Suite**
*Compiled from Reference Inspection Data: 11 Practical Field Demonstration Videos*

---

## Overview & Index of Video Reference Demonstrations

This manual documents the step-by-step procedures, key safety protocols, and measurement benchmarks from the 11 uploaded engineering field demonstration videos.

| Video ID | Topic / Demonstration | Category | Equipment Involved | Key Formula / Standard |
|---|---|---|---|---|
| **VID-01** | Induction Motor Star-Delta Starting Transition | Motor Engineering | 3-Phase Contactor, Timer, Overload | $I_{star} = \frac{1}{3} I_{delta}$, $T_{star} = \frac{1}{3} T_{delta}$ |
| **VID-02** | Motor Insulation Resistance (Megger) Testing | Motor Maintenance | 500V/1000V DC Insulation Tester | IEEE 43 ($R_{ins} \ge 1 \, \text{M}\Omega + \text{kV}$ rating) |
| **VID-03** | Motor Terminal Box Bridging (Star vs Delta) | Motor Connections | U1, V1, W1 / U2, V2, W2 terminals | IEC 60034-8 Terminal Marking |
| **VID-04** | Motor Power Factor Capacitor Bank Connection | Motor Efficiency | Delta-connected Capacitor Bank | $Q_c = P (\tan\varphi_1 - \tan\varphi_2)$ |
| **VID-05** | Transformer Insulation & Polarization Index (PI) | Transformer Testing | 1000V/2500V DC Megger | $PI = R_{10\text{min}} / R_{1\text{min}} \ge 2.0$ |
| **VID-06** | Transformer Turn Ratio (TTR) Verification | Transformer Commissioning | TTR Test Set, Voltmeter | $V_1 / V_2 = N_1 / N_2 = I_2 / I_1$ |
| **VID-07** | Transformer Off-Circuit Tap Changer Adjustment | Transformer Operation | Tap selector handle (+/- 2.5%, 5%) | De-energize completely before tap change! |
| **VID-08** | Digital Multimeter True-RMS AC Voltage & Current | General Measurements | Fluke/True-RMS Multimeter & Clamp | $V_{rms} = \sqrt{\frac{1}{T} \int v(t)^2 dt}$ |
| **VID-09** | Phase Rotation Sequence Check (L1-L2-L3) | General Electrical | Phase Sequence Indicator Meter | Positive Phase Sequence (Clockwise: A-B-C) |
| **VID-10** | Earth Ground Resistance 3-Point Fall of Potential | Safety & Grounding | Earth Tester, Auxiliary Stakes | IEEE 81 (62% Rule for potential spike) |
| **VID-11** | Infrared Thermal Imaging of Panel & Breakers | Field Troubleshooting | Thermal Imager / FLIR Camera | $\Delta T \le 10^\circ\text{C}$ normal, $> 30^\circ\text{C}$ critical fault |

---

## Detailed Step-by-Step Field Procedures

### 1. Motor Star-Delta Starting Sequence (VID-01):
1. **Initial Energization**: Main contactor (KM1) and Star contactor (KM3) pick up simultaneously. Motor starts in Star configuration. Starting inrush current is limited to approximately $33\%$ of Direct-On-Line (DOL) current.
2. **Timing Period**: Star timer runs for 5 to 10 seconds until motor attains approximately 75% to 85% of rated rotational speed.
3. **Transition**: Star contactor (KM3) drops out, followed by a guaranteed mechanical/electrical interlock pause of 30–50 ms to prevent phase-to-phase short circuits.
4. **Run Configuration**: Delta contactor (KM2) picks up. Full line voltage is applied across each phase winding.

### 2. Transformer Insulation Resistance & PI Testing (VID-05):
1. Ensure transformer is de-energized, isolated, and discharged to ground.
2. Short-circuit all HV terminals together ($H_1, H_2, H_3$) and LV terminals together ($X_1, X_2, X_3, X_0$).
3. Test combinations:
   - Primary to Secondary + Ground ($HV - LV + G$)
   - Primary to Secondary ($HV - LV$)
   - Secondary to Primary + Ground ($LV - HV + G$)
4. Measure at 1 minute and 10 minutes. Calculate Polarization Index: $PI = R_{10} / R_1$. A value above 2.0 indicates dry, sound dielectric insulation.

### 3. Earth Ground Testing (VID-10):
1. Place Earth Electrode Under Test (E).
2. Drive Current Stake (C) at distance $D \approx 30 - 50 \, \text{m}$.
3. Drive Potential Stake (P) at distance $0.62 \times D$ from E along a straight line.
4. Inject test frequency AC and record resistance in Ohms. Target: $< 5 \, \Omega$ for distribution transformers, $< 1 \, \Omega$ for primary substations.
