# Walkthrough - Backend Sync para SuperAhorro

He completado la creación de la estructura del backend y la configuración necesaria en la aplicación Android para que puedas sincronizar tus compras en la nube usando **Node.js, Express, Supabase y Render**.

## Cambios Realizados

### Backend (Carpeta `backend/`)
- **[package.json](file:///C:/Users/Tomi Losa/AndroidStudioProjects/Trabajo%20Integrador/backend/package.json):** Configuración del proyecto con las librerías necesarias (`express`, `@supabase/supabase-js`, `dotenv`, `cors`).
- **[server.js](file:///C:/Users/Tomi Losa/AndroidStudioProjects/Trabajo%20Integrador/backend/server.js):** El servidor principal. Maneja la conexión con Supabase y expone los endpoints `GET /supermercados`, `POST /compras` y `POST /budget/check`.
- **Chat IA Centralizado:** Procesa los mensajes usando Gemini 1.5 Flash con contexto de gastos.
- **Simulador de Gasto Seguro (Banquero):** Implementación del Algoritmo del Banquero de Sistemas Operativos. Evalúa si un gasto deja al usuario en un "estado inseguro" respecto a sus presupuestos mensuales.
- **[database.sql](file:///C:/Users/Tomi Losa/AndroidStudioProjects/Trabajo%20Integrador/backend/database.sql):** Script actualizado con las tablas `presupuestos` y la columna `categoria` en `compras`.

### App Android
- **[SuperAhorroApi.kt](file:///C:/Users/Tomi Losa/AndroidStudioProjects/Trabajo%20Integrador/app/src/main/java/com/undef/superahorro/Loza/Urieta/data/remote/SuperAhorroApi.kt):** Modelos y endpoints para Chat y Verificación de Presupuesto.
- **[NuevaCompraScreen.kt](file:///C:/Users/Tomi Losa/AndroidStudioProjects/Trabajo%20Integrador/app/src/main/java/com/undef/superahorro/Loza/Urieta/ui/screens/purchases/NuevaCompraScreen.kt):** Ahora incluye un selector de categoría y el botón para verificar si el gasto es "seguro" mediante el algoritmo del banquero.
- **[ChatViewModel.kt](file:///C:/Users/Tomi Losa/AndroidStudioProjects/Trabajo%20Integrador/app/src/main/java/com/undef/superahorro/Loza/Urieta/ui/screens/chat/ChatViewModel.kt):** Delegación completa del procesamiento de IA al backend.

## Próximos Pasos

### 1. Configurar Supabase
1. Ve a [Supabase](https://supabase.com/) y crea un nuevo proyecto.
2. Abre el **SQL Editor** y pega el contenido de [database.sql](file:///C:/Users/Tomi Losa/AndroidStudioProjects/Trabajo%20Integrador/backend/database.sql). Ejecútalo.
3. Ve a **Project Settings > API** y copia la `URL` y la `anon public key`.
4. Obtén una API Key de Gemini en [Google AI Studio](https://aistudio.google.com/).

### 2. Probar Localmente (Opcional)
1. Crea un archivo `.env` dentro de la carpeta `backend/` usando como base el `.env.example`.
2. Ejecuta `npm install` y luego `npm start` dentro de la carpeta `backend/`.
3. En la app Android, en [SuperAhorroApi.kt](file:///C:/Users/Tomi Losa/AndroidStudioProjects/Trabajo%20Integrador/app/src/main/java/com/undef/superahorro/Loza/Urieta/data/remote/SuperAhorroApi.kt), descomenta la URL de localhost (`10.0.2.2`).

### 3. Desplegar en Render
1. Sube la carpeta `backend` a un repositorio de GitHub (o configura Render para que use una subcarpeta).
2. En [Render](https://render.com/), crea un nuevo **Web Service**.
3. En la sección **Environment**, agrega las variables `SUPABASE_URL`, `SUPABASE_ANON_KEY` y `GEMINI_API_KEY`.
4. Una vez desplegado, copia la URL que te da Render y pégala en `SuperAhorroApi.kt`.

> [!TIP]
> Recuerda que al usar Render (plan gratuito), el servidor "se duerme" después de 15 minutos de inactividad. La primera petición de la app puede tardar unos segundos en responder mientras el servidor despierta.
