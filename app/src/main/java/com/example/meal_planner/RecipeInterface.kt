package com.example.meal_planner

import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/* Class Diagram
class RecipeInterface {
	<<interface>>

	~getAllRecipes() Flow~List~RecipeInfo~~
	~getallSavedRecipes() Flow~List~RecipeInfo~~
	~getAllScheduledInfo() Flow~List~ScheduledRecipeInfo~~
	~getAllTags() Flow~List~Tag~~
	~getAllIngredients() Flow~List~IngredientsInfo~~
	~getRecipeInfo(id: int) Flow~List~RecipeInfo~~
	~getScheduledInfo(id: int) Flow~List~ScheduledRecipeInfo~~

	~newRecipe(name: String, description: String, instructions: List~String~, isSaved: Boolean. time: List<Int>)
	~newTag(name: String, recipe: Recipe)
	~newIngredient(name: String, quantity: float, unit: String, recipe: Recipe)
	~newSchedule(date: datetime, mealType: String, haveCooked: Boolean, recipe: Recipe)

	~deleteRecipe(id: int)
	~deleteTag(id: int)
	~deleteIngredient(id: int)
	~deleteSchedule(id: int)

	~updateRecipeSavedState(recipeId: int, isSaved: Boolean)
	~updateRecipeCookedSate(scheduledId: int, haveCooked: Boolean)
}
*/



interface RecipeInterface {
    fun getAllRecipes(): Flow<List<Recipe>>
    fun getAllSavedRecipes(): Flow<List<RecipeInfo>>
    fun getAllScheduledInfo(): Flow<List<ScheduledRecipeInfo>>
    fun getAllTags(): Flow<List<Tag>>
    fun getAllIngredients(): Flow<List<IngredientsInfo>>
    fun getRecipeInfo(id: Int): Flow<List<RecipeInfo>>
    fun getScheduledInfo(id: Int): Flow<List<ScheduledRecipeInfo>>
    fun newRecipe(name: String, description: String, instructions: List<String>, image: String = "", isSaved: Boolean, time: List<Int>)
    fun newTag(name: String, recipe: Recipe)
    fun newIngredient(name: String, quantity: Float, unit: String, recipe: Recipe)
    fun newSchedule(date: LocalDateTime, mealType: MealType, haveCooked: Boolean, recipe: Recipe)

    fun deleteRecipe(id: Int)
    fun deleteTag(id: Int)
    fun deleteIngredient(id: Int)
    fun deleteSchedule(id: Int)

    fun updateRecipeSavedState(recipeId: Int, isSaved: Boolean)
    fun updateRecipeCookedSate(scheduledId: Int, haveCooked: Boolean)
}