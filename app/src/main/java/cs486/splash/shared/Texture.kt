package cs486.splash.shared

enum class Texture(private var t: String) {
    PEBBLES("Pebbles (Hard)"),
    LUMPY("Lumpy"),
    SAUSAGE("Sausage"),
    SMOOTH("Smooth"),
    BLOBS("Blobs (Soft)"),
    MUSHY("Mushy"),
    LIQUID("Liquid");
    
    override fun toString(): String {
        return t;
    }
}
