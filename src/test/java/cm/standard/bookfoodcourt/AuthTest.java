package cm.standard.bookfoodcourt;

import cm.standard.bookfoodcourt.auth.service.SmsAuthService;
import cm.standard.bookfoodcourt.util.redis.RedisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AuthTest {
    @Autowired
    private SmsAuthService smsAuthService;

    @Autowired
    private RedisService redisService;

    @Test
    public void smsTest() throws Exception {
        System.out.println(smsAuthService.sendSmsOne("01050514790", "333321"));
    }

    @Test
    public void redisDelete() {
        redisService.deleteData("01050514790");
    }
}
