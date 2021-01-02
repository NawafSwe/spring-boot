package payroll;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;

// @Entity is a JPA annotation to make this object ready for storage in a JPA-based data store.
@Entity
public class Employee {
    // generating id with generated value;
    // @id is marked with more JPA annotations to indicate it’s the primary key and automatically populated by the JPA provider.
    @Id
    @GeneratedValue
    Long id;
    String name;
    String role;

    public Employee() {
    }

    public Employee(String name, String role) {
        this.name = name;
        this.role = role;
    }

    public Long getId() {
        return this.id;
    }

    public String getRole() {
        return this.role;
    }

    public String getName() {
        return this.name;
    }

    public void setId(Long primaryKey) {
        this.id = primaryKey;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Employee)) {
            return false;
        }
        Employee tempEmployee = (Employee) o;
        return Objects.equals(this.id, tempEmployee.id) && Objects.equals(this.name, tempEmployee.name) && Objects.equals(this.role, tempEmployee.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.name, this.role);
    }

    @Override
    public String toString() {
        return "Employee{" + "id=" + this.id + ", name='" + this.name + '\'' + ", role='" + this.role + '\'' + '}';
    }
}
