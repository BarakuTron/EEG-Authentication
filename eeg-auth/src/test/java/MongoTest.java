
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import app.EegauthappApplication;
import app.model.SubjectEEGData;
import app.repository.MongoRepository;

@SpringBootTest(classes = EegauthappApplication.class)
@ActiveProfiles("test")
public class MongoTest {
    
    @Autowired
    private MongoRepository mongoRepository;
    
    
    public void testInsertEEGData() {
        String id = "testId2";
        List<Double> deltaArray = Arrays.asList(1.0, 2.0, 3.0, 4.0);
        List<Double> thetaArray = Arrays.asList(4.0, 5.0, 6.0);
        List<Double> lowAlphaArray = Arrays.asList(7.0, 8.0, 9.0);
        List<Double> highAlphaArray = Arrays.asList(10.0, 11.0, 12.0);
        List<Double> lowBetaArray = Arrays.asList(13.0, 14.0, 15.0);
        List<Double> highBetaArray = Arrays.asList(16.0, 17.0, 18.0);
        List<Double> lowGammaArray = Arrays.asList(19.0, 20.0, 21.0);
        List<Double> highGammaArray = Arrays.asList(22.0, 23.0, 24.0);
//
//        mongoRepository.insertEEGData(id, deltaArray, thetaArray, lowAlphaArray, highAlphaArray, lowBetaArray,
//                highBetaArray, lowGammaArray, highGammaArray);
    }
    
//    @Test
    public void testGetSubjectDataById() {
        // Test that data can be retrieved successfully
        SubjectEEGData data = mongoRepository.getSubjectDataById("testId2");
        assertEquals("testId2", data.getId());
        assertEquals(Arrays.asList(1.0, 2.0, 3.0, 4.0), data.getDeltaArray());
        assertEquals(Arrays.asList(4.0, 5.0, 6.0), data.getThetaArray());
        assertEquals(Arrays.asList(7.0, 8.0, 9.0), data.getLowAlphaArray());
        assertEquals(Arrays.asList(10.0, 11.0, 12.0), data.getHighAlphaArray());
        assertEquals(Arrays.asList(13.0, 14.0, 15.0), data.getLowBetaArray());
        assertEquals(Arrays.asList(16.0, 17.0, 18.0), data.getHighBetaArray());
        assertEquals(Arrays.asList(19.0, 20.0, 21.0), data.getLowGammaArray());
        assertEquals(Arrays.asList(22.0, 23.0, 24.0), data.getHighGammaArray());
        
        // Test that null is returned when the id doesn't exist
        assertNull(mongoRepository.getSubjectDataById("nonexistent_id"));
    }
    
    
    @Test
    public void testGetBandDataForObjectId() {
        // Test that data can be retrieved successfully
        List<Double> data = mongoRepository.getBandDataForObjectId("delta", "testId2");
        assertEquals(Arrays.asList(1.0, 2.0, 3.0, 4.0), data);
        assertNotEquals(Arrays.asList(1.0, 2.0, 3.0, 5.0), data);
    }
    
}
