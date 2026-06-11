package com.asrafrosmadi.recipeexplorer.ui.main.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
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
import com.asrafrosmadi.recipeexplorer.ui.main.RecipeListViewModel
import com.asrafrosmadi.recipeexplorer.ui.shared.RecipeAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog

class RecipesFragment : Fragment(R.layout.fragment_recipes) {

    private val viewModel: RecipeListViewModel by activityViewModels()
    private lateinit var adapter: RecipeAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerView)
        val swipe = view.findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)
        val search = view.findViewById<EditText>(R.id.searchEdit)
        val empty = view.findViewById<TextView>(R.id.emptyView)
        val error = view.findViewById<TextView>(R.id.errorText)
        val difficultyFilterBtn = view.findViewById<TextView>(R.id.difficultyFilterBtn)
        val mealTypeFilterBtn = view.findViewById<TextView>(R.id.mealTypeFilterBtn)

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