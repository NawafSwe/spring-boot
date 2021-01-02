package payroll;
//To wrap your repository with a web layer, you must turn to Spring MVC. Thanks to Spring Boot, there is little in infrastructure to code.
// Instead, we can focus on actions:

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * @RestController indicates that the data returned by each method will be written straight into
 * the response body instead of rendering a template.
 **/
@RestController
public class EmployeeController {
    private final EmployeeRepository repository;

    //An EmployeeRepository is injected by constructor into the controller.
    public EmployeeController(EmployeeRepository repository) {
        this.repository = repository;
    }

    // Aggregate root
    // tag::get-aggregate-root[]
//    @GetMapping("/employees")
//    List<Employee> all() {
//        return repository.findAll();
//    }
//   end::get-aggregate-root[]

    // ------------------------- //

    //  Getting an aggregate root resource
    /**
     * CollectionModel<> is another Spring HATEOAS container; it’s aimed at encapsulating collections of resources—instead of a single resource entity, like EntityModel<> from earlier. CollectionModel<>, too, lets you include links.
     *
     * Don’t let that first statement slip by. What does "encapsulating collections" mean? Collections of employees?
     *
     * Not quite.
     *
     * Since we’re talking REST, it should encapsulate collections of employee resources.
     *
     * That’s why you fetch all the employees, but then transform them into a list of EntityModel<Employee> objects. (Thanks Java 8 Streams!)
     * **/
    @GetMapping("/employees")
    CollectionModel<EntityModel<Employee>> all() {

        List<EntityModel<Employee>> employees = repository.findAll().stream()
                .map(employee -> EntityModel.of(employee,
                        linkTo(methodOn(EmployeeController.class).one(employee.getId())).withSelfRel(),
                        linkTo(methodOn(EmployeeController.class).all()).withRel("employees")))
                .collect(Collectors.toList());

        return CollectionModel.of(employees, linkTo(methodOn(EmployeeController.class).all()).withSelfRel());
    }

    //posting new employee
    @PostMapping("/employees")
    Employee newEmployee(@RequestBody Employee newEmployee) {
        return repository.save(newEmployee);
    }

    //get single employee

    /**
     * This is very similar to what we had before, but a few things have changed:
     * <p>
     * The return type of the method has changed from Employee to EntityModel<Employee>. EntityModel<T> is a generic container from Spring HATEOAS that includes not only the data but a collection of links.
     * <p>
     * linkTo(methodOn(EmployeeController.class).one(id)).withSelfRel() asks that Spring HATEOAS build a link to the EmployeeController 's one() method, and flag it as a self link.
     * <p>
     * linkTo(methodOn(EmployeeController.class).all()).withRel("employees") asks Spring HATEOAS to build a link to the aggregate root, all(), and call it "employees".
     **/
    @GetMapping("/employees/{id}")
    EntityModel<Employee> one(@PathVariable Long id) {

        Employee employee = repository.findById(id) //
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        return EntityModel.of(employee, //
                linkTo(methodOn(EmployeeController.class).one(id)).withSelfRel(),
                linkTo(methodOn(EmployeeController.class).all()).withRel("employees"));
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
