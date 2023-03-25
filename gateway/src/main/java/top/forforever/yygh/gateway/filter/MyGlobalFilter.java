package top.forforever.yygh.gateway.filter;

import com.google.gson.JsonObject;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @create: 2023/3/16
 * @Description:
 * @FileName: MyGlobalFilter
 * @自定义内容：
 */
//@Component
public class MyGlobalFilter implements GlobalFilter, Ordered {

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    //执行过滤功能
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        //api接口，校验必须登录
        if (antPathMatcher.match("/admin/user/**",path)){
            return chain.filter(exchange);
        }else {//对于非登录接口验证：必须登录之后才能通过
            List<String> list = request.getHeaders().get("X-Token");
            if (list == null){
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.SEE_OTHER);
                //路由跳转：
                response.getHeaders().set(HttpHeaders.LOCATION,"http://localhost:9528");
                return response.setComplete();//结束请求
            }else {
                return chain.filter(exchange);
            }
        }
    }

    //影响的是全局过滤器的执行顺序：值越小优先级越高
    @Override
    public int getOrder() {
        return 0;
    }
   // private Mono<Void> out(ServerHttpResponse response) {
//        response.setStatusCode(HttpStatus.FORBIDDEN);
//        JsonObject message = new JsonObject();
//        message.addProperty("success", false);
//        message.addProperty("code", 28004);
//        message.addProperty("data", "鉴权失败");
//        byte[] bits = message.toString().getBytes(StandardCharsets.UTF_8);
//        DataBuffer buffer = response.bufferFactory().wrap(bits);
//        //response.setStatusCode(HttpStatus.UNAUTHORIZED);
//        //指定编码，否则在浏览器中会中文乱码
//        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
//        return response.writeWith(Mono.just(buffer));
//    }
}
