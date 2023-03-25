package top.forforever.yygh.hosp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.data.domain.Page;
import top.forforever.yygh.common.result.R;
import top.forforever.yygh.model.hosp.HospitalSet;
import top.forforever.yygh.model.hosp.Schedule;
import top.forforever.yygh.vo.hosp.HospitalSetQueryVo;

import java.util.Map;

/**
 * <p>
 * 医院设置表 服务类
 * </p>
 *
 * @author forever
 * @since 2023-03-05
 */
public interface HospitalSetService extends IService<HospitalSet> {
    //根据条件查询分页信息
    R getPageList(Integer pageNum, Integer pageSize, HospitalSetQueryVo hospitalSetQueryVo);

    String getSignkeyByHoscode(String resultHoscode);

}
