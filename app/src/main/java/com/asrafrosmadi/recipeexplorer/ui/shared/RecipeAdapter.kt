package com.asrafrosmadi.recipeexplorer.ui.shared

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.LifecycleCoroutineScope
import com.asrafrosmadi.recipeexplorer.R
import com.asrafrosmadi.recipeexplorer.data.model.Recipe
import com.asrafrosmadi.recipeexplorer.util.ImageLoader

class RecipeAdapter(
    private val scope: LifecycleCoroutineScope,
    private val isFavorite: (Int) -> Boolean,
    private val onFavorite: (Recipe) -> Unit,
    private val onClick: (Recipe) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.VH>() {
    private val items = mutableListOf<Recipe>()

    fun submit(list: List<Recipe>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_recipe, parent, false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) =
        holder.bind(items[position])

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        private val img: ImageView = view.findViewById(R.id.imgRecipe)
        private val title: TextView = view.findViewById(R.id.titleText)
        private val rating: TextView = view.findViewById(R.id.metaRating)
        private val reviewCount: TextView = view.findViewById(R.id.metaReviewCount)
        private val meta: TextView = view.findViewById(R.id.metaText)
        private val tags: TextView = view.findViewById(R.id.tagsText)
        private val fav: ImageButton = view.findViewById(R.id.favButton)

        fun bind(recipe: Recipe) {
            title.text = recipe.name
            rating.text = "⭐ ${recipe.rating}"
            reviewCount.text = "${recipe.reviewCount} reviews"
            meta.text = "${recipe.cuisine} • ${recipe.difficulty} • ${recipe.prepTimeMinutes + recipe.cookTimeMinutes} min"
            tags.text = (recipe.mealType + recipe.tags).distinct().joinToString(" • ")
            fav.setImageResource(if (isFavorite(recipe.id)) R.drawable.ic_star_filled else R.drawable.ic_star_outline)
            ImageLoader.load(itemView.context, recipe.image, img, scope)
            itemView.setOnClickListener { onClick(recipe) }
            fav.setOnClickListener { onFavorite(recipe); notifyItemChanged(bindingAdapterPosition) }
        }
    }
}
