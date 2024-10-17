package cm.standard.bookfoodcourt.util.code;

public enum TypeCode {
    MEMBER_JOIN_CODE(10, "MJ"),
    STORE_CODE(20, "SC"),
    HIGH_CATEGORY_CODE(30, "HC"),
    MIDDLE_CATEGORY_CODE(40, "MC"),
    LOW_CATEGORY_CODE(50, "LC"),
    FOOD_INFO_CODE (60, "FI"),
    FOOD_STORE_INFO_CODE(70, "FS"),
    LIKE_FOOD_CODE(80, "LF"),
    CART_FOOD_CODE(90, "CF"),
    PAYMENT_CODE(90, "PAY"),
    ORDER_CODE (100, "OC"),
    FOOD_PROMOTION_CODE (100, "FPC"),
    PROMOTION_CODE (110, "PC"),
    STORE(300, "store"),
    FOOD(310, "food"),
    AUTH_CODE(320, "auth"),
    ADMIN_JOIN_CODE(330, "admin"),
    ;

    private int code;
    private String value;

    TypeCode(int code, String value) {
        this.code = code;
        this.value = value;
    }

    public static String getValueByCode(int code) {
        for (TypeCode status : TypeCode.values()) {
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
