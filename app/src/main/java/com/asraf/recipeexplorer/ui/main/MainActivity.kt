package com.asrafrosmadi.recipeexplorer.ui.main

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.asrafrosmadi.recipeexplorer.R
import com.asrafrosmadi.recipeexplorer.data.model.Recipe
import com.asrafrosmadi.recipeexplorer.ui.detail.RecipeDetailActivity
import com.asrafrosmadi.recipeexplorer.ui.shared.RecipeAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : AppCompatActivity() {
    private val viewModel: RecipeListViewModel by viewModels()
    private lateinit var adapter: RecipeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        setContentView(R.layout.activity_main)
        setupEdgeToEdgeInsets()

        val recycler = findViewById<RecyclerView>(R.id.recyclerView)
        val swipe = findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)
        val search = findViewById<EditText>(R.id.searchEdit)
        val spinnerLayout = findViewById<View>(R.id.spinnerLayout)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        val empty = findViewById<TextView>(R.id.emptyView)
        val error = findViewById<TextView>(R.id.errorText)
        val difficultyFilterBtn = findViewById<TextView>(R.id.difficultyFilterBtn)
        val mealTypeFilterBtn = findViewById<TextView>(R.id.mealTypeFilterBtn)
        val btnClearFavorites = findViewById<ImageButton>(R.id.btnClearFavorites)

        adapter = RecipeAdapter(lifecycleScope, { viewModel.isFavorite(it) }, { viewModel.toggleFavorite(it) }, { openDetail(it) })
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter
        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                val lm = rv.layoutManager as LinearLayoutManager
                if (dy > 0 && lm.findLastVisibleItemPosition() >= adapter.itemCount - 3) viewModel.loadMore()
            }
        })

        search.addTextChangedListener {
            val text = it.toString()
            if (text.isEmpty()) {
                search.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, R.drawable.ic_search, 0
                )
            } else {
                search.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, R.drawable.ic_outline_close, 0
                )
            }
        }

        search.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.loadInitial(search.text.toString())
                true
            }
            else
                false
        }

        search.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {

                val drawableEnd = 2
                val drawable = search.compoundDrawables[drawableEnd]

                if (drawable != null && event.rawX >= (
                            search.right - drawable.bounds.width()
                        )
                ) {

                    val text = search.text.toString()

                    if (text.isNotEmpty()) {
                        search.text.clear()
                        search.clearFocus()

                        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(search.windowToken, 0)

                        viewModel.loadInitial("")
                    } else {
                        viewModel.loadInitial("")
                    }

                    return@setOnTouchListener true
                }
            }
            false
        }

        swipe.setOnRefreshListener {
            viewModel.refresh()
        }

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navRecipes -> {
                    viewModel.showRecipes()
                    true
                }

                R.id.navBookmarks -> {
                    viewModel.showBookmarks()
                    true
                }

                else -> false
            }
        }

        difficultyFilterBtn.setOnClickListener {
            val state = viewModel.state.value ?: return@setOnClickListener

            showFilterBottomSheet(
                title = "Difficulty",
                options = state.difficulties,
                selected = state.selectedDifficulty
            ) {
                selected ->
                viewModel.setFilters(
                    selected,
                    state.selectedMealType
                )
            }
        }

        mealTypeFilterBtn.setOnClickListener {
            val state = viewModel.state.value ?: return@setOnClickListener

            showFilterBottomSheet(
                title = "Meal Type",
                options = state.mealTypes,
                selected = state.selectedMealType
            ) {
                selected ->
                viewModel.setFilters(
                    state.selectedDifficulty,
                    selected
                )
            }
        }

        btnClearFavorites.setOnClickListener {
            val currentList = viewModel.state.value?.recipes.orEmpty()

            if (currentList.isEmpty()) {
                AlertDialog.Builder(this)
                    .setTitle("Favorites Empty!")
                    .setMessage("Your favorite list is empty.")
                    .setPositiveButton("OK", null)
                    .show()
            }
            else {
                AlertDialog.Builder(this)
                    .setTitle("Clear Favorites!")
                    .setMessage("Do you want to remove all your favorite recipes?")
                    .setPositiveButton("Yes") { dialog, _ ->
                        viewModel.clearAllFavorites()
                        dialog.dismiss()
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }

        viewModel.state.observe(this) { state ->
            val isBookmarks = state.mode == RecipeListViewModel.Mode.BOOKMARKS

            btnClearFavorites.visibility = if (isBookmarks) View.VISIBLE else View.GONE

            search.isVisible = !isBookmarks
            spinnerLayout.isVisible = !isBookmarks
            swipe.isEnabled = !isBookmarks
            swipe.isRefreshing = state.loading && !isBookmarks

            bottomNavigation.menu.findItem(R.id.navRecipes).isEnabled = !state.loading
            bottomNavigation.menu.findItem(R.id.navBookmarks).isEnabled = !state.loading

            difficultyFilterBtn.text = "${state.selectedDifficulty} ▼"
            mealTypeFilterBtn.text = "${state.selectedMealType} ▼"

            adapter.submit(state.recipes)

            empty.visibility = if (state.recipes.isEmpty() && !state.loading) android.view.View.VISIBLE else android.view.View.GONE
            error.visibility = if (state.error.isNullOrBlank()) android.view.View.GONE else android.view.View.VISIBLE
            error.text = state.error
        }
    }

    private fun showFilterBottomSheet(
        title: String,
        options: List<String>,
        selected: String,
        onSelected: (String) -> Unit
    ) {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_filter, null)

        val titleText = view.findViewById<TextView>(R.id.bottomSheetTitle)
        val container = view.findViewById<LinearLayout>(R.id.filterOptionContainer)

        titleText.text = title
        container.removeAllViews()

        options.forEach { option ->
            val itemView = layoutInflater.inflate(
                R.layout.item_bottom_sheet_filter,
                container,
                false
            )

            val titleView = itemView.findViewById<TextView>(R.id.filterTitle)
            val selectedView = itemView.findViewById<TextView>(R.id.filterSelected)

            titleView.text = option
            selectedView.visibility = View.VISIBLE
            selectedView.text = if (option == selected) "●" else "○"
            selectedView.setTextColor(
                if (option == selected)
                    getColor(R.color.primary)
                else
                    getColor(android.R.color.darker_gray)
            )

            itemView.setOnClickListener {
                onSelected(option)
                dialog.dismiss()
            }

            container.addView(itemView)
        }

        dialog.setContentView(view)
        dialog.show()
    }

    private fun setupEdgeToEdgeInsets() {
        val root = findViewById<View>(R.id.rootLayout)
        val header = findViewById<View>(R.id.headerTopBar)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val bottomNavigation = findViewById<View>(R.id.bottomNavigation)

        val originalHeaderPaddingTop = header.paddingTop
        val originalRecyclerPaddingBottom = recyclerView.paddingBottom
        val originalNavigationPaddingBottom = bottomNavigation.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            header.setPadding(
                header.paddingLeft,
                originalHeaderPaddingTop + systemBars.top,
                header.paddingRight,
                header.paddingBottom
            )

            recyclerView.setPadding(
                recyclerView.paddingLeft,
                recyclerView.paddingTop,
                recyclerView.paddingRight,
                originalRecyclerPaddingBottom + systemBars.bottom
            )

            bottomNavigation.setPadding(
                bottomNavigation.paddingLeft,
                bottomNavigation.paddingTop,
                bottomNavigation.paddingRight,
                originalNavigationPaddingBottom + systemBars.bottom
            )

            insets
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshFavoriteState()
    }

    private fun openDetail(recipe: Recipe) {
        startActivity(Intent(this, RecipeDetailActivity::class.java).putExtra(RecipeDetailActivity.EXTRA_RECIPE_JSON, recipe.toJson().toString()))
    }

}
