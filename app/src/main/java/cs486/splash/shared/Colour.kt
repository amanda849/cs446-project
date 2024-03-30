package cs486.splash.shared

import androidx.annotation.ColorInt
import androidx.compose.ui.graphics.Color

val colorsDef: List<Color> = listOf(
    Color(0xFFa69b8a),
    Color(0xFF8A6C3A),
    Color(0xFF9C6644),
    Color(0xFFa35a53),
    Color(0xFF7F5539),
    Color(0xFF6E4428),
    Color(0xFF744627),
    Color(0xFF4C2206),
    Color(0xFF000000),
    Color(0xFF33422c),
)

enum class Colour(@ColorInt private var c: Long) {
    PALE_BROWN(0xFFa69b8a),
    YELLOW_BROWN(0xFF8A6C3A),
    LIGHT_BROWN(0xFF9C6644),
    PINKISH_BROWN(0xFFa35a53),
    BROWN1(0xFF7F5539),
    BROWN2(0xFF6E4428),
    BROWN3(0xFF744627),
    DARK_BROWN(0xFF4C2206),
    BLACK(0xFF000000),
    GREEN(0xFF33422c);

    companion object {
        fun valueOf(@ColorInt value: Long) = Colour.entries.find { it.c == value }!!
    }

    @ColorInt
    fun toColorLong(): Long {
        return c
    }
}

val unusualColours : List<Colour> = listOf(Colour.PINKISH_BROWN)
