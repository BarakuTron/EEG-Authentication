import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import app.EegauthappApplication;
import app.service.ClassifierService;
import app.service.CsvService;
import app.service.NeuroskyDataReader;
import app.service.RegisterService;
import app.service.UserService;

@SpringBootTest(classes = EegauthappApplication.class)
@ActiveProfiles("test")
class RegisterTest {
	
	@Autowired
	private RegisterService registerService;

	@Test
	void testRegistration() {
		boolean status = registerService.registerUser("testUser");
		assertTrue(status);
		
		boolean status2 = registerService.registerUser("testUser");
		assertFalse(status2);
	}
}
