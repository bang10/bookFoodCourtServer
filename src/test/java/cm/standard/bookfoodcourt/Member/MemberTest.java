package cm.standard.bookfoodcourt.Member;

import cm.standard.bookfoodcourt.member.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MemberTest {

    @Autowired
    private MemberService memberService;

    @Test
    public void getMemberListTest() throws Exception {
        System.out.println(memberService.getMemberInfoList(null));
    }
}
