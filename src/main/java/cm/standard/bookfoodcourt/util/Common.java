package cm.standard.bookfoodcourt.util;

import cm.standard.bookfoodcourt.util.code.TypeCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Slf4j
@Component
public class Common {

    /**
     * PK 생성
     * @param code
     * @return
     */
    public String createPrimaryKey (int code) {
        log.info("Common.createPrimaryKey >>> Create Primary Key");

        final String value = TypeCode.getValueByCode(code);
        if (value == null) {
            log.error("Common.createPrimaryKey >>> value is null");
            return null;
        }

        final String now = this.getNowTimeWithMsToString("yyyyMMddHHmmssSSS");
        final int randomValue = this.randomValue(1, 9999);

        log.info("Common.createPrimaryKey >>> Create Primary Key. now: " + now + ", randomValue: " + randomValue + ", code: " + code);
        return value + now + randomValue;
    }

    public String getNowTimeWithMsToString (String format) {
        try {
            final Instant now = Instant.now();
            final ZoneId koreaZoneId = ZoneId.of("Asia/Seoul");
            final ZonedDateTime koreaTime = now.atZone(koreaZoneId);
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);

            return formatter.format(koreaTime);
        } catch (Exception e) {
            log.error("Common.getNowTimeWithMsToString >>> error", e);
            throw new RuntimeException("오류가 발생했습니다.");
        }
    }

    public int randomValue(int min, int max) {
        return new Random().nextInt(min, max);
    }

    public String removeSpecialEngCharAndSpaces (String str) {
        return str.replaceAll("[^a-zA-Z0-9]", "");
    }

    /**
     * Dto의 모든 값이 null인지 확인
     * String은 빈칸, Integer는 0 값인 경우 null과 같은 처리
     * @param dto
     * @return
     */
    public Boolean isAllNullFromDto (Object dto) {
        if (dto == null) return true;

        BeanWrapper wrapper = new BeanWrapperImpl(dto);
        for (Field field : dto.getClass().getDeclaredFields()) {
            Object value = wrapper.getPropertyValue(field.getName());

            if (value != null) {
                // It is true that type of value is blank, type of int is zero
                if (value instanceof String && ((String) value).isBlank()) {
                    continue;
                }

                if (value instanceof Integer && ((Integer) value).intValue() == 0) {
                    continue;
                }

                return false;
            }
        }

        return true;
    }
}
