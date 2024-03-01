package cs486.splash.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot
import cs486.splash.models.BowelLog
import cs486.splash.models.BowelLog.Companion.toBowelLog
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
    val TAG = "BOWEL_LOG_VIEW_MODEL"
    var bowelLogs : MutableLiveData<List<BowelLog>> = MutableLiveData()

    private val bowelLogRepository: BowelLogRepository = BowelLogRepository()

    init {
        getAllBowelLogs()
    }

    fun getAllBowelLogs() : LiveData<List<BowelLog>> {
        bowelLogRepository.fetchAllBowelLogs().addSnapshotListener(EventListener<QuerySnapshot> { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@EventListener
            }

            val bowelLogList : MutableList<BowelLog> = mutableListOf()
            for (documentSnapshot in value!!) {
                val logItem = documentSnapshot.toBowelLog()
                logItem?.let {
                    // not null
                    bowelLogList.add(logItem)
                }
            }
            bowelLogs.value = bowelLogList
        })
        return bowelLogs
    }

    /**
     * Adds [bowelLog]
     */
    fun addNewBowelLog(bowelLog: BowelLog) {
        bowelLogRepository.addNewBowelLog(bowelLog)
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
    }
}