package cs486.splash.model

/**
 * Temporarily the object that serves as storage holding the bowel logs. To be updated with
 * communication to a backend server
 */
object  BowelLogRepo {
    var bowelLogs = mutableListOf<BowelLog>()
    fun fetchAllBowelLogs(): List<BowelLog> {
        return bowelLogs
    }

    fun addNewBowelLog(bowelLog: BowelLog) : List<BowelLog>{
        bowelLogs.add(bowelLog)
        return bowelLogs
    }
}