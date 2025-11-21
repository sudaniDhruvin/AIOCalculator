package com.belbytes.calculators.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.belbytes.calculators.R
import com.belbytes.calculators.ui.setup.SetupActivity
import com.belbytes.calculators.ui.theme.AIOCalculatorTheme
import com.belbytes.calculators.utils.PreferenceManager
import kotlinx.coroutines.launch

class OnboardingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AIOCalculatorTheme {
                OnboardingScreen(
                    onComplete = {
                        PreferenceManager.setOnboardingCompleted(this, true)
                        // Navigate to setup screens after onboarding
                        startActivity(Intent(this, SetupActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    val pagerState = rememberPagerState()
    var currentPage by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()
    
    LaunchedEffect(pagerState.currentPage) {
        currentPage = pagerState.currentPage
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Content Area
        HorizontalPager(
            count = 4,
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> OnboardingPage1()
                1 -> OnboardingPage2()
                2 -> OnboardingPage3()
                3 -> OnboardingPage4()
            }
        }
        
        // Bottom Navigation
        BottomNavigationSection(
            currentPage = currentPage,
            totalPages = 4,
            onSkip = onComplete,
            onNext = {
                if (currentPage < 3) {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(currentPage + 1)
                    }
                } else {
                    onComplete()
                }
            },
            onDone = onComplete
        )
    }
}

@Composable
fun BottomNavigationSection(
    currentPage: Int,
    totalPages: Int,
    onSkip: () -> Unit,
    onNext: () -> Unit,
    onDone: () -> Unit
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // SKIP Button
        TextButton(
            onClick = onSkip,
            modifier = Modifier.padding(start = 0.dp)
        ) {
            Text(
                text = context.getString(R.string.skip).uppercase(),
                color = Color(0xFF2196F3),
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )
        }
        
        // Progress Indicators
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            repeat(totalPages) { index ->
                Box(
                    modifier = Modifier
                        .width(if (index == currentPage) 24.dp else 8.dp)
                        .height(4.dp)
                        .background(
                            color = if (index == currentPage) 
                                Color(0xFF2196F3) 
                            else 
                                Color(0xFFBBDEFB),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(2.dp)
                        )
                )
            }
        }
        
        // Next/Done Button
        IconButton(
            onClick = {
                if (currentPage == totalPages - 1) {
                    onDone()
                } else {
                    onNext()
                }
            },
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = Color(0xFFE3F2FD),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                )
        ) {
            if (currentPage == totalPages - 1) {
                Text(
                    text = context.getString(R.string.done).uppercase(),
                    color = Color(0xFF2196F3),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    text = context.getString(R.string.next),
                    color = Color(0xFF2196F3),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

