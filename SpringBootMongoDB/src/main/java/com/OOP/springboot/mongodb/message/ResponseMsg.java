package com.OOP.springboot.mongodb.message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.OOP.springboot.mongodb.model.Customer;
import org.json.JSONObject;

public class ResponseMsg {
	private String message;
	private String url;
	private String error = "";
	private List<Customer> customers = new ArrayList<Customer>();
	private List<String> links = new ArrayList<>();
	private List<Map<String,String>> dataObjects = new ArrayList<>();

	public ResponseMsg(String message, String url, List<Customer> customers) {
		this.message = message;
		this.url = url;
		this.customers = customers;
	}

	public ResponseMsg(String message, String url, List<String> links, boolean isCrawl) {
		this.message = message;
		this.url = url;
		this.links = links;
	}

	// Constructor for China
	public ResponseMsg(String message, String url, List<Map<String,String>> dataObjects, boolean sCrawl, String country) {
		this.message = message;
		this.url = url;
		this.dataObjects = dataObjects;
	}
	
	public ResponseMsg(String message, String url, String error) {
		this.message = message;
		this.url = url;
		this.error = error;
	}

	public List<Map<String,String>> getDataObjects() {
		return dataObjects;
	}

	public void setDataObjects(List<Map<String,String>>dataObjects) {
		this.dataObjects = dataObjects;
	}

	public ResponseMsg(String message, String url) {
		this(message, url, new ArrayList<>());
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getUrl() {
		return this.url;
	}
	
	public void setCustomers(List<Customer> customers) {
		this.customers = customers;
	}
	
	public List<Customer> getCustomers() {
		return this.customers;
	}
	
	public void setError(String error) {
		this.error = error;
	}
	
	public String getError() {
		return this.error;
	}

	public List<String> getLinks() {
		return links;
	}

//	public void setLinks(List<String> links) {
//		this.links = links;
//	}
}