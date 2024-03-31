package cs486.splash.shared

enum class Texture(private var t: String) {
    PEBBLES("Pebbles"),
    LUMPY("Lumpy"),
    SAUSAGE("Sausage"),
    SMOOTH("Smooth"),
    BLOBS("Blobs"),
    MUSHY("Mushy"),
    LIQUID("Liquid");
    
    override fun toString(): String {
        return t;
    }
}
