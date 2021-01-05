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
    private @Id
    @GeneratedValue
    Long id;
    private String role;
    // -- after upgrading server  --
    private String firstName;
    private String lastName;


    public Employee() {
    }

//    public Employee(String name, String role) {
//        this.name = name;
//        this.role = role;
//    }

    // -- after upgrading server --
    //This class is very similar to the previous version of Employee. Let’s go over the changes:
    //
    //Field name has been replaced by firstName and lastName.
    //
    //A "virtual" getter for the old name property, getName() is defined. It uses the firstName and lastName fields to produce a value.
    //
    //A "virtual" setter for the old name property is also defined, setName(). It parses an incoming string and stores it into the proper fields.

    //Of course not EVERY change to your API is as simple as splitting a string or merging two strings. But it’s surely not impossible to come up with a set of transforms for most scenarios, right?
    public Employee(String firstName, String lastName, String role) {
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Long getId() {
        return this.id;
    }

    public String getRole() {
        return this.role;
    }

    public String getName() {

        return this.firstName + " " + this.lastName;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setId(Long primaryKey) {
        this.id = primaryKey;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setName(String name) {
        String[] names = name.split(" ");
        this.firstName = names[0];
        this.lastName = names[1];

    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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
        return Objects.equals(this.id, tempEmployee.id) && Objects.equals(this.role, tempEmployee.role)
                && Objects.equals(this.firstName, tempEmployee.firstName) && Objects.equals(this.lastName, tempEmployee.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.firstName, this.lastName, this.role);
    }

    @Override
    public String toString() {
        return "Employee{" + "id=" + this.id + ", full name='" + this.getName() + '\'' + ", role='" + this.role + '\'' + '}';
    }
}
