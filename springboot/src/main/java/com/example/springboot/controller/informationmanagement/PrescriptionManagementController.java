package com.codegym.a0223i1_pharmacy_professional_be.controller.informationmanagement;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;


import com.codegym.a0223i1_pharmacy_professional_be.dto.PrescriptionDetailDTO;
import com.codegym.a0223i1_pharmacy_professional_be.entity.Medicine;
import com.codegym.a0223i1_pharmacy_professional_be.entity.Prescription;
import com.codegym.a0223i1_pharmacy_professional_be.entity.PrescriptionDetail;

import com.codegym.a0223i1_pharmacy_professional_be.entity.Symptom;
import com.codegym.a0223i1_pharmacy_professional_be.service.interfaceservice.informationmanagement.MedicineInformationService;
import com.codegym.a0223i1_pharmacy_professional_be.service.interfaceservice.informationmanagement.PrescriptionDetailService;
import com.codegym.a0223i1_pharmacy_professional_be.service.interfaceservice.informationmanagement.PrescriptionService;
import com.codegym.a0223i1_pharmacy_professional_be.service.interfaceservice.informationmanagement.SymptomService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("api/v1/details")
public class PrescriptionManagementController {
    private PrescriptionService prescriptionService;
    private PrescriptionDetailService detailPrescriptionService;
    private MedicineInformationService medicineService;
    private SymptomService symptomService;

    @Autowired
    public PrescriptionManagementController(PrescriptionService prescriptionService, PrescriptionDetailService detailPrescriptionService, MedicineInformationService medicineService, SymptomService symptomService) {
        this.prescriptionService = prescriptionService;
        this.detailPrescriptionService = detailPrescriptionService;
        this.medicineService = medicineService;
        this.symptomService = symptomService;
    }

    @GetMapping
    public ResponseEntity<List<Prescription>> findAllPrescription() {
        List<Prescription> prescriptionList = prescriptionService.findAll();
        for (Prescription prescription : prescriptionList) {
            Symptom symptom = prescription.getSymptom();
            if (symptom != null) {
                prescription.setSymptomName(symptom.getSymptomName());
            }
        }
        Collections.sort(prescriptionList, Comparator.comparing(Prescription::getCreateDate).reversed());

        return ResponseEntity.ok(prescriptionList);
    }


    @PostMapping
    public ResponseEntity<?> createPrescription(@RequestBody PrescriptionDetailDTO detailPrescriptionDTO) {

        Prescription existingPrescription = prescriptionService.findPrescriptionByName(detailPrescriptionDTO.getPrescription().getPrescriptionName());

        if (existingPrescription != null && existingPrescription.getPrescriptionName().equals(detailPrescriptionDTO.getPrescription().getPrescriptionName())) {
            return ResponseEntity.ok().body("Prescription with the same name already exists");

        } else {
            try {

                List<PrescriptionDetailDTO> detailPrescriptions = detailPrescriptionDTO.getDetailPrescription();
                List<PrescriptionDetailDTO> saveList = new ArrayList<>();

                PrescriptionDetail detailPrescription = new PrescriptionDetail();


                saveList.addAll(detailPrescriptions);

                detailPrescription.setMedicineList(saveList.get(0).getMedicineId() + "," + saveList.get(1).getMedicineId2() + "," + saveList.get(2).getMedicineId3()
                        + "," + saveList.get(3).getMedicineId4() + "," + saveList.get(4).getMedicineId5() + "," + saveList.get(5).getMedicineId6()
                        + "," + saveList.get(6).getMedicineId7());


                detailPrescription.setTimes(saveList.get(0).getTimes() + "," + saveList.get(1).getTimes2() + "," + saveList.get(2).getTimes3() + "," + saveList.get(3).getTimes4()
                        + "," + saveList.get(4).getTimes5() + "," + saveList.get(5).getTimes6() + "," + saveList.get(6).getTimes7());

                detailPrescription.setQuantity(saveList.get(0).getQuantity() + "," + saveList.get(1).getQuantity2() + "," + saveList.get(2).getQuantity3() + "," + saveList.get(3).getQuantity4()
                        + "," + saveList.get(4).getQuantity5() + "," + saveList.get(5).getQuantity6() + "," + saveList.get(6).getQuantity7());

                detailPrescription.setQuantityPerTimes(saveList.get(0).getQuantityPerTimes() + "," + saveList.get(1).getQuantityPerTimes2() + "," + saveList.get(2).getQuantityPerTimes3()
                        + "," + saveList.get(3).getQuantityPerTimes4() + "," + saveList.get(4).getQuantityPerTimes5() + "," + saveList.get(5).getQuantityPerTimes6()
                        + "," + saveList.get(6).getQuantityPerTimes7());


                detailPrescriptionDTO.setDeleteFlag(true);

                Symptom symptom = new Symptom();
                symptom.setSymptomName(detailPrescriptionDTO.getPrescription().getSymptom().getSymptomName());

                LocalDateTime createDate = LocalDateTime.now();


                Prescription prescription = new Prescription();
                prescription.setPrescriptionName(detailPrescriptionDTO.getPrescription().getPrescriptionName());
                prescription.setTarget(detailPrescriptionDTO.getPrescription().getTarget());
                prescription.setTreatmentPeriod(detailPrescriptionDTO.getPrescription().getTreatmentPeriod());
                prescription.setNote(detailPrescriptionDTO.getPrescription().getNote());
                prescription.setDeleteFlag(detailPrescriptionDTO.getDeleteFlag());
                prescription.setCreateDate(createDate);
                prescription.setPrescriptionId(prescriptionService.generateNextCode());
                prescription.setSymptom(symptom);

                detailPrescription.setPrescription(prescription);

                PrescriptionDetail save = detailPrescriptionService.save(detailPrescription);

                return new ResponseEntity<>(save, HttpStatus.CREATED);
            } catch (Exception e){
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PrescriptionDetail> updateDetailPrescription(@PathVariable Long id, @RequestBody PrescriptionDetailDTO detailPrescriptionDTO) {

        List<PrescriptionDetailDTO> detailPrescriptions = detailPrescriptionDTO.getDetailPrescription();
        List<PrescriptionDetailDTO> saveList = new ArrayList<>();


        saveList.addAll(detailPrescriptions);


        PrescriptionDetail existingDetailPrescription = detailPrescriptionService.findById(id);
        Prescription existingPrescription = prescriptionService.findPrescriptionById(detailPrescriptionDTO.getPrescription().getPrescriptionId());
        Symptom existingSymptom = symptomService.findSymptomByPrescriptionId(detailPrescriptionDTO.getPrescription().getPrescriptionId());

        if (existingDetailPrescription != null) {
            existingSymptom.setSymptomName(detailPrescriptionDTO.getPrescription().getSymptom().getSymptomName());

            existingDetailPrescription.setQuantityPerTimes(detailPrescriptionDTO.getQuantityPerTimes());
            existingDetailPrescription.setTimes(detailPrescriptionDTO.getTimes());
            existingDetailPrescription.setQuantity(detailPrescriptionDTO.getQuantity());


            existingDetailPrescription.setMedicineList(saveList.get(0).getMedicineId() + "," + saveList.get(1).getMedicineId2()  + "," + saveList.get(2).getMedicineId3()
                    + "," + saveList.get(3).getMedicineId4() + "," + saveList.get(4).getMedicineId5() + "," + saveList.get(5).getMedicineId6()
                    + "," + saveList.get(6).getMedicineId7());
            existingDetailPrescription.setTimes(saveList.get(0).getTimes() + "," + saveList.get(1).getTimes2() + "," + saveList.get(2).getTimes3() + "," + saveList.get(3).getTimes4()
                    + "," + saveList.get(4).getTimes5() + "," + saveList.get(5).getTimes6() + "," + saveList.get(6).getTimes7());

            existingDetailPrescription.setQuantity(saveList.get(0).getQuantity() + "," + saveList.get(1).getQuantity2() + "," + saveList.get(2).getQuantity3() + "," + saveList.get(3).getQuantity4()
                    + "," + saveList.get(4).getQuantity5() + "," + saveList.get(5).getQuantity6() + "," + saveList.get(6).getQuantity7()
            );

            existingDetailPrescription.setQuantityPerTimes(saveList.get(0).getQuantityPerTimes() + "," + saveList.get(1).getQuantityPerTimes2() + "," + saveList.get(2).getQuantityPerTimes3()
                    + "," + saveList.get(3).getQuantityPerTimes4() + "," + saveList.get(4).getQuantityPerTimes5() + "," + saveList.get(5).getQuantityPerTimes6() + "," + saveList.get(6).getQuantityPerTimes7()
            );

            existingPrescription.setPrescriptionName(detailPrescriptionDTO.getPrescription().getPrescriptionName());
            existingPrescription.setNote(detailPrescriptionDTO.getPrescription().getNote());
            existingPrescription.setTarget(detailPrescriptionDTO.getPrescription().getTarget());
            existingPrescription.setTreatmentPeriod(detailPrescriptionDTO.getPrescription().getTreatmentPeriod());
            existingPrescription.setSymptom(existingSymptom);


            prescriptionService.updatePrescription(existingPrescription, existingSymptom);
            existingDetailPrescription.setPrescription(existingPrescription);

            detailPrescriptionService.updateDetailPrescription(existingDetailPrescription);
            return ResponseEntity.ok(existingDetailPrescription);
        } else {
            return ResponseEntity.notFound().build();
        }

    }



    @GetMapping("/{id}")
    public ResponseEntity<Symptom> findDetailPrescriptionById(@PathVariable Long id) {
        Prescription prescription = prescriptionService.findPrescriptionByDetailId(id);
        PrescriptionDetail detailPrescription = detailPrescriptionService.findById(id);

        if (prescription == null) {
            return ResponseEntity.notFound().build();
        }

        Symptom symptom = symptomService.findSymptomByPrescriptionId(prescription.getPrescriptionId());

        if (symptom == null) {
            return ResponseEntity.notFound().build();
        }

        PrescriptionDetailDTO dto = new PrescriptionDetailDTO();

        dto.setPrescription(prescription);
        dto.setSymptom(symptom);
        dto.setQuantity(detailPrescription.getQuantity());
        dto.setTimes(detailPrescription.getTimes());
        dto.setQuantityPerTimes(detailPrescription.getQuantityPerTimes());

        return ResponseEntity.ok(symptom);
    }

    @DeleteMapping("/{id}")
    public  ResponseEntity<Void> deletePrescription(@PathVariable String id){
        Prescription existingPrescription = prescriptionService.findPrescriptionById(id);
        if(existingPrescription != null){
            prescriptionService.deletePrescription(id);
            return ResponseEntity.ok().build();
        }else {
            return ResponseEntity.notFound().build();
        }
    }
}

