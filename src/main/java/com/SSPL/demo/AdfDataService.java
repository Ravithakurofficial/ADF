package com.SSPL.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdfDataService {

    @Autowired
    private AdfDataRepository adfDataRepository;

    public void saveData(database adfData) {
        adfDataRepository.save(adfData);
    }

    public List<database> getAllData() {
        return adfDataRepository.findAll(); // Fetch all records from the database
    }
}
