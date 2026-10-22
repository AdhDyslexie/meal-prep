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

        val recipeSeeds = listOf(
            RecipeSeed(
                recipe = Recipe(
                    name = "Banana Chia Oatmeal",
                    description = "Warm rolled oats topped with banana, chia seeds, and a touch of honey.",
                    instructions = "1. Combine rolled oats and milk in a saucepan.\n2. Simmer over medium heat, stirring, until creamy.\n3. Stir in chia seeds and sliced banana.\n4. Serve with honey.",
                    mealType = MealType.BREAKFAST,
                    prepTimeMinutes = 5,
                    cookTimeMinutes = 10
                ),
                tags = listOf("Vegetarian", "High-Fiber", "Quick & Easy", "Breakfast"),
                ingredients = listOf(
                    IngredientSeed("Rolled Oats", 100f, "g"),
                    IngredientSeed("Milk", 250f, "ml"),
                    IngredientSeed("Chia Seeds", 15f, "g"),
                    IngredientSeed("Banana", 1f, "pcs"),
                    IngredientSeed("Honey", 15f, "ml")
                ),
                imagePath = "oatmeal.jpg"
            ),
            RecipeSeed(
                recipe = Recipe(
                    name = "Spinach and Feta Scramble",
                    description = "Soft scrambled eggs with tender spinach and salty feta.",
                    instructions = "1. Whisk the eggs with a pinch of black pepper.\n2. Warm olive oil in a skillet and cook the spinach until wilted.\n3. Add the eggs and stir gently until just set.\n4. Crumble feta over the top and serve.",
                    mealType = MealType.BREAKFAST,
                    prepTimeMinutes = 5,
                    cookTimeMinutes = 8
                ),
                tags = listOf("Vegetarian", "High-Protein", "Breakfast"),
                ingredients = listOf(
                    IngredientSeed("Eggs", 2f, "pcs"),
                    IngredientSeed("Spinach", 50f, "g"),
                    IngredientSeed("Feta Cheese", 30f, "g"),
                    IngredientSeed("Olive Oil", 10f, "ml"),
                    IngredientSeed("Black Pepper", 1f, "pinch")
                )
            ),
            RecipeSeed(
                recipe = Recipe(
                    name = "Blueberry Pancakes",
                    description = "Fluffy, lightly sweet pancakes filled with juicy blueberries.",
                    instructions = "1. Whisk flour and baking powder in a bowl.\n2. Add milk and one egg, stirring just until combined.\n3. Fold in the blueberries.\n4. Cook small portions of batter in a lightly oiled skillet until bubbles form; flip and cook until golden.\n5. Serve with maple syrup.",
                    mealType = MealType.BREAKFAST,
                    prepTimeMinutes = 10,
                    cookTimeMinutes = 15
                ),
                tags = listOf("Vegetarian", "American", "Breakfast", "Kid-Friendly"),
                ingredients = listOf(
                    IngredientSeed("All-Purpose Flour", 150f, "g"),
                    IngredientSeed("Milk", 200f, "ml"),
                    IngredientSeed("Eggs", 1f, ""),
                    IngredientSeed("Baking Powder", 5f, "g"),
                    IngredientSeed("Blueberries", 75f, "g"),
                    IngredientSeed("Maple Syrup", 30f, "ml")
                )
            ),
            RecipeSeed(
                recipe = Recipe(
                    name = "Mediterranean Quinoa Bowl",
                    description = "A fresh quinoa bowl with chickpeas, crisp vegetables, lemon, and parsley.",
                    instructions = "1. Cook the quinoa according to its package directions and let it cool slightly.\n2. Rinse and drain the chickpeas.\n3. Dice the cucumber, halve the cherry tomatoes, and finely chop the red onion.\n4. Toss the quinoa and vegetables with olive oil and lemon juice.\n5. Top with parsley and serve.",
                    mealType = MealType.LUNCH,
                    prepTimeMinutes = 15,
                    cookTimeMinutes = 15
                ),
                tags = listOf("Vegan", "Plant-Based", "Mediterranean", "High-Fiber", "Lunch"),
                ingredients = listOf(
                    IngredientSeed("Quinoa", 100f, "g"),
                    IngredientSeed("Chickpeas", 150f, "g"),
                    IngredientSeed("Cucumber", 1f, "pcs"),
                    IngredientSeed("Cherry Tomatoes", 100f, "g"),
                    IngredientSeed("Red Onion", 0.5f, "pcs"),
                    IngredientSeed("Lemon", 1f, "pcs"),
                    IngredientSeed("Parsley", 15f, "g"),
                    IngredientSeed("Olive Oil", 15f, "ml")
                )
            ),
            RecipeSeed(
                recipe = Recipe(
                    name = "Lentil and Vegetable Soup",
                    description = "A comforting, hearty soup of lentils, vegetables, and fragrant spices.",
                    instructions = "1. Dice the carrot and celery.\n2. Warm olive oil in a pot and cook the carrot and celery until softened.\n3. Stir in lentils and curry powder.\n4. Add vegetable broth and simmer until the lentils are tender.\n5. Stir in coconut milk and warm through before serving.",
                    mealType = MealType.LUNCH,
                    prepTimeMinutes = 10,
                    cookTimeMinutes = 35
                ),
                tags = listOf("Vegan", "Plant-Based", "Comfort Food", "Lunch"),
                ingredients = listOf(
                    IngredientSeed("Lentils", 150f, "g"),
                    IngredientSeed("Carrot", 1f, "pcs"),
                    IngredientSeed("Celery", 2f, "stalks"),
                    IngredientSeed("Olive Oil", 15f, "ml"),
                    IngredientSeed("Curry Powder", 5f, "g"),
                    IngredientSeed("Vegetable Broth", 500f, "ml"),
                    IngredientSeed("Coconut Milk", 200f, "ml")
                )
            ),
            RecipeSeed(
                recipe = Recipe(
                    name = "Ginger Soy Salmon with Broccoli",
                    description = "Oven-baked salmon glazed with ginger and soy, served with broccoli and rice.",
                    instructions = "1. Heat the oven to 200°C (400°F) and start cooking the brown rice.\n2. Mix soy sauce, grated ginger, and sesame oil.\n3. Place the salmon on a lined baking tray and brush with the sauce.\n4. Bake until the salmon flakes easily; add broccoli to the tray for the final 12 minutes.\n5. Serve over brown rice.",
                    mealType = MealType.DINNER,
                    prepTimeMinutes = 10,
                    cookTimeMinutes = 20
                ),
                tags = listOf("Seafood", "Fish", "Japanese", "High-Protein", "Gluten-Free", "Dinner"),
                ingredients = listOf(
                    IngredientSeed("Salmon Fillet", 200f, "g"),
                    IngredientSeed("Soy Sauce", 30f, "ml"),
                    IngredientSeed("Ginger", 10f, "g"),
                    IngredientSeed("Sesame Oil", 10f, "ml"),
                    IngredientSeed("Broccoli", 150f, "g"),
                    IngredientSeed("Brown Rice", 150f, "g")
                )
            ),
            RecipeSeed(
                recipe = Recipe(
                    name = "Spaghetti Carbonara",
                    description = "A classic pasta dish with crisp pancetta, egg, and Parmesan sauce.",
                    instructions = "1. Cook the spaghetti in salted water until al dente; reserve a cup of pasta water.\n2. Cook the pancetta in a skillet until crisp.\n3. Whisk the eggs with grated Parmesan and black pepper.\n4. Toss the hot drained pasta with the pancetta, then remove the skillet from the heat.\n5. Stir in the egg mixture, loosening with a little pasta water until glossy. Serve immediately.",
                    mealType = MealType.DINNER,
                    prepTimeMinutes = 10,
                    cookTimeMinutes = 20,
                    isSaved = true
                ),
                tags = listOf("Italian", "Pork", "High-Protein", "Dinner"),
                ingredients = listOf(
                    IngredientSeed("Spaghetti", 200f, "g"),
                    IngredientSeed("Pancetta", 100f, "g"),
                    IngredientSeed("Eggs", 2f, "pcs"),
                    IngredientSeed("Parmesan Cheese", 50f, "g"),
                    IngredientSeed("Black Pepper", 2f, "g")
                ),
                imagePath = "carbonara.jpg"
            ),
            RecipeSeed(
                recipe = Recipe(
                    name = "Chickpea Coconut Curry",
                    description = "A warming plant-based curry with chickpeas simmered in spiced coconut milk.",
                    instructions = "1. Warm olive oil in a saucepan and stir in curry powder.\n2. Add drained chickpeas and stir to coat.\n3. Pour in coconut milk and simmer until the sauce thickens slightly.\n4. Season to taste and serve hot.",
                    mealType = MealType.DINNER,
                    prepTimeMinutes = 5,
                    cookTimeMinutes = 20
                ),
                tags = listOf("Vegan", "Plant-Based", "Indian", "Gluten-Free", "Dinner"),
                ingredients = listOf(
                    IngredientSeed("Chickpeas", 200f, "g"),
                    IngredientSeed("Coconut Milk", 250f, "ml"),
                    IngredientSeed("Curry Powder", 10f, "g"),
                    IngredientSeed("Olive Oil", 15f, "ml")
                )
            ),
            RecipeSeed(
                recipe = Recipe(
                    name = "Beef and Avocado Tacos",
                    description = "Seasoned ground beef tucked into corn tortillas with avocado and salsa.",
                    instructions = "1. Cook the ground beef in a skillet over medium heat, breaking it up as it browns.\n2. Warm the corn tortillas in a dry skillet.\n3. Slice the avocado and cut the lime into wedges.\n4. Fill the tortillas with beef, avocado, and salsa.\n5. Squeeze lime over the tacos and serve.",
                    mealType = MealType.DINNER,
                    prepTimeMinutes = 10,
                    cookTimeMinutes = 15
                ),
                tags = listOf("Beef", "Mexican", "High-Protein", "Quick & Easy", "Dinner"),
                ingredients = listOf(
                    IngredientSeed("Ground Beef", 250f, "g"),
                    IngredientSeed("Corn Tortillas", 3f, "pcs"),
                    IngredientSeed("Avocado", 1f, "pcs"),
                    IngredientSeed("Salsa", 50f, "g"),
                    IngredientSeed("Lime", 1f, "pcs"),
                    IngredientSeed("Cumin", 5f, "g"),
                    IngredientSeed("Chili Powder", 5f, "g")
                )
            ),
            RecipeSeed(
                recipe = Recipe(
                    name = "Chicken and Vegetable Stir-Fry",
                    description = "A quick skillet meal of chicken and crisp vegetables in a savory ginger-soy sauce.",
                    instructions = "1. Slice the chicken breast and bell pepper; cut the broccoli into small florets.\n2. Stir-fry the chicken in sesame oil until browned and cooked through.\n3. Add broccoli and bell pepper and cook until crisp-tender.\n4. Stir in soy sauce and grated ginger; cook for another minute.\n5. Serve hot with rice.",
                    mealType = MealType.DINNER,
                    prepTimeMinutes = 15,
                    cookTimeMinutes = 20
                ),
                tags = listOf("Poultry", "Chicken", "Asian", "Quick & Easy", "Dinner"),
                ingredients = listOf(
                    IngredientSeed("Chicken Breast", 200f, "g"),
                    IngredientSeed("Bell Pepper", 1f, "pcs"),
                    IngredientSeed("Broccoli", 100f, "g"),
                    IngredientSeed("Sesame Oil", 15f, "ml"),
                    IngredientSeed("Soy Sauce", 30f, "ml"),
                    IngredientSeed("Ginger", 10f, "g"),
                    IngredientSeed("Brown Rice", 150f, "g")
                )
            ),
            RecipeSeed(
                recipe = Recipe(
                    name = "Banana Blueberry Overnight Oats",
                    description = "Creamy make-ahead oats with chia seeds, banana, blueberries, and honey.",
                    instructions = "1. Stir rolled oats, milk, and chia seeds together in a covered container.\n2. Refrigerate for at least 6 hours or overnight.\n3. Top with sliced banana and blueberries.\n4. Drizzle with honey and serve.",
                    mealType = MealType.BREAKFAST,
                    prepTimeMinutes = 10,
                    cookTimeMinutes = 0
                ),
                tags = listOf("Vegetarian", "High-Fiber", "Quick & Easy", "Breakfast"),
                ingredients = listOf(
                    IngredientSeed("Rolled Oats", 80f, "g"),
                    IngredientSeed("Milk", 200f, "ml"),
                    IngredientSeed("Chia Seeds", 15f, "g"),
                    IngredientSeed("Banana", 1f, "pcs"),
                    IngredientSeed("Blueberries", 50f, "g"),
                    IngredientSeed("Honey", 15f, "ml")
                )
            ),
            RecipeSeed(
                recipe = Recipe(
                    name = "Spinach and Feta Omelet",
                    description = "A fluffy omelet filled with wilted spinach and tangy feta.",
                    instructions = "1. Whisk the eggs with black pepper.\n2. Warm olive oil in a nonstick skillet and cook the spinach until wilted.\n3. Pour in the eggs and cook, gently lifting the edges as they set.\n4. Sprinkle feta over one half, fold the omelet, and cook until the center is set.\n5. Serve warm.",
                    mealType = MealType.BREAKFAST,
                    prepTimeMinutes = 5,
                    cookTimeMinutes = 10
                ),
                tags = listOf("Vegetarian", "High-Protein", "Breakfast", "Quick & Easy"),
                ingredients = listOf(
                    IngredientSeed("Eggs", 3f, "pcs"),
                    IngredientSeed("Spinach", 50f, "g"),
                    IngredientSeed("Feta Cheese", 40f, "g"),
                    IngredientSeed("Olive Oil", 10f, "ml"),
                    IngredientSeed("Black Pepper", 1f, "pinch")
                )
            ),
            RecipeSeed(
                recipe = Recipe(
                    name = "Mediterranean Chickpea Quinoa Salad",
                    description = "A bright, filling salad with quinoa, chickpeas, crunchy vegetables, and lemon dressing.",
                    instructions = "1. Cook the quinoa according to its package directions and let it cool slightly.\n2. Rinse and drain the chickpeas.\n3. Dice the cucumber, halve the cherry tomatoes, and finely chop the red onion and parsley.\n4. Whisk lemon juice with olive oil.\n5. Toss the quinoa, chickpeas, and vegetables with the dressing and parsley.",
                    mealType = MealType.LUNCH,
                    prepTimeMinutes = 15,
                    cookTimeMinutes = 15
                ),
                tags = listOf("Vegan", "Plant-Based", "Mediterranean", "High-Fiber", "Lunch"),
                ingredients = listOf(
                    IngredientSeed("Quinoa", 100f, "g"),
                    IngredientSeed("Chickpeas", 150f, "g"),
                    IngredientSeed("Cucumber", 1f, "pcs"),
                    IngredientSeed("Cherry Tomatoes", 100f, "g"),
                    IngredientSeed("Red Onion", 0.5f, "pcs"),
                    IngredientSeed("Lemon", 1f, "pcs"),
                    IngredientSeed("Parsley", 15f, "g"),
                    IngredientSeed("Olive Oil", 15f, "ml")
                )
            ),
            RecipeSeed(
                recipe = Recipe(
                    name = "Chicken and Brown Rice Soup",
                    description = "A comforting chicken soup with tender vegetables and hearty brown rice.",
                    instructions = "1. Dice the chicken, carrot, and celery; mince the garlic.\n2. Add the chicken broth, carrot, celery, and garlic to a pot and bring to a simmer.\n3. Stir in the brown rice and cook until nearly tender.\n4. Add the chicken and simmer until it is cooked through and the rice is tender.\n5. Season with black pepper and serve.",
                    mealType = MealType.LUNCH,
                    prepTimeMinutes = 15,
                    cookTimeMinutes = 40
                ),
                tags = listOf("Poultry", "Chicken", "American", "High-Protein", "Lunch"),
                ingredients = listOf(
                    IngredientSeed("Chicken Breast", 200f, "g"),
                    IngredientSeed("Carrot", 1f, "pcs"),
                    IngredientSeed("Celery", 2f, "stalks"),
                    IngredientSeed("Garlic", 2f, "cloves"),
                    IngredientSeed("Chicken Broth", 500f, "ml"),
                    IngredientSeed("Brown Rice", 100f, "g"),
                    IngredientSeed("Black Pepper", 1f, "pinch")
                )
            ),
            RecipeSeed(
                recipe = Recipe(
                    name = "Coconut Lentil Curry",
                    description = "Red or brown lentils simmered with vegetables, curry spices, and creamy coconut milk.",
                    instructions = "1. Dice the carrot and mince the garlic.\n2. Warm olive oil in a saucepan and cook the carrot until it begins to soften.\n3. Stir in the garlic and curry powder.\n4. Add the lentils and vegetable broth; simmer until the lentils are tender.\n5. Stir in the coconut milk and warm through before serving.",
                    mealType = MealType.DINNER,
                    prepTimeMinutes = 10,
                    cookTimeMinutes = 35
                ),
                tags = listOf("Vegan", "Plant-Based", "Indian", "Gluten-Free", "High-Fiber", "Dinner"),
                ingredients = listOf(
                    IngredientSeed("Lentils", 150f, "g"),
                    IngredientSeed("Coconut Milk", 250f, "ml"),
                    IngredientSeed("Curry Powder", 10f, "g"),
                    IngredientSeed("Garlic", 2f, "cloves"),
                    IngredientSeed("Carrot", 1f, "pcs"),
                    IngredientSeed("Vegetable Broth", 400f, "ml")
                )
            ),
            RecipeSeed(
                recipe = Recipe(
                    name = "Tofu and Vegetable Stir-Fry",
                    description = "Crisp tofu and colorful vegetables tossed in a ginger-garlic soy sauce.",
                    instructions = "1. Cook the brown rice according to its package directions.\n2. Press the tofu dry and cut it into cubes; slice the bell pepper and cut the broccoli into florets.\n3. Heat sesame oil in a large skillet and brown the tofu on several sides.\n4. Add the broccoli and bell pepper and stir-fry until crisp-tender.\n5. Add grated ginger, minced garlic, and soy sauce; toss for another minute and serve over rice.",
                    mealType = MealType.DINNER,
                    prepTimeMinutes = 15,
                    cookTimeMinutes = 20
                ),
                tags = listOf("Vegan", "Plant-Based", "Asian", "Quick & Easy", "Dinner"),
                ingredients = listOf(
                    IngredientSeed("Tofu", 200f, "g"),
                    IngredientSeed("Broccoli", 100f, "g"),
                    IngredientSeed("Bell Pepper", 1f, "pcs"),
                    IngredientSeed("Soy Sauce", 30f, "ml"),
                    IngredientSeed("Ginger", 10f, "g"),
                    IngredientSeed("Garlic", 2f, "cloves"),
                    IngredientSeed("Sesame Oil", 15f, "ml"),
                    IngredientSeed("Brown Rice", 150f, "g")
                )
            )
        )

        suspend fun populateDatabase(dao: RecipeDao) {
            Log.i("RecipeDatabase", "Seeding database...")

            // Maps to hold inserted entity IDs: Name -> ID
            val tagIdMap = mutableMapOf<String, Int>()
            val ingredientIdMap = mutableMapOf<String, Int>()

            // 1. Seed Tags
            for (tagName in tags) {
                val id = dao.insertTag(Tag(name = tagName)).toInt()
                tagIdMap[tagName] = id
            }

            // 2. Seed Ingredients
            for (ingName in ingredients) {
                val id = dao.insertIngredient(Ingredient(name = ingName)).toInt()
                ingredientIdMap[ingName] = id
            }

            // 3. Seed Recipes & Junctions
            for (seed in recipeSeeds) {
                val recipeId = dao.insertRecipe(seed.recipe).toInt()

                // Link RecipeTags
                for (tagName in seed.tags) {
                    tagIdMap[tagName]?.let { tagId ->
                        dao.insertRecipeTag(RecipeTag(recipeId = recipeId, tagId = tagId))
                    }
                }

                // Link RecipeIngredients with quantities
                for (ingSeed in seed.ingredients) {
                    ingredientIdMap[ingSeed.name]?.let { ingredientId ->
                        dao.insertRecipeIngredient(
                            RecipeIngredient(
                                recipeId = recipeId,
                                ingredientId = ingredientId,
                                quantity = ingSeed.quantity,
                                unit = ingSeed.unit
                            )
                        )
                    }
                }

                // Link Image
                if (seed.imagePath.isNotBlank()) {
                    dao.insertImage(Image(path = seed.imagePath, recipeId = recipeId))
                }
            }

            Log.i("RecipeDatabase", "Database seeding completed successfully!")
        }
    }
}