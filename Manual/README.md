# PowerCalc Engineering Reference & Manuals Repository

Welcome to the **PowerCalc Manual Folder**. This directory contains the complete technical reference data extracted, categorized, and organized from reference images and videos.

## Directory Structure

```
/Manual/
├── Images/
│   ├── Motor_Data/             # Motor ratings, FLC, Star-Delta bridging & Power Factor Correction
│   ├── Transformer_Data/       # Transformer kVA tables, %Z impedance, short circuit & vector groups
│   └── General_Data/           # Ohm's law circle, voltage drop, cable ampacity, color codes & IP ratings
├── Videos/                     # Field video demonstration procedures, tests & inspection steps
├── Motor_Data.md               # Master technical reference manual for electric motors
├── Transformer_Data.md         # Master technical reference manual for power & distribution transformers
├── General_Data.md             # Master technical reference manual for general electrical engineering
├── Videos_Manual.md            # Comprehensive guide to the 11 practical field demonstration videos
└── README.md                   # This directory index and guide
```

## Data Categorization Summary

1. **Motor Data**:
   - 3-Phase Induction motor FLC from 0.37 kW to 200 kW across 220V, 380V, 400V, 415V, 460V, 690V.
   - Terminal bridge configurations: Star (horizontal W2-U2-V2) vs Delta (vertical U1-W2, V1-U2, W1-V2).
   - Reactive power compensation ($Q_c$) calculation and Delta/Star capacitor bank sizing in $\mu\text{F}$.
   - Overload relay readjustment safety criteria ($I_{corr} = I_{orig} \cdot PF_1 / PF_2$).

2. **Transformer Data**:
   - Single-phase and three-phase full load currents from 15 kVA to 3150 kVA.
   - Prospective secondary short-circuit current ($I_{sc} = I_n / \%Z$).
   - Vector groups (Dyn11, Dyn5, Yyn0, Dd0, YNd11) and phase displacement diagrams.
   - Efficiency and loss formulas (no-load core losses $P_0$ and load copper losses $P_k$).

3. **General Data**:
   - Complete Ohm's law and AC/DC power calculation matrix.
   - Conductor voltage drop formulas and permissible drop percentages (IEC 60364 & NEC).
   - Copper conductor current-carrying capacities (Methods B1, B2, C, D, E).
   - International conductor color coding standards (IEC vs NEC 120/208V & 277/480V).
   - Ingress Protection (IP) code reference table (IEC 60529).

4. **Videos**:
   - Transcripts, step-by-step procedures, and standards for the 11 uploaded demonstration videos covering Star-Delta starting, Megger insulation testing, TTR verification, tap changers, multimeter measurements, phase rotation, ground resistance, and thermal imaging.
