package com.codegym.a0223i1_pharmacy_professional_be.controller.informationmanagement;

import com.codegym.a0223i1_pharmacy_professional_be.entity.Medicine;
import com.codegym.a0223i1_pharmacy_professional_be.service.interfaceservice.informationmanagement.MedicineInformationService;
import com.codegym.a0223i1_pharmacy_professional_be.dto.MedicineDto;
import com.codegym.a0223i1_pharmacy_professional_be.entity.Medicine;
import com.codegym.a0223i1_pharmacy_professional_be.service.interfaceservice.informationmanagement.IMedicineService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.xml.ws.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/medicine")
@CrossOrigin("*")
public class MedicineInformationManagementController {
    //
    @Autowired
    MedicineInformationService medicineService;
    @Autowired
    private IMedicineService medicineService2;

    @PersistenceContext
    private EntityManager entityManager;


    @GetMapping("")
    public ResponseEntity<List<Medicine>> getAllMedicine() {
        return new ResponseEntity<>(medicineService.getAllMedicine(), HttpStatus.OK);
    }



    @GetMapping("/{id}")
    public ResponseEntity<MedicineDto> getMedicineById(@PathVariable String id) {
        return new ResponseEntity<>(medicineService2.findMedicineById(id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMedicineById(@PathVariable String id) {
        System.out.println(id);

        Medicine existingMedicine = medicineService.findMedicineById(id);

        if (existingMedicine == null) {
            return new ResponseEntity<>("Không tìm thấy Medicine có ID: " + id, HttpStatus.NOT_FOUND);
        }

        medicineService.deleteMedicine(existingMedicine);
        return new ResponseEntity<>("success", HttpStatus.OK);
    }


    @GetMapping("/search")
    public ResponseEntity<List<Medicine>> filterMedicines(
            @RequestParam(required = false) String attribute,
            @RequestParam(required = false) String condition,
            @RequestParam(required = false) String searchText) {
        if (searchText == null || searchText.equals("")) {
            return new ResponseEntity<>(medicineService.getAllMedicine(), HttpStatus.OK);
        }

        String queryString = "SELECT m.medicineId, m.medicineGroup, m.medicineName, m.material, m.activeIngredient, m.unit, m.conversionRate, m.conversionUnit," +
                "m.quantity, m.importPrice, m.wholesalePrice, m.retailPrice, m.supplierDiscount, m.profitMarginWholesale, m.profitMarginRetail, m.supplier, m.origin, m.status FROM Medicine m WHERE 1=1";

        if (attribute != null && condition != null) {
            // Xử lý các yêu cầu bộ lọc
            queryString += " AND ";

            // Xử lý điều kiện tương ứng
            switch (condition) {
                case "Bằng":
                    queryString += "m." + attribute + " = :searchText";
                    break;
                case "Gần bằng":
                    queryString += "m." + attribute + " like :searchText";
                    searchText = "%" + searchText + "%";
                    break;
                case "Lớn hơn":
                    queryString += "m." + attribute + " > :searchText";
                    break;
                case "Nhỏ hơn":
                    queryString += "m." + attribute + " < :searchText";
                    break;
                case "Lớn hơn bằng":
                    queryString += "m." + attribute + " >= :searchText";
                    break;
                case "Nhỏ hơn bằng":
                    queryString += "m." + attribute + " <= :searchText";
                    break;
                case "Khác":
                    queryString += "m." + attribute + " != :searchText";
                    break;
                case "Tất cả":
                    return new ResponseEntity<>(medicineService.getAllMedicine(), HttpStatus.OK);
                default:
                    break;
            }
        }

        Query query = entityManager.createQuery(queryString, Medicine.class);
        if (searchText != null) {
            query.setParameter("searchText", searchText);
        }

        List<Medicine> medicines = query.getResultList();
        System.out.println(queryString+"");

        return new ResponseEntity<>(medicines, HttpStatus.OK);
    }

}
