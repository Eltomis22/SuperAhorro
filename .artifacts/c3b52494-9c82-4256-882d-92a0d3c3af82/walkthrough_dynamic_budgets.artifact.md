# Walkthrough - Presupuestos Personalizados (Banquero Dinámico)

He implementado la evolución del sistema de presupuestos para que el Algoritmo del Banquero funcione con límites personalizados por cada usuario, facilitando además el acceso a esta configuración desde la pantalla de inicio.

## Cambios Realizados

### Backend (Node.js & Supabase)
- **Base de Datos:** Se actualizó la tabla `presupuestos` para incluir `usuario_email` y un índice único. Ahora los límites son privados para cada usuario.
- **Endpoints:**
    - `GET /api/v1/presupuestos`: Obtiene tus límites personales.
    - `POST /api/v1/presupuestos`: Guarda o actualiza tus topes mensuales.
- **Algoritmo del Banquero:** Se modificó la lógica para que use los límites dinámicos definidos por el usuario en lugar de valores fijos.

### App Android
- **Nueva Pantalla:** Se creó [PresupuestosScreen.kt](file:///C:/Users/Tomi%20Losa/AndroidStudioProjects/Trabajo%20Integrador/app/src/main/java/com/undef/superahorro/Loza/Urieta/ui/screens/budget/PresupuestosScreen.kt) donde el usuario puede editar sus topes para Comida, Servicios, Ocio y Otros.
- **Atajos en la Home:**
    - Se añadió un icono de billetera en la barra superior para acceso instantáneo.
    - Se hizo que la tarjeta de "Ahorro" sea interactiva (al tocarla te lleva a tus presupuestos).
- **Integración de API:** Se definieron los nuevos modelos y métodos en Retrofit para sincronizar esta configuración.

## Cómo verificar los cambios

### 1. Actualizar Base de Datos
Debes ejecutar el SQL de actualización en Supabase:
```sql
ALTER TABLE presupuestos ADD COLUMN usuario_email TEXT;
ALTER TABLE presupuestos ADD UNIQUE (usuario_email, categoria);
```

### 2. Definir tus límites
1. Abre la app y toca el icono de la billetera arriba a la derecha.
2. Ingresa un monto bajo para "Ocio" (ej: $500) y dale al icono de guardar (abajo a la derecha).

### 3. Probar el Simulador
1. Ve a "Nueva Compra" y elige la categoría "Ocio".
2. Ingresa un gasto superior a tu límite (ej: $1000).
3. Toca "Verificar si el gasto es seguro 🏦". El sistema ahora te advertirá basándose en tu propia configuración.

---

> [!TIP]
> **Dato de Calidad:** Si un usuario nunca configuró sus presupuestos, el servidor le asignará automáticamente unos valores base de $10.000 para que el sistema de seguridad nunca deje de funcionar.
