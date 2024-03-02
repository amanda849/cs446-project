package cs486.splash.shared

enum class Texture(private var t: String) {
    SOLID("Solid"), SOFT("Soft"), PEBBLES("Pebbles");
    
    override fun toString(): String {
        return t;
    }
}
