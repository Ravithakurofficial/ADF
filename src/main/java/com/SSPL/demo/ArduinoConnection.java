package com.SSPL.demo;

import com.fazecast.jSerialComm.SerialPort;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.*;

@RestController
public class ArduinoConnection {

    @Autowired
    private AdfDataService adfDataService;

    private SerialPort serialPort;

    private double azmat;
    private double elevation;
    private double range;
    private double latitude;
    private double longitude;

    private final double serverLatitude = 28.640597286241622;
    private final double serverLongitude = 77.04705327701325;

    @PostMapping("/api/serial-data")
    public ResponseEntity<Map<String, Object>> receiveSerialData(@RequestBody PortConfig dataRequest) {
        String receivedData = dataRequest.getData();
        System.out.println("Data received from frontend: " + receivedData);

        Map<String, Object> response = new HashMap<>();

        // Parse the received data
        parseData(receivedData);

        // Check if the received data contains "dont"
        if (receivedData.toLowerCase().contains("dont")) {
            System.out.println("Received data contains 'dont'; skipping database entry.");
            response.put("message", "Data received but not processed due to 'dont' keyword.");
            response.put("data", receivedData); // Add the received data to the response
            return ResponseEntity.ok(response); // Return early
        }

        // Save data to the database only if "dont" is not present
        database adfData = new database();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Ensure format matches database column
        String currentTime = sdf.format(new Date());
        adfData.setTimestamp(currentTime);
        adfData.setAzmat(azmat);
        adfData.setElevation(elevation);
        adfData.setExpectedRange(range);
        adfData.setLatitude(latitude); // Set the calculated latitude
        adfData.setLongitude(longitude); // Set the calculated longitude
        adfData.setServerLatitude(serverLatitude); // Set the server latitude
        adfData.setServerLongitude(serverLongitude); // Set the server longitude
        // Save to database
        adfDataService.saveData(adfData);

        // Optionally, send this data to a connected serial port
        if (serialPort != null && serialPort.isOpen()) {
            serialPort.writeBytes(receivedData.getBytes(), receivedData.length());
        }

        response.put("message", "Data received and processed");
        response.put("data", receivedData); // Add the received data to the response
        return ResponseEntity.ok(response);
    }

    private void parseData(String data) {
        try {
            // Remove any extra whitespace and split the string by spaces
            String[] parts = data.trim().split("\\s+");

            for (String part : parts) {
                // Trim any leading or trailing whitespace from each part
                part = part.trim();

                if (part.startsWith("AZ")) {
                    azmat = Double.parseDouble(part.substring(2).trim());
                } else if (part.startsWith("ev")) {
                    elevation = Double.parseDouble(part.substring(2).trim());
                } else if (part.startsWith("r")) {
                    range = Double.parseDouble(part.substring(1).trim());
                }
            }

            // Convert azimuth angle from degrees to radians
            double azmatRad = toRadians(azmat);

            // Earth radius in meters
            double earthRadius = 6371000;

            // Calculate the offsets
            double x_offset = range * cos(azmatRad);
            double y_offset = range * sin(azmatRad);

            // Calculate new latitude and longitude
            latitude = serverLatitude + (y_offset / earthRadius) * (180 / Math.PI);
            longitude = serverLongitude + (x_offset / earthRadius) * (180 / Math.PI) / cos(toRadians(serverLatitude));

            System.out.println("Calculated Coordinates: Latitude " + latitude + ", Longitude " + longitude);
            System.out.println("Azmat: " + azmat);
            System.out.println("Elevation: " + elevation);
            System.out.println("Range: " + range);
        } catch (NumberFormatException e) {
            System.err.println("Error parsing data: " + e.getMessage());
        }
    }

    @GetMapping("/api/update-data")
    public ResponseEntity<Map<String, Double>> sendDataToFrontend() {
        Map<String, Double> response = new HashMap<>();
        response.put("azmat", azmat);
        response.put("elevation", elevation);
        response.put("range", range);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/map-geo")
    public ResponseEntity<Map<String, Double>> sendDataToMap() {
        Map<String, Double> response = new HashMap<>();
        response.put("serverLatitude", serverLatitude);
        response.put("serverLongitude", serverLongitude);
        response.put("latitude", latitude);
        response.put("longitude", longitude);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/events")
    public ResponseEntity<List<database>> getEvents() {
        List<database> eventData = adfDataService.getAllData(); // Fetch all data from the database
        return ResponseEntity.ok(eventData); // Return the data as JSON
    }
    @PostMapping("/sendNotification")
    public String sendNotification(@RequestBody NotificationRequest notificationRequest) {
        try {
            // Build the notification message
            Message message = Message.builder()
                    .setNotification(com.google.firebase.messaging.Notification.builder()
                            .setTitle(notificationRequest.getTitle())
                            .setBody(notificationRequest.getBody())
                            .build())
                    .setTopic("global") // Or you can target a specific user using token
                    .build();

            // Send the notification via Firebase Messaging
            String response = FirebaseMessaging.getInstance().send(message);
            return "Notification sent: " + response;
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
            return "Error sending notification: " + e.getMessage();
        }
    }



    // Additional methods for serial port configuration and handling can be added here.
}
