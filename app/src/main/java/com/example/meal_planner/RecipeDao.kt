package com.example.meal_planner

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    // --- Inserts ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: Recipe): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: Tag): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredient(ingredient: Ingredient): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipeTag(recipeTag: RecipeTag)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipeIngredient(recipeIngredient: RecipeIngredient)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheduledRecipe(scheduledRecipe: ScheduledRecipe): Long

    // --- Relation Queries ---

    @Transaction
    @Query("SELECT * FROM Recipe")
    fun getAllRecipes(): Flow<List<Recipe>>

    @Transaction
    @Query("SELECT * FROM Recipe")
    fun getAllRecipesWithDetails(): Flow<List<RecipeWithDetails>>

    @Transaction
    @Query("SELECT * FROM Recipe WHERE isSaved = 1")
    fun getSavedRecipesWithDetails(): Flow<List<RecipeWithDetails>>

    @Transaction
    @Query("SELECT * FROM Recipe WHERE id = :id")
    fun getRecipeWithDetailsById(id: Int): Flow<RecipeWithDetails?>

    @Transaction
    @Query("SELECT * FROM ScheduledRecipe")
    fun getAllScheduledWithDetails(): Flow<List<ScheduledRecipeWithDetails>>

    @Transaction
    @Query("SELECT * FROM ScheduledRecipe WHERE id = :id")
    fun getScheduledWithDetailsById(id: Int): Flow<ScheduledRecipeWithDetails?>

    // --- Entity Queries ---

    @Query("SELECT * FROM Tag")
    fun getAllTags(): Flow<List<Tag>>

    @Query("SELECT * FROM Ingredient")
    fun getAllIngredients(): Flow<List<Ingredient>>

    @Transaction
    @Query("SELECT * FROM RecipeIngredient")
    fun getAllRecipeIngredientsWithDetail(): Flow<List<RecipeIngredientWithDetail>>

    // --- Updates ---

    @Query("UPDATE Recipe SET isSaved = :isSaved WHERE id = :recipeId")
    suspend fun updateRecipeSavedState(recipeId: Int, isSaved: Boolean)

    @Query("UPDATE ScheduledRecipe SET hasCooked = :haveCooked WHERE id = :scheduledId")
    suspend fun updateScheduledCookedState(scheduledId: Int, haveCooked: Boolean)

    // --- Deletes ---

    @Query("DELETE FROM Recipe WHERE id = :id")
    suspend fun deleteRecipeById(id: Int)

    @Query("DELETE FROM Tag WHERE id = :id")
    suspend fun deleteTagById(id: Int)

    @Query("DELETE FROM Ingredient WHERE id = :id")
    suspend fun deleteIngredientById(id: Int)

    @Query("DELETE FROM ScheduledRecipe WHERE id = :id")
    suspend fun deleteScheduledRecipeById(id: Int)

    @Query("DELETE FROM Recipe")
    suspend fun deleteAllRecipes()

    @Query("DELETE FROM Tag")
    suspend fun deleteAllTags()

    @Query("DELETE FROM Ingredient")
    suspend fun deleteAllIngredients()

    @Query("DELETE FROM ScheduledRecipe")
    suspend fun deleteAllScheduledRecipes()
}
