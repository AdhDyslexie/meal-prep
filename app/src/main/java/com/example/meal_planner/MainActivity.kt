package com.example.meal_planner

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.meal_planner.ui.theme.MealplannerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MealplannerTheme {
                Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = { BottomNavBar() }) { innerPadding ->
                    Pager(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

//@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Pager(modifier: Modifier) {
    val pagerState = rememberPagerState(pageCount = { 3 }, initialPage = 0)
//    coroutineScope = rememberCoroutineScope()

//    var canNavigate by remember { mutableStateOf(false) }

    // Disable navigation when on gallary page
//    LaunchedEffect(pagerState.currentPage) {
//        if (pagerState.currentPage == 0) {
//            canNavigate = false
//        }
//    }

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
fun BottomNavBar() {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(15.dp),
        horizontalArrangement = Arrangement.SpaceBetween) {
        Button(onClick = { Log.i("Navbar", "Home") }) { Text("Home") }
        Button(onClick = { Log.i("Navbar", "Recipe") }) { Text("Recipe") }
        Button(onClick = { Log.i("Navbar", "S-List") }) { Text("S-List") }
    }
}

@Composable
fun HomePage(modifier: Modifier = Modifier) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Home Page", fontSize = 30.sp)
    }
}

@Composable
fun RecipePage(modifier: Modifier = Modifier) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Recipe Page", fontSize = 30.sp)
    }
}

@Composable
fun ShoppingListPage(modifier: Modifier = Modifier) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Shopping List Page", fontSize = 30.sp)
    }
}
