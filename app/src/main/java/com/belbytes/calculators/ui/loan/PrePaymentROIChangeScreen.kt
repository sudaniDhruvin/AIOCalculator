package com.belbytes.calculators.ui.loan

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.ceil
import kotlin.math.ln
import kotlin.math.pow

data class PrePaymentResult(
    // Scenario 1: Keep EMI Same, Change Tenure
    val scenario1OldMonths: Double,
    val scenario1NewMonths: Double,
    val scenario1OldInterest: Double,
    val scenario1NewInterest: Double,
    // Scenario 2: Keep Tenure Same, Change EMI
    val scenario2OldEMI: Double,
    val scenario2NewEMI: Double,
    val scenario2OldInterest: Double,
    val scenario2NewInterest: Double
)

data class ROIChangeResult(
    // Scenario 1: Keep EMI Same, Change Tenure
    val scenario1OldMonths: Double,
    val scenario1NewMonths: Double,
    val scenario1OldInterest: Double,
    val scenario1NewInterest: Double,
    // Scenario 2: Keep Tenure Same, Change EMI
    val scenario2OldEMI: Double,
    val scenario2NewEMI: Double,
    val scenario2OldInterest: Double,
    val scenario2NewInterest: Double
)

@Composable
fun PrePaymentROIChangeScreen(
    onBackClick: () -> Unit
) {
    val tabs = listOf("Pre Payment", "ROI Change")
    val pagerState = rememberPagerState(initialPage = 0) { tabs.size }
    var selectedTab by rememberSaveable { mutableStateOf("Pre Payment") }
    
    // Common fields
    var outstandingAmount by rememberSaveable { mutableStateOf("") }
    var currentRate by rememberSaveable { mutableStateOf("") }
    var currentEMI by rememberSaveable { mutableStateOf("") }
    
    // Pre Payment specific
    var prePaymentAmount by rememberSaveable { mutableStateOf("") }
    
    // ROI Change specific
    var revisedRate by rememberSaveable { mutableStateOf("") }
    
    var showResults by rememberSaveable { mutableStateOf(false) }
    var prePaymentResult by rememberSaveable { mutableStateOf<PrePaymentResult?>(null) }
    var roiChangeResult by rememberSaveable { mutableStateOf<ROIChangeResult?>(null) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    
    val scrollState = rememberScrollState()
    
    // Scroll to end when results are shown
    LaunchedEffect(showResults) {
        if (showResults) {
            delay(100) // Small delay to ensure content is rendered
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }
    
    // Sync selectedTab with pager state
    LaunchedEffect(pagerState.currentPage) {
        selectedTab = tabs[pagerState.currentPage]
        showResults = false
        prePaymentResult = null
        roiChangeResult = null
        errorMessage = null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        PrePaymentROIChangeHeader(onBackClick = onBackClick)

        // Scrollable content wrapper
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Tabs
        val scope = rememberCoroutineScope()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            tabs.forEachIndexed { index, tab ->
                TabButton(
                    text = tab,
                    selected = selectedTab == tab,
                    onClick = { 
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Swipeable content
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Outstanding Amount Input
                PrePaymentROIInputField(
                    label = "Outstanding Amount",
                    placeholder = "Ex: 1,00,000",
                    value = outstandingAmount,
                    onValueChange = { outstandingAmount = it }
                )

                // Current Rate % Input
                PrePaymentROIInputField(
                    label = "Current Rate %",
                    placeholder = "Ex: 12%",
                    value = currentRate,
                    onValueChange = { currentRate = it }
                )

                // Current EMI Input
                PrePaymentROIInputField(
                    label = "Current EMI",
                    placeholder = "Ex: 1000",
                    value = currentEMI,
                    onValueChange = { currentEMI = it }
                )

                // Conditional Fields based on Tab
                if (tabs[page] == "Pre Payment") {
                    // Pre Payment Amount Input
                    PrePaymentROIInputField(
                        label = "Pre Payment Amount",
                        placeholder = "Ex: 10%",
                        value = prePaymentAmount,
                        onValueChange = { prePaymentAmount = it }
                    )
                } else {
                    // Revised Rate % Input
                    PrePaymentROIInputField(
                        label = "Revised Rate %",
                        placeholder = "Ex: 10%",
                        value = revisedRate,
                        onValueChange = { revisedRate = it }
                    )
                }

                // Action Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Calculate Button
                    Button(
                        onClick = {
                            errorMessage = null
                            if (selectedTab == "Pre Payment") {
                                val result = calculatePrePayment(
                                    outstandingAmount = outstandingAmount,
                                    currentRate = currentRate,
                                    currentEMI = currentEMI,
                                    prePaymentAmount = prePaymentAmount
                                )
                                if (result != null) {
                                    prePaymentResult = result
                                    roiChangeResult = null
                                    showResults = true
                                    errorMessage = null
                                } else {
                                    showResults = false
                                    prePaymentResult = null
                                    errorMessage = when {
                                        outstandingAmount.isBlank() || outstandingAmount.toDoubleOrNull() == null || outstandingAmount.toDoubleOrNull()!! <= 0 ->
                                            "Please enter a valid outstanding amount"
                                        currentRate.isBlank() || currentRate.toDoubleOrNull() == null || currentRate.toDoubleOrNull()!! <= 0 ->
                                            "Please enter a valid current rate"
                                        currentEMI.isBlank() || currentEMI.toDoubleOrNull() == null || currentEMI.toDoubleOrNull()!! <= 0 ->
                                            "Please enter a valid current EMI"
                                        prePaymentAmount.isBlank() || prePaymentAmount.toDoubleOrNull() == null || prePaymentAmount.toDoubleOrNull()!! <= 0 ->
                                            "Please enter a valid pre payment amount"
                                        else -> "Please check all input values"
                                    }
                                }
                            } else {
                                val result = calculateROIChange(
                                    outstandingAmount = outstandingAmount,
                                    currentRate = currentRate,
                                    currentEMI = currentEMI,
                                    revisedRate = revisedRate
                                )
                                if (result != null) {
                                    roiChangeResult = result
                                    prePaymentResult = null
                                    showResults = true
                                    errorMessage = null
                                } else {
                                    showResults = false
                                    roiChangeResult = null
                                    errorMessage = when {
                                        outstandingAmount.isBlank() || outstandingAmount.toDoubleOrNull() == null || outstandingAmount.toDoubleOrNull()!! <= 0 ->
                                            "Please enter a valid outstanding amount"
                                        currentRate.isBlank() || currentRate.toDoubleOrNull() == null || currentRate.toDoubleOrNull()!! <= 0 ->
                                            "Please enter a valid current rate"
                                        currentEMI.isBlank() || currentEMI.toDoubleOrNull() == null || currentEMI.toDoubleOrNull()!! <= 0 ->
                                            "Please enter a valid current EMI"
                                        revisedRate.isBlank() || revisedRate.toDoubleOrNull() == null ->
                                            "Please enter a valid revised rate"
                                        else -> "Please check all input values"
                                    }
                                }
                            }
                        },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Calculate",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Reset Button
                Button(
                    onClick = {
                        outstandingAmount = ""
                        currentRate = ""
                        currentEMI = ""
                        prePaymentAmount = ""
                        revisedRate = ""
                        showResults = false
                        prePaymentResult = null
                        roiChangeResult = null
                        errorMessage = null
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEBEBEB)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp
                    )
                ) {
                    Text(
                        text = "Reset",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                }
            }
        }

        // Error Message Section
        errorMessage?.let { error ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFEBEE)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = error,
                    fontSize = 14.sp,
                    color = Color(0xFFC62828),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // Results Section
        AnimatedVisibility(
            visible = showResults && (prePaymentResult != null || roiChangeResult != null),
            enter = expandVertically(
                    animationSpec = tween(300),
                    expandFrom = Alignment.Top
                ) + fadeIn(
                    animationSpec = tween(300)
                ),
                exit = shrinkVertically(
                    animationSpec = tween(300),
                    shrinkTowards = Alignment.Top
                ) + fadeOut(
                    animationSpec = tween(300)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Horizontal Divider
                    Divider(
                        color = Color(0xFFE0E0E0),
                        thickness = 1.dp,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    // Results Cards - Two scenarios
                    prePaymentResult?.let { result ->
                        // Scenario 1: If you want to change EMI (Keep Tenure Same)
                        PrePaymentROIScenarioCard(
                            title = "if you want to change EMI",
                            oldEMI = result.scenario2OldEMI,
                            newEMI = result.scenario2NewEMI,
                            oldInterest = result.scenario2OldInterest,
                            newInterest = result.scenario2NewInterest
                        )
                        
                        // Scenario 2: If you want to change Tenure (Keep EMI Same)
                        PrePaymentROIScenarioCard(
                            title = "if you want to change Tenure",
                            oldMonths = result.scenario1OldMonths,
                            newMonths = result.scenario1NewMonths,
                            oldInterest = result.scenario1OldInterest,
                            newInterest = result.scenario1NewInterest,
                            showMonths = true
                        )
                    }
                    
                    roiChangeResult?.let { result ->
                        // Scenario 1: If you want to change EMI (Keep Tenure Same)
                        PrePaymentROIScenarioCard(
                            title = "if you want to change EMI",
                            oldEMI = result.scenario2OldEMI,
                            newEMI = result.scenario2NewEMI,
                            oldInterest = result.scenario2OldInterest,
                            newInterest = result.scenario2NewInterest
                        )
                        
                        // Scenario 2: If you want to change Tenure (Keep EMI Same)
                        PrePaymentROIScenarioCard(
                            title = "if you want to change Tenure",
                            oldMonths = result.scenario1OldMonths,
                            newMonths = result.scenario1NewMonths,
                            oldInterest = result.scenario1OldInterest,
                            newInterest = result.scenario1NewInterest,
                            showMonths = true
                        )
                    }
                }
            }
        }
        }
    }
}

@Composable
fun PrePaymentROIChangeHeader(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .background(Color(0xFF2196F3))
            .statusBarsPadding()
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 8.dp)
                .size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }

        Text(
            text = "Pre Payment ROI Change",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TabButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable { onClick() }
            .fillMaxWidth()
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) Color(0xFF2196F3) else Color(0xFF757575),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            textAlign = TextAlign.Center
        )
        if (selected) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(Color(0xFF2196F3))
            )
        }
    }
}

@Composable
fun PrePaymentROIInputField(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF5F5F5),
                focusedContainerColor = Color(0xFFF5F5F5),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true
        )
    }
}

@Composable
fun PrePaymentROIScenarioCard(
    title: String,
    oldEMI: Double? = null,
    newEMI: Double? = null,
    oldMonths: Double? = null,
    newMonths: Double? = null,
    oldInterest: Double,
    newInterest: Double,
    showMonths: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF7F7F7)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Title
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            if (showMonths && oldMonths != null && newMonths != null) {
                // Months Comparison Table
                PrePaymentROIComparisonTable(
                    label1 = "New Months",
                    value1 = formatDecimal(newMonths),
                    label2 = "Old Months",
                    value2 = formatDecimal(oldMonths),
                    difference = formatDecimal(oldMonths - newMonths)
                )
            } else if (oldEMI != null && newEMI != null) {
                // EMI Comparison Table
                PrePaymentROIComparisonTable(
                    label1 = "New EMI",
                    value1 = formatCurrencyWithDecimal(newEMI),
                    label2 = "Old EMI",
                    value2 = formatCurrencyWithDecimal(oldEMI),
                    difference = formatCurrencyWithDecimal(oldEMI - newEMI)
                )
            }
            
            // Interest Comparison Table
            PrePaymentROIComparisonTable(
                label1 = "New Interest",
                value1 = formatCurrencyWithDecimal(newInterest),
                label2 = "Old Interest",
                value2 = formatCurrencyWithDecimal(oldInterest),
                difference = formatCurrencyWithDecimal(oldInterest - newInterest)
            )
        }
    }
}

@Composable
fun PrePaymentROIComparisonTable(
    label1: String,
    value1: String,
    label2: String,
    value2: String,
    difference: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label1,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = label2,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End
            )
            Text(
                text = "Difference",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End
            )
        }
        
        // Values Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = value1,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = value2,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End
            )
            Text(
                text = difference,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End
            )
        }
    }
}

private fun formatDecimal(value: Double): String {
    return String.format("%.2f", value)
}

fun calculatePrePayment(
    outstandingAmount: String,
    currentRate: String,
    currentEMI: String,
    prePaymentAmount: String
): PrePaymentResult? {
    return try {
        val principal = outstandingAmount.toDoubleOrNull() ?: return null
        val rate = currentRate.toDoubleOrNull() ?: return null
        val emi = currentEMI.toDoubleOrNull() ?: return null
        
        if (principal <= 0 || rate <= 0 || emi <= 0) return null
        
        val prePayment = prePaymentAmount.toDoubleOrNull() ?: return null
        if (prePayment <= 0) return null
        
        // Calculate pre payment amount (could be percentage or absolute)
        val prePaymentValue = if (prePayment < 1.0) {
            // Assume it's a percentage if less than 1
            principal * prePayment
        } else {
            // Assume it's absolute amount
            prePayment
        }
        
        if (prePaymentValue >= principal) return null
        
        val monthlyRate = rate / (12 * 100)
        val newPrincipal = principal - prePaymentValue
        
        // Calculate current tenure and interest using formula from documentation
        // k = EMI / Principal
        // n = log(k / (k - monthlyRate)) / log(1 + monthlyRate)
        val k = emi / principal
        val currentTenureMonths = if (monthlyRate > 0 && k > monthlyRate) {
            ln(k / (k - monthlyRate)) / ln(1 + monthlyRate)
        } else {
            principal / emi
        }
        val currentTotalInterest = (emi * currentTenureMonths) - principal
        
        // Scenario 1: Keep EMI Same, Change Tenure
        val kNew = emi / newPrincipal
        val newTenureMonths = if (monthlyRate > 0 && kNew > monthlyRate) {
            ln(kNew / (kNew - monthlyRate)) / ln(1 + monthlyRate)
        } else {
            newPrincipal / emi
        }
        val newTotalInterestScenario1 = (emi * newTenureMonths) - newPrincipal
        
        // Scenario 2: Keep Tenure Same, Change EMI
        // Calculate current EMI at current tenure (for comparison)
        val currentEMIAtTenure = if (monthlyRate > 0) {
            val rateFactor = java.lang.Math.pow(1 + monthlyRate, currentTenureMonths)
            principal * monthlyRate * rateFactor / (rateFactor - 1)
        } else {
            principal / currentTenureMonths
        }
        val currentTotalInterestScenario2 = (currentEMIAtTenure * currentTenureMonths) - principal
        
        // Calculate new EMI with same tenure
        val newEMI = if (monthlyRate > 0) {
            val rateFactor = java.lang.Math.pow(1 + monthlyRate, currentTenureMonths)
            newPrincipal * monthlyRate * rateFactor / (rateFactor - 1)
        } else {
            newPrincipal / currentTenureMonths
        }
        val newTotalInterestScenario2 = (newEMI * currentTenureMonths) - newPrincipal
        
        PrePaymentResult(
            scenario1OldMonths = currentTenureMonths,
            scenario1NewMonths = newTenureMonths,
            scenario1OldInterest = currentTotalInterest,
            scenario1NewInterest = newTotalInterestScenario1,
            scenario2OldEMI = currentEMIAtTenure,
            scenario2NewEMI = newEMI,
            scenario2OldInterest = currentTotalInterestScenario2,
            scenario2NewInterest = newTotalInterestScenario2
        )
    } catch (e: Exception) {
        null
    }
}

fun calculateROIChange(
    outstandingAmount: String,
    currentRate: String,
    currentEMI: String,
    revisedRate: String
): ROIChangeResult? {
    return try {
        val principal = outstandingAmount.toDoubleOrNull() ?: return null
        val currentRateValue = currentRate.toDoubleOrNull() ?: return null
        val emi = currentEMI.toDoubleOrNull() ?: return null
        val revisedRateValue = revisedRate.toDoubleOrNull() ?: return null
        
        if (principal <= 0 || currentRateValue <= 0 || emi <= 0) return null
        
        val currentMonthlyRate = currentRateValue / (12 * 100)
        val revisedMonthlyRate = revisedRateValue / (12 * 100)
        
        // Scenario 1: Keep EMI Same, Change Tenure
        // Calculate current tenure using formula from documentation
        val k = emi / principal
        val currentTenureMonths = if (currentMonthlyRate > 0 && k > currentMonthlyRate) {
            ln(k / (k - currentMonthlyRate)) / ln(1 + currentMonthlyRate)
        } else {
            principal / emi
        }
        val currentTotalInterestScenario1 = (emi * currentTenureMonths) - principal
        
        // Calculate new tenure with revised rate (keeping EMI same)
        val kRevised = emi / principal
        val newTenureMonths = if (revisedMonthlyRate > 0 && kRevised > revisedMonthlyRate) {
            ln(kRevised / (kRevised - revisedMonthlyRate)) / ln(1 + revisedMonthlyRate)
        } else {
            principal / emi
        }
        val newTotalInterestScenario1 = (emi * newTenureMonths) - principal
        
        // Scenario 2: Keep Tenure Same, Change EMI
        // Calculate current EMI at current tenure (for comparison)
        val currentEMIAtTenure = if (currentMonthlyRate > 0) {
            val rateFactor = java.lang.Math.pow(1 + currentMonthlyRate, currentTenureMonths)
            principal * currentMonthlyRate * rateFactor / (rateFactor - 1)
        } else {
            principal / currentTenureMonths
        }
        val currentTotalInterestScenario2 = (currentEMIAtTenure * currentTenureMonths) - principal
        
        // Calculate new EMI with revised rate (keeping tenure same)
        val newEMI = if (revisedMonthlyRate > 0) {
            val rateFactor = java.lang.Math.pow(1 + revisedMonthlyRate, currentTenureMonths)
            principal * revisedMonthlyRate * rateFactor / (rateFactor - 1)
        } else {
            principal / currentTenureMonths
        }
        val newTotalInterestScenario2 = (newEMI * currentTenureMonths) - principal
        
        ROIChangeResult(
            scenario1OldMonths = currentTenureMonths,
            scenario1NewMonths = newTenureMonths,
            scenario1OldInterest = currentTotalInterestScenario1,
            scenario1NewInterest = newTotalInterestScenario1,
            scenario2OldEMI = currentEMIAtTenure,
            scenario2NewEMI = newEMI,
            scenario2OldInterest = currentTotalInterestScenario2,
            scenario2NewInterest = newTotalInterestScenario2
        )
    } catch (e: Exception) {
        null
    }
}

private fun formatCurrencyWithDecimal(amount: Double): String {
    return String.format("%,.2f", amount)
}

