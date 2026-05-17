package com.asrafrosmadi.recipeexplorer.data.repository

import android.content.Context
import com.asrafrosmadi.recipeexplorer.data.local.FavoritesStore
import com.asrafrosmadi.recipeexplorer.data.local.RecipeCache
import com.asrafrosmadi.recipeexplorer.data.model.Recipe
import com.asrafrosmadi.recipeexplorer.data.model.RecipeResponse
import com.asrafrosmadi.recipeexplorer.data.network.RecipeApiService

class RecipeRepository(context: Context) {
    private val api = RecipeApiService()
    private val cache = RecipeCache(context)
    private val favorites = FavoritesStore(context)

    fun fetchRecipes(
        query: String,
        limit: Int,
        skip: Int
    ): RecipeResponse =
        if (query.isBlank())
            api.fetchRecipes(limit, skip)
        else
            api.searchRecipes(query, limit, skip)

    fun saveCache(recipes: List<Recipe>) =
        cache.saveLoadedRecipes(recipes)

    fun getCache(): List<Recipe> =
        cache.getLoadedRecipes()

    fun getFavorites(): List<Recipe> =
        favorites.getFavorites()

    fun isFavorite(id: Int): Boolean =
        favorites.isFavorite(id)

    fun toggleFavorite(recipe: Recipe): Boolean =
        favorites.toggle(recipe)

    fun clearFavorites() =
        favorites.clearFavorites()

}
