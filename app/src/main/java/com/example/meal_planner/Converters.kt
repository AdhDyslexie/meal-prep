package com.example.meal_planner

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromMealType(mealType: MealType): String {
        return mealType.name
    }

    @TypeConverter
    fun toMealType(value: String): MealType {
        return MealType.valueOf(value)
    }
}
