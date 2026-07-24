package com.undef.superahorro.Loza.Urieta.ui.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.undef.superahorro.Loza.Urieta.data.model.Compra
import java.io.File
import java.io.FileOutputStream

object ExportHelper {

    fun exportarComprasCSV(context: Context, compras: List<Compra>) {
        val fileName = "reporte_compras_${System.currentTimeMillis()}.csv"
        val file = File(context.cacheDir, fileName)
        
        try {
            val out = FileOutputStream(file)
            // Header del CSV
            out.write("Fecha,Hora,Supermercado,Total,Categoria\n".toByteArray())
            
            compras.forEach { compra ->
                val line = "${compra.fecha},${compra.hora},${compra.supermercado},${compra.total},${compra.categoria}\n"
                out.write(line.toByteArray())
            }
            out.close()

            // Compartir el archivo
            val uri = FileProvider.getUriForFile(
                context,
                "com.undef.superahorro.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_SUBJECT, "Mis Compras - SuperAhorro")
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Enviar reporte de compras"))
            
        } catch (e: Exception) {
            android.widget.Toast.makeText(context, "Error al exportar: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
        }
    }
}
