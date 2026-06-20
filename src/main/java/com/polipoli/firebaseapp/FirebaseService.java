package com.polipoli.firebaseapp;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * FirebaseService — centraliza todas las operaciones CRUD contra Firestore.
 * Autor: Alejandro De Mendoza
 * Módulo: Fundamentos de la Tecnología Cloud — Unidad 4
 */
public class FirebaseService {

    private static Firestore db;

    // ─── Inicialización ──────────────────────────────────────────────────────
    public static void initialize() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            FileInputStream serviceAccount =
                    new FileInputStream("serviceAccountKey.json");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
        }
        db = FirestoreClient.getFirestore();
    }

    public static Firestore getDb() {
        return db;
    }

    // ─── INSERT ──────────────────────────────────────────────────────────────
    public static String insertDocument(String collection, Map<String, Object> data)
            throws ExecutionException, InterruptedException {
        DocumentReference docRef = db.collection(collection).document();
        data.put("id", docRef.getId());
        docRef.set(data).get();
        return docRef.getId();
    }

    // ─── READ ALL ─────────────────────────────────────────────────────────────
    public static List<Map<String, Object>> getAllDocuments(String collection)
            throws ExecutionException, InterruptedException {
        List<Map<String, Object>> results = new ArrayList<>();
        QuerySnapshot querySnapshot = db.collection(collection).get().get();
        for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
            Map<String, Object> data = new HashMap<>(document.getData());
            data.put("docId", document.getId());
            results.add(data);
        }
        return results;
    }

    // ─── READ BY FIELD ────────────────────────────────────────────────────────
    public static List<Map<String, Object>> searchDocuments(
            String collection, String field, String value)
            throws ExecutionException, InterruptedException {
        List<Map<String, Object>> results = new ArrayList<>();
        Query query = db.collection(collection).whereEqualTo(field, value);
        QuerySnapshot snapshot = query.get().get();
        for (QueryDocumentSnapshot doc : snapshot.getDocuments()) {
            Map<String, Object> data = new HashMap<>(doc.getData());
            data.put("docId", doc.getId());
            results.add(data);
        }
        return results;
    }

    // ─── UPDATE ──────────────────────────────────────────────────────────────
    public static void updateDocument(String collection, String docId,
                                       Map<String, Object> updates)
            throws ExecutionException, InterruptedException {
        db.collection(collection).document(docId).update(updates).get();
    }

    // ─── DELETE ──────────────────────────────────────────────────────────────
    public static void deleteDocument(String collection, String docId)
            throws ExecutionException, InterruptedException {
        db.collection(collection).document(docId).delete().get();
    }

    // ─── BATCH INSERT desde CSV ───────────────────────────────────────────────
    public static int batchInsert(String collection, List<Map<String, Object>> records)
            throws ExecutionException, InterruptedException {
        WriteBatch batch = db.batch();
        int count = 0;
        for (Map<String, Object> record : records) {
            DocumentReference ref = db.collection(collection).document();
            record.put("id", ref.getId());
            batch.set(ref, record);
            count++;
            // Firestore limita 500 ops por batch
            if (count % 499 == 0) {
                batch.commit().get();
                batch = db.batch();
            }
        }
        batch.commit().get();
        return count;
    }
}
