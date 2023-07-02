package app.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.springframework.stereotype.Service;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.AutoWEKAClassifier;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToNominal;

@Service
public class ClassifierService {
	
	// Runs AutoWEKA and returns classifier in byte format
	public byte[] runAutoWEKA(File dataFile, int timeLimit) throws Exception {
		
		// Load data
		CSVLoader loader = new CSVLoader();
		loader.setSource(dataFile);
		Instances data = loader.getDataSet();
		
		// Apply filter
		Instances filteredData = applyNominalFilter(data);
		// Set Class Index
		filteredData.setClassIndex(filteredData.numAttributes() - 1);
		
		// Build classifier
		AutoWEKAClassifier classifier = new AutoWEKAClassifier();
		classifier.setTimeLimit(timeLimit);
		classifier.buildClassifier(filteredData);
		
		System.out.println(classifier.toString());
		
		// Return classifier as byte array
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(classifier);
		oos.flush();
		return bos.toByteArray();
	}
	
	// Evaluates the performance of a classifier on a set of data and returns the predicted accuracy 
	public double evaluateClassifier(File dataFile, byte[] byteClassifier) throws Exception {
		
		// Load data
		CSVLoader loader = new CSVLoader();
		loader.setSource(dataFile);
		Instances data = loader.getDataSet();
				
		// Apply filter
		Instances filteredData = applyNominalFilter(data);
		// Set Class Index
		filteredData.setClassIndex(filteredData.numAttributes() - 1);
			
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(byteClassifier));
		Classifier classifier = (Classifier) ois.readObject();
		ois.close();
		
		Evaluation eval = new Evaluation(filteredData); 
		eval.evaluateModel(classifier, filteredData); 
	
		System.out.println(eval.toSummaryString());
		System.out.println(eval.toClassDetailsString());
		System.out.println(eval.toMatrixString());
		
		double accuracy = eval.pctCorrect();
		return accuracy;
	}
	
	// Applies nominal filter to the last attribute
	public Instances applyNominalFilter (Instances data) throws Exception {
		NumericToNominal numToNom = new NumericToNominal();
		numToNom.setAttributeIndices("last");
		numToNom.setInputFormat(data);
		Instances filteredData = Filter.useFilter(data, numToNom);
		return filteredData;
	}
}
