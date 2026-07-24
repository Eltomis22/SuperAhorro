package com.undef.superahorro.Loza.Urieta.ui.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Helpers para formatear y parsear montos.
 */
object Formatters {

    private val dbDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val uiDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    /** Convierte fecha de DB (yyyy-MM-dd) a UI (dd/MM/yyyy) */
    fun formatearFecha(fecha: String): String {
        return try {
            val localDate = LocalDate.parse(fecha, dbDateFormatter)
            localDate.format(uiDateFormatter)
        } catch (e: Exception) {
            fecha
        }
    }

    /**
     * Formatea un número como moneda sin decimales para la UI general.
     */
    fun formatearMoneda(monto: Double): String {
        val entero = monto.toLong().toString()
        val sb = StringBuilder()
        val len = entero.length
        for (i in 0 until len) {
            if (i > 0 && (len - i) % 3 == 0) sb.append('.')
            sb.append(entero[i])
        }
        return "$ ${if (sb.isEmpty()) "0" else sb.toString()}"
    }

    /**
     * Transformación visual para campos de texto que admiten decimales (con coma).
     * Muestra puntos de miles mientras el usuario escribe.
     */
    class ThousandsSeparatorTransformation : VisualTransformation {
        override fun filter(text: AnnotatedString): TransformedText {
            val originalText = text.text
            if (originalText.isEmpty()) return TransformedText(text, OffsetMapping.Identity)

            // Dividimos en parte entera y decimal
            val parts = originalText.split(',')
            val partEntera = parts[0]
            val partDecimal = if (parts.size > 1) "," + parts[1].take(2) else ""

            // Formatear miles en la parte entera
            val sb = StringBuilder()
            val cleanEntera = partEntera.filter { it.isDigit() }
            val len = cleanEntera.length
            for (i in 0 until len) {
                if (i > 0 && (len - i) % 3 == 0) sb.append('.')
                sb.append(cleanEntera[i])
            }
            
            val formattedText = sb.toString() + partDecimal

            val offsetMapping = object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    if (offset <= 0) return 0
                    val sub = originalText.substring(0, offset.coerceAtMost(originalText.length))
                    val subParts = sub.split(',')
                    val subEntera = subParts[0].filter { it.isDigit() }
                    
                    // Contamos cuántos puntos de miles agregamos en el substring
                    var points = 0
                    val l = subEntera.length
                    for (i in 0 until l) {
                        if (i > 0 && (l - i) % 3 == 0) points++
                    }
                    
                    // Si el cursor ya pasó la coma
                    return if (sub.contains(',')) {
                        formattedText.indexOf(',') + (sub.length - originalText.indexOf(','))
                    } else {
                        subEntera.length + points
                    }
                }

                override fun transformedToOriginal(offset: Int): Int {
                    if (offset <= 0) return 0
                    val subFormatted = formattedText.substring(0, offset.coerceAtMost(formattedText.length))
                    return subFormatted.count { it.isDigit() || it == ',' }
                }
            }

            return TransformedText(AnnotatedString(formattedText), offsetMapping)
        }
    }
}
