package top.forforever.yygh.hosp.service;



import org.springframework.data.domain.Page;
import top.forforever.yygh.model.hosp.Department;
import top.forforever.yygh.vo.hosp.DepartmentVo;

import java.util.List;
import java.util.Map;

/**
 * @create: 2023/3/13
 * @Description:
 * @FileName: DepartmentService
 * @自定义内容：
 */
public interface DepartmentService {

    void saveDepartment(Map<String, Object> switchMap);

    Page getDepartmentPage(Map<String, Object> switchMap);

    void remove(String resultHoscode, String depcode);

    List<DepartmentVo> getDepartmentVoList(String hoscode);

    String getDepName(String hoscode, String depcode);

    Department getDepartment(String hoscode, String depcode);

}
