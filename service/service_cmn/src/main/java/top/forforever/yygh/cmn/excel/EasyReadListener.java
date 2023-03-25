package top.forforever.yygh.cmn.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.read.listener.ReadListener;

import java.util.Map;

/**
 * @create: 2023/3/10
 * @Description:
 * @FileName: EasyReadListener
 * @自定义内容：
 */
public class EasyReadListener implements ReadListener<Student> {
    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {

    }

    @Override
    public void invokeHead(Map<Integer, CellData> headMap, AnalysisContext context) {
        System.out.println(headMap);
    }

    @Override
    public void invoke(Student student, AnalysisContext context) {
        System.out.println(student);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        System.out.println("结束啦......");
    }

    @Override
    public boolean hasNext(AnalysisContext context) {
        return true;
    }
}
