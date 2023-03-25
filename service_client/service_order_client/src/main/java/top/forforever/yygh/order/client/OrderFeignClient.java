package top.forforever.yygh.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import top.forforever.yygh.vo.order.OrderCountQueryVo;

import java.util.Map;

/**
 * @create: 2023/3/25
 * @Description:
 * @FileName: OrderFeignClient
 * @自定义内容：
 */
@FeignClient(value = "service-order")
public interface OrderFeignClient {
    @PostMapping("/user/order/orderInfo/getStaCountMap")
    public Map<String,Object> getStaCountMap(@RequestBody OrderCountQueryVo orderCountQueryVo);
}
