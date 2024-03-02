package cs486.splash.shared

data class SymptomTags(
    var bloating : Boolean = false,
    var cramps : Boolean = false,
    var pain : Boolean = false,
    var nausea : Boolean = false,
    var fatigue : Boolean = false,
    var urgency : Boolean = false,
    var other : String = ""
) {
    constructor(str: String) : this() {
        val values = str.split(", ")
        for (value in values) {
            val field = value.split(": ")
            if (field[0] == "bloating") {
                bloating = field[1].toBoolean()
            } else if (field[0] == "cramps") {
                cramps = field[1].toBoolean()
            } else if (field[0] == "pain") {
                pain = field[1].toBoolean()
            } else if (field[0] == "nausea") {
                nausea = field[1].toBoolean()
            } else if (field[0] == "fatigue") {
                fatigue = field[1].toBoolean()
            } else if (field[0] == "urgency") {
                urgency = field[1].toBoolean()
            } else {
                other = field[1]
            }
        }
    }
    override fun toString(): String {
        return "bloating: $bloating, cramps: $cramps, pain: $pain, nausea: $nausea, fatigue: $fatigue, urgency: $urgency, other: $other>"
    }
}