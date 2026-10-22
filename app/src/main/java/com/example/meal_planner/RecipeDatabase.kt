package com.example.meal_planner

import android.content.Context
import android.util.Log
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.SQLiteConnection
import java.time.LocalDateTime

@Database(
    entities = [
        Recipe::class,
        Tag::class,
        RecipeTag::class,
        Ingredient::class,
        RecipeIngredient::class,
        Image::class,
        ScheduledRecipe::class
    ],
    version = 1
)
abstract class RecipeDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao

    companion object {
        @Volatile
        private var INSTANCE: RecipeDatabase? = null

        fun getDatabase(context: Context): RecipeDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    RecipeDatabase::class.java,
                    "recipe_database"
                )
                    .addCallback(RecipeDatabaseCallback())
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }

    data class IngredientSeed(
        val name: String,
        val quantity: Float = 1f,
        val unit: String = "g"
    )
    data class RecipeSeed(
        val recipe: Recipe,
        val tags: List<String>,
        val ingredients: List<IngredientSeed>,
        val imagePath: String = ""
    )

    private class RecipeDatabaseCallback : RoomDatabase.Callback() {
        override suspend fun onCreate(connection: SQLiteConnection) {
            super.onCreate(connection)
            INSTANCE?.let { database ->
                populateDatabase(database.recipeDao())
            }
        }

        val tags = listOf(
            // Diet and dietary requirements
            "Vegetarian", "Vegan", "Pescatarian", "Flexitarian", "Plant-Based",
            "Gluten-Free", "Dairy-Free", "Nut-Free", "Egg-Free", "Soy-Free",
            "Keto", "Low-Carb", "Paleo", "Whole30",

            // Main ingredient
            "Poultry", "Chicken", "Turkey", "Seafood", "Fish", "Beef",
            "Pork", "Lamb", "Eggs", "Legumes", "Tofu", "Mushrooms",

            // Cuisine
            "Italian", "Mexican", "Japanese", "Mediterranean", "French",
            "European", "American", "Indian", "Asian", "Chinese", "Thai",
            "Korean", "Greek", "Middle Eastern", "Spanish", "Caribbean",

            // Meal, preparation, and nutrition
            "Breakfast", "Brunch", "Lunch", "Dinner", "Snack", "Dessert",
            "High-Protein", "Low-Calorie", "High-Fiber", "Heart-Healthy",
            "Quick & Easy", "One-Pot", "Meal Prep", "Kid-Friendly",
            "Comfort Food", "Spicy", "Budget-Friendly", "Seasonal"
        )


        val ingredients = listOf(
            "Rolled Oats", "Milk", "Banana", "Chia Seeds", "Honey",
            "Eggs", "Spinach", "Feta Cheese", "Olive Oil",
            "All-Purpose Flour", "Baking Powder", "Blueberries", "Maple Syrup",
            "Quinoa", "Chickpeas", "Cucumber", "Cherry Tomatoes", "Red Onion",
            "Lemon", "Parsley",
            "Chicken Breast", "Garlic", "Smoked Paprika", "Brown Rice",
            "Salmon Fillet", "Soy Sauce", "Ginger", "Sesame Oil", "Broccoli",
            "Spaghetti", "Pancetta", "Parmesan Cheese", "Black Pepper",
            "Lentils", "Carrot", "Celery", "Coconut Milk", "Curry Powder",
            "Vegetable Broth", "Corn Tortillas", "Ground Beef", "Avocado",
            "Salsa", "Lime", "Bell Pepper",

            "Butter", "Cheddar Cheese", "Greek Yogurt", "Sour Cream", "Heavy Cream",
            "Mozzarella Cheese", "Ricotta Cheese", "Cream Cheese", "Tofu",
            "Black Beans", "Kidney Beans", "Green Peas", "Corn", "Sweet Potato",
            "Potato", "Zucchini", "Eggplant", "Kale", "Arugula", "Mushrooms",
            "Green Onion", "Shallot", "Red Cabbage", "Cauliflower", "Asparagus",
            "Green Beans", "Jalapeño", "Cilantro", "Basil", "Rosemary", "Thyme",
            "Oregano", "Cumin", "Chili Powder", "Turmeric", "Cinnamon",
            "Red Pepper Flakes", "Tomato Paste", "Diced Tomatoes", "Pasta",
            "Panko Breadcrumbs", "Breadcrumbs", "Chicken Broth", "Beef Broth",
            "White Rice", "Jasmine Rice", "Couscous", "Bulgur", "Almonds",
            "Walnuts", "Peanut Butter", "Raisins", "Dates", "Coconut Flakes",
            "Canola Oil", "Apple Cider Vinegar", "Balsamic Vinegar", "Dijon Mustard",
            "Worcestershire Sauce", "Brown Sugar", "Vanilla Extract", "Baking Soda"
        )

        // Aim for 81 meals, so that there are 27 meals for each meal type
        val recipes = listOf<Recipe>(
            // Tags: Vegetarian, High-Fiber, Quick & Easy, Breakfast
            // Ingredients: Rolled Oats, Milk, Chia Seeds, Banana, Honey
            Recipe(
                name="Banana Chia Oatmeal",
                description="Warm rolled oats topped with banana, chia seeds, and a touch of honey.",
                instructions = "1. Combine rolled oats and milk in a saucepan.\n2. Simmer over medium heat, stirring, until creamy.\n3. Stir in chia seeds and sliced banana.\n4. Serve with honey.",
                mealType = MealType.BREAKFAST,
                prepTimeMinutes = 5,
                cookTimeMinutes = 10
            ),
            // Tags: Vegetarian, High-Protein, Breakfast
            // Ingredients: Eggs, Spinach, Feta Cheese, Olive Oil, Black Pepper
            Recipe(
                name="Spinach and Feta Scramble",
                description="Soft scrambled eggs with tender spinach and salty feta.",
                instructions="1. Whisk the eggs with a pinch of black pepper.\n2. Warm olive oil in a skillet and cook the spinach until wilted.\n3. Add the eggs and stir gently until just set.\n4. Crumble feta over the top and serve.",
                mealType=MealType.BREAKFAST,
                prepTimeMinutes=5,
                cookTimeMinutes=8
            ),
            // Tags: Vegetarian, American, Breakfast
            // Ingredients: All-Purpose Flour, Milk, Eggs, Baking Powder, Blueberries, Maple Syrup
            Recipe(
                name="Blueberry Pancakes",
                description="Fluffy, lightly sweet pancakes filled with juicy blueberries.",
                instructions="1. Whisk flour and baking powder in a bowl.\n2. Add milk and one egg, stirring just until combined.\n3. Fold in the blueberries.\n4. Cook small portions of batter in a lightly oiled skillet until bubbles form; flip and cook until golden.\n5. Serve with maple syrup.",
                mealType=MealType.BREAKFAST,
                prepTimeMinutes=10,
                cookTimeMinutes=15
            ),
            // Tags: Vegan, Plant-Based, Mediterranean, High-Fiber, Lunch
            // Ingredients: Quinoa, Chickpeas, Cucumber, Cherry Tomatoes, Red Onion, Lemon, Parsley, Olive Oil
            Recipe(
                name="Mediterranean Quinoa Bowl",
                description="A fresh quinoa bowl with chickpeas, crisp vegetables, lemon, and parsley.",
                instructions="1. Cook the quinoa according to its package directions and let it cool slightly.\n2. Rinse and drain the chickpeas.\n3. Dice the cucumber, halve the cherry tomatoes, and finely chop the red onion.\n4. Toss the quinoa and vegetables with olive oil and lemon juice.\n5. Top with parsley and serve.",
                mealType=MealType.LUNCH,
                prepTimeMinutes=15,
                cookTimeMinutes=15
            ),

            // Tags:
            // Ingredients:
            Recipe(
                name="Lentil and Vegetable Soup",
                description="A comforting, hearty soup of lentils, vegetables, and fragrant spices.",
                instructions="1. Dice the carrot and celery.\n2. Warm olive oil in a pot and cook the carrot and celery until softened.\n3. Stir in lentils and curry powder.\n4. Add vegetable broth and simmer until the lentils are tender.\n5. Stir in coconut milk and warm through before serving.",
                mealType=MealType.LUNCH,
                prepTimeMinutes=10,
                cookTimeMinutes=35
            ),

            // Tags:
            // Ingredients:
            Recipe(
                name="Ginger Soy Salmon with Broccoli",
                description="Oven-baked salmon glazed with ginger and soy, served with broccoli and rice.",
                instructions="1. Heat the oven to 200°C (400°F) and start cooking the brown rice.\n2. Mix soy sauce, grated ginger, and sesame oil.\n3. Place the salmon on a lined baking tray and brush with the sauce.\n4. Bake until the salmon flakes easily; add broccoli to the tray for the final 12 minutes.\n5. Serve over brown rice.",
                mealType=MealType.DINNER,
                prepTimeMinutes=10,
                cookTimeMinutes=20
            ),

            // Tags:
            // Ingredients:
            Recipe(
                name="Spaghetti Carbonara",
                description="A classic pasta dish with crisp pancetta, egg, and Parmesan sauce.",
                instructions="1. Cook the spaghetti in salted water until al dente; reserve a cup of pasta water.\n2. Cook the pancetta in a skillet until crisp.\n3. Whisk the eggs with grated Parmesan and black pepper.\n4. Toss the hot drained pasta with the pancetta, then remove the skillet from the heat.\n5. Stir in the egg mixture, loosening with a little pasta water until glossy. Serve immediately.",
                mealType=MealType.DINNER,
                prepTimeMinutes=10,
                cookTimeMinutes=20
            ),

            // Tags:
            // Ingredients:
            Recipe(
                name="Chickpea Coconut Curry",
                description="A warming plant-based curry with chickpeas simmered in spiced coconut milk.",
                instructions="1. Warm olive oil in a saucepan and stir in curry powder.\n2. Add drained chickpeas and stir to coat.\n3. Pour in coconut milk and simmer until the sauce thickens slightly.\n4. Season to taste and serve hot.",
                mealType=MealType.DINNER,
                prepTimeMinutes=5,
                cookTimeMinutes=20
            ),
            // Tags:
            // Ingredients:
            Recipe(
                name = "Beef and Avocado Tacos",
                description="Seasoned ground beef tucked into corn tortillas with avocado and salsa.",
                instructions="1. Cook the ground beef in a skillet over medium heat, breaking it up as it browns.\n2. Warm the corn tortillas in a dry skillet.\n3. Slice the avocado and cut the lime into wedges.\n4. Fill the tortillas with beef, avocado, and salsa.\n5. Squeeze lime over the tacos and serve.",
                mealType=MealType.DINNER,
                prepTimeMinutes=10,
                cookTimeMinutes=15
            ),
            // Tags:
            // Ingredients:
            Recipe(
                name="Chicken and Vegetable Stir-Fry",
                description = "A quick skillet meal of chicken and crisp vegetables in a savory ginger-soy sauce.",
                instructions = "1. Slice the chicken breast and bell pepper; cut the broccoli into small florets.\n2. Stir-fry the chicken in sesame oil until browned and cooked through.\n3. Add broccoli and bell pepper and cook until crisp-tender.\n4. Stir in soy sauce and grated ginger; cook for another minute.\n5. Serve hot with rice.",
                mealType = MealType.DINNER,
                prepTimeMinutes = 15,
                cookTimeMinutes =  20
            ),

            // Tags: Vegetarian, High-Fiber, Quick & Easy, Breakfast
            // Ingredients: Rolled Oats, Milk, Chia Seeds, Banana, Blueberries, Honey
            Recipe(
                name = "Banana Blueberry Overnight Oats",
                description = "Creamy make-ahead oats with chia seeds, banana, blueberries, and honey.",
                instructions = "1. Stir rolled oats, milk, and chia seeds together in a covered container.\n2. Refrigerate for at least 6 hours or overnight.\n3. Top with sliced banana and blueberries.\n4. Drizzle with honey and serve.",
                mealType = MealType.BREAKFAST,
                prepTimeMinutes = 10,
                cookTimeMinutes = 0
            ),

            // Tags: Vegetarian, High-Protein, Breakfast, Quick & Easy
            // Ingredients: Eggs, Spinach, Feta Cheese, Olive Oil, Black Pepper
            Recipe(
                name = "Spinach and Feta Omelet",
                description = "A fluffy omelet filled with wilted spinach and tangy feta.",
                instructions = "1. Whisk the eggs with black pepper.\n2. Warm olive oil in a nonstick skillet and cook the spinach until wilted.\n3. Pour in the eggs and cook, gently lifting the edges as they set.\n4. Sprinkle feta over one half, fold the omelet, and cook until the center is set.\n5. Serve warm.",
                mealType = MealType.BREAKFAST,
                prepTimeMinutes = 5,
                cookTimeMinutes = 10
            ),

            // Tags: Vegetarian, American, Breakfast, Kid-Friendly
            // Ingredients: All-Purpose Flour, Milk, Eggs, Baking Powder, Blueberries, Maple Syrup
            Recipe(
                name = "Blueberry Pancakes",
                description = "Tender, golden pancakes studded with fresh blueberries and served with maple syrup.",
                instructions = "1. Whisk the flour and baking powder in a bowl.\n2. Whisk the milk and eggs together, then stir them into the dry ingredients just until combined.\n3. Fold in the blueberries.\n4. Cook small portions of batter in a lightly oiled skillet over medium heat, flipping when bubbles form.\n5. Serve with maple syrup.",
                mealType = MealType.BREAKFAST,
                prepTimeMinutes = 10,
                cookTimeMinutes = 15
            ),

            // Tags: Vegan, Plant-Based, Mediterranean, High-Fiber, Lunch
            // Ingredients: Quinoa, Chickpeas, Cucumber, Cherry Tomatoes, Red Onion, Lemon, Parsley, Olive Oil
            Recipe(
                name = "Mediterranean Chickpea Quinoa Salad",
                description = "A bright, filling salad with quinoa, chickpeas, crunchy vegetables, and lemon dressing.",
                instructions = "1. Cook the quinoa according to its package directions and let it cool slightly.\n2. Rinse and drain the chickpeas.\n3. Dice the cucumber, halve the cherry tomatoes, and finely chop the red onion and parsley.\n4. Whisk lemon juice with olive oil.\n5. Toss the quinoa, chickpeas, and vegetables with the dressing and parsley.",
                mealType = MealType.LUNCH,
                prepTimeMinutes = 15,
                cookTimeMinutes = 15
            ),

            // Tags: Poultry, Chicken, American, High-Protein, Lunch
            // Ingredients: Chicken Breast, Carrot, Celery, Garlic, Chicken Broth, Brown Rice, Black Pepper
            Recipe(
                name = "Chicken and Brown Rice Soup",
                description = "A comforting chicken soup with tender vegetables and hearty brown rice.",
                instructions = "1. Dice the chicken, carrot, and celery; mince the garlic.\n2. Add the chicken broth, carrot, celery, and garlic to a pot and bring to a simmer.\n3. Stir in the brown rice and cook until nearly tender.\n4. Add the chicken and simmer until it is cooked through and the rice is tender.\n5. Season with black pepper and serve.",
                mealType = MealType.LUNCH,
                prepTimeMinutes = 15,
                cookTimeMinutes = 40
            ),

            // Tags: Seafood, Fish, Japanese, High-Protein, Gluten-Free, Dinner
            // Ingredients: Salmon Fillet, Soy Sauce, Ginger, Sesame Oil, Broccoli, Brown Rice
            Recipe(
                name = "Ginger Soy Salmon with Broccoli",
                description = "Oven-roasted salmon and broccoli with a savory ginger-soy glaze, served over brown rice.",
                instructions = "1. Heat the oven to 200°C (400°F) and begin cooking the brown rice.\n2. Mix soy sauce, grated ginger, and sesame oil.\n3. Place the salmon and broccoli on a lined baking tray and brush with the sauce.\n4. Bake until the salmon flakes easily and the broccoli is tender, about 12–15 minutes.\n5. Serve over brown rice.",
                mealType = MealType.DINNER,
                prepTimeMinutes = 10,
                cookTimeMinutes = 25
            ),

            // Tags: Beef, Mexican, High-Protein, Quick & Easy, Dinner
            // Ingredients: Ground Beef, Corn Tortillas, Avocado, Salsa, Lime, Cumin, Chili Powder
            Recipe(
                name = "Beef and Avocado Tacos",
                description = "Seasoned beef tacos topped with creamy avocado, salsa, and a squeeze of lime.",
                instructions = "1. Cook the ground beef in a skillet over medium heat, breaking it into small pieces.\n2. Stir in cumin and chili powder and cook until the beef is browned and cooked through.\n3. Warm the corn tortillas in a dry skillet.\n4. Fill each tortilla with beef, sliced avocado, and salsa.\n5. Serve with lime wedges.",
                mealType = MealType.DINNER,
                prepTimeMinutes = 10,
                cookTimeMinutes = 15
            ),

            // Tags: Vegan, Plant-Based, Indian, Gluten-Free, High-Fiber, Dinner
            // Ingredients: Lentils, Coconut Milk, Curry Powder, Garlic, Carrot, Vegetable Broth
            Recipe(
                name = "Coconut Lentil Curry",
                description = "Red or brown lentils simmered with vegetables, curry spices, and creamy coconut milk.",
                instructions = "1. Dice the carrot and mince the garlic.\n2. Warm olive oil in a saucepan and cook the carrot until it begins to soften.\n3. Stir in the garlic and curry powder.\n4. Add the lentils and vegetable broth; simmer until the lentils are tender.\n5. Stir in the coconut milk and warm through before serving.",
                mealType = MealType.DINNER,
                prepTimeMinutes = 10,
                cookTimeMinutes = 35
            ),

            // Tags: Italian, Pork, High-Protein, Dinner
            // Ingredients: Spaghetti, Pancetta, Eggs, Parmesan Cheese, Black Pepper
            Recipe(
                name = "Spaghetti Carbonara",
                description = "Classic Italian pasta tossed with crisp pancetta, egg, Parmesan, and black pepper.",
                instructions = "1. Cook the spaghetti until al dente, reserving a cup of the pasta water before draining.\n2. Cook the pancetta in a skillet until crisp.\n3. Whisk the eggs, grated Parmesan, and black pepper in a bowl.\n4. Remove the skillet from the heat and toss the hot spaghetti with the pancetta.\n5. Stir in the egg mixture, adding a little pasta water until the sauce is smooth and glossy. Serve immediately.",
                mealType = MealType.DINNER,
                prepTimeMinutes = 10,
                cookTimeMinutes = 20
            ),

            // Tags: Vegan, Plant-Based, Asian, Quick & Easy, Dinner
            // Ingredients: Tofu, Broccoli, Bell Pepper, Soy Sauce, Ginger, Garlic, Sesame Oil, Brown Rice
            Recipe(
                name = "Tofu and Vegetable Stir-Fry",
                description = "Crisp tofu and colorful vegetables tossed in a ginger-garlic soy sauce.",
                instructions = "1. Cook the brown rice according to its package directions.\n2. Press the tofu dry and cut it into cubes; slice the bell pepper and cut the broccoli into florets.\n3. Heat sesame oil in a large skillet and brown the tofu on several sides.\n4. Add the broccoli and bell pepper and stir-fry until crisp-tender.\n5. Add grated ginger, minced garlic, and soy sauce; toss for another minute and serve over rice.",
                mealType = MealType.DINNER,
                prepTimeMinutes = 15,
                cookTimeMinutes = 20
            )
        )

//        val imageUrls = listOf(
//            "https://images.unsplash.com/photo-1517673132405-a56a62b18caf?auto=format&fit=crop&w=1000&q=80",
//            "https://images.unsplash.com/photo-1525351484163-7529414344d8?auto=format&fit=crop&w=1000&q=80",
//            "https://images.unsplash.com/photo-1528207776546-365bb710ee93?auto=format&fit=crop&w=1000&q=80",
//            "https://images.unsplash.com/photo-1512621776951-a57141f2e?auto=format&fit=crop&w=1000&q=80",
//            "https://images.unsplash.com/photo-1547592180-85f173990554?auto=format&fit=crop&w=1000&q=80",
//            "https://images.unsplash.com/photo-1467003909585-2f8a72700288?auto=format&fit=crop&w=1000&q=80",
//            "https://images.unsplash.com/photo-1473093295043-cdd812d0e601?auto=format&fit=crop&w=1000&q=80",
//            "https://images.unsplash.com/photo-1603894584373-5ac82b2e0d6?auto=format&fit=crop&w=1000&q=80",
//            "https://images.unsplash.com/photo-1551504734-5ee1c4a1479b?auto=format&fit=crop&w=1000&q=80",
//            "https://images.unsplash.com/photo-1512058564366-18510be2db19?auto=format&fit=crop&w=1000&q=80"
//        )

        suspend fun populateDatabase(dao: RecipeDao) {
            Log.i("RecipeDatabase", "Seeding database...")


            // Seed Tags
            for (i in tags) {
                dao.insertTag(Tag(name = i))
            }
            val tagItalianId = dao.insertTag(Tag(name = "Italian")).toInt()
            val tagQuickId = dao.insertTag(Tag(name = "Quick & Easy")).toInt()
            val tagHealthyId = dao.insertTag(Tag(name = "Healthy")).toInt()

            // Seed Ingredients
            val ingPastaId = dao.insertIngredient(Ingredient(name = "Spaghetti")).toInt()
            val ingEggsId = dao.insertIngredient(Ingredient(name = "Eggs")).toInt()
            val ingPancettaId = dao.insertIngredient(Ingredient(name = "Pancetta")).toInt()
            val ingCheeseId = dao.insertIngredient(Ingredient(name = "Parmesan Cheese")).toInt()
            val ingOatsId = dao.insertIngredient(Ingredient(name = "Oats")).toInt()
            val ingMilkId = dao.insertIngredient(Ingredient(name = "Milk")).toInt()

            // Seed Recipe 1: Spaghetti Carbonara
            val recipe1Id = dao.insertRecipe(
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

            // Junctions & Image for Recipe 1
            dao.insertRecipeTag(RecipeTag(recipeId = recipe1Id, tagId = tagItalianId))
            dao.insertRecipeTag(RecipeTag(recipeId = recipe1Id, tagId = tagQuickId))

            dao.insertRecipeIngredient(RecipeIngredient(recipeId = recipe1Id, ingredientId = ingPastaId, quantity = 200f, unit = "g"))
            dao.insertRecipeIngredient(RecipeIngredient(recipeId = recipe1Id, ingredientId = ingEggsId, quantity = 2f, unit = "pcs"))
            dao.insertRecipeIngredient(RecipeIngredient(recipeId = recipe1Id, ingredientId = ingPancettaId, quantity = 100f, unit = "g"))
            dao.insertRecipeIngredient(RecipeIngredient(recipeId = recipe1Id, ingredientId = ingCheeseId, quantity = 50f, unit = "g"))

            dao.insertImage(Image(path = "carbonara.jpg", recipeId = recipe1Id))

            // Seed Recipe 2: Oatmeal
            val recipe2Id = dao.insertRecipe(
                Recipe(
                    name = "Oatmeal",
                    description = "Warm and comforting breakfast oatmeal.",
                    instructions = "1. Heat milk in a pot.\n2. Stir in oats.\n3. Cook on low heat for 5 minutes.",
                    mealType = MealType.BREAKFAST,
                    prepTimeMinutes = 2,
                    cookTimeMinutes = 5,
                    isSaved = false
                )
            ).toInt()

            dao.insertRecipeTag(RecipeTag(recipeId = recipe2Id, tagId = tagHealthyId))
            dao.insertRecipeTag(RecipeTag(recipeId = recipe2Id, tagId = tagQuickId))

            dao.insertRecipeIngredient(RecipeIngredient(recipeId = recipe2Id, ingredientId = ingOatsId, quantity = 50f, unit = "g"))
            dao.insertRecipeIngredient(RecipeIngredient(recipeId = recipe2Id, ingredientId = ingMilkId, quantity = 250f, unit = "ml"))

            dao.insertImage(Image(path = "oatmeal.jpg", recipeId = recipe2Id))

            // Seed Scheduled Recipe
            dao.insertScheduledRecipe(
                ScheduledRecipe(
                    date = LocalDateTime.now(),
                    mealType = MealType.DINNER,
                    hasCooked = false,
                    recipeId = recipe1Id
                )
            )

            Log.i("RecipeDatabase", "Database seeding complete!")
        }
    }
}