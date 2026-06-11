package com.asrafrosmadi.recipeexplorer.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.asrafrosmadi.recipeexplorer.data.model.Recipe
import com.asrafrosmadi.recipeexplorer.data.repository.RecipeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecipeListViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = RecipeRepository(app)
    private val pageSize = 10
    private var skip = 0
    private var total = Int.MAX_VALUE
    private var currentQuery = ""
    private var allLoaded = mutableListOf<Recipe>()
    // #7 - Apply separate lists for recipe & bookmark to preserves each tab's state when switching
    private var bookmarkLoaded = mutableListOf<Recipe>()
    private var selectedDifficulty = "All Difficulty"
    private var selectedMealType = "All Meal Type"

    // #6 - Prevent duplicate API calls before livedata loading state updates.
    private var isDataFetch = false

    private val _state = MutableLiveData(UiState())
    val state: LiveData<UiState> = _state

    init {
        loadInitial()
    }

    fun loadInitial(query: String = currentQuery) {
        currentQuery = query.trim()
        skip = 0
        total = Int.MAX_VALUE
        allLoaded.clear()
        fetch(reset = true)
    }

    fun refresh() = loadInitial(currentQuery)

    fun loadMore() {
        // #6 - Replace state ui with logic state using flag.
        if (isDataFetch || skip >= total) return
        fetch(reset = false)
    }

    fun setFilters(difficulty: String, mealType: String) {
        selectedDifficulty = difficulty
        selectedMealType = mealType
        publish()
    }

    fun refreshFavoriteState() {
        bookmarkLoaded = repo.getFavorites().toMutableList()
        if (_state.value?.mode == Mode.BOOKMARKS) {
            publish()
        }
    }

    fun clearAllFavorites() {
        repo.clearFavorites()
        bookmarkLoaded.clear()
        publish()
    }

    fun toggleFavorite(recipe: Recipe) {
        repo.toggleFavorite(recipe)
        bookmarkLoaded = repo.getFavorites().toMutableList()
        publish()
    }

    fun isFavorite(id: Int) = repo.isFavorite(id)

    fun showBookmarks() {
        bookmarkLoaded = repo.getFavorites().toMutableList()
        _state.value = _state.value?.copy(mode = Mode.BOOKMARKS)
        publish()
    }

    fun showRecipes() {
        _state.value = _state.value?.copy(mode = Mode.RECIPES)

        // #6 - Only reload if the list is empty to avoids refresh repeatedly.
        if (allLoaded.isEmpty()) {
            loadInitial(currentQuery)
        } else {
            publish()
        }
    }

    private fun fetch(reset: Boolean) {
        if (isDataFetch) return
        isDataFetch = true

        _state.value = _state.value?.copy(
            loading = true,
            error = null,
            mode = Mode.RECIPES,
            bookmarkMode = false
        )

        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    repo.fetchRecipes(currentQuery, pageSize, skip)
                }

                if (reset) allLoaded.clear()
                allLoaded.addAll(response.recipes)

                // #6 - Prevention layer: apply distinctBy to remove duplicate recipes by "id"
                allLoaded = allLoaded
                    .distinctBy { it.id }
                    .toMutableList()

                skip = allLoaded.size
                total = response.total
                withContext(Dispatchers.IO) {
                    repo.saveCache(allLoaded)
                }

                publish()
            } catch (e: Exception) {
                if (allLoaded.isEmpty())
                    allLoaded.addAll(withContext(Dispatchers.IO) {
                        repo.getCache()
                    })

                publish(error = e.message ?: "Something went wrong. Please try again.")
            } finally {
                isDataFetch = false
            }
        }
    }

    private fun publish(error: String? = null) {
        val currentMode = _state.value?.mode ?: Mode.RECIPES
        val isBookmarks = currentMode == Mode.BOOKMARKS

        val displayedList = if (isBookmarks) {
            bookmarkLoaded
        } else {
            allLoaded.filter { recipe ->
                val diffOk =
                    selectedDifficulty == "All Difficulty" ||
                            recipe.difficulty.equals(selectedDifficulty, true)

                val mealOk =
                    selectedMealType == "All Meal Type" ||
                            recipe.mealType.any { it.equals(selectedMealType, true) }

                diffOk && mealOk
            }
        }

        val difficulties = listOf("All Difficulty") +
                allLoaded.map { it.difficulty }
                    .filter { it.isNotBlank() }
                    .distinct()
                    .sorted()

        val mealTypes = listOf("All Meal Type") +
                allLoaded.flatMap { it.mealType }
                    .filter { it.isNotBlank() }
                    .distinct()
                    .sorted()

        _state.postValue(
            UiState(
                recipes = displayedList,
                difficulties = difficulties,
                mealTypes = mealTypes,
                selectedDifficulty = selectedDifficulty,
                selectedMealType = selectedMealType,
                loading = false,
                error = error,
                canLoadMore = skip < total && !isBookmarks,
                bookmarkMode = isBookmarks,
                mode = currentMode
            )
        )
    }

    enum class Mode {
        RECIPES,
        BOOKMARKS
    }

    data class UiState(
        val recipes: List<Recipe> = emptyList(),
        val difficulties: List<String> = listOf("All Difficulty"),
        val mealTypes: List<String> = listOf("All Meal Type"),
        val selectedDifficulty: String = "All Difficulty",
        val selectedMealType: String = "All Meal Type",
        val loading: Boolean = false,
        val error: String? = null,
        val canLoadMore: Boolean = true,
        val bookmarkMode: Boolean = false,
        val mode: Mode = Mode.RECIPES
    )
}
