package cs486.splash.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.concurrent.thread
import cs486.splash.models.BowelLog
import cs486.splash.models.BowelLogRepo


/**
 * Holds a immutable copy of the model's state
 */
data class BowelContentUiState(
    val bowelLogs: Map<String, BowelLog> = mapOf<String, BowelLog>()
)

/**
 * Layer that facilitates communication between View and Model
 */
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

    /**
     * Adds [bowelLog]
     */
    fun addNewBowelLog(bowelLog: BowelLog) {
        _uiState.update { currentState ->
            currentState.copy(bowelLogs = BowelLogRepo.addNewBowelLog(bowelLog))
        }
    }

    /**
     * Edits [bowelLog]
     */
    fun editBowelLog(bowelLog: BowelLog) {
        _uiState.update { currentState ->
            currentState.copy(bowelLogs = BowelLogRepo.editBowelLog(bowelLog))
        }
    }

    /**
     * Deletes the bowel log with id [bowelLogId]
     */
    fun deleteBowelLog(bowelLogId: String) {
        _uiState.update { currentState ->
            currentState.copy(bowelLogs = BowelLogRepo.deleteBowelLog(bowelLogId))
        }
    }
}