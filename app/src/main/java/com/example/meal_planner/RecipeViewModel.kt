package com.example.meal_planner

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class RecipeViewModel(application: Application) : AndroidViewModel(application), RecipeInterface {

    private val dao: RecipeDao = RecipeDatabase.getDatabase(application).recipeDao()

    override fun getAllRecipes(): Flow<List<RecipeInfo>> {
        return dao.getAllRecipesWithDetails().map { list ->
            list.map { it.toRecipeInfo() }
        }
    }

    override fun getAllSavedRecipes(): Flow<List<RecipeInfo>> {
        return dao.getSavedRecipesWithDetails().map { list ->
            list.map { it.toRecipeInfo() }
        }
    }

    override fun getAllScheduledInfo(): Flow<List<ScheduledRecipeInfo>> {
        return dao.getAllScheduledWithDetails().map { list ->
            list.map { it.toScheduledRecipeInfo() }
        }
    }

    override fun getAllTags(): Flow<List<Tag>> {
        return dao.getAllTags()
    }

    override fun getAllIngredients(): Flow<List<IngredientsInfo>> {
        return dao.getAllRecipeIngredientsWithDetail().map { list ->
            list.map {
                IngredientsInfo(
                    id = it.recipeIngredient.ingredientId,
                    name = it.ingredient.name,
                    quantity = it.recipeIngredient.quantity,
                    unit = it.recipeIngredient.unit
                )
            }
        }
    }

    override fun getImage(id: Int): Flow<List<Image>> {
        return dao.getImagesForRecipe(id)
    }

    override fun getRecipeInfo(id: Int): Flow<List<RecipeInfo>> {
        return dao.getRecipeWithDetailsById(id).map { item ->
            if (item != null) listOf(item.toRecipeInfo()) else emptyList()
        }
    }

    override fun getScheduledInfo(id: Int): Flow<List<ScheduledRecipeInfo>> {
        return dao.getScheduledWithDetailsById(id).map { item ->
            if (item != null) listOf(item.toScheduledRecipeInfo()) else emptyList()
        }
    }

    override fun newRecipe(
        name: String,
        description: String,
        instructions: List<String>,
        isSaved: Boolean,
        time: List<Int>
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val prep = time.getOrNull(0) ?: 0
            val cook = time.getOrNull(1) ?: 0
            val recipe = Recipe(
                name = name,
                description = description,
                instructions = instructions.joinToString("\n"),
                mealType = MealType.DINNER,
                prepTimeMinutes = prep,
                cookTimeMinutes = cook,
                isSaved = isSaved
            )
            dao.insertRecipe(recipe)
        }
    }

    override fun newImage(path: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertImage(Image(path = path, recipeId = 1))
        }
    }

    override fun newTag(name: String, recipe: Recipe) {
        viewModelScope.launch(Dispatchers.IO) {
            val tagId = dao.insertTag(Tag(name = name)).toInt()
            if (recipe.id != 0) {
                dao.insertRecipeTag(RecipeTag(recipeId = recipe.id, tagId = tagId))
            }
        }
    }

    override fun newIngredient(
        name: String,
        quantity: Float,
        unit: String,
        recipe: Recipe
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val ingredientId = dao.insertIngredient(Ingredient(name = name)).toInt()
            if (recipe.id != 0) {
                dao.insertRecipeIngredient(
                    RecipeIngredient(
                        recipeId = recipe.id,
                        ingredientId = ingredientId,
                        quantity = quantity,
                        unit = unit
                    )
                )
            }
        }
    }

    override fun newSchedule(
        date: LocalDateTime,
        mealType: MealType,
        haveCooked: Boolean,
        recipe: Recipe
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertScheduledRecipe(
                ScheduledRecipe(
                    date = date,
                    mealType = mealType,
                    hasCooked = haveCooked,
                    recipeId = recipe.id
                )
            )
        }
    }

    override fun deleteRecipe(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteRecipeById(id)
        }
    }

    override fun deleteImage(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteImageById(id)
        }
    }

    override fun deleteTag(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteTagById(id)
        }
    }

    override fun deleteIngredient(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteIngredientById(id)
        }
    }

    override fun deleteSchedule(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteScheduledRecipeById(id)
        }
    }

    override fun updateRecipeSavedState(recipeId: Int, isSaved: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.updateRecipeSavedState(recipeId, isSaved)
        }
    }

    override fun updateRecipeCookedSate(scheduledId: Int, haveCooked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.updateScheduledCookedState(scheduledId, haveCooked)
        }
    }
}
