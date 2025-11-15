package com.belbytes.calculators.ui.bank

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import android.content.Context
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

data class SimpleInterestResult(
    val principalAmount: Double,
    val interestAmount: Double,
    val totalAmount: Double
)

enum class InterestType {
    SIMPLE,
    COMPOUND
}

enum class CompoundingFrequency {
    MONTHLY,
    QUARTERLY,
    HALF_YEARLY,
    YEARLY
}

@Composable
fun SimpleInterestCalculatorScreen(
    onBackClick: () -> Unit
) {
    var selectedTab by rememberSaveable { mutableStateOf("Duration") }
    var amount by rememberSaveable { mutableStateOf("") }
    var interestRate by rememberSaveable { mutableStateOf("") }

    // Duration tab fields
    var years by rememberSaveable { mutableStateOf("") }
    var months by rememberSaveable { mutableStateOf("") }
    var days by rememberSaveable { mutableStateOf("") }

    // Date tab fields
    var fromDate by rememberSaveable { mutableStateOf<Date?>(null) }
    var toDate by rememberSaveable { mutableStateOf<Date?>(null) }

    // Interest type
    var interestTypeString by rememberSaveable { mutableStateOf(InterestType.SIMPLE.name) }
    val interestType = remember(interestTypeString) {
        try {
            InterestType.valueOf(interestTypeString)
        } catch (e: IllegalArgumentException) {
            InterestType.SIMPLE
        }
    }

    // Compounding frequency (only for compound interest)
    var compoundingFrequency by rememberSaveable { mutableStateOf("Monthly") }

    var showResults by rememberSaveable { mutableStateOf(false) }
    var result by remember { mutableStateOf<SimpleInterestResult?>(null) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    val tabs = listOf("Duration", "Date")
    val pagerState = rememberPagerState(initialPage = 0) { tabs.size }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Sync selectedTab with pager state
    LaunchedEffect(pagerState.currentPage) {
        selectedTab = tabs[pagerState.currentPage]
        showResults = false
        result = null
        errorMessage = null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        SimpleInterestCalculatorHeader(onBackClick = onBackClick)

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
            modifier = Modifier.weight(1f)
        ) { page ->
            val pageScrollState = rememberScrollState()
            
            // Scroll to end when results are shown
            LaunchedEffect(showResults, page) {
                if (showResults && pagerState.currentPage == page) {
                    delay(100) // Small delay to ensure content is rendered
                    pageScrollState.animateScrollTo(pageScrollState.maxValue)
                }
            }
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(pageScrollState)
                    .imePadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Amount Input
                SimpleInterestInputField(
                    label = "Amount",
                    placeholder = "Ex: 1,00,000",
                    value = amount,
                    onValueChange = { amount = it }
                )

                // Interest Input
                SimpleInterestInputField(
                    label = "Interest",
                    placeholder = "Ex: 7.5%",
                    value = interestRate,
                    onValueChange = { interestRate = it }
                )

                // Conditional Fields based on Tab
                if (tabs[page] == "Duration") {
                    // Period Inputs in One Row
                    Column {
                        Text(
                            text = "Period",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Years Input
                            Column(modifier = Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = years,
                                    onValueChange = { years = it },
                                    placeholder = {
                                        Text(
                                            text = "Years",
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
                                        focusedBorderColor = Color.Transparent
                                    ),
                                    singleLine = true
                                )
                            }

                            // Months Input
                            Column(modifier = Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = months,
                                    onValueChange = { newValue ->
                                        val numValue = newValue.toDoubleOrNull()
                                        if (newValue.isEmpty() || (numValue != null && numValue >= 0 && numValue <= 11)) {
                                            months = newValue
                                        }
                                    },
                                    placeholder = {
                                        Text(
                                            text = "Months",
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
                                        focusedBorderColor = Color.Transparent
                                    ),
                                    singleLine = true
                                )
                            }

                            // Days Input
                            Column(modifier = Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = days,
                                    onValueChange = { newValue ->
                                        val numValue = newValue.toDoubleOrNull()
                                        if (newValue.isEmpty() || (numValue != null && numValue >= 0 && numValue <= 30)) {
                                            days = newValue
                                        }
                                    },
                                    placeholder = {
                                        Text(
                                            text = "Days",
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
                                        focusedBorderColor = Color.Transparent
                                    ),
                                    singleLine = true
                                )
                            }
                        }
                    }
                } else {
                    // Date Inputs
                    val context = LocalContext.current

                    // From Date
                    SimpleInterestDateField(
                        label = "From Date",
                        date = fromDate,
                        onDateSelected = { fromDate = it },
                        context = context
                    )

                    // To Date
                    SimpleInterestDateField(
                        label = "To Date",
                        date = toDate,
                        onDateSelected = { toDate = it },
                        context = context,
                        minDate = fromDate
                    )
                }

                // Interest Type Radio Buttons
                Column {
                    Text(
                        text = "Interest Type",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        SimpleInterestRadioButton(
                            label = "Simple",
                            selected = interestType == InterestType.SIMPLE,
                            onClick = { interestTypeString = InterestType.SIMPLE.name }
                        )
                        SimpleInterestRadioButton(
                            label = "Compound",
                            selected = interestType == InterestType.COMPOUND,
                            onClick = { interestTypeString = InterestType.COMPOUND.name }
                        )
                    }
                }

                // Compounding Frequency Dropdown (only shown when Compound is selected)
                AnimatedVisibility(
                    visible = interestType == InterestType.COMPOUND,
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
                    SimpleInterestCompoundingDropdown(
                        label = "Compounded",
                        value = compoundingFrequency,
                        onValueChange = { compoundingFrequency = it }
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
                            keyboardController?.hide() // Hide keyboard when calculate is clicked
                            errorMessage = null
                            val calculationResult = calculateSimpleInterest(
                                selectedTab = selectedTab,
                                amount = amount,
                                interestRate = interestRate,
                                years = years,
                                months = months,
                                days = days,
                                fromDate = fromDate,
                                toDate = toDate,
                                interestType = interestType,
                                compoundingFrequency = compoundingFrequency
                            )
                            if (calculationResult != null) {
                                result = calculationResult
                                showResults = true
                                errorMessage = null
                            } else {
                                showResults = false
                                result = null
                                errorMessage = when {
                                    amount.isBlank() || (amount.toDoubleOrNull() ?: -1.0) <= 0 ->
                                        "Please enter a valid amount"
                                    interestRate.isBlank() || (interestRate.toDoubleOrNull() ?: -1.0) <= 0 ->
                                        "Please enter a valid interest rate"
                                    selectedTab == "Duration" && years.isBlank() && months.isBlank() && days.isBlank() ->
                                        "Please enter at least one period value"
                                    selectedTab == "Duration" && months.isNotBlank() -> {
                                        val monthsValue = months.toDoubleOrNull()
                                        when {
                                            monthsValue == null -> "Please enter a valid number of months"
                                            monthsValue < 0 -> "Months cannot be negative"
                                            monthsValue > 11 -> "Months cannot exceed 11"
                                            else -> null
                                        } ?: ""
                                    }
                                    selectedTab == "Duration" && days.isNotBlank() -> {
                                        val daysValue = days.toDoubleOrNull()
                                        when {
                                            daysValue == null -> "Please enter a valid number of days"
                                            daysValue < 0 -> "Days cannot be negative"
                                            daysValue > 30 -> "Days cannot exceed 30"
                                            else -> null
                                        } ?: ""
                                    }
                                    selectedTab == "Date" && (fromDate == null || toDate == null) ->
                                        "Please select both from and to dates"
                                    selectedTab == "Date" && fromDate != null && toDate != null && toDate!!.before(fromDate) ->
                                        "To date must be greater than or equal to from date"
                                    else -> {
                                        // Check for invalid month/day values
                                        val monthsValue = months.toDoubleOrNull()
                                        val daysValue = days.toDoubleOrNull()
                                        when {
                                            monthsValue != null && monthsValue > 11 -> "Months cannot exceed 11"
                                            daysValue != null && daysValue > 30 -> "Days cannot exceed 30"
                                            else -> "Please check all input values"
                                        }
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
                            amount = ""
                            interestRate = ""
                            years = ""
                            months = ""
                            days = ""
                            fromDate = null
                            toDate = null
                            interestTypeString = InterestType.SIMPLE.name
                            compoundingFrequency = "Monthly"
                            showResults = false
                            result = null
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
                    visible = showResults && result != null,
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
                    result?.let { res ->
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

                            // Results Card
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
                                    SimpleInterestResultRow(
                                        label = "Principal Amount",
                                        value = formatCurrencyWithDecimal(res.principalAmount)
                                    )
                                    SimpleInterestResultRow(
                                        label = "Interest Amount",
                                        value = formatCurrencyWithDecimal(res.interestAmount)
                                    )
                                    SimpleInterestResultRow(
                                        label = "Total Amount",
                                        value = formatCurrencyWithDecimal(res.totalAmount)
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Extra spacing at bottom to ensure buttons remain accessible
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun SimpleInterestCalculatorHeader(onBackClick: () -> Unit) {
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
            text = "Simple Interest",
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
fun SimpleInterestInputField(
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
fun SimpleInterestDateField(
    label: String,
    date: Date?,
    onDateSelected: (Date) -> Unit,
    context: Context,
    minDate: Date? = null
) {
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val initialYear = date?.let {
                        val cal = Calendar.getInstance()
                        cal.time = it
                        cal.get(Calendar.YEAR)
                    } ?: calendar.get(Calendar.YEAR)
                    
                    val initialMonth = date?.let {
                        val cal = Calendar.getInstance()
                        cal.time = it
                        cal.get(Calendar.MONTH)
                    } ?: calendar.get(Calendar.MONTH)
                    
                    val initialDay = date?.let {
                        val cal = Calendar.getInstance()
                        cal.time = it
                        cal.get(Calendar.DAY_OF_MONTH)
                    } ?: calendar.get(Calendar.DAY_OF_MONTH)
                    
                    val datePickerDialog = DatePickerDialog(
                        context,
                        null, // We'll handle date selection with setOnDateChangedListener
                        initialYear,
                        initialMonth,
                        initialDay
                    )
                    
                    // Set minimum date if provided
                    minDate?.let {
                        datePickerDialog.datePicker.minDate = it.time
                    }
                    
                    // Set up listener to auto-dismiss when date is selected (fires immediately on date change)
                    datePickerDialog.datePicker.setOnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
                        calendar.set(year, monthOfYear, dayOfMonth)
                        onDateSelected(calendar.time)
                        datePickerDialog.dismiss()
                    }
                    
                    datePickerDialog.show()
                }
        ) {
            OutlinedTextField(
                value = date?.let { dateFormat.format(it) } ?: "",
                onValueChange = {},
                readOnly = true,
                enabled = false,
                placeholder = {
                    Text(
                        text = "Select date",
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
                    disabledContainerColor = Color(0xFFF5F5F5),
                    disabledBorderColor = Color.Transparent,
                    disabledTextColor = Color.Black
                ),
                singleLine = true
            )
        }
    }
}

@Composable
fun SimpleInterestRadioButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = Color(0xFF222222),
                unselectedColor = Color(0xFF757575)
            )
        )
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Black,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
fun SimpleInterestCompoundingDropdown(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("Monthly", "Quarterly", "Half Yearly", "Yearly")

    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedContainerColor = Color(0xFFF5F5F5),
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent,
                    disabledContainerColor = Color(0xFFF5F5F5),
                    disabledBorderColor = Color.Transparent,
                    disabledTextColor = Color.Black
                ),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown",
                        tint = Color.Gray
                    )
                }
            )
        }

        AnimatedVisibility(
            visible = expanded,
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
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column {
                    options.forEachIndexed { index, option ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onValueChange(option)
                                    expanded = false
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Text(
                                text = option,
                                fontSize = 14.sp,
                                color = Color.Black
                            )
                        }
                        if (index < options.size - 1) {
                            Divider(
                                color = Color(0xFFE0E0E0),
                                thickness = 1.dp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SimpleInterestResultRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = Color.Black,
            fontWeight = FontWeight.Normal
        )
    }
}

fun calculateSimpleInterest(
    selectedTab: String,
    amount: String,
    interestRate: String,
    years: String,
    months: String,
    days: String,
    fromDate: Date?,
    toDate: Date?,
    interestType: InterestType,
    compoundingFrequency: String
): SimpleInterestResult? {
    return try {
        val principal = amount.toDoubleOrNull() ?: return null
        val rate = interestRate.toDoubleOrNull() ?: return null

        if (principal <= 0 || rate <= 0) return null

        // Calculate time period
        val timeInYears = if (selectedTab == "Duration") {
            val yearsValue = years.toDoubleOrNull() ?: 0.0
            val monthsValue = months.toDoubleOrNull() ?: 0.0
            val daysValue = days.toDoubleOrNull() ?: 0.0

            if (yearsValue == 0.0 && monthsValue == 0.0 && daysValue == 0.0) return null

            yearsValue + (monthsValue / 12.0) + (daysValue / 365.0)
        } else {
            // Date tab
            if (fromDate == null || toDate == null) return null
            if (toDate.before(fromDate)) return null

            val diffInMillis = toDate.time - fromDate.time
            val diffInDays = diffInMillis / (1000.0 * 60 * 60 * 24)
            diffInDays / 365.0
        }

        if (timeInYears <= 0) return null

        val r = rate / 100.0

        val interestAmount: Double
        val totalAmount: Double

        if (interestType == InterestType.SIMPLE) {
            // Simple Interest: I = P * r * t
            interestAmount = principal * r * timeInYears
            totalAmount = principal + interestAmount
        } else {
            // Compound Interest
            val n = when (compoundingFrequency) {
                "Monthly" -> 12
                "Quarterly" -> 4
                "Half Yearly" -> 2
                "Yearly" -> 1
                else -> 12
            }

            // Compound Interest: A = P * (1 + r/n)^(n*t)
            val amount = principal * java.lang.Math.pow(1 + r / n, n * timeInYears)
            interestAmount = amount - principal
            totalAmount = amount
        }

        SimpleInterestResult(
            principalAmount = principal,
            interestAmount = interestAmount,
            totalAmount = totalAmount
        )
    } catch (e: Exception) {
        null
    }
}

private fun formatCurrencyWithDecimal(amount: Double): String {
    return String.format("%,.2f", amount)
}