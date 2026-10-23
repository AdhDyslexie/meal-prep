package com.example.meal_planner

import androidx.room.TypeConverter
import kotlinx.datetime.LocalDate

class Converters {
    @TypeConverter
    fun fromMealType(mealType: MealType): String {
        return mealType.name
    }

    @TypeConverter
    fun toMealType(value: String): MealType {
        return MealType.valueOf(value)
    }

    @TypeConverter
    fun fromDate(date: LocalDate): String {
        return date.toString()
    }

    @TypeConverter
    fun toDate(value : String): LocalDate {
        return LocalDate.parse(value)
    }
}
