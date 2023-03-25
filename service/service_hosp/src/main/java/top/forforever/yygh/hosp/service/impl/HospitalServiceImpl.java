package top.forforever.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import top.forforever.yygh.client.DictFeignClient;
import top.forforever.yygh.enums.DictEnum;
import top.forforever.yygh.hosp.repository.HospitalRepository;
import top.forforever.yygh.hosp.service.HospitalService;
import top.forforever.yygh.model.hosp.Hospital;
import top.forforever.yygh.vo.hosp.HospitalQueryVo;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @create: 2023/3/12
 * @Description:
 * @FileName: HospitalServiceImpl
 * @自定义内容：
 */
@Service
public class HospitalServiceImpl implements HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DictFeignClient dictFeignClient;

    @Override
    public void saveHospital(Map<String, Object> switchMap) {
        Hospital hospital = JSONObject.parseObject(JSONObject.toJSONString(switchMap), Hospital.class);
        Hospital isHospital = hospitalRepository.findByHoscode(hospital.getHoscode());
        if (isHospital == null) { //平台上没有该医院信息做添加
            //0 未上线 , 1 已上线
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }else {//平台上有该医院信息做修改
            hospital.setId(isHospital.getId());
            hospital.setStatus(isHospital.getStatus());
            hospital.setCreateTime(isHospital.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(isHospital.getIsDeleted());
            hospitalRepository.save(hospital);
        }
    }

    @Override
    public Hospital getHospitalByHoscode(String hoscode) {
        return hospitalRepository.findByHoscode(hoscode);
    }

    @Override
    public Page<Hospital> getHospitalPage(Integer pageNum, Integer pageSize, HospitalQueryVo hospitalQueryVo) {

        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo,hospital);
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                //.withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) //改变默认字符串匹配方式：模糊查询
                .withMatcher("hosname",ExampleMatcher.GenericPropertyMatchers.contains())
                .withIgnoreCase(true); //改变默认大小写忽略方式：忽略大小写
        Example<Hospital> example = Example.of(hospital,matcher);

        PageRequest pageRequest = PageRequest.of(pageNum-1, pageSize, Sort.by("createTime").ascending());
        Page<Hospital> hospitalPage = hospitalRepository.findAll(example, pageRequest);
        hospitalPage.getContent();
        hospitalPage.getContent().forEach(item -> {
            packHospital(item);
        });
        return hospitalPage;
    }

    @Override
    public void updateStatus(String id, Integer status) {
        if (status == 0 || status == 1){
            Hospital hospital = hospitalRepository.findById(id).get();
            hospital.setStatus(status);
            hospital.setUpdateTime(new Date());
            hospitalRepository.save(hospital);
        }
    }

    @Override
    public Hospital getHospById(String id) {
        Hospital hospital = hospitalRepository.findById(id).get();
        packHospital(hospital);
        return hospital;
    }

    @Override
    public List<Hospital> findByHosnameLike(String hosname) {
        return hospitalRepository.findByHosnameLike(hosname);
    }

    @Override
    public Hospital getHospitalDetail(String hoscode) {
        Hospital hospital = hospitalRepository.findByHoscode(hoscode);
        if (hospital != null){
            this.packHospital(hospital);
        }
        return hospital;
    }

    private void packHospital(Hospital hospital){
        String hostype = hospital.getHostype();
        String provinceCode = hospital.getProvinceCode();
        String cityCode = hospital.getCityCode();
        String districtCode = hospital.getDistrictCode();
        String provinceAddress = dictFeignClient.getNameByValue(Long.parseLong(provinceCode));
        String cityAddress = dictFeignClient.getNameByValue(Long.parseLong(cityCode));
        String districtAddress = dictFeignClient.getNameByValue(Long.parseLong(districtCode));
        String nameLevel = dictFeignClient.getNameByDictCodeAndValue(DictEnum.HOSTYPE.getDictCode(), Long.parseLong(hostype));
        hospital.getParam().put("hostypeString",nameLevel);
        hospital.getParam().put("fullAddress",provinceAddress+cityAddress+districtAddress+hospital.getAddress());
    }

}
