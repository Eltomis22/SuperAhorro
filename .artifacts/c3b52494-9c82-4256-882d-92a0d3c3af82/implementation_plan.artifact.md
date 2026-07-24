# Plan de Implementación - Funciones Opcionales SuperAhorro

Este plan detalla la implementación de los requisitos opcionales restantes: Notificaciones, Exportación de Datos, Comparativa de Precios y Filtros Avanzados, sin tocar la lógica de IA.

## User Review Required

> [!IMPORTANT]
> **Permisos de Notificaciones:** En Android 13+, necesitaremos pedir el permiso `POST_NOTIFICATIONS`.
>
> **Almacenamiento:** Para la exportación a CSV, utilizaremos el almacenamiento interno de la app y un `FileProvider` para compartir el archivo (mail, WhatsApp, etc.), lo cual es más seguro y moderno que pedir permisos de escritura en disco.

## Proposed Changes

### [Component Name] Utilidades y Helpers

#### [NEW] [NotificationHelper.kt](file:///C:/Users/Tomi%20Losa/AndroidStudioProjects/Trabajo%20Integrador/app/src/main/java/com/undef/superahorro/Loza/Urieta/ui/util/NotificationHelper.kt)
- Clase para crear canales de notificación y lanzar avisos cuando se supera un presupuesto.

#### [NEW] [ExportHelper.kt](file:///C:/Users/Tomi%20Losa/AndroidStudioProjects/Trabajo%20Integrador/app/src/main/java/com/undef/superahorro/Loza/Urieta/ui/util/ExportHelper.kt)
- Lógica para convertir la lista de compras de Room a un archivo `.csv` y compartirlo mediante un `Intent`.

### [Component Name] Pantalla de Historial (Filtros y Exportación)

#### [MODIFY] [HistorialComprasScreen.kt](file:///C:/Users/Tomi%20Losa/AndroidStudioProjects/Trabajo%20Integrador/app/src/main/java/com/undef/superahorro/Loza/Urieta/ui/screens/purchases/HistorialComprasScreen.kt)
- Añadir filtros por Categoría (Comida, Ocio, etc.) y un buscador por rango de precio.
- Añadir un icono de "Descargar" en la barra superior para activar la exportación a CSV.

### [Component Name] Comparativa de Precios

#### [NEW] [ComparativaScreen.kt](file:///C:/Users/Tomi%20Losa/AndroidStudioProjects/Trabajo%20Integrador/app/src/main/java/com/undef/superahorro/Loza/Urieta/ui/screens/comparativa/ComparativaScreen.kt)
- Nueva pantalla que analiza los productos guardados y muestra un ranking de qué supermercado ofrece el mejor precio para cada producto frecuente.

### [Component Name] Repositorio y Lógica

#### [MODIFY] [SuperAhorroRepository.kt](file:///C:/Users/Tomi%20Losa/AndroidStudioProjects/Trabajo%20Integrador/app/src/main/java/com/undef/superahorro/Loza/Urieta/data/SuperAhorroRepository.kt)
- Añadir un método `obtenerRankingPrecios()` que agrupe productos por nombre y busque el precio mínimo por supermercado.

## Verification Plan

### Automated Tests
- No se requieren para esta fase, validación visual y funcional.

### Manual Verification
1.  **Filtros:** Entrar al historial y filtrar por "Comida". Verificar que solo salgan esas compras.
2.  **Exportación:** Tocar el botón de exportar y elegir "Gmail". Verificar que el CSV se adjunte correctamente.
3.  **Notificaciones:** Cargar una compra que supere el límite de "Ocio". Verificar que llegue el aviso al celular.
4.  **Comparativa:** Entrar a la nueva pantalla y ver si "Leche" sale con el precio de Coto y Carrefour comparados.
