# All-in-One Levitating Studio Pod, Ibis Paint Color Studio & Multi-Ecosystem Tool Integration

An architectural plan to transform AetherStudio Elite into an all-in-one creative workstation. Rather than fragmenting controls across separated modes or tabs, all tools and controls from **Ibis Paint, Adobe Photoshop, Figma, and Canva** are unified into a single levitating rounded-squircle control pod with a top horizontal section and a vertical levitating left dock in a Cyberpunk Glassmorphism aesthetic with neon cyan and violet glows.

## User Review & Critical Decisions

> [!IMPORTANT]
> The design below incorporates your specific instructions:
> 1. **No Separated Modes (All Tools Unified in One Frame)**: Every control from Ibis Paint, Photoshop, Figma, and Canva is directly accessible in a single integrated studio interface with quick-action clusters and smooth flyout shelves, eliminating mode switching.
> 2. **Levitating Rounded-Squircle Control Pod (`#studio-unified-pod`)**:
>    - **Top Horizontal Section**: Houses document control, Undo/Redo, Canvas Zoom/100%/Rotate, Symmetry axes (Mirror X, Mirror Y, Radial), Rulers/Grid, Gemini AI Vision diagnostics, and Publish/Export (SVG, PSD, TIFF).
>    - **Left Vertical Levitating Dock**: Houses all primary creative tools in a unified vertical squircle pod:
>      - *Ibis Paint*: Brush engine (Dip Pen, Felt Tip, Watercolor, Airbrush, Manga Screentone), Catmull-Rom stabilizer (0-10), inside/outside smart lineart containment.
>      - *Adobe Photoshop*: Smudge, Blur, Dodge, Burn, Clone Stamp, Magic Wand, Lasso, Curves & Adjustment filters.
>      - *Figma*: Bezier Pen, Anchor Node edit, Vector shapes, Alignment/Distribution matrix, typography controls.
>      - *Canva*: Graphic element stamps, gradient mesh backgrounds, layout badges, and frame overlays.
> 3. **Ibis Paint Color Studio Pod**:
>    - Dual-picker interface featuring both a **circular 360° HSB color wheel** and a **square Saturation/Brightness gradient box**.
>    - Dedicated **Transparency (Opacity) Slider from 0% to 100%** with a live alpha checkerboard preview and percentage readout.
>    - Quick swatch bank with custom color pins and standard JSON palette import/export.
>    - Real-time circular zoom loupe eyedropper for sampling colors directly from canvas pixels.
> 4. **Aesthetic Direction**: **Cyberpunk Glassmorphism** with deep OLED blacks, multi-layer blur filters (`backdrop-filter: blur(24px)`), and glowing neon cyan (`#06b6d4`) and electric violet (`#8b5cf6`) interactive borders.

---

## 1. Overview & Core Concept

- **What It Does**: Provides professional artists and designers with the ultimate creative toolset in a single unified canvas frame. Instead of hunting through separate app views or menus, artists have a sleek, levitating glassmorphic squircle pod where drawing, photo retouching, vector manipulation, and element templating exist harmoniously.
- **Target Audience**: Digital painters, manga/anime illustrators, UI/UX concept designers, and social graphic creators wanting desktop-grade depth with touch-first ergonomics.
- **Key Value**: Zero context switching. Color inking with Ibis Paint stabilization, retouch lineart with Photoshop smudge/dodge, draw vector paths with Figma pen bezier nodes, and drop Canva layout shapes without ever leaving the canvas.

---

## 2. User Experience & Visual Design

### A. Levitating Rounded-Squircle Pod Architecture
- **Glassmorphic Surface**: Frosted dark glass container with `background: rgba(10, 12, 22, 0.82)`, `backdrop-filter: blur(28px)`, border `1px solid rgba(6, 182, 212, 0.28)`, and dual neon drop-shadows `0 12px 40px rgba(6, 182, 212, 0.22), 0 0 25px rgba(139, 92, 246, 0.18)`.
- **Top Horizontal Squircle Section**:
  - **Doc Header & Status**: Project title pill with real-time auto-save indicator.
  - **History**: Rapid Undo, Redo, and undo step counter.
  - **Transform & Navigation**: 1-tap Canvas Zoom In (+), Zoom Out (-), Fit-to-Screen (100%), and 0° Rotation Reset.
  - **Precision Guides**: Symmetry selector (Off, Horizontal X, Vertical Y, Radial 8-way kaleidoscope), Snapping Pixel Grid toggle, and Canvas Rulers.
  - **AI Studio & Export**: Gemini AI Canvas Coach button and Multi-Format Publishing button (SVG, PSD, TIFF, PNG).

### B. Vertical Levitating Left Dock (Unified Multi-App Tool Suite)
A single vertical squircle dock organizing all creative powerhouses into immediately clickable tool clusters:
1. **Ibis Paint Drawing Suite**:
   - Brush Tool with expandable brush shelf: *Dip Pen (Hard/Soft)*, *Felt Tip Pen*, *Airbrush*, *Watercolor Brush*, *Neon Glow Pen*, and *Manga Screen Tone*.
   - Stabilizer Slider (0 to 10) with real-time Catmull-Rom exponential stroke dampening.
   - Smart Lineart Containment: Toggle between *Off*, *Inside Contours*, and *Outside Lineart*.
2. **Adobe Photoshop Retouch & Adjustment Suite**:
   - *Smudge Tool* (blends adjacent pixels smoothly with pressure sensitivity).
   - *Blur Tool* (softens hard stroke edges).
   - *Dodge Tool* (lightens highlights) & *Burn Tool* (deepens shadows).
   - *Lasso & Marquee Selection* with floating marching ants indicator.
   - *Adjustment Matrix*: 1-tap Curves, Brightness/Contrast, Hue/Saturation, and Gaussian Blur filters.
3. **Figma Vector & Layout Suite**:
   - *Bezier Pen Tool* with interactive anchor points and curve handles.
   - *Geometric Shapes*: Rectangle, Rounded Squircle, Ellipse, Polygon, Star, Arrow.
   - *Alignment & Distribution Bar*: Align Left, Center, Right, Top, Middle, Bottom, Distribute Horizontally/Vertically.
   - *Typography Hub*: Font selection, font size, tracking/kerning, and text effects.
4. **Canva Asset & Styling Suite**:
   - *Graphic Stamps*: Badges, geometric stickers, starbursts, framing ribbons.
   - *Gradient Mesh*: Background gradient generator with multi-stop radial and linear presets.
   - *Template Layouts*: Dribbble Shot, YouTube Banner, Instagram Square, Mobile Wallpaper presets.

### C. Authentic Ibis Paint Color Studio Pod
- **Dual HSB Picker**:
  - Outer 360° chromatic hue ring with draggable glowing thumb.
  - Inner square saturation/brightness gradient with high-precision reticle.
- **Transparency Slider (0% - 100%)**:
  - Live alpha slider with checkered transparency backdrop showing exact opacity level in real-time.
  - Synchronized with brush flow and layer opacity.
- **Hex & RGB Readouts**: Editable HEX input, HSV values, and quick color chip bank.
- **Eyedropper Loupe**: Circular magnifier showing 9x magnified canvas pixels with crosshairs and instant color lock.

---

## 3. Technical Architecture & Component Mapping

```
┌────────────────────────────────────────────────────────────────────────┐
│             Top Horizontal Squircle Pod (#studio-top-pod)              │
│ ┌───────────────┐ ┌───────────────┐ ┌──────────────┐ ┌───────────────┐ │
│ │ History & Nav │ │ Symmetry/Grid │ │ AI Vision    │ │ Publish/Export│ │
│ │ Undo/Redo/Zoom│ │ Off/X/Y/Radial│ │ Gemini Coach │ │ SVG,PSD,TIFF  │ │
│ └───────────────┘ └───────────────┘ └──────────────┘ └───────────────┘ │
└────────────────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────────────────┐
│            Left Vertical Levitating Dock (#studio-left-dock)           │
├────────────────────────────────────────────────────────────────────────┤
│ ┌────────────────────────────────────────────────────────────────────┐ │
│ │ 🎨 Ibis Paint Inking Cluster                                       │ │
│ │ • Dip Pen • Felt Tip • Airbrush • Watercolor • Glow • Screentone   │ │
│ │ • Stabilizer (0-10) • Lineart Inside/Outside Containment           │ │
│ └────────────────────────────────────────────────────────────────────┘ │
│ ┌────────────────────────────────────────────────────────────────────┐ │
│ │ 🖌️ Photoshop Retouch Cluster                                       │ │
│ │ • Smudge • Blur • Dodge • Burn • Lasso • Image Adjustment Filters  │ │
│ └────────────────────────────────────────────────────────────────────┘ │
│ ┌────────────────────────────────────────────────────────────────────┐ │
│ │ 📐 Figma Vector & Layout Cluster                                   │ │
│ │ • Bezier Pen • Node Edit • Shapes • Align/Distribute • Typography  │ │
│ └────────────────────────────────────────────────────────────────────┘ │
│ ┌────────────────────────────────────────────────────────────────────┐ │
│ │ ✨ Canva Assets & Mesh Cluster                                     │ │
│ │ • Graphic Stamps • Gradient Mesh Presets • Frame Overlays          │ │
│ └────────────────────────────────────────────────────────────────────┘ │
│ ┌────────────────────────────────────────────────────────────────────┐ │
│ │ 🌈 Ibis Paint Color Studio Pod                                     │ │
│ │ • Circular 360° Hue Wheel & Square Saturation/Brightness Box       │ │
│ │ • 0% - 100% Alpha Transparency Slider • Swatch Bank • Loupe        │ │
│ └────────────────────────────────────────────────────────────────────┘ │
└────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                     Multi-Layer Canvas Workspace                       │
│  • Layer Stack with Opacity & 12 Blend Modes (Hover/Scrub Preview)     │
│  • Scoped Storage Publishing Bridge (Java/Kotlin Native)               │
└────────────────────────────────────────────────────────────────────────┘
```

---

## 4. Key Implementation Steps & Verification

1. **CSS Glassmorphism & Cyberpunk Neon System**:
   - Implement `.studio-unified-pod`, `.studio-top-pod`, and `.studio-left-dock` with neon cyan/violet glow shadows and rounded squircle borders (`border-radius: 20px`).
2. **Ibis Paint Color Studio Engine**:
   - Implement circular Canvas-based HSB color wheel and square Saturation/Brightness Canvas picker.
   - Build horizontal transparency slider from 0% to 100% with CSS checkerboard pattern.
   - Connect active color changes directly to brush stroke and swatch bank.
3. **Integrated Multi-Tool Canvas Handlers**:
   - Connect all drawing, retouching (smudge/blur/dodge/burn), vector, and element stamp functions into the active pointer down/move/up handlers.
4. **Validation & Testing**:
   - Run Node.js JavaScript syntax verification on `index.html`.
   - Run Robolectric unit tests via `gradle :app:testDebugUnitTest`.
   - Compile application via `compile_applet`.
