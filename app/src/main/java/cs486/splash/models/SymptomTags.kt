package cs486.splash.models

data class SymptomTags(
    var bloating : Boolean = false,
    var cramps : Boolean = false,
    var pain : Boolean = false,
    var nausea : Boolean = false,
    var fatigue : Boolean = false,
    var urgency : Boolean = false,
    var other : String = ""
) {
    override fun toString(): String {
        return "<bloating: $bloating, cramps: $cramps, pain: $pain, nausea: $nausea, fatigue: $fatigue, urgency: $urgency, other: '$other'>"
    }
}