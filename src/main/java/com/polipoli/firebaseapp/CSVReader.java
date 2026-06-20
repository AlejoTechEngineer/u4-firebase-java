package com.polipoli.firebaseapp;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * CSVReader — lee archivos CSV y convierte cada fila en un Map<String, Object>.
 * Detecta automáticamente la colección destino según las cabeceras del archivo.
 */
public class CSVReader {

    public static class CSVResult {
        public final String detectedCollection;
        public final List<String> headers;
        public final List<Map<String, Object>> records;

        public CSVResult(String detectedCollection,
                         List<String> headers,
                         List<Map<String, Object>> records) {
            this.detectedCollection = detectedCollection;
            this.headers = headers;
            this.records = records;
        }
    }

    /**
     * Lee el CSV y retorna el resultado con colección detectada y registros.
     */
    public static CSVResult read(File file) throws IOException {
        List<Map<String, Object>> records = new ArrayList<>();
        List<String> headers = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String headerLine = br.readLine();
            if (headerLine == null) throw new IOException("El archivo CSV está vacío.");

            // Limpiar BOM si existe
            headerLine = headerLine.replace("\uFEFF", "");
            String[] cols = headerLine.split(",");
            for (String col : cols) {
                headers.add(col.trim().toLowerCase());
            }

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] values = line.split(",", -1);
                Map<String, Object> record = new LinkedHashMap<>();
                for (int i = 0; i < headers.size(); i++) {
                    String val = (i < values.length) ? values[i].trim() : "";
                    // Intentar parsear como número
                    record.put(headers.get(i), parseValue(val));
                }
                records.add(record);
            }
        }

        String collection = detectCollection(headers);
        return new CSVResult(collection, headers, records);
    }

    /** Parsea String a número si es posible, de lo contrario retorna String. */
    private static Object parseValue(String val) {
        if (val == null || val.isEmpty()) return val;
        try { return Long.parseLong(val); } catch (NumberFormatException ignored) {}
        try { return Double.parseDouble(val); } catch (NumberFormatException ignored) {}
        return val;
    }

    /**
     * Detecta el tipo de colección según las cabeceras del CSV.
     * Prioriza el match más fuerte.
     */
    public static String detectCollection(List<String> headers) {
        Set<String> h = new HashSet<>(headers);

        // Estudiantes
        if (h.contains("carrera") || h.contains("matricula") ||
                (h.contains("nombre") && h.contains("email") && h.contains("edad"))) {
            return "students";
        }
        // Empleados
        if (h.contains("cargo") || h.contains("salario") ||
                h.contains("departamento") || h.contains("empleado")) {
            return "empleados";
        }
        // Productos
        if (h.contains("precio") || h.contains("stock") ||
                h.contains("categoria") || h.contains("producto")) {
            return "productos";
        }
        // Default
        return "registros";
    }

    /** Retorna label amigable para mostrar en UI. */
    public static String collectionLabel(String collection) {
        return switch (collection) {
            case "students"  -> "Estudiantes";
            case "empleados" -> "Empleados";
            case "productos" -> "Productos";
            default          -> "Registros";
        };
    }
}
