
package com.example.meal_planner

import android.R
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDate
import java.time.LocalDateTime


@Entity
@Serializable
data class Recipe(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val instructions: String, // List stored as single string
    val image: String = "",
    val mealType: MealType,
    val prepTimeMinutes: Int,
    val cookTimeMinutes: Int,
    val isSaved: Boolean = false
)

@Entity
@Serializable
data class Tag(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
)

@Entity(
    primaryKeys = ["recipeId", "tagId"],
    foreignKeys = [
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Tag::class,
            parentColumns = ["id"],
            childColumns = ["tagId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
@Serializable
data class RecipeTag(
    val recipeId: Int,
    val tagId: Int
)

@Entity
@Serializable
data class Ingredient(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
)

@Entity(
    primaryKeys = ["recipeId", "ingredientId"],
    foreignKeys = [
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Ingredient::class,
            parentColumns = ["id"],
            childColumns = ["ingredientId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
@Serializable
data class RecipeIngredient(
    val recipeId: Int,
    val ingredientId: Int,
    val quantity: Float,
    val unit: String
)

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
@Serializable
data class ScheduledRecipe(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String, // Store as: YYYY-MM-DD
    val mealType: MealType,
    val hasCooked: Boolean,
    val recipeId: Int
)