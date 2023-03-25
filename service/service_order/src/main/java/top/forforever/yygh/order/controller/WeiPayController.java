package top.forforever.yygh.order.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.forforever.yygh.common.exception.YyghException;
import top.forforever.yygh.common.result.R;
import top.forforever.yygh.order.service.WeiPayService;

import java.util.Map;

/**
 * @create: 2023/3/23
 * @Description:
 * @FileName: WeixinPayController
 * @自定义内容：
 */
@RestController
@RequestMapping("/user/order/weixin")
public class WeiPayController {

    @Autowired
    private WeiPayService weiPayService;

    //查询二维码状态
    @GetMapping("/status/{orderId}")
    public R getPayStatus(@PathVariable Long orderId) {
        Map<String,String> map =  weiPayService.getPayStatus(orderId);
        //查询不一定成功
        if (map == null) {
            return R.error().message("查询失败");
        }
        if ("SUCCESS".equals(map.get("trade_state"))) { //支付成功
            //更新订单表中的状态    修改支付表中的支付状态
            weiPayService.paySuccess(orderId,map);
            return R.ok();
        }
        return R.ok().message("支付中");
    }

    //生成二维码
    @GetMapping("/{orderId}")
    public R createNative(@PathVariable Long orderId){
        String weiPayUrl = weiPayService.createNative(orderId);
        return R.ok().data("weiPayUrl",weiPayUrl);
    }
}
