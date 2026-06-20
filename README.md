# Firebase Cloud Manager — Unidad 4

**Módulo:** Fundamentos de la Tecnología Cloud  
**Institución:** Politécnico Grancolombiano  
**Autor:** Alejandro Mendoza ([@AlejoTechEngineer](https://github.com/AlejoTechEngineer))

---

## Descripción

Aplicación de escritorio desarrollada en **Java 17 + JavaFX 21** que permite gestionar colecciones en **Firebase Firestore** con operaciones CRUD completas e interfaz gráfica moderna con tema oscuro.

## Tecnologías

| Tecnología | Versión |
|------------|---------|
| Java | 17 |
| JavaFX | 21 |
| Firebase Admin SDK | 9.x |
| Maven | 3.8+ |
| Firebase Firestore | Cloud |

## Funcionalidades

| Operación | Descripción |
|-----------|-------------|
| **Insertar** | Carga registros individualmente desde el formulario o masivamente desde CSV |
| **Consultar** | Lista todos los registros o filtra por cualquier campo |
| **Editar** | Selecciona un registro de la tabla y modifica sus valores |
| **Eliminar** | Borra un registro con confirmación previa |

## Colecciones soportadas

- `students` — Estudiantes (nombre, email, edad, carrera)
- `empleados` — Empleados (nombre, cargo, salario, departamento)
- `productos` — Productos (nombre, precio, stock, categoría)

## Requisitos previos

- Java 17 o superior
- Maven 3.8+
- Proyecto Firebase activo con Firestore habilitado en modo producción o prueba

## Configuración Firebase

1. Ir a **Firebase Console → Configuración del proyecto → Cuentas de servicio**
2. Generar nueva clave privada → descargar `serviceAccountKey.json`
3. **Colocar el archivo `serviceAccountKey.json` en la raíz del proyecto** (misma carpeta que `pom.xml`)

> ⚠️ **Este archivo NO se sube al repositorio.** Está incluido en `.gitignore`.

## Ejecución

```bash
# Instalar dependencias y ejecutar directamente
mvn javafx:run

# O compilar fat-JAR y ejecutar
mvn package
java -jar target/firebase-cloud-manager-1.0.0.jar
```

## Importación de datos CSV

Los archivos de prueba están en la carpeta `data/`:

| Archivo | Colección |
|---------|-----------|
| `data/estudiantes.csv` | `students` |
| `data/empleados.csv` | `empleados` |
| `data/productos.csv` | `productos` |

El sistema detecta automáticamente el tipo de colección según las cabeceras del CSV.

## Estructura del proyecto

```
u4-firebase-java/
├── src/
│   └── main/
│       ├── java/com/polipoli/firebaseapp/
│       │   ├── MainApp.java            ← Punto de entrada JavaFX
│       │   ├── MainController.java     ← Controlador CRUD + UI
│       │   ├── FirebaseService.java    ← Operaciones Firestore
│       │   └── CSVReader.java          ← Lectura y detección de CSV
│       └── resources/
│           ├── fxml/MainView.fxml      ← Layout de la interfaz
│           └── css/styles.css          ← Tema oscuro
├── data/                               ← CSVs de prueba
├── pom.xml
├── .gitignore
└── serviceAccountKey.json              ← (NO incluido en Git)
```

## ⚠️ Seguridad

**Nunca subir `serviceAccountKey.json` a repositorios públicos.** El archivo contiene credenciales privadas de Firebase con acceso completo al proyecto.

El `.gitignore` ya lo excluye:

```
serviceAccountKey.json
```
