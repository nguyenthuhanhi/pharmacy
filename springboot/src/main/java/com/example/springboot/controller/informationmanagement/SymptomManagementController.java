package com.codegym.a0223i1_pharmacy_professional_be.controller.informationmanagement;



import com.codegym.a0223i1_pharmacy_professional_be.entity.Symptom;
import com.codegym.a0223i1_pharmacy_professional_be.service.interfaceservice.informationmanagement.SymptomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("api/v1/symptoms")
public class SymptomManagementController {

    private SymptomService symptomService;

    @Autowired

    public SymptomManagementController(SymptomService symptomService) {
        this.symptomService = symptomService;
    };


    @GetMapping
    public ResponseEntity<List<Symptom>> findAllSymptom() {
        List<Symptom> symptoms = symptomService.findAll();
        return ResponseEntity.ok(symptoms);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Symptom> getDataBySymptomId(@PathVariable Integer id) {
        try {
            Symptom symptom = symptomService.findById(id);
            if (symptom != null) {
                return ResponseEntity.ok(symptom);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
