# FarmTrace — Smart Farm-to-Table Tracking System & Live Market Simulation

> **Every Vegetable Has a Story.**  
> *From Farm to Table. Every Step Matters.*

FarmTrace is an agricultural traceability platform engineered with Android, Jetpack Compose, Material Design 3, Room local persistence, ZXing QR-code generation, and an interactive 3D supply-chain and live marketplace simulation.

---

## 🌟 What's New: Cinematic Entrance & Live Market Simulation

### 1. Animated Entrance Logo (Cinematic Splash Screen)
- **4-Phase Sequence**:
  - **Phase 1**: Dark introduction with forest-green gradient and drifting agricultural background.
  - **Phase 2**: Glowing seed appears in the center and grows into an organic sprout leaf with a circular sweep arc.
  - **Phase 3**: Typewriter letter-by-letter reveal of **FarmTrace** with tagline *"EVERY VEGETABLE HAS A STORY."* and *"From Farm to Your Table"*.
  - **Phase 4**: Smooth fade into the home screen with floating decorative vegetable icons (Tomato, Carrot, Brinjal, Chilli).
- **Control**: Includes a dedicated **"Skip"** button, respects reduced motion, and can be replayed anytime from the About screen.

### 2. Global "Data Mode" Switch (`Original | Demo`)
- **Header-Level Segmented Control**: Visible across every screen to instantly switch between **Original** mode and **Demo** mode.
- **Strict Data Separation**:
  - **Original Mode (Default)**: Shows strictly real user-registered batches and verified journey events. Simulated customers and fake market activity are completely disabled. Features a green **"● LIVE DATA"** badge.
  - **Demo Mode**: Loads pre-configured sample vegetable batches (Tomatoes, Potatoes, Onions, Carrots), starts the simulated customer shopping engine, and features an amber indicator banner: *"⚠️ DEMO MODE: Viewing simulated sample batches & live shoppers"*.
- **Safety Checks**:
  - When in Demo mode, attempting to register a batch shows a prompt asking whether to switch to Original mode to save real data.
  - Dedicated **"Reset Demo Data"** restores sample batches and inventory without touching real user records.

### 3. Simulated Customers Buying Registered Vegetables
- **12 Fictional Shoppers**: First names only (Priya, Arun, Meena, Karthik, Fatima, Ravi, Ananya, Kavin, Arjun, Divya, Rahul, Sneha).
- **Autonomous Shopping Engine**: Every 4–7 seconds, a random customer purchases 1–4 kg from an active batch with available stock.
- **Inventory & Sold Out States**: Deducts from remaining demo stock; when stock reaches zero, the batch automatically updates to **"Sold Out"**.
- **Live Activity Feed**: Sliding cards showing buyer name, produce, batch ID, shop location, price (₹), and timestamp.
- **Top Toast Notifications**: Animated banner sliding in from top: *"🛒 Demo Purchase: Priya bought 2 kg Tomato (Batch FT-2026-000123) · ₹70"*.

### 4. Interactive Vegetable Marketplace & Sales Overview
- **Sales Overview Cards**: Tracks Total Simulated Sales (₹), Volume Sold (kg), and Unique Shoppers, with a produce sales distribution visualizer.
- **"Simulate Buy" User Interaction**: Allows users to select any batch in the marketplace, choose a quantity slider (1–10 kg), test inventory deduction, and receive instant purchase confirmation!
- **Simulation Controls**: One-tap toggle to Start or Pause the background shopping engine, Clear simulated purchase logs, and Reset demo inventory.

---

## 🛠️ Core Traceability Features

1. **3D Interactive Miniature Farm**: 5-stage interactive isometric map (Farm, Mandi, Cold Logistics Truck, Supermarket, Kitchen Table) with physics-based floating vegetables, animated truck, and day/dusk atmospheric lighting.
2. **High-Contrast QR Code Engine**: Generates ISO standard QR code bitmaps via ZXing `QRCodeWriter` and formatted printable crate labels with farm origin details.
3. **QR Scanner**: Smartphone viewfinder frame with an animated laser scan line, quick-test sample batch scan buttons, and manual batch lookup.
4. **Public Shopper Portal**: Detailed produce cards, freshness indicators, and vertical chronological timeline with cold chain telemetry.
5. **Farmer Dashboard**: Metrics, filterable batches, Add Journey Stop dialog, and one-tap CSV export.

---

## 🧪 Testing & Verification

```bash
# Verify build and compile APK
compile_applet

# Run unit and Robolectric tests
gradle :app:testDebugUnitTest
```
