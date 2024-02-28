package cs486.splash.models

class FactorTags(
    var workout : Boolean = false,
    var water : Boolean = false,
    var diet : Boolean = false,
    var fiber : Boolean = false,
    var other : String = ""
) {
    override fun toString(): String {
        return "<workout: $workout, water: $water, diet: $diet, fiber: $fiber, other: '$other'>"
    }
}