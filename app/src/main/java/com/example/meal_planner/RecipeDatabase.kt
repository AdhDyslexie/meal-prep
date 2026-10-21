package com.example.meal_planner

import android.content.Context
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.Dispatchers
import android.util.Log
import androidx.room3.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Recipe::class, Tag::class, RecipeTag::class, Ingredient::class, RecipeIngredient::class, Image::class, ScheduledRecipe::class], version = 1)
abstract class RecipeDatabase: RoomDatabase() {
    abstract fun UniDao(): RecipeDao
    companion object {
        @Volatile
        private var Instance: RecipeDatabase? = null
        fun getDatabase(context: Context): RecipeDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context.applicationContext, RecipeDatabase::class.java, "uni_database")
                    .addCallback(roomDatabaseCallback)
                    .build()
                    .also { Instance = it }
            }
        }
    }
    private val roomDatabaseCallback: Callback = object : Callback() {
        fun onCreate(db: SupportSQLiteDatabase) { // Maybe needs override?
            CoroutineScope(Dispatchers.IO).launch {
                Log.i("RecipeDatabase", "onCreate")
            }
        }
    }
}