package net.javaguides.springboot.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import net.javaguides.springboot.model.Employee;
import net.javaguides.springboot.repository.EmployeeRepository;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class EmployeeControllerITest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        employeeRepository.deleteAll();
    }

    @Test
    public void givenEmployeeObject_whenCreateEmployee_thenReturnSavedEmployee() throws JsonProcessingException, Exception {

        Employee employee = Employee.builder()
                .firstName("Matthias")
                .lastName("Holcombe")
                .email("test@email.com")
                .build();

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

        employeeRepository.saveAll(employeeList);

        ResultActions response = mockMvc.perform(get("/api/employees"));

        response.andExpect(status().isOk())
            .andExpect(jsonPath("$.size()", is(2)));
    }

    @Test
    public void givenEmployeeId_whenGetEmployeeById_thenReturnEmployee() throws Exception {
        Employee employee = Employee.builder()
        .firstName("Matthias")
        .lastName("Holcombe")
        .email("test@email.com")
        .build();

        employeeRepository.save(employee);

        ResultActions response = mockMvc.perform(get("/api/employees/{id}", employee.getId()));

        response.andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", is("Matthias")))
            .andExpect(jsonPath("$.lastName", is("Holcombe")))
            .andExpect(jsonPath("$.email", is("test@email.com")));
    }

    @Test
    public void givenInvalidEmployeeID_whenGetEmployeeById_thenReturnNotFound() throws Exception {

        ResultActions response = mockMvc.perform(get("/api/employees/{id}", "100000000"));

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
        
        employeeRepository.save(savedEmployee);

        ResultActions response = mockMvc.perform(put("/api/employees/{id}", savedEmployee.getId())
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
        
        ResultActions response = mockMvc.perform(put("/api/employees/{id}", 10000000000l)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updatedEmployee)));

        response.andExpect(status().isNotFound());        
    }


    @Test
    public void givenEmployeeId_whenDeleteEmployee_thenReturnSuccess() throws Exception {
        Employee employee = Employee.builder()
        .firstName("Matthias")
        .lastName("Holcombe")
        .email("test@email.com")
        .build();

        employeeRepository.save(employee);
        ResultActions response = mockMvc.perform(delete("/api/employees/{id}", employee.getId()));
        response.andExpect(status().isOk())
            .andExpect(jsonPath("$", is("Deleted Successfully")));
    }
}
