# Design Specification

Design tokens, components, and layout architecture extracted from mobile analytics dashboard mockups in [`./design/`](file:///Users/aswinharidas/Development/personal/playground/callerinfo/design/).

---

## 1. Typography

- **Font Family**: `Urbanist`, sans-serif
- **Weights**:
  - Regular (`400`)
  - Medium (`500`)
  - Semi-Bold (`600`)
  - Bold (`700`)

### Type Scale

| Role | Size | Weight | Tracking / Line Height | Usage Example |
| :--- | :--- | :--- | :--- | :--- |
| **Heading 01** | `48px` | Semi-Bold (`600`) | Tight (`-0.02em`), `1.15` | Hero numbers, display headers |
| **Heading 02** | `32px`–`36px` | Medium (`500`) / Bold (`700`) | Normal, `1.2` | Screen titles ("Hi Miranda..."), key section headers |
| **Sub-Heading** | `24px` | Medium (`500`) | Normal, `1.3` | Card headers, primary metric values |
| **Body Large** | `20px` | Regular (`400`) | Normal, `1.4` | Featured metric labels, secondary titles |
| **Body Regular** | `16px` | Regular (`400`) / Medium (`500`) | Normal, `1.5` | Standard card text, descriptions, table items |
| **Caption / Small** | `12px`–`14px` | Medium (`500`) | Normal, `1.4` | Delta trends (`+34 today`), timestamps, chart axis labels |

---

## 2. Color Palette

### Brand & Accents

| Token | Hex | Preview / Description | Usage |
| :--- | :--- | :--- | :--- |
| `primary` | `#E7FE55` | Vibrant Chartreuse / Electric Lime | Primary CTA, highlight accents, chart active line |
| `vanilla` | `#EFF0A3` | Pastel Yellow | Featured callout banner, metric highlight card |
| `honeydew` | `#CFDECA` | Soft Pastel Sage / Green | Positive metric card, growth indicators |
| `alice-blue` | `#D8DFE9` | Soft Muted Periwinkle | Informational card variant, secondary accents |

### Neutrals & Surfaces

| Token | Hex | Preview / Description | Usage |
| :--- | :--- | :--- | :--- |
| `surface-dark` | `#212121` | Eerie Black | Primary text, active tab pills, dark circular icon buttons |
| `surface-card` | `#FFFFFF` | Pure White | Default card background, input fields, pill outlines |
| `background` | `#F6F5FA` | Ghost White / Soft Lavender-Grey | Screen scaffold background, input background |
| `muted-text` | `#8E8E93` | Neutral Gray | Subtitles, inactive tab labels, secondary stat labels |
| `border-subtle` | `#E8E8EE` | Light Gray Divider | Card borders, subtle divider lines |

---

## 3. Elevation & Radius

- **Corner Radii**:
  - `radius-full`: `9999px` (Filter pills, round action buttons, avatar badges)
  - `radius-card`: `24px` (Main metric cards, chart containers, promo banners)
  - `radius-sm`: `12px` (Internal pill badges, sub-segment blocks)
- **Shadows & Borders**:
  - Flat design language: relies on surface contrast (`#FFFFFF` on `#F6F5FA`) rather than heavy drop shadows.
  - Optional subtle card shadow: `0px 2px 8px rgba(0, 0, 0, 0.04)`.
  - Border: `1px solid #E8E8EE` on uncolored white cards.

---

## 4. Component Library & Patterns

### 4.1 Top App Bar
- **Leading**: Circular button (`40x40px`, `#FFFFFF`, icon `Menu` / hamburger).
- **Trailing Action Group**:
  - Circular message icon button (`40x40px`, `#FFFFFF`).
  - Circular notification bell icon button (`40x40px`, `#FFFFFF`, with optional unread indicator dot).

### 4.2 Greeting & Screen Header
- **Layout**: Left-aligned, vertical stack.
- **Title**: "Hi [Name], here's what's happening in your store." (`Heading 02`, `#212121`).

### 4.3 Segmented Filter Pills (Time Range)
- **Items**: `Today`, `This Week`, `This Month`, `This Year`.
- **States**:
  - `Active`: Solid `#212121`, text `#FFFFFF`, rounded `9999px`, padding `8px 16px`.
  - `Inactive`: Background `#FFFFFF` or transparent with `1px solid #E8E8EE`, text `#8E8E93`.

### 4.4 Multi-Segment Progress Card (Orders Breakdown)
- **Header**: Primary count (e.g. `Orders 34,750 /month`), sub-badge `+312 today`.
- **Progress Bar**: Horizontal composite bar split proportionally into 4 states:
  - *Completed*: Gradient / Dark fill
  - *On Delivery*: Solid gray fill
  - *Pending*: Light dotted pattern
  - *Unpaid*: Diagonal hatched / striped pattern
- **Legend / Values**: 4-column breakdown beneath the bar with quantity and status label.

### 4.5 Action / Banner Card
- **Background**: Soft Vanilla (`#EFF0A3`).
- **Radius**: `24px`.
- **Content**: 2-line prompt text ("Analyze total sales over last year. Compare revenue, orders and refunded.") + trailing circular dark action button (`40x40px`, `#212121`) with right arrow (`→`).

### 4.6 2x2 Metric Grid Cards
- **Grid Layout**: 2 columns, `12px`–`16px` gap.
- **Card Anatomy**:
  - Trailing top action: Diagonal arrow (`↗`) indicating drill-down.
  - Main metric: `Heading 02` or `Sub-Heading` (e.g., `12,089`, `$231,224`, `34,750`, `$11,345`).
  - Metric label: `Body Regular` (e.g., `Visitors`, `Total Sales`, `Total orders`, `Refunded`).
  - Trend delta: `Caption` (e.g., `+34 today`, `+$1877 today`).
- **Variants**:
  - Mint / Honeydew (`#CFDECA`)
  - Vanilla (`#EFF0A3`)
  - Ghost White (`#FFFFFF` with subtle border)

### 4.7 Chart Card (Revenue vs Orders)
- **Header**:
  - Title: "Revenue vs Orders"
  - Interactive value badge: Pill with active value (e.g., `$231,224`, `#EFF0A3` or `#E7FE55`).
  - Legend: Colored circular indicators (Yellow = Revenue, Dark = Orders).
- **Chart Body**:
  - Dual line representation: Smooth spline for Revenue, dashed stroke for Orders.
  - X-Axis: Month abbreviations (`Jan`, `Feb`, `Mar`, `Apr`, `May`, `Jun`).

### 4.8 Secondary Metric Rows / Cards
- **Card Anatomy**:
  - Circular icon leading badge (Black circle with icon or accent graphic).
  - Stat value (e.g., `$421.11`, `3.8`).
  - Stat description (e.g., `Average Sale Value`, `Average Item per Sale`).
  - Comparison text (e.g., `$12.90 less than last month`, `0.23 less than last month`).

---

## 5. Layout & Spacing Tokens

- **Screen Padding**: Horizontal `20px`, Top `16px`, Bottom `32px`.
- **Stack Spacing**:
  - Section-to-section: `24px`.
  - Item-to-item within section: `12px`–`16px`.
  - Inner card padding: `16px`–`20px`.
