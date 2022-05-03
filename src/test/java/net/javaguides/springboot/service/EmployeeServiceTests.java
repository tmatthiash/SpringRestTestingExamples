package net.javaguides.springboot.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import net.javaguides.springboot.exception.ResourceNotFoundException;
import net.javaguides.springboot.model.Employee;
import net.javaguides.springboot.repository.EmployeeRepository;
import net.javaguides.springboot.service.impl.EmployeeServiceImpl;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTests {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;

    @BeforeEach
    public void setup() {

        this.employee = Employee.builder()
                .id(1l)
                .firstName("Matthias")
                .lastName("Holcombe")
                .email("test@email.com")
                .build();

        // employeeRepository = Mockito.mock(EmployeeRepository.class);
        // employeeService = new EmployeeServiceImpl(employeeRepository);
    };

    @DisplayName("test for save employee method")
    @Test
    public void givenEmployeeObject_whenSaveEmployee_thenReturnEmployee() {

        given(employeeRepository.findByEmail(employee.getEmail()))
                .willReturn(Optional.empty());

        given(employeeRepository.save(employee))
                .willReturn(employee);

        Employee returnedEmployee = employeeService.saveEmployee(employee);

        assertThat(returnedEmployee).isNotNull();
    }

    @DisplayName("test for save employee method with email already exists exception")
    @Test
    public void givenEmployeeWithExistingEmailObject_whenSaveEmployee_thenException() {

        given(employeeRepository.findByEmail(employee.getEmail()))
                .willReturn(Optional.of(employee));

        org.junit.jupiter.api.Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            employeeService.saveEmployee(employee);
        });

        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @DisplayName("test for get all employees")
    @Test
    public void givenEmployeesList_whenGetAllEmployees_thenReturnAll() {

        Employee employee2 = Employee.builder()
                .id(2l)
                .firstName("bob")
                .lastName("bobert")
                .email("test2@email.com")
                .build();

        given(employeeRepository.findAll()).willReturn(List.of(employee, employee2));

        List<Employee> returnList = employeeService.getAllEmployees();

        assertThat(returnList.size()).isEqualTo(2);
    }

    @DisplayName("test for get all employees")
    @Test
    public void givenEmptyEmployeesList_whenGetAllEmployees_thenReturnEmpty() {

        given(employeeRepository.findAll()).willReturn(Collections.emptyList());

        List<Employee> returnList = employeeService.getAllEmployees();

        assertThat(returnList.size()).isEqualTo(0);
    }

    @DisplayName("test for get employee by id")
    @Test
    public void givenEmployeeId_whenGetById_thenReturnEmployeeObject() {

        given(employeeRepository.findById(employee.getId())).willReturn(Optional.of(employee));

        Optional<Employee> returnEmployee = employeeService.getEmployeeById(employee.getId());

        assertThat(returnEmployee).isPresent();
    }

    @DisplayName("test for update employee")
    @Test
    public void givenEmployee_whenUpdate_thenReturnUpdatedEmployee() {
        given(employeeRepository.save(employee)).willReturn(employee);
        employee.setEmail("email2@test.com");
        employee.setFirstName("bob");

        Employee updatedEmployee = employeeService.updateEmployee(employee);

        assertThat(updatedEmployee.getEmail()).isEqualTo("email2@test.com");
        assertThat(updatedEmployee.getFirstName()).isEqualTo("bob");
    }

    @DisplayName("test for delete employee")
    @Test
    public void givenEmployeeId_whenDelete_thenDontReturnAnything() {
        BDDMockito.willDoNothing().given(employeeRepository).deleteById(employee.getId());

        employeeService.deleteEmployee(employee.getId());

        verify(employeeRepository, times(1)).deleteById(employee.getId());
    }
}
