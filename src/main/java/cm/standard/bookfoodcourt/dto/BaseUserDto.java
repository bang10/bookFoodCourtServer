package cm.standard.bookfoodcourt.dto;

import lombok.*;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseUserDto extends ViewDto{
    private String memberId;
    private String userId;
    private String passcode;
    private String userName;
    private String tellNumber;
    private LocalDateTime sysRegDate;
    private String sysRegId;
    private LocalDateTime sysModDate;
    private String sysModId;
    private String status;
    private String note;
}
