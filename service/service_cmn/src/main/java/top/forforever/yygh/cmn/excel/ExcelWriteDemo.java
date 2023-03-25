package top.forforever.yygh.cmn.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;

import java.util.ArrayList;
import java.util.List;

/**
 * @create: 2023/3/10
 * @Description:
 * @FileName: ExcelWriteDemo
 * @自定义内容：
 */
public class ExcelWriteDemo {

    //方法二：向多个sheet中写入数据
    public static void main(String[] args) {
        List<Student> students = new ArrayList<>();
        students.add(new Student(1,"张三",18));
        students.add(new Student(2,"李四",19));
        students.add(new Student(3,"王五",20));

        List<Student> studentList = new ArrayList<>();
        studentList.add(new Student(1,"赵六",18));
        studentList.add(new Student(2,"力气",19));
        //指定写出的路径和实体类
        ExcelWriter excelWriter = EasyExcel.write("C:\\Users\\29294\\Desktop\\abc.xlsx", Student.class).build();
        //写出多个sheet中
        WriteSheet sheet1 = EasyExcel.writerSheet(0, "学生列表1").build();
        WriteSheet sheet2 = EasyExcel.writerSheet(1, "学生列表2").build();
        //写出的内容
        excelWriter.write(students,sheet1);
        excelWriter.write(studentList,sheet2);
        //关闭流
        excelWriter.finish();
    }

    //方法一：向单个sheet中写入数据
//    public static void main(String[] args) {
//        List<Student> students = new ArrayList<>();
//        students.add(new Student(1,"张三",18));
//        students.add(new Student(2,"李四",19));
//        students.add(new Student(3,"王五",20));
//        EasyExcel.write("C:\\Users\\29294\\Desktop\\hello.xlsx",Student.class).sheet("学生列表1").doWrite(students);
//    }
}
