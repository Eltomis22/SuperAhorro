# Walkthrough - Sincronización de Usuarios y Multiusuario (Cloud Auth)

He implementado la arquitectura multiusuario real, permitiendo que cada usuario de la aplicación tenga su propia base de datos de compras en la nube sin interferir con otros.

## Mejoras Realizadas

### Backend (Node.js & Supabase)
- **Base de Datos:** Se creó la tabla `usuarios_cloud` y se vinculó la tabla `compras` mediante la columna `usuario_email`.
- **Nuevos Endpoints:**
    - `POST /api/v1/usuarios/registrar`: Registro remoto.
    - `POST /api/v1/usuarios/login`: Validación de credenciales en la nube.
- **Lógica de Sincronización:** El servidor ahora solo procesa y devuelve datos pertenecientes al email del usuario activo. El **Algoritmo del Banquero** ahora es personalizado por usuario.

### App Android
- **Arquitectura Híbrida:** El `SuperAhorroRepository` ahora coordina el almacenamiento local (Room) con la sincronización cloud. Si un usuario inicia sesión en un dispositivo nuevo, la app valida sus datos contra Supabase.
- **Multiusuario:** Cada objeto `Compra` enviado ahora incluye automáticamente el email del usuario logueado.
- **Inyección de Dependencias:** Se actualizó `SuperAhorroApp` para inyectar correctamente el `SettingsRepository` en el flujo de datos principal.

## Instrucciones para el usuario

### 1. Actualizar Supabase
Debes ejecutar las actualizaciones del archivo [database.sql](file:///C:/Users/Tomi%20Losa/AndroidStudioProjects/SuperAhorro-Backend/database.sql) en tu panel de Supabase para añadir la tabla de usuarios y la nueva columna en compras.

### 2. Reiniciar Servidor
Asegúrate de que Render despliegue el último commit para activar los nuevos endpoints de autenticación.

---

> [!TIP]
> **Prueba de Oro:** Registra un usuario "A" y carga una compra. Luego desinstala la app (o borra datos) e inicia sesión con el usuario "A". El sistema estará listo para ser extendido a una función de "Sincronización de Histórico" en el futuro.
