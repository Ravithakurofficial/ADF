package com.SSPL.demo;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class ArduinoService {

    @Autowired
    private ArduinoController arduinoController;

    // Path to the Firebase credentials file (update as needed)
    private static final String FIREBASE_CONFIG_PATH = "D:\\Drdo work folder\\Gui + backend\\drdo1 (1)\\drdo1\\drdo\\demo\\google-services.json";

    @PostConstruct
    public void initialize() {
        try (FileInputStream serviceAccount = new FileInputStream(FIREBASE_CONFIG_PATH)) {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            FirebaseApp.initializeApp(options);
            System.out.println("Firebase initialized successfully");
        } catch (IOException e) {
            System.err.println("Failed to initialize Firebase: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @PostConstruct
    public void init() {
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    arduinoController.sendDataToClients(line);
                }
            } catch (Exception e) {
                System.err.println("Error reading data from Arduino: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }
}
