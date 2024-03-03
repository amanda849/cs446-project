package cs486.splash.shared

import cs486.splash.models.BowelLog
import java.util.Date

/**
 * A class containing all analysis data
 *
 * @property rangeStartDate Date            the starting timestamp of the analysis
 * @property rangeEndDate Date              the ending timestamp of the analysis
 * @property averageTimesPerDay Float       the average number of logs per day in the time range
 * @property mostCommonColours List         the most common colours of logs in the time range
 * @property numUnusualColours Int          the number of logs with unusual colours in the time range
 * @property percentageTextures Map         the percentage of logs with each texture in the time range
 * @property numLocations Int               the number of locations of logs in the time range
 * @property averageDuration Int            the average number of seconds per log in the time range
 * @property mostLogsHours List             the hours with the most logs in the time range
 * @property percentageSymptoms Map         the percentage of logs with each symptom in the time range
 * @property percentageFactors Map          the percentage of log with each factor in the time range
 */
class AnalysisData {
    // time range
    private var rangeStartDate : Date = Date()
    private var rangeEndDate : Date = Date()

    // analysis attributes
    private var averageTimesPerDay : Float = 0F
    private var mostCommonColours : List<Colour> = listOf()
    private var numUnusualColours : Int = 0
    private var percentageTextures : Map<Texture, Float> = mapOf()
    private var numLocations : Int = 0
    private var averageDuration : Int = 0
    private var mostLogsHours : List<Int> = listOf()
    private var percentageSymptoms : Map<String, Float> = mapOf()
    private var percentageFactors : Map<String, Float> = mapOf()

    /**
     * Recompute all analysis attributes for the given [logs]
     * within the range [startDate] to [endDate]
     */
    fun update(startDate : Date, endDate : Date, logs : List<BowelLog>) {
        // Accumulators
        val timesPerDay : MutableList<Int> = mutableListOf(0)
        val colourCount : MutableMap<Colour, Int> = mutableMapOf(
            Colour.LIGHT_BROWN to 0,
            Colour.PINKISH_BROWN to 0,
            Colour.BROWN1 to 0,
            Colour.BROWN2 to 0,
            Colour.BROWN3 to 0,
            Colour.DARK_BROWN to 0
        )
        val textureCount : MutableMap<Texture, Float> = mutableMapOf(
            Texture.SOLID to 0F,
            Texture.SOFT to 0F,
            Texture.PEBBLES to 0F
        )
        val locations : MutableSet<String> = mutableSetOf()
        val durations : MutableList<Int> = mutableListOf()
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
            locations.add(log.location)

            // averageDuration
            val duration = log.timeEnded.time - log.timeStarted.time
            durations.add(duration.toInt())

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

        averageTimesPerDay = timesPerDay.sum().toFloat() / numLogs

        val maxColourCount = colourCount.maxOf{ it.value }
        mostCommonColours = colourCount.filter{ it.value == maxColourCount }.map{ it.key }

        percentageTextures = textureCount.mapValues { (it.value / numLogs) * 100 }.toMap()

        numLocations = locations.count()

        averageDuration = (durations.sum().toFloat() / numLogs).toInt()

        val maxHourCount = hourCount.max()
        mostLogsHours = hourCount.withIndex().filter{ it.value == maxHourCount }.map{ it.index }

        percentageSymptoms = symptomsCount.mapValues { (it.value / numLogs) * 100 }.toMap()

        percentageFactors = factorsCount.mapValues { (it.value / numLogs) * 100 }.toMap()
    }
}