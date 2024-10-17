package cm.standard.bookfoodcourt.util.code;

public enum AdminCode {
    ADMIN(10, "admin"),
    STORE_MANAGER(20, "storeManager"),
    ;
    private int code;
    private String value;

    AdminCode(int code, String value) {
        this.code = code;
        this.value = value;
    }

    public static String getValueByCode(int code) {
        for (AdminCode status : AdminCode.values()) {
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
