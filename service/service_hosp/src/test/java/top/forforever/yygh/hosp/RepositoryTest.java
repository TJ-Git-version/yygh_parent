package top.forforever.yygh.hosp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import top.forforever.yygh.hosp.bean.Actor;
import top.forforever.yygh.hosp.repository.ActorRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @create: 2023/3/11
 * @Description:
 * @FileName: RespotryTest
 * @自定义内容：
 */
@SpringBootTest
public class RepositoryTest {

    @Autowired
    private ActorRepository actorRepository;

    //自定义方法
    @Test
    public void testDefaultMethod(){
        List<Actor> actors = actorRepository.findByNameIsLike("周");
        actors.forEach(System.out::println);
    }

    @Test
    public void testFindAll(){
        List<Actor> actorList = actorRepository.findAll();
        System.out.println(actorList);
    }

    @Test
    public void testLinkName(){
        //创建匹配器，即如何使用查询条件
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) //改变默认字符串匹配方式：模糊查询
                .withIgnoreCase(true); //改变默认大小写忽略方式：忽略大小写
        Actor user = new Actor();
        user.setName("周");
        Example<Actor> userExample = Example.of(user, matcher);
        List<Actor> userList = actorRepository.findAll(userExample);
        System.out.println(userList);
    }

    //分页查询
    @Test
    public void testQueryPage(){
        int pageNum = 0;
        int pageSize = 3;
        Pageable pageable = PageRequest.of(pageNum,pageSize, Sort.by("id"));
        Actor actor = new Actor();
        actor.setName("周");
        //创建匹配器，即如何使用查询条件
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) //改变默认字符串匹配方式：模糊查询
                .withIgnoreCase(true); //改变默认大小写忽略方式：忽略大小写
        Example<Actor> example = Example.of(actor,matcher);
        Page<Actor> page = actorRepository.findAll(example, pageable);

        System.out.println("总记录数:"+page.getTotalElements());
        System.out.println("总页数:"+page.getTotalPages());
        List<Actor> actors = page.getContent();
        for (Actor actor1 : actors) {
            System.out.println(actor1);
        }
    }

    //修改
    @Test
    public void testUpdate(){
        Actor actor = actorRepository.findById("25").get();
        actor.setName("小肖");
        actor.setAge(30);
        actorRepository.save(actor);
    }

    //删
    @Test
    public void testDelete(){
        actorRepository.deleteById("23");
    }
    //新增
    @Test
    public void testInsert(){
        List<Actor> actors = new ArrayList<>();
        actors.add(new Actor("23","周",19,true,new Date()));
        actors.add(new Actor("24","周润",19,true,new Date()));
        actors.add(new Actor("25","润发",19,true,new Date()));
        actorRepository.insert(actors);
//        actorRepository.insert(new Actor("20","周润发",19,true,new Date()));
//        actorRepository.save(new Actor("21","小周",22,true,new Date()));
    }

}
