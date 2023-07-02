package app.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.opencsv.CSVWriter;

import app.model.SubjectEEGData;
import app.repository.MongoRepository;

@Service
public class CsvService {
	
	@Autowired
	MongoRepository mongoRepository;

	public File createUserCSVFile(SubjectEEGData eegDataCollection, String filepath, int segmentSize, int overlap, int numOfSubjects, int rowsPerSubject) throws IOException {
		
		List<List<Double>> segmentedData = new ArrayList<>();
		segmentedData.add(segmentData(eegDataCollection.getDeltaArray(), segmentSize, overlap));
		segmentedData.add(segmentData(eegDataCollection.getThetaArray(), segmentSize, overlap));
		segmentedData.add(segmentData(eegDataCollection.getLowAlphaArray(), segmentSize, overlap));
		segmentedData.add(segmentData(eegDataCollection.getHighAlphaArray(), segmentSize, overlap));
		segmentedData.add(segmentData(eegDataCollection.getLowBetaArray(), segmentSize, overlap));
		segmentedData.add(segmentData(eegDataCollection.getHighBetaArray(), segmentSize, overlap));
		segmentedData.add(segmentData(eegDataCollection.getLowGammaArray(), segmentSize, overlap));
		segmentedData.add(segmentData(eegDataCollection.getHighGammaArray(), segmentSize, overlap));

		// Create user file with user's segmented data
		File userFile = createUserFile(segmentedData, filepath);
		
		// Find the number of sections based on the size of the segments and the overlap)
		int numOfSections = getTotalSections(eegDataCollection.getDeltaArray().size(), segmentSize, overlap);
		System.out.println("Number of sections: " + numOfSections);

		// Create pool file
		File poolFile = calculateAndSaveMean(segmentSize, numOfSections, overlap);
		
		// Take some rows from poolFile, add them to userFile
		List<String> selectedRows = getRandomRowsFromCSV(poolFile, rowsPerSubject, numOfSubjects);
		addSelectedRowsToUserFile(selectedRows, userFile);
		
		// Delete pool file after having used it
		poolFile.delete();
		
		return userFile;
	}
	
	public static int getTotalSections(int numValues, int valuesPerSection, int overlap) {
	    if (numValues <= 0 || valuesPerSection <= 0 || overlap < 0) {
	        throw new IllegalArgumentException("Invalid input");
	    }
	    if (overlap >= valuesPerSection) {
	        return 0;
	    }
	    int valuesPerCompleteSection = valuesPerSection - overlap;
	    int totalSections = (numValues - overlap) / valuesPerCompleteSection;
	    return totalSections;
	}
	
	
	public List<Double> segmentData(List<Double> dataList, int windowSize, int overlap) {
	    List<Double> segmentedData = new ArrayList<>();

	    // Convert dataList to double[]
	    double[] dataArray = new double[dataList.size()];
	    for (int i = 0; i < dataList.size(); i++) {
	        dataArray[i] = dataList.get(i);
	    }

	    for (int i = 0; i <= dataArray.length - windowSize; i += (windowSize - overlap)) {
	        double sum = 0;
	        for (int j = i; j < i + windowSize; j++) {
	            sum += dataArray[j];
	        }
	        segmentedData.add(sum / windowSize);
	    }

	    return segmentedData;
	}


	public File createUserFile(List<List<Double>> segmentedData, String filePath) {
	    // Create a new CSV file
	    File outputFile = new File(filePath);

	    try (CSVWriter writer = new CSVWriter(new FileWriter(outputFile))) {
	        // Write the headers
	        String[] headers = {"delta", "theta", "lowAlpha", "highAlpha", "lowBeta", "highBeta", "lowGamma",
	                "highGamma", "Label"};
	        writer.writeNext(headers);

	        for (int i = 0; i < segmentedData.get(0).size(); i++) {
	            String[] row = new String[9];
	            for (int j = 0; j < 8; j++) {
	                row[j] = Double.toString(segmentedData.get(j).get(i));
	            }
	            row[8] = "1";
	            writer.writeNext(row);
	        }
	        return outputFile;
	    } catch (IOException e) {
	        System.err.println("Error writing to CSV file: " + e.getMessage());
	        return null;
	    }
	}
	
	private List<String> getRandomRowsFromCSV(File file, int rowsPerUser, int numberOfUsers) {
	    List<String> rows = new ArrayList<>();
	    
	    // Read all rows into a list
	    List<String> allRows = new ArrayList<>();
	    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
	        // Discard the header row
	        reader.readLine();
	        
	        String line;
	        while ((line = reader.readLine()) != null) {
	            allRows.add(line);
	        }
	    } catch (IOException e) {
	        System.err.println("Error: Could not read input file: " + file.getAbsolutePath());
	        e.printStackTrace();
	        return Collections.emptyList();
	    }

	    // Group the rows by user ID
	    Map<String, List<String>> rowsByUser = new HashMap<>();
	    for (String row : allRows) {
	        String[] fields = row.split(",");
	        String userId = fields[0];
	        if (!rowsByUser.containsKey(userId)) {
	            rowsByUser.put(userId, new ArrayList<>());
	        }
	        rowsByUser.get(userId).add(row);
	    }

	    // Select 5 random rows for each user
	    for (Map.Entry<String, List<String>> entry : rowsByUser.entrySet()) {
	        String userId = entry.getKey();
	        List<String> userRows = entry.getValue();
	        Collections.shuffle(userRows);
	        int count = Math.min(rowsPerUser, userRows.size());
	        for (int i = 0; i < count; i++) {
	            rows.add(userRows.get(i));
	        }
	    }

	    // Limit the number of users
	    if (rows.size() > numberOfUsers * rowsPerUser) {
	        rows = rows.subList(0, numberOfUsers * rowsPerUser);
	    }

	    return rows;
	}
	
	private void addSelectedRowsToUserFile(List<String> selectedRows, File userFile) {
	    FileWriter writer = null;

	    try {
	        writer = new FileWriter(userFile, true);

	        for (String row : selectedRows) {
	            String[] columns = row.split(",");
	            
	            // Remove ID column
	            String[] formattedColumns = Arrays.copyOfRange(columns, 1, columns.length);
	            
	            // Add Label column with value 0
	            String formattedRow = String.join(",", formattedColumns) + ",0";
	            
	            writer.write(formattedRow + "\n");
	        }

	    } catch (IOException e) {
	        System.err.println("Error: Could not write to file: " + userFile.getAbsolutePath());
	        e.printStackTrace();
	    } finally {
	        try {
	            if (writer != null) {
	                writer.close();
	            }
	        } catch (IOException e) {
	            System.err.println("Error: Could not close file: " + userFile.getAbsolutePath());
	                e.printStackTrace();
	        }
	    }
	}
	
	public File calculateAndSaveMean(int numInputsPerSection, int numSections, int sectionOverlap) {
	    List<String> bandNames = Arrays.asList("delta", "theta", "lowAlpha", "highAlpha", "lowBeta", "highBeta",
	            "lowGamma", "highGamma");
	    try {
	        // Check if file exists
	        boolean fileExists = new File("AllUserDataTest.csv").exists();
	        FileWriter csvWriter = new FileWriter("AllUserDataTest.csv", true);
	        // Add header row if file is newly created
	        if (!fileExists) {
	            csvWriter.append("ID,");
	            for (String bandName : bandNames) {
	                csvWriter.append(bandName + ",");
	            }
	            csvWriter.append("\n");
	        }
	        for (int id = 1; id <= 10; id++) {
	            List<List<Double>> bandMeans = new ArrayList<>();
	            for (String bandName : bandNames) {
	                List<Double> means = calculateMeanForSection(bandName, numInputsPerSection, numSections,
	                        sectionOverlap, Integer.toString(id));
	                bandMeans.add(means);
	            }
	            for (int section = 0; section < numSections; section++) {
	                csvWriter.append(Integer.toString(id) + ",");
	                for (int bandIndex = 0; bandIndex < bandNames.size(); bandIndex++) {
	                    csvWriter.append(Double.toString(bandMeans.get(bandIndex).get(section)) + ",");
	                }
	                csvWriter.append("\n");
	            }
	        }
	        csvWriter.close();
	        return new File("AllUserDataTest.csv");
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return null;
	}

	public List<Double> calculateMeanForSection(String bandName,
	        int numInputsPerSection, int numSections,
	        int sectionOverlap,String objectId) {

	    //This method calculates the mean value of a given band name for a given object id over a number of sections
	    //Each section has a fixed number of inputs and an overlap with the previous and next sections

	    //Get the band data array for the current document
	    List<Double> bandData = mongoRepository.getBandDataForObjectId(bandName, objectId);

	    //Initialize list to hold mean values for each section
	    List<Double> means = new ArrayList<>();

	    int startIndex = 0;
	    int endIndex = numInputsPerSection -1;
	    for(int i=0;i<numSections;i++){
	        double sum=0;
	        int count=0;

	        //Loop through the current section of band data and calculate the sum and count of its values
	        for(int j=startIndex;j<=endIndex && j<bandData.size();j++){
	            sum+=bandData.get(j);
	            count++;
	        }

	        //Calculate the mean value for the current section and add it to the list of means
	        double mean=sum/count;
	        means.add(mean);
	        startIndex+=numInputsPerSection-sectionOverlap;
	        endIndex+=numInputsPerSection-sectionOverlap;
	    }
	    return means;
	}

}
