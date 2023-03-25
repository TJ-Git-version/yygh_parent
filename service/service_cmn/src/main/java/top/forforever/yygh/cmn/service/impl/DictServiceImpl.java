package top.forforever.yygh.cmn.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import top.forforever.yygh.cmn.listener.DictReadListener;
import top.forforever.yygh.cmn.mapper.DictMapper;
import top.forforever.yygh.cmn.service.DictService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.forforever.yygh.model.cmn.Dict;
import top.forforever.yygh.vo.cmn.DictEeVo;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 组织架构表 服务实现类
 * </p>
 *
 * @author forever
 * @since 2023-03-10
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Override
    @Cacheable(value = "dict",key = "'selectIndexList:'+#pid")
    public List<Dict> getChildListByPid(Long pid) {
        LambdaQueryWrapper<Dict> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dict::getParentId,pid);
        List<Dict> dictList = baseMapper.selectList(queryWrapper);
        dictList.forEach(dict ->{
            dict.setHasChildren(isHasChildren(dict.getId()));
        });
        return dictList;
    }

    @Override
    public void download(HttpServletResponse response) throws IOException {
        List<Dict> dictList = baseMapper.selectList(null);
        List<DictEeVo> dictEeVoList = new ArrayList<>(dictList.size());
        dictList.forEach(dict -> {
            DictEeVo dictEeVo = new DictEeVo();
            BeanUtils.copyProperties(dict,dictEeVo);//要求源对象dict和目标对象dictEeVo对应的属性名必须相同
            dictEeVoList.add(dictEeVo);
        });
        //设置请求头信息
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("数据字典", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        //写出数据，一定要用response.getOutputStream()，否则很严重。。。
        EasyExcel.write(response.getOutputStream(),DictEeVo.class).sheet(0,"数据字典").doWrite(dictEeVoList);
    }

    @Override
    @CacheEvict(value = "dict",allEntries = true)
    public void upload(MultipartFile file) throws IOException {
        EasyExcel.read(file.getInputStream(),DictEeVo.class,new DictReadListener(this))
                .sheet(0).doRead();
    }

    @Override
    @CacheEvict(value = "dict",allEntries = true)
    public void deleteById(String id) {
        LambdaQueryWrapper<Dict> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dict::getParentId,id);
        Integer count = baseMapper.selectCount(queryWrapper);
        if (count > 0) {
            baseMapper.delete(queryWrapper);
            baseMapper.deleteById(id);
        }else {
            baseMapper.deleteById(id);
        }
    }

    @Override
    public String getNameByDictCodeAndValue(String dictcode, Long value) {
        if (StringUtils.isEmpty(dictcode)){
            Dict dictValue =
                    baseMapper.selectOne(new LambdaQueryWrapper<Dict>().eq(!StringUtils.isEmpty(value), Dict::getValue, value));
            return dictValue.getName();
        }else {
            Dict dictDictCode = baseMapper.selectOne(new LambdaQueryWrapper<Dict>()
                    .eq(Dict::getDictCode, dictcode));
            Dict dict = baseMapper.selectOne(new LambdaQueryWrapper<Dict>()
                    .eq(Dict::getParentId, dictDictCode.getId()).eq(Dict::getValue, value));
            return dict.getName();
        }
    }

    private boolean isHasChildren(Long pid) {
        LambdaQueryWrapper<Dict> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dict::getParentId,pid);
        Integer count = baseMapper.selectCount(queryWrapper);
        return count > 0;
    }
}
