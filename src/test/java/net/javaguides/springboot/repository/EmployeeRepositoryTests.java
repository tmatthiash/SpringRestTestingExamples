package net.javaguides.springboot.repository;

import java.util.List;

import org.assertj.core.api.Assertions;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import net.javaguides.springboot.model.Employee;

@DataJpaTest
public class EmployeeRepositoryTests {

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee employee;

    @BeforeEach
    public void setup() {
        this.employee = Employee.builder()
        .firstName("Matthias")
        .lastName("Holcombe")
        .email("test@email.com")
        .build();
    }
    
    @DisplayName("JUnit test for save employee operation")
    @Test
    public void givenEmployeeOject_whenSave_thenReturnSavedEmployee() {

        Employee savedEmployee = employeeRepository.save(employee);

        Assertions.assertThat(savedEmployee).isNotNull();
        Assertions.assertThat(savedEmployee.getId()).isGreaterThan(0);
    }
    @DisplayName("Test for get all employees operation")
    @Test
    public void givenEmployeesList_whenFindAll_thenEmployeesList() {
        
        Employee employee2 = Employee.builder()
        .firstName("test")
        .lastName("test")
        .email("test2@email.com")
        .build();

        employeeRepository.save(employee);
        employeeRepository.save(employee2);

        List<Employee> returnList = employeeRepository.findAll();

        assertThat(returnList).isNotNull();
        assertThat(returnList.size()).isEqualTo(2);
    }

    @DisplayName("test for get employee by id")
    @Test
    public void givenEmployeeObject_whenFindById_returnEmployeeObject() {

        employeeRepository.save(employee);

        Employee returnEmployee = employeeRepository.findById(employee.getId()).get();

        assertThat(returnEmployee).isNotNull();                
    }

    @DisplayName("test for get employee by email")
    @Test
    public void givenEmployeeObject_whenFindByEmail_returnEmployeeObject() {

        employeeRepository.save(employee);

        Employee returnEmployee = employeeRepository.findByEmail(employee.getEmail()).get();

        assertThat(returnEmployee).isNotNull();  
    }

    @DisplayName("unit test for update employee operation")
    @Test
    public void givenEmployeeObject_whenUpdateEmployee_thenReturnUpdatedEmployee() {

        employeeRepository.save(employee);

        Employee savedEmployee = employeeRepository.findById(employee.getId()).get();
        savedEmployee.setEmail("newEmail@email.com");
        Employee updatedEmployee = employeeRepository.save(savedEmployee);

        assertThat(updatedEmployee.getEmail()).isEqualTo("newEmail@email.com");
    }

    @DisplayName("test for delete employee operation")
    @Test
    public void givenEmployeeObject_whenDeleteEmployee_thenEmployeeIsRemoved() {

        employeeRepository.save(employee);

        employeeRepository.delete(employee);
        
        List<Employee> allEmployees = employeeRepository.findAll();

        assertThat(allEmployees.size()).isEqualTo(0);
    }

    @DisplayName("test for find by first and last name JPQL query")
    @Test
    public void givenEmployeeObject_whenFindByFirstAndLastName_thenReturnEmployee() {

        employeeRepository.save(employee);

        Employee foundEmployee = employeeRepository.findByJPQL("Matthias", "Holcombe");

        assertThat(foundEmployee).isNotNull();
    }

    @DisplayName("test for find by first and last name JPQL w/ named params query")
    @Test
    public void givenEmployeeObject_whenFindByFirstAndLastNameNamedParams_thenReturnEmployee() {

        employeeRepository.save(employee);

        Employee foundEmployee = employeeRepository.findByJPQLNamedParams("Matthias", "Holcombe");

        assertThat(foundEmployee).isNotNull();
    }
    @DisplayName("test for find by first and last name JPQL w/ native SQL")
    @Test
    public void givenEmployeeObject_whenFindByFirstAndLastNameNativeSql_thenReturnEmployee() {

        employeeRepository.save(employee);

        Employee foundEmployee = employeeRepository.findByNativeSQL("Matthias", "Holcombe");

        assertThat(foundEmployee).isNotNull();
    }    
}
