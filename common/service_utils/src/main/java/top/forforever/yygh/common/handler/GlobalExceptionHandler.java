package top.forforever.yygh.common.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.forforever.yygh.common.exception.YyghException;
import top.forforever.yygh.common.result.R;

import java.sql.SQLException;

/**
 * @create: 2023/3/6
 * @Description: 全局异常
 * @FileName: GlobalExceptionHandler
 * @自定义内容：
 */
@RestControllerAdvice //凡是由 @ControllerAdvice 标记的类表示全局异常处理类
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)//粗粒度
    public R handlerException(Exception ex){
        log.error(ex.getMessage());
        ex.printStackTrace(); //输出异常：日志信息
        return R.error().message("全局异常："+ex.getMessage());
    }

    @ExceptionHandler(value = RuntimeException.class)//粗粒度
    public R handlerRuntimeException(RuntimeException ex){
        log.error(ex.getMessage());
        ex.printStackTrace(); //输出异常：日志信息
        return R.error().message("全局运行时异常："+ex.getMessage());
    }

    @ExceptionHandler(value = ArithmeticException.class)//细粒度的异常处理
    public R handlerArithmeticException(ArithmeticException ex){
        log.error(ex.getMessage());
        ex.printStackTrace(); //输出异常：日志信息
        return R.error().message("算数异常："+ex.getMessage());
    }

    @ExceptionHandler(value = SQLException.class)//细粒度的异常处理
    public R handlerSQLException(SQLException ex){
        log.error(ex.getMessage());
        ex.printStackTrace(); //输出异常：日志信息
        return R.error().message("SQL异常："+ex.getMessage());
    }

    @ExceptionHandler(value = YyghException.class)//细粒度的异常处理
    public R handlerYyghException(YyghException ex){
        log.error(ex.getMessage());
        ex.printStackTrace(); //输出异常：日志信息
        return R.error()
                .message(ex.getMessage())
                .code(ex.getCode());
    }
}
