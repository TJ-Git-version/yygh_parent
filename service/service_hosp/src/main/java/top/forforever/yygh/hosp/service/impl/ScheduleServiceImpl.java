package top.forforever.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import top.forforever.yygh.common.exception.YyghException;
import top.forforever.yygh.hosp.repository.ScheduleRepository;
import top.forforever.yygh.hosp.service.DepartmentService;
import top.forforever.yygh.hosp.service.HospitalService;
import top.forforever.yygh.hosp.service.ScheduleService;
import top.forforever.yygh.hosp.util.DateUtil;
import top.forforever.yygh.model.hosp.BookingRule;
import top.forforever.yygh.model.hosp.Department;
import top.forforever.yygh.model.hosp.Hospital;
import top.forforever.yygh.model.hosp.Schedule;
import top.forforever.yygh.model.order.OrderInfo;
import top.forforever.yygh.vo.hosp.BookingScheduleRuleVo;
import top.forforever.yygh.vo.hosp.ScheduleOrderVo;
import top.forforever.yygh.vo.order.OrderMqVo;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @create: 2023/3/14
 * @Description:
 * @FileName: ScheduleServiceImpl
 * @自定义内容：
 */
@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private DepartmentService departmentService;
    @Override
    public void saveSchedule(Map<String, Object> switchMap) {
        Schedule schedule = JSONObject.parseObject(JSONObject.toJSONString(switchMap), Schedule.class);

        Schedule platFormSchedule = scheduleRepository.findByHoscodeAndDepcodeAndHosScheduleId(schedule.getHoscode(),
                schedule.getDepcode(),schedule.getHosScheduleId());
        if (platFormSchedule == null) {
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            scheduleRepository.save(schedule);
        }else {
            schedule.setCreateTime(platFormSchedule.getCreateTime());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(platFormSchedule.getIsDeleted());
            schedule.setId(platFormSchedule.getId());
            scheduleRepository.save(schedule);
        }
    }

    @Override
    public Page<Schedule> getSchedulePage(Map<String, Object> switchMap) {
        int pageNum = Integer.parseInt(switchMap.get("pageNum").toString());
        int pageSize = Integer.parseInt(switchMap.get("limit").toString());

        Schedule schedule = new Schedule();
        schedule.setHoscode(switchMap.get("hoscode").toString());
        Example<Schedule> example = Example.of(schedule);
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, Sort.by("createTime").ascending());
        Page<Schedule> schedulePage = scheduleRepository.findAll(example, pageRequest);
        return schedulePage;
    }

    @Override
    public void remove(Map<String, Object> switchMap) {
        String hoscode = (String) switchMap.get("hoscode");
        String hosScheduleId = (String) switchMap.get("hosScheduleId");
        Schedule schedule = scheduleRepository.findByHoscodeAndHosScheduleId(hoscode,hosScheduleId);
        if (schedule != null) {
            scheduleRepository.deleteById(schedule.getId());
        }
    }

    @Override
    public Map<String, Object> getPageByHoscodeAndDepcode(Integer pageNum, Integer pageSize, String hoscode, String depcode) {

        //查询条件
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode);

        //聚合：最好使用mongoTemplate
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate")
                           .first("workDate").as("workDate")
                           .count().as("docCount")
                           .sum("reservedNumber").as("reservedNumber")
                           .sum("availableNumber").as("availableNumber"),
                Aggregation.sort(Sort.Direction.ASC,"workDate"),
                Aggregation.skip((pageNum-1)*pageSize),
                Aggregation.limit(pageSize)
        );
        /*
               第一个参数Aggregation：表示聚合条件
               第二个参数InputType：表示输入类型，可以根据当前指定的字节码找到mongo对应集合
               第三个参数OutputType：表示输出类型，封装聚合后的信息
         */
        AggregationResults<BookingScheduleRuleVo> aggregate = mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class);
        //根据聚合条件查询的排班信息
        List<BookingScheduleRuleVo> bookingScheduleRuleVoList = aggregate.getMappedResults();
        bookingScheduleRuleVoList.forEach(bookingScheduleRuleVo ->{
            //将时间转换为周期时间
            String dayOfWeek = DateUtil.getDayOfWeek(new DateTime(bookingScheduleRuleVo.getWorkDate()));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);
        });
        //查询排班总数
        Aggregation aggregationSum = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate")
                        .first("workDate").as("workDate")
        );
        /*
               第一个参数Aggregation：表示聚合条件
               第二个参数InputType：表示输入类型，可以根据当前指定的字节码找到mongo对应集合
               第三个参数OutputType：表示输出类型，封装聚合后的信息
         */
        AggregationResults<BookingScheduleRuleVo> aggregateSum = mongoTemplate.aggregate(aggregationSum, Schedule.class, BookingScheduleRuleVo.class);
        int total = aggregateSum.getMappedResults().size();
        Map<String,Object> map = new HashMap<>();
        map.put("list",bookingScheduleRuleVoList);
        map.put("total",total);

        //获取医院名称
        Hospital hospital = hospitalService.getHospitalByHoscode(hoscode);
        //其他基础数据
        Map<String, String> baseMap = new HashMap<>();
        baseMap.put("hosname",hospital.getHosname());
        map.put("baseMap",baseMap);
        return map;
    }

    @Override
    public List<Schedule> getScheduleDetail(String hoscode, String depcode, String workDate) {
        Date date = new DateTime(workDate).toDate();
        List<Schedule> scheduleList = scheduleRepository.findByHoscodeAndDepcodeAndWorkDate(hoscode,depcode,date);
        if (scheduleList != null){
        //把得到list集合遍历，向设置其他值：医院名称、科室名称、日期对应星期
            scheduleList.forEach(this::packageSchedule);
        }
        return scheduleList;
    }

    @Override
    public Map<String, Object> getUserSchedulePage(String hoscode, String depcode, Integer pageNum, Integer pageSize) {
        Hospital hospital = hospitalService.getHospitalByHoscode(hoscode);
        if (hoscode == null){
            throw new YyghException(20001,"该医院不存在");
        }
        BookingRule bookingRule = hospital.getBookingRule();
        //获取可预约数据分页数据
        IPage<Date> page = this.getListDate(pageNum,pageSize,bookingRule);
        //获取当前页的数据
        List<Date> records = page.getRecords();

        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode).and("workDate").in(records);
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate").first("workDate").as("workDate")
                        .count().as("docCount")
                        .sum("reservedNumber").as("reservedNumber")
                        .sum("availableNumber").as("availableNumber"),
                Aggregation.sort(Sort.Direction.ASC, "workDate")
        );
        AggregationResults<BookingScheduleRuleVo> aggregate = mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> mappedResults = aggregate.getMappedResults();
        Map<Date, BookingScheduleRuleVo> collect = mappedResults.stream().collect(Collectors.toMap(BookingScheduleRuleVo::getWorkDate, bookingScheduleRuleVo -> bookingScheduleRuleVo));

        List<BookingScheduleRuleVo> bookingScheduleRuleVoList = new ArrayList<>();
        int size = records.size();
        for (int i = 0; i < size; i++) {
            Date date = records.get(i);
            BookingScheduleRuleVo bookingScheduleRuleVo = collect.get(date);
            if (bookingScheduleRuleVo == null) {
                bookingScheduleRuleVo = new BookingScheduleRuleVo();
                bookingScheduleRuleVo.setWorkDate(date);
                //bookingScheduleRuleVo.setWorkDateMd(date);
                bookingScheduleRuleVo.setDocCount(0);
                bookingScheduleRuleVo.setAvailableNumber(0);
                bookingScheduleRuleVo.setAvailableNumber(-1);
            }
            bookingScheduleRuleVo.setStatus(0);

            bookingScheduleRuleVo.setWorkDateMd(date);
            bookingScheduleRuleVo.setDayOfWeek(DateUtil.getDayOfWeek(new DateTime(date)));
            DateTime dateTime = this.getDateTime(new Date(), bookingRule.getStopTime());
            //第一页第一条的数据做特殊判断
            if (i == 0 && pageNum == 1){
                //如果医院规定的当前的挂号截止时间在此时此刻之前，说明：此时此刻已经过了当天的挂号截止时间了
                if (dateTime.isBeforeNow()){
                    bookingScheduleRuleVo.setStatus(-1);
                }
            }

            //对最后一条数据进行特色处理
            if (pageNum == page.getPages() && i == (size-1)) {
                bookingScheduleRuleVo.setStatus(1);
            }
            bookingScheduleRuleVoList.add(bookingScheduleRuleVo);
        }

        Map<String,Object> map = new HashMap<>();
        map.put("list",bookingScheduleRuleVoList);
        map.put("total",page.getTotal());

        //其他基础数据
        Map<String, String> baseMap = new HashMap<>();
        //医院名称
        baseMap.put("hosname", hospitalService.getHospitalByHoscode(hoscode).getHosname());
        //科室
        Department department =departmentService.getDepartment(hoscode, depcode);
        //大科室名称
        baseMap.put("bigname", department.getBigname());
        //科室名称
        baseMap.put("depname", department.getDepname());
        //月
        baseMap.put("workDateString", new DateTime().toString("yyyy年MM月"));
        //放号时间
        baseMap.put("releaseTime", bookingRule.getReleaseTime());
        //停号时间
        baseMap.put("stopTime", bookingRule.getStopTime());
        map.put("baseMap",baseMap);
        return map;
    }

    @Override
    public Schedule getScheduleById(String scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).get();
        this.packageSchedule(schedule);
        return schedule;
    }

    @Override
    public ScheduleOrderVo getScheduleOrderVo(String scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).get();
        ScheduleOrderVo scheduleOrderVo = new ScheduleOrderVo();

        BeanUtils.copyProperties(schedule,scheduleOrderVo);
        scheduleOrderVo.setReserveDate(schedule.getWorkDate());
        scheduleOrderVo.setReserveTime(schedule.getWorkTime());

        Hospital hospital = hospitalService.getHospitalByHoscode(schedule.getHoscode());
        scheduleOrderVo.setHosname(hospital.getHosname());

        Department department = departmentService.getDepartment(hospital.getHoscode(), schedule.getDepcode());
        scheduleOrderVo.setDepname(department.getDepname());

        DateTime dateTime = this.getDateTime(new DateTime(schedule.getWorkDate()).plusDays(hospital.getBookingRule().getQuitDay()).toDate(), hospital.getBookingRule().getQuitTime());
        scheduleOrderVo.setQuitTime(dateTime.toDate());//预约挂号截止时间

        //scheduleOrderVo.setStartTime(new DateTime(hospital.getBookingRule().getReleaseTime()).toDate());
        //scheduleOrderVo.setEndTime(new DateTime(hospital.getBookingRule().getStopTime()).toDate());

        //当天停止挂号时间
        scheduleOrderVo.setStopTime((this.getDateTime(schedule.getWorkDate(),hospital.getBookingRule().getStopTime())).toDate());
        return scheduleOrderVo;
    }

    @Override
    public void updateAvailableNumber(String scheduleId, Integer availableNumber) {
        Schedule schedule = scheduleRepository.findById(scheduleId).get();
        schedule.setAvailableNumber(availableNumber);
        schedule.setUpdateTime(new Date());
        scheduleRepository.save(schedule);
    }

    @Override
    public void cancelSchedule(OrderMqVo orderMqVo) {
        Schedule schedule = scheduleRepository.findByHosScheduleId(orderMqVo.getScheduleId());
        schedule.setAvailableNumber(schedule.getAvailableNumber() + 1);
        schedule.setUpdateTime(new Date());
        scheduleRepository.save(schedule);
    }

    private IPage<Date> getListDate(Integer pageNum, Integer pageSize, BookingRule bookingRule) {
        Integer cycle = bookingRule.getCycle();
        //判断此时此刻是否已经超过了医院规定的当天挂号起始时间，如果此时此刻已经超过了：cycle+1
        String releaseTime = bookingRule.getReleaseTime();
        //今天医院规定的挂号的起始时间：2023-3-21 08:30
        DateTime dateTime = this.getDateTime(new Date(), releaseTime);
        if (dateTime.isBeforeNow()){
            cycle=cycle+1;
        }

        //预约周期内所有的时间列表（10天|11天）
        List<Date> list = new ArrayList<>();
        for (int i = 0; i < cycle; i++) {
            //计算当前预约日期
            list.add(new DateTime(new DateTime().plusDays(i).toString("yyyy-MM-dd")).toDate());
        }
        /*
            pageNum：1 2
            pageSize：7 7
            end      7  14
         */
        //获取当前页展示的数据（7）
        int start = (pageNum-1)*pageSize;
        int end = start + pageSize;

        if (end > list.size()){
            end = list.size();
        }
        //获取当前页展示的日期
        List<Date> currentListPage = new ArrayList<>();
        for (int j = start; j < end; j++) {
            Date date = list.get(j);
            currentListPage.add(date);
        }

        //分页展示  获取总记录数 和 封装当前页的日期
        IPage<Date> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum, pageSize, list.size());
        page.setRecords(currentListPage);

        return page;
    }

    /**
     * 将Date日期（yyyy-MM-dd HH:mm）转换为DateTime
     */
    private DateTime getDateTime(Date date, String timeString) {
        String dateTimeString = new DateTime(date).toString("yyyy-MM-dd") + " "+ timeString;
        DateTime dateTime = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").parseDateTime(dateTimeString);
        return dateTime;
    }

    private void packageSchedule(Schedule schedule) {
        //设置医院名称
        schedule.getParam().put("hosname",hospitalService.getHospitalByHoscode(schedule.getHoscode()).getHosname());
        //设置科室名称
        schedule.getParam().put("depname",
                departmentService.getDepName(schedule.getHoscode(),schedule.getDepcode()));
        //设置日期对应星期
        schedule.getParam().put("dayOfWeek",DateUtil.getDayOfWeek(new DateTime(schedule.getWorkDate())));
    }

}
