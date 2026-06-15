package com.smartshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SmartshopEcommerceApplication {

	public static void main(String[] args) {
	    SpringApplication.run(SmartshopEcommerceApplication.class, args);

	    System.out.println("""
	        ===============================================
	              🚀 SMARTSHOP E-COMMERCE SYSTEM
	        ===============================================
	        ✅ Application Started Successfully
	        🔐 Security: JWT Authentication Enabled
	        💾 Database: MySQL Connected
	        🌐 REST APIs Ready for Requests
	        🛒 SmartShop Backend is Running...
	        ===============================================
	        """);
	}

}
