---
name: Cyber-Block Tactical
colors:
  surface: '#0f131f'
  surface-dim: '#0f131f'
  surface-bright: '#353947'
  surface-container-lowest: '#0a0e1a'
  surface-container-low: '#171b28'
  surface-container: '#1b1f2c'
  surface-container-high: '#262937'
  surface-container-highest: '#313442'
  on-surface: '#dfe1f4'
  on-surface-variant: '#b9cacb'
  inverse-surface: '#dfe1f4'
  inverse-on-surface: '#2c303e'
  outline: '#849495'
  outline-variant: '#3b494b'
  surface-tint: '#00dbe9'
  primary: '#dbfcff'
  on-primary: '#00363a'
  primary-container: '#00f0ff'
  on-primary-container: '#006970'
  inverse-primary: '#006970'
  secondary: '#adc6ff'
  on-secondary: '#002e6a'
  secondary-container: '#0566d9'
  on-secondary-container: '#e6ecff'
  tertiary: '#f5f5f5'
  on-tertiary: '#2f3131'
  tertiary-container: '#d9d9d9'
  on-tertiary-container: '#5d5f5f'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#7df4ff'
  primary-fixed-dim: '#00dbe9'
  on-primary-fixed: '#002022'
  on-primary-fixed-variant: '#004f54'
  secondary-fixed: '#d8e2ff'
  secondary-fixed-dim: '#adc6ff'
  on-secondary-fixed: '#001a42'
  on-secondary-fixed-variant: '#004395'
  tertiary-fixed: '#e2e2e2'
  tertiary-fixed-dim: '#c6c6c7'
  on-tertiary-fixed: '#1a1c1c'
  on-tertiary-fixed-variant: '#454747'
  background: '#0f131f'
  on-background: '#dfe1f4'
  surface-variant: '#313442'
typography:
  headline-xl:
    fontFamily: Space Grotesk
    fontSize: 48px
    fontWeight: '700'
    lineHeight: 52px
    letterSpacing: -0.04em
  headline-xl-mobile:
    fontFamily: Space Grotesk
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 36px
    letterSpacing: -0.03em
  headline-lg:
    fontFamily: Space Grotesk
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 38px
    letterSpacing: -0.02em
  headline-lg-mobile:
    fontFamily: Space Grotesk
    fontSize: 24px
    fontWeight: '700'
    lineHeight: 28px
    letterSpacing: -0.02em
  headline-md:
    fontFamily: Space Grotesk
    fontSize: 22px
    fontWeight: '600'
    lineHeight: 26px
    letterSpacing: -0.01em
  body-lg:
    fontFamily: Space Grotesk
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
    letterSpacing: 0em
  body-md:
    fontFamily: Space Grotesk
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
    letterSpacing: 0em
  label-lg:
    fontFamily: Space Mono
    fontSize: 13px
    fontWeight: '700'
    lineHeight: 16px
    letterSpacing: 0.06em
  label-md:
    fontFamily: Space Mono
    fontSize: 11px
    fontWeight: '500'
    lineHeight: 14px
    letterSpacing: 0.08em
  label-sm:
    fontFamily: Space Mono
    fontSize: 9px
    fontWeight: '500'
    lineHeight: 12px
    letterSpacing: 0.1em
spacing:
  gutter: 0.75rem
  gutter-desktop: 1rem
  margin: 1rem
  margin-desktop: 2rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 0.75rem
  space-lg: 1.25rem
  space-xl: 2rem
---

## Brand & Style

This design system draws direct inspiration from cult-classic enthusiast Android interfaces, minimalist hardware dashboards, and dot-matrix hardware widgets. It rejects sterile corporate minimalism and generic glass effects in favor of an unapologetic, tactical, and industrial cyber-block aesthetic. 

The visual voice is precise, engineered, and uncompromising:
- **Tone:** Methodical, enthusiast-driven, diagnostic, raw yet hyper-refined.
- **Visual Style:** Cyber-Brutalist meets Hardware Utility. Monolithic geometric surfaces, structured block containers, tactile inset/outset borders, and discrete dot-matrix LED textures.
- **Emotional Response:** Empowers the user with a sense of low-level control, computational density, hardware-native feedback, and zero telemetry fluff.

## Colors

The palette operates in a deeply immersive nocturnal spectrum. Deep abyssal foundations contrast against surgical neon signals to deliver instant visual hierarchy.

- **Base Canvas:** Gradients transitioning strictly along vertical axes from Midnight Blue (`#050814`) through Cold Sub-surface (`#0B1021`) down to Absolute Obsidian (`#020308`). Never use flat washed grays.
- **Primary Accent (`#00F0FF` - Electric Cyan):** Reserved for active telemetry, primary actions, focused interactive strokes, and high-frequency toggles.
- **Secondary Accent (`#3B82F6` - Vivid Cobalt):** Serves auxiliary statuses, sub-system groupings, and mid-priority data highlights.
- **Tactical High-White (`#FFFFFF` - Crisp Milk White):** Primary optical focus for headers, status counters, and active binary readouts.
- **Structural Grays:**
  - Border Inactive: `#1A233D`
  - Subtle Muted Text: `#5F759E`
  - Surface Inset: `#080D1D`
  - Matrix Mesh Grid: `rgba(0, 240, 255, 0.04)`

Use stark binary contrast: elements are either definitively unlit (recessed, dark midnight) or energized (stark white / cyan).

## Typography

The typography leverages an engineered pairing: **Space Grotesk** handles high-impact proportional headers and reading layers, while **Space Mono** locks metadata, values, telemetry, and index keys to a strict mechanical grid.

- **Capitalization:** All label variants (`label-lg`, `label-md`, `label-sm`) default to uppercase styling with expanded letter spacing to replicate industrial stamped placards.
- **Numbers & Metrics:** Must consistently invoke `Space Mono` for strict tabular alignment across lists and dashboard widgets.
- **Hierarchical Rules:** Never combine more than two font sizes within a single block widget. Hierarchy is created through color luminance (`#FFFFFF` to `#5F759E`) and weight, rather than excessive scale variance.

## Layout & Spacing

Layouts follow a modular, chunky cyber-brick tiling system inspired by physical hardware components and power-user widget matrices.

- **Grid Architecture:** 
  - **Mobile:** 4-column module grid with compact 12px (`0.75rem`) gutters and 16px (`1rem`) screen margins.
  - **Tablet/Desktop:** 8 to 12-column dynamic matrix utilizing 16px (`1rem`) gutters and 32px (`2rem`) margins. Content snaps into strict integer block proportions (e.g., 1x1, 2x1, 2x2 modular blocks).
- **Rhythm & Alignment:** Spacing adheres strictly to a 4px/8px mechanical increment. Zero soft offsets or fluid centered gaps. Everything aligns flush to parent bounding tracks.
- **Surface Denseness:** Containers are tightly spaced to convey informational density without clutter. Empty space is treated as disabled or unallocated sector memory.

## Elevation & Depth

This design system avoids soft organic blurs and floaty diffused drop shadows. Depth is communicated strictly via **Tactile Tiering, Step Borders, and Hard Offset Shadows**.

1. **Surface Layers:**
   - **Tier 0 (Root Base):** Radial-angled gradient `#050814` to `#020308`.
   - **Tier 1 (Modular Tiles):** Solid `#0B1021` enclosed in a 1px or 2px outline of `#1A233D`.
   - **Tier 2 (Interactive Floating Modules):** Solid `#0E152B` with 2px hard outset borders.
2. **Hard Pixel Shadow:**
   - Elements elevate by throwing sharp, 0-blur displacement shadows: `2px 2px 0px 0px #000000` or `3px 3px 0px 0px #00F0FF` for selected states.
3. **Dot-Matrix Textures:**
   - Backgrounds of elevated widgets may feature a repeating micro dot-matrix background pattern (1px dots spaced 6px apart in `rgba(0, 240, 255, 0.05)`) mimicking LED panel backplanes.
4. **Border Insets:**
   - De-emphasized/input regions use an inverted border style: a top-left stroke of `#03050A` and bottom-right stroke of `#1E2845` to simulate stamped, physical recessed bays.

## Shapes

The shape system is unapologetically **Sharp (0)**. 

- **Corner Radii:** Strictly `0px` across all containers, badges, inputs, buttons, and popovers. No pill shapes; no fluid rounded aesthetics.
- **Chamfers & Cut Corners:** Tactical modules may feature 45-degree corner notches (`clip-path: polygon(...)` 6px cuts) at the top-right corner to denote index tabs or interactive trigger anchors.
- **Strokes:** Borders are uniform 1px or 2px thick, high-density vectors without anti-aliased feathering.

## Components

### Buttons
- **Primary Block Action:** Sharp 0px box, solid `#00F0FF` fill with stark `#020308` bold `Space Grotesk` uppercase text. On press: shifts 2px diagonally downward with hard black shadow snapping to 0px.
- **Secondary Ghost Action:** Transparent background with a 1.5px `#1A233D` border, `#FFFFFF` text. On hover/focus: border transitions instantly to `#00F0FF` with a subtle corner dot marker.

### Chips & Badges
- **Tactical Tag:** Micro rectangular tags with `label-sm` in `Space Mono`. 
- **Style:** Background `#080D1D`, 1px solid `#1A233D`. Status tags feature a 4x4px pure square LED pixel dot indicator (active `#00F0FF`, standby `#3B82F6`, alert `#FF0055`).

### Cards & Modular Tiles
- **Widget Tile:** Deep obsidian surface (`#0B1021`), 1px outline (`#1A233D`). 
- **Header Strip:** Features a dedicated top partition bar with a mono index tracker (e.g., `// SEC_01`) and dot-matrix background fill.
- **Interactive State:** Hovering switches the perimeter stroke to `#3B82F6` and illuminates corner ticks.

### Input Fields
- **Data Cell Input:** Recessed container (`#080D1D`) with an inset sharp border. Monospace typography in crisp `#FFFFFF`.
- **Focus State:** 1px solid `#00F0FF` perimeter with an instant non-blinking vertical block cursor (`█`).

### Checkboxes & Binary Switches
- **Checkbox:** 14x14px sharp square. Unchecked: 1px `#1A233D` outline. Checked: Solid `#00F0FF` fill with a centered `#020308` 6x6px solid square core.
- **Toggle Unit:** Segmented dual-block chassis. An enclosed track containing two equal sharp blocks (`OFF` / `ON`). The active side snaps to `#00F0FF` with `#000000` text, while the inactive remains `#0B1021` with `#5F759E` text.

### Segmented Sliders & Telemetry Bars
- **Progress Gauge:** Built as a chained sequence of discrete 3px wide rectangular micro-blocks separated by 1px gaps, simulating LED diagnostic displays rather than continuous smooth bars.