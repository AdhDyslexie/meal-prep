package com.example.meal_planner

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@Database(
    entities = [
        Recipe::class,
        Tag::class,
        RecipeTag::class,
        Ingredient::class,
        RecipeIngredient::class,
//        ScheduledRecipe::class
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class RecipeDatabase : RoomDatabase() {
    abstract fun RecipeDao(): RecipeDao

    companion object {
        @Volatile
        private var INSTANCE: RecipeDatabase? = null
        fun getDatabase(context: Context): RecipeDatabase {
            if (INSTANCE == null) {
                Log.i("RecipeDatabase", "Creating database...")
            } else {
                Log.i("RecipeDatabase", "Retrieving database...")
            }

            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    RecipeDatabase::class.java,
                    "recipe_database"
                )
                    .addCallback(recipeDatabaseCallback)
                    .build()
                    .also { INSTANCE = it }
            }
        }

        private val recipeDatabaseCallback: Callback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                Log.i("RecipeDatabase", "Database onCreate called...")
                INSTANCE?.let { db ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(db.RecipeDao())
                    }
                    Log.i("RecipeDatabase", "Database should be seed here!")
                }
            }
        }
        suspend fun populateDatabase(dao: RecipeDao) {
            Log.i("RecipeDatabase", "Seeding database...")

            // Seed Tags
            val tag1 = dao.insertTag(Tag(name = "Italian")).toInt()
            val tag2 = dao.insertTag(Tag(name = "Quick & Easy")).toInt()
            val tag3 = dao.insertTag(Tag(name = "Healthy")).toInt()

            // Seed Ingredients
            val ing1 = dao.insertIngredient(Ingredient(name = "Spaghetti")).toInt()
            val ing2 = dao.insertIngredient(Ingredient(name = "Eggs")).toInt()
            val ing3 = dao.insertIngredient(Ingredient(name = "Pancetta")).toInt()
            val ing4 = dao.insertIngredient(Ingredient(name = "Parmesan Cheese")).toInt()
            val ing5 = dao.insertIngredient(Ingredient(name = "Oats")).toInt()
            val ing6 = dao.insertIngredient(Ingredient(name = "Milk")).toInt()

            // Seed Recipe
            val recipe1 = dao.insertRecipe(
                Recipe(
                    name = "Spaghetti Carbonara",
                    description = "Classic Italian pasta dish with eggs, cheese, and pancetta.",
                    instructions = "1. Boil pasta.\n2. Fry pancetta.\n3. Whisk eggs and cheese.\n4. Combine all ingredients.",
                    mealType = MealType.DINNER,
                    prepTimeMinutes = 10,
                    cookTimeMinutes = 15,
                    isSaved = true
                )
            ).toInt()

            // Seed Recipe Tags
            dao.insertRecipeTag(RecipeTag(recipeId = recipe1, tagId = tag1))
            dao.insertRecipeTag(RecipeTag(recipeId = recipe1, tagId = tag2))
            dao.insertRecipeTag(RecipeTag(recipeId = recipe1, tagId = tag3))

            // Seed Recipe Ingredients
            dao.insertRecipeIngredient(RecipeIngredient(recipeId = recipe1, ingredientId = ing1,
                quantity = 100f, unit = "grams"))
            dao.insertRecipeIngredient(RecipeIngredient(recipeId = recipe1, ingredientId = ing2,
                quantity = 1f, unit = ""))
            dao.insertRecipeIngredient(RecipeIngredient(recipeId = recipe1, ingredientId = ing3,
                quantity = 100f, unit = "grams"))
            dao.insertRecipeIngredient(RecipeIngredient(recipeId = recipe1, ingredientId = ing4,
                quantity = 50f, unit = "grams"))
            dao.insertRecipeIngredient(RecipeIngredient(recipeId = recipe1, ingredientId = ing5,
                quantity = 100f, unit = "grams"))
            dao.insertRecipeIngredient(RecipeIngredient(recipeId = recipe1, ingredientId = ing6,
                quantity = 100f, unit = "grams"))

            Log.i("RecipeDatabase", "Database seeding completed successfully!")
        }
    }
}
