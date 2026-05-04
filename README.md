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
    * Android Gradle Plugin: **8.7.3**
    * Gradle: **9.0.0**
    * Gradle Wrapper: **Amazon Coretto-17.0.9**

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

## 👨‍💻 Author

**Ahmad Amirul Asraf Bin Rosmadi**

Senior Android Engineer
