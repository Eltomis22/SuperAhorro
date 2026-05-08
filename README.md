# SuperAhorro

App Android para llevar el control de tus compras de supermercado. Permite cargar tickets, ver el detalle de cada compra, compartirla, y revisar estadísticas mensuales.

> **Trabajo Práctico Integrador — Tecnologías Móviles 2026 · UNDEF**
> Autores: **Loza · Urieta**

---

## Tabla de contenidos

- [Características](#características)
- [Stack técnico](#stack-técnico)
- [Capturas y mockups](#capturas-y-mockups)
- [Cómo correr el proyecto](#cómo-correr-el-proyecto)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Arquitectura](#arquitectura)
- [Modelo de datos](#modelo-de-datos)
- [Cumplimiento de la consigna](#cumplimiento-de-la-consigna)
- [Roadmap (2da entrega)](#roadmap-2da-entrega)

---

## Características

- **Login / Registro / Recuperar contraseña** (mockeado, sin backend en esta entrega).
- **Home** con resumen de gasto del mes y últimas compras.
- **Listado e Historial** de compras con filtros (Todos / Este mes / Mes anterior).
- **Detalle de compra** con productos, total, ticket y acciones de compartir, editar y eliminar.
- **CRUD completo de compras y productos** con confirmación al borrar.
- **Estadísticas** con KPIs, evolución mensual y gasto por supermercado.
- **Configuración**: modo oscuro, idioma (ES / EN), notificaciones y huella digital — todos cambian en runtime sin reiniciar la app.
- **Compartir compra** vía `Intent.ACTION_SEND` (chooser nativo de Android).

---

## Stack técnico

| Capa | Tecnología |
|---|---|
| Lenguaje | Kotlin 2.2 |
| UI | Jetpack Compose (BOM 2024.09) |
| Diseño | Material 3 |
| Navegación | Navigation Compose 2.8 |
| Async | Kotlin Coroutines 1.9 |
| Lifecycle | androidx.lifecycle 2.10 |
| Build | Gradle 9.3 (Kotlin DSL) · AGP 9.1 |
| SDK | `compileSdk 36` · `minSdk 30` · `targetSdk 36` |
| JVM | Java 11 |

---

## Capturas y mockups

En la raíz del repo dejamos dos archivos de presentación con mockups visuales de cada pantalla:

- [`SuperAhorro_Presentacion.html`](SuperAhorro_Presentacion.html) — slides interactivos (abrir en cualquier navegador).
- [`SuperAhorro_Guion_Presentacion.md`](SuperAhorro_Guion_Presentacion.md) — guión paso a paso para defender el TPI.

---

## Cómo correr el proyecto

### Requisitos

- **Android Studio** Ladybug o superior
- **JDK 11** (Android Studio lo trae integrado)
- Un emulador o dispositivo físico con **Android 11 (API 30)** o más nuevo

### Pasos

1. Cloná el repo:
   ```bash
   git clone https://github.com/Eltomis22/SuperAhorro.git
   cd SuperAhorro
   ```

2. Abrí el proyecto desde Android Studio (`File → Open` y seleccioná la carpeta).

3. Esperá a que sincronice Gradle (la primera vez baja todas las dependencias).

4. Levantá un emulador o conectá un dispositivo, y dale a **Run** ▶ (o `Shift + F10`).

> **Nota:** el archivo `local.properties` se genera solo al abrir el proyecto. No está versionado.

---

## Estructura del proyecto

```
app/
└── src/main/
    ├── AndroidManifest.xml
    ├── java/com/undef/superahorro/Loza/Urieta/
    │   ├── MainActivity.kt           ← única Activity de la app
    │   ├── data/
    │   │   ├── MockData.kt           ← "BD en memoria" reactiva
    │   │   └── model/Models.kt       ← User, Compra, Producto
    │   ├── navigation/
    │   │   ├── NavGraph.kt           ← grafo de Navigation Compose
    │   │   └── Screen.kt             ← rutas tipadas
    │   └── ui/
    │       ├── AppSettings.kt        ← state global (dark mode / idioma)
    │       ├── components/           ← BottomNavBar, ConfirmDialog, SuperTopAppBar
    │       ├── screens/              ← 13 pantallas (Splash, Login, Home, …)
    │       ├── theme/                ← Color, Theme, Type
    │       └── util/Formatters.kt
    └── res/
        ├── values/strings.xml        ← textos en español
        ├── values-en/strings.xml     ← textos en inglés
        ├── drawable/, mipmap-*/, xml/
```

### Pantallas (13)

| # | Pantalla | Ruta |
|---|---|---|
| 1 | Splash | `splash` |
| 2 | Login | `login` |
| 3 | Register | `register` |
| 4 | Forgot password | `forgot_password` |
| 5 | Home | `home` |
| 6 | Listado de compras | `listado_compras` |
| 7 | Historial | `historial_compras` |
| 8 | Estadísticas | `estadisticas` |
| 9 | Mi perfil | `mi_perfil` |
| 10 | Configuración | `settings` |
| 11 | Nueva compra / Editar compra | `nueva_compra` · `editar_compra/{compraId}` |
| 12 | Nuevo producto | `nuevo_producto/{compraId}` |
| 13 | Detalle de compra | `detalle_compra/{compraId}` |

---

## Arquitectura

**Single Activity** con todo el contenido como composables dentro de un grafo de navegación.

```
MainActivity (ComponentActivity)
   └─ setContent
        └─ SuperAhorroTheme(darkTheme = AppSettings.darkMode)
             └─ CompositionLocalProvider
                  ├─ LocalConfiguration (locale dinámico)
                  └─ LocalContext (createConfigurationContext)
                       └─ SuperAhorroNavGraph
                            ├─ Splash → Login → Home → …
                            └─ CRUD compras (Nueva / Detalle / Editar / Producto)
```

### Decisiones clave

- **Las pantallas no conocen al `NavController` directamente.** Exponen callbacks (`onBack`, `onLoginSuccess`, etc.) y el grafo decide a dónde navegar. Esto las hace más fáciles de testear.
- **`popUpTo(...)` para limpiar la pila** en transiciones donde no queremos que el botón "atrás" vuelva — ej. logout limpia toda la pila con `popUpTo(0) { inclusive = true }`.
- **`navArgument`** con `NavType.IntType` para pasar IDs entre pantallas de forma tipada.
- **Estado global reactivo** vía `MockData` (singleton con `mutableStateListOf`) — Compose recompone solo cuando muta la lista.

---

## Modelo de datos

Tres `data class` simples, diseñadas para migrar a Room en la 2da entrega sin tocar el resto del código:

```kotlin
data class User(
    val id: Int,
    val nombre: String,
    val email: String,
    val avatarUrl: String? = null
)

data class Compra(
    val id: Int,
    val fecha: String,            // yyyy-MM-dd
    val hora: String,             // HH:mm
    val supermercado: String,
    val total: Double,
    val ticketImagenUri: String? = null,
    val productos: List<Producto> = emptyList()
)

data class Producto(
    val id: Int,
    val codigo: String,
    val nombre: String,
    val descripcion: String,
    val cantidad: Int,
    val precio: Double
) {
    /** Computed: cantidad × precio. */
    val subtotal: Double get() = cantidad * precio
}
```

> El **total de la compra** representa lo que pagó el usuario en el ticket y **no** se calcula a partir de los productos — pueden no coincidir por descuentos o productos no detallados.

---

## Cumplimiento de la consigna

| Requisito | Dónde se ve |
|---|---|
| Mínimo 5 pantallas | 13 pantallas implementadas |
| Navegación entre pantallas | `navigation/NavGraph.kt` con Navigation Compose |
| Formularios con validación | Login, Register, Nueva compra, Nuevo producto |
| Persistencia (1ra entrega) | `MockData` reactivo (Room queda para la 2da entrega) |
| **Uso de un Intent del sistema** | Botón **Compartir** en `DetalleCompraScreen` (`Intent.ACTION_SEND`) |
| Internacionalización | `values/strings.xml` (es) + `values-en/strings.xml`, conmutable en runtime |
| Modo oscuro | Theme reactivo en `SuperAhorroTheme` controlado por `AppSettings.darkMode` |
| Material Design | Material 3 en toda la app |
| Componentes reusables | `BottomNavBar`, `SuperTopAppBar`, `ConfirmDialog` |

### Detalle del Intent (compartir compra)

```kotlin
val shareIntent = Intent(Intent.ACTION_SEND).apply {
    type = "text/plain"
    putExtra(Intent.EXTRA_TEXT, "Compra en $supermercado el $fecha — Total: $$total")
}
val chooser = Intent.createChooser(shareIntent, "Compartir compra").apply {
    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
}
context.startActivity(chooser)
```

> El flag `FLAG_ACTIVITY_NEW_TASK` es necesario porque `LocalContext` está sobrescrito por un `createConfigurationContext` (para el cambio de idioma en runtime), y ese contexto envuelto no es una `Activity`.

---

## Roadmap (2da entrega)

- [ ] **Persistencia con Room** — mover `MockData` a un Repository con DAOs. La API pública (`agregarCompra`, etc.) queda igual; las pantallas no se tocan.
- [ ] **ViewModels con StateFlow** — sacar la lógica de las pantallas al ViewModel.
- [ ] **Autenticación real** con backend (Retrofit + JWT).
- [ ] **Cámara y OCR** con ML Kit para escanear el ticket y autocompletar productos.
- [ ] **Notificaciones** push de presupuesto.
- [ ] **Tests** — Compose UI tests + unit tests del Repository.

---

## Licencia

Trabajo académico. Uso libre con atribución a los autores.
