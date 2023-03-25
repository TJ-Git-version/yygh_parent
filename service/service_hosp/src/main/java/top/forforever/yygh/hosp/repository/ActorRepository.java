package top.forforever.yygh.hosp.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import top.forforever.yygh.hosp.bean.Actor;

import java.util.List;

/**
 * @create: 2023/3/11
 * @Description:
 * @FileName: ActorRepository
 * @自定义内容：
 */
public interface ActorRepository extends MongoRepository<Actor, String> {

    public List<Actor> findByNameIsLike(String name);

}
