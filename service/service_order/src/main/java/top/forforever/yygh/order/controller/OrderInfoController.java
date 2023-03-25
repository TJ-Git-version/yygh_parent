package top.forforever.yygh.order.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import top.forforever.yygh.common.result.R;
import top.forforever.yygh.common.uitl.AuthContextHolder;
import top.forforever.yygh.enums.OrderStatusEnum;
import top.forforever.yygh.model.order.OrderInfo;
import top.forforever.yygh.model.user.UserInfo;
import top.forforever.yygh.order.service.OrderInfoService;
import top.forforever.yygh.vo.order.OrderCountQueryVo;
import top.forforever.yygh.vo.order.OrderQueryVo;

import java.sql.Struct;
import java.util.Map;

/**
 * <p>
 * 订单表 前端控制器
 * </p>
 *
 * @author forever
 * @since 2023-03-22
 */
@RestController
@RequestMapping("/user/order/orderInfo")
public class OrderInfoController {

    @Autowired
    private OrderInfoService orderInfoService;

    @PostMapping("/getStaCountMap")
    public Map<String,Object> getStaCountMap(@RequestBody OrderCountQueryVo orderCountQueryVo){
       return orderInfoService.getStaCountMap(orderCountQueryVo);
    }
    @GetMapping("/cancel/{orderId}")
    public R cancelOrder(@PathVariable Long orderId){
        orderInfoService.cancelOrder(orderId);
        return R.ok();
    }

    @GetMapping("/detail/{orderId}")
    public R detail(@PathVariable Long orderId) {
        return R.ok().data("orderInfo",orderInfoService.detail(orderId));
    }

    @GetMapping("/getStatusList")
    public R getStatusList(){
        return R.ok().data("statusList",OrderStatusEnum.getStatusList());
    }

    @GetMapping("/{pageNum}/{pageSize}")
    public R getOrderInfoPage(@PathVariable Integer pageNum,
                              @PathVariable Integer pageSize,
                              OrderQueryVo orderQueryVo,
                              @RequestHeader String token){
        Page<OrderInfo> page = null;
        Long userId = AuthContextHolder.getUserId(token);
        if (!StringUtils.isEmpty(userId)){
            page = orderInfoService.getOrderInfoPage(pageNum,pageSize,orderQueryVo,userId);
        }
        return R.ok().data("page",page);
    }

    @PostMapping("/{scheduleId}/{patientId}")
    public R saveOrder(@PathVariable String scheduleId,
                       @PathVariable Long patientId){
        Long orderId = orderInfoService.saveOrder(scheduleId,patientId);
        return R.ok().data("orderId",orderId);
    }

}

