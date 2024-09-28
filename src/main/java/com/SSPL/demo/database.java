package com.SSPL.demo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "AdfData")
public class database {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment configuration
    @Column(name = "ID") // Column name for the ID
    private Long id; // Use Long for auto-increment IDs

    @Column(name = "TimeStamp")
    private String timestamp;

    @Column(name = "Theta")
    private double azmat;

    @Column(name = "Elevation")
    private double elevation;

    @Column(name = "ExpectedRange")
    private double expectedRange;

    @Column(name = "Longitude")
    private double longitude;

    // Add Latitude field
    @Column(name = "Latitude")
    private double latitude;

    @Column(name = "Server_Longitude")
    private double serverLongitude;

    // Add Latitude field
    @Column(name = "Server_Latitude")
    private double serverLatitude;
}
