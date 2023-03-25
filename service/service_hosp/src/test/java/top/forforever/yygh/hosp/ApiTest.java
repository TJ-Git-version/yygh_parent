package top.forforever.yygh.hosp;

import com.mongodb.client.result.DeleteResult;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import top.forforever.yygh.hosp.bean.Actor;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @create: 2023/3/11
 * @Description:
 * @FileName: ApiTest
 * @自定义内容：
 */
@SpringBootTest
public class ApiTest {

    @Autowired
    private MongoTemplate mongoTemplate;



    @Test
    public void testRandom(){
        for (int i = 0; i < 20; i++) {

            System.out.println((int) (Math.random()*10+100));
        }
    }

    @Test
    public void testDateTime(){
        System.out.println(new DateTime());
    }

    //分页查询
    @Test
    public void testQueryPage(){
        int pageNum = 1;
        int pageSize = 3;
        Query query = new Query(Criteria.where("gender").is(true));
        long count = mongoTemplate.count(query, Actor.class);
        List<Actor> actors = mongoTemplate.find(query.skip((pageNum-1)*pageSize).limit(pageSize), Actor.class);
        Map<String, Object> map = new HashMap<>();
        map.put("total",count);
        map.put("rows",actors);
        System.out.println(count);
        for (Actor actor : actors) {
            System.out.println(actor);
        }
    }

    //查询
    @Test
    public void testFind(){
        /*
        Actor actor = mongoTemplate.findById("2", Actor.class);
        Actor actor = mongoTemplate.findOne(query, Actor.class);
        List<Actor> actors = mongoTemplate.find(query, Actor.class);
         */
        String format = String.format("%s%s%s", ".*", "y", ".*");
        Pattern pattern = Pattern.compile(format,Pattern.CASE_INSENSITIVE);
        Query query = new Query(Criteria.where("name").regex(pattern));
        List<Actor> actors = mongoTemplate.find(query, Actor.class);
        actors.forEach(System.out::println);
    }

    //修改方法
    //upsert：如何表中有该数据则修改，没有则添加
    //updateFirst：只修改满足条件的第一个文档
    //updateMulti：修改满足条件的所有文档
    @Test
    public void testUpdate(){
        Query query = new Query(Criteria.where("gender").is(true));
        Update update = new Update();
        update.set("age",21);
        update.set("birth",new Date());
        mongoTemplate.updateMulti(query,update,Actor.class);
    }

    //删除操作
    //and关系：Criteria.where("_id").is("1").and("name").is("刘德华")
    /*
    or关系：
        Criteria criteria = new Criteria();
        criteria.orOperator(Criteria.where("_id").is("1"),Criteria.where("name").is("刘德华"));
        Query query = new Query(criteria);
     */
    @Test
    public void testDelete(){
        Criteria criteria = new Criteria();
        criteria.orOperator(Criteria.where("_id").is("1"),Criteria.where("name").is("刘德华"));
        Query query = new Query(criteria);
        DeleteResult deleteResult = mongoTemplate.remove(query,Actor.class);
        System.out.println(deleteResult.getDeletedCount());
    }

    //批量添加只能使用insert
    @Test
    public void testBatchInsert(){
        List<Actor> actors = new ArrayList<>();
        actors.add(new Actor("7","Tom",19,true,new Date()));
        actors.add(new Actor("6","jack",20,false,new Date()));
        actors.add(new Actor("8","Jeery",19,true,new Date()));
        mongoTemplate.insert(actors,Actor.class);
    }

    //要想用save实现修改，先查询，再插入
    @Test
    public void testSave(){
        Actor actor = mongoTemplate.findById("1", Actor.class);
        actor.setName("张学友");
        mongoTemplate.save(actor);
    }

    //新增：可使用insert、save
    //insert：只能新增，不能修改
    //save：既能新增，也能修改
    @Test
    public void testMongoInsert(){
        mongoTemplate.insert(new Actor("9","yyyy",18,true,new Date()));
    }
}
