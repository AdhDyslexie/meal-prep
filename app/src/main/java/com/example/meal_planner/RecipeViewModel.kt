package com.example.meal_planner

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@Suppress("UNCHECKED_CAST")
class RecipeViewModelFactory(val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RecipeViewModel(context) as T
    }
}
class RecipeViewModel(context: Context) : ViewModel(), RecipeInterface {

    // REMINDER: Only function that modify the db (create, update, delete) need coroutines.

    val mDao: RecipeDao
    init {
        val db = RecipeDatabase.getDatabase(context)
        mDao = db.RecipeDao()
    }
    override fun getAllRecipes() = mDao.getAllRecipesWithDetails()


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
        image: String,
        isSaved: Boolean,
        time: List<Int>
    ) {
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
