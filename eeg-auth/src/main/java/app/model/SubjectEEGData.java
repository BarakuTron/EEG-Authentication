package app.model;

import java.util.List;

public class SubjectEEGData {
	private final String id;
    private List<Double> deltaArray;
    private List<Double> thetaArray;
    private List<Double> lowAlphaArray;
    private List<Double> highAlphaArray;
    private List<Double> lowBetaArray;
    private List<Double> highBetaArray;
    private List<Double> lowGammaArray;
    private List<Double> highGammaArray;
    
    public SubjectEEGData(String id, List<Double> deltaArray, List<Double> thetaArray, List<Double> lowAlphaArray,
            List<Double> highAlphaArray, List<Double> lowBetaArray, List<Double> highBetaArray, List<Double> lowGammaArray,
            List<Double> highGammaArray) {
        this.id = id;
        this.deltaArray = deltaArray;
        this.thetaArray = thetaArray;
        this.lowAlphaArray = lowAlphaArray;
        this.highAlphaArray = highAlphaArray;
        this.lowBetaArray = lowBetaArray;
        this.highBetaArray = highBetaArray;
        this.lowGammaArray = lowGammaArray;
        this.highGammaArray = highGammaArray;
    }
    
    public String getId() {
        return id;
    }
   
    // methods for adding and retrieving data from the lists

    public void addDelta(double value) {
        deltaArray.add(value);
    }

    public List<Double> getDeltaArray() {
        return deltaArray;
    }

    public void addTheta(double value) {
        thetaArray.add(value);
    }

    public List<Double> getThetaArray() {
        return thetaArray;
    }

    public void addLowAlpha(double value) {
        lowAlphaArray.add(value);
    }

    public List<Double> getLowAlphaArray() {
        return lowAlphaArray;
    }

    public void addHighAlpha(double value) {
        highAlphaArray.add(value);
    }

    public List<Double> getHighAlphaArray() {
        return highAlphaArray;
    }

    public void addLowBeta(double value) {
        lowBetaArray.add(value);
    }

    public List<Double> getLowBetaArray() {
        return lowBetaArray;
    }

    public void addHighBeta(double value) {
        highBetaArray.add(value);
    }

    public List<Double> getHighBetaArray() {
        return highBetaArray;
    }

    public void addLowGamma(double value) {
        lowGammaArray.add(value);
    }

    public List<Double> getLowGammaArray() {
        return lowGammaArray;
    }

    public void addHighGamma(double value) {
        highGammaArray.add(value);
    }

    public List<Double> getHighGammaArray() {
        return highGammaArray;
    }
}

