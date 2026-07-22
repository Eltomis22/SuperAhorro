# Plan de Consolidación - Android & Backend (Revisión de Compañero)

Este plan detalla el proceso para fusionar los cambios subidos por tu compañero, analizando su calidad y asegurando que la versión final sea la más completa y funcional (incluyendo IA, Algoritmo del Banquero y Sincronización Total).

## User Review Required

> [!CAUTION]
> **Calidad del Código del Compañero:** Tras analizar los repositorios, he detectado que la versión subida por tu compañero es **incompleta** en comparación con la que construimos:
> 1. **Backend:** Su `server.js` solo tiene 1 endpoint (supermercados), faltándole toda la lógica de sincronización de compras, IA y el Algoritmo del Banquero.
> 2. **Android:** La interfaz de la API tiene nombres de campos en español que no coinciden con las mejores prácticas y carece de parámetros esenciales para el algoritmo de seguridad.
> 3. **Hardcoding:** Ha dejado credenciales de Supabase hardcodeadas en el código del servidor.

**Decisión Propuesta:** Mantener los commits de tu compañero en el historial (para que sume puntos), pero realizar una **"Consolidación Final"** que sobreescriba los archivos con nuestra versión optimizada y completa.

## Proposed Changes

### 1. Repositorio Android (Merge & Fix)
- Realizar un `merge` de la rama del compañero.
- Resolver conflictos priorizando **nuestra versión** (que incluye `@SerializedName`, categorías y validaciones completas).
- Crear un commit final de "Consolidación de Arquitectura".

### 2. Repositorio Backend (Restore functionality)
- Restaurar el archivo `server.js` completo (con IA, Banquero, CORS y variables de entorno).
- Restaurar el `package.json` con todas las dependencias.
- Mantener su repositorio de GitHub pero con el código funcional.

## Verification Plan

### Automated Tests
- Verificar que el servidor inicie correctamente con `npm start`.
- Probar los endpoints `GET /supermercados`, `POST /compras`, `POST /chat` y `POST /budget/check`.

### Manual Verification
1. Abrir la app Android.
2. Probar el flujo completo: Nueva Compra -> Sincronizar -> Chat IA -> Verificar Gasto Seguro.
3. Confirmar que los datos aparecen en Supabase.
