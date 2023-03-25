package top.forforever.yygh.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @create: 2023/3/6
 * @Description: 自定义异常
 * @FileName: YyghException
 * @自定义内容：
 */
@Data
@AllArgsConstructor
public class YyghException extends RuntimeException{

    private Integer code;

    private String message;

}
