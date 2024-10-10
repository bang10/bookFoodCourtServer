package cm.standard.bookfoodcourt.util.code;

public enum AuthTypeCode {
    SMS("SMS", "10"),
    EMAIL("SMS", "20")
    ;

    private String code;
    private String value;

    AuthTypeCode(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public static String getValueByCode(String code) {
        for (AuthTypeCode status : AuthTypeCode.values()) {
            if (status.code.equals(code)) {
                return status.value;
            }
        }
        return null;  // 해당 code가 없을 경우 null 반환
    }

    public String getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}
