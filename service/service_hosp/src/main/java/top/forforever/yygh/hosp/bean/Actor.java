package top.forforever.yygh.hosp.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * @create: 2023/3/11
 * @Description:
 * @FileName: Actor
 * @自定义内容：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
//@Document(value = "actor") //指定映射表名
public class Actor {
    //@Id//如果属性的id字段与id不一致使用@id标记该字段为id
    private String id;
    //@Field(value = "name")
    private String name;

    private Integer age;

    private boolean gender;

    private Date birth;
}
