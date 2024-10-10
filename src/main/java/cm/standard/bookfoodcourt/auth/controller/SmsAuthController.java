package cm.standard.bookfoodcourt.auth.controller;

import cm.standard.bookfoodcourt.auth.service.SmsAuthService;
import cm.standard.bookfoodcourt.util.Common;
import cm.standard.bookfoodcourt.util.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api-1/auth")
public class SmsAuthController {
    private final SmsAuthService smsAuthService;
    private final Common common;

    @PostMapping("/send/sms/{tellNumber}")
    public ResponseEntity<ApiResponse<Boolean>> sendSms(@PathVariable("tellNumber") String tellNumber) throws Exception {
        log.info("Start tellNumber auth check tellNumber: {}", tellNumber);
        ApiResponse<Boolean> apiResponse = new ApiResponse<>();
        Boolean result = false;

        final String originTellNumber = common.removeSpecialEngCharAndSpaces(tellNumber);
        final int randomNumber = common.randomValue(10000, 99999);
        String message = "인증번호는 " + randomNumber + "입니다.";

        final SingleMessageSentResponse smsResult = smsAuthService.sendSmsOne(originTellNumber, message);


        return ResponseEntity.ok(apiResponse);
    }
}
