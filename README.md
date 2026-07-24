# SuperAhorro 🚀

App Android profesional para el control inteligente de gastos y compras de supermercado. Permite la gestión completa de tickets, productos, estadísticas en tiempo real y sincronización total con la nube.

> **Trabajo Práctico Integrador — Tecnologías Móviles 2026 · UNDEF**
> Autores: **Loza · Urieta**

---

## 🌟 Características Destacadas

- **Autenticación Multi-Factor:** Login/Registro real con **Autenticación Biométrica** protegida (gestión de dueño de huella para evitar accesos cruzados).
- **Sincronización Cloud Bidireccional:** Backup y restauración automática de compras y productos con un **Backend Node.js** (Render) y base de datos **Supabase**.
- **IA Generativa (Gemini):** Chat inteligente que analiza tus gastos reales para darte consejos financieros personalizados.
- **Seguridad Financiera:** Implementación del **Algoritmo del Banquero** para verificar si un gasto es seguro basándose en tus presupuestos mensuales personalizados.
- **Análisis Comparativo:** Nueva sección de **Comparativa de Precios** que analiza tu historial para decirte en qué supermercado conseguiste cada producto más barato.
- **Productividad:** **Exportación de Datos a CSV** compatible con Excel para llevar el control fuera del celular.
- **Filtros de Élite:** Buscador avanzado en el historial por texto (supermercado/categoría) y por rango de precios.
- **Notificaciones Reales:** Avisos al sistema Android cuando se detecta un gasto "Inseguro" por el simulador de presupuesto.
- **Gestión de Archivos:** Captura de tickets con la cámara y borrado físico de fotos al eliminar compras para optimizar espacio.
- **Internacionalización:** App 100% bilingüe (Español / Inglés).

---

## 🛠️ Stack Técnico Completo

| Capa | Tecnología |
|---|---|
| **UI** | Jetpack Compose (Material 3) |
| **Arquitectura** | MVVM + Repository Pattern |
| **Persistencia Local** | **Room** (Relacional) + **Jetpack DataStore** |
| **Backend** | **Node.js** + **Express** (Alojado en Render) |
| **Base de Datos Cloud** | **Supabase** (PostgreSQL) |
| **IA** | **Google Gemini 1.5 Flash** (Proxy Server) |
| **Seguridad** | **Biometric Library** + Algoritmo del Banquero |
| **Async** | Kotlin Coroutines & Flow |

---

## 🏛️ Arquitectura y Seguridad

La aplicación implementa una arquitectura **Offline-First**:
1.  **View (Compose):** Interfaz reactiva e internacionalizada.
2.  **Repository:** Única fuente de verdad que coordina la persistencia local (Room) y la sincronización con la API (Retrofit).
3.  **Gestión de Identidad:** La app rastrea al "Dueño de la huella", solicitando confirmación si un usuario distinto intenta usar la biometría del dispositivo.

---

## 📋 Cumplimiento de la Consigna (Bloques A-D + Bonus)

| Bloque | Requisito | Implementación |
|---|---|---|
| **A** | **Sesión y Preferencias** | Login/Registro real. Biometría inteligente. DataStore. |
| **B** | **Persistencia (Room)** | 3 Tablas vinculadas. Sincronización bidireccional Compras/Productos. |
| **C** | **Networking (Retrofit)** | API RESTful en Render. Sincronización Down/Up. |
| **D** | **Device Intents** | Cámara, Compartir (Social), Abrir Imagen, Notificaciones. |
| **Premium** | **Análisis y Datos** | **Exportación CSV**, **Comparativa de Precios**, Filtros Avanzados. |
| **Extra** | **IA y Algoritmos** | Chat con Gemini IA y Simulador Seguro (Banquero). |

---

## 🔧 Cómo correr el proyecto

### 1. Backend
Servidor activo en: `https://super-ahorro-backend.onrender.com/api/v1/`
Repositorio: `SuperAhorro-Backend`

### 2. Android App
1. Cloná el repo: `git clone https://github.com/Eltomis22/SuperAhorro.git`
2. Abrí el proyecto en Android Studio.
3. Sincronizá Gradle y ejecutá (`Shift + F10`).
4. **Nota:** Para usar la biometría, vincula tu cuenta primero en **Configuración > Autenticación biométrica**.

---

## 📄 Licencia
Trabajo académico. Uso libre con atribución a los autores (Loza · Urieta).
UNDEF — Tecnologías Móviles 2026.
