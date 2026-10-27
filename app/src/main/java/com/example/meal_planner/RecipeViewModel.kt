package com.example.meal_planner

import android.app.Application
import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import java.time.LocalDateTime

@Suppress("UNCHECKED_CAST")
class RecipeViewModelFactory(val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RecipeViewModel(context) as T
    }
}
class RecipeViewModel(context: Context) : ViewModel(), RecipeInterface {

    // REMINDER: Only function that modify the db (create, update, delete) need coroutines.

    var shoppingList by mutableStateOf<List<ShoppingItem>?>(null)

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

    fun getWeekDaysFor(targetDate: LocalDate): List<DayCellInfo> {
        // Find the offset from Monday (ISO day number: Monday = 1, Sunday = 7)
        val daysFromMonday = targetDate.dayOfWeek.isoDayNumber - 1

        // Subtract those days to land precisely on Monday of this week
        val monday = targetDate.minus(daysFromMonday, DateTimeUnit.DAY)

        // Loop through 0 to 6 to generate Monday through Sunday
        return (0..6).map { offset ->
            val currentDay = monday.plus(offset, DateTimeUnit.DAY)
            DayCellInfo(
                date = currentDay,
                month = currentDay.month,
                dayOfMonth = currentDay.day,
                dayOfWeek = currentDay.dayOfWeek
            )
        }
    }

    fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val (height: Int, width: Int) = options.outHeight to options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    // List should remain ephemeral
    fun generateShoppingList(scheduledMeals: List<ScheduledRecipeInfo>): List<ShoppingItem> {
        val map = mutableMapOf<String, ShoppingItem>()

        for (meal in scheduledMeals) {
            for (ingredient in meal.recipeInfo.ingredientsInfo) {
                val key = ingredient.name.lowercase().trim()
                val existing = map[key]

                if (existing != null) {
                    // Combine quantities for duplicate ingredients
                    map[key] = existing.copy(quantity = existing.quantity + ingredient.quantity)
                } else {
                    map[key] = ShoppingItem(
                        id = key,
                        name = ingredient.name,
                        quantity = ingredient.quantity,
                        unit = ingredient.unit
                    )
                }
            }
        }
        return map.values.toList()
    }

    fun getDrawableIdForRecipe(imageName: String): Int {
        return when (imageName) {
            "banana_blueberry_overnight_oats" -> R.drawable.banana_blueberry_overnight_oats
            "banana_chia_oatmeal" -> R.drawable.banana_chia_oatmeal
            "beef_and_avocado_tacos" -> R.drawable.beef_and_avocado_tacos
            "blueberry_pancakes" -> R.drawable.blueberry_pancakes
            "chicken_and_brown_rice_soup" -> R.drawable.chicken_and_brown_rice_soup
            "chicken_and_vegetable_stirfry" -> R.drawable.chicken_and_vegetable_stirfry
            "chickpea_coconut_curry" -> R.drawable.chickpea_coconut_curry
            "coconut_lentil_curry" -> R.drawable.coconut_lentil_curry
            "ginger_soy_salmon_with_broccoli" -> R.drawable.ginger_soy_salmon_with_broccoli
            "lentil_and_vegetable_soup" -> R.drawable.lentil_and_vegetable_soup
            "mediterranean_chickpea_quinoa_salad" -> R.drawable.mediterranean_chickpea_quinoa_salad
            "mediterranean_quinoa_bowl" -> R.drawable.mediterranean_quinoa_bowl
            "spaghetti_carbonara" -> R.drawable.spaghetti_carbonara
            "spinach_and_feta_omelet" -> R.drawable.spinach_and_feta_omelet
            "spinach_and_feta_scramble" -> R.drawable.spinach_and_feta_scramble
            "tofu_and_vegetable_stirfry" -> R.drawable.tofu_and_vegetable_stirfry
            else -> 0
        }
    }

}
