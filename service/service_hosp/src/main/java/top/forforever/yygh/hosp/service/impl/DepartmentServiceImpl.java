package top.forforever.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import top.forforever.yygh.hosp.repository.DepartmentRepository;
import top.forforever.yygh.hosp.service.DepartmentService;
import top.forforever.yygh.model.hosp.Department;
import top.forforever.yygh.vo.hosp.DepartmentVo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @create: 2023/3/13
 * @Description:
 * @FileName: DepartmentServiceImpl
 * @自定义内容：
 */
@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public void saveDepartment(Map<String, Object> switchMap) {
        String jsonString = JSONObject.toJSONString(switchMap);
        Department department = JSONObject.parseObject(jsonString, Department.class);

        //医院编号+科室编号 联合查询
        Department platformDepartment = departmentRepository.findByHoscodeAndDepcode(department.getHoscode(),department.getDepcode());
        if (platformDepartment == null) {
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }else {
            department.setId(platformDepartment.getId());
            department.setCreateTime(platformDepartment.getCreateTime());
            department.setUpdateTime(new Date());
            department.setIsDeleted(platformDepartment.getIsDeleted());
            departmentRepository.save(department);
        }

    }

    @Override
    public Page getDepartmentPage(Map<String, Object> switchMap) {
        Integer pageNum = Integer.parseInt((String) switchMap.get("pageNum"));
        Integer pageSize = Integer.parseInt((String) switchMap.get("limit"));
        Department department = new Department();
        Example<Department> example = Example.of(department);
        Pageable pageable = PageRequest.of(pageNum-1,pageSize);
        Page<Department> page = departmentRepository.findAll(example, pageable);
        return page;
    }

    @Override
    public void remove(String hoscode, String depcode) {
        Department department = departmentRepository.findByHoscodeAndDepcode(hoscode, depcode);
        if (department != null) {
            departmentRepository.deleteById(department.getId());
        }
    }

    @Override
    public List<DepartmentVo> getDepartmentVoList(String hoscode) {
        Department department = new Department();
        department.setHoscode(hoscode);
        Example<Department> example = Example.of(department);
        List<Department> departmentList = departmentRepository.findAll(example);
        //map的key：结束当前科室所属大科室的编号
        //map的value：结束当前大科室底下的所有子科室
        List<DepartmentVo> bigDepartmentList = new ArrayList<>();
        Map<String, List<Department>> collect = departmentList.stream().collect(Collectors.groupingBy(Department::getBigcode));
        System.out.println("科室："+collect);
        for (Map.Entry<String, List<Department>> entry : collect.entrySet()) {
            DepartmentVo bigDepartmentVo = new DepartmentVo();
            //1.大科室编号
            String bigcode = entry.getKey();
            //2.当前大科室地下所有的子科室列表
            List<Department> departments = entry.getValue();
            List<DepartmentVo> childDepartmentVoList = new ArrayList<>();
            departments.forEach(item ->{
                DepartmentVo childDepartmentVo = new DepartmentVo();
                //大科室编号
                String depcode = item.getDepcode();
                //当前大科室地下所有的子科室列表
                String depname = item.getDepname();
                childDepartmentVo.setDepcode(depcode);
                childDepartmentVo.setDepname(depname);
                childDepartmentVoList.add(childDepartmentVo);
            });

            bigDepartmentVo.setDepcode(bigcode);
            bigDepartmentVo.setDepname(departments.get(0).getBigname());
            bigDepartmentVo.setChildren(childDepartmentVoList);
            bigDepartmentList.add(bigDepartmentVo);
        }
        return bigDepartmentList;
    }

    @Override
    public String getDepName(String hoscode, String depcode) {
        Department department = departmentRepository.findByHoscodeAndDepcode(hoscode, depcode);
        if (department != null){
            return department.getDepname();
        }
        return "";
    }

    @Override
    public Department getDepartment(String hoscode, String depcode) {
        return departmentRepository.findByHoscodeAndDepcode(hoscode, depcode);
    }

}
