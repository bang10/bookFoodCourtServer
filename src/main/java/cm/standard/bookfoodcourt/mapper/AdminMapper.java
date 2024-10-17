package cm.standard.bookfoodcourt.mapper;

import cm.standard.bookfoodcourt.dto.BaseAdminDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdminMapper {
    /**
     * 조건에 맞는 admin 조회
     * @param baseAdminDto
     * @return
     */
    public List<BaseAdminDto> getAdminList(BaseAdminDto baseAdminDto);

    /**
     * admin 추가
     * 실제 사용 X
     * @param baseAdminDto
     * @return
     */
    public int addAdmin(BaseAdminDto baseAdminDto);

}
