package cm.standard.bookfoodcourt.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeUserInfoDto {
    @NotBlank(message = "ID는 필수입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,12}$", message = "영어, 숫자를 조합하여 8자~12자입니다.")
    private String userId;
    @NotBlank(message = "비밀번호는 필수 입니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{12,20}$", message = "영어 대소문자, 숫자, 특수문자 1개 이상을 조합하여 12자~20자입니다.")
    private String passcode;
    private String passcodeCheck;
    private String userName;
}
