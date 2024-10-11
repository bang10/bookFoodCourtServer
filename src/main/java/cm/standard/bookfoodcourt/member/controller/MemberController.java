package cm.standard.bookfoodcourt.member.controller;

import cm.standard.bookfoodcourt.dto.BaseUserDto;
import cm.standard.bookfoodcourt.dto.ChangeUserInfoDto;
import cm.standard.bookfoodcourt.member.service.MemberService;
import cm.standard.bookfoodcourt.util.api.ApiResponse;
import cm.standard.bookfoodcourt.util.redis.RedisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api-1/member")
public class MemberController {
    private final MemberService memberService;
    private final RedisService redisService;

    @PostMapping("/join")
    public ResponseEntity<ApiResponse<Integer>> join(@RequestBody @Valid BaseUserDto baseUserDto) throws Exception {
        log.info("회원가입 시작 >>> ID: " + baseUserDto.getUserId());
        ApiResponse<Integer> apiResponse = new ApiResponse<>();
        if (!baseUserDto.getPasscode().equals(baseUserDto.getPasscodeCheck())) {
            apiResponse.code = "994";
            apiResponse.message = "비밀번호, 2차 비밀번호가 일치하지 않습니다.";
            apiResponse.result = null;

            return ResponseEntity.ok(apiResponse);
        }

        // DB 확인
        BaseUserDto userInfoDto = new BaseUserDto();
        userInfoDto.setUserId(baseUserDto.getUserId());
        BaseUserDto userInfo = memberService.getMemberInfo(userInfoDto);
        // 이미 회원인 경우
        if (userInfo != null) {
            apiResponse.code = "999";
            apiResponse.message = "이미 사용중인 ID입니다.";

            return ResponseEntity.ok(apiResponse);
        }

        // 번호인증 여부 확인
        final String resultTellAuth = redisService.getData("JOIN_KEY_" + baseUserDto.getTellNumber(), String.class);
        if (resultTellAuth == null) {
            apiResponse.code = "995";
            apiResponse.message = "번호 인증을 진행해 주세요.";

            return ResponseEntity.ok(apiResponse);
        }

        // 가입
        Integer joinResult = memberService.memberJoin(baseUserDto);
        if (joinResult == 1) {
            apiResponse.code = "0";
            apiResponse.message = "회원가입에 성공했습니다.";

            return ResponseEntity.ok(apiResponse);
        }

        if (joinResult == -1) {
            apiResponse.code = "997";
            apiResponse.message = "이미 가입된 회원입니다.";

            return ResponseEntity.ok(apiResponse);
        }

        if (joinResult == -2) {
            apiResponse.code = "996";
            apiResponse.message = "잘못된 접근입니다.";

            return ResponseEntity.badRequest().body(apiResponse);
        }

        apiResponse.code = "998";
        apiResponse.message = "회원가입에 실패했습니다. 다시 시도해주세요.";

        redisService.deleteData("JOIN_KEY_" + baseUserDto.getTellNumber());

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Boolean>> login(@RequestBody BaseUserDto baseUserDto) throws Exception {
        // TODO 같은 IP로 연속적으로 요청이 온다면 차단

        log.info("Start login ID: " + baseUserDto.getUserId());
        ApiResponse<Boolean> apiResponse = new ApiResponse<>();

        if (baseUserDto.getUserId() == null || baseUserDto.getUserId().isBlank()
        || baseUserDto.getPasscode() == null || baseUserDto.getPasscode().isBlank()) {
            log.info("Login fail because user id or passcode is blank");
            apiResponse.code = "999";
            apiResponse.message = "아이디, 비밀번호를 확인해 주세요.";
            apiResponse.result = false;
            return ResponseEntity.ok(apiResponse);
        }

        final Boolean loginResult = memberService.login(baseUserDto);
        // 미승인된 사용자
        if (loginResult == null) {
            apiResponse.code = "998";
            apiResponse.message = "미승인된 회원입니다.";
            apiResponse.result = false;
            return ResponseEntity.ok(apiResponse);
        }

        if (!loginResult) {
            apiResponse.code = "997";
            apiResponse.message = "일치하는 정보가 없습니다.";
            apiResponse.result = false;
            return ResponseEntity.ok(apiResponse);
        }

        apiResponse.code = "0";
        apiResponse.message = "로그인에 성공했습니다.";
        apiResponse.result = true;
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * 회원 정보 찾기
     * userName, tellNumber는 필수 값
     * userId는 선택
     * @param baseUserDto
     * @return
     * @throws Exception
     */
    @PostMapping("/info/check")
    public ResponseEntity<ApiResponse<String>> infoCheckId(@RequestBody BaseUserDto baseUserDto) throws Exception {
        log.info("MemberController.infoCheck Start >>> ID: " + baseUserDto);
        ApiResponse<String> apiResponse = new ApiResponse<>();

        if (baseUserDto.getUserName() == null || baseUserDto.getUserName().isBlank()
        || baseUserDto.getTellNumber() == null || baseUserDto.getTellNumber().isBlank()) {
            apiResponse.code = "999";
            apiResponse.message = "필수 값이 없습니다.";
            apiResponse.result = null;

            return ResponseEntity.badRequest().body(apiResponse);
        }

        // 번호인증 결과 확인
        String redisResult = "";
        if (baseUserDto.getUserId() == null || baseUserDto.getUserId().isBlank()) {
            redisResult = redisService.getData("ID_KEY_" + baseUserDto.getTellNumber(), String.class);
        } else {
            redisResult = redisService.getData("PASS_KEY_" + baseUserDto.getTellNumber(), String.class);
        }

        if (redisResult == null || redisResult.isBlank() || !redisResult.equals("SUCCESS")) {
            apiResponse.code = "997";
            apiResponse.message = "번호 인증이 만료되었습니다. 다시 진행해주세요.";
            apiResponse.result = null;

            return ResponseEntity.ok(apiResponse);
        }

        if (baseUserDto.getUserId() == null || baseUserDto.getUserId().isBlank()) {
            redisService.deleteData("ID_KEY_" + baseUserDto.getTellNumber());
        } else {
            redisService.deleteData("PASS_KEY_" + baseUserDto.getTellNumber());
            redisService.saveDateSecond("PASS_KEY_CHECK_" + baseUserDto.getUserId(), "SUCCESS", 180L);
        }

        BaseUserDto userInfoDto = new BaseUserDto();
        userInfoDto.setUserName(baseUserDto.getUserName());
        userInfoDto.setTellNumber(baseUserDto.getTellNumber());

        BaseUserDto userInfo = memberService.getMemberInfo(userInfoDto);
        if (userInfo == null) {
            apiResponse.code = "998";
            apiResponse.message = "일치하는 사용자 정보가 없습니다.";
            apiResponse.result = null;

            return ResponseEntity.ok(apiResponse);
        }

        final String status = userInfo.getStatus();
        if (!status.equals("10") && !status.equals("20") && !status.equals("40")) {
            apiResponse.code = "-1";
            apiResponse.message = "탈퇴한 회원이거나 미승인된 회원입니다.";
            apiResponse.result = null;

            return ResponseEntity.ok(apiResponse);
        }

        apiResponse.code = "0";
        apiResponse.message = "성공적으로 조회되었습니다.";
        apiResponse.result = userInfo.getUserId();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * 비밀번호 재설정
     * @param changeUserInfoDto
     * @return
     * @throws Exception
     */
    @PostMapping("/update/passcode")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<ApiResponse<Boolean>> resetPasscode(@RequestBody @Valid ChangeUserInfoDto changeUserInfoDto) throws Exception {
        log.info("MemberController.resetPasscode Start >>> ID: " + changeUserInfoDto.getUserId());
        ApiResponse<Boolean> apiResponse = new ApiResponse<>();

        if (!changeUserInfoDto.getPasscode().equals(changeUserInfoDto.getPasscodeCheck())) {
            apiResponse.code = "998";
            apiResponse.message = "비밀번호, 2차 비밀번호가 일치하지 않습니다.";
            apiResponse.result = false;

            return ResponseEntity.ok(apiResponse);
        }

        final String redisResult = redisService.getData("PASS_KEY_CHECK_" + changeUserInfoDto.getUserId(), String.class);
        if (redisResult == null || !redisResult.equals("SUCCESS")) {
            apiResponse.code = "999";
            apiResponse.message = "인증 정보가 옳바르지 않습니다.";
            apiResponse.result = false;

            return ResponseEntity.ok(apiResponse);
        }

        BaseUserDto updateUserInfoDto = new BaseUserDto();
        updateUserInfoDto.setUserId(changeUserInfoDto.getUserId());
        updateUserInfoDto.setPasscode(changeUserInfoDto.getPasscode());

        final Integer updateResult = memberService.updateBaseUserInfo(updateUserInfoDto);
        if (updateResult == null || updateResult != 1) {
            log.debug("MemberController.resetPasscode Fail >>> ID: " + changeUserInfoDto.getUserId() + ", count: " + updateResult);
            apiResponse.code = "-1";
            apiResponse.message = "오류가 발생했습니다.";
            apiResponse.result = false;

            return ResponseEntity.internalServerError().body(apiResponse);
        }
        // 인증 내역 레디스 삭제
        redisService.deleteData("PASS_KEY_CHECK_" + changeUserInfoDto.getUserId());

        apiResponse.code = "0";
        apiResponse.message = "성공적으로 변경했습니다.";
        apiResponse.result = true;

        return ResponseEntity.ok(apiResponse);

    }
}
