package com.example.meal_planner

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

class RecipeViewModel : ViewModel(), RecipeInterface {
    // NOTE: You only need to use coroutines for writes to the database

    override fun getAllRecipes(): Flow<List<RecipeInfo>> {
        TODO("Not yet implemented")
    }

    override fun getAllSavedRecipes(): Flow<List<RecipeInfo>> {
        TODO("Not yet implemented")
    }

    override fun getAllScheduledInfo(): Flow<List<ScheduledRecipeInfo>> {
        TODO("Not yet implemented")
    }

    override fun getAllTags(): Flow<List<Tag>> {
        TODO("Not yet implemented")
    }

    override fun getAllIngredients(): Flow<List<IngredientsInfo>> {
        TODO("Not yet implemented")
    }

    override fun getImage(id: Int): Flow<List<Image>> {
        TODO("Not yet implemented")
    }

    override fun getRecipeInfo(id: Int): Flow<List<RecipeInfo>> {
        TODO("Not yet implemented")
    }

    override fun getScheduledInfo(id: Int): Flow<List<ScheduledRecipeInfo>> {
        TODO("Not yet implemented")
    }

    override fun newRecipe(
        name: String,
        description: String,
        instructions: List<String>,
        isSaved: Boolean,
        time: List<Int>
    ) {
        TODO("Not yet implemented")
    }

    override fun newImage(path: String) {
        TODO("Not yet implemented")
    }

    override fun newTag(name: String, recipe: Recipe) {
        TODO("Not yet implemented")
    }

    override fun newIngredient(
        name: String,
        quantity: Float,
        unit: String,
        recipe: Recipe
    ) {
        TODO("Not yet implemented")
    }

    override fun newSchedule(
        date: LocalDateTime,
        mealType: MealType,
        haveCooked: Boolean,
        recipe: Recipe
    ) {
        TODO("Not yet implemented")
    }

    override fun deleteRecipe(id: Int) {
        TODO("Not yet implemented")
    }

    override fun deleteImage(id: Int) {
        TODO("Not yet implemented")
    }

    override fun deleteTag(id: Int) {
        TODO("Not yet implemented")
    }

    override fun deleteIngredient(id: Int) {
        TODO("Not yet implemented")
    }

    override fun deleteSchedule(id: Int) {
        TODO("Not yet implemented")
    }

    override fun updateRecipeSavedState(recipeId: Int, isSaved: Boolean) {
        TODO("Not yet implemented")
    }

    override fun updateRecipeCookedSate(scheduledId: Int, haveCooked: Boolean) {
        TODO("Not yet implemented")
    }
}