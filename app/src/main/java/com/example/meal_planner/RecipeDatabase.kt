package com.example.meal_planner

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
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
        ScheduledRecipe::class
    ],
    version = 1
)
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
            dao.insertTag(Tag(name = "Italian"))
            dao.insertTag(Tag(name = "Quick & Easy"))
            dao.insertTag(Tag(name = "Healthy"))

            // Seed Ingredients
            dao.insertIngredient(Ingredient(name = "Spaghetti"))
            dao.insertIngredient(Ingredient(name = "Eggs"))
            dao.insertIngredient(Ingredient(name = "Pancetta"))
            dao.insertIngredient(Ingredient(name = "Parmesan Cheese"))
            dao.insertIngredient(Ingredient(name = "Oats"))
            dao.insertIngredient(Ingredient(name = "Milk"))

            // Seed Recipe
            dao.insertRecipe(
                Recipe(
                    name = "Spaghetti Carbonara",
                    description = "Classic Italian pasta dish with eggs, cheese, and pancetta.",
                    instructions = "1. Boil pasta.\n2. Fry pancetta.\n3. Whisk eggs and cheese.\n4. Combine all ingredients.",
                    mealType = MealType.DINNER,
                    prepTimeMinutes = 10,
                    cookTimeMinutes = 15,
                    isSaved = true
                )
            )

            Log.i("RecipeDatabase", "Database seeding completed successfully!")
        }
    }
}
