# Plan de Implementación - Auditoría Técnica y Refactorización Final

Este plan aborda las observaciones del profesor sobre la arquitectura (Room + Retrofit), la eliminación de textos hardcodeados y la fragmentación de archivos largos. Además, resuelve los problemas visuales y de entrada de datos (decimales) reportados.

## User Review Required

> [!IMPORTANT]
> **Arquitectura Single Source of Truth:** Migraremos la lista de supermercados para que use Room como caché. La UI ahora siempre leerá del repositorio, y este decidirá si refrescar desde la nube o usar lo que tiene en el teléfono.
>
> **Refactorización de Pantallas:** Dividiremos `NuevaCompraScreen.kt` y `HomeScreen.kt` en varios archivos más pequeños para cumplir con el límite de líneas sugerido por la cátedra.

## Proposed Changes

### [Component Name] Persistencia y Datos (Cumplimiento Bloque 1)

#### [NEW] [SupermercadoEntity.kt](file:///C:/Users/Tomi%20Losa/AndroidStudioProjects/Trabajo%20Integrador/app/src/main/java/com/undef/superahorro/Loza/Urieta/data/model/SupermercadoEntity.kt)
- Entidad para cachear la lista de supermercados sugeridos.

#### [NEW] [SupermercadoDao.kt](file:///C:/Users/Tomi%20Losa/AndroidStudioProjects/Trabajo%20Integrador/app/src/main/java/com/undef/superahorro/Loza/Urieta/data/local/SupermercadoDao.kt)
- Métodos para insertar y obtener supermercados de Room.

#### [MODIFY] [SuperAhorroRepository.kt](file:///C:/Users/Tomi%20Losa/AndroidStudioProjects/Trabajo%20Integrador/app/src/main/java/com/undef/superahorro/Loza/Urieta/data/SuperAhorroRepository.kt)
- Cambiar `obtenerSupermercados()`: Primero devuelve lo que hay en Room, luego descarga de la API en segundo plano y actualiza la DB local.

### [Component Name] Refactorización de Interfaz (Cumplimiento Bloque 3)

#### [NEW] [HomeComponents.kt](file:///C:/Users/Tomi%20Losa/AndroidStudioProjects/Trabajo%20Integrador/app/src/main/java/com/undef/superahorro/Loza/Urieta/ui/screens/home/HomeComponents.kt)
- Extraer `PremiumSpendingCard`, `ModernActionCard` e `InfoMiniCard` para reducir el tamaño de `HomeScreen.kt`.

#### [NEW] [NuevaCompraComponents.kt](file:///C:/Users/Tomi%20Losa/AndroidStudioProjects/Trabajo%20Integrador/app/src/main/java/com/undef/superahorro/Loza/Urieta/ui/screens/purchases/NuevaCompraComponents.kt)
- Extraer la lógica de la cámara y los campos de formulario para reducir `NuevaCompraScreen.kt` a menos de 200 líneas.

### [Component Name] Correcciones de UI/UX y I18n (Cumplimiento Bloque 2)

#### [MODIFY] [HomeScreen.kt](file:///C:/Users/Tomi%20Losa/AndroidStudioProjects/Trabajo%20Integrador/app/src/main/java/com/undef/superahorro/Loza/Urieta/ui/screens/home/HomeScreen.kt)
- **Fix Texto Cortado:** Ajustar el peso y tamaño de fuente en las tarjetas de acceso rápido.
- **Mensaje Dinámico:** Vincular el mensaje de la tarjeta principal al estado del presupuesto real del usuario.

#### [MODIFY] [Formatters.kt](file:///C:/Users/Tomi%20Losa/AndroidStudioProjects/Trabajo%20Integrador/app/src/main/java/com/undef/superahorro/Loza/Urieta/ui/util/Formatters.kt)
- Corregir la `ThousandsSeparatorTransformation` para que soporte la entrada de decimales con coma sin perder la posición del cursor.

## Verification Plan

### Automated Tests
- Ejecutar `clean assembleDebug` para asegurar que el procesador KSP (Room) acepte las nuevas entidades.

### Manual Verification
1.  **Modo Avión:** Abrir la app sin internet. La lista de supermercados debería aparecer igual (gracias al caché de Room).
2.  **Entrada de Datos:** Escribir "1.500,50" en una compra. Verificar que se guarde el valor con decimales.
3.  **Visual:** Comprobar que "Estadísticas" se lee perfectamente en la Home.
