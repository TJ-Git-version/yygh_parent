package top.forforever.yygh.cmn.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.read.listener.ReadListener;

import java.io.IOException;
import java.util.Map;

/**
 * @create: 2023/3/10
 * @Description:
 * @FileName: ExcelReadListener
 * @自定义内容：
 */
public class ExcelReadListener extends AnalysisEventListener<Student> {


    //每解析excel文件中的一行数据，都会调用一次invoke方法
    @Override
    public void invoke(Student student, AnalysisContext context) {
        System.out.println(student);
    }

    //当解析excel文件某个sheet的标题的时候
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        System.out.println("标题为：" + headMap);
    }

    //当excel文件被解析完毕之后，走这个方法：收尾工作，关闭链接
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {

    }
}
