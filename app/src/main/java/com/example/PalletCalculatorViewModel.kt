package com.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class PalletCalculatorViewModel : ViewModel() {

    // Main input for the quantity of jabas
    private val _jabasInput = MutableStateFlow("")
    val jabasInput: StateFlow<String> = _jabasInput.asStateFlow()

    // Trigger state for the calculation animation (increments on manual 'CALCULAR' press)
    private val _animationTrigger = MutableStateFlow(0)
    val animationTrigger: StateFlow<Int> = _animationTrigger.asStateFlow()

    // Calculations based on the input
    val quantityState: StateFlow<Int> = _jabasInput.map { input ->
        input.toIntOrNull() ?: 0
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val palletsCount: StateFlow<Int> = quantityState.map { qty ->
        qty / 72
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val puchoCount: StateFlow<Int> = quantityState.map { qty ->
        qty % 72
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    // Formatted result representation following Spanish rules and the specific examples
    val resultText: StateFlow<String> = quantityState.map { qty ->
        val pallets = qty / 72
        val pucho = qty % 72

        val palletsPart = if (pallets == 1) "1 pallet" else "$pallets pallets"
        
        if (qty == 0) {
            "0 pallets"
        } else if (pucho == 0) {
            palletsPart
        } else {
            val puchoPart = if (pucho == 1) "1 jaba" else "$pucho jabas"
            "$palletsPart + $puchoPart"
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "0 pallets"
    )

    /**
     * Updates the input field with strict validation.
     * Only accepts digits (0-9). Drops spaces, signs, commas, periods, or letters.
     */
    fun onJabasInputChanged(newValue: String) {
        // Filter out non-digits
        val filtered = newValue.filter { it.isDigit() }
        
        // Handle leading zeros gracefully (e.g. typing '0' then '5' becomes '5')
        val normalized = if (filtered.length > 1 && filtered.startsWith("0")) {
            val withoutZeros = filtered.dropWhile { it == '0' }
            if (withoutZeros.isEmpty()) "0" else withoutZeros
        } else {
            filtered
        }
        
        _jabasInput.value = normalized
    }

    /**
     * Clears the input, resetting everything to 0.
     */
    fun clearInput() {
        _jabasInput.value = ""
    }

    /**
     * Manually triggers the short result animation and keyboard dismissal effects.
     */
    fun triggerCalculationAnimation() {
        _animationTrigger.value += 1
    }
}
