package com.asrafrosmadi.recipeexplorer.ui.detail

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.lifecycleScope
import com.asrafrosmadi.recipeexplorer.R
import com.asrafrosmadi.recipeexplorer.data.model.Recipe
import com.asrafrosmadi.recipeexplorer.data.repository.RecipeRepository
import com.asrafrosmadi.recipeexplorer.util.ImageLoader
import org.json.JSONObject

class RecipeDetailActivity : AppCompatActivity() {
    private lateinit var repo: RecipeRepository
    private lateinit var recipe: Recipe
    private lateinit var favBtn: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        setContentView(R.layout.activity_recipe_detail)
        setupEdgeToEdgeInsets()

        repo = RecipeRepository(this)
        recipe = Recipe.fromJson(
            JSONObject(intent.getStringExtra(EXTRA_RECIPE_JSON) ?: "{}")
        )

        favBtn = findViewById(R.id.detailFavBtn)
        favBtn.setOnClickListener {
            repo.toggleFavorite(recipe)
            updateFavIcon()
        }

        updateFavIcon()

        findViewById<ImageButton>(R.id.backBtn).setOnClickListener {
            finish()
        }

        findViewById<TextView>(R.id.detailTitle).text = recipe.name
        findViewById<TextView>(R.id.detailRating).text = "⭐ ${recipe.rating}"
        findViewById<TextView>(R.id.detailReviewCount).text = "${recipe.reviewCount} reviews   •   ${recipe.caloriesPerServing} kcal/serving"
        findViewById<TextView>(R.id.detailDifficulty).text = "${recipe.difficulty}"
        findViewById<TextView>(R.id.detailMealType).text = "${recipe.mealType.joinToString(", ")}"
        findViewById<TextView>(R.id.detailCuisine).text = "${recipe.cuisine}"
        findViewById<TextView>(R.id.detailPrep).text = "${recipe.prepTimeMinutes} min"
        findViewById<TextView>(R.id.detailCook).text = "${recipe.cookTimeMinutes} min"
        findViewById<TextView>(R.id.detailServings).text = "${recipe.servings}"

//        findViewById<TextView>(R.id.detailMeta).text = buildString {
//            append("• Difficulty: ${recipe.difficulty}\n")
//            append("• Meal Type: ${recipe.mealType.joinToString(", ")}\n")
//            append("• Cuisine: ${recipe.cuisine}\n")
//            append("• Servings: ${recipe.servings} People\n")
//            append("• Calories: ${recipe.caloriesPerServing} Kcal/Serving\n")
//            append("${recipe.caloriesPerServing} kcal/serving\n")
//            append("• Prep Time: ${recipe.prepTimeMinutes} Minutes\n")
//            append("• Cook Time: ${recipe.cookTimeMinutes} Minutes\n")
//            append("• Rating ${recipe.rating} (${recipe.reviewCount} reviews)\n")
//        }

        findViewById<TextView>(R.id.detailIngredients).text =
            recipe.ingredients
                .takeIf { it.isNotEmpty() }
                ?.joinToString(separator = "\n") { "• $it" }
                ?: "No ingredients available"

        findViewById<TextView>(R.id.detailInstructions).text =
            recipe.instructions
                .takeIf { it.isNotEmpty() }
                ?.mapIndexed { index, step -> "${index + 1}. $step" }
                ?.joinToString(separator = "\n\n")
                ?: "No instructions available"

        ImageLoader.load(
            this,
            recipe.image,
            findViewById<ImageView>(R.id.detailImage),
            lifecycleScope
        )

    }

    private fun updateFavIcon() {
        favBtn.setImageResource(
            if (repo.isFavorite(recipe.id))
                R.drawable.ic_star_filled
            else
                R.drawable.ic_star_outline)
    }

    private fun setupEdgeToEdgeInsets() {
        val root = findViewById<View>(R.id.detailRoot)
        val backBtn = findViewById<View>(R.id.backBtn)
        val favBtn = findViewById<View>(R.id.detailFavBtn)
        val scrollView = findViewById<ScrollView>(R.id.detailScroll)

        val originalBackTop = (backBtn.layoutParams as ViewGroup.MarginLayoutParams).topMargin
        val originalFavTop = (favBtn.layoutParams as ViewGroup.MarginLayoutParams).topMargin
        val originalScrollBottom = scrollView.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            backBtn.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = originalBackTop + systemBars.top
            }

            favBtn.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = originalFavTop + systemBars.top
            }

            scrollView.setPadding(
                scrollView.paddingLeft,
                scrollView.paddingTop,
                scrollView.paddingRight,
                originalScrollBottom + systemBars.bottom
            )

            insets
        }
    }

    companion object {
        const val EXTRA_RECIPE_JSON = "recipe_json"
    }

}
