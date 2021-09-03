package com.OOP.springboot.mongodb.service;

import java.util.List;
import java.util.Optional;

import com.OOP.springboot.mongodb.exception.CustomException;
import com.OOP.springboot.mongodb.model.Customer;
import com.OOP.springboot.mongodb.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {
	
	@Autowired
	CustomerRepository repo;
	
	public Customer saveCustomer(Customer customer){
		return repo.save(customer);
	}
	
	public List<Customer> saveListCustomers(List<Customer> customers) {
		return repo.saveAll(customers);
	}
	
	public List<Customer> retrieveAllCustomers(){
		return repo.findAll();
	}
	
	public Optional<Customer> getCustomerByID(String id) {
		return repo.findById(id);
	}
	
	public Customer updateCustomer(String id, Customer customer) throws CustomException {
		
		Optional<Customer> customerOpt = repo.findById(id);
		
		if(!customerOpt.isPresent()) {
			throw new CustomException("404", "Can not find a customer for updating with id = " + id);
		}
			
		Customer _customer = customerOpt.get();
		
		_customer.setFirstName(customer.getFirstName());
		_customer.setLastName(customer.getLastName());
		_customer.setAddress(customer.getAddress());
		_customer.setAge(customer.getAge());
		_customer.setSalary(customer.getSalary());
		
		repo.save(_customer);
		
		return _customer;
	}
	
	public void deleteCustomerById(String id) {
		repo.deleteById(id);
	}
	
	public void deleteAll() {
		repo.deleteAll();
	}
}