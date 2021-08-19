package com.example.demo.web;

import com.example.demo.emploee.domain.Employee;
import com.example.demo.emploee.model.EmployeeState;
import com.example.demo.exception.EmployeeNotFoundException;
import com.example.demo.exception.UnexpectedEmployeeStateException;
import com.example.demo.web.model.CreateEmployeePayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

import static com.example.demo.emploee.model.EmployeeEvent.TO_CHECK;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

  private final ObjectMapper mapper = new ObjectMapper();
  private final Employee employee;

  private MockMvc mockMvc;

  @MockBean
  private EmployeeService employeeService;

  public EmployeeControllerTest() {
    employee = new Employee();
    employee.setName("Jane");
    employee.setSurname("Fox");
    employee.setBirthDate(Instant.now());
    employee.setContractNumber("C-1");
    employee.setState(EmployeeState.ADDED);
    employee.setId(0L);
  }

  @BeforeEach
  void setUp(WebApplicationContext context) {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
        .build();

    when(employeeService.create(any())).thenReturn(employee);
    doThrow(new EmployeeNotFoundException(2L)).when(employeeService).manage(2L, TO_CHECK);
    doThrow(new UnexpectedEmployeeStateException(3L, TO_CHECK)).when(employeeService).manage(3L, TO_CHECK);
  }

  @Test
  void testCreateEmployee() throws Exception {
    this.mockMvc.perform(post("/v1/employee")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsBytes(CreateEmployeePayload.of("Jane", "Fox"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.name").value("Jane"))
        .andExpect(jsonPath("$.surname").value("Fox"))
        .andExpect(jsonPath("$.contractNumber").value("C-1"))
        .andExpect(jsonPath("$.state").value("ADDED"));
  }

  @Test
  void testCreateEmployeeRequiredParamsNull() throws Exception {
    this.mockMvc.perform(post("/v1/employee")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsBytes(Collections.emptyMap())))
        .andExpect(status().isBadRequest());

    this.mockMvc.perform(post("/v1/employee")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsBytes(Map.of("name", "Jane"))))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testCheckEmployee() throws Exception {
    this.mockMvc.perform(post("/v1/employee/{id}/manage", "1")
            .param("event", TO_CHECK.name()))
        .andExpect(status().isOk());
  }

  @Test
  void testCheckEmployeeNotFoundById() throws Exception {
    this.mockMvc.perform(post("/v1/employee/{id}/manage", "2")
            .param("event", TO_CHECK.name()))
        .andExpect(status().isNotFound());
  }

  @Test
  void testCheckEmployeeIncorrectState() throws Exception {
    this.mockMvc.perform(post("/v1/employee/{id}/manage", "3")
            .param("event", TO_CHECK.name()))
        .andExpect(status().isUnprocessableEntity());
  }
}
