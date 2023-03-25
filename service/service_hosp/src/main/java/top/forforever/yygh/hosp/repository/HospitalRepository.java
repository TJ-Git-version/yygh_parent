package top.forforever.yygh.hosp.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import top.forforever.yygh.model.hosp.Hospital;

import java.util.List;

/**
 * @create: 2023/3/12
 * @Description:
 * @FileName: HospitalRepository
 * @自定义内容：
 */
public interface HospitalRepository extends MongoRepository<Hospital,String> {
    Hospital findByHoscode(String hoscode);

    List<Hospital> findByHosnameLike(String hosname);
}
