package com.example.demo.server.web;

import com.example.demo.server.EmployeeCreatePayloads;
import com.example.demo.server.domain.Employee;
import com.example.demo.server.exception.EmployeeNotFoundException;
import com.example.demo.server.model.EmployeePayload;
import com.example.demo.server.services.EmployeeService;
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
import java.util.List;
import java.util.Map;

import static com.example.avro.Action.TO_CHECK;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

  private final ObjectMapper mapper = new ObjectMapper();
  private final EmployeePayload employee;

  private MockMvc mockMvc;

  @MockBean
  private EmployeeService employeeService;

  public EmployeeControllerTest() {
    employee = new EmployeePayload(0L, "Jane", "Fox", Instant.now(),
        null, null, "C-1", Employee.State.ADDED);
  }

  @BeforeEach
  void setUp(WebApplicationContext context) {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
        .build();

    when(employeeService.create(any())).thenReturn(employee);
    when(employeeService.getById(1L)).thenReturn(employee);
    when(employeeService.getById(2L)).thenThrow(new EmployeeNotFoundException(2L));
    when(employeeService.getAll()).thenReturn(List.of(employee));
    doThrow(new EmployeeNotFoundException(2L)).when(employeeService).manage(2L, TO_CHECK);
  }

  @Test
  void testCreateEmployee() throws Exception {
    this.mockMvc.perform(post("/v1/employee")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsBytes(EmployeeCreatePayloads.of("Jane", "Fox"))))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
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
  void testGetEmployeeById() throws Exception {
    this.mockMvc.perform(get("/v1/employee/{id}", "1"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.name").value("Jane"))
        .andExpect(jsonPath("$.surname").value("Fox"))
        .andExpect(jsonPath("$.contractNumber").value("C-1"))
        .andExpect(jsonPath("$.state").value("ADDED"));
  }

  @Test
  void testGetEmployeeByIdNotFound() throws Exception {
    this.mockMvc.perform(get("/v1/employee/{id}", "2"))
        .andExpect(status().isNotFound());
  }

  @Test
  void testGetAllEmployees() throws Exception {
    this.mockMvc.perform(get("/v1/employee"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].id").isNotEmpty())
        .andExpect(jsonPath("$[0].name").value("Jane"))
        .andExpect(jsonPath("$[0].surname").value("Fox"))
        .andExpect(jsonPath("$[0].contractNumber").value("C-1"))
        .andExpect(jsonPath("$[0].state").value("ADDED"));
  }

  @Test
  void testCheckEmployee() throws Exception {
    this.mockMvc.perform(post("/v1/employee/{id}/manage", "1")
            .param("action", TO_CHECK.name()))
        .andExpect(status().isOk());
  }

  @Test
  void testCheckEmployeeNotFoundById() throws Exception {
    this.mockMvc.perform(post("/v1/employee/{id}/manage", "2")
            .param("action", TO_CHECK.name()))
        .andExpect(status().isNotFound());
  }
}
