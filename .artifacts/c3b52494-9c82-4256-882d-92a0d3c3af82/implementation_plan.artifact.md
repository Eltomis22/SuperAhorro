# Plan de Implementación - Sincronización de Usuarios y Multiusuario (Cloud Auth)

Este plan detalla la migración hacia una arquitectura multiusuario real, donde los datos de perfil y compras están vinculados a una cuenta en la nube (Supabase). Esto permite que varios usuarios usen la misma app sin mezclar sus gastos.

## User Review Required

> [!IMPORTANT]
> **Privacidad de Datos:** A partir de este cambio, cada compra enviada al servidor incluirá el `email` del usuario logueado. En Supabase, solo se podrán ver las compras asociadas a ese email.
>
> **Migración de Datos:** Las compras que ya están en Supabase quedarán como "huérfanas" (sin email) a menos que las asignemos manualmente a tu usuario de prueba.

## Proposed Changes

### [Component Name] Backend (Node.js)

#### [MODIFY] [database.sql](file:///C:/Users/Tomi Losa/AndroidStudioProjects/SuperAhorro-Backend/database.sql)
- Crear tabla `usuarios_cloud` (id, nombre, email, clave).
- Modificar tabla `compras` añadiendo la columna `usuario_email` (TEXT).

#### [MODIFY] [server.js](file:///C:/Users/Tomi Losa/AndroidStudioProjects/SuperAhorro-Backend/server.js)
- Implementar endpoint `POST /api/v1/usuarios/registrar`: Guarda el usuario en Supabase tras validarlo.
- Implementar endpoint `POST /api/v1/usuarios/login`: Valida las credenciales contra la base de datos cloud.
- Actualizar `POST /api/v1/compras`: Ahora procesará el campo `usuario_email` enviado desde la app.
- Actualizar `POST /api/v1/budget/check`: El algoritmo ahora solo sumará los gastos del usuario que hace la consulta.

### [Component Name] Android App

#### [MODIFY] [SuperAhorroApi.kt](file:///C:/Users/Tomi Losa/AndroidStudioProjects/Trabajo%20Integrador/app/src/main/java/com/undef/superahorro/Loza/Urieta/data/remote/SuperAhorroApi.kt)
- Añadir modelos `AuthRequest` y `AuthResponse`.
- Definir endpoints `registrarUsuarioCloud` y `loginUsuarioCloud`.

#### [MODIFY] [Models.kt](file:///C:/Users/Tomi Losa/AndroidStudioProjects/Trabajo%20Integrador/app/src/main/java/com/undef/superahorro/Loza/Urieta/data/model/Models.kt)
- Añadir campo `usuarioEmail: String` a la data class `Compra`.

#### [MODIFY] [SuperAhorroRepository.kt](file:///C:/Users/Tomi Losa/AndroidStudioProjects/Trabajo%20Integrador/app/src/main/java/com/undef/superahorro/Loza/Urieta/data/SuperAhorroRepository.kt)
- Sincronizar el registro local con el registro cloud.
- Al agregar compras, obtener el email activo desde el `SettingsRepository` para incluirlo en el envío.

## Verification Plan

### Automated Tests
- Scripts `curl` para registrar un usuario y luego verificar que sus compras se guardan con su email.

### Manual Verification
1. Crear una cuenta nueva desde la app.
2. Verificar en el panel de Supabase que el usuario aparece en la tabla `usuarios_cloud`.
3. Cargar un gasto de $500.
4. Verificar que en la tabla `compras` de Supabase, la nueva fila tiene tu email.
