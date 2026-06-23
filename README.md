# Liquid Wallpapers

Premium 4K wallpapers and a background creator for modern Android screens.

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://www.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-Jetpack%20Compose-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Diveno Labs](https://img.shields.io/badge/By-Diveno%20Labs-ff3d00?style=for-the-badge)](https://www.divenolabs.in/)

Liquid Wallpapers is a premium Android wallpaper app built by [Diveno Labs](https://www.divenolabs.in/). It combines Pexels-powered 4K discovery, Diveno Favorites, Daily Mix, mood categories, saved favorites, direct wallpaper application, Text Studio, icon blur, dark-mode dimming, and Liquid Studio for creating custom fluid gradients.

The app is designed for image-first browsing: calm dark surfaces, glass-inspired controls, AMOLED-friendly contrast, and fast Android-first interactions.

## Quick Links

| Resource | Link |
| --- | --- |
| Product page | [divenolabs.in/apps/liquid-wallpapers](https://www.divenolabs.in/apps/liquid-wallpapers) |
| Download | [Google Play](https://play.google.com/store/apps/details?id=com.divenolabs.liquidwall) |
| Changelog | [Liquid Wallpapers changelog](https://www.divenolabs.in/apps/liquid-wallpapers/changelog) |
| Privacy policy | [Liquid Wallpapers Privacy](https://www.divenolabs.in/liquid-wallpapers-privacy-policy.html) |
| Terms of service | [Liquid Wallpapers Terms](https://www.divenolabs.in/liquid-wallpapers-terms.html) |
| Diveno Labs | [www.divenolabs.in](https://www.divenolabs.in/) |
| Support | [care@divenolabs.in](mailto:care@divenolabs.in) |

## Current Version

**v2.7**

- Premium Home redesign with Today's Drop.
- Better wallpaper curation and safer Pexels results.
- Refreshed mood categories and thumbnails.
- Redesigned Profile and About surfaces.
- Real Profile stats for saved wallpapers, Daily Mix state, and cache size.
- New compressed splash screen artwork.
- UI spacing, navigation, and naming fixes across the app.

## Features

### Wallpaper Discovery

- Pexels-powered 4K wallpaper search.
- Diveno Favorites / Founder's Collection loaded from Diveno-hosted metadata.
- Today’s Drop for a stable daily featured wallpaper.
- Fresh Wall feed for high-quality mobile wallpapers.
- Content filters for cleaner, wallpaper-first results.

### Daily Mix

- Swipe through a fresh daily wallpaper deck.
- Swipe right to favorite and left to skip.
- Seen wallpapers are remembered during the day to avoid repeats.
- Profile includes a reset action for another pass.

### Categories

- Mood-based discovery for AMOLED, nature, neon, calm, space, abstract, liquid glass, gradients, flowers, sunsets, coastal scenes, and more.
- Trending categories are surfaced near the top.
- Category search queries are tuned for wallpaper-style results.

### Favorites

- Save wallpapers locally with Room.
- Revisit saved wallpapers in **My Favorites**.
- Open saved items directly in the wallpaper detail flow.

### Liquid Studio

Create custom wallpapers inside the app.

- Liquid gradient presets.
- Blur, dimming, brightness, contrast, and strength controls.
- Text Studio for quote-style wallpapers.
- Export to Gallery.
- Apply to Home screen, Lock screen, or both.

### Profile

- Emoji avatar customization.
- Saved wallpaper count.
- Daily Mix status.
- Image cache size.
- Cache clearing, sharing, major links, and About.

## Design Language

Liquid Wallpapers uses a premium glass-inspired interface:

- Dark, AMOLED-friendly surfaces.
- Orange accent lighting.
- Image-first composition.
- Floating bottom navigation.
- Smooth Compose animations.
- Ghost-fade scrolling behind the navigation bar.

The goal is simple: wallpapers should feel like a curated visual space, not a noisy image grid.

## Privacy And Content

Liquid Wallpapers asks for permissions only around the wallpaper workflow:

- Internet and network state access to fetch wallpapers.
- Set wallpaper permission to apply images directly.
- Storage / photos access where required for saving wallpapers, especially on older Android versions.

Wallpaper images may come from Pexels-powered discovery or Diveno-hosted metadata. Third-party images remain owned by their respective photographers or rights holders.

Read the full policies:

- [Privacy Policy](https://www.divenolabs.in/liquid-wallpapers-privacy-policy.html)
- [Terms of Service](https://www.divenolabs.in/liquid-wallpapers-terms.html)

## Tech Stack

- Kotlin
- Jetpack Compose
- Material 3
- Hilt
- Retrofit
- Coil
- Room
- Pexels API
- Firebase Analytics, Auth, and Firestore dependencies
- Google Play In-App Updates

## Project Structure

```text
app/src/main/java/com/example/liquidwallpapers/
├── data/          # API, Room, repositories, filtering
├── di/            # Hilt modules
├── ui/            # Compose screens, components, theme
└── util/          # Image and bitmap helpers
```

## Build

Add your Pexels API key to `local.properties`:

```properties
PEXELS_API_KEY=your_key_here
```

Then build:

```powershell
.\gradlew.bat :app:assembleDebug
```

## Brand

Liquid Wallpapers is a [Diveno Labs](https://www.divenolabs.in/) product from Jaipur, Rajasthan.

Diveno Labs builds Android apps, practical AI tools, and polished digital products with a focus on speed, clarity, privacy, and design.
