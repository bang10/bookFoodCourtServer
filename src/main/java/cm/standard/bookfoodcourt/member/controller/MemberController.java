package cm.standard.bookfoodcourt.member.controller;

import cm.standard.bookfoodcourt.dto.BaseUserDto;
import cm.standard.bookfoodcourt.member.service.MemberService;
import cm.standard.bookfoodcourt.util.api.ApiResponse;
import cm.standard.bookfoodcourt.util.redis.RedisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/join")
    public ResponseEntity<ApiResponse<Integer>> join(@RequestBody @Valid BaseUserDto baseUserDto) throws Exception {
        log.info("회원가입 시작 >>> ID: " + baseUserDto.getUserId());

        // DB 확인
        BaseUserDto userInfoDto = new BaseUserDto();
        userInfoDto.setUserId(baseUserDto.getUserId());

        ApiResponse<Integer> apiResponse = new ApiResponse<>();
        BaseUserDto userInfo = memberService.getMemberInfo(userInfoDto);
        // 이미 회원인 경우
        if (userInfo != null) {
            apiResponse.code = "999";
            apiResponse.message = "이미 사용중인 ID입니다.";

            return ResponseEntity.ok(apiResponse);
        }

        // TODO 번호인증 여부 확인

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

        apiResponse.code = "998";
        apiResponse.message = "회원가입에 실패했습니다. 다시 시도해주세요.";

        return ResponseEntity.ok(apiResponse);
    }

}
