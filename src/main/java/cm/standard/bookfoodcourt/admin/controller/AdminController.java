package cm.standard.bookfoodcourt.admin.controller;

import cm.standard.bookfoodcourt.admin.service.AdminService;
import cm.standard.bookfoodcourt.dto.BaseAdminDto;
import cm.standard.bookfoodcourt.util.api.ApiResponse;
import cm.standard.bookfoodcourt.util.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/api-1")
@CrossOrigin(origins = "http://localhost:9000", allowedHeaders = {"Authorization", "Content-Type"})
public class AdminController {
    private final AdminService adminService;
    private final RedisService redisService;

    /**
     * 관리자 계정 로그인
     * @param baseAdminDto
     * @return
     * @throws Exception
     */
    @PostMapping("/login")
    @CrossOrigin(origins = "http://localhost:9000", allowedHeaders = {"Authorization", "Content-Type"})
    public ResponseEntity<ApiResponse<Boolean>> login(@RequestBody BaseAdminDto baseAdminDto) throws Exception {
        ApiResponse<Boolean> apiResponse = new ApiResponse<>();

        if (baseAdminDto.getUserId() == null || baseAdminDto.getUserId().isBlank()) {
            apiResponse.code = "999";
            apiResponse.message = "필수 값이 없습니다.";
            apiResponse.result = false;

            return ResponseEntity.badRequest().body(apiResponse);
        }

        if (baseAdminDto.getPasscode() == null || baseAdminDto.getPasscode().isBlank()) {
            apiResponse.code = "998";
            apiResponse.message = "필수 값이 없습니다.";
            apiResponse.result = false;

            return ResponseEntity.badRequest().body(apiResponse);
        }

        Integer loginResultCode =  adminService.adminLogin(baseAdminDto);
        if (loginResultCode == null || loginResultCode == -2) {
            apiResponse.code = "997";
            apiResponse.message = "일치하는 정보가 없습니다.";
            apiResponse.result = false;

            return ResponseEntity.ok(apiResponse);
        }

        if (loginResultCode == -1) {
            apiResponse.code = "996";
            apiResponse.message = "미승인 사용자입니다.";
            apiResponse.result = false;

            return ResponseEntity.ok(apiResponse);
        }

        if (loginResultCode == 0) {
            apiResponse.code = "0";
            apiResponse.message = "등록된 전화번호로 인증번호를 전송했습니다. 인증번호를 확인해 주세요.";
            apiResponse.result = true;

            return ResponseEntity.ok(apiResponse);
        }

        apiResponse.code = "-999";
        apiResponse.message = "서버 오류가 발생했습니다.";
        apiResponse.result = false;
        return ResponseEntity.internalServerError().body(apiResponse);
    }

    @PostMapping("/check/code/div")
    public ResponseEntity<ApiResponse<String>> checkCodeDiv(@RequestBody BaseAdminDto baseAdminDto) throws Exception {
        ApiResponse<String> apiResponse = new ApiResponse<>();

        if (baseAdminDto.getUserId() == null || baseAdminDto.getUserId().isBlank()) {
            apiResponse.code = "999";
            apiResponse.message = "필수 값이 없습니다.";
            apiResponse.result = "";

            return ResponseEntity.badRequest().body(apiResponse);
        }

        if (baseAdminDto.getCode() == null || baseAdminDto.getCode().isBlank()) {
            apiResponse.code = "998";
            apiResponse.message = "필수 값이 없습니다.";
            apiResponse.result = "";

            return ResponseEntity.badRequest().body(apiResponse);
        }

        final String redisCode = redisService.getData(baseAdminDto.getUserId(), String.class);
        if (redisCode == null || redisCode.isBlank()) {
            apiResponse.code = "997";
            apiResponse.message = "인증이 만료되었습니다.";
            apiResponse.result = "";

            return ResponseEntity.ok().body(apiResponse);
        }

        if (!redisCode.equals(baseAdminDto.getCode())) {
            apiResponse.code = "996";
            apiResponse.message = "인증번호가 일치하지 않습니다.";
            apiResponse.result = "";

            return ResponseEntity.ok().body(apiResponse);
        }

        BaseAdminDto searchAdminDto = new BaseAdminDto();
        searchAdminDto.setUserId(baseAdminDto.getUserId());

        final BaseAdminDto adminDto = adminService.getAdminInfo(searchAdminDto);
        if (adminDto == null) {
            apiResponse.code = "995";
            apiResponse.message = "일치하는 정보가 없습니다.";
            apiResponse.result = "";

            return ResponseEntity.ok().body(apiResponse);
        }

        Boolean isPermission = adminService.isPermissionMember(adminDto);
        if (!isPermission) {
            apiResponse.code = "994";
            apiResponse.message = "미승인된 사용자입니다.";
            apiResponse.result = "";

            return ResponseEntity.ok().body(apiResponse);
        }

        redisService.deleteData(baseAdminDto.getUserId());

        apiResponse.code = "0";
        apiResponse.message = "인증에 성공했습니다.";
        apiResponse.result = adminDto.getAdminId();

        return ResponseEntity.ok().body(apiResponse);
    }
}
