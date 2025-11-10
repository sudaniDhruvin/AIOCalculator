package com.belbytes.calculators.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belbytes.calculators.data.DataRepository
import com.belbytes.calculators.data.FeaturedTool
import com.belbytes.calculators.data.RecentCalculation
import com.belbytes.calculators.data.RecentCalculationRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeUiState(
    val featuredTools: List<FeaturedTool> = emptyList(),
    val recentCalculations: List<RecentCalculation> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class HomeViewModel(
    private val context: Context,
    private val dataRepository: DataRepository,
    private val recentCalculationRepository: RecentCalculationRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadData()
        cleanupDuplicates()
        observeRecentCalculations()
    }
    
    private fun cleanupDuplicates() {
        viewModelScope.launch {
            try {
                recentCalculationRepository.cleanupDuplicates()
            } catch (e: Exception) {
                // Ignore cleanup errors
                e.printStackTrace()
            }
        }
    }
    
    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // Load featured tools from JSON
                val appData = dataRepository.loadAppData(context)
                _uiState.update { 
                    it.copy(
                        featuredTools = appData.featuredTools,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = e.message ?: "Failed to load data",
                        isLoading = false
                    )
                }
            }
        }
    }
    
    private fun observeRecentCalculations() {
        viewModelScope.launch {
            recentCalculationRepository.getRecentCalculations(limit = 10)
                .collect { calculations ->
                    _uiState.update { 
                        it.copy(recentCalculations = calculations)
                    }
                }
        }
    }
    
    fun refresh() {
        loadData()
    }
}

