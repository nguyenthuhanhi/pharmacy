package com.codegym.a0223i1_pharmacy_professional_be.controller.informationmanagement;

import com.codegym.a0223i1_pharmacy_professional_be.dto.SupplierDTO;
import com.codegym.a0223i1_pharmacy_professional_be.entity.Supplier;
import com.codegym.a0223i1_pharmacy_professional_be.service.interfaceservice.informationmanagement.ISupplierService;
import com.codegym.a0223i1_pharmacy_professional_be.validate.SupplierValidate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/suppliers")
public class SupplierManagementController {
    //Quản lý nhà cung cấp
    @Autowired
    private ISupplierService supplierService;

    @Autowired
    private SupplierValidate supplierValidate;

    @GetMapping("/listSupplier")
    public ResponseEntity<Page<Supplier>> getAllSuppliers(
            @RequestParam(required = false) String orderBy,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String searchValue,
            Pageable pageable) {

        Page<Supplier> suppliers;

        if (searchType != null && !searchType.isEmpty() && searchValue != null && !searchValue.isEmpty()) {
            switch (searchType) {
                case "supplierId":
                    suppliers = supplierService.findAllSuppliers(searchValue, null, null, null, orderBy, pageable);
                    break;
                case "supplierName":
                    suppliers = supplierService.findAllSuppliers(null, searchValue, null, null, orderBy, pageable);
                    break;
                case "address":
                    suppliers = supplierService.findAllSuppliers(null, null, searchValue, null, orderBy, pageable);
                    break;
                case "phoneNumber":
                    suppliers = supplierService.findAllSuppliers(null, null, null, searchValue, orderBy, pageable);
                    break;
                default:
                    suppliers = supplierService.findAllSuppliers(null, null, null, null, orderBy, pageable);
                    break;
            }
        } else if (orderBy != null && !orderBy.isEmpty()) {
            suppliers = supplierService.findAllSuppliers(null, null, null, null, orderBy, pageable);
        } else {
            suppliers = supplierService.findAllSuppliers(null, null, null, null, null, pageable);
        }

        return new ResponseEntity<>(suppliers, HttpStatus.OK);
    }



    @PostMapping("/addSupplier")
    public ResponseEntity<?> addSupplier(@RequestBody SupplierDTO supplierDTO) {
        if (supplierDTO == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Supplier supplier = supplierService.findSupplierById(supplierDTO.getSupplierId());
        if (supplier != null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            Map<String, String> errors = supplierValidate.validate(supplierDTO);
            if (errors.isEmpty()) {
                supplierService.addNewSupplier(supplierDTO);
                return new ResponseEntity<>(supplierDTO, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
            }
        }
    }

    @GetMapping("/getSupplierById/{id}")
    public ResponseEntity<?> findSupplierById(@PathVariable String id) {
        Supplier supplier = supplierService.findSupplierById(id);
        if (supplier == null) {
            return new ResponseEntity<>("không tìm thấy nhà cung cấp nào", HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(supplier, HttpStatus.OK);
        }
    }


    @PostMapping("/updateSupplier/{id}")
    public ResponseEntity<?> updateSupplier(@PathVariable("id") String id, @RequestBody SupplierDTO supplierDTO){
        if(supplierService.findSupplierById(id) == null){
            return new ResponseEntity<>("không tìm thấy nhà cung cấp nào",HttpStatus.BAD_REQUEST);
        }
        else {
            Map<String,String> errors = supplierValidate.validate(supplierDTO);
            if(errors.isEmpty()){
                supplierService.editSupplier(supplierDTO, id);
                return new ResponseEntity<>(supplierDTO,HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
            }
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSupplier(@PathVariable("id") String id) {
        Supplier supplier = supplierService.findSupplierById(id);
        if(supplier == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        supplierService.deleteById(id);
        return new ResponseEntity<>(supplier,HttpStatus.NO_CONTENT);

    }
}