package cs486.splash.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.concurrent.thread
import cs486.splash.models.BowelLog
import cs486.splash.models.BowelLogRepository


/**
 * Holds a immutable copy of the model's state
 */
data class BowelContentUiState(
    val bowelLogs: List<DocumentSnapshot> = emptyList()
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
                currentState.copy(bowelLogs = BowelLogRepository.fetchAllBowelLogs())
            }
        }
    }
/** Need to be modified
    /**
     * Adds [bowelLog]
     */
    fun addNewBowelLog(bowelLog: BowelLog) {
        /*
        _uiState.update { currentState ->
            currentState.copy(bowelLogs = BowelLogRepository.addNewBowelLog(bowelLog))
        }*/
    }

    /**
     * Edits [bowelLog]
     */
    fun editBowelLog(bowelLog: BowelLog) {
        /*
        _uiState.update { currentState ->
            currentState.copy(bowelLogs = BowelLogRepository.editBowelLog(bowelLog))
        }*/
    }

    /**
     * Deletes the bowel log with id [bowelLogId]
     */
    fun deleteBowelLog(bowelLogId: String) {
        /*
        _uiState.update { currentState ->
            currentState.copy(bowelLogs = BowelLogRepository.deleteBowelLog(bowelLogId))
        }*/
    }**/
}