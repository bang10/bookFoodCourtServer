package cm.standard.bookfoodcourt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class AuthDto implements Serializable {
    private String apiKey;
    private String apiSecretKey;
    private String keyName;
    private String note;
    private String apiType;
    private Boolean apiUsing;
    private String apiDomain;
    private String fromNumber;
}
