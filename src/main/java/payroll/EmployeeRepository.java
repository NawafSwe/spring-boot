package payroll;


import com.sun.xml.bind.v2.model.core.ID;
import org.springframework.data.jpa.repository.JpaRepository;
import payroll.Employee;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repositories are interfaces with methods supporting
 * creating, reading, updating, and deleting records against a back end data store.
 * Some repositories also support data paging, and sorting, where appropriate.
 * Spring Data synthesizes implementations based on conventions found in the naming of the methods in the interface.
 * ---------------------------------------------------------------
 * There are multiple repository implementations besides JPA. You can use Spring Data MongoDB, Spring Data GemFire, Spring Data Cassandra, etc. For this tutorial, we’ll stick with JPA.
 * Spring makes accessing data easy. By simply declaring the following EmployeeRepository interface we automatically will be able to
 * <p>
 * Create new Employees
 * <p>
 * Update existing ones
 * <p>
 * Delete Employees
 * <p>
 * Find Employees (one, all, or search by simple or complex properties)
 **/
//To get all this free functionality, all we had to do was declare an interface which extends Spring Data JPA’s JpaRepository,
//specifying the domain type as Employee and the id type as Long.
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    // saving employee
    Employee save(Employee employee);

    //find employee by id
    Optional<Employee> findById(ID primaryKey);

    // find all employees
    List<Employee> findAll();

    // count number of employee
    long count();

    // delete employee
    void delete(Employee employee);

    // checking if employee exists by id
    boolean existsById(ID primaryKey);


}
