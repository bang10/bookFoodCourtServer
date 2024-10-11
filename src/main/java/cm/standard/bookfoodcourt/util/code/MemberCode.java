package cm.standard.bookfoodcourt.util.code;

public enum MemberCode {
    NEW(10, "new"),
    NORMAL(20, "normal"),
    BEFORE_PERMISSION(30, "before-permission"),
    OK(40, "ok"),
    REJECTED(50, "rejected"),
    DELETED(60, "deleted"),
    THROW_OUT(70, "through-out"),

    ;

    private int code;
    private String value;

    MemberCode(int code, String value) {
        this.code = code;
        this.value = value;
    }

    public static String getValueByCode(int code) {
        for (MemberCode status : MemberCode.values()) {
            if (status.code == code) {
                return status.value;
            }
        }
        return null;  // 해당 code가 없을 경우 null 반환
    }

    public int getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}
