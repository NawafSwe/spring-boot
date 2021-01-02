package payroll;
//To wrap your repository with a web layer, you must turn to Spring MVC. Thanks to Spring Boot, there is little in infrastructure to code.
// Instead, we can focus on actions:

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmployeeController {
    private final EmployeeRepository repository;

    public EmployeeController(EmployeeRepository repository) {
        this.repository = repository;
    }

    // Aggregate root
    // tag::get-aggregate-root[]
    @GetMapping("/employees")
    List<Employee> all() {
        return repository.findAll();
    }
    // end::get-aggregate-root[]

    //posting new employee
    @PostMapping("/employees")
    Employee newEmployee(@RequestBody Employee newEmployee) {
        return repository.save(newEmployee);
    }

    //get single employee
    @GetMapping("/employees/{id}")
    Employee getEmployee(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    @PutMapping("/employees/{id}")
    Employee updatedEmployee(@RequestBody Employee updatedEmployee, @PathVariable Long id) {
        return repository.findById(id)
                .map(employee -> {
                    employee.setName(updatedEmployee.name);
                    employee.setRole(updatedEmployee.role);
                    employee.setId(updatedEmployee.id);
                    return repository.save(employee);
                })
                .orElseGet(() -> {
                    updatedEmployee.setId(id);
                    return repository.save(updatedEmployee);
                });
    }

    @DeleteMapping("employees/{id}")
    void deletedEmployee(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
