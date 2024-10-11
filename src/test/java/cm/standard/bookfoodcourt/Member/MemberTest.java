package cm.standard.bookfoodcourt.Member;

import cm.standard.bookfoodcourt.member.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;

@SpringBootTest
public class MemberTest {

    @Autowired
    private MemberService memberService;

    @Test
    public void getMemberListTest() throws Exception {
        System.out.println(memberService.getMemberInfoList(null));
    }

    @Test
    public void randomNumber() {
        for (int i = 0; i < 10; i++) {
            System.out.println(new Random().nextInt(1, 9999));
        }
    }
}
