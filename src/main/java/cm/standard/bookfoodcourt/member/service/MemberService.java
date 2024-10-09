package cm.standard.bookfoodcourt.member.service;

import cm.standard.bookfoodcourt.dto.BaseUserDto;
import cm.standard.bookfoodcourt.mapper.MemberMapper;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberMapper memberMapper;

    /**
     * 회원 정보 조회 (단일)
     * @param baseUserDto
     * @return
     * @throws Exception
     */
    public BaseUserDto getMemberInfo (BaseUserDto baseUserDto) throws Exception {
        final List<BaseUserDto> memberInfoList = this.getMemberInfoList(baseUserDto);
        if (memberInfoList.size() > 1) {
            throw new Exception("검색된 사용자가 2명 이상입니다.");
        }

        if (memberInfoList.isEmpty()) {
            return null;
        }

        return memberInfoList.get(0);
    }
    /**
     * 회원 정보 조회 (리스트)
     * @param baseUserDto
     * @return
     * @throws Exception
     */
    public List<BaseUserDto> getMemberInfoList (BaseUserDto baseUserDto) {
        return memberMapper.getMemberList(baseUserDto);
    }

}
