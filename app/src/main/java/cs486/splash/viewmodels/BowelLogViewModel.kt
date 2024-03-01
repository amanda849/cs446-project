package cs486.splash.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import cs486.splash.models.BowelLog
import cs486.splash.models.BowelLog.Companion.toBowelLog
import cs486.splash.models.BowelLogRepository

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

    /**
     * Ties bowelLogs to the state of the bowelLogRepository for the current user/collection
     * Do not call this again as it will tie another listener
     */
    private fun getAllBowelLogs() : ListenerRegistration {
        return bowelLogRepository.fetchAllBowelLogs().addSnapshotListener(EventListener<QuerySnapshot> addSnapshotListener@{ value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener

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
        bowelLogRepository.editBowelLog(bowelLog)
    }

    /**
     * Deletes the bowel log with id [bowelLogId]
     */
    fun deleteBowelLog(bowelLogId: String) {
        bowelLogRepository.deleteBowelLog(bowelLogId)
    }
}