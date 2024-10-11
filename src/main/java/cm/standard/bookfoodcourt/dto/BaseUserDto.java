package cm.standard.bookfoodcourt.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseUserDto extends ViewDto{
    private String memberId;
    @NotBlank(message = "ID는 필수입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,12}$", message = "영어, 숫자를 조합하여 8자~12자입니다.")
    private String userId;
    @NotBlank(message = "비밀번호는 필수 입니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{12,20}$", message = "영어 대소문자, 숫자, 특수문자 1개 이상을 조합하여 12자~20자입니다.")
    private String passcode;
    private String passcodeCheck;
    @NotBlank (message = "이름은 필수입니다.")
    private String userName;
    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "^010\\d{7,12}$", message = "숫자만 입력해 주세요.")
    private String tellNumber;
    private LocalDateTime sysRegDate;
    private String sysRegId;
    private LocalDateTime sysModDate;
    private String sysModId;
    private String status;
    private String note;
}
