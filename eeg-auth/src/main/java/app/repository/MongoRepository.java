package app.repository;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import app.model.SubjectEEGData;

@Repository
public class MongoRepository {

	private final MongoTemplate mongoTemplate;

	public MongoRepository(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	public void insertEEGData(SubjectEEGData eegDataCollection) {

		Query query = new Query(Criteria.where("_id").is(eegDataCollection.getId()));

		Update update = new Update()
				.push("delta").each(eegDataCollection.getDeltaArray())
				.push("theta").each(eegDataCollection.getThetaArray())
				.push("lowAlpha").each(eegDataCollection.getLowAlphaArray())
				.push("highAlpha").each(eegDataCollection.getHighAlphaArray())
				.push("lowBeta").each(eegDataCollection.getLowBetaArray())
				.push("highBeta").each(eegDataCollection.getHighBetaArray())
				.push("lowGamma").each(eegDataCollection.getLowGammaArray())
				.push("highGamma").each(eegDataCollection.getHighGammaArray());

		mongoTemplate.upsert(query, update, "EEG_data");
	}

	public SubjectEEGData getSubjectDataById(String id) {
		Query query = new Query(Criteria.where("_id").is(id));
		Document document = mongoTemplate.findOne(query, Document.class, "EEG_data");

		if (document == null) {
			return null;
		}

		List<Double> deltaArray = (List<Double>) document.get("delta");
		List<Double> thetaArray = (List<Double>) document.get("theta");
		List<Double> lowAlphaArray = (List<Double>) document.get("lowAlpha");
		List<Double> highAlphaArray = (List<Double>) document.get("highAlpha");
		List<Double> lowBetaArray = (List<Double>) document.get("lowBeta");
		List<Double> highBetaArray = (List<Double>) document.get("highBeta");
		List<Double> lowGammaArray = (List<Double>) document.get("lowGamma");
		List<Double> highGammaArray = (List<Double>) document.get("highGamma");

		return new SubjectEEGData(id, deltaArray, thetaArray, lowAlphaArray, highAlphaArray, lowBetaArray,
				highBetaArray, lowGammaArray, highGammaArray);
	}

	public List<Double> getBandDataForObjectId(String bandName, String objectId) {
		List<Double> bandData = new ArrayList<>();
		try {
			Query query = new Query(Criteria.where("_id").is(objectId));
			Document document = mongoTemplate.findOne(query, Document.class, "EEG_data");

			if (document != null) {
				bandData = document.getList(bandName, Double.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bandData;
	}	
}
