package com.agentgroups.realchatapplication.service;

import com.agentgroups.realchatapplication.dto.LoginRequestDTO;
import com.agentgroups.realchatapplication.dto.LoginResponseDTO;
import com.agentgroups.realchatapplication.dto.RegisterRequestDTO;
import com.agentgroups.realchatapplication.dto.UserDTO;
import com.agentgroups.realchatapplication.jwt.JwtService;
import com.agentgroups.realchatapplication.model.User;
import com.agentgroups.realchatapplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    public UserDTO signup(RegisterRequestDTO registerRequestDTO) {
        if (userRepository.findByUsername(registerRequestDTO.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(registerRequestDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword())); // In a real application, make sure to
        user.setEmail(registerRequestDTO.getEmail());
        User saveduser=userRepository.save(user);

        return convertToUserDTO(saveduser);
    }

    private UserDTO convertToUserDTO(User saveduser) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(saveduser.getId());
        userDTO.setUsername(saveduser.getUsername());
        userDTO.setEmail(saveduser.getEmail());
        return userDTO;
    }

    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {

        User user = userRepository.findByUsername(loginRequestDTO.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(),loginRequestDTO.getPassword()));

//        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
//            throw new RuntimeException("Invalid credentials");
//        }

        // Generate JWT token (implementation not shown here)
        String token = jwtService.generateToken(user); // Replace with actual token generation logic

//        LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
//        loginResponseDTO.setToken(token);
//        loginResponseDTO.setUserDTO(convertToUserDTO(user));

        return LoginResponseDTO.builder()
                .token(token)
                .userDTO(convertToUserDTO(user))
                .build();
    }

    public ResponseEntity<String> logout() {

        ResponseCookie responseCookie=ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0).sameSite("strict")
                .build();
        // Invalidate the JWT token (implementation depends on your token management strategy)
        // For stateless JWT, you might not need to do anything on the server side.
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,responseCookie.toString())
                .body("Logged out successfully");
    }
}
