package com.example.meal_planner

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.meal_planner.ui.theme.MealplannerTheme
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import kotlinx.datetime.*
import java.time.format.DateTimeFormatter
import kotlin.time.Clock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
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
    // Remember PagerState and CoroutineScope at the root UI level
    val pagerState = rememberPagerState(pageCount = { 3 }, initialPage = 0)
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // Pass navigation callbacks down to BottomNavBar
            BottomNavBar(
                onPageSelected = { pageIndex: Int ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pageIndex)
                    }
                }
            )
        }
    ) { innerPadding ->
        // Pass pagerState down to Pager
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
fun SchedulePage(
    modifier: Modifier = Modifier,
    viewmodel: RecipeViewModel
) {
    // Current week dates & queries
    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }
    val weekDays = remember(today) { getWeekDaysFor(today) }
    val weekDates = remember(weekDays) { weekDays.map {it.date}.toSet() }
    val rawSchedule = viewmodel.getAllScheduledInfo().collectAsState(emptyList()).value
    val filteredSchedule = remember(rawSchedule, weekDates) {
        rawSchedule.map { it.toScheduledRecipeInfo() }.filter { it.date in weekDates }
    }
    val scheduleByDate = remember(filteredSchedule) {
        filteredSchedule.groupBy { it.date }
    }
    var selectedRecipe by remember { mutableStateOf<RecipeInfo?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Schedule Page", fontSize = 30.sp)

            // Display a ScheduleCard for each day of the week
            weekDays.forEach { dayCell ->
                ScheduleCard(
                    dayOfWeek = dayCell.date.toJavaLocalDate().format(DateTimeFormatter.ofPattern("EEE")), // e.g. Sun
                    dateText = dayCell.date.toJavaLocalDate().format(DateTimeFormatter.ofPattern("MMM d")), // e.g. Oct 25
                    scheduledMeals = scheduleByDate[dayCell.date] ?: emptyList(),
                    onMealClick = { recipeInfo -> selectedRecipe = recipeInfo }
                )
            }
        }
        // Overlay for schedule page
        val isExpanded = selectedRecipe != null

        if (isExpanded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { selectedRecipe = null }
            )
        }

        AnimatedVisibility(
            visible = isExpanded,
            modifier = Modifier.align(Alignment.Center),
            enter = fadeIn() + scaleIn(initialScale = 0.8f),
            exit = fadeOut() + scaleOut(targetScale = 0.8f)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.85f),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                RecipeDetails(
                    recipeInfo = selectedRecipe,
                    showActionButtons = false, // Hides Save & Cook buttons
                    onClose = { selectedRecipe = null }
                )
            }
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

        list // the return
    }

    var selectedRecipe by remember { mutableStateOf<RecipeInfo?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(5.dp)
            ) {
                items(
                    items = filteredRecipes,
                    key = { recipe -> recipe.id }
                ) { recipe ->
                    RecipeCard(
                        recipeInfo = recipe,
                        onClick = { selectedRecipe = recipe }
                    )
                }
            }
        }

        // Track whether the overlay is open or closed
        val isExpanded = selectedRecipe != null

        // Background Dimmer
        if (isExpanded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { selectedRecipe = null } // Tap outside to close
            )
        }

        // Animated Grow/Shrink Container
        AnimatedVisibility(
            visible = isExpanded,
            modifier = Modifier.align(Alignment.Center),
            enter = fadeIn() + scaleIn(initialScale = 0.8f), // Grows into view
            exit = fadeOut() + scaleOut(targetScale = 0.8f)   // Shrinks back down
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.85f),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                RecipeDetails(
                    recipeInfo = selectedRecipe,
                    onToggleSave = { isSaved ->
                        selectedRecipe?.let { recipe ->
                            viewmodel.updateRecipeSavedState(recipe.id, isSaved)
                        }
                    },
                    onScheduleCook = { date, mealType ->
                        selectedRecipe?.let { recipe ->
                            viewmodel.newSchedule(date, mealType, recipe.id)
                        }
                    },
                    onClose = { selectedRecipe = null }
                )
            }
        }
    }
}

@Composable
fun ShoppingListPage(
    modifier: Modifier = Modifier,
    viewmodel: RecipeViewModel
) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Shopping List Page", fontSize = 30.sp)
    }
}

@Composable
fun ScheduleCard(
    dayOfWeek: String,
    dateText: String,
    scheduledMeals: List<ScheduledRecipeInfo> = emptyList(),
    onMealClick: (RecipeInfo) -> Unit = {}
) {
    /*
    TODO:
    - Highlight the border of today's meals
    - Grey out the days that have past this week
    */

    // Find scheduled meal for each meal type on this day
    val breakfast = scheduledMeals.find { it.mealType.equals("BREAKFAST", ignoreCase = true) }
    val lunch = scheduledMeals.find { it.mealType.equals("LUNCH", ignoreCase = true) }
    val dinner = scheduledMeals.find { it.mealType.equals("DINNER", ignoreCase = true) }

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)
        .border(1.dp, color = Color.Black, shape = RoundedCornerShape(10.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween)
    {
        Column(modifier = Modifier.padding(5.dp), horizontalAlignment = Alignment.Start) {
            Text(text = dayOfWeek, fontSize = 20.sp, fontWeight = FontWeight.Bold) // Display DoW
            Text(text = dateText, fontSize = 18.sp) // Display the date of the meal
        }
        ScheduleButton(
            name = "Breakfast",
            onClick = { breakfast?.recipeInfo?.let { onMealClick(it) } },
            isScheduled = breakfast != null
        )
        ScheduleButton(
            name = "Lunch",
            onClick = { lunch?.recipeInfo?.let { onMealClick(it) } },
            isScheduled = lunch != null
        )
        ScheduleButton(
            name = "Dinner",
            onClick = { dinner?.recipeInfo?.let { onMealClick(it) } },
            isScheduled = dinner != null
        )
    }
}

@Composable
fun ScheduleButton(name: String, onClick: () -> Unit, isScheduled: Boolean = false) {
    Button(
        enabled = isScheduled, // get from state variable
        modifier = Modifier
            .padding(10.dp)
            .size(86.dp, 54.dp),
        colors = if (isScheduled) buttonColors(Color.Cyan) else buttonColors(Color.LightGray),
        contentPadding = PaddingValues(10.dp),
        shape = RoundedCornerShape(10.dp),
        onClick = onClick)
    {
        Text(name)
    }
}

@Composable
fun RecipeCard(recipeInfo: RecipeInfo, onClick: () -> Unit) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .aspectRatio(1.3f)
            .background(Color.LightGray, shape = RoundedCornerShape(10.dp))
            .border(1.dp, color = Color.Black, shape = RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.TopCenter
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

        // Fill button triggers onClick
        Button(
            shape = RoundedCornerShape(10.dp),
            onClick = onClick,
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
fun RecipeDetails(
    recipeInfo: RecipeInfo?,
    onClose: () -> Unit,
    showActionButtons: Boolean = true,
    onToggleSave: (Boolean) -> Unit = {},
    onScheduleCook: (LocalDate, MealType) -> Unit = { _, _ -> }
) {
    val scrollState = rememberScrollState()
    var isSaved by remember(recipeInfo) { mutableStateOf(recipeInfo?.isSaved ?: false) }
    var showCookDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Exit button row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { onClose() }) { Text("X") }
        }

        // Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = recipeInfo?.name ?: "<Recipe Name>", fontSize = 28.sp)
        }

        // Action buttons
        if (showActionButtons) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Save / Saved Toggle Button
                Button(
                    onClick = {
                        val newState = !isSaved
                        isSaved = newState             // Update UI state immediately
                        onToggleSave(newState)         // Update database
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSaved) Color.Blue else Color.Gray
                    )
                ) {
                    Text(if (isSaved) "Saved" else "Save")
                }

                // Cook Button opens Dialog Prompt
                Button(onClick = { showCookDialog = true }) {
                    Text("Cook")
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Ingredients
        Column(
            modifier = Modifier
                .fillMaxWidth(0.875f)
                .border(1.dp, color = Color.Black, shape = RoundedCornerShape(10.dp))
                .padding(8.dp)
        ) {
            Text("Ingredients:", fontWeight = FontWeight.Bold)
            recipeInfo?.ingredientsInfo?.forEach { ingredient ->
                Text("- ${ingredient.quantity} ${ingredient.unit}: ${ingredient.name}")
            } ?: run {
                Text("- Ingredient 1")
                Text("- Ingredient 2")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Instructions
        Column(
            modifier = Modifier
                .fillMaxWidth(0.875f)
                .border(1.dp, color = Color.Black, shape = RoundedCornerShape(10.dp))
                .padding(8.dp)
        ) {
            Text("Instructions:", fontWeight = FontWeight.Bold)
            recipeInfo?.instructions?.forEach {step ->
                Text(step)
            } ?: run {
                Text("1. Instruction")
                Text("2. Instruction")
            }
        }
    }
    // Show Dialog Prompt when 'Cook' is clicked
    if (showCookDialog && recipeInfo != null) {
        ScheduleMealDialog(
            recipeInfo = recipeInfo,
            onDismiss = { showCookDialog = false },
            onConfirm = { date, mealType ->
                onScheduleCook(date, mealType)
                showCookDialog = false
            }
        )
    }
}

data class DayCellInfo(
    val date: LocalDate,
    val month: Month,
    val dayOfMonth: Int,
    val dayOfWeek: DayOfWeek
)
fun getWeekDaysFor(targetDate: LocalDate): List<DayCellInfo> {
    // Find the offset from Monday (ISO day number: Monday = 1, Sunday = 7)
    val daysFromMonday = targetDate.dayOfWeek.isoDayNumber - 1

    // Subtract those days to land precisely on Monday of this week
    val monday = targetDate.minus(daysFromMonday, DateTimeUnit.DAY)

    // Loop through 0 to 6 to generate Monday through Sunday
    return (0..6).map { offset ->
        val currentDay = monday.plus(offset, DateTimeUnit.DAY)
        DayCellInfo(
            date = currentDay,
            month = currentDay.month,
            dayOfMonth = currentDay.day,
            dayOfWeek = currentDay.dayOfWeek
        )
    }
}

@Composable
fun ScheduleMealDialog(
    recipeInfo: RecipeInfo,
    onDismiss: () -> Unit,
    onConfirm: (LocalDate, MealType) -> Unit
) {
    // Default to today's date in YYYY-MM-DD format
    var dateText by remember {
        mutableStateOf(Clock.System.todayIn(TimeZone.currentSystemDefault()).toString())
    }
    var selectedMealType by remember {
        mutableStateOf(
            runCatching { MealType.valueOf(recipeInfo.mealType.uppercase()) }.getOrDefault(MealType.DINNER)
        )
    }
    var isDateError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Schedule ${recipeInfo.name}") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Date Input
                OutlinedTextField(
                    value = dateText,
                    onValueChange = {
                        dateText = it
                        isDateError = false
                    },
                    label = { Text("Date (YYYY-MM-DD)") },
                    isError = isDateError,
                    supportingText = {
                        if (isDateError) {
                            Text("Invalid date format. Use YYYY-MM-DD", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Meal Type Dropdown
                Text("Select Meal Type:", style = MaterialTheme.typography.labelLarge)
                SingleSelectDropdown(
                    label = "Meal Type",
                    options = MealType.entries.map { it.name },
                    selectedOption = selectedMealType.name,
                    onOptionSelected = { selectedName ->
                        selectedMealType = MealType.valueOf(selectedName)
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    try {
                        val parsedDate = LocalDate.parse(dateText.trim())
                        onConfirm(parsedDate, selectedMealType)
                    } catch (e: Exception) {
                        isDateError = true
                    }
                }
            ) {
                Text("Schedule")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
