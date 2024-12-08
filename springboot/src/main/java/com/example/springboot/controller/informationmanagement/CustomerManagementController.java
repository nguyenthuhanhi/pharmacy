package com.codegym.a0223i1_pharmacy_professional_be.controller.informationmanagement;

import com.codegym.a0223i1_pharmacy_professional_be.dto.AccountRoleDTO;
import com.codegym.a0223i1_pharmacy_professional_be.dto.CustomerDTO;
import com.codegym.a0223i1_pharmacy_professional_be.dto.IInvoiceDTO;
import com.codegym.a0223i1_pharmacy_professional_be.entity.Account;
import com.codegym.a0223i1_pharmacy_professional_be.entity.Customer;
import com.codegym.a0223i1_pharmacy_professional_be.repository.customermanagement.ICustomerRepository;
import com.codegym.a0223i1_pharmacy_professional_be.service.interfaceservice.accountmanagement.IAccountService;
import com.codegym.a0223i1_pharmacy_professional_be.service.interfaceservice.customermanagement.ICustomerService;
import com.codegym.a0223i1_pharmacy_professional_be.validate.CustomerValidate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*")
@RequestMapping("/api/customer")
public class CustomerManagementController {
    //Quản lý khách hàng
    @Autowired
    private ICustomerService iCustomerService;
    @Autowired
    private ICustomerRepository iCustomerRepository;

    @Autowired
    private IAccountService iAccountService;
    @Autowired
    private CustomerValidate customerValidate;

    @GetMapping("/lists")
    public ResponseEntity<?> getAllCustomer(@RequestParam(value = "page", defaultValue = "10") Integer page) {
        Page<Customer> customers = iCustomerService.getAllCustomer(Pageable.ofSize(page));
        if (customers.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }

    //    @GetMapping("/list")
//    public ResponseEntity<Page<?>> getAllCustomer(@RequestParam String sortOption,
//                                                  @RequestParam String searchType,
//                                                  @RequestParam String searchValue,
//                                                  Pageable pageable) {
//        Page<Customer> customers = null;
//        if (searchType != null && !searchType.isEmpty() && searchValue != null && !searchValue.isEmpty()) {
//            switch (searchType) {
//                case "customerId":
//                    customers = iCustomerService.getCustomerById(searchValue, pageable);
//                    break;
//                case "customerName":
//                    customers = iCustomerService.getAllCustomerByName(searchValue, pageable);
//                    break;
//                case "customerType":
//                    customers = iCustomerService.getAllCustomerByType(searchValue, pageable);
//                    break;
//                case "address":
//                    customers = iCustomerService.getCustomerByAddress(searchValue, pageable);
//                    break;
//                case "phoneNumber":
//                    customers = iCustomerService.getAllCustomerByPhoneNumber(searchValue, pageable);
//                    break;
//            }
//        } else if (sortOption != null && !sortOption.isEmpty()) {
//            switch (sortOption) {
//                case "customerId":
//                    customers = iCustomerService.getAllOrderByCustomerId(pageable);
//                    break;
//                case "customerName":
//                    customers = iCustomerService.getAllOrderByCustomerName(pageable);
//                    break;
//                case "address":
//                    customers = iCustomerService.getAllOrderByCustomerAddress(pageable);
//                    break;
//                case "customerType":
//                    customers = iCustomerService.getAllOrderByCustomerType(pageable);
//                    break;
//                case "phoneNumber":
//                    customers = iCustomerService.getAllOrderByPhoneNumber(pageable);
//                    break;
//            }
//        } else {
//            customers = iCustomerService.getAllOrderByCustomerId(pageable);
//        }
//        return new ResponseEntity<>(customers, HttpStatus.OK);
//    }
    @GetMapping("/list")
    public ResponseEntity<Page<Customer>> getAllCustomers(
            @RequestParam(required = false) String sortOption,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String searchValue,
            Pageable pageable) {
        Page<Customer> customers;
        if (searchType != null && !searchType.isEmpty() && searchValue != null && !searchValue.isEmpty()) {
            switch (searchType) {
                case "customerId":
                    customers = iCustomerRepository.findAllCustomerWithSort(searchValue, null, null, null, null, sortOption, pageable);
                    break;
                case "customerName":
                    customers = iCustomerRepository.findAllCustomerWithSort(null, searchValue, null, null, null, sortOption, pageable);
                    break;
                case "address":
                    customers = iCustomerRepository.findAllCustomerWithSort(null, null, searchValue, null, null, sortOption, pageable);
                    break;
                case "phoneNumber":
                    customers = iCustomerRepository.findAllCustomerWithSort(null, null, null, searchValue, null, sortOption, pageable);
                    break;
                case "customerType":
                    customers = iCustomerRepository.findAllCustomerWithSort(null, null, null, null, searchValue, sortOption, pageable);
                    break;
                default:
                    customers = iCustomerRepository.findAllCustomerWithSort(null, null, null, null, null, sortOption, pageable);
                    break;
            }
        } else if (sortOption != null && !sortOption.isEmpty()) {
            customers = iCustomerRepository.findAllCustomerWithSort(null, null, null, null, null, sortOption, pageable);
        } else {
            customers = iCustomerRepository.findAllCustomerWithSort(null, null, null, null, null, null, pageable);
        }
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }


    @GetMapping("/listPhone")
    public ResponseEntity<?> getAllPhoneNumber() {
        List<String> phones = iCustomerService.getAllPhoneNumber();
        if (phones.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(phones, HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable String id) {
        Customer customer = iCustomerService.getCustomerById(id);
        if (customer == null) {
            return new ResponseEntity<>("Khách hàng không tồn tại", HttpStatus.BAD_REQUEST);
        }
        iCustomerService.deleteCustomer(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/getCustomerById/{id}")
    public ResponseEntity<?> findCustomerById(@PathVariable String id) {
        Customer customer = iCustomerService.getCustomerById(id);
        if (customer == null) {
            return new ResponseEntity<>("Khách hàng không tồn tại", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(customer, HttpStatus.OK);
    }


    @GetMapping(value = "/getAllInvoiceCustomer")
    public ResponseEntity<?> getAllInvoiceCustomer(@RequestParam(defaultValue = "") String id,
                                                   @RequestParam(required = false) String startDay,
                                                   @RequestParam(required = false) String endDay,
                                                   @RequestParam(required = false) String startHour,
                                                   @RequestParam(required = false) String endHour, Pageable pageable) {

        Page<IInvoiceDTO> invoice = iCustomerService.getAllInvoiceCustomer(id, startDay, endDay, startHour, endHour, pageable);
        if (invoice == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(invoice, HttpStatus.OK);
    }

    @PostMapping("/createCustomer")
    public ResponseEntity<?> createCustomer(@RequestBody CustomerDTO customerDTO) {
        if (customerDTO == null) {
            return new ResponseEntity<CustomerDTO>(HttpStatus.BAD_REQUEST);
        } else {
            Map<String, String> errors = customerValidate.validate(customerDTO);
            if (errors.isEmpty()) {
                Account account = new Account();
                account.setPassword("123");
                account.setDeleteFlag(false);
                account = iAccountService.registerAccount(account);
                customerDTO.setAccountId(account.getAccountId());

                AccountRoleDTO accountRoleDTO = new AccountRoleDTO();
                accountRoleDTO.setAccountId(account.getAccountId());
                accountRoleDTO.setRoleId(3);
                iCustomerService.createCustomer(customerDTO);
                return new ResponseEntity<>(customerDTO, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
            }
        }
    }


    @PostMapping("/updateCustomer")
    public ResponseEntity<?> updateCustomer(@RequestBody CustomerDTO customerDTO) {
        if (iCustomerService.findCustomerById(customerDTO.getCustomerId()) == null) {
            return new ResponseEntity<>("không tìm thấy khách hàng nào", HttpStatus.BAD_REQUEST);
        } else {
            Map<String, String> errors = customerValidate.validate(customerDTO);
            if (errors.isEmpty()) {
                iCustomerService.updateCustomer(customerDTO);
                return new ResponseEntity<>(customerDTO, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
            }
        }
    }
}
