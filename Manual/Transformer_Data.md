# Transformer Engineering Technical Data Manual
**PowerCalc Engineering Reference Suite**
*Compiled from Reference Inspection Data: Transformer Ratings, Vector Groups, Impedance & Protection*

---

## 1. Transformer Full Load Rated Currents (FLC / FLA) Table
*Calculated according to IEC 60076 & IEEE C57 standards*

### Three-Phase Transformer Full Load Current:
$$I_{3\phi} = \frac{S_{\text{kVA}} \times 1000}{\sqrt{3} \times V_{\text{line}}}$$

### Single-Phase Transformer Full Load Current:
$$I_{1\phi} = \frac{S_{\text{kVA}} \times 1000}{V_{\text{line}}}$$

| Rated Power (kVA) | 400V (A) [3-Phase] | 415V (A) [3-Phase] | 480V (A) [3-Phase] | 11 kV (A) [3-Phase] | 22 kV (A) [3-Phase] | 33 kV (A) [3-Phase] | 230V (A) [1-Phase] | Typical %Z |
|---|---|---|---|---|---|---|---|---|
| **15** | 21.65 | 20.87 | 18.04 | 0.79 | 0.39 | 0.26 | 65.22 | 4.0% |
| **25** | 36.08 | 34.78 | 30.07 | 1.31 | 0.66 | 0.44 | 108.70 | 4.0% |
| **50** | 72.17 | 69.56 | 60.14 | 2.62 | 1.31 | 0.87 | 217.39 | 4.0% |
| **100** | 144.34 | 139.12 | 120.28 | 5.25 | 2.62 | 1.75 | 434.78 | 4.0% |
| **160** | 230.94 | 222.59 | 192.45 | 8.40 | 4.20 | 2.80 | 695.65 | 4.0% |
| **200** | 288.68 | 278.24 | 240.56 | 10.50 | 5.25 | 3.50 | 869.57 | 4.0% |
| **250** | 360.84 | 347.80 | 300.70 | 13.12 | 6.56 | 4.37 | 1086.96 | 4.0% |
| **315** | 454.66 | 438.23 | 378.89 | 16.53 | 8.27 | 5.51 | - | 4.0% |
| **400** | 577.35 | 556.49 | 481.13 | 20.99 | 10.50 | 7.00 | - | 4.0% |
| **500** | 721.69 | 695.60 | 601.41 | 26.24 | 13.12 | 8.75 | - | 4.5% |
| **630** | 909.33 | 876.46 | 757.77 | 33.07 | 16.53 | 11.02 | - | 4.5% |
| **800** | 1154.70 | 1112.96 | 962.25 | 41.99 | 20.99 | 14.00 | - | 5.0% |
| **1000** | 1443.38 | 1391.20 | 1202.81 | 52.49 | 26.24 | 17.50 | - | 5.0% |
| **1250** | 1804.22 | 1739.00 | 1503.52 | 65.61 | 32.80 | 21.87 | - | 5.5% |
| **1600** | 2309.40 | 2225.93 | 1924.50 | 83.98 | 41.99 | 27.99 | - | 6.0% |
| **2000** | 2886.75 | 2782.41 | 2405.63 | 104.97 | 52.49 | 34.99 | - | 6.0% |
| **2500** | 3608.44 | 3478.01 | 3007.03 | 131.22 | 65.61 | 43.74 | - | 6.5% |
| **3150** | 4546.63 | 4382.30 | 3788.86 | 165.33 | 82.67 | 55.11 | - | 7.0% |

---

## 2. Transformer Impedance (%Z) & Prospective Short-Circuit Fault Current

### Short-Circuit Current at Secondary Terminals:
$$I_{sc} = \frac{I_{rated}}{\%Z / 100} = \frac{S_{\text{kVA}} \times 1000}{\sqrt{3} \times V_{\text{sec}} \times (\%Z / 100)}$$

### Short-Circuit Apparent Power ($S_{sc}$):
$$S_{sc} = \frac{S_{rated}}{\%Z / 100} \quad [\text{MVA}]$$

*Example: 1000 kVA, 400V, %Z = 5.0%*
- $I_{rated} = 1443.4\text{ A}$
- $I_{sc} = 1443.4 / 0.05 = 28,868\text{ A} = 28.87\text{ kA}$
- Minimum Secondary Switchgear Breaking Capacity: $\ge 30\text{ kA}$ or $36\text{ kA}$.

---

## 3. Transformer Vector Groups & Phase Shift

| Vector Group | Primary Connection | Secondary Connection | Phase Displacement | Typical Applications |
|---|---|---|---|---|
| **Dyn11** | Delta (D) | Star with Neutral (yn) | +30° (Secondary leads Primary by 30° - 11 o'clock) | Most widely used standard distribution transformer worldwide |
| **Dyn5** | Delta (D) | Star with Neutral (yn) | -150° (Secondary lags Primary by 150° - 5 o'clock) | Distribution networks, paralleling specific grid segments |
| **Yyn0** | Star (Y) | Star with Neutral (yn) | 0° (In-phase - 12 o'clock) | Interconnected systems, auxiliary sub-station feeds |
| **Dd0** | Delta (D) | Delta (d) | 0° (In-phase) | Industrial plants, isolation transformers |
| **YNd11** | Star with Neutral (YN) | Delta (d) | +30° | Generator step-up (GSU) transformers |

---

## 4. Transformer Losses & Efficiency Formula
- **No-Load Loss ($P_0$)**: Constant core hysteresis and eddy-current losses in iron core.
- **Load Loss ($P_k$)**: Variable $I^2R$ copper losses in primary and secondary windings proportional to $(\text{Load ratio})^2$.
- **Efficiency ($\eta$) at load factor $x$ and power factor $\cos\varphi$**:
$$\eta(x) = \frac{x \cdot S_{rated} \cdot \cos\varphi}{x \cdot S_{rated} \cdot \cos\varphi + P_0 + x^2 \cdot P_k}$$
- Maximum efficiency occurs when variable copper loss equals fixed core loss:
$$x_{\eta,\max} = \sqrt{\frac{P_0}{P_k}}$$
