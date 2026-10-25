package com.example.meal_planner

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.meal_planner.ui.theme.MealplannerTheme
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.grid.items

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
                MainScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: RecipeViewModel) {
    // 1. Remember PagerState and CoroutineScope at the root UI level
    val pagerState = rememberPagerState(pageCount = { 3 }, initialPage = 0)
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // 2. Pass navigation callbacks down to BottomNavBar
            BottomNavBar(
                onPageSelected = { pageIndex: Int ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pageIndex)
                    }
                }
            )
        }
    ) { innerPadding ->
        // 3. Pass pagerState down to Pager
        Pager(
            modifier = Modifier.padding(innerPadding),
            pagerState = pagerState,
            viewmodel = viewModel
        )
    }
}

//@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Pager(
    modifier: Modifier,
    viewmodel: RecipeViewModel,
    pagerState: PagerState,
) {

    // Trigger db seeding / queries
    DBTest(viewmodel)

    // Horizontal pager with 3 pages
    HorizontalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize(),
        userScrollEnabled = true
    ) { page ->
        when (page) {
            0 -> SchedulePage(modifier, viewmodel)
            1 -> RecipePage(modifier, viewmodel)
            2 -> ShoppingListPage(modifier, viewmodel)
        }
    }
}

@Composable
fun DBTest(viewmodel: RecipeViewModel) {

    // Trigger db seeding
    val seed = viewmodel.getAllRecipes().collectAsState(emptyList()).value
    Log.i("Pager", seed.toString())


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
    val test = false
    if(test) {

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
}

@Composable
fun BottomNavBar(onPageSelected: (Int) -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(15.dp),
        horizontalArrangement = Arrangement.SpaceBetween) {
        Button(colors = buttonColors(Color.Blue), onClick = { onPageSelected(0) }) { Text("Home") }
        Button(colors = buttonColors(Color.Red), onClick = { onPageSelected(1) }) { Text("Recipe") }
        Button(colors = buttonColors(Color.Green), onClick = { onPageSelected(2) }) { Text("S-List") }
    }
}

@Composable
fun SchedulePage(modifier: Modifier = Modifier, viewmodel: RecipeViewModel) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Home Page", fontSize = 30.sp)
        for(i in 0..7) {
            ScheduleCard()
        }
    }
}



@Composable
fun RecipePage(modifier: Modifier = Modifier, viewmodel: RecipeViewModel) {
    // Filter & Sort State
    var changeRecipeList by remember { mutableIntStateOf(0) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedTags by remember { mutableStateOf<Set<String>>(emptySet()) }
    var selectedSort by remember { mutableStateOf<String?>(null) }

    // Fetch raw recipes from ViewModel
    val recipesFlow = remember(changeRecipeList, viewmodel) {
        if (changeRecipeList == 0) {
            viewmodel.getAllRecipesWithDetails()
        } else {
            viewmodel.getAllSavedRecipes()
        }
    }
    val rawRecipes = recipesFlow.collectAsState(initial = emptyList()).value

    // Fetch tags for dropdown
    val allTags = viewmodel.getAllTags().collectAsState(initial = emptyList()).value.map { it.name }

    // Filter recipes by category (meal type) & tags and sort the result. Re-compile when keys state changes
    val filteredRecipes = remember(rawRecipes, selectedCategory, selectedTags, selectedSort) {
        var list = rawRecipes.map { it.toRecipeInfo() }

        // Category Filter
        if (!selectedCategory.isNullOrBlank()) {
            list = list.filter { it.mealType.equals(selectedCategory, ignoreCase = true) }
        }

        // Tags Filter (Recipe must match ALL selected tags)
        if (selectedTags.isNotEmpty()) {
            list = list.filter { recipe ->
                selectedTags.all { selectedTag -> recipe.tags.contains(selectedTag) }
            }
        }

        // Sorting
        when (selectedSort) {
            "Name (A-Z)" -> list = list.sortedBy { it.name }
            "Name (Z-A)" -> list = list.sortedByDescending { it.name }
            "Time to cook (Q-L)" -> list = list.sortedBy { it.prepTime + it.cookTime }
        }

        list
    }

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Recipe Page", fontSize = 30.sp)

        // All / Saved Selector Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .border(1.dp, color = Color.Black, shape = RoundedCornerShape(10.dp)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { changeRecipeList = 0 },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (changeRecipeList == 0) Color.Blue else Color.Gray
                )
            ) { Text("All Recipes") }

            Button(
                onClick = { changeRecipeList = 1 },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (changeRecipeList == 1) Color.Blue else Color.Gray
                )
            ) { Text("Saved Recipes") }
        }

        // Filter Options Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .border(1.dp, color = Color.Black, shape = RoundedCornerShape(10.dp)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Category Dropdown
            SingleSelectDropdown(
                label = "Category",
                options = MealType.entries.map { it.name },
                selectedOption = selectedCategory,
                onOptionSelected = { category ->
                    selectedCategory = if (selectedCategory == category) null else category
                }
            )

            // Tags Dropdown (Multi-select)
            MultiSelectDropdown(
                label = "Filter By",
                options = allTags,
                selectedOptions = selectedTags,
                onOptionToggled = { tag ->
                    selectedTags = if (selectedTags.contains(tag)) {
                        selectedTags - tag
                    } else {
                        selectedTags + tag
                    }
                }
            )

            // Sort By Dropdown
            SingleSelectDropdown(
                label = "Sort By",
                options = listOf("Name (A-Z)", "Name (Z-A)", "Time to cook (Q-L)"),
                selectedOption = selectedSort,
                onOptionSelected = { sortOption ->
                    selectedSort = if (selectedSort == sortOption) null else sortOption
                }
            )
        }

        // List a grid of recipes
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize().padding(5.dp)
        ) {
            items(
                items = filteredRecipes,
                key = { recipe -> recipe.id }
            ) { recipe ->
                RecipeCard(recipe)
            }
        }
    }
}

@Composable
fun SingleSelectDropdown(
    label: String,
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Button(
            onClick = { expanded = !expanded },
            colors = buttonColors(containerColor = if (selectedOption != null) Color.Blue else Color.LightGray)
        ) {
            Text(text = selectedOption ?: label)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 250.dp) // Limit height
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false // Hide dropdown on selection
                    }
                )
            }
        }
    }
}

@Composable
fun MultiSelectDropdown(
    label: String,
    options: List<String>,
    selectedOptions: Set<String>,
    onOptionToggled: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Button(
            onClick = { expanded = !expanded },
            colors = buttonColors(containerColor = if (selectedOptions.isNotEmpty()) Color.Blue else Color.LightGray)
        ) {
            Text(
                text = if (selectedOptions.isEmpty()) label
                else "$label (${selectedOptions.size})"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }, // Hide when tapping outside
            modifier = Modifier.heightIn(max = 250.dp) // Limit height
        ) {
            options.forEach { option ->
                val isSelected = selectedOptions.contains(option)
                DropdownMenuItem(
                    text = { Text(option) },
                    trailingIcon = {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = null
                        )
                    },
                    onClick = { onOptionToggled(option) }
                )
            }
        }
    }
}

@Composable
fun ShoppingListPage(modifier: Modifier = Modifier, viewmodel: RecipeViewModel) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Shopping List Page", fontSize = 30.sp)
    }
}

@Composable
fun ScheduleCard() {
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
        ScheduleButton("Breakfast", { Log.i("MealCard", "Breakfast Btn") })
        ScheduleButton("Lunch", { Log.i("MealCard", "Lunch Btn") });
        ScheduleButton("Dinner", { Log.i("MealCard", "Dinner Btn") });
    }
}

@Composable
fun ScheduleButton(name: String, onClick: () -> Unit) {
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
fun RecipeCard(recipeInfo: RecipeInfo) {
    /*
    Needs a:
    - Constrained box
    - Title
    - Image (when added)
    - Button (transparent) that fills the whole box
    */

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .aspectRatio(1.3f)
            .background(Color.LightGray, shape = RoundedCornerShape(10.dp))
            .border(1.dp, color = Color.Black, shape = RoundedCornerShape(10.dp))
        ,
        contentAlignment = Alignment.TopCenter
//            .graphicsLayer { rotationY = angle; clip = false },
    ) {
        // box constraints
        val width = constraints.maxWidth
        val height = constraints.maxHeight

//        val bitmap = remember { mutableStateOf<Bitmap?>(null) }
       /* LaunchedEffect(index, width, height) {
            bitmap.value = viewModel.loadScaledImage(contentResolver, index, width, height)
        }*/
//        if (bitmap.value != null) {
//            Image(
//                bitmap.value!!.asImageBitmap(),
//                contentDescription = "",
//                contentScale = ContentScale.Crop,
//                modifier = Modifier.fillMaxSize()
//            )
        Text(text = recipeInfo.name, fontSize = 16.sp)

        Button(
            shape = RoundedCornerShape(10.dp),
            onClick = {
                /*  if (index < viewModel.imageList.size) {
                       viewModel.currentImage = viewModel.imageList[index]
                       viewModel.currentImageIdx = index
                       onClick()
                       coroutineScope.launch {
                           pagerState.animateScrollToPage(1)
                       }
                   }*/
                // TODO: Open RecipeDetials()
            },
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize(),

            content = { Text(text = "") },
            colors = ButtonColors(
                Color.Transparent,
                Color.Transparent,
                Color.Transparent,
                Color.Transparent
            )
        )
//        } else {
//            Text(
//                text = "Loading image...",
//                modifier = Modifier.align(Alignment.Center)
//            )
//        }
    }

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