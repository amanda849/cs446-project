package cs486.splash.model

/**
 * Temporarily the object that serves as storage holding the bowel logs. To be updated with
 * communication to a backend server
 */
object  BowelLogRepo {
    // Holds bowel logs, key is the id and value is the log
    private var bowelLogs = HashMap<String, BowelLog>()

    /**
     * Returns the bowel logs
     */
    fun fetchAllBowelLogs(): Map<String, BowelLog> {
        return bowelLogs
    }

    /**
     * Returns the bowel logs after adding [bowelLog] to the repo
     */
    fun addNewBowelLog(bowelLog: BowelLog) : Map<String, BowelLog>{
        // should add logic for checking that id is unique
        // mb also add logic for assigning ID, as that should be done in the backend as oppose to frontend
        bowelLogs[bowelLog.id] = bowelLog
        return bowelLogs
    }

    /**
     * Returns the bowel logs after editing [editedBowelLog]
     */
    fun editBowelLog(editedBowelLog: BowelLog) : Map<String, BowelLog>{
        bowelLogs[editedBowelLog.id] = editedBowelLog
        return bowelLogs
    }

    /**
     * Returns the bowel logs after deleting the bowel log with id [bowelLogId]
     */
    fun deleteBowelLog(bowelLogId: String) : Map<String, BowelLog> {
        bowelLogs.remove(bowelLogId)
        return bowelLogs
    }
}