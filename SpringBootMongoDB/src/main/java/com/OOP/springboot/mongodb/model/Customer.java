package com.OOP.springboot.mongodb.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "Customers")
public class Customer {
	@Id
	private String id;
	private String firstName;
	private String lastName;
	private Integer age;
	private String address;
	private Double salary;
	
//	@Field // Always added to each data type
//	private String copyrightby = "The Best OOP Team";

	public Customer(String firstName, String lastName, int age, String address, Double salary) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.age = age;
		this.address = address;
		this.salary = salary;
	}
	
	public String getId() {
		return this.id;
	}
	
	public void setFirstName(String firstName) { this.firstName = this.firstName; }
	
	public String getFirstName() {
		return this.firstName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getLastName() {
		return this.lastName;
	}
	
	public void setAge(int age) { this.age = age; }
	
	public int getAge() {
		return this.age;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getAddress() {
		return this.address;
	}
	
	public void setSalary(Double salary) {
		this.salary = salary;
	}
	
	public Double getSalary() {
		return this.salary;
	}

}
