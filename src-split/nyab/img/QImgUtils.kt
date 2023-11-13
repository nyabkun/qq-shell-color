// 2023. nyabkun  MIT LICENSE

package nyab.img

import java.awt.Color
import java.util.*
import kotlin.math.absoluteValue

// qq-shell-color is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=3] = qHexColorToRGBPacked() <-[Call]- QMyColorStyle.toAWTColor() <-[Call]- QColor.toAWTColor()[Root]
internal fun qHexColorToRGBPacked(cssHex: String): Int {
    val hex = if (cssHex.startsWith("#")) {
        cssHex.substring(1)
    } else {
        cssHex
    }

    return when (hex.length) {
        3 -> {
            val hr = hex.substring(0, 1)
            val hg = hex.substring(1, 2)
            val hb = hex.substring(2, 3)
            val r = (hr + hr).toInt(16)
            val g = (hg + hg).toInt(16)
            val b = (hb + hb).toInt(16)

            intArrayOf(r, g, b).qPackToRGBInt()
        }

        6 -> {
            val r = hex.substring(0, 2).toInt(16)
            val g = hex.substring(2, 4).toInt(16)
            val b = hex.substring(4, 6).toInt(16)
            intArrayOf(r, g, b).qPackToRGBInt()
        }

        8 -> {
            val r = hex.substring(0, 2).toInt(16)
            val g = hex.substring(2, 4).toInt(16)
            val b = hex.substring(4, 6).toInt(16)
            val a = hex.substring(6, 8).toInt(16)
            intArrayOf(r, g, b, a).qPackToARGBInt()
        }

        else -> {
            throw IllegalArgumentException(hex)
//            QE.IllegalArgument.throwIt(hex)
        }
    }
}

// CallChain[size=4] = IntArray.qPackToARGBInt() <-[Call]- qHexColorToRGBPacked() <-[Call]- QMyColorStyle.toAWTColor() <-[Call]- QColor.toAWTColor()[Root]
internal fun IntArray.qPackToARGBInt(): Int {
    return (this[0] shl 24) or (this[1] shl 16) or (this[2] shl 8) or (this[3])
}

// CallChain[size=4] = IntArray.qPackToRGBInt() <-[Call]- qHexColorToRGBPacked() <-[Call]- QMyColorStyle.toAWTColor() <-[Call]- QColor.toAWTColor()[Root]
internal fun IntArray.qPackToRGBInt(): Int {
    return (this[0] shl 16) or (this[1] shl 8) or (this[2])
}