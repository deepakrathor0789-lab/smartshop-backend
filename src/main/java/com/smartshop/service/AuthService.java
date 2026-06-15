package com.smartshop.service;

import com.smartshop.dto.JwtResponse;
import com.smartshop.dto.LoginRequest;
import com.smartshop.dto.RegisterRequest;

public interface AuthService {

    // Register new user
    String register(RegisterRequest registerRequest);

    // Login user and return JWT token
    JwtResponse login(LoginRequest loginRequest);
}