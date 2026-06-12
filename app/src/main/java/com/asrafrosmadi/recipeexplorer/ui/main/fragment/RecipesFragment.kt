package com.asrafrosmadi.recipeexplorer.ui.main.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.OptIn
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.asrafrosmadi.recipeexplorer.R
import com.asrafrosmadi.recipeexplorer.data.model.Recipe
import com.asrafrosmadi.recipeexplorer.ui.detail.RecipeDetailActivity
import com.asrafrosmadi.recipeexplorer.ui.main.viewmodel.RecipeListViewModel
import com.asrafrosmadi.recipeexplorer.ui.shared.RecipeAdapter
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class RecipesFragment : Fragment(R.layout.fragment_recipes) {

    private val viewModel: RecipeListViewModel by activityViewModels()
    private lateinit var adapter: RecipeAdapter
    private var filterBadge: BadgeDrawable? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerView)
        val swipe = view.findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)
        val search = view.findViewById<EditText>(R.id.searchEdit)
        val empty = view.findViewById<LinearLayout>(R.id.emptyView)
        val error = view.findViewById<TextView>(R.id.errorText)
        val difficultyFilterBtn = view.findViewById<TextView>(R.id.difficultyFilterBtn)
        val mealTypeFilterBtn = view.findViewById<TextView>(R.id.mealTypeFilterBtn)
        val advancedFilterBtn = view.findViewById<ImageButton>(R.id.advancedFilterBtn)
        filterBadge = BadgeDrawable.create(requireContext()).apply {
            isVisible = false
            maxCharacterCount = 2
        }

        adapter = RecipeAdapter(
            viewLifecycleOwner.lifecycleScope,
            { viewModel.isFavorite(it) },
            { viewModel.toggleFavorite(it) },
            { openDetail(it) }
        )

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                val lm = rv.layoutManager as LinearLayoutManager

                if (dy > 0 && lm.findLastVisibleItemPosition() >= adapter.itemCount - 3) {
                    viewModel.loadMore()
                }
            }
        })

        search.addTextChangedListener {
            val text = it.toString()

            search.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                if (text.isEmpty()) R.drawable.ic_search else R.drawable.ic_outline_close,
                0
            )
        }

        search.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.loadInitial(search.text.toString())
                hideKeyboard(search)
                true
            } else {
                false
            }
        }

        search.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = 2
                val drawable = search.compoundDrawables[drawableEnd]

                if (drawable != null && event.rawX >= search.right - drawable.bounds.width()) {
                    search.text.clear()
                    search.clearFocus()
                    hideKeyboard(search)
                    viewModel.loadInitial("")
                    return@setOnTouchListener true
                }
            }

            false
        }

        swipe.setOnRefreshListener {
            viewModel.refresh()
        }

        difficultyFilterBtn.setOnClickListener {
            val state = viewModel.state.value ?: return@setOnClickListener

            showFilterBottomSheet(
                title = "Difficulty",
                options = state.difficulties,
                selected = state.selectedDifficulty
            ) { selected ->
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
            ) { selected ->
                viewModel.setFilters(
                    state.selectedDifficulty,
                    selected
                )
            }
        }

        advancedFilterBtn.setOnClickListener {
            showAdvancedFilterBottomSheet()
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            if (state.mode != RecipeListViewModel.Mode.RECIPES)
                return@observe

            adapter.submit(state.recipes)

            swipe.isRefreshing = state.loading
            empty.visibility =
                if (state.recipes.isEmpty() && !state.loading)
                    View.VISIBLE
                else
                    View.GONE

            error.visibility =
                if (state.error.isNullOrBlank())
                    View.GONE
                else
                    View.VISIBLE

            error.text = state.error

            difficultyFilterBtn.text = "${state.selectedDifficulty} ▼"
            mealTypeFilterBtn.text = "${state.selectedMealType} ▼"

            updateFilterBadge(state.activeFilterCount)
        }
    }

    private fun showFilterBottomSheet(
        title: String,
        options: List<String>,
        selected: String,
        onSelected: (String) -> Unit
    ) {
        val dialog = BottomSheetDialog(requireContext())
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
                    requireContext().getColor(R.color.primary)
                else
                    requireContext().getColor(android.R.color.darker_gray)
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

    private fun showAdvancedFilterBottomSheet() {
        val state = viewModel.state.value ?: return

        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_sheet_advanced_filter, null)

        val container = view.findViewById<LinearLayout>(R.id.advancedFilterContainer)
        val resetBtn = view.findViewById<Button>(R.id.resetFilterBtn)
        val applyBtn = view.findViewById<Button>(R.id.applyFilterBtn)

        val cuisineChecks = mutableMapOf<String, CheckBox>()
        val ingredientChecks = mutableMapOf<String, CheckBox>()
        val tagChecks = mutableMapOf<String, CheckBox>()

        val timeChecks = mutableMapOf<Int, CheckBox>()
        val ratingChecks = mutableMapOf<Double, CheckBox>()
        val calorieChecks = mutableMapOf<RecipeListViewModel.CalorieRange, CheckBox>()

        val currentCount = calculateSelectedFilterCount(state.advancedFilters)
        if (currentCount > 0) {
            applyBtn.text = "Apply ($currentCount)"
        }

        addSectionTitle(container, "Cuisine")
        state.cuisines.forEach { cuisine ->
            val checkBox = addCheckBox(
                container,
                cuisine,
                state.advancedFilters.cuisines.contains(cuisine)
            )
            cuisineChecks[cuisine] = checkBox
        }

        addSectionTitle(container, "Cooking Time")
        mapOf(
            15 to "Under 15 min",
            30 to "Under 30 min",
            45 to "Under 45 min"
        ).forEach { (minutes, label) ->
            val checkBox = addCheckBox(
                container,
                label,
                state.advancedFilters.maxCookingTimeMinutes == minutes
            )
            timeChecks[minutes] = checkBox
        }

        addSectionTitle(container, "Rating")
        mapOf(
            4.5 to "4.5+ rating",
            4.0 to "4.0+ rating"
        ).forEach { (rating, label) ->
            val checkBox = addCheckBox(
                container,
                label,
                state.advancedFilters.minRating == rating
            )
            ratingChecks[rating] = checkBox
        }

        addSectionTitle(container, "Calories")
        mapOf(
            RecipeListViewModel.CalorieRange.UNDER_300 to "Under 300 kcal",
            RecipeListViewModel.CalorieRange.BETWEEN_300_500 to "300–500 kcal",
            RecipeListViewModel.CalorieRange.ABOVE_500 to "500+ kcal"
        ).forEach { (range, label) ->
            val checkBox = addCheckBox(
                container,
                label,
                state.advancedFilters.calorieRange == range
            )
            calorieChecks[range] = checkBox
        }

//        addSectionTitle(container, "Ingredients")
//        state.ingredients.take(20).forEach { ingredient ->
//            val checkBox = addCheckBox(
//                container,
//                ingredient,
//                state.advancedFilters.ingredients.contains(ingredient)
//            )
//            ingredientChecks[ingredient] = checkBox
//        }

        addSectionTitle(container, "Tags")
        state.tags.take(20).forEach { tag ->
            val checkBox = addCheckBox(
                container,
                tag,
                state.advancedFilters.tags.contains(tag)
            )
            tagChecks[tag] = checkBox
        }

        resetBtn.setOnClickListener {
            viewModel.resetAdvancedFilters()
            dialog.dismiss()
        }

        applyBtn.setOnClickListener {
            val selectedTime = timeChecks
                .filterValues { it.isChecked }
                .keys
                .minOrNull()

            val selectedRating = ratingChecks
                .filterValues { it.isChecked }
                .keys
                .maxOrNull()

            val selectedCalorie = calorieChecks
                .filterValues { it.isChecked }
                .keys
                .firstOrNull()

            val filters = RecipeListViewModel.AdvancedFilters(
                cuisines = cuisineChecks.filterValues { it.isChecked }.keys,
                maxCookingTimeMinutes = selectedTime,
                minRating = selectedRating,
                calorieRange = selectedCalorie,
                ingredients = ingredientChecks.filterValues { it.isChecked }.keys,
                tags = tagChecks.filterValues { it.isChecked }.keys
            )

            viewModel.setAdvancedFilters(filters)

            val selectedCount = calculateSelectedFilterCount(filters)
            applyBtn.text =
                if (selectedCount > 0)
                    "Apply ($selectedCount)"
                else
                    "Apply"

            dialog.dismiss()
        }

        dialog.setContentView(view)

        dialog.setOnShowListener {
            val bottomSheet =
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

            bottomSheet?.let { sheet ->
                val behavior = BottomSheetBehavior.from(sheet)

                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
                behavior.isDraggable = false
            }
        }

        dialog.show()
    }

    private fun calculateSelectedFilterCount(
        filters: RecipeListViewModel.AdvancedFilters
    ): Int {
        return filters.cuisines.size +
                filters.ingredients.size +
                filters.tags.size +
                (if (filters.maxCookingTimeMinutes != null) 1 else 0) +
                (if (filters.minRating != null) 1 else 0) +
                (if (filters.calorieRange != null) 1 else 0)
    }

    @OptIn(ExperimentalBadgeUtils::class)
    private fun updateFilterBadge(count: Int) {
        val badge = filterBadge ?: return

        if (count == 0) {
            badge.isVisible = false
            return
        }

        badge.number = count
        badge.isVisible = true
        BadgeUtils.attachBadgeDrawable(
            badge,
            requireView().findViewById(R.id.advancedFilterBtn)
        )
    }

    private fun addSectionTitle(container: LinearLayout, title: String) {
        val titleView = TextView(requireContext()).apply {
            text = title
            textSize = 16f
            setTextColor(requireContext().getColor(R.color.primary))
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 18, 0, 6)
        }

        container.addView(titleView)
    }

    private fun addCheckBox(
        container: LinearLayout,
        label: String,
        checked: Boolean
    ): CheckBox {
        val checkBox = CheckBox(requireContext()).apply {
            text = label
            isChecked = checked
            textSize = 14f
            setPadding(0, 2, 0, 2)
        }

        container.addView(checkBox)
        return checkBox
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshFavoriteState()
        adapter.notifyDataSetChanged()
    }

    private fun hideKeyboard(view: View) {
        val imm = requireContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun openDetail(recipe: Recipe) {
        startActivity(
            Intent(requireContext(), RecipeDetailActivity::class.java)
                .putExtra(
                    RecipeDetailActivity.EXTRA_RECIPE_JSON,
                    recipe.toJson().toString()
                )
        )
    }
}