# Production App Release Log
---

## v1.0.2 - Major App Changes
Date Released: 12 JUNE 2026
### Enhancements
- Refactor bottom navigation using fragments.
- Preserve Recipes and Bookmarks tab state during navigation.
- Separate recipe and bookmark data sources to avoid state conflicts.
- Fix duplicate recipes during filtering.
- Prevent duplicate API requests during infinite scrolling.
- Improve bookmark state synchronization across screens.
- Enhance network and offline error handling.
### Improvements
- Added a feature that allows users to share recipes.
- Enable recipe details sharing via WhatsApp & text.
- Add interactive checklist for ingredients & cooking steps.
- Add advanced recipe filtering feature. 
- Support filtering by cuisine, cooking time, rating, calories and tags. 
- Add active filter badge indicator. 
- Add apply and reset actions for the filters. 
#
---
## v1.0.1 - App Improvement
Date Released: 15 MAY 2026
### Improvements
- Update to latest Android SDK 37
- Added in-app update support for new app version release
### Tech
- Firebase Analytics integration
#
---
## v1.0.0 - Initial App Release
Date Released: 05 MAY 2026
### Features
- Browse delicious recipe collections
- Search recipes instantly
- Filter recipes by difficulty and meal type
- View detailed ingredients and cooking instructions
- Save favorite recipes with bookmarks
- Smooth infinite scrolling experience
- Pull-to-refresh support
- Offline cache support for previously loaded recipes
- Modern and responsive UI design
### Tech
- Kotlin + MVVM architecture
- R8/ProGuard enabled