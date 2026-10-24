package com.example.meal_planner

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import java.time.LocalDateTime
import kotlinx.datetime.LocalDate


data class RecipeInfo(
    val id: Int,
    val name: String,
    val description: String,
    val ingredientsInfo: List<IngredientsInfo>,
    val instructions: List<String>,
    val tags: List<String>,
    val prepTime: Float,
    val cookTime: Float,
    val image: String = "",
    val isSaved: Boolean
)

data class IngredientsInfo(
    val id: Int,
    val name: String,
    val quantity: Float,
    val unit: String
)

data class ScheduledRecipeInfo(
    val id: Int,
    val date: LocalDate, // Store as YYYY-MM-DD
    val mealType: String,
    val haveCooked: Boolean,
    val recipeInfo: RecipeInfo
)

// --- Room Relation Classes for Querying Composite Data ---


data class RecipeIngredientWithDetail(
    @Embedded val recipeIngredient: RecipeIngredient,
    @Relation(
        parentColumn = "ingredientId",
        entityColumn = "id"
    )
    val ingredient: Ingredient
)


data class RecipeWithDetails(
    @Embedded val recipe: Recipe,

    @Relation(
        entity = RecipeIngredient::class,
        parentColumn = "id",
        entityColumn = "recipeId"
    )
    val ingredients: List<RecipeIngredientWithDetail>,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = RecipeTag::class,
            parentColumn = "recipeId",
            entityColumn = "tagId"
        )
    )
    val tags: List<Tag>,
)


data class ScheduledRecipeWithDetails(
    @Embedded val scheduledRecipe: ScheduledRecipe,

    @Relation(
        entity = Recipe::class,
        parentColumn = "recipeId",
        entityColumn = "id"
    )
    val recipeWithDetails: RecipeWithDetails
)

// --- Mapping Extension Functions ---

fun RecipeWithDetails.toRecipeInfo(): RecipeInfo {
    return RecipeInfo(
        id = recipe.id,
        name = recipe.name,
        description = recipe.description,
        ingredientsInfo = ingredients.map {
            IngredientsInfo(
                id = it.recipeIngredient.ingredientId,
                name = it.ingredient.name,
                quantity = it.recipeIngredient.quantity,
                unit = it.recipeIngredient.unit
            )
        },
        instructions = recipe.instructions.split("\n").filter { it.isNotBlank() },
        tags = tags.map { it.name },
        prepTime = recipe.prepTimeMinutes.toFloat(),
        cookTime = recipe.cookTimeMinutes.toFloat(),
        isSaved = recipe.isSaved
    )
}

fun ScheduledRecipeWithDetails.toScheduledRecipeInfo(): ScheduledRecipeInfo {
    return ScheduledRecipeInfo(
        id = scheduledRecipe.id,
        date = scheduledRecipe.date,
        mealType = scheduledRecipe.mealType.name,
        haveCooked = scheduledRecipe.hasCooked,
        recipeInfo = recipeWithDetails.toRecipeInfo()
    )
}
