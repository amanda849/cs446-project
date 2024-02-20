package cs486.splash.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.concurrent.thread
import cs486.splash.model.BowelLog
import cs486.splash.model.BowelLogRepo

data class BowelContentUiState(
    val bowelLogs: List<BowelLog> = emptyList(),
)

class BowelLogViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(BowelContentUiState())
    val uiState: StateFlow<BowelContentUiState> = _uiState.asStateFlow()

    init {
        thread {
            _uiState.update { currentState ->
                currentState.copy(bowelLogs = BowelLogRepo.fetchAllBowelLogs())
            }
        }
    }

    fun addNewBowelLog(bowelLog: BowelLog) {
        _uiState.update { currentState ->
            currentState.copy(bowelLogs = BowelLogRepo.addNewBowelLog(bowelLog))
        }
    }
}