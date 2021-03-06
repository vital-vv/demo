package com.example.demo.server.web;

import com.example.avro.Action;
import com.example.demo.server.model.EmployeeCreatePayload;
import com.example.demo.server.model.EmployeePayload;
import com.example.demo.server.services.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/v1/employee", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class EmployeeController {

  private final EmployeeService service;

  @Operation(summary = "Create a new employee", responses = {
      @ApiResponse(responseCode = "200", description = "Successful Operation"),
      @ApiResponse(responseCode = "400", description = "Invalid employee params", content = @Content),
  })
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public EmployeePayload create(@RequestBody @Valid EmployeeCreatePayload payload) {
    return service.create(payload);
  }

  @Operation(summary = "Get all employees")
  @GetMapping()
  public List<EmployeePayload> employees() {
    return service.getAll();
  }

  @Operation(summary = "Get an employee by identifier", responses = {
      @ApiResponse(responseCode = "200", description = "Successful Operation"),
      @ApiResponse(responseCode = "404", description = "Employee not found by id")
  })
  @GetMapping("{id}")
  public EmployeePayload employeeById(@PathVariable("id") Long id) {
    return service.getById(id);
  }

  @Operation(summary = "Update employee state", responses = {
      @ApiResponse(responseCode = "200", description = "Successful Operation"),
      @ApiResponse(responseCode = "400", description = "Invalid request params"),
      @ApiResponse(responseCode = "404", description = "Employee not found by id")
  })
  @PostMapping("{id}/manage")
  public void manage(@PathVariable("id") Long id, @RequestParam("action") Action action) {
    service.manage(id, action);
  }
}
