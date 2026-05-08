# 🍽️ Recipe Explorer App

A modern **Android Recipe Explorer app** built with **Kotlin**, queries by the **DummyJSON Recipes API**.

Browse, search, and bookmark your favorite recipes with a smooth and responsive UI.

---

## ✨ Features

### 📋 Recipe Browsing

* View recipes with:

    * Title, image, cuisine, difficulty
    * Cooking time, rating, tags, and meal type
* **Infinite scrolling** with pagination (10 items per load)

---

### 🔍 Search & Filter

* Search recipes by keyword
* Filter recipes by:

    * Difficulty
    * Meal type
* Filters are applied on currently loaded data

---

### 📖 Recipe Details

* Full recipe view including:

    * Ingredients
    * Step-by-step instructions
    * Prep & cook time
    * Servings & calories
    * Rating & meal type

---

### ⭐ Favorites (Bookmarks)

* Save your favorite recipes
* Dedicated **Bookmarks tab**
* Favorites persist across app restarts
* Additional feature: **Clear all favorites**

---

### 🔄 Data Handling

* Pull-to-refresh support
* Offline support:
    * Displays cached recipes if API fails
* Graceful error handling

---

### ⚡ Performance

* Image caching:
    * In-memory (`LruCache`)
    * Disk cache
* Smooth scrolling experience

---

### 🏗️ Architecture

* MVVM (Model-View-ViewModel)
* Clean separation of:
    * UI
    * Business logic
    * Data sources (API + local)

---

## 🛠️ Tech Stack

* **Language:** Kotlin
* **UI:** Android XML Views
* **Architecture:** MVVM
* **Networking:** `HttpURLConnection` + `org.json`
* **Local Storage:** SharedPreferences
* **Caching:** LruCache + Disk Cache

---

## 🚀 How to Run

1. Clone this repository:

   ```bash
   git clone https://github.com/asrafrosmadi/recipe-explorer-app.git
   ```

2. Open in **Android Studio**

3. Configure environment:

    * JDK: **17**
    * Android Gradle Plugin: **9.1.1**
    * Gradle: **9.4.1**
    * Gradle Wrapper: **Amazon Coretto-17.0.19**

4. Sync Gradle

5. Run the app on:

    * Emulator OR
    * Physical Android device

---

## 🌐 API Endpoints

### Get first 10 recipes

```
https://dummyjson.com/recipes?limit=10&skip=0
```

### Get next page (pagination)

```
https://dummyjson.com/recipes?limit=10&skip=10
```

### Search recipes

```
https://dummyjson.com/recipes/search?q=margherita
```

---

## 📌 Notes

* Pagination uses `limit` and `skip`
* Filters are applied locally (client-side)
* Favorites are stored using SharedPreferences

---

📷 App Preview

* Recipes Tab UI
<img width="300" height="683" alt="Recipes UI" src="https://github.com/user-attachments/assets/1e5dac2c-a72f-448e-a7af-617d8a9386c8" />

* Bookmarks Tab UI
<img width="300" height="683" alt="Bookmarks UI" src="https://github.com/user-attachments/assets/ce26ad20-b676-4a04-a9ed-7a0e05768410" />

* Recipe Details UI
<img width="300" height="683" alt="Recipes Details UI" src="https://github.com/user-attachments/assets/0b7461a2-fc98-443a-984c-ac215d0f90fa" />


---


# Privacy Policy for Recipe Explorer

**Last Updated:** 07 May 2026

Welcome to **Recipe Explorer**. Your privacy is important to us. This Privacy Policy explains how the application collects, uses, and protects your information when you use the app.

---

# 1. Introduction

Recipe Explorer is a mobile application developed to help users browse, search, and bookmark recipes using publicly available recipe APIs.

By using this application, you agree to the practices described in this Privacy Policy.

---

# 2. Information We Collect

Recipe Explorer does **not collect or store any personally identifiable information** such as:

- Name
- Email address
- Phone number
- Payment information
- Location data
- Contacts
- Camera or microphone data

The application only stores limited local app data necessary for functionality.

---

# 3. Local Data Storage

Recipe Explorer may store the following data locally on your device using Android SharedPreferences and cache storage:

- Favorite/bookmarked recipes
- Cached recipe listings
- App preferences and temporary data
- Cached images for performance optimization

This data remains only on your device and is not transmitted to external servers owned by the developer.

---

# 4. Internet Usage

The app requires internet access to retrieve recipe data and images from third-party public APIs.

Example API source:
- https://dummyjson.com/recipes

Recipe Explorer does not control or own third-party APIs and services.

---

# 5. Third-Party Services

Recipe Explorer may use publicly available third-party services to load recipe content and images.

These services may have their own privacy policies.

Example:
- DummyJSON API  
  https://dummyjson.com

---

# 6. Data Sharing

Recipe Explorer does **not sell, share, rent, or distribute** user data to third parties.

No personal information is collected for advertising or analytics purposes.

---

# 7. Children's Privacy

Recipe Explorer is not directed toward children under the age of 13.

The application does not knowingly collect personal data from children.

---

# 8. Security

We take reasonable measures to protect locally stored application data. However, no method of electronic storage or transmission over the internet is 100% secure.

---

# 9. Changes to This Privacy Policy

This Privacy Policy may be updated from time to time. Any changes will be reflected on this page with an updated revision date.

---

# 10. Contact Information

If you have any questions regarding this Privacy Policy, you may contact:

**Developer:** Asraf Rosmadi  
**GitHub:** https://github.com/asrafrosmadi  
**Project Repository:** https://github.com/asrafrosmadi/recipe-explorer

---

# 11. Consent

By using Recipe Explorer, you consent to this Privacy Policy.


---

## 👨‍💻 Author

**Ahmad Amirul Asraf Bin Rosmadi**

Senior Android Engineer
