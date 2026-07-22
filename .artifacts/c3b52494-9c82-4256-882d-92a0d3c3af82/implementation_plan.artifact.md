# Plan de Implementación - Simulador de Gasto Seguro (Algoritmo del Banquero)

Este plan describe la integración del **Algoritmo del Banquero** como una funcionalidad de "Seguridad Financiera". La app permitirá al usuario simular si un gasto planeado es "seguro" basándose en su presupuesto mensual y sus necesidades futuras estimadas.

## User Review Required

> [!IMPORTANT]
> **Cambio en el Modelo de Datos:** Para que el algoritmo funcione, necesitamos clasificar los gastos. Propongo añadir un campo `categoria` a la tabla `compras` (ej: Comida, Servicios, Ocio).
>
> **Concepto de "Seguridad":** El algoritmo no solo verifica si tienes dinero *hoy*, sino si gastar eso hoy te impedirá cubrir el presupuesto máximo de otras categorías esenciales en el futuro (evasión de "deadlock" financiero).

## Proposed Changes

### [Component Name] Backend (Node.js)

#### [MODIFY] [database.sql](file:///C:/Users/Tomi Losa/AndroidStudioProjects/Trabajo%20Integrador/backend/database.sql)
- Añadir tabla `presupuestos` (categoria, monto_maximo).
- Añadir columna `categoria` a la tabla `compras`.

#### [MODIFY] [server.js](file:///C:/Users/Tomi Losa/AndroidStudioProjects/Trabajo%20Integrador/backend/server.js)
- Implementar la función `isSafeState(available, max, allocation)`.
- Crear endpoint `POST /api/v1/budget/check`:
    - Recibe: `{ categoria, monto_solicitado }`.
    - Calcula el estado actual consultando Supabase.
    - Devuelve: `{ safe: boolean, message: string }`.

### [Component Name] Android App

#### [MODIFY] [Models.kt](file:///C:/Users/Tomi Losa/AndroidStudioProjects/Trabajo%20Integrador/app/src/main/java/com/undef/superahorro/Loza/Urieta/data/model/Models.kt)
- Añadir campo `categoria: String` a la entidad `Compra`.

#### [MODIFY] [SuperAhorroApi.kt](file:///C:/Users/Tomi Losa/AndroidStudioProjects/Trabajo%20Integrador/app/src/main/java/com/undef/superahorro/Loza/Urieta/data/remote/SuperAhorroApi.kt)
- Definir `BudgetCheckRequest` y `BudgetCheckResponse`.
- Añadir el endpoint `checkBudget`.

#### [MODIFY] [NuevaCompraScreen.kt](file:///C:/Users/Tomi Losa/AndroidStudioProjects/Trabajo%20Integrador/app/src/main/java/com/undef/superahorro/Loza/Urieta/ui/screens/purchases/NuevaCompraScreen.kt)
- Añadir un selector de categoría.
- Añadir un botón o aviso de "Verificar Gasto Seguro" que consulte al backend antes de guardar.

## Verification Plan

### Automated Tests
- Script de prueba en Node.js para validar que el algoritmo detecta estados inseguros (ej: cuando pides más dinero del que queda disponible para cubrir el mínimo de otras categorías).

### Manual Verification
1. Definir presupuestos máximos en Supabase (ej: Comida: 1000, Ocio: 500).
2. Simular un gasto de 400 en Ocio cuando solo quedan 1200 totales. El algoritmo debe validar si los 800 restantes cubren el máximo de Comida.
3. Verificar que la app muestre una alerta roja si el estado es "Inseguro".
