import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import app.EegauthappApplication;
import app.service.ClassifierService;
import app.service.CsvService;
import app.service.LoginService;
import app.service.NeuroskyDataReader;
import app.service.RegisterService;
import app.service.UserService;

@SpringBootTest(classes = EegauthappApplication.class)
@ActiveProfiles("test")
class AuthenticationTest {
	
	@Autowired
	private LoginService loginService;

	@Test
	void testAuthentication() {
		boolean status = loginService.authenticateUser("testUser");
		assertTrue(status);
	}
}
