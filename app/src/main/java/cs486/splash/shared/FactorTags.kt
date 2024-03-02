package cs486.splash.shared

class FactorTags(
    var workout : Boolean = false,
    var water : Boolean = false,
    var diet : Boolean = false,
    var fiber : Boolean = false,
    var other : String = ""
) {
    constructor(str: String) : this() {
        val values = str.split(", ")
        for (value in values) {
            val field = value.split(": ")
            if (field[0] == "workout") {
                workout = field[1].toBoolean()
            } else if (field[0] == "water") {
                water = field[1].toBoolean()
            } else if (field[0] == "diet") {
                diet = field[1].toBoolean()
            } else if (field[0] == "fiber") {
                fiber = field[1].toBoolean()
            } else {
                other = field[1]
            }
        }
    }
    override fun toString(): String {
        return "workout: $workout, water: $water, diet: $diet, fiber: $fiber, other: $other"
    }
}