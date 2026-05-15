package com.asrafrosmadi.recipeexplorer.data.model

data class RecipeResponse(
    val recipes: List<Recipe>,
    val total: Int,
    val skip: Int,
    val limit: Int
)
