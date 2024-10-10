package cm.standard.bookfoodcourt.auth.controller;

import cm.standard.bookfoodcourt.auth.service.SmsAuthService;
import cm.standard.bookfoodcourt.dto.AuthResultDto;
import cm.standard.bookfoodcourt.util.Common;
import cm.standard.bookfoodcourt.util.api.ApiResponse;
import cm.standard.bookfoodcourt.util.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api-1/auth")
public class SmsAuthController {
    private final SmsAuthService smsAuthService;
    private final Common common;
    private final RedisService redisService;

    @PostMapping("/send/sms/{tellNumber}")
    public ResponseEntity<ApiResponse<Boolean>> sendSms(@PathVariable("tellNumber") String tellNumber) throws Exception {
        log.info("Start tellNumber auth check tellNumber: {}", tellNumber);
        ApiResponse<Boolean> apiResponse = new ApiResponse<>();

        final String originTellNumber = common.removeSpecialEngCharAndSpaces(tellNumber);
        final int randomNumber = common.randomValue(10000, 99999);
        String message = "인증번호는 " + randomNumber + "입니다.";

        final SingleMessageSentResponse smsResult = smsAuthService.sendSmsOne(originTellNumber, message);
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
    public ResponseEntity<ApiResponse<Boolean>> checkSms(@RequestBody AuthResultDto authResultDto) throws Exception {
        ApiResponse<Boolean> apiResponse = new ApiResponse<>();
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

        // 회원 가입시 사용되는 레디스
        redisService.saveDateSecond("JOIN_KEY_" + authResultDto.getSendTo(), "SUCCESS", 1800L);

        return ResponseEntity.ok(apiResponse);
    }
}
