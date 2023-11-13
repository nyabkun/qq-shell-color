// 2023. nyabkun  MIT LICENSE

package nyab.conf

import java.awt.Color
import nyab.img.qHexColorToRGBPacked
import nyab.util.QColor

// qq-shell-color is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=2] = QMyColorStyle <-[Ref]- QColor.toAWTColor()[Root]
enum class QMyColorStyle(private val cssHex: (QColor) -> String) {
    // CallChain[size=3] = QMyColorStyle.Dracula <-[Propag]- QMyColorStyle.toAWTColor() <-[Call]- QColor.toAWTColor()[Root]
    Dracula({ color ->
        when(color) {
            QColor.Black -> "#000000"
            QColor.White -> "#ffffff"

            QColor.DefaultBG -> "#282a36"
            QColor.DefaultFG -> "#f8f8f2"

            QColor.Red -> "#ff5555"
            QColor.LightRed -> "#ff4c4c" // Original
            QColor.Green -> "#50fa7b"
            QColor.LightGreen -> "#96faaf" // Original
            QColor.Yellow -> "#f1fa8c"
            QColor.LightYellow -> "#f8fcc7" // Original
            QColor.Blue -> "#3287ee"
            QColor.LightBlue -> "#90bdf0" // Original
            QColor.Purple -> "#bd93f9"
            QColor.LightPurple -> "#d7bdfc"
            QColor.Cyan -> "#8be9fd"
            QColor.LightCyan -> "#c2f4ff" // Original

            QColor.LightGray -> "#c0c0c0"
            QColor.DarkGray -> "#555555"

            QColor.ErrorBG -> "#693c3c" // Original
            QColor.CurrentLine -> "#44475a" // Original
            QColor.Selection -> "#64b9ff" // Original
            QColor.Caret -> "#b1def5" // Original
        }
    }),

    // CallChain[size=3] = QMyColorStyle.OneDarkVivid <-[Propag]- QMyColorStyle.toAWTColor() <-[Call]- QColor.toAWTColor()[Root]
    OneDarkVivid({ color ->
        when(color) {
            QColor.Black -> "#000000"
            QColor.White -> "#ffffff"

            QColor.DefaultBG -> "#282c34"
            QColor.DefaultFG -> "#bbbbbb"

            QColor.Red -> "#ef596f"
            QColor.LightRed -> "#f092a0" // Original
            QColor.Green -> "#89ca78"
            QColor.LightGreen -> "#c6e3bf" // Original
            QColor.Yellow -> "#e5c07b"
            QColor.LightYellow -> "#e6d3b1" // Original
            QColor.Blue -> "#61afef"
            QColor.LightBlue -> "#a3cdf0" // Original
            QColor.Purple -> "#d55fde"
            QColor.LightPurple -> "#db9be0" // Original
            QColor.Cyan -> "#2bbac5"
            QColor.LightCyan -> "#91c5c9" // Original
            QColor.LightGray -> "#646a73"
            QColor.DarkGray -> "#464c55"

//            else -> "#FF0000"
            QColor.ErrorBG -> "#693c3c" // Original
            QColor.CurrentLine -> "#44475a" // Original
            QColor.Selection -> "#64b9ff" // Original 100, 185, 255, 50
            QColor.Caret -> "#319ee5" // Original
        }
    });

    companion object {
        
    }

    // CallChain[size=3] = QMyColorStyle.toCSSHex() <-[Call]- QMyColorStyle.toAWTColor() <-[Call]- QColor.toAWTColor()[Root]
    fun toCSSHex(color: QColor): String {
        return cssHex(color)
    }

    // CallChain[size=2] = QMyColorStyle.toAWTColor() <-[Call]- QColor.toAWTColor()[Root]
    fun toAWTColor(color: QColor): Color {
        return Color(qHexColorToRGBPacked(toCSSHex(color)))
    }
}