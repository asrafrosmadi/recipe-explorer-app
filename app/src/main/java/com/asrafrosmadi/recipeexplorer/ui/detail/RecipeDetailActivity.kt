package com.asrafrosmadi.recipeexplorer.ui.detail

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
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
        findViewById<ImageButton>(R.id.shareBtn).setOnClickListener {
            shareRecipe()
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

        /*findViewById<TextView>(R.id.detailIngredients).text =
            recipe.ingredients
                .takeIf { it.isNotEmpty() }
                ?.joinToString(separator = "\n") { "• $it" }
                ?: "No ingredients available"

        findViewById<TextView>(R.id.detailInstructions).text =
            recipe.instructions
                .takeIf { it.isNotEmpty() }
                ?.mapIndexed { index, step -> "${index + 1}. $step" }
                ?.joinToString(separator = "\n\n")
                ?: "No instructions available"*/

        setupChecklistSection(
            header = findViewById(R.id.ingredientsHeader),
            clearButton = findViewById(R.id.clearIngredientsTicks),
            container = findViewById(R.id.detailIngredients),
            title = "Ingredients",
            items = recipe.ingredients,
            storageKey = "ingredients_${recipe.id}",
            numbered = false
        )

        setupChecklistSection(
            header = findViewById(R.id.instructionsHeader),
            clearButton = findViewById(R.id.clearInstructionsTicks),
            container = findViewById(R.id.detailInstructions),
            title = "Instructions",
            items = recipe.instructions,
            storageKey = "instructions_${recipe.id}",
            numbered = true
        )

        ImageLoader.load(
            this,
            recipe.image,
            findViewById<ImageView>(R.id.detailImage),
            lifecycleScope
        )

    }

    private fun setupChecklistSection(
        header: TextView,
        clearButton: TextView,
        container: LinearLayout,
        title: String,
        items: List<String>,
        storageKey: String,
        numbered: Boolean
    ) {
        val prefs = getSharedPreferences("recipe_checklist_prefs", Context.MODE_PRIVATE)
        val checkedItems = prefs.getStringSet(
            storageKey,
            emptySet()
        )?.toMutableSet() ?: mutableSetOf()

        fun updateClearButtonVisibility() {
            clearButton.visibility =
                if (checkedItems.isEmpty())
                    View.GONE
                else
                    View.VISIBLE
        }

        var isExpanded = true

        fun updateHeader() {
            header.text = if (isExpanded) "$title ▼" else "$title ▶"
        }

        fun renderItems() {
            container.removeAllViews()

            if (items.isEmpty()) {
                val emptyText = TextView(this).apply {
                    text = "No $title available"
                    textSize = 15f
                }
                container.addView(emptyText)
                return
            }

            items.forEachIndexed { index, item ->
                val itemKey = index.toString()

                val checkBox = CheckBox(this).apply {
                    text = if (numbered) "${index + 1}. $item" else item
                    isChecked = checkedItems.contains(itemKey)
                    textSize = 15f

                    if (numbered) {
                        setPadding(0, 8, 0, 8)
                    }

                    setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            checkedItems.add(itemKey)
                        } else {
                            checkedItems.remove(itemKey)
                        }

                        prefs.edit()
                            .putStringSet(storageKey, checkedItems)
                            .apply()

                        updateClearButtonVisibility()
                    }
                }

                if (numbered) {
                    checkBox.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        bottomMargin = if (numbered) 24 else 4
                    }
                }

                container.addView(checkBox)
            }
        }

        header.setOnClickListener {
            isExpanded = !isExpanded
            container.visibility = if (isExpanded) View.VISIBLE else View.GONE
            updateHeader()
        }

        clearButton.setOnClickListener {
            prefs.edit()
                .remove(storageKey)
                .apply()

            checkedItems.clear()
            renderItems()

            updateClearButtonVisibility()
        }

        updateHeader()
        updateClearButtonVisibility()
        renderItems()
    }

    private fun shareRecipe() {
        val shareText = buildString {
            appendLine("🍽️ ${recipe.name}")
            appendLine()
            appendLine("⭐ Rating: ${recipe.rating}")
            appendLine("🔥 Calories: ${recipe.caloriesPerServing} kcal/serving")
            appendLine("🥘 Cuisine: ${recipe.cuisine}")
            appendLine("⏱️ Prep: ${recipe.prepTimeMinutes} min")
            appendLine("🍳 Cook: ${recipe.cookTimeMinutes} min")
            appendLine("👥 Servings: ${recipe.servings}")

            appendLine()
            appendLine("Ingredients:")
            recipe.ingredients.forEach {
                appendLine("• $it")
            }

            appendLine()
            appendLine("Instructions:")
            recipe.instructions.forEachIndexed { index, step ->
                appendLine("${index + 1}. $step")
            }

            appendLine()
            appendLine("📱 Explore more recipes in Recipe Explorer App via Play Store!")
            appendLine("🔗 https://play.google.com/store/apps/details?id=com.asrafrosmadi.recipeexplorer")
        }

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, recipe.name)
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        startActivity(
            Intent.createChooser(
                shareIntent,
                "Share recipe via"
            )
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
