package com.asrafrosmadi.recipeexplorer.data.model

import org.json.JSONArray
import org.json.JSONObject

data class Recipe(
    val id: Int,
    val name: String,
    val ingredients: List<String>,
    val instructions: List<String>,
    val prepTimeMinutes: Int,
    val cookTimeMinutes: Int,
    val servings: Int,
    val difficulty: String,
    val cuisine: String,
    val caloriesPerServing: Int,
    val tags: List<String>,
    val userId: Int,
    val image: String,
    val rating: Double,
    val reviewCount: Int,
    val mealType: List<String>
) {
    companion object {
        fun fromJson(json: JSONObject): Recipe = Recipe(
            id = json.optInt("id"),
            name = json.optString("name"),
            ingredients = jsonArrayToList(json, "ingredients"),
            instructions = jsonArrayToList(json, "instructions"),
            prepTimeMinutes = json.optInt("prepTimeMinutes"),
            cookTimeMinutes = json.optInt("cookTimeMinutes"),
            servings = json.optInt("servings"),
            difficulty = json.optString("difficulty"),
            cuisine = json.optString("cuisine"),
            caloriesPerServing = json.optInt("caloriesPerServing"),
            tags = jsonArrayToList(json, "tags"),
            userId = json.optInt("userId"),
            image = json.optString("image"),
            rating = json.optDouble("rating"),
            reviewCount = json.optInt("reviewCount"),
            mealType = jsonArrayToList(json, "mealType")
        )

        private fun jsonArrayToList(json: JSONObject, key: String): List<String> {
            val array = json.optJSONArray(key) ?: return emptyList()
            return List(array.length()) { index ->
                array.optString(index)
            }
        }
    }

    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("id", id)
            put("name", name)
            put("ingredients", JSONArray(ingredients))
            put("instructions", JSONArray(instructions))
            put("prepTimeMinutes", prepTimeMinutes)
            put("cookTimeMinutes", cookTimeMinutes)
            put("servings", servings)
            put("difficulty", difficulty)
            put("cuisine", cuisine)
            put("caloriesPerServing", caloriesPerServing)
            put("tags", JSONArray(tags))
            put("userId", userId)
            put("image", image)
            put("rating", rating)
            put("reviewCount", reviewCount)
            put("mealType", JSONArray(mealType))
        }
    }
}
