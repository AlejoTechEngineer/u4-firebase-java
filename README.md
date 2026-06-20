<div align="center">

# 🔥 Firebase Cloud Manager

### Desktop CRUD app — Java 17 · JavaFX 21 · Firebase Firestore

[![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://adoptium.net/)
[![JavaFX](https://img.shields.io/badge/JavaFX-21-3776AB?style=for-the-badge&logo=java&logoColor=white)](https://openjfx.io/)
[![Firebase](https://img.shields.io/badge/Firebase-Admin%209.2.0-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)](https://firebase.google.com/)
[![Maven](https://img.shields.io/badge/Maven-3.8+-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-Academic-blueviolet?style=for-the-badge)](.)

*Unidad 4 — Fundamentos de la Tecnología Cloud · Maestría en Arquitectura de Software · Politécnico Grancolombiano*

</div>

---

## ¿Qué es esto?

Aplicación de escritorio que conecta una UI JavaFX con **Google Firebase Firestore** en tiempo real. Permite gestionar múltiples colecciones NoSQL con operaciones CRUD completas desde una interfaz gráfica con tema oscuro, además de importación masiva de datos mediante archivos CSV con detección automática de esquema.

Construida como parte de la **Unidad 4** del módulo *Fundamentos de la Tecnología Cloud*, explorando la integración de servicios cloud gestionados (Firebase) con aplicaciones cliente nativas Java.

---

## Arquitectura

```
┌─────────────────────────────────────────────────────────┐
│                    JavaFX UI Layer                       │
│         MainView.fxml  ·  styles.css (dark theme)       │
└────────────────────────┬────────────────────────────────┘
                         │ eventos / bindings
┌────────────────────────▼────────────────────────────────┐
│                  Controller Layer                        │
│    MainController.java — CRUD logic · CSV dispatch      │
└──────────┬─────────────────────────┬────────────────────┘
           │                         │
┌──────────▼──────────┐  ┌──────────▼──────────────────┐
│   FirebaseService   │  │        CSVReader             │
│  Firestore CRUD ops │  │  schema detection · parsing  │
└──────────┬──────────┘  └─────────────────────────────┘
           │
┌──────────▼──────────────────────────────────────────────┐
│              Google Firebase Firestore                   │
│         collections: students · empleados · productos    │
└─────────────────────────────────────────────────────────┘
```

---

## Features

| Feature | Descripción |
|---------|-------------|
| **CRUD completo** | Crear, leer, actualizar y eliminar documentos en Firestore |
| **Importación CSV** | Carga masiva con detección automática de colección destino por cabeceras |
| **Multi-colección** | Gestión de `students`, `empleados` y `productos` desde la misma UI |
| **Búsqueda dinámica** | Filtro por cualquier campo en tiempo real sobre la tabla |
| **Dark UI** | Interfaz JavaFX con tema oscuro personalizado via CSS |
| **Fat JAR** | Distribución como ejecutable autónomo con todas las dependencias |

---

## Stack

```
Java 17 (LTS)
├── JavaFX 21              — UI framework (FXML + CSS)
├── Firebase Admin SDK 9.2.0
│   ├── google-cloud-firestore
│   └── firebase-admin (auth + init)
└── Maven 3.8+             — build & dependency management
```

---

## Quickstart

### Prerrequisitos

```bash
java -version   # debe mostrar openjdk 17 o superior
mvn -version    # debe mostrar Apache Maven 3.8+
```

Además necesitas:
- Proyecto activo en [Firebase Console](https://console.firebase.google.com/) con **Firestore** habilitado
- [JavaFX SDK 21](https://gluonhq.com/products/javafx/) si vas a ejecutar el JAR directamente

---

### 1. Clonar

```bash
git clone https://github.com/AlejoTechEngineer/u4-firebase-java.git
cd u4-firebase-java
```

### 2. Credenciales Firebase

1. Abre [Firebase Console](https://console.firebase.google.com/) → tu proyecto → ⚙️ **Configuración** → **Cuentas de servicio**
2. Clic en **Generar nueva clave privada** → descarga el `.json`
3. Renómbralo a `serviceAccountKey.json` y colócalo en la raíz del proyecto:

```
u4-firebase-java/
├── pom.xml
├── serviceAccountKey.json   ← aquí
└── src/
```

> `serviceAccountKey.json` está en `.gitignore`. Nunca se sube al repo.

### 3. Compilar

```bash
mvn package
```

Genera `target/firebase-cloud-manager-1.0.0.jar` con todas las dependencias incluidas.

### 4. Ejecutar

**Durante desarrollo (recomendado):**

```bash
mvn javafx:run
```

**Con el JAR compilado:**

```bash
java --module-path /path/to/javafx-sdk-21/lib \
     --add-modules javafx.controls,javafx.fxml \
     -jar target/firebase-cloud-manager-1.0.0.jar
```

---

## Estructura

```
u4-firebase-java/
│
├── src/main/
│   ├── java/com/polipoli/firebaseapp/
│   │   ├── MainApp.java            # Entry point — inicializa JavaFX y Firebase
│   │   ├── MainController.java     # Controlador principal — CRUD, eventos UI, CSV
│   │   ├── FirebaseService.java    # Capa de acceso a Firestore (get/add/update/delete)
│   │   └── CSVReader.java          # Parser CSV con detección automática de colección
│   │
│   └── resources/
│       ├── fxml/MainView.fxml      # Layout declarativo de la interfaz
│       └── css/styles.css          # Tema oscuro personalizado
│
├── data/
│   ├── estudiantes.csv             # Dataset de prueba → colección students
│   ├── empleados.csv               # Dataset de prueba → colección empleados
│   └── productos.csv               # Dataset de prueba → colección productos
│
├── pom.xml                         # Dependencias Maven + plugin JavaFX
└── .gitignore                      # Excluye serviceAccountKey.json, target/, .idea/
```

---

## Formato CSV

El `CSVReader` detecta la colección destino según las cabeceras del archivo — no requiere configuración manual.

**`estudiantes.csv` → `students`**
```csv
nombre,email,edad,carrera
Ana López,ana@example.com,22,Ingeniería de Sistemas
```

**`empleados.csv` → `empleados`**
```csv
nombre,cargo,salario,departamento
Carlos Ruiz,Dev Senior,4500000,Tecnología
```

**`productos.csv` → `productos`**
```csv
nombre,precio,stock,categoria
Laptop Pro,3200000,15,Electrónica
```

---

## Seguridad

> **`serviceAccountKey.json` contiene credenciales con acceso total a tu proyecto Firebase. Nunca lo subas a ningún repositorio.**

El `.gitignore` lo excluye de forma permanente. Si por error lo commiteás:

1. Revoca la clave inmediatamente en **Firebase Console → Cuentas de servicio → Revocar**
2. Genera una nueva clave privada
3. Limpia el historial de git con `git filter-branch` o `git filter-repo`

---

## Autor

**Alejandro De Mendoza**
Ingeniero Informático · Especialista en IA · Maestría en Arquitectura de Software
[@AlejoTechEngineer](https://github.com/AlejoTechEngineer)
