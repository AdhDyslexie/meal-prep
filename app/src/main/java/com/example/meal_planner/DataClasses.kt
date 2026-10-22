package com.example.meal_planner

import androidx.room3.Embedded
import androidx.room3.Junction
import androidx.room3.Relation
import java.time.LocalDateTime

/* Class Diagram
class RecipeInfo {
	~id: int
	~name: String
	~description: String
	~ingredientsInfo: List~ingredientsInfo~
	~instruction: List~String~
	~tags: List~String~
	~prepTime: float
	~cookTime: float
	~imagePath: String
	~isSaved: bool
}

class ScheduledRecipeInfo {
	~id: int
	~date: Date
	~mealType: String
	~haveCooked: bool
	~RecipeInfo recipeInfo
}

class IngredientsInfo {
	~id: int
	~name: String
	~quantity: float
	~unit: String
}

ScheduledRecipeInfo ..> RecipeInfo : Needs
RecipeInfo ..> IngredientsInfo : Needs
*/

data class RecipeInfo(
    val id: Int,
    val name: String,
    val description: String,
    val ingredientsInfo: List<IngredientsInfo>,
    val instructions: List<String>,
    val tags: List<String>,
    val prepTime: Float,
    val cookTime: Float,
    val imagePath: String,
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
    val date: LocalDateTime,
    val mealType: String,
    val haveCooked: Boolean,
    val recipeInfo: RecipeInfo
)

// --- Room Relation Classes for Querying Composite Data ---

data class RecipeIngredientWithDetail(
    @Embedded val recipeIngredient: RecipeIngredient,
    @Relation(
        parentColumns = ["ingredientId"],
        entityColumns = ["id"]
    )
    val ingredient: Ingredient
)

data class RecipeWithDetails(
    @Embedded val recipe: Recipe,

    @Relation(
        parentColumns = ["id"],
        entityColumns = ["recipeId"]
    )
    val ingredients: List<RecipeIngredientWithDetail>,

    @Relation(
        parentColumns = ["id"],
        entityColumns = ["id"],
        associateBy = Junction(
            value = RecipeTag::class,
            parentColumns = ["recipeId"],
            entityColumns = ["tagId"]
        )
    )
    val tags: List<Tag>,

    @Relation(
        parentColumns = ["id"],
        entityColumns = ["recipeId"]
    )
    val images: List<Image>
)

data class ScheduledRecipeWithDetails(
    @Embedded val scheduledRecipe: ScheduledRecipe,

    @Relation(
        entity = Recipe::class,
        parentColumns = ["recipeId"],
        entityColumns = ["id"]
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
        imagePath = images.firstOrNull()?.path ?: "",
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