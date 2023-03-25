package top.forforever.yygh.cmn.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @create: 2023/3/10
 * @Description:
 * @FileName: Student
 * @自定义内容：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Student {
    @ExcelProperty("学生id")
    @ColumnWidth(value = 30)
    private Integer id;

    @ExcelProperty("学生姓名")
    @ColumnWidth(value = 30)
    private String name;

    @ExcelProperty("学生年龄")
    @ColumnWidth(value = 30)
    private Integer age;
}
