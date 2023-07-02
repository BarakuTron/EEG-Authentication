package app.service;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.model.SubjectEEGData;

@Service
public class RegisterService {
	
	@Autowired
	private ClassifierService classifierService;
	@Autowired
	private UserService userService;
	@Autowired
	private NeuroskyDataReader neuroskyDataReader;
	@Autowired
	private CsvService csvService;
	
	// Method responsible for registering a user, returns true if user was successfully registered
	public boolean registerUser(String username) {
		
		if(!userService.userExists(username)) {
			// Record data for 180s
			SubjectEEGData subjectEEGData = neuroskyDataReader.recordSubject(username, 180);
			String filepath = "userRegistration_" + username + ".csv";
			
			byte[] userClassifier;
			try {
				// Create user CSV file 
				File userRegistrationFile = csvService.createUserCSVFile(subjectEEGData, filepath, 6, 3, 10, 6);				
				// Find classifier through autoWEKA
				userClassifier = classifierService.runAutoWEKA(userRegistrationFile, 3);
				// Save classifier
				userService.addUserWithClassifier(username, userClassifier);
				// Success
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			} 
		}
		return false;
	}
}
