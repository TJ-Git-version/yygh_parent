package top.forforever.yygh.cmn.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.read.listener.ReadListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.BeanUtils;
import top.forforever.yygh.cmn.mapper.DictMapper;
import top.forforever.yygh.cmn.service.DictService;
import top.forforever.yygh.model.cmn.Dict;
import top.forforever.yygh.vo.cmn.DictEeVo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @create: 2023/3/10
 * @Description:
 * @FileName: DictReadListener
 * @自定义内容：
 */
public class DictReadListener extends AnalysisEventListener<DictEeVo> {

    public static final Integer LIMIT_DICT = 10;

    private List<Dict> dictList = new ArrayList<>();

    private DictService dictService;

    public DictReadListener(DictService dictService){
        this.dictService = dictService;
    }

    @Override
    public void invoke(DictEeVo dictEeVo, AnalysisContext context) {
        Dict dict = new Dict();
        BeanUtils.copyProperties(dictEeVo,dict);
        dictList.add(dict);
        if(dictList.size() > LIMIT_DICT){
            dictService.saveOrUpdateBatch(dictList);
        }

//      Integer count = isAlreadyDict(dictEeVo.getId());
//        if (count > 0) {
//            dictMapper.updateById(dict);
//        }else if(dictList.size() > LIMIT_DICT){
//            dictMapper.saveBatch(dictList);
//        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        this.dictService.saveOrUpdateBatch(dictList);
//        dictList.forEach(itemDict -> {
//            Integer count = isAlreadyDict(itemDict.getId());
//            if (count > 0) {
//                dictMapper.updateById(itemDict);
//            }else {
//                dictMapper.save(itemDict);
//            }
//        });
    }
//    public Integer isAlreadyDict(Long id){
//        LambdaQueryWrapper<Dict> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(Dict::getId,id);
//        return dictService.count(queryWrapper);
//    }
}
