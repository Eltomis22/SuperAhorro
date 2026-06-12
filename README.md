# SuperAhorro

App Android profesional para el control inteligente de gastos y compras de supermercado. Permite la gestión completa de tickets, productos, estadísticas en tiempo real y sincronización con la nube.

> **Trabajo Práctico Integrador — Tecnologías Móviles 2026 · UNDEF**
> Autores: **Loza · Urieta**

---

## 🚀 Características (2da Entrega)

- **Autenticación Real:** Sistema de Login y Registro con persistencia en base de datos local.
- **Gestión de Compras (CRUD):** Registro completo de compras y productos vinculados, con almacenamiento persistente.
- **Dashboard Premium:** Inicio dinámico con widgets de "Súper Favorito", "Ahorro Estimado" y consejos inteligentes.
- **Estadísticas Dinámicas:** Gráficos de barras animados y evolución de gastos calculados en tiempo real desde la DB.
- **Cámara e Intents:** Captura de tickets físicos mediante la cámara del sistema con gestión de permisos y visualización externa.
- **Internacionalización Nativa:** Detección automática del idioma según la configuración del dispositivo.
- **Diseño Moderno:** Interfaz de alta fidelidad con paleta "Bosque Esmeralda", bordes redondeados y micro-animaciones.

---

## 🛠️ Stack Técnico

| Capa | Tecnología |
|---|---|
| **UI** | Jetpack Compose (Material 3) |
| **Arquitectura** | MVVM + Repository Pattern |
| **Base de Datos** | **Room** (SQLite) con DAOs y migraciones |
| **Networking** | **Retrofit 2** + Gson (GET/POST) |
| **Persistencia Simple** | **Jetpack DataStore** (Sesión y Ajustes) |
| **Imágenes/Hardware** | **Intents** (Camera, Share, View) + FileProvider |
| **Async** | Kotlin Coroutines & Flow (Tiempo real) |
| **Dependency Injection** | Manual DI via Application class |

---

## 🏛️ Arquitectura y Flujo de Datos

La aplicación implementa una arquitectura **MVVM (Model-View-ViewModel)** robusta, garantizando la separación de responsabilidades:

1.  **View (Compose):** Pantallas reactivas que observan el estado del ViewModel.
2.  **ViewModel:** Gestiona el estado de la UI y la lógica de negocio usando `StateFlow`.
3.  **Repository:** Actúa como mediador y fuente única de verdad, coordinando:
    *   **Room:** Almacenamiento local para compras, productos y usuarios.
    *   **Retrofit:** Sincronización remota y obtención de datos externos.
    *   **DataStore:** Persistencia de estado de sesión y preferencias de la app.

---

## 📋 Cumplimiento de la Consigna (Bloques A-D)

| Bloque | Requisito | Implementación |
|---|---|---|
| **A** | **Sesión y Preferencias** | Login/Registro real persistido en Room. Estado de sesión y modo oscuro en **DataStore**. |
| **B** | **Persistencia (Room)** | Base de datos con 3 tablas vinculadas (`compras`, `productos`, `usuarios`). Uso de DAOs y `fallbackToDestructiveMigration`. |
| **C** | **Networking (Retrofit)** | **GET:** Lista de supermercados desde API. **POST:** Sincronización de compras cargadas al servidor. |
| **D** | **Device Intents** | **Cámara:** `TakePicture` para tickets. **Compartir:** `ACTION_SEND` para resúmenes. **Ver:** `ACTION_VIEW` para abrir fotos. |

---

## 🔧 Cómo correr el proyecto

### Requisitos
- **Android Studio** Ladybug o superior.
- Dispositivo físico o emulador con **Android 11 (API 30)** o superior.

### Pasos
1. Cloná el repo: `git clone https://github.com/Eltomis22/SuperAhorro.git`
2. Abrí el proyecto en Android Studio y sincronizá Gradle.
3. Ejecutá la app (`Shift + F10`).
4. **Nota:** Para probar la autenticación real, primero debés ir a "Registrarse" y crear una cuenta local.

---

## 📁 Estructura del Proyecto

```
app/src/main/java/com/undef/superahorro/Loza/Urieta/
├── data/
│   ├── local/          # Room DB, DAOs (CompraDao, UserDao)
│   ├── remote/         # Retrofit API interface
│   ├── model/          # Entidades y Modelos de dominio
│   └── SuperAhorroRepository.kt  # Fuente única de verdad
├── ui/
│   ├── screens/        # Pantallas organizadas por feature (Auth, Purchases, Stats, etc.)
│   ├── theme/          # Sistema de diseño, Colores Premium y Tipografía
│   └── util/           # Helpers (Formatters, ColorUtils, etc.)
└── navigation/         # Grafo de navegación y rutas tipadas
```

---

## 📄 Licencia
Trabajo académico. Uso libre con atribución a los autores (Loza · Urieta).
UNDEF — Tecnologías Móviles 2026.
