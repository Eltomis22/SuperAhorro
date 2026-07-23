# SuperAhorro

App Android profesional para el control inteligente de gastos y compras de supermercado. Permite la gestión completa de tickets, productos, estadísticas en tiempo real y sincronización total con la nube.

> **Trabajo Práctico Integrador — Tecnologías Móviles 2026 · UNDEF**
> Autores: **Loza · Urieta**

---

## 🚀 Características Finales

- **Autenticación Multi-Factor:** Login/Registro local en Room + **Autenticación Biométrica** (Huella digital).
- **Sincronización Cloud Real:** Sincronización automática de compras con un **Backend propio en Node.js** alojado en Render y base de datos **Supabase**.
- **IA Generativa (Gemini):** Chat inteligente integrado en el backend que analiza tus gastos reales para darte consejos financieros personalizados.
- **Seguridad Financiera:** Implementación del **Algoritmo del Banquero** (evasión de deadlocks) para verificar si un gasto es seguro basándose en tu presupuesto mensual.
- **Gestión de Archivos:** Captura de tickets con la cámara y gestión inteligente del almacenamiento (eliminación física de fotos al borrar compras).
- **Internacionalización:** App 100% bilingüe (Español / Inglés) detectando la configuración del sistema.
- **Modo Oscuro:** Soporte nativo para modo oscuro persistido en DataStore.

---

## 🛠️ Stack Técnico Completo

| Capa | Tecnología |
|---|---|
| **UI** | Jetpack Compose (Material 3) |
| **Arquitectura** | MVVM + Repository Pattern |
| **Persistencia Local** | **Room** (SQLite) + **Jetpack DataStore** |
| **Backend** | **Node.js** + **Express** (Alojado en Render) |
| **Base de Datos Cloud** | **Supabase** (PostgreSQL) |
| **IA** | **Google Gemini 1.5 Flash** (Backend Proxy) |
| **Seguridad** | **Biometric Library** + Algoritmo del Banquero |
| **Async** | Kotlin Coroutines & Flow |

---

## 🏛️ Arquitectura y Flujo de Datos

La aplicación implementa una arquitectura **MVVM** robusta:

1.  **View (Compose):** Interfaz reactiva e internacionalizada.
2.  **ViewModel:** Gestión de estado y lógica de UI.
3.  **Repository:** Mediador entre Room, DataStore y la API de Render.
4.  **Backend Proxy:** Protege la API Key de Gemini y centraliza la lógica pesada (IA y Algoritmos).

---

## 📋 Cumplimiento de la Consigna (Bloques A-D + Agregados)

| Bloque | Requisito | Implementación |
|---|---|---|
| **A** | **Sesión y Preferencias** | Login/Registro real. Biometría. DataStore para temas y sesión. |
| **B** | **Persistencia (Room)** | DB con 3 tablas vinculadas y borrado físico de archivos. |
| **C** | **Networking (Retrofit)** | Backend Node.js en Render. Sincronización completa (GET/POST/DELETE). |
| **D** | **Device Intents** | Cámara, Compartir Ticket, Ver Imagen en Galería. |
| **Extra** | **IA y Algoritmos** | Chat con Gemini IA y Simulador Seguro (Banquero). |

---

## 🔧 Cómo correr el proyecto

### 1. Backend (Opcional si usas el de Render)
El servidor reside en su propio repositorio: `SuperAhorro-Backend`.
Requiere `SUPABASE_URL`, `SUPABASE_ANON_KEY` y `GEMINI_API_KEY`.

### 2. Android App
1. Cloná el repo: `git clone https://github.com/Eltomis22/SuperAhorro.git`
2. Abrí el proyecto en Android Studio Ladybug.
3. Sincronizá Gradle y ejecutá (`Shift + F10`).
4. **Nota:** Para usar la biometría, actívala primero en la pantalla de Configuración.

---

## 📄 Licencia
Trabajo académico. Uso libre con atribución a los autores (Loza · Urieta).
UNDEF — Tecnologías Móviles 2026.
