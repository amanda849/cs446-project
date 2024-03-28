package cs486.splash.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import cs486.splash.models.BowelLog
import cs486.splash.models.BowelLog.Companion.toBowelLog
import cs486.splash.models.BowelLogRepository
import cs486.splash.shared.AnalysisData
import cs486.splash.shared.Colour
import cs486.splash.shared.FactorTags
import cs486.splash.shared.SymptomTags
import cs486.splash.shared.Texture
import cs486.splash.shared.getPastMonthBeginDate
import cs486.splash.shared.getPastWeekBeginDate
import cs486.splash.shared.getPastYearBeginDate
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale

/**
 * Layer that facilitates communication between View and Model
 */
class BowelLogViewModel : ViewModel() {
    val TAG = "BOWEL_LOG_VIEW_MODEL"
    var bowelLogs: MutableLiveData<List<BowelLog>> = MutableLiveData(emptyList())
    var bowelLogsByDate : Map<String, List<BowelLog>> = mapOf<String, MutableList<BowelLog>>()

    private var weekAnalysisData : AnalysisData = AnalysisData()
    private var monthAnalysisData : AnalysisData = AnalysisData()
    private var yearAnalysisData : AnalysisData = AnalysisData()

    private val bowelLogRepository: BowelLogRepository = BowelLogRepository()

    init {
        getAllBowelLogs()
    }

    /**
     * Ties bowelLogs to the state of the bowelLogRepository for the current user/collection
     * Do not call this again as it will tie another listener
     */
    private fun getAllBowelLogs() : ListenerRegistration {
        return bowelLogRepository.fetchAllBowelLogs()
            .orderBy("timeStarted", Query.Direction.DESCENDING)
            .addSnapshotListener(EventListener<QuerySnapshot> addSnapshotListener@{ value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                bowelLogs.value = emptyList()
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
            bowelLogsByDate = orderBowelLogsByDate()
            updateAnalysisData()
        })
    }

    /**
     * Adds a bowel log
     */
    fun addNewBowelLog(color: Colour, texture: Texture, timeStarted: Date, timeEnded: Date,
                       location: String, symptomTags: SymptomTags, factorTags: FactorTags) {
        val currentDate = Date()
        val bowelLog = BowelLog("", color, texture, timeStarted, timeEnded, location,
            symptomTags, factorTags, currentDate, currentDate)
        bowelLogRepository.addNewBowelLog(bowelLog)

        // Update the analysis data when a new log is added
        updateAnalysisData()
    }

    /**
     * Edits bowel log with id [id]
     */
    fun editBowelLog(id: String, color: Colour, texture: Texture, timeStarted: Date, timeEnded: Date,
                     location: String, symptomTags: SymptomTags, factorTags: FactorTags) {
        val currentDate = Date()
        for (bowelLog in bowelLogs.value!!) {
            if (bowelLog.id == id) {
                bowelLog.color = color
                bowelLog.texture = texture
                bowelLog.timeStarted = timeStarted
                bowelLog.timeEnded = timeEnded
                bowelLog.location = location
                bowelLog.symptoms = symptomTags
                bowelLog.factors = factorTags
                bowelLog.timeModified = currentDate

                bowelLogRepository.editBowelLog(bowelLog)
            }
        }

        // Update the analysis data when a new log is updated
//        updateAnalysisData()
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
     * Returns the list of bowel logs starting from [startDate]
     */
    fun getBowelLogsFromDate(startDate: Date) : List<BowelLog> {
        val logs : MutableList<BowelLog> = mutableListOf()
        for (bowelLog in bowelLogs.value!!) {
            if (bowelLog.timeStarted.after(startDate)) {
                logs += bowelLog
            }
        }
        return logs.toList()
    }

    /**
     * Deletes the bowel log with id [bowelLogId]
     */
    fun deleteBowelLog(bowelLogId: String) {
        bowelLogRepository.deleteBowelLog(bowelLogId)

        // Update the analysis data when a log is deleted
        updateAnalysisData()
    }

    /**
     * Creates a map of all bowel logs keyed by date format dd/MM/yyyy
     */
    fun orderBowelLogsByDate() : Map<String, List<BowelLog>> {
        val collectedLogs : MutableMap<String, MutableList<BowelLog>> = mutableMapOf<String, MutableList<BowelLog>>()
        for (bowelLog in bowelLogs.value!!) {
            val dateString = SimpleDateFormat("dd/MM/yyyy", Locale.CANADA).format(bowelLog.timeStarted)
            if (collectedLogs.containsKey(dateString)) {
                collectedLogs[dateString]!!.add(bowelLog)
            } else {
                collectedLogs[dateString] = mutableListOf<BowelLog>(bowelLog)
            }
        }
        return collectedLogs
    }

    fun getBowelLogsOnDate(date : Date?) : List<BowelLog>? {
        if (date == null) return null

        val dateString = SimpleDateFormat("dd/MM/yyyy", Locale.CANADA).format(date)
        if (bowelLogsByDate.containsKey(dateString)){
            return bowelLogsByDate[dateString]
        }

        return null
    }

    fun getBowelLogsOnLocalDate(date : LocalDate?) : List<BowelLog>? {
        if (date == null) return null
        return getBowelLogsOnDate(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()))?.sortedBy {it.timeStarted}
    }

    /**
     * Update the analysis data for the past week until [d]
     */
    private fun updateAnalysisDataWeek(d : Date) {
        val beginDate = getPastWeekBeginDate(d)
        val logs = getBowelLogsFromDate(beginDate)
        weekAnalysisData.update(beginDate, d, logs)
    }

    /**
     * Update the analysis data for the past calendar month until [d]
     */
    private fun updateAnalysisDataMonth(d : Date) {
        val beginDate = getPastMonthBeginDate(d)
        val logs = getBowelLogsFromDate(beginDate)

        monthAnalysisData.update(beginDate, d, logs)
    }

    /**
     * Update the analysis data for the past calendar year until [d]
     */
    private fun updateAnalysisDataYear(d : Date) {
        val beginDate = getPastYearBeginDate(d)
        val logs = getBowelLogsFromDate(beginDate)

        yearAnalysisData.update(beginDate, d, logs)
    }

    /**
     * Recompute all analysis data for the past week, month and year
     */
    private fun updateAnalysisData() {
        // Get the current date one time for all time ranges
        val currDate = Date()

        updateAnalysisDataWeek(currDate)
        updateAnalysisDataMonth(currDate)
        updateAnalysisDataYear(currDate)
    }

    /**
     * Returns the analysis data from the past week
     */
    fun getAnalysisDataWeek() : AnalysisData {
        return weekAnalysisData
    }

    /**
     * Returns the analysis data from the past calendar month
     */
    fun getAnalysisDataMonth() : AnalysisData {
        return monthAnalysisData
    }

    /**
     * Returns the analysis data from the past calendar year
     */
    fun getAnalysisDataYear() : AnalysisData {
        return yearAnalysisData
    }
}