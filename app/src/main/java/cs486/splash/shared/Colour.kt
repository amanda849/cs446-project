package cs486.splash.shared

import androidx.annotation.ColorInt

enum class Colour(@ColorInt private var c: Int) {
    LIGHT_BROWN(0xFF9C6644.toInt()),
    PINKISH_BROWN(0xFF9C5444.toInt()),
    BROWN1(0xFF7F5539.toInt()),
    BROWN2(0xFF6E4428.toInt()),
    BROWN3(0xFF744627.toInt()),
    DARK_BROWN(0xFF4C2286.toInt());

    companion object {
        fun valueOf(@ColorInt value: Int) = Colour.entries.find { it.c == value }!!
    }

    @ColorInt
    fun toColorInt(): Int {
        return c
    }
}