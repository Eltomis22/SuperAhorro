package com.undef.superahorro.Loza.Urieta.ui.util

import androidx.compose.ui.graphics.Color

object ColorUtils {
    
    // Paleta de colores profesionales para identificar comercios
    private val palette = listOf(
        Color(0xFF00796B), // Teal
        Color(0xFF1976D2), // Blue
        Color(0xFFD32F2F), // Red
        Color(0xFFF57C00), // Orange
        Color(0xFF7B1FA2), // Purple
        Color(0xFF388E3C), // Green
        Color(0xFF455A64), // Blue Grey
        Color(0xFFE91E63), // Pink
        Color(0xFF0097A7), // Cyan
        Color(0xFFAFB42B)  // Lime
    )

    /**
     * Genera un color consistente basado en un nombre.
     * El mismo nombre siempre devolverá el mismo color.
     */
    fun getColorForName(name: String): Color {
        if (name.isBlank()) return palette[0]
        
        // Usamos el hash del nombre para elegir un color de la lista
        val hash = name.lowercase().trim().hashCode()
        val index = Math.abs(hash) % palette.size
        
        return palette[index]
    }
}
