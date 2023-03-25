package top.forforever.yygh.cmn.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;

/**
 * @create: 2023/3/10
 * @Description:
 * @FileName: ExcelReadDemo
 * @自定义内容：
 */
public class ExcelReadDemo {
    //方式二：读多个sheet
    public static void main(String[] args) {
        ExcelReader excelReader = EasyExcel.read("C:\\Users\\29294\\Desktop\\abc.xlsx").build();
        ReadSheet readSheet1 = EasyExcel.readSheet(0).head(Student.class)
                .registerReadListener(new EasyReadListener()).build();
        ReadSheet readSheet2 = EasyExcel.readSheet(1).head(Student.class)
                .registerReadListener(new EasyReadListener()).build();
        excelReader.read(readSheet1,readSheet2);
        excelReader.finish();
    }
//    //简单读
//    public static void main(String[] args) {
//        EasyExcel.read("C:\\Users\\29294\\Desktop\\hello.xlsx",Student.class,new ExcelReadListener())
//                .sheet(0).doRead();
//    }
}
