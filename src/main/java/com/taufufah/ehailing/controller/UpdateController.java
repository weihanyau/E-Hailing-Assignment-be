package com.taufufah.ehailing.controller;

import com.taufufah.ehailing.dto.DriverCustomerDto;
import com.taufufah.ehailing.service.UpdateService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UpdateController {
    private final UpdateService updateService;

    public UpdateController(UpdateService updateService) {
        this.updateService = updateService;
    }

    @PostMapping("/distance")
    Integer getShortestPathTime(@RequestBody DriverCustomerDto driverCustomer) {
        return updateService.findShortestTime(driverCustomer.getDriverId(), driverCustomer.getCustomerId());
    }

    @PostMapping("/update")
    void simulateFetching(@RequestBody DriverCustomerDto driverCustomer) {
        updateService.findShortestPath(driverCustomer.getDriverId(), driverCustomer.getCustomerId());
    }
}
