package payroll;
//To wrap your repository with a web layer, you must turn to Spring MVC. Thanks to Spring Boot, there is little in infrastructure to code.
// Instead, we can focus on actions:

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
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
    private final EmployeeModelAssembler assembler;

    //An EmployeeRepository is injected by constructor into the controller.
    //inject the EmployeeAssembler
    public EmployeeController(EmployeeRepository repository, EmployeeModelAssembler assembler) {

        this.repository = repository;
        this.assembler = assembler;
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
     * <p>
     * Don’t let that first statement slip by. What does "encapsulating collections" mean? Collections of employees?
     * <p>
     * Not quite.
     * <p>
     * Since we’re talking REST, it should encapsulate collections of employee resources.
     * <p>
     * That’s why you fetch all the employees, but then transform them into a list of EntityModel<Employee> objects. (Thanks Java 8 Streams!)
     **/
    @GetMapping("/employees")
    CollectionModel<EntityModel<Employee>> all() {
//The code is, again, almost the same, however you get to replace all that EntityModel<Employee> creation logic with map(assembler::toModel). Thanks to Java 8 method references, it’s super easy to plug it in and simplify your controller.
//A key design goal of Spring HATEOAS is to make it easier to do The Right Thing™. In this scenario: adding hypermedia to your service without hard coding a thing.
        List<EntityModel<Employee>> employees = repository.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(employees, linkTo(methodOn(EmployeeController.class).all()).withSelfRel());
    }

//    //posting new employee
//    @PostMapping("/employees")
//    Employee newEmployee(@RequestBody Employee newEmployee) {
//        return repository.save(newEmployee);
//    }
//
//

    //    Another step in the right direction involves ensuring that each of your REST methods returns a proper response. Update the POST method like this:
//
//    POST that handles "old" and "new" client requests where above code is old endpoint
    @PostMapping("/employees")
    ResponseEntity<?> newEmployee(@RequestBody Employee employee) {
        EntityModel<Employee> entityModel = assembler.toModel(repository.save(employee));
        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }
// above , postMapping ---> The new Employee object is saved as before. But the resulting object is wrapped using the EmployeeModelAssembler.
//
//Spring MVC’s ResponseEntity is used to create an HTTP 201 Created status message. This type of response typically includes a Location response header, and we use the URI derived from the model’s self-related link.
//
//Additionally, return the model-based version of the saved object.
//
//With these tweaks in place, you can use the same endpoint to create a new employee resource, and use the legacy name field


    /**
     * get single employee
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

        return assembler.toModel(employee);
    }

    /*
     this is whole replace for obj
     if value did not exist within the request body will cause an error
     try to use optionals or other solution like checking which data is exists  within the body
    */

    // old end point
//    @PutMapping("/employees/{id}")
//    Employee updatedEmployee(@RequestBody Employee updatedEmployee, @PathVariable Long id) {
//        return repository.findById(id)
//                .map(employee -> {
//                    //setting employee name
//                    employee.setName(updatedEmployee.getFirstName() + " " + updatedEmployee.getLastName());
//                    employee.setRole(updatedEmployee.getRole());
//                    employee.setId(updatedEmployee.getId());
//                    return repository.save(employee);
//                })
//                .orElseGet(() -> {
//                    updatedEmployee.setId(id);
//                    return repository.save(updatedEmployee);
//                });
//    }

    // ----------- after adopting new endpoint ----------------

    //The Employee object built from the save() operation is then wrapped using the EmployeeModelAssembler into an EntityModel<Employee> object. Using the getRequiredLink() method, you can retrieve the Link created by the EmployeeModelAssembler with a SELF rel. This method returns a Link which must be turned into a URI with the toUri method.
    //
    //Since we want a more detailed HTTP response code than 200 OK, we will use Spring MVC’s ResponseEntity wrapper.
    // It has a handy static method created() where we can plug in the resource’s URI.
    // It’s debatable if HTTP 201 Created carries the right semantics since we aren’t necessarily "creating" a new resource.
    // But it comes pre-loaded with a Location response header, so run with it.
    @PutMapping("/employees/{id}")
    ResponseEntity<?> updatedEmployee(@RequestBody Employee employee, @PathVariable Long id) {
        Employee updatedEmployee = repository.findById(id).map(employee1 -> {
            employee1.setName(employee.getName());
            employee1.setRole(employee.getRole());
            return repository.save(employee1);
        }).orElseGet(() -> {
            // if not found crete one with id
            employee.setId(id);
            return repository.save(employee);
        });
        EntityModel<Employee> entityModel = assembler.toModel(updatedEmployee);
        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

//    @DeleteMapping("employees/{id}")
//    void deletedEmployee(@PathVariable Long id) {
//        repository.deleteById(id);
//    }

    // updating deleteMapping
    @DeleteMapping("employees/{id}")
    ResponseEntity<?> deletedEmployee(@RequestBody Employee deletedEmployee, @PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
        //This returns an HTTP 204 No Content response.
    }
}
