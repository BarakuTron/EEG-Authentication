package app.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.repository.MongoRepository;
import app.model.SubjectEEGData;

@Service
public class NeuroskyDataReader {

	private static final String thinkgearHost = "127.0.0.1";
	private static final int thinkgearPort = 13854;
	private static final String command = "{\"enableRawOutput\": false, \"format\": \"Json\"}\n";

	private InputStream input;
	private OutputStream output;
	private BufferedReader reader;

	@Autowired
	MongoRepository mongoRepository;
	SubjectEEGData eegDataCollection;


	// Adds a subject to the mongodb database
	public void addSubject(String id, int duration) {

		List<Double> deltaArray = new ArrayList<>();
		List<Double> thetaArray = new ArrayList<>();
		List<Double> lowAlphaArray = new ArrayList<>();
		List<Double> highAlphaArray = new ArrayList<>();
		List<Double> lowBetaArray = new ArrayList<>();
		List<Double> highBetaArray = new ArrayList<>();
		List<Double> lowGammaArray = new ArrayList<>();
		List<Double> highGammaArray = new ArrayList<>();

		try {
			System.out.println("Connecting to host = " + thinkgearHost + ", port = " + thinkgearPort);
			Socket clientSocket = new Socket(thinkgearHost, thinkgearPort);
			input = clientSocket.getInputStream();
			output = clientSocket.getOutputStream();
			System.out.println("Sending command " + command);
			write(command);
			reader = new BufferedReader(new InputStreamReader(input, Charset.forName("UTF-8")));

			eegDataCollection = new SubjectEEGData(id, deltaArray, thetaArray, lowAlphaArray, highAlphaArray, lowBetaArray, highBetaArray, lowGammaArray, highGammaArray);

			while (deltaArray.size() < duration) {
				clientEvent(eegDataCollection);
			}

			mongoRepository.insertEEGData(eegDataCollection);

			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Records data for registering or logging in
	public SubjectEEGData recordSubject(String id, int duration) {

		List<Double> deltaArray = new ArrayList<>();
		List<Double> thetaArray = new ArrayList<>();
		List<Double> lowAlphaArray = new ArrayList<>();
		List<Double> highAlphaArray = new ArrayList<>();
		List<Double> lowBetaArray = new ArrayList<>();
		List<Double> highBetaArray = new ArrayList<>();
		List<Double> lowGammaArray = new ArrayList<>();
		List<Double> highGammaArray = new ArrayList<>();

		try {
			System.out.println("Connecting to host = " + thinkgearHost + ", port = " + thinkgearPort);
			Socket clientSocket = new Socket(thinkgearHost, thinkgearPort);
			input = clientSocket.getInputStream();
			output = clientSocket.getOutputStream();
			System.out.println("Sending command " + command);
			write(command);
			reader = new BufferedReader(new InputStreamReader(input, Charset.forName("UTF-8")));

			eegDataCollection = new SubjectEEGData(id, deltaArray, thetaArray, lowAlphaArray, highAlphaArray, lowBetaArray, highBetaArray, lowGammaArray, highGammaArray);

			while (deltaArray.size() < duration) {
				clientEvent(eegDataCollection);
			}
			clientSocket.close();

			return eegDataCollection;

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void write(String data) {
		try {
			output.write(data.getBytes());
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void clientEvent(SubjectEEGData eegDataCollection) throws NumberFormatException, IOException {
		double delta;
		double theta;
		double lowAlpha;
		double highAlpha;
		double lowBeta;
		double highBeta;
		double lowGamma;
		double highGamma;

		JSONObject eegPower = null;
		if (reader.ready()) {
			String jsonText = reader.readLine();
			try {
				JSONObject json = new JSONObject(jsonText);

				try {
					// poorSignalLevel = json.getString("poorSignalLevel");
					eegPower = json.getJSONObject("eegPower");
				} catch (Exception e) {
					//System.out.println("There was an error receiving the eegPower." + e);
				}
				

				if (eegPower != null) {
					delta = eegPower.getDouble("delta");
					theta = eegPower.getDouble("theta");
					lowAlpha = eegPower.getDouble("lowAlpha");
					highAlpha = eegPower.getDouble("highAlpha");
					lowBeta = eegPower.getDouble("lowBeta");
					highBeta = eegPower.getDouble("highBeta");
					lowGamma = eegPower.getDouble("lowGamma");
					highGamma = eegPower.getDouble("highGamma");

					System.out.println("LowAlpha: " + lowAlpha + " | LowBeta: " + lowBeta + " | HighAlpha: " + highAlpha
							+ " | highBeta: " + highBeta + " | Delta: " + delta + " | lowGamma: " + lowGamma
							+ " | Theta: " + theta + " | highGamma: " + highGamma);

					if (!Double.isNaN(delta) && !Double.isNaN(theta) && !Double.isNaN(lowAlpha)
							&& !Double.isNaN(highAlpha) && !Double.isNaN(lowBeta) && !Double.isNaN(highBeta)
							&& !Double.isNaN(lowGamma) && !Double.isNaN(highGamma)) {

						eegDataCollection.addDelta(delta);
						eegDataCollection.addTheta(theta);
						eegDataCollection.addLowAlpha(lowAlpha);
						eegDataCollection.addHighAlpha(highAlpha);
						eegDataCollection.addLowBeta(lowBeta);
						eegDataCollection.addHighBeta(highBeta);
						eegDataCollection.addLowGamma(lowGamma);
						eegDataCollection.addHighGamma(highGamma);
					}
				}
			} catch (JSONException e) {
				System.out.println("There was an error parsing the JSONObject." + e);
			}
		}

	}
}
