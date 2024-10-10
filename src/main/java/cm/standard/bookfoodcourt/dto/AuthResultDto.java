package cm.standard.bookfoodcourt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResultDto implements Serializable {
    private String authId;
    private String groupId;
    private String sendTo;
    private String sendFrom;
    private String messageType;
    private String statusMessage;
    private String country;
    private String messageId;
    private String statusCode;
    private String accountId;
    private LocalDateTime sysRegDate;

    private String code;
    private String requestRedisType;
}
