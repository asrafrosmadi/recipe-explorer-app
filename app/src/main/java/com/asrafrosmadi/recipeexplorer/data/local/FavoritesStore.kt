package com.asrafrosmadi.recipeexplorer.data.local

import android.content.Context
import com.asrafrosmadi.recipeexplorer.data.model.Recipe
import org.json.JSONArray

class FavoritesStore(context: Context) {
    private val prefs = context.getSharedPreferences("favorites_store", Context.MODE_PRIVATE)

    fun getFavorites(): List<Recipe> {
        val raw = prefs.getString(KEY_FAVORITES, "[]") ?: "[]"
        val arr = JSONArray(raw)
        return List(arr.length()) {
            Recipe.fromJson(arr.getJSONObject(it))
        }
    }

    fun isFavorite(id: Int): Boolean = getFavorites().any {
        it.id == id
    }

    fun toggle(recipe: Recipe): Boolean {
        val current = getFavorites().toMutableList()
        val existing = current.indexOfFirst {
            it.id == recipe.id
        }

        val nowFav = if (existing >= 0) {
            current.removeAt(existing)
            false
        } else {
            current.add(recipe)
            true
        }

        val arr = JSONArray()
        current.forEach {
            arr.put(it.toJson())
        }

        prefs.edit()
            .putString(KEY_FAVORITES, arr.toString())
            .apply()

        return nowFav
    }

    fun clearFavorites() {
        prefs.edit()
            .putString(KEY_FAVORITES, "[]")
            .apply()
    }

    companion object {
        private const val KEY_FAVORITES = "favorites"
    }

}
