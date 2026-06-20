# Firebase Cloud Manager — Unidad 4
**Módulo:** Fundamentos de la Tecnología Cloud  
**Institución:** Politécnico Grancolombiano  
**Autor:** Alejandro De Mendoza  

---

## Descripción
Aplicación de escritorio desarrollada en **Java 17 + JavaFX 21** que permite gestionar
colecciones en **Firebase Firestore** con operaciones CRUD completas e interfaz gráfica moderna.

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

## Requisitos
- Java 17 o superior
- Maven 3.8+
- Proyecto Firebase activo con Firestore habilitado

## Configuración Firebase
1. Ir a **Firebase Console → Configuración del proyecto → Cuentas de servicio**
2. Generar nueva clave privada → descargar `serviceAccountKey.json`
3. **Colocar el archivo `serviceAccountKey.json` en la raíz del proyecto** (misma carpeta que `pom.xml`)

## Ejecución
```bash
# Instalar dependencias y ejecutar
mvn javafx:run

# O compilar fat-JAR
mvn package
java -jar target/firebase-cloud-manager-1.0.0.jar
```

## CSVs de prueba
Los archivos de prueba están en la carpeta `data/`:
- `data/estudiantes.csv`
- `data/empleados.csv`
- `data/productos.csv`

El sistema detecta automáticamente el tipo de colección según las cabeceras del CSV.

## Estructura del proyecto
```
firebase-cloud-manager/
├── src/main/java/com/polipoli/firebaseapp/
│   ├── MainApp.java          ← Punto de entrada JavaFX
│   ├── MainController.java   ← Controlador CRUD + UI
│   ├── FirebaseService.java  ← Operaciones Firestore
│   └── CSVReader.java        ← Lectura y detección de CSV
├── src/main/resources/
│   ├── fxml/MainView.fxml    ← Layout de la interfaz
│   └── css/styles.css        ← Tema oscuro
├── data/                     ← CSVs de prueba
├── pom.xml
└── serviceAccountKey.json    ← (NO subir a Git — agregar a .gitignore)
```

## ⚠️ Seguridad
**Nunca subir `serviceAccountKey.json` a repositorios públicos.**
Agregar al `.gitignore`:
```
serviceAccountKey.json
```
