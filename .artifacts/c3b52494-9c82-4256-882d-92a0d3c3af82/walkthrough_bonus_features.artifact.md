# Walkthrough - Funciones Premium y Opcionales

He completado la implementación de las funciones opcionales para llevar la aplicación al 100% de los requisitos técnicos solicitados, añadiendo herramientas de análisis y gestión avanzadas.

## Funciones Implementadas

### 1. Filtros Avanzados y Buscador
En la pantalla de **Historial**, ahora cuentas con:
- **Buscador de Texto:** Filtra instantáneamente por nombre de supermercado o categoría.
- **Filtro de Precio Mínimo:** Permite ocultar gastos pequeños para enfocarte en los más grandes.
- **Chips de Tiempo y Supermercado:** Combinables con las búsquedas anteriores.

### 2. Exportación de Datos (CSV)
- Se añadió un icono de descarga en el Historial.
- Al tocarlo, la app genera un archivo **.csv** compatible con Excel que incluye: Fecha, Hora, Supermercado, Total y Categoría.
- Utiliza el sistema de "Compartir" para enviarlo por email o guardarlo en la nube.

### 3. Comparativa de Precios Real
- **Nueva Pantalla:** Accedé desde "Comparativa" en la Home.
- **Lógica:** La app analiza todos tus productos registrados y te dice en qué supermercado conseguiste el precio más bajo para cada uno.
- **Ranking:** Muestra una lista comparativa con iconos verdes para el lugar más barato detectado.

### 4. Notificaciones de Presupuesto
- El sistema ahora genera **notificaciones reales** en la barra de Android.
- Si intentas verificar un gasto y el Algoritmo del Banquero detecta que es "Inseguro", recibirás un aviso emergente inmediato.

## Verificación de los cambios

1. **Prueba de Exportación:** Ve al Historial, pulsa el icono de descarga y elige "Drive" o "Gmail" para ver el archivo generado.
2. **Prueba de Filtros:** Escribe "Ocio" en el buscador del Historial y coloca un precio mínimo de "1000".
3. **Prueba de Comparativa:** Carga dos compras con el producto "Leche" en diferentes supermercados y precios. Entra a la pantalla de Comparativa para ver el análisis.

---

> [!NOTE]
> **Permisos:** La primera vez que abras la app, Android podría pedirte permiso para mostrar notificaciones. Debes aceptarlo para que las alertas de presupuesto funcionen.
