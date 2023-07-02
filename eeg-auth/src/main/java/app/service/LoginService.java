package app.service;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.model.SubjectEEGData;

@Service
public class LoginService {
    
	@Autowired
	private ClassifierService classifierService;
	@Autowired
	private UserService userService;
	@Autowired
	private NeuroskyDataReader neuroskyDataReader;
	@Autowired
	private CsvService csvService;
	
	// Method responsible for performing user authentication
	public boolean authenticateUser(String username) {
		
		byte[] userClassifier;
		double predictedAccuracy;
		String filepath = "userLogin_" + username + ".csv";
		
		try {
			// Get the user's classifier
			userClassifier = userService.getClassifierByUsername(username);
			
			if(userClassifier != null) {
				//Record data for 60s
				SubjectEEGData subjectEEGData = neuroskyDataReader.recordSubject(username, 60);
				// Create user CSV file
				File userLoginFile = csvService.createUserCSVFile(subjectEEGData, filepath , 6, 3, 10, 2);
				// Predict accuracy using user's classifier on user file 
				predictedAccuracy = classifierService.evaluateClassifier(userLoginFile, userClassifier);
				
				System.out.println("Accuracy: " + predictedAccuracy);
				
				// If predicted accuracy is more than the threshold, authenticate user (return true)
				if(predictedAccuracy > 70) {
					return true;
				}
			}
			return false;			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
    
  
    
}
