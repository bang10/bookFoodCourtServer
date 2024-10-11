package cm.standard.bookfoodcourt.mapper;

import cm.standard.bookfoodcourt.dto.AuthDto;
import cm.standard.bookfoodcourt.dto.AuthResultDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthMapper {
    /**
     * 인증에 사용하기 위한 정보 조회
     * @param authDto
     * @return
     */
    public AuthDto getAuthInfo (AuthDto authDto);
    public Integer saveAuthResult (AuthResultDto authResultDto);
}
