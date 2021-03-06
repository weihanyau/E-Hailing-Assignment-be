package com.taufufah.ehailing.controller;

import java.util.List;

import com.taufufah.ehailing.exceptions.CustomerNotFoundException;
import com.taufufah.ehailing.model.Customer;
import com.taufufah.ehailing.model.Destination;
import com.taufufah.ehailing.model.Status;
import com.taufufah.ehailing.model.Vertex;
import com.taufufah.ehailing.repository.CustomerRepository;
import com.taufufah.ehailing.repository.DestinationRepository;
import com.taufufah.ehailing.repository.VertexRepository;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {
    private final CustomerRepository customerRepository;
    private final VertexRepository vertexRepository;
    private final DestinationRepository destinationRepository;

    public CustomerController(CustomerRepository customerRepository, VertexRepository vertexRepository,
            DestinationRepository destinationRepository) {
        this.customerRepository = customerRepository;
        this.vertexRepository = vertexRepository;
        this.destinationRepository = destinationRepository;
    }

    @GetMapping("/customers")
    List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @GetMapping("/customers/{id}")
    Customer getOneDriver(@PathVariable Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }

    @GetMapping("/customers/status/{status}")
    List<Customer> getDriverbyStatus(@PathVariable String status) {
        return customerRepository.findAllByStatus(Status.valueOf(status.toUpperCase()));
    }

    @PutMapping("/customers/{id}/updateStatus")
    Customer updateCustomerStatus(@RequestBody Customer customer, @PathVariable Long id) {
        return customerRepository.updateCustomerStatus(id, customer.getStatus());
    }

    @PutMapping("/customers/{id}/updateDestination")
    Customer updateCustomerDestination(@RequestBody Destination destination, @PathVariable Long id) {
        Destination lastDest = customerRepository.findById(id).get().getDestination();
        destinationRepository.deleteById(lastDest.getId());

        Long destId = destinationRepository.save(destination).getId();
        return customerRepository.updateDestination(id, destId);
    }

    @PatchMapping("/customers/{id}")
    Customer updateCustomer(@RequestBody Customer newCustomer, @PathVariable Long id) {
        Customer oldCustomer = customerRepository.findById(id).orElseThrow(() -> new CustomerNotFoundException(id));
        destinationRepository.deleteById(oldCustomer.getDestination().getId());
        customerRepository.deleteById(id);

        Long customerId = customerRepository.save(newCustomer).getId();
        Vertex closestToCustomer = vertexRepository.findClosestNodes(newCustomer.getLongitude(),
                newCustomer.getLatitude());

        Destination destination = new Destination(newCustomer.getDest_longitude(), newCustomer.getDest_latitude());
        Long destId = destinationRepository.save(destination).getId();
        Vertex closestToDestination = vertexRepository.findClosestNodes(destination.getLongitude(),
                destination.getLatitude());

        destinationRepository.connectToClosestVertex(destId, closestToDestination.getId());
        customerRepository.updateDestination(customerId, destId);
        return customerRepository.connectToClosestVertex(customerId, closestToCustomer.getId());
    }

    @PostMapping("/customers")
    Customer newCustomer(@RequestBody Customer newCustomer) {
        Long customerId = customerRepository.save(newCustomer).getId();
        Vertex closestToCustomer = vertexRepository.findClosestNodes(newCustomer.getLongitude(),
                newCustomer.getLatitude());

        Destination destination = new Destination(newCustomer.getDest_longitude(), newCustomer.getDest_latitude());
        Long destId = destinationRepository.save(destination).getId();
        Vertex closestToDestination = vertexRepository.findClosestNodes(destination.getLongitude(),
                destination.getLatitude());

        destinationRepository.connectToClosestVertex(destId, closestToDestination.getId());
        customerRepository.updateDestination(customerId, destId);
        return customerRepository.connectToClosestVertex(customerId, closestToCustomer.getId());
    }

    @DeleteMapping("/customers/{id}")
    void deleteCustomer(@PathVariable Long id) {
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new CustomerNotFoundException(id));
        customerRepository.deleteById(id);
        destinationRepository.deleteById(customer.getDestination().getId());
    }
}
