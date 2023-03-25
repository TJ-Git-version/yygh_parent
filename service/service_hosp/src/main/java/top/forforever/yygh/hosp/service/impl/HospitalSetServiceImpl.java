package top.forforever.yygh.hosp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.util.StringUtils;
import top.forforever.yygh.common.exception.YyghException;
import top.forforever.yygh.common.result.R;
import top.forforever.yygh.hosp.mapper.HospitalSetMapper;
import top.forforever.yygh.hosp.service.HospitalSetService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.forforever.yygh.model.hosp.HospitalSet;
import top.forforever.yygh.model.hosp.Schedule;
import top.forforever.yygh.vo.hosp.HospitalSetQueryVo;

import java.util.Map;

/**
 * <p>
 * 医院设置表 服务实现类
 * </p>
 *
 * @author forever
 * @since 2023-03-05
 */
@Service
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet> implements HospitalSetService {

    //根据条件查询分页信息
    @Override
    public R getPageList(Integer pageNum, Integer pageSize, HospitalSetQueryVo hospitalSetQueryVo) {
        Page<HospitalSet> page = new Page<>(pageNum,pageSize);

        LambdaQueryWrapper<HospitalSet> queryWrapper = new LambdaQueryWrapper<>();
        //getHosname
        queryWrapper.like(!StringUtils.isEmpty(hospitalSetQueryVo.getHosname()),
                HospitalSet::getHosname,
                hospitalSetQueryVo.getHosname());
        //getHoscode
        queryWrapper.eq(!StringUtils.isEmpty(hospitalSetQueryVo.getHoscode()),
                HospitalSet::getHoscode,
                hospitalSetQueryVo.getHoscode());
        page(page,queryWrapper);
        return R.ok().data("total",page.getTotal()).data("rows",page.getRecords());
    }

    @Override
    public String getSignkeyByHoscode(String resultHoscode) {
        LambdaQueryWrapper<HospitalSet> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HospitalSet::getHoscode,resultHoscode);
        HospitalSet hospitalSet = baseMapper.selectOne(queryWrapper);
        if (!StringUtils.isEmpty(hospitalSet)){
            return hospitalSet.getSignKey();
        }else {
            throw new YyghException(20001,"医院设置信息不存在");
        }

    }


}
