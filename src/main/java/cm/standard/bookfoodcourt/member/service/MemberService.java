package cm.standard.bookfoodcourt.member.service;

import cm.standard.bookfoodcourt.dto.BaseUserDto;
import cm.standard.bookfoodcourt.mapper.MemberMapper;
import cm.standard.bookfoodcourt.util.Common;
import cm.standard.bookfoodcourt.util.redis.RedisService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    private final Common common;
    private final RedisService redisService;

    @Transactional(rollbackFor = Exception.class)
    public Integer memberJoin(BaseUserDto baseUserDto) throws Exception {
        final String redisResult = (String) redisService.getData(baseUserDto.getUserId());

        if (redisResult != null) {
            return -1;
        }

        // 컨트롤러 단에서 체크하지만 다시 한번 체크
        if (baseUserDto.getUserId() == null) return null;
        if (baseUserDto.getUserName() == null) return null;
        if (baseUserDto.getPasscode() == null) return null;
        if (baseUserDto.getTellNumber() == null) return null;

        // 있으면 안되는 값
        if (baseUserDto.getMemberId() != null) {
            throw new Exception("잘못된 접근입니다.");
        }

        final String memberId = common.createPrimaryKey(10);
        String sysRegId = "system";
        String status = "30"; // 승인전
        final String encodingPasscode = passwordEncoder.encode(baseUserDto.getPasscode());

        baseUserDto.setMemberId(memberId);
        baseUserDto.setSysRegId(sysRegId);
        baseUserDto.setSysModId(sysRegId);
        baseUserDto.setStatus(status);
        baseUserDto.setPasscode(encodingPasscode);

        final Integer result = memberMapper.joinMember(baseUserDto);
        if (result == null) {
            log.error("MemberService.memberJoin error");
            throw new RuntimeException("회원가입 진행 중 오류가 발생했습니다.");
        }

        if (result != 1) {
            log.error("MemberService.memberJoin error >>> 가입 건 수가" + result + "입니다.");
            throw new RuntimeException("회원가입 진행 중 오류가 발생했습니다.");
        }

        // 동시성 방지 10초 레디스
        redisService.saveDateSecond(baseUserDto.getUserId(), "SUCCESS", 10L);


        return result;
    }

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
