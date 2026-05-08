package com.undef.superahorro.Loza.Urieta.ui.util

/**
 * Helpers para formatear y parsear montos con separador de miles "punto".
 *
 * Ej: "1000" -> "1.000", "100000" -> "100.000".
 *
 * Independiente del locale del sistema (siempre usa punto como separador
 * de miles para que en español argentino se vea natural).
 */
object Formatters {

    /** Toma sólo dígitos de la entrada y los formatea con punto cada 3 cifras. */
    fun formatearMiles(input: String): String {
        val digitos = input.filter { it.isDigit() }
        if (digitos.isEmpty()) return ""
        // Quitamos ceros a la izquierda (ej: "00100" -> "100")
        val sinCerosIzq = digitos.trimStart('0').ifEmpty { "0" }
        val sb = StringBuilder()
        val len = sinCerosIzq.length
        for (i in 0 until len) {
            if (i > 0 && (len - i) % 3 == 0) sb.append('.')
            sb.append(sinCerosIzq[i])
        }
        return sb.toString()
    }

    /** Inverso: convierte "1.000" -> 1000.0. Devuelve null si no se puede parsear. */
    fun parsearMiles(formateado: String): Double? {
        return formateado.replace(".", "").toDoubleOrNull()
    }

    /**
     * Formatea un Double como moneda para mostrar al usuario.
     * Ej: 12750.0 -> "$ 12.750"
     * Internamente reusa [formatearMiles] para garantizar el mismo separador.
     */
    fun formatearMoneda(monto: Double): String {
        val entero = monto.toLong().toString()
        return "$ ${formatearMiles(entero)}"
    }
}
