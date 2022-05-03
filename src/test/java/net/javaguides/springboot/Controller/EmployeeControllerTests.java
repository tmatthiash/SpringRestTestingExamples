package net.javaguides.springboot.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.*;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.javaguides.springboot.model.Employee;
import net.javaguides.springboot.service.EmployeeService;

@WebMvcTest
public class EmployeeControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void givenEmployeeObject_whenCreateEmployee_thenReturnSavedEmployee() throws JsonProcessingException, Exception {

        Employee employee = Employee.builder()
                .firstName("Matthias")
                .lastName("Holcombe")
                .email("test@email.com")
                .build();
 
        given(employeeService.saveEmployee(any(Employee.class)))
                .will((invocation) -> invocation.getArgument(0));

        ResultActions response = mockMvc.perform(post("/api/employees")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(employee)));

        response.andExpect(status().isCreated())
            .andExpect(jsonPath("$.firstName", is("Matthias")))
            .andExpect(jsonPath("$.lastName", is("Holcombe")))
            .andExpect(jsonPath("$.email", is("test@email.com")));
    }

    @Test
    public void givenListOfEmployees_whenGetAllEmployees_thenReturnAllEmployees() throws Exception {
        Employee employee = Employee.builder()
        .firstName("Matthias")
        .lastName("Holcombe")
        .email("test@email.com")
        .build();

        Employee employee2 = Employee.builder()
        .firstName("bob")
        .lastName("bobert")
        .email("test2@email.com")
        .build();

        List<Employee> employeeList = new ArrayList<>();
        employeeList.add(employee);
        employeeList.add(employee2);
        
        given(employeeService.getAllEmployees()).willReturn(employeeList);

        ResultActions response = mockMvc.perform(get("/api/employees"));

        response.andExpect(status().isOk())
            .andExpect(jsonPath("$.size()", is(2)));
    }

    @Test
    public void givenEmployeeId_whenGetEmployeeById_thenReturnEmployee() throws Exception {
        Employee employee = Employee.builder()
        .id(1l)
        .firstName("Matthias")
        .lastName("Holcombe")
        .email("test@email.com")
        .build();

        given(employeeService.getEmployeeById(employee.getId())).willReturn(Optional.of(employee));

        ResultActions response = mockMvc.perform(get("/api/employees/{id}", employee.getId()));

        response.andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", is("Matthias")))
            .andExpect(jsonPath("$.lastName", is("Holcombe")))
            .andExpect(jsonPath("$.email", is("test@email.com")));
    }

    @Test
    public void givenInvalidEmployeeID_whenGetEmployeeById_thenReturnNotFound() throws Exception {
        given(employeeService.getEmployeeById(100l)).willReturn(Optional.empty());

        ResultActions response = mockMvc.perform(get("/api/employees/{id}", "100"));

        response.andExpect(status().isNotFound());

    }

    @Test
    public void givenUpdatedEmployee_whenUpdateEmployee_thenReturnUpdatedEmployee() throws JsonProcessingException, Exception {
        Employee savedEmployee = Employee.builder()
        .firstName("Matthias")
        .lastName("Holcombe")
        .email("test@email.com")
        .build();

        Employee updatedEmployee = Employee.builder()
        .firstName("Bob")
        .lastName("Bobert")
        .email("test2@email.com")
        .build();

        given(employeeService.getEmployeeById(1l)).willReturn(Optional.of(savedEmployee));
        given(employeeService.updateEmployee(any(Employee.class)))
                .will((invocation) -> invocation.getArgument(0));

        ResultActions response = mockMvc.perform(put("/api/employees/{id}", 1l)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updatedEmployee)));

        response.andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", is("Bob")))
            .andExpect(jsonPath("$.lastName", is("Bobert")))
            .andExpect(jsonPath("$.email", is("test2@email.com")));
    }

    @Test
    public void givenInvalidEmployeeId_whenGetEmployeeById_thenReturnNotFound() throws JsonProcessingException, Exception {
        
        Employee updatedEmployee = Employee.builder()
        .firstName("Bob")
        .lastName("Bobert")
        .email("test2@email.com")
        .build();
        
        given(employeeService.getEmployeeById(1l)).willReturn(Optional.empty());
        ResultActions response = mockMvc.perform(put("/api/employees/{id}", 1l)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updatedEmployee)));

        response.andExpect(status().isNotFound());        
    }

    @Test
    public void givenEmployeeId_whenDeleteEmployee_thenReturnSuccess() throws Exception {
        willDoNothing().given(employeeService).deleteEmployee(1l);

        ResultActions response = mockMvc.perform(delete("/api/employees/{id}", 1l));
        response.andExpect(status().isOk())
            .andExpect(jsonPath("$", is("Deleted Successfully")));
    }
}
