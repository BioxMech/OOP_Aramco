package com.OOP.springboot.mongodb.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.OOP.springboot.mongodb.exception.CustomException;
import com.OOP.springboot.mongodb.message.ResponseMsg;
import com.OOP.springboot.mongodb.model.Customer;
import com.OOP.springboot.mongodb.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {
	
	@Autowired
	CustomerService customerService;

	@PostMapping("/create") //  is used to save a single Customer data to MongoDB database.
	public ResponseEntity<ResponseMsg> saveCustomer(@RequestBody Customer customer, HttpServletRequest request) {
		try {
			// save to MongoDB database
			Customer _customer = customerService.saveCustomer(customer);
			
			String message = "Upload Successfully a Customer to MongoDB with id = " + _customer.getId();
			return new ResponseEntity<ResponseMsg>(new ResponseMsg(message, request.getRequestURI(),
											Arrays.asList(customer)), HttpStatus.OK);
		}catch(Exception e) {
			String message = "Can NOT upload  a Customer to MongoDB database";
			return new ResponseEntity<ResponseMsg>(new ResponseMsg(message, request.getRequestURI(),
											e.getMessage()), HttpStatus.OK);
		}
	}
	
	@PostMapping("/create/v2") // is used to save a list of multiple Customer data to MongoDB database.
	public ResponseEntity<ResponseMsg> saveListCustomers(@RequestBody List<Customer> customers, HttpServletRequest request) {
		try {
			// save a list of customers to MongoDB database
			List<Customer> _customers = customerService.saveListCustomers(customers);
			
			String message = "Upload Successfully a list of Customer to MongoDB";
			return new ResponseEntity<ResponseMsg>(new ResponseMsg(message, request.getRequestURI(), _customers), HttpStatus.OK);	
		}catch(Exception e) {
			String message = "Can NOT upload a List of Customer to MongoDB database";
			return new ResponseEntity<ResponseMsg>(new ResponseMsg(message, request.getRequestURI(), 
														e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/all")  // is used to retrieve a single Customer document from MongoDB database via a given id
	public ResponseEntity<ResponseMsg> getAllCustomers(HttpServletRequest request) {
		try {
			// get all documents from MongoDB database
			List<Customer> customers = customerService.retrieveAllCustomers();
			
			String message = "Retrieve all Customer successfully!";
			
			return new ResponseEntity<ResponseMsg>(new ResponseMsg(message, 
									request.getRequestURI(), customers), HttpStatus.OK);	
		}catch(Exception e) {
			String message = "Can NOT retrieve all data from MongoDB database";
			return new ResponseEntity<ResponseMsg>(new ResponseMsg(message, request.getRequestURI(), 
									e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getone/{id}")  // is used to retrieve all Customer documents from MongoDB database.
	public ResponseEntity<ResponseMsg> getCustomerById(@PathVariable String id, HttpServletRequest request) {
		try {
			// get a document from MongoDB database using ID
			Optional<Customer> customerOpt = customerService.getCustomerByID(id);
			
			if(customerOpt.isPresent()) {
				String message = "Successfully get a Customer from MongoDB with id = " + id;
				return new ResponseEntity<ResponseMsg>(new ResponseMsg(message, request.getRequestURI(), 
										Arrays.asList(customerOpt.get())), HttpStatus.OK);
			} else {
				String message = "Fail! Not FOUND a Customer from MongoDB with id = " + id;
				return new ResponseEntity<ResponseMsg>(new ResponseMsg(message, request.getRequestURI(), 
															"NOT FOUND"), HttpStatus.NOT_FOUND);
			} 
		}catch(Exception e) {
			String message = "Can NOT get a Customer from MongoDB database with id = " + id;
			return new ResponseEntity<ResponseMsg>(new ResponseMsg(message, request.getRequestURI(), 
														e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PutMapping("/update/{id}") // to update a Customer document from MongoDB via a given id and request body’s data
	public ResponseEntity<ResponseMsg> updateCustomer(@PathVariable String id, @RequestBody Customer customer,
															HttpServletRequest request) {
		try {
			// update a customer to MongoDB
			Customer _customer = customerService.updateCustomer(id, customer);
			String message = "Successfully Update a Customer to MongoDB with id = " + id;
			
			return new ResponseEntity<ResponseMsg> (new ResponseMsg(message, request.getRequestURI(),
					Arrays.asList(_customer)), HttpStatus.OK);
		} catch (CustomException ce) {
			String message = "Can NOT update to MongoDB a Customer with id = " + id;
			return new ResponseEntity<ResponseMsg> (new ResponseMsg(message, request.getRequestURI(), 
					ce.getMessage()), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			String message = "Can NOT update to MongoDB a Customer with id = " + id;
			return new ResponseEntity<ResponseMsg> (new ResponseMsg(message, request.getRequestURI(), 
					e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping("/deletebyid/{id}")  //  is used to delete a Customer document from MongoDB via a give id
	public ResponseEntity<ResponseMsg> deleteCustomerById(@PathVariable String id, HttpServletRequest request) {
		try {
			// delete a Customer from MongoDB database using ID
			customerService.deleteCustomerById(id);
			
			String message = "Successfully delete a Customer from MongoDB database with id = " + id;
			return new ResponseEntity<ResponseMsg>(new ResponseMsg(message, request.getRequestURI()), HttpStatus.OK);
		} catch(Exception e) {
			String message = "Can Not delete a Customer from MongoDB database with id = " + id;
			return new ResponseEntity<ResponseMsg>(new ResponseMsg(message, request.getRequestURI(), e.getMessage()), 
														HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/deleteall") // is used to delete all Customer documents from MongoDB database.
	public ResponseEntity<ResponseMsg> deleteAllCustomers(HttpServletRequest request){
		String message = "";
		try {
			customerService.deleteAll();
			
			message = "Successfully delete all Customer from MongoDB database!";
			return new ResponseEntity<ResponseMsg> (new ResponseMsg(message, 
											request.getRequestURI()), HttpStatus.OK);
		} catch(Exception e) {
			message = "Can NOT delete All documents from MongoDB database";
			return new ResponseEntity<ResponseMsg> (new ResponseMsg(message, 
							request.getRequestURI(), e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}