package top.forforever.yygh.hosp.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import top.forforever.yygh.model.hosp.Department;
import top.forforever.yygh.vo.hosp.DepartmentVo;

import java.util.List;

/**
 * @create: 2023/3/13
 * @Description:
 * @FileName: DepartmentRepository
 * @自定义内容：
 */
public interface DepartmentRepository extends MongoRepository<Department,String> {
    Department findByHoscodeAndDepcode(String hoscode, String depcode);

}
