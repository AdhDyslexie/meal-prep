package com.example.meal_planner

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

// Method to accesses db from UI
interface RecipeInterface {
    // --- Queries ---
    fun getAllRecipes(): Flow<List<Recipe>>
    fun getAllRecipesWithDetails(): Flow<List<RecipeWithDetails>>
    fun getAllSavedRecipes(): Flow<List<RecipeWithDetails>>
    fun getAllScheduledInfo(): Flow<List<ScheduledRecipeWithDetails>>
    fun getAllTags(): Flow<List<Tag>>
    fun getAllIngredients(): Flow<List<Ingredient>>
    fun getRecipeInfo(id: Int): Flow<RecipeWithDetails?>
    fun getScheduledInfo(id: Int): Flow<ScheduledRecipeWithDetails?>

    // --- Insertions ---
//    fun newRecipe(name: String, description: String, instructions: List<String>, image: String = "", isSaved: Boolean, time: List<Int>)
//    fun newTag(name: String, recipe: Recipe)
//    fun newIngredient(name: String, quantity: Float, unit: String, recipe: Recipe)
    fun newSchedule(date: LocalDate, mealType: MealType, recipeId: Int, haveCooked: Boolean = false)

    // --- Deletions ---
//    fun deleteRecipe(id: Int)
//    fun deleteTag(id: Int)
//    fun deleteIngredient(id: Int)
//    fun deleteSchedule(id: Int)

    // --- Updates ---
    fun updateRecipeSavedState(recipeId: Int, isSaved: Boolean)
    fun updateRecipeCookedSate(scheduledId: Int, haveCooked: Boolean)
}