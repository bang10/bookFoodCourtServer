package cm.standard.bookfoodcourt;

import cm.standard.bookfoodcourt.auth.service.SmsAuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AuthTest {
    @Autowired
    private SmsAuthService smsAuthService;

    @Test
    public void smsTest() throws Exception {
        System.out.println(smsAuthService.sendSmsOne("01050514790", "333321"));
    }
}
