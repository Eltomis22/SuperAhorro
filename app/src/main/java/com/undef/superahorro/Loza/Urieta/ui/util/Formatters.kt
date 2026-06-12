package com.undef.superahorro.Loza.Urieta.ui.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Helpers para formatear y parsear montos con separador de miles "punto".
 */
object Formatters {

    /** Toma sólo dígitos de la entrada y los formatea con punto cada 3 cifras. */
    fun formatearMiles(input: String): String {
        val digitos = input.filter { it.isDigit() }
        if (digitos.isEmpty()) return ""
        val sinCerosIzq = digitos.trimStart('0').ifEmpty { "0" }
        val sb = StringBuilder()
        val len = sinCerosIzq.length
        for (i in 0 until len) {
            if (i > 0 && (len - i) % 3 == 0) sb.append('.')
            sb.append(sinCerosIzq[i])
        }
        return sb.toString()
    }

    /** Inverso: convierte "1.000" -> 1000.0. */
    fun parsearMiles(formateado: String): Double? {
        return formateado.replace(".", "").toDoubleOrNull()
    }

    /** Formatea para mostrar como moneda fija. */
    fun formatearMoneda(monto: Double): String {
        val entero = monto.toLong().toString()
        return "$ ${formatearMiles(entero)}"
    }

    /**
     * Transformación visual para campos de texto de precios.
     * Permite que el usuario escriba solo números y el punto aparezca visualmente
     * sin mover el cursor de forma errática.
     */
    class ThousandsSeparatorTransformation : VisualTransformation {
        override fun filter(text: AnnotatedString): TransformedText {
            val originalText = text.text
            if (originalText.isEmpty()) return TransformedText(text, OffsetMapping.Identity)

            val formattedText = formatearMiles(originalText)

            val offsetMapping = object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    if (offset <= 0) return 0
                    val textBeforeCursor = originalText.substring(0, offset)
                    return formatearMiles(textBeforeCursor).length
                }

                override fun transformedToOriginal(offset: Int): Int {
                    if (offset <= 0) return 0
                    val formattedBeforeCursor = formattedText.substring(0, offset.coerceAtMost(formattedText.length))
                    return formattedBeforeCursor.count { it.isDigit() }
                }
            }

            return TransformedText(AnnotatedString(formattedText), offsetMapping)
        }
    }
}
