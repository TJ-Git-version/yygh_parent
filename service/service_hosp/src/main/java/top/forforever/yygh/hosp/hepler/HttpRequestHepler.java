package top.forforever.yygh.hosp.hepler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @create: 2023/3/12
 * @Description:
 * @FileName: HttpRequestHandler
 * @自定义内容：
 */
public class HttpRequestHepler {
    public static Map<String, Object> switchMap(Map<String, String[]> parameterMap) {
        Set<Map.Entry<String, String[]>> entries = parameterMap.entrySet();
        Map<String,Object> resultMap = new HashMap<>();
        entries.forEach(entrie ->{
            resultMap.put(entrie.getKey(),entrie.getValue()[0]);
        });
        return resultMap;
    }
}
