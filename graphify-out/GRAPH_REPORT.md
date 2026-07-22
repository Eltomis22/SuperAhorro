# Graph Report - Trabajo Integrador  (2026-07-21)

## Corpus Check
- 61 files · ~16,891 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 353 nodes · 442 edges · 31 communities (26 shown, 5 thin omitted)
- Extraction: 89% EXTRACTED · 11% INFERRED · 0% AMBIGUOUS · INFERRED: 49 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `b65cc310`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- SuperAhorroRepository
- SuperAhorroNavGraph
- HomeScreen
- Screen
- EstadisticasScreen
- Producto
- SettingsRepository
- DetalleCompraViewModel
- Formatters
- LoginViewModel
- SplashViewModel
- NuevaCompraViewModel
- SuperAhorro
- .onCreate
- ListadoComprasViewModel
- UserDao
- RegisterViewModel
- HistorialComprasViewModel
- NuevoProductoViewModel
- ChatViewModel
- .sincronizarCompra
- SuperAhorroApp
- ColorUtils
- gradlew
- ConfirmDialog
- AppSettings.kt

## God Nodes (most connected - your core abstractions)
1. `SuperAhorroRepository` - 20 edges
2. `SuperAhorroNavGraph()` - 20 edges
3. `Screen` - 19 edges
4. `Compra` - 17 edges
5. `MiPerfilViewModel` - 14 edges
6. `SuperTopAppBar()` - 13 edges
7. `HomeScreen()` - 13 edges
8. `SettingsRepository` - 12 edges
9. `CompraDao` - 10 edges
10. `Producto` - 9 edges

## Surprising Connections (you probably didn't know these)
- `create()` --calls--> `SettingsRepository`  [INFERRED]
  app/src/main/java/com/undef/superahorro/Loza/Urieta/ui/screens/auth/LoginViewModel.kt → app/src/main/java/com/undef/superahorro/Loza/Urieta/data/SettingsRepository.kt
- `create()` --calls--> `SettingsRepository`  [INFERRED]
  app/src/main/java/com/undef/superahorro/Loza/Urieta/ui/screens/auth/RegisterViewModel.kt → app/src/main/java/com/undef/superahorro/Loza/Urieta/data/SettingsRepository.kt
- `create()` --calls--> `SettingsRepository`  [INFERRED]
  app/src/main/java/com/undef/superahorro/Loza/Urieta/ui/screens/auth/SplashViewModel.kt → app/src/main/java/com/undef/superahorro/Loza/Urieta/data/SettingsRepository.kt
- `create()` --calls--> `SettingsRepository`  [INFERRED]
  app/src/main/java/com/undef/superahorro/Loza/Urieta/ui/screens/miperfil/MiPerfilViewModel.kt → app/src/main/java/com/undef/superahorro/Loza/Urieta/data/SettingsRepository.kt
- `SuperAhorroNavGraph()` --calls--> `LoginScreen()`  [INFERRED]
  app/src/main/java/com/undef/superahorro/Loza/Urieta/navigation/NavGraph.kt → app/src/main/java/com/undef/superahorro/Loza/Urieta/ui/screens/auth/LoginScreen.kt

## Import Cycles
- None detected.

## Communities (31 total, 5 thin omitted)

### Community 0 - "SuperAhorroRepository"
Cohesion: 0.06
Nodes (9): CompraDao, Flow, MockData, ChatMessage, Compra, CompraConProductos, User, Flow (+1 more)

### Community 1 - "SuperAhorroNavGraph"
Cohesion: 0.06
Nodes (26): NavHostController, SuperAhorroNavGraph(), SuperTopAppBar(), ForgotPasswordScreen(), RegisterScreen(), ChatBubble(), ChatScreen(), CambiarClaveScreen() (+18 more)

### Community 2 - "HomeScreen"
Cohesion: 0.13
Nodes (19): BottomItem, NavHostController, SuperAhorroBottomBar(), CompraResumenCard(), EmptyRecentState(), HomeScreen(), InfoMiniCard(), Color (+11 more)

### Community 3 - "Screen"
Cohesion: 0.09
Nodes (19): CambiarClave, CambiarEmail, ChatIA, DetalleCompra, EditarCompra, EditarPerfil, Estadisticas, ForgotPassword (+11 more)

### Community 4 - "EstadisticasScreen"
Cohesion: 0.14
Nodes (15): ChartCard(), EstadisticasScreen(), KPICard(), Color, Modifier, NavHostController, ModernBarChart(), ProgressRow() (+7 more)

### Community 5 - "Producto"
Cohesion: 0.14
Nodes (7): Flow, ProductoDao, getDatabase(), Context, SuperAhorroDatabase, Producto, RoomDatabase

### Community 6 - "SettingsRepository"
Cohesion: 0.13
Nodes (10): Flow, PreferencesKeys, SettingsRepository, create(), HomeUiState, HomeViewModel, CreationExtras, StateFlow (+2 more)

### Community 7 - "DetalleCompraViewModel"
Cohesion: 0.16
Nodes (10): DetalleCompraScreen(), NavHostController, ProductoItemCard(), DetalleCompraUiState, create(), DetalleCompraViewModel, CreationExtras, StateFlow (+2 more)

### Community 8 - "Formatters"
Cohesion: 0.22
Nodes (5): AnnotatedString, Formatters, ThousandsSeparatorTransformation, TransformedText, VisualTransformation

### Community 9 - "LoginViewModel"
Cohesion: 0.22
Nodes (8): LoginScreen(), create(), CreationExtras, StateFlow, T, ViewModel, LoginUiState, LoginViewModel

### Community 10 - "SplashViewModel"
Cohesion: 0.22
Nodes (8): SplashScreen(), create(), CreationExtras, StateFlow, T, ViewModel, SplashUiState, SplashViewModel

### Community 11 - "NuevaCompraViewModel"
Cohesion: 0.22
Nodes (7): create(), CreationExtras, StateFlow, T, ViewModel, NuevaCompraUiState, NuevaCompraViewModel

### Community 12 - "SuperAhorro"
Cohesion: 0.18
Nodes (10): 🏛️ Arquitectura y Flujo de Datos, 🚀 Características (2da Entrega), 📋 Cumplimiento de la Consigna (Bloques A-D), 🔧 Cómo correr el proyecto, 📁 Estructura del Proyecto, 📄 Licencia, Pasos, Requisitos (+2 more)

### Community 13 - ".onCreate"
Cohesion: 0.22
Nodes (7): Activity, MainActivity, findActivity(), Context, SuperAhorroTheme(), Bundle, ComponentActivity

### Community 14 - "ListadoComprasViewModel"
Cohesion: 0.22
Nodes (7): ListadoComprasUiState, create(), CreationExtras, StateFlow, T, ViewModel, ListadoComprasViewModel

### Community 16 - "RegisterViewModel"
Cohesion: 0.28
Nodes (7): create(), CreationExtras, StateFlow, T, ViewModel, RegisterUiState, RegisterViewModel

### Community 17 - "HistorialComprasViewModel"
Cohesion: 0.28
Nodes (7): create(), HistorialComprasUiState, HistorialComprasViewModel, CreationExtras, StateFlow, T, ViewModel

### Community 18 - "NuevoProductoViewModel"
Cohesion: 0.28
Nodes (7): create(), CreationExtras, StateFlow, T, ViewModel, NuevoProductoUiState, NuevoProductoViewModel

### Community 19 - "ChatViewModel"
Cohesion: 0.32
Nodes (7): ChatUiState, ChatViewModel, create(), CreationExtras, StateFlow, T, ViewModel

### Community 20 - ".sincronizarCompra"
Cohesion: 0.40
Nodes (3): ApiResponse, SuperAhorroApi, Response

### Community 23 - "gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

## Knowledge Gaps
- **26 isolated node(s):** `PreferencesKeys`, `Splash`, `Login`, `Register`, `ForgotPassword` (+21 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **5 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `SuperAhorroNavGraph()` connect `SuperAhorroNavGraph` to `HomeScreen`, `EstadisticasScreen`, `DetalleCompraViewModel`, `LoginViewModel`, `SplashViewModel`, `.onCreate`?**
  _High betweenness centrality (0.303) - this node is a cross-community bridge._
- **Why does `Compra` connect `SuperAhorroRepository` to `HomeScreen`, `NuevaCompraViewModel`, `.sincronizarCompra`?**
  _High betweenness centrality (0.196) - this node is a cross-community bridge._
- **Why does `CompraResumenCard()` connect `HomeScreen` to `SuperAhorroRepository`?**
  _High betweenness centrality (0.148) - this node is a cross-community bridge._
- **Are the 18 inferred relationships involving `SuperAhorroNavGraph()` (e.g. with `.onCreate()` and `ForgotPasswordScreen()`) actually correct?**
  _`SuperAhorroNavGraph()` has 18 INFERRED edges - model-reasoned connections that need verification._
- **What connects `PreferencesKeys`, `Splash`, `Login` to the rest of the system?**
  _26 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `SuperAhorroRepository` be split into smaller, more focused modules?**
  _Cohesion score 0.058279370952821465 - nodes in this community are weakly interconnected._
- **Should `SuperAhorroNavGraph` be split into smaller, more focused modules?**
  _Cohesion score 0.06236786469344609 - nodes in this community are weakly interconnected._