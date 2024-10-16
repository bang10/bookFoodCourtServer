package cm.standard.bookfoodcourt.auth.controller;

import cm.standard.bookfoodcourt.auth.service.SmsAuthService;
import cm.standard.bookfoodcourt.dto.AuthResultDto;
import cm.standard.bookfoodcourt.dto.BaseUserDto;
import cm.standard.bookfoodcourt.dto.ChangeUserInfoDto;
import cm.standard.bookfoodcourt.util.Common;
import cm.standard.bookfoodcourt.util.api.ApiResponse;
import cm.standard.bookfoodcourt.util.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api-1/auth")
public class SmsAuthController {
    private final SmsAuthService smsAuthService;
    private final Common common;
    private final RedisService redisService;

    @PostMapping("/send/sms")
    public ResponseEntity<ApiResponse<Boolean>> sendSms(@RequestBody BaseUserDto baseUserDto) throws Exception {
        final String tellNumber = baseUserDto.getTellNumber();

        log.info("Start tellNumber auth check tellNumber: {}", tellNumber);
        ApiResponse<Boolean> apiResponse = new ApiResponse<>();

        if (tellNumber == null || tellNumber.isEmpty()) {
            apiResponse.code = "998";
            apiResponse.message = "전화번호는 필수입니다.";
            apiResponse.result = false;

            return ResponseEntity.ok(apiResponse);
        }

        final boolean isMatch = tellNumber.matches("^010\\d{7,12}$");
        if (!isMatch) {
            apiResponse.code = "997";
            apiResponse.message = "전화번호는 숫자만 옳바른 형식에 맞게 입력해 주세요.";
            apiResponse.result = false;

            return ResponseEntity.ok(apiResponse);
        }

        final String originTellNumber = common.removeSpecialEngCharAndSpaces(tellNumber);
        final int randomNumber = common.randomValue(10000, 99999);
        String message = "인증번호는 " + randomNumber + "입니다.";

        final SingleMessageSentResponse smsResult = smsAuthService.sendSmsOne(originTellNumber, message);
        if (smsResult == null) {
            apiResponse.code = "997";
            apiResponse.message = "전화번호는 숫자만 입력해 주세요.";
            apiResponse.result = false;

            return ResponseEntity.ok(apiResponse);
        }

        smsAuthService.saveAuthResult(smsResult);

        if (smsResult.getStatusCode().equals("2000") || smsResult.getStatusCode().equals("3000") || smsResult.getStatusCode().equals("4000")) {
            // 3분간 저장
            redisService.saveDateSecond(originTellNumber, Integer.toString(randomNumber), 180L);

            apiResponse.code = "0";
            apiResponse.message = "성공적으로 전송했습니다. 메시지를 확인해주세요.";
            apiResponse.result = true;
            return ResponseEntity.ok(apiResponse);
        }

        log.error("SmsAuthController sendSms error: {}", smsResult);
        apiResponse.code = "999";
        apiResponse.message = "메시지 전송 서버 오류가 발생했습니다. 잠시후 다시 시도해주세요.";
        apiResponse.result = false;

        return ResponseEntity.internalServerError().body(apiResponse);
    }

    @PostMapping("/check/sms")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<ApiResponse<Boolean>> checkSms(@RequestBody AuthResultDto authResultDto) throws Exception {
        ApiResponse<Boolean> apiResponse = new ApiResponse<>();
        if (authResultDto.getRequestRedisType() == null) {
            apiResponse.code = "993";
            apiResponse.code = "requestRedisType is a required value.";
            apiResponse.result = false;

            return ResponseEntity.badRequest().body(apiResponse);
        }

        String key = "";
        switch (authResultDto.getRequestRedisType()) {
            case "join" -> key = "JOIN_KEY_";
            case "id" -> key = "ID_KEY_";
            case "pass" -> key = "PASS_KEY_";
            case "manage" -> key = "ADMIN_MANEGE_KEY_"; // 관리자 ID
            case "manager" -> key = "MANAGER_KEY_"; // 관리자 PW
            default -> {
                log.debug("SmsAuthController checkSms >>> Not found key code: {}", authResultDto);

                apiResponse.code = "994";
                apiResponse.message = "일치하는 코드가 없습니다.";
                apiResponse.result = false;

                return ResponseEntity.ok(apiResponse);
            }
        }

        if (authResultDto.getSendTo() == null || authResultDto.getCode() == null) {
            apiResponse.code = "998";
            apiResponse.message = "전화번호, 인증번호는 필수 값입니다.";
            apiResponse.result = false;

            return ResponseEntity.ok(apiResponse);
        }
        if (authResultDto.getSendTo().isBlank() || authResultDto.getCode().isBlank()) {
            apiResponse.code = "998";
            apiResponse.message = "전화번호, 인증번호는 필수 값입니다.";
            apiResponse.result = false;

            return ResponseEntity.ok(apiResponse);
        }

        final String checkTellNumberAuth = redisService.getData(authResultDto.getSendTo(), String.class);
        if (checkTellNumberAuth == null) {
            apiResponse.code = "999";
            apiResponse.message = "인증번호 전송이 안되었거나 만료되었습니다. 다시 진행해주세요.";
            apiResponse.result = false;

            return ResponseEntity.ok(apiResponse);
        }

        boolean result = checkTellNumberAuth.equals(authResultDto.getCode());
        if (!result) {
            apiResponse.code = "998";
            apiResponse.message = "인증번호가 일치하지 않습니다. 다시 확인해 주세요.";
            apiResponse.result = false;

            return ResponseEntity.ok(apiResponse);
        }
        redisService.deleteData(authResultDto.getSendTo());

        apiResponse.code = "0";
        apiResponse.message = "인증번호가 일치합니다.";
        apiResponse.result = true;

        // 타입에 맞는 인증결과 저장
        redisService.saveDateSecond(key + authResultDto.getSendTo(), "SUCCESS", 1800L);

        return ResponseEntity.ok(apiResponse);
    }
}
