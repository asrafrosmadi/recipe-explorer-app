package com.asrafrosmadi.recipeexplorer.ui.main.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asrafrosmadi.recipeexplorer.R
import com.asrafrosmadi.recipeexplorer.data.model.Recipe
import com.asrafrosmadi.recipeexplorer.ui.detail.RecipeDetailActivity
import com.asrafrosmadi.recipeexplorer.ui.main.RecipeListViewModel
import com.asrafrosmadi.recipeexplorer.ui.shared.RecipeAdapter

class BookmarksFragment : Fragment(R.layout.fragment_bookmarks) {

    private val viewModel: RecipeListViewModel by activityViewModels()
    private lateinit var adapter: RecipeAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerView)
        val empty = view.findViewById<TextView>(R.id.emptyView)

        adapter = RecipeAdapter(
            viewLifecycleOwner.lifecycleScope,
            { viewModel.isFavorite(it) },
            { viewModel.toggleFavorite(it) },
            { openDetail(it) }
        )

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        viewModel.state.observe(viewLifecycleOwner) { state ->
            if (state.mode != RecipeListViewModel.Mode.BOOKMARKS)
                return@observe

            adapter.submit(state.recipes)

            empty.visibility =
                if (state.recipes.isEmpty())
                    View.VISIBLE
                else
                    View.GONE
        }
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