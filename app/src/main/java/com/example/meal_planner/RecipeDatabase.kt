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
import kotlinx.datetime.LocalDate
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

            // Seed Master Tags & cache their IDs in a Map
            val tagDictionary = mutableMapOf<String, Int>()
            for (tagName in DatabaseData.tags) {
                val id = dao.insertTag(Tag(name=tagName)).toInt()
                tagDictionary[tagName] = id
            }

            // Seed Master Ingredients & cache their IDs in a Map
            val ingredientDictionary = mutableMapOf<String, Int>()
            for (ingName in DatabaseData.ingredients) {
                val id = dao.insertIngredient(Ingredient(name=ingName)).toInt()
                ingredientDictionary[ingName] = id
            }

            // Seed recipes & connect them with tags & ingredients
            for (seed in DatabaseData.recipes) {
                val recipeId = dao.insertRecipe(seed.recipe).toInt()

                // Link Tags
                for (tagName in seed.tags) {
                    // retrieve tag from db or insert if not
                    val tagID = tagDictionary.getOrPut(tagName) {
                        dao.insertTag(Tag(name = tagName)).toInt()
                    }
                    dao.insertRecipeTag( // insert junction table entry
                        RecipeTag(recipeId, tagID)
                    )
                }

                // Link Ingredients
                for (ing in seed.ingredients) {
                    // retrieve ingredient from db or insert if not
                    val ingredientId = ingredientDictionary.getOrPut(ing.name) {
                        dao.insertIngredient(Ingredient(name = ing.name)).toInt()
                    }
                    dao.insertRecipeIngredient( // insert junction table entry
                        RecipeIngredient(
                            recipeId,
                            ingredientId,
                            ing.quantity,
                            ing.unit
                        )
                    )
                }
            }
            Log.i("RecipeDatabase", "Database seeding completed successfully!")
        }
    }
}
