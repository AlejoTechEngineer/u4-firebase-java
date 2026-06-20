# Firebase Cloud Manager — Unidad 4

App de escritorio en **Java 17 + JavaFX 21** con conexión a **Firebase Firestore**, CRUD completo e importación masiva de datos desde archivos CSV.

---

## Contexto académico

| Campo | Detalle |
|-------|---------|
| **Módulo** | Fundamentos de la Tecnología Cloud |
| **Unidad** | 4 |
| **Programa** | Maestría en Arquitectura de Software |
| **Institución** | Politécnico Grancolombiano |
| **Autor** | Alejandro De Mendoza |

---

## Funcionalidades

- **Importación CSV** — carga masiva de registros con detección automática de la colección destino según las cabeceras del archivo
- **CRUD completo** — insertar, buscar, editar y eliminar documentos directamente sobre Firebase Firestore
- **Gestión de colecciones** — soporte para tres colecciones: `students`, `empleados` y `productos`
- **Interfaz gráfica JavaFX** — UI de escritorio con tema oscuro, tabla de resultados interactiva y formularios contextuales

---

## Stack tecnológico

| Tecnología | Versión |
|------------|---------|
| Java | 17 |
| JavaFX | 21 |
| Firebase Admin SDK | 9.2.0 |
| Maven | 3.8+ |
| Firebase Firestore | Cloud (modo producción/prueba) |

---

## Instalación y ejecución

### Requisitos previos

- [Java 17 JDK](https://adoptium.net/) o superior instalado y en el `PATH`
- [Apache Maven 3.8+](https://maven.apache.org/download.cgi) instalado y en el `PATH`
- [JavaFX SDK 21](https://gluonhq.com/products/javafx/) descargado y descomprimido localmente
- Proyecto activo en [Firebase Console](https://console.firebase.google.com/) con **Firestore** habilitado

### 1. Clonar el repositorio

```bash
git clone https://github.com/AlejoTechEngineer/u4-firebase-java.git
cd u4-firebase-java
```

### 2. Configurar credenciales de Firebase

1. En [Firebase Console](https://console.firebase.google.com/), abre tu proyecto
2. Ve a **Configuración del proyecto → Cuentas de servicio**
3. Haz clic en **Generar nueva clave privada** y descarga el archivo `.json`
4. Renómbralo a `serviceAccountKey.json` y colócalo en la **raíz del proyecto** (junto a `pom.xml`)

> **Este archivo NO se incluye en el repositorio.** Está excluido por `.gitignore`.

### 3. Compilar

```bash
mvn package
```

Genera el fat-JAR en `target/firebase-cloud-manager-1.0.0.jar` con todas las dependencias incluidas.

### 4. Ejecutar

**Con Maven (recomendado durante desarrollo):**

```bash
mvn javafx:run
```

**Con el JAR compilado (requiere JavaFX SDK en disco):**

```bash
java --module-path /ruta/a/javafx-sdk-21/lib \
     --add-modules javafx.controls,javafx.fxml \
     -jar target/firebase-cloud-manager-1.0.0.jar
```

> Reemplaza `/ruta/a/javafx-sdk-21/lib` con la ruta real donde descomprimiste el JavaFX SDK.

---

## Estructura del proyecto

```
u4-firebase-java/
├── data/
│   ├── empleados.csv          # CSV de prueba — colección empleados
│   ├── estudiantes.csv        # CSV de prueba — colección students
│   └── productos.csv          # CSV de prueba — colección productos
├── src/
│   └── main/
│       ├── java/com/polipoli/firebaseapp/
│       │   ├── MainApp.java            # Punto de entrada JavaFX
│       │   ├── MainController.java     # Controlador CRUD + lógica UI
│       │   ├── FirebaseService.java    # Operaciones Firestore
│       │   └── CSVReader.java          # Lectura y detección automática de CSV
│       └── resources/
│           ├── fxml/
│           │   └── MainView.fxml       # Layout de la interfaz
│           └── css/
│               └── styles.css          # Tema oscuro
├── pom.xml
├── .gitignore
└── serviceAccountKey.json     # ⚠️ NO incluido — debes agregarlo manualmente
```

---

## Formato de los CSV

El sistema detecta la colección destino según las cabeceras del archivo. Ejemplos:

**`estudiantes.csv`** → colección `students`
```
nombre,email,edad,carrera
Ana López,ana@email.com,22,Ingeniería de Sistemas
```

**`empleados.csv`** → colección `empleados`
```
nombre,cargo,salario,departamento
Carlos Ruiz,Desarrollador,4500000,Tecnología
```

**`productos.csv`** → colección `productos`
```
nombre,precio,stock,categoria
Laptop,3200000,15,Electrónica
```

---

## Seguridad

> **Nunca subas `serviceAccountKey.json` a un repositorio público.**

Este archivo contiene credenciales privadas con acceso completo a tu proyecto Firebase. El `.gitignore` ya lo excluye:

```
serviceAccountKey.json
```

Si accidentalmente lo subes, revoca la clave de inmediato desde **Firebase Console → Cuentas de servicio** y genera una nueva.
