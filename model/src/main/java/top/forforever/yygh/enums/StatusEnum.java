package top.forforever.yygh.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @create: 2023/3/21
 * @Description:
 * @FileName: StatusEnum
 * @自定义内容：
 */
@Getter
@AllArgsConstructor
public enum StatusEnum {
    LOCK(0,"锁定"),
    NORMAL(1,"正常")
    ;
    private Integer status;

    private String statusString;

    public static String getStatusStringByStatus(Integer status){
        StatusEnum[] values = StatusEnum.values();
        for (StatusEnum value : values) {
            if (value.getStatus().intValue() == status){
                return value.getStatusString();
            }
        }
        return "";
    }
}
