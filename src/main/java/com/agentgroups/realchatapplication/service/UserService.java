package com.agentgroups.realchatapplication.service;

import com.agentgroups.realchatapplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public void setUserOnlineStatus(String username, boolean isOnline) {
        // Implement logic to update user's online status in the database
        // This is a placeholder implementation
        System.out.println("Setting user " + username + " online status to " + isOnline);
        userRepository.updateUserOnlineStatus(username, isOnline);
    }
}
