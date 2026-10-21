package com.example.meal_planner
import java.time.LocalDateTime

//data class DataClasses()

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