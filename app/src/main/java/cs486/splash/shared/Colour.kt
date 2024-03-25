package cs486.splash.shared

import androidx.annotation.ColorInt

enum class Colour(@ColorInt private var c: Long) {
    LIGHT_BROWN(0xFF9C6644),
    PINKISH_BROWN(0xFF9C5444),
    BROWN1(0xFF7F5539),
    BROWN2(0xFF6E4428),
    BROWN3(0xFF744627),
    DARK_BROWN(0xFF4C2206);

    companion object {
        fun valueOf(@ColorInt value: Long) = Colour.entries.find { it.c == value }!!
    }

    @ColorInt
    fun toColorLong(): Long {
        return c
    }
}

val unusualColours : List<Colour> = listOf(Colour.PINKISH_BROWN)
