package com.asrafrosmadi.recipeexplorer.data.local

import android.content.Context
import com.asrafrosmadi.recipeexplorer.data.model.Recipe
import org.json.JSONArray

class RecipeCache(context: Context) {
    private val prefs = context.getSharedPreferences("recipe_cache", Context.MODE_PRIVATE)

    fun saveLoadedRecipes(recipes: List<Recipe>) {
        val arr = JSONArray()
        recipes.forEach {
            arr.put(it.toJson())
        }

        prefs.edit().putString("loaded_recipes", arr.toString()).apply()
    }

    fun getLoadedRecipes(): List<Recipe> {
        val raw = prefs.getString("loaded_recipes", null) ?: return emptyList()
        val arr = JSONArray(raw)
        return List(arr.length()) {
            Recipe.fromJson(arr.getJSONObject(it))
        }
    }

}
