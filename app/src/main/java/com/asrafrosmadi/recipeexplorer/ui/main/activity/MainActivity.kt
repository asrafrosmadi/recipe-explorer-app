package com.asrafrosmadi.recipeexplorer.ui.main.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.asrafrosmadi.recipeexplorer.BuildConfig
import com.asrafrosmadi.recipeexplorer.R
import com.asrafrosmadi.recipeexplorer.ui.main.fragment.BookmarksFragment
import com.asrafrosmadi.recipeexplorer.ui.main.fragment.RecipesFragment
import com.asrafrosmadi.recipeexplorer.ui.main.viewmodel.RecipeListViewModel
import com.asrafrosmadi.recipeexplorer.ui.update.InAppUpdateManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private val viewModel: RecipeListViewModel by viewModels()
    private lateinit var inAppUpdateManager: InAppUpdateManager
    private val recipesFragment = RecipesFragment()
    private val bookmarksFragment = BookmarksFragment()
    private lateinit var activeFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        setContentView(R.layout.activity_main)

        inAppUpdateManager = InAppUpdateManager(this)
        if (BuildConfig.ENVIRONMENT == "PRODUCTION" && !BuildConfig.DEBUG) {
            inAppUpdateManager.checkForUpdate()
        }

        setupEdgeToEdgeInsets()
        setupBottomNavigation()
        setupClearFavoritesButton()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(
                    R.id.fragmentContainer,
                    recipesFragment,
                    TAG_RECIPES
                )
                .commit()

            supportFragmentManager.beginTransaction()
                .add(
                    R.id.fragmentContainer,
                    bookmarksFragment,
                    TAG_BOOKMARKS
                )
                .hide(bookmarksFragment)
                .commit()

            activeFragment = recipesFragment
            viewModel.showRecipes()
        }

        observeMainState()
    }

    private fun setupBottomNavigation() {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navRecipes -> {
                    viewModel.showRecipes()
                    supportFragmentManager.beginTransaction()
                        .hide(activeFragment)
                        .show(recipesFragment)
                        .commit()

                    activeFragment = recipesFragment

                    true
                }

                R.id.navBookmarks -> {
                    viewModel.showBookmarks()
                    supportFragmentManager.beginTransaction()
                        .hide(activeFragment)
                        .show(bookmarksFragment)
                        .commit()

                    activeFragment = bookmarksFragment

                    true
                }

                else -> false
            }
        }
    }

    private fun setupClearFavoritesButton() {
        val btnClearFavorites = findViewById<ImageButton>(R.id.btnClearFavorites)

        btnClearFavorites.setOnClickListener {
            val currentList = viewModel.state.value?.recipes.orEmpty()

            if (currentList.isEmpty()) {
                AlertDialog.Builder(this)
                    .setTitle("Favorites Empty!")
                    .setMessage("Your favorite list is empty.")
                    .setPositiveButton("OK", null)
                    .show()
            } else {
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
    }

    private fun observeMainState() {
        val btnClearFavorites = findViewById<ImageButton>(R.id.btnClearFavorites)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        viewModel.state.observe(this) { state ->
            val isBookmarks = state.mode == RecipeListViewModel.Mode.BOOKMARKS

            btnClearFavorites.visibility =
                if (isBookmarks) View.VISIBLE else View.GONE

            bottomNavigation.menu.findItem(R.id.navRecipes).isEnabled = !state.loading
            bottomNavigation.menu.findItem(R.id.navBookmarks).isEnabled = !state.loading
        }
    }

    private fun setupEdgeToEdgeInsets() {
        val root = findViewById<View>(R.id.rootLayout)
        val header = findViewById<View>(R.id.headerTopBar)
        val bottomNavigation = findViewById<View>(R.id.bottomNavigation)

        val originalHeaderPaddingTop = header.paddingTop
        val originalNavigationPaddingBottom = bottomNavigation.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            header.setPadding(
                header.paddingLeft,
                originalHeaderPaddingTop + systemBars.top,
                header.paddingRight,
                header.paddingBottom
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
        inAppUpdateManager.onResume()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == InAppUpdateManager.Companion.UPDATE_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(
                    this,
                    "New app version available! Update the app to continue.",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
    }

    companion object {
        private const val TAG_RECIPES = "RECIPES"
        private const val TAG_BOOKMARKS = "BOOKMARKS"
    }
}