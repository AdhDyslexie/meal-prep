package com.example.meal_planner

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.util.query
import com.example.meal_planner.ui.theme.MealplannerTheme
import kotlinx.coroutines.flow.collectLatest

/*
FUCK ANDROID DATABASES!
THREE FUCKING DAYS WASTED TRYING TO GET THIS SHIT TO WORK!

Thank you for listening to my ted talk
*/


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: RecipeViewModel by viewModels { RecipeViewModelFactory(application) }
        enableEdgeToEdge()
        setContent {
            MealplannerTheme {
                Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = { BottomNavBar() }) { innerPadding ->
                    Pager(
                        modifier = Modifier.padding(innerPadding),
                        viewModel
                    )
                }
            }
        }
    }
}

//@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Pager(modifier: Modifier, viewmodel: RecipeViewModel) {
    val pagerState = rememberPagerState(pageCount = { 3 }, initialPage = 0)
//    coroutineScope = rememberCoroutineScope()

//    var canNavigate by remember { mutableStateOf(false) }

    // Disable navigation when on gallery page
//    LaunchedEffect(pagerState.currentPage) {
//        if (pagerState.currentPage == 0) {
//            canNavigate = false
//        }
//    }

    // Trigger db seeding
    val test = viewmodel.getAllRecipes().collectAsState(emptyList()).value
    Log.i("Pager", test.toString())

    DBTest(viewmodel)

    // Horizontal pager with 2 pages (Gallary view & full (single) image view)
    HorizontalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize(),
        userScrollEnabled = true
    ) { page ->
        when (page) {
            0 -> HomePage(modifier)
            1 -> RecipePage(modifier)
            2 -> ShoppingListPage(modifier)
        }
    }
}

@Composable
fun DBTest(viewmodel: RecipeViewModel) {

    // Query tests

    val recipeList = viewmodel.getAllRecipesWithDetails().collectAsState(emptyList()).value
    Log.i("Pager", "Size of recipeList: " + recipeList.size.toString())

    val recipe = recipeList.firstOrNull()
    recipe?.let { // needed because of recompilation
        val recipe = it.toRecipeInfo()
        Log.i("Pager", recipe.toString())
    }

    val initSchedule = viewmodel.getAllScheduledInfo().collectAsState(emptyList()).value
    Log.i("Pager", "Size of initSchedule: " + initSchedule.size.toString())

    val schedule = initSchedule.firstOrNull()
    schedule?.let {
        val schedule = it.toScheduledRecipeInfo()
        Log.i("Pager", "Scheduled Recipe Info:\n${schedule.toString()}")
    }

    val initTags = viewmodel.getAllTags().collectAsState(emptyList()).value
    Log.i("Pager", "Size of initTags: " + initTags.size.toString())

    initTags.let {
        Log.i("Pager", "All tags:\n")
        for(i in it) {
            Log.i("Pager", "\t" + i.name)
        }
    }

    val initIngredient = viewmodel.getAllIngredients().collectAsState(emptyList()).value
    Log.i("Pager", "Size of initIngredient: " + initIngredient.size.toString())

    initIngredient.let {
        Log.i("Pager", "All initIngredient:\n")
        for(i in it) {
            Log.i("Pager", "\t" + i.name)
        }
    }

    val firstRecipe = viewmodel.getRecipeInfo(1).collectAsState(null).value
    firstRecipe?.let {
        val recipeInfo = it.toRecipeInfo()
        Log.i("Pager", "First Recipe:\n\t $recipeInfo")
    }

    val firstSchedule = viewmodel.getScheduledInfo(1).collectAsState(null).value
    firstSchedule?.let {
        val schedule = it.toScheduledRecipeInfo()
        Log.i("Pager", "First Recipe:\n\t $schedule")
    }

    // Update tests

    firstRecipe?.let {
        val recipeInfo = it.toRecipeInfo()
        viewmodel.updateRecipeSavedState(recipeInfo.id, true)
    }

    val newFirstRecipe = viewmodel.getRecipeInfo(1).collectAsState(null).value
    firstRecipe?.let {
        val recipeInfo = it.toRecipeInfo()
        Log.i("Pager", "First Recipe save state = \n\t ${recipeInfo.isSaved}")
    }

    firstSchedule?.let {
        val schedule = it.toScheduledRecipeInfo()
        viewmodel.updateRecipeCookedSate(schedule.id, false)
    }
    val newFirstSchedule = viewmodel.getScheduledInfo(1).collectAsState(null).value
    newFirstSchedule?.let {
        val schedule = it.toScheduledRecipeInfo()
        Log.i("Pager", "First Schedule has cooked = \n\t ${schedule.haveCooked}")
    }

}

@Composable
fun BottomNavBar() {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(15.dp),
        horizontalArrangement = Arrangement.SpaceBetween) {
        Button(colors = buttonColors(Color.Blue), onClick = { Log.i("Navbar", "Home") }) { Text("Home") }
        Button(colors = buttonColors(Color.Red), onClick = { Log.i("Navbar", "Recipe") }) { Text("Recipe") }
        Button(colors = buttonColors(Color.Green), onClick = { Log.i("Navbar", "S-List") }) { Text("S-List") }
    }
}

@Composable
fun HomePage(modifier: Modifier = Modifier) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Home Page", fontSize = 30.sp)
        for(i in 0..7) {
            MealCard()
        }
    }
}

@Composable
fun RecipePage(modifier: Modifier = Modifier) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Recipe Page", fontSize = 30.sp)
        RecipeDetials();
    }
}

@Composable
fun ShoppingListPage(modifier: Modifier = Modifier) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Shopping List Page", fontSize = 30.sp)
    }
}

@Composable
fun MealCard() {
    /*
    - Day of week should be queried & then displays
    - Date should be queried & then displayed
    - Buttons should open an overlay with the meals details that was assigned to it
        - Details collected from a database
    - If nothing is assigned to a button its state should be set to hidden
    */
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)
        .border(1.dp, color = Color.Black, shape = RoundedCornerShape(10.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween)
    {
        Column(modifier = Modifier.padding(5.dp), horizontalAlignment = Alignment.Start) {
            Text(text = "Thu", fontSize = 20.sp, fontWeight = FontWeight.Bold) // Display DoW
            Text(text = "Oct 15", fontSize = 18.sp) // Display the date of the meal
        }
        MealButton("Breakfast", { Log.i("MealCard", "Breakfast Btn") })
        MealButton("Lunch", { Log.i("MealCard", "Lunch Btn") });
        MealButton("Dinner", { Log.i("MealCard", "Dinner Btn") });
    }
}

@Composable
fun MealButton(name: String, onClick: () -> Unit) {
    Button(
        enabled = true, // get from state variable
        modifier = Modifier
            .padding(10.dp)
            .size(86.dp, 54.dp),
//        colors = buttonColors(Color.Blue),
        contentPadding = PaddingValues(10.dp),
        shape = RoundedCornerShape(10.dp),
        onClick = onClick)
    {
        Text(name)
    }
}

@Composable
fun RecipeCard() {

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RecipeDetials() {
    /*
     - Details should be an overlay, with dynamic content
     - Should grow into place when opened
     - Should shrink into place when closed
    */
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = { Log.i("RecipeDetials", "Exit") }) { Text("X") }
        Text(text = "<Recipe Name>", fontSize = 30.sp) // title
//        Image()
        Column(modifier = Modifier
            .fillMaxWidth(0.875f)
            .border(1.dp, color = Color.Black, shape = RoundedCornerShape(10.dp))
        ) { // Ingredients
            // Bullet list of ingredients
            Text("- Ingredient 1")
            Text("- Ingredient 2")
            Text("- Ingredient 3")
        }
        Column(modifier = Modifier
            .fillMaxWidth(0.875f)
            .border(1.dp, color = Color.Black, shape = RoundedCornerShape(10.dp))
        ) { // Instructions
            // Numbered list of instructions
            Text("1. Instruction")
            Text("2. Instruction")
            Text("3. Instruction")
        }
    }
}