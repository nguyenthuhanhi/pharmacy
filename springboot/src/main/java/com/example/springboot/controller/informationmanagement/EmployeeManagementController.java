package com.codegym.a0223i1_pharmacy_professional_be.controller.informationmanagement;

import com.codegym.a0223i1_pharmacy_professional_be.dto.EmployeeDto;
import com.codegym.a0223i1_pharmacy_professional_be.dto.EmployeeDtoCreateUpdate;
import com.codegym.a0223i1_pharmacy_professional_be.entity.Account;
import com.codegym.a0223i1_pharmacy_professional_be.entity.AccountRole;
import com.codegym.a0223i1_pharmacy_professional_be.entity.Employee;
import com.codegym.a0223i1_pharmacy_professional_be.entity.Role;
import com.codegym.a0223i1_pharmacy_professional_be.service.interfaceservice.accountmanagement.IAccountRoleService;
import com.codegym.a0223i1_pharmacy_professional_be.service.interfaceservice.accountmanagement.IAccountService;
import com.codegym.a0223i1_pharmacy_professional_be.service.interfaceservice.accountmanagement.IRoleService;
import com.codegym.a0223i1_pharmacy_professional_be.service.interfaceservice.informationmanagement.IEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/employee")
public class EmployeeManagementController {
    @Autowired
    IEmployeeService iEmployeeService;
    @Autowired
    IRoleService iRoleService;
    @Autowired
    IAccountRoleService iAccountRoleService;
    @Autowired
    IAccountService iAccountService;
    @GetMapping("/listSearchName1")
    public ResponseEntity<List<EmployeeDto>> findAllEmployee1(@RequestParam(required = false, defaultValue = "") String fill){
        List<EmployeeDto> employeeDtoList = iEmployeeService.findAllEmployee1(fill);
        if(employeeDtoList.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(employeeDtoList,HttpStatus.OK);
    }
    @GetMapping("/listSearchName2")
    public ResponseEntity<List<EmployeeDto>> findAllEmployee2(@RequestParam(required = false, defaultValue = "") String fill){
        List<EmployeeDto> employeeDtoList = iEmployeeService.findAllEmployee2(fill);
        if(employeeDtoList.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(employeeDtoList,HttpStatus.OK);
    }
    @GetMapping("/listSearchName3")
    public ResponseEntity<List<EmployeeDto>> findAllEmployee3(@RequestParam(required = false, defaultValue = "") String fill){
        List<EmployeeDto> employeeDtoList = iEmployeeService.findAllEmployee3(fill);
        if(employeeDtoList.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(employeeDtoList,HttpStatus.OK);
    }
    @GetMapping("/listSearchName4")
    public ResponseEntity<List<EmployeeDto>> findAllEmployee4(@RequestParam(required = false, defaultValue = "") String fill){
        List<EmployeeDto> employeeDtoList = iEmployeeService.findAllEmployee4(fill);
        if(employeeDtoList.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(employeeDtoList,HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<EmployeeDto>> findAll(){
        List<EmployeeDto> employeeList = iEmployeeService.findAll();
        if(employeeList.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(employeeList,HttpStatus.OK);
    }
    @PostMapping("/create")
    public ResponseEntity<Employee> create(@RequestBody EmployeeDtoCreateUpdate employeeDtoCreateUpdate){
        if(employeeDtoCreateUpdate == null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        Account account = new Account();
        account.setEmail(employeeDtoCreateUpdate.getEmail());
        account.setPassword(employeeDtoCreateUpdate.getPassword());
        account.setDeleteFlag(false);
        iAccountService.save(account);

        Employee employee = new Employee();
        employee.setEmployeeId(employeeDtoCreateUpdate.getEmployee_id());
        employee.setEmployeeName(employeeDtoCreateUpdate.getEmployee_name());
        employee.setPhoneNumber(employeeDtoCreateUpdate.getPhone_number());
        employee.setDateStart(employeeDtoCreateUpdate.getDate_start());
        employee.setAddress(employeeDtoCreateUpdate.getAddress());
        employee.setNote(employeeDtoCreateUpdate.getNote());
        employee.setSalary(employeeDtoCreateUpdate.getSalary());
        employee.setImage(employeeDtoCreateUpdate.getImage());
        employee.setAccount(account);

        iEmployeeService.save(employee);

        Role role = iRoleService.findById(employeeDtoCreateUpdate.getRole_id());

        AccountRole accountRole = new AccountRole();
        accountRole.setAccount(account);
        accountRole.setRole(role);
        iAccountRoleService.save(accountRole);

        return new ResponseEntity<>(employee,HttpStatus.OK);
    }
    @GetMapping("/{employeeId}")
    public ResponseEntity<EmployeeDto> detail(@PathVariable String employeeId){
        EmployeeDto employee = iEmployeeService.findById(employeeId);
        if(employee == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(employee,HttpStatus.OK);
    }
    @PutMapping("/{employeeId}")
    public ResponseEntity<EmployeeDto> update(@PathVariable String employeeId, @RequestBody EmployeeDtoCreateUpdate employeeDtoCreateUpdate){
        EmployeeDto employeedto = iEmployeeService.findById(employeeId);
        if(employeedto == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Employee employee = new Employee();
        employee.setEmployeeId(employeeDtoCreateUpdate.getEmployee_id());
        employee.setEmployeeName(employeeDtoCreateUpdate.getEmployee_name());
        employee.setPhoneNumber(employeeDtoCreateUpdate.getPhone_number());
        employee.setDateStart(employeeDtoCreateUpdate.getDate_start());
        employee.setAddress(employeeDtoCreateUpdate.getAddress());
        employee.setNote(employeeDtoCreateUpdate.getNote());
        employee.setSalary(employeeDtoCreateUpdate.getSalary());
        employee.setImage(employeeDtoCreateUpdate.getImage());
        Account account = iAccountService.findById(employeeDtoCreateUpdate.getAccount_id());
        employee.setAccount(account);
        iEmployeeService.update(employee);

        iEmployeeService.updateEmail(employeeDtoCreateUpdate.getAccount_id(),employeeDtoCreateUpdate.getEmail());

        iEmployeeService.updateRoleId(employeeDtoCreateUpdate.getAccount_id(),employeeDtoCreateUpdate.getRole_id());

        return new ResponseEntity<>(employeedto,HttpStatus.OK);
    }
    @DeleteMapping("/{employeeId}")
    public ResponseEntity<EmployeeDto> delete(@PathVariable String employeeId){
        EmployeeDto employee = iEmployeeService.findById(employeeId);
        if(employee == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        iEmployeeService.delete(employeeId);
        return new ResponseEntity<>(employee,HttpStatus.NO_CONTENT);
    }
}
