
package com.example.meal_planner

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey
import kotlinx.serialization.Serializable
import java.time.LocalDateTime


/*
// Meal planner ERD

Recipe {
	int id PK
	string name
	string description
	string instruction
	boolean is_saved
	int prep_time_minutes
	int cook_time_minutes
}
Ingredient {
	int id PK
	string name
}
RecipeIngredient {
	int recipe_id FK
	int ingredient_id FK
	float quantity
	string unit
}
Tag {
	int id PK
	string name
}
RecipeTag {
	int recipe_id FK
	int tag_id FK
}

IMAGE {
	int id PK
	string path
	int recipe_id FK
}

ScheduledRecipe {
	int id PK
	datetime date
	string meal_type
	boolean have_cooked
	int recipe_id FK
}

Recipe ||--o| IMAGE : has
RecipeIngredient }|--|| Recipe : includes
RecipeIngredient }|--|| Ingredient : uses
RecipeTag }|--|| Recipe : tagged_with
RecipeTag }|--|| Tag : belongs_too
ScheduledRecipe }|--|| Recipe : schedules
*/

@Entity
@Serializable
data class Recipe(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val instructions: String, // List stored as single string
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
data class Image(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val path: String,
    val recipeId: Int
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
    val date: LocalDateTime,
    val mealType: MealType,
    val hasCooked: Boolean,
    val recipeId: Int
)