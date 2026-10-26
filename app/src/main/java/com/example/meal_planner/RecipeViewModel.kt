package com.example.meal_planner

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
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
    override fun getAllRecipes() = mDao.getAllRecipes()

    override fun getAllRecipesWithDetails() = mDao.getAllRecipesWithDetails()

    override fun getAllSavedRecipes() = mDao.getSavedRecipesWithDetails()

    override fun getAllScheduledInfo() = mDao.getAllScheduledWithDetails()

    override fun getAllTags() = mDao.getAllTags()

    override fun getAllIngredients() = mDao.getAllIngredients()

    override fun getRecipeInfo(id: Int) = mDao.getRecipeWithDetailsById(id)

    override fun getScheduledInfo(id: Int) = mDao.getScheduledWithDetailsById(id)

    override fun newSchedule(
        date: LocalDate,
        mealType: MealType,
        recipeId: Int,
        haveCooked: Boolean
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            mDao.insertScheduledRecipe(
                ScheduledRecipe(
                    date = date,
                    mealType = mealType,
                    hasCooked = false,
                    recipeId = recipeId
                )
            )
        }
    }

    override fun updateRecipeSavedState(recipeId: Int, isSaved: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            mDao.updateRecipeSavedState(recipeId, isSaved)
        }
    }

    override fun updateRecipeCookedSate(scheduledId: Int, haveCooked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            mDao.updateScheduledCookedState(scheduledId, haveCooked)
        }
    }
}
