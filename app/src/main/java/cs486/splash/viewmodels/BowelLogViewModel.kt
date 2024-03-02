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
import cs486.splash.models.FactorTags
import cs486.splash.models.SymptomTags
import java.util.Date

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
     * Adds a bowel log
     */
    fun addNewBowelLog(color: Int, texture: String, timeStarted: Date, timeEnded: Date,
                       location: String, symptomTags: String, factorTags: String) {
        val currentDate = Date()
        val bowelLog = BowelLog(/*TODO:figure out how id is populated*/
            "", color, texture, timeStarted, timeEnded, location,
            SymptomTags(symptomTags), FactorTags(factorTags), currentDate, currentDate)
        bowelLogRepository.addNewBowelLog(bowelLog)
    }

    /**
     * Edits bowel log with id [id]
     */
    fun editBowelLog(id: String, color: Int, texture: String, timeStarted: Date, timeEnded: Date,
                     location: String, symptomTags: String, factorTags: String) {
        val currentDate = Date()
        for (bowelLog in bowelLogs.value!!) {
            if (bowelLog.id == id) {
                bowelLog.color = color
                bowelLog.texture = texture
                bowelLog.timeStarted = timeStarted
                bowelLog.timeEnded = timeEnded
                bowelLog.location = location
                bowelLog.symptoms = SymptomTags(symptomTags)
                bowelLog.factors = FactorTags(factorTags)
                bowelLog.timeModified = currentDate

                bowelLogRepository.editBowelLog(bowelLog)
            }
        }
    }

    /**
     * Returns the bowel log with id [bowelLogId]
     */
    fun getBowelLog(bowelLogId: String) : BowelLog? {
        for (bowelLog in bowelLogs.value!!) {
            if (bowelLog.id == bowelLogId) {
                return bowelLog
            }
        }
        return null
    }

    /**
     * Deletes the bowel log with id [bowelLogId]
     */
    fun deleteBowelLog(bowelLogId: String) {
        bowelLogRepository.deleteBowelLog(bowelLogId)
    }
}