package top.forforever.yygh.hosp.service;

import org.springframework.data.domain.Page;
import top.forforever.yygh.model.hosp.Hospital;
import top.forforever.yygh.vo.hosp.HospitalQueryVo;

import java.util.List;
import java.util.Map;

/**
 * @create: 2023/3/12
 * @Description:
 * @FileName: HospitalService
 * @自定义内容：
 */
public interface HospitalService {
    void saveHospital(Map<String, Object> switchMap);

    Hospital getHospitalByHoscode(String hoscode);

    Page<Hospital> getHospitalPage(Integer pageNum, Integer pageSize, HospitalQueryVo hospitalQueryVo);

    void updateStatus(String id, Integer status);

    Hospital getHospById(String id);

    List<Hospital> findByHosnameLike(String hosname);

    Hospital getHospitalDetail(String hoscode);
}
