package cm.standard.bookfoodcourt.auth.service;

import cm.standard.bookfoodcourt.dto.AuthDto;
import cm.standard.bookfoodcourt.dto.AuthResultDto;
import cm.standard.bookfoodcourt.mapper.AuthMapper;
import cm.standard.bookfoodcourt.util.Common;
import cm.standard.bookfoodcourt.util.code.AuthTypeCode;
import cm.standard.bookfoodcourt.util.exception.AuthInfoNotFoundException;
import cm.standard.bookfoodcourt.util.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SmsAuthService {
    private final AuthMapper authMapper;
    private final RedisService redisService;
    private final Common common;
    /**
     * 단건 인증 메시지 전송
     * @param to
     * @param verificationCodeMessage
     * @return
     */
    public SingleMessageSentResponse sendSmsOne(String to, String verificationCodeMessage) throws Exception {
        log.info("SmsAuthService.SendSmsOne >>> Start send Verification code");
        String typeCode = AuthTypeCode.getValueByCode("SMS");
        if (typeCode == null) {
            log.error("Not found type code");
            throw new NullPointerException("Type code is null");
        }

        // 값 레디스 조회
        AuthDto authResult = redisService.getData(typeCode, AuthDto.class);
        if (authResult == null) {
            AuthDto authDto = new AuthDto();
            authDto.setApiType(typeCode);
            authResult = authMapper.getAuthInfo(authDto);

            if (authResult == null) {
                log.error("Not found auth info");
                throw new NullPointerException("Auth info is null");
            }

            if (!authResult.getApiUsing()) {
                log.error("SmsAuthService.SendSmsOne >>> Invalid credentials");
                throw new AuthInfoNotFoundException("Invalid credentials");
            }

            redisService.saveData(typeCode, authResult, 1L, TimeUnit.HOURS);
        }

        final DefaultMessageService defaultMessageService = NurigoApp.INSTANCE.initialize(authResult.getApiKey(), authResult.getApiSecretKey(), authResult.getApiDomain());
        Message message = new Message();

        message.setFrom(authResult.getFromNumber());
        message.setTo(to);
        message.setText(verificationCodeMessage);

        return defaultMessageService.sendOne(new SingleMessageSendingRequest(message));
    }

    /**
     * 전송결과 저장
     * @param param
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveAuthResult (SingleMessageSentResponse param) {
        AuthResultDto authResultDto = new AuthResultDto();
        authResultDto.setGroupId(param.getGroupId());
        authResultDto.setSendTo(param.getTo());
        authResultDto.setSendFrom(param.getFrom());
        authResultDto.setMessageType(String.valueOf(param.getType()));
        authResultDto.setStatusMessage(param.getStatusMessage());
        authResultDto.setCountry(param.getCountry());
        authResultDto.setMessageId(param.getMessageId());
        authResultDto.setStatusCode(param.getStatusCode());
        authResultDto.setAccountId(param.getAccountId());

        final String resultId = common.createPrimaryKey(320);
        if (resultId == null) {
            log.error("SmsAuthService.saveAuthResult >>> resultId is null");
            throw new NullPointerException("SmsAuthService.saveAuthResult >>> resultId is null");
        }
        authResultDto.setAuthId(resultId);

        authMapper.saveAuthResult(authResultDto);
    }
}
