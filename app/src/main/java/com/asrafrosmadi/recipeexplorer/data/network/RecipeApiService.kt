package com.asrafrosmadi.recipeexplorer.data.network

import com.asrafrosmadi.recipeexplorer.data.model.Recipe
import com.asrafrosmadi.recipeexplorer.data.model.RecipeResponse
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.net.URLEncoder
import java.net.UnknownHostException

class RecipeApiService {
    companion object {
        private const val ERROR_LOAD_RECIPES =
            "Unable to load recipes. Please try again."
        private const val ERROR_NO_INTERNET =
            "No internet connection. Please check your network and try again."
        private const val ERROR_TIMEOUT =
            "Connection timed out. Please try again."
    }

    private val baseUrl = "https://dummyjson.com/recipes"

    fun fetchRecipes(limit: Int, skip: Int): RecipeResponse {
        return parseResponse(get("$baseUrl?limit=$limit&skip=$skip"))
    }

    fun searchRecipes(query: String, limit: Int, skip: Int): RecipeResponse {
        val q = URLEncoder.encode(query, "UTF-8")
        return parseResponse(get("$baseUrl/search?q=$q&limit=$limit&skip=$skip"))
    }

    private fun get(urlString: String): String {
        try {
            val connection = (URL(urlString).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 15000
                readTimeout = 15000
                setRequestProperty("Accept", "application/json")
            }

            return try {
                val code = connection.responseCode
                val stream = if (code in 200..299) connection.inputStream else connection.errorStream
                val body = stream.bufferedReader().use {
                    it.readText()
                }

                if (code !in 200..299) throw IllegalStateException(ERROR_LOAD_RECIPES)
                body
            }
            finally {
                connection.disconnect()
            }
        } catch (e: UnknownHostException) {
            throw IOException(ERROR_NO_INTERNET)
        } catch (e: SocketTimeoutException) {
            throw IOException(ERROR_TIMEOUT)
        }
    }

    private fun parseResponse(json: String): RecipeResponse {
        val root = JSONObject(json)
        val arr = root.optJSONArray("recipes")
        val recipes = if (arr != null) List(arr.length()) {
            Recipe.fromJson(arr.getJSONObject(it))
        }
        else emptyList()

        return RecipeResponse(
            recipes,
            root.optInt("total"),
            root.optInt("skip"),
            root.optInt("limit")
        )
    }
}
