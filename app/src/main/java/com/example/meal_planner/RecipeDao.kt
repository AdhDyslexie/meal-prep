package com.example.meal_planner

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    @Insert
    fun insert(recipe: Recipe)
    @Insert
    fun insert(tag: Tag)
    @Insert
    fun insert(ingredient: Ingredient)
    @Insert
    fun insert(scheduledRecipe: ScheduledRecipe)
    @Insert
    fun insert(image: Image)
    @Insert
    fun insert(recipeTag: RecipeTag)
    @Insert
    fun insert(recipeIngredient: RecipeIngredient)

    @Query("SELECT * FROM Recipe")
    fun allRecipes(): Flow<List<Recipe>>

    @Query("SELECT * FROM Recipe WHERE isSaved = true")
    fun allSavedRecipes(): Flow<List<Recipe>>

    @Query("SELECT * FROM Tag")
    fun allTags(): List<Tag>

    @Query("SELECT * FROM Ingredient")
    fun allIngredients(): Flow<List<Ingredient>>

    @Query("SELECT * FROM Image")
    fun allImages(): Flow<List<Image>>

// REFERENCE:
//    @Query("SELECT courseoffering.id as id, course.name AS coursename, lecturer.name AS lecturername, courseoffering.year AS year, courseoffering.semester AS semester FROM course,courseoffering,lecturer WHERE lecturer.name=:lect AND lecturerId=lecturer.id AND courseId=course.id ORDER BY "+
//            "CASE :ordering WHEN 'year' THEN year " +
//            "WHEN 'semester' THEN semester "+
//            "WHEN 'coursename' THEN coursename END DESC")

    @Transaction()
    @Query("SELECT Recipe.id AS id, Recipe.name AS name, Recipe.description AS description, Recipe.instructions AS instructions, Recipe.prepTimeMinutes AS prepTimeMinutes, Recipe.cookTimeMinutes AS cookTimeMinutes, Recipe.isSaved AS isSaved FROM Recipe")
    fun allRecipeInfo(): Flow<List<RecipeInfo>>




    @Query("DELETE * FROM Recipe Tag RecipeTag Ingredient RecipeIngredient Image ScheduledRecipe")
    fun deleteAll()

    @Query("DELETE FROM Recipe WHERE id = :recipe.id")
    fun delete(recipe: Recipe)

    @Query("DELETE FROM Tag WHERE id = :tag.id")
    fun delete(tag: Tag)

    @Query("DELETE FROM Ingredient WHERE id = :ingredient.id")
    fun delete(ingredient: Ingredient)

    @Query("DELETE FROM ScheduledRecipe WHERE id = :scheduledRecipe.id")
    fun delete(scheduledRecipe: ScheduledRecipe)

}