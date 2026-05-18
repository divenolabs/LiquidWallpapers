# Liquid Wallpapers

Liquid Wallpapers is a premium Android wallpaper app and background creator built by Diveno Labs. It combines Pexels-powered wallpaper discovery, a curated Diveno Favorites collection, Daily Mix, mood-based categories, saved favorites, and a built-in Studio for creating custom liquid gradient wallpapers.

The app is designed for modern Android screens, including AMOLED displays, with a glassmorphic interface, dark visual language, direct Home and Lock screen application, and careful attention to smoothness, privacy, and battery efficiency.

[Download on Google Play](https://play.google.com/store/apps/details?id=com.divenolabs.liquidwall&hl=en_IN)

## Highlights

- Premium wallpaper discovery powered by Pexels search and curated feeds.
- Diveno Favorites / Founder's Collection loaded from Diveno-hosted metadata.
- Daily Mix swipe deck for a fresh set of wallpapers each day.
- Mood-based categories including Abstract Art, Amoled Dark, Deep Space, Cyberpunk, Nature HD, Minimalist, Aesthetics, Automotive, and Mountains.
- Favorites powered by a local Room database.
- Detail view with zoom, pan, favorite, edit, download, text tools, and wallpaper application.
- Liquid Studio for generating custom liquid gradient wallpapers.
- Profile hub with emoji avatars, favorites, cache clearing, sharing, Daily Mix reset, and app information.

## Studio

Liquid Studio turns the phone into a creative wallpaper suite. Users can generate custom fluid gradient backgrounds, tune the result, and export it without leaving the app.

Studio features include:

- Vibe presets such as Midnight, Cyberpunk, Aurora, Ember, Ocean, Galaxy, Neon, Sakura, Storm, and more.
- Blur, dim, brightness, and contrast controls.
- Gallery export.
- Direct Home screen and Lock screen application.
- Text Studio support for custom quotes, multi-line text, alignment control, and sharp exports.

## Daily Mix And Discovery

Liquid Wallpapers is built for repeat discovery rather than endless grid scrolling.

- Explore starts with Diveno Favorites and a Pexels-powered wallpaper feed.
- Search fetches high-quality mobile wallpaper results from Pexels.
- Daily Mix uses day-based wallpaper selection so each day feels fresh.
- Seen wallpapers are remembered for the day to avoid repeat cards.
- Swipe right to favorite and swipe left to skip.
- Profile includes a Daily Mix reset action for another pass.

## Design Language

The app uses a premium glassmorphic UI designed around dark surfaces, floating controls, orange accent lighting, and image-first composition.

Key interaction details:

- Floating glass navigation across Explore, Categories, Daily Mix, Studio, and Profile.
- Ghost-fade scrolling so content fades behind the navigation bar.
- Smooth Android-first animations tuned for a 60fps feel.
- Dark-mode-friendly wallpaper controls for dimming, blur, and contrast.

## Privacy And Content

Liquid Wallpapers avoids hidden or unnecessary data collection. Wallpaper images may come from Pexels or Diveno-hosted metadata, and third-party images remain the property of their respective photographers or rights holders.

The app may request:

- Internet and network state access to fetch wallpapers.
- Set wallpaper permission to apply images directly.
- Legacy external storage access on older Android versions for saving wallpapers.

See the public policy pages for full details:

- [Privacy Policy](https://divenolabs.com/liquid-wallpapers-privacy-policy.html)
- [Terms and Conditions](https://divenolabs.com/liquid-wallpapers-terms.html)

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

## Current Release

Current Play Store build:

- Version name: `2.6`
- Version code: `15`
- Package ID: `com.divenolabs.liquidwall`
- Minimum SDK: `26`
- Target SDK: `35`
- Compile SDK: `35`

## Changelog

### v2.6

- Fixed a crash that could occur during wallpaper zoom gestures.
- Added under-the-hood optimizations for Android 15.
- Updated the About page.

### v2.5

- Fixed the blank home feed issue.
- Fixed the Daily Mix already-seen bug.
- Optimized Daily Mix randomization.

### v2.4

- Added Daily Fresh Feed behavior.
- Fixed a critical Studio crash when brightness or alpha levels were pushed to maximum.
- Converted splash assets to WebP and enabled ProGuard shrinking for a major app size reduction.
- Fixed the Contrast label wrapping issue.
- Improved title contrast ratios.

### v2.2

- Improved Text Studio exports for multi-line text, custom alignments, and sharp 4K quote wallpapers.
- Added ghost-fade scrolling behind the floating glass navigation bar.
- Improved battery behavior and scrolling smoothness.

### v2.1

- Redesigned the app with a premium glassmorphic UI.
- Added navigation for Explore, Categories, Daily Mix, Studio, and Profile.
- Added Daily Mix swipe discovery.
- Added Studio for creating custom liquid gradient wallpapers.
- Added Profile with emoji avatars, favorites, and app actions.
- Added mood-based Categories and revamped Favorites.
- Fixed Search and improved security, performance, and general bugs.

## Local Development

1. Clone the repository.
2. Open the project in Android Studio.
3. Create a `local.properties` file with your Pexels API key:

```properties
PEXELS_API_KEY=your_pexels_api_key
```

4. Sync Gradle.
5. Run the app on an Android emulator or physical device.

The project reads the Pexels API key from `local.properties` and injects it into `BuildConfig`.

## Brand

Liquid Wallpapers is a Diveno Labs product.

We believe your screen influences your mind. Liquid Wallpapers curates and creates visual spaces that bring clarity, fluidity, and focus to everyday Android use.
