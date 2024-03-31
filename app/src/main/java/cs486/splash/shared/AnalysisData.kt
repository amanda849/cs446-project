package cs486.splash.shared

import cs486.splash.R
import cs486.splash.models.BowelLog
import java.util.Date
import kotlin.math.roundToInt

/**
 * A class containing all analysis data
 *
 * @property rangeStartDate Date            the starting timestamp of the analysis
 * @property rangeEndDate Date              the ending timestamp of the analysis
 * @property totalTimes Int                 the total number of entries in the time range
 * @property averageTimesPerDay Float       the average number of logs per day in the time range
 * @property mostCommonColours List         the most common colours of logs in the time range
 * @property numUnusualColours Int          the number of logs with unusual colours in the time range
 * @property percentageTextures Map         the percentage of logs with each texture in the time range
 * @property numLocations Int               the number of locations of logs in the time range
 * @property averageDuration Float          the average number of minutes per log in the time range
 * @property mostLogsHours List             the hours with the most logs in the time range
 * @property percentageSymptoms Map         the percentage of logs with each symptom in the time range
 * @property percentageFactors Map          the percentage of log with each factor in the time range
 *
 * @property sufficientData Boolean         whether there is at least one log to perform analysis on
 */
class AnalysisData {
    // time range
    private var rangeStartDate : Date = Date()
    private var rangeEndDate : Date = Date()
    private var numDays : Int = 0

    // analysis attributes
    private var totalTimes : Int = 0
    private var averageTimesPerDay : Float = 0F
    private var mostCommonColours : List<Colour> = listOf()
    private var numUnusualColours : Int = 0
    private var percentageTextures : Map<Texture, Float> = mapOf()
    private var numLocations : Int = 0
    private var averageDuration : Float = 0F
    private var mostLogsHours : List<Int> = listOf()
    private var percentageSymptoms : Map<String, Float> = mapOf()
    private var percentageFactors : Map<String, Float> = mapOf()

    private var sufficientData : Boolean = false

    /**
     * Returns true if there is sufficient data
     */
    fun hasSufficientData() : Boolean {
        return sufficientData
    }

    /**
     * Returns start date of the time range for which analysis is being computed for
     */
    fun getRangeStartDate() : Date {
        return rangeStartDate
    }

    /**
     * Returns end date of the time range for which analysis is being computed for
     */
    fun getRangeEndDate() : Date {
        return rangeEndDate
    }

    /**
     * Returns the total number of logs as a string
     */
    fun getTimesTotal() : String {
        return totalTimes.toString()
    }

    /**
     * Returns the average number of logs per day as a string
     */
    fun getAverageTimesPerDay() : String {
        return "%.2f".format(averageTimesPerDay)
    }

    /**
     * Returns a list of most common colours
     */
    fun getMostCommonColours() : List<Colour> {
        return mostCommonColours
    }

    /**
     * Returns the number of logs with unusual colours as a string
     */
    fun getNumUnusualColours() : String {
        return numUnusualColours.toString()
    }

    /**
     * Returns the number of blobs and the percentages of logs with each texture
     */
    fun getPercentageTextures() : Map<Texture, Pair<Int, String>> {
        return percentageTextures.mapValues {
            Pair((it.value / 10).roundToInt(), "%.2f".format(it.value) + "%")
        }
    }

    /**
     * Returns the number of locations as a string
     */
    fun getNumLocations() : String {
        return numLocations.toString()
    }

    /**
     * Returns the average duration in minutes as a string
     */
    fun getAverageDuration() : String {
        return "%.2f".format(averageDuration)
    }

    /**
     * Returns a comma delimited list of hours per day (0-23)
     * with the most logs as a string
     *
     * If 3 hours exceeded, returns "several hours of day"
     */
    fun getMostLogsHours() : String {
        if (mostLogsHours.size > 3) return "several hours of day"
        return mostLogsHours.joinToString { it -> formatHour(it) }
    }

    /**
     * Returns the percentages of logs with each symptom
     */
    fun getPercentageSymptoms() : Map<String, String> {
        return percentageSymptoms.mapValues { "%.2f".format(it.value) + "%" }
    }

    /**
     * Returns the percentages of logs with each factor
     */
    fun getPercentageFactors() : Map<String, String> {
        return percentageFactors.mapValues { "%.2f".format(it.value) + "%" }
    }

    /**
     * Recompute all analysis attributes for the given [logs]
     * within the range [startDate] to [endDate]
     */
    fun update(startDate : Date, endDate : Date, logs : List<BowelLog>) {
        if (logs.isEmpty()) return


        // Accumulators
        val timesPerDay : MutableList<Int> = mutableListOf(0)
        val colourCount : MutableMap<Colour, Int> = mutableMapOf(
            Colour.PALE_BROWN to 0,
            Colour.YELLOW_BROWN to 0,
            Colour.LIGHT_BROWN to 0,
            Colour.PINKISH_BROWN to 0,
            Colour.BROWN1 to 0,
            Colour.BROWN2 to 0,
            Colour.BROWN3 to 0,
            Colour.DARK_BROWN to 0,
            Colour.BLACK to 0,
            Colour.GREEN to 0,
        )
        val textureCount : MutableMap<Texture, Float> = mutableMapOf(
            Texture.PEBBLES to 0F,
            Texture.LUMPY to 0F,
            Texture.SAUSAGE to 0F,
            Texture.SMOOTH to 0F,
            Texture.BLOBS to 0F,
            Texture.MUSHY to 0F,
            Texture.LIQUID to 0F
        )
        val locations : MutableSet<String> = mutableSetOf()
        val durations : MutableList<Float> = mutableListOf()
        val hourCount : MutableList<Int> = MutableList(24) {0}
        val symptomsCount : MutableMap<String, Float> = mutableMapOf(
            "bloating" to 0F,
            "cramps" to 0F,
            "pain" to 0F,
            "nausea" to 0F,
            "fatigue" to 0F,
            "urgency" to 0F
        )
        val factorsCount : MutableMap<String, Float> = mutableMapOf(
            "workout" to 0F,
            "water" to 0F,
            "diet" to 0F,
            "fiber" to 0F
        )

        val numLogs = logs.size
        var currDay = getDayMidnight(rangeStartDate)
        var timesPerDayIndex = 0

        for (log in logs) {
            // averageTimesPerDay
            if (getDayMidnight(log.timeStarted) != currDay) {
                timesPerDayIndex++
                timesPerDay.add(0)
                currDay = getDayMidnight(log.timeStarted)
            }
            timesPerDay[timesPerDayIndex]++

            // mostCommonColour + numUnusualColours
            colourCount[log.color] = colourCount[log.color]!! + 1
            if (log.color in unusualColours) {
                numUnusualColours++
            }

            // percentageTextures
            textureCount[log.texture] = textureCount[log.texture]!! + 1

            // numLocations
            locations.add(log.location.lowercase())

            // averageDuration
            val duration = log.timeEnded.time - log.timeStarted.time
            durations.add(duration.toFloat())

            // mostLogsHours
            val hour = getHour(log.timeStarted)
            hourCount[hour]++

            // percentageSymptoms
            if (log.symptoms.bloating) symptomsCount["bloating"] = symptomsCount["bloating"]!! + 1
            if (log.symptoms.cramps) symptomsCount["cramps"] = symptomsCount["cramps"]!! + 1
            if (log.symptoms.pain) symptomsCount["pain"] = symptomsCount["pain"]!! + 1
            if (log.symptoms.nausea) symptomsCount["nausea"] = symptomsCount["nausea"]!! + 1
            if (log.symptoms.fatigue) symptomsCount["fatigue"] = symptomsCount["fatigue"]!! + 1
            if (log.symptoms.urgency) symptomsCount["urgency"] = symptomsCount["urgency"]!! + 1

            // percentageFactors
            if (log.factors.workout) factorsCount["workout"] = factorsCount["workout"]!! + 1
            if (log.factors.water) factorsCount["water"] = factorsCount["water"]!! + 1
            if (log.factors.diet) factorsCount["diet"] = factorsCount["diet"]!! + 1
            if (log.factors.fiber) factorsCount["fiber"] = factorsCount["fiber"]!! + 1
        }

        rangeStartDate = startDate
        rangeEndDate = endDate
        numDays = getNumDays(startDate, endDate)

        totalTimes = logs.size

        averageTimesPerDay = timesPerDay.sum().toFloat() / numDays

        val maxColourCount = colourCount.maxOf{ it.value }
        mostCommonColours = colourCount.filter{ it.value == maxColourCount }.map{ it.key }

        percentageTextures = textureCount.mapValues { (it.value / numLogs) * 100 }.toMap()

        numLocations = locations.count()

        averageDuration = ((durations.sum() / numLogs) / 60000)

        val maxHourCount = hourCount.max()
        mostLogsHours = hourCount.withIndex().filter{ it.value == maxHourCount }.map{ it.index }

        percentageSymptoms = symptomsCount.mapValues { (it.value / numLogs) * 100 }.toMap()

        percentageFactors = factorsCount.mapValues { (it.value / numLogs) * 100 }.toMap()

        sufficientData = true
    }
}