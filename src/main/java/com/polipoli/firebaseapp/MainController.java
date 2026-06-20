package com.polipoli.firebaseapp;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.*;

/**
 * MainController — controlador principal de la interfaz JavaFX.
 * Maneja las 4 operaciones CRUD + carga masiva desde CSV.
 * Autor: Alejandro De Mendoza
 */
public class MainController implements Initializable {

    // ── Top bar ──────────────────────────────────────────────────────────────
    @FXML private ComboBox<String> collectionCombo;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> searchByCombo;
    @FXML private Button btnSearch;
    @FXML private Button btnLoadAll;
    @FXML private Button btnLoadCsv;
    @FXML private Label statusLabel;

    // ── Table ─────────────────────────────────────────────────────────────────
    @FXML private TableView<Map<String, Object>> tableView;

    // ── Form panel ───────────────────────────────────────────────────────────
    @FXML private VBox formPanel;
    @FXML private GridPane formGrid;
    @FXML private Button btnSave;
    @FXML private Button btnCancelEdit;
    @FXML private Button btnNewRecord;
    @FXML private Button btnDeleteSelected;
    @FXML private Label formTitleLabel;

    // ── State ─────────────────────────────────────────────────────────────────
    private String currentCollection = "students";
    private List<String> currentHeaders = new ArrayList<>();
    private final ObservableList<Map<String, Object>> tableData = FXCollections.observableArrayList();
    private Map<String, Object> editingRecord = null; // null = inserción nueva
    private final Map<String, TextField> formFields = new LinkedHashMap<>();

    // ── Collections config ────────────────────────────────────────────────────
    private static final Map<String, List<String>> COLLECTION_FIELDS = new LinkedHashMap<>();
    static {
        COLLECTION_FIELDS.put("students",  List.of("nombre", "email", "edad", "carrera"));
        COLLECTION_FIELDS.put("empleados", List.of("nombre", "cargo", "salario", "departamento"));
        COLLECTION_FIELDS.put("productos", List.of("nombre", "precio", "stock", "categoria"));
    }

    // ─────────────────────────────────────────────────────────────────────────
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        collectionCombo.setItems(FXCollections.observableArrayList(
                "students", "empleados", "productos"));
        collectionCombo.setValue("students");
        collectionCombo.setOnAction(e -> {
            currentCollection = collectionCombo.getValue();
            setupFormForCollection(currentCollection);
            loadAllRecords();
        });

        tableView.setEditable(false);
        tableView.setItems(tableData);
        tableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, selected) -> { if (selected != null) populateFormForEdit(selected); });

        setupFormForCollection("students");
        loadAllRecords();
    }

    // ─── Setup dinámico del form y tabla ─────────────────────────────────────
    private void setupFormForCollection(String collection) {
        currentHeaders = new ArrayList<>(
                COLLECTION_FIELDS.getOrDefault(collection, List.of("nombre")));

        // Reconstruir form
        formGrid.getChildren().clear();
        formFields.clear();
        int row = 0;
        for (String field : currentHeaders) {
            Label lbl = new Label(capitalize(field) + ":");
            lbl.getStyleClass().add("form-label");
            TextField tf = new TextField();
            tf.setPromptText("Ingrese " + field);
            tf.getStyleClass().add("form-field");
            GridPane.setConstraints(lbl, 0, row);
            GridPane.setConstraints(tf, 1, row);
            formGrid.getChildren().addAll(lbl, tf);
            formFields.put(field, tf);
            row++;
        }

        // Reconstruir columnas de tabla
        tableView.getColumns().clear();

        // Columna ID (corta)
        TableColumn<Map<String, Object>, String> idCol = new TableColumn<>("ID");
        idCol.setPrefWidth(120);
        idCol.setCellValueFactory(cell -> {
            Object v = cell.getValue().get("docId");
            return new SimpleStringProperty(v != null ? v.toString().substring(0, 8) + "…" : "");
        });
        tableView.getColumns().add(idCol);

        for (String field : currentHeaders) {
            TableColumn<Map<String, Object>, String> col = new TableColumn<>(capitalize(field));
            col.setPrefWidth(150);
            col.setCellValueFactory(cell -> {
                Object v = cell.getValue().get(field);
                return new SimpleStringProperty(v != null ? v.toString() : "");
            });
            tableView.getColumns().add(col);
        }

        // Columna acciones
        TableColumn<Map<String, Object>, String> actCol = new TableColumn<>("Acciones");
        actCol.setPrefWidth(160);
        actCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn   = new Button("✏ Editar");
            private final Button deleteBtn = new Button("🗑 Eliminar");
            private final HBox box = new HBox(6, editBtn, deleteBtn);
            {
                editBtn.getStyleClass().add("btn-edit");
                deleteBtn.getStyleClass().add("btn-delete");
                editBtn.setOnAction(e -> {
                    Map<String, Object> item = getTableView().getItems().get(getIndex());
                    populateFormForEdit(item);
                });
                deleteBtn.setOnAction(e -> {
                    Map<String, Object> item = getTableView().getItems().get(getIndex());
                    confirmAndDelete(item);
                });
            }
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
        tableView.getColumns().add(actCol);

        // Searchby combo
        searchByCombo.setItems(FXCollections.observableArrayList(currentHeaders));
        searchByCombo.setValue(currentHeaders.get(0));

        clearForm();
        setStatus("Colección: " + CSVReader.collectionLabel(collection));
    }

    // ─── CRUD: Load All ───────────────────────────────────────────────────────
    @FXML
    private void loadAllRecords() {
        setStatus("Cargando registros…");
        new Thread(() -> {
            try {
                List<Map<String, Object>> docs = FirebaseService.getAllDocuments(currentCollection);
                Platform.runLater(() -> {
                    tableData.setAll(docs);
                    setStatus("✓ " + docs.size() + " registros cargados desde '" + currentCollection + "'");
                    clearForm();
                });
            } catch (Exception ex) {
                Platform.runLater(() -> setStatus("✗ Error al cargar: " + ex.getMessage()));
            }
        }).start();
    }

    // ─── CRUD: Search ─────────────────────────────────────────────────────────
    @FXML
    private void searchRecords() {
        String query = searchField.getText().trim();
        String field = searchByCombo.getValue();
        if (query.isEmpty()) { loadAllRecords(); return; }

        setStatus("Buscando '" + query + "' en campo '" + field + "'…");
        new Thread(() -> {
            try {
                List<Map<String, Object>> results =
                        FirebaseService.searchDocuments(currentCollection, field, query);
                Platform.runLater(() -> {
                    tableData.setAll(results);
                    setStatus("✓ " + results.size() + " resultado(s) para '" + query + "'");
                });
            } catch (Exception ex) {
                Platform.runLater(() -> setStatus("✗ Error en búsqueda: " + ex.getMessage()));
            }
        }).start();
    }

    // ─── CRUD: Save (Insert o Update) ────────────────────────────────────────
    @FXML
    private void saveRecord() {
        Map<String, Object> data = collectFormData();
        if (data == null) return; // validación falló

        if (editingRecord == null) {
            // INSERT
            setStatus("Insertando nuevo registro…");
            new Thread(() -> {
                try {
                    String id = FirebaseService.insertDocument(currentCollection, data);
                    Platform.runLater(() -> {
                        setStatus("✓ Registro insertado con ID: " + id.substring(0, 8) + "…");
                        clearForm();
                        loadAllRecords();
                    });
                } catch (Exception ex) {
                    Platform.runLater(() -> setStatus("✗ Error al insertar: " + ex.getMessage()));
                }
            }).start();
        } else {
            // UPDATE
            String docId = (String) editingRecord.get("docId");
            setStatus("Actualizando registro " + docId.substring(0, 8) + "…");
            new Thread(() -> {
                try {
                    FirebaseService.updateDocument(currentCollection, docId, data);
                    Platform.runLater(() -> {
                        setStatus("✓ Registro actualizado correctamente.");
                        clearForm();
                        loadAllRecords();
                    });
                } catch (Exception ex) {
                    Platform.runLater(() -> setStatus("✗ Error al actualizar: " + ex.getMessage()));
                }
            }).start();
        }
    }

    // ─── CRUD: Delete ─────────────────────────────────────────────────────────
    private void confirmAndDelete(Map<String, Object> record) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Eliminar este registro?");
        alert.setContentText("Esta acción no se puede deshacer.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String docId = (String) record.get("docId");
            setStatus("Eliminando…");
            new Thread(() -> {
                try {
                    FirebaseService.deleteDocument(currentCollection, docId);
                    Platform.runLater(() -> {
                        setStatus("✓ Registro eliminado.");
                        loadAllRecords();
                    });
                } catch (Exception ex) {
                    Platform.runLater(() -> setStatus("✗ Error al eliminar: " + ex.getMessage()));
                }
            }).start();
        }
    }

    // ─── CSV Load ─────────────────────────────────────────────────────────────
    @FXML
    private void loadFromCsv() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Seleccionar archivo CSV");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = chooser.showOpenDialog(tableView.getScene().getWindow());
        if (file == null) return;

        setStatus("Leyendo CSV: " + file.getName() + "…");
        new Thread(() -> {
            try {
                CSVReader.CSVResult result = CSVReader.read(file);
                String targetCollection = result.detectedCollection;
                int count = FirebaseService.batchInsert(targetCollection, result.records);
                Platform.runLater(() -> {
                    collectionCombo.setValue(targetCollection);
                    currentCollection = targetCollection;
                    setupFormForCollection(targetCollection);
                    loadAllRecords();
                    setStatus("✓ " + count + " registros cargados a colección '"
                            + CSVReader.collectionLabel(targetCollection) + "' desde CSV.");
                });
            } catch (Exception ex) {
                Platform.runLater(() ->
                        setStatus("✗ Error al procesar CSV: " + ex.getMessage()));
            }
        }).start();
    }

    // ─── Form helpers ─────────────────────────────────────────────────────────
    private void populateFormForEdit(Map<String, Object> record) {
        editingRecord = record;
        formTitleLabel.setText("✏ Editando registro");
        for (String field : currentHeaders) {
            TextField tf = formFields.get(field);
            if (tf != null) {
                Object val = record.get(field);
                tf.setText(val != null ? val.toString() : "");
            }
        }
        btnSave.setText("💾 Guardar cambios");
        btnCancelEdit.setVisible(true);
    }

    @FXML
    private void newRecord() {
        clearForm();
    }

    @FXML
    private void cancelEdit() {
        clearForm();
    }

    private void clearForm() {
        editingRecord = null;
        formTitleLabel.setText("➕ Nuevo registro");
        formFields.values().forEach(tf -> tf.setText(""));
        btnSave.setText("💾 Insertar");
        btnCancelEdit.setVisible(false);
    }

    private Map<String, Object> collectFormData() {
        Map<String, Object> data = new LinkedHashMap<>();
        for (String field : currentHeaders) {
            TextField tf = formFields.get(field);
            String val = tf != null ? tf.getText().trim() : "";
            if (val.isEmpty()) {
                showAlert("Campo requerido", "El campo '" + capitalize(field) + "' es obligatorio.");
                return null;
            }
            // Intentar castear a número
            try { data.put(field, Long.parseLong(val)); continue; }
            catch (NumberFormatException ignored) {}
            try { data.put(field, Double.parseDouble(val)); continue; }
            catch (NumberFormatException ignored) {}
            data.put(field, val);
        }
        return data;
    }

    private void setStatus(String msg) {
        statusLabel.setText(msg);
    }

    private void showAlert(String title, String message) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
