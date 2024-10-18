package cm.standard.bookfoodcourt.admin.service;

import cm.standard.bookfoodcourt.auth.service.SmsAuthService;
import cm.standard.bookfoodcourt.dto.BaseAdminDto;
import cm.standard.bookfoodcourt.mapper.AdminMapper;
import cm.standard.bookfoodcourt.util.Common;
import cm.standard.bookfoodcourt.util.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {
    private final AdminMapper adminMapper;
    private final Common common;
    private final PasswordEncoder passwordEncoder;
    private final SmsAuthService smsAuthService;
    private final RedisService redisService;

    public Boolean isPermissionMember (BaseAdminDto baseAdminDto) {
        if (baseAdminDto == null) {
            return null;
        }

        final String status = baseAdminDto.getStatus();
        if (!status.equals("10") && !status.equals("20") && !status.equals("40")) {
            return false;
        }

        return true;
    }

    /**
     * 관리자 로그인
     * @param baseAdminDto
     * @return
     * @throws Exception
     */
    public Integer adminLogin(BaseAdminDto baseAdminDto) throws Exception {
        baseAdminDto.setDivisionCode("10");
        final BaseAdminDto adminInfo = this.getAdminInfo(baseAdminDto);
        final Boolean isPermission = this.isPermissionMember(adminInfo);
        if (isPermission == null) {
            return null;
        }

        if (!isPermission) {
            return -1;
        }

        boolean isEqualPasscode = passwordEncoder.matches(baseAdminDto.getPasscode(), adminInfo.getPasscode());
        if (!isEqualPasscode) {
            return -2;
        }

        final int randomNumber = common.randomValue(10000, 99999);
        String message = "인증번호는 " + randomNumber + "입니다.";

        final SingleMessageSentResponse smsResult = smsAuthService.sendSmsOne(adminInfo.getTellNumber(), message);
        smsAuthService.saveAuthResult(smsResult);

        if (smsResult.getStatusCode().equals("2000") || smsResult.getStatusCode().equals("3000") || smsResult.getStatusCode().equals("4000")) {
            // 3분간 저장
            redisService.saveDateSecond(adminInfo.getUserId(), Integer.toString(randomNumber), 180L);
            return 0;
        }

        return -999;
    }

    /**
     * 조건에 맞는 관리자 1명 조회
     * @param baseAdminDto
     * @return
     */
    public BaseAdminDto getAdminInfo(BaseAdminDto baseAdminDto) throws Exception {
        final List<BaseAdminDto> baseAdminDtoList = this.getAdminList(baseAdminDto);
        if (baseAdminDtoList == null || baseAdminDtoList.isEmpty()) {
            return null;
        }
        if (baseAdminDtoList.size() > 1) {
            throw new Exception("2명 이상 조회되었습니다.");
        }
        return baseAdminDtoList.get(0);
    }

    /**
     * 조건에 맞는 관리자 리스트 조회
     * @param baseAdminDto
     * @return
     */
    public List<BaseAdminDto> getAdminList(BaseAdminDto baseAdminDto) {
        return adminMapper.getAdminList(baseAdminDto);
    }

    /**
     * 관리자 저장
     * @param baseAdminDto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public int saveAdmin(BaseAdminDto baseAdminDto) {
        final String adminId = common.createPrimaryKey(330);
        baseAdminDto.setAdminId(adminId);
        baseAdminDto.setPasscode(passwordEncoder.encode(baseAdminDto.getPasscode()));

        return adminMapper.addAdmin(baseAdminDto);
    }
}
