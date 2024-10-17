package cm.standard.bookfoodcourt.admin;

import cm.standard.bookfoodcourt.admin.service.AdminService;
import cm.standard.bookfoodcourt.dto.BaseAdminDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AdminTest {
    @Autowired
    private AdminService adminService;

    @Test
    public void adminSaveTest() {
        BaseAdminDto baseAdminDto = new BaseAdminDto();
        baseAdminDto.setUserId("vkeh1241");
        baseAdminDto.setPasscode("Alskdj1245!@?");
        baseAdminDto.setUserName("방성환");
        baseAdminDto.setTellNumber("01054404790");
        baseAdminDto.setSysRegId("system");
        baseAdminDto.setSysModId("system");
        baseAdminDto.setStatus("30");
        baseAdminDto.setDivisionCode("10");

        adminService.saveAdmin(baseAdminDto);
    }
}
