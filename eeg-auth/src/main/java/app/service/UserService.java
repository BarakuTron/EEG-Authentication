package app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.model.User;
import app.repository.UserRepository;

// Class responsible for handling interactions with UserRepository
@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public User addUserWithClassifier(String username, byte[] classifierData) {
        User newUser = new User(username, classifierData);
        return userRepository.save(newUser);
    }
    
    public byte[] getClassifierByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if(user != null) {
        	return user.getClassifier();
        }
        return null;
    }
    
    public boolean userExists(String username) {  	
    	return userRepository.findByUsername(username) != null;
    }
}
