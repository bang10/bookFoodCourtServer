package cm.standard.bookfoodcourt.mapper;

import cm.standard.bookfoodcourt.dto.BaseUserDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MemberMapper {
    /**
     * 회원 정보 리스트 조회
     * @param baseUserDto
     * @return
     */
    public List<BaseUserDto> getMemberList(BaseUserDto baseUserDto);

    /**
     * 회원 가입
     * @param baseUserDto
     * @return
     */
    public Integer joinMember(BaseUserDto baseUserDto);

    /**
     * 회원정보 수정
     * @param baseUserDto
     * @return
     */
    public Integer updateMember(BaseUserDto baseUserDto);
}
