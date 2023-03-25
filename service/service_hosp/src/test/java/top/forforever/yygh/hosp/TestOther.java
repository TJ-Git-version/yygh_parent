package top.forforever.yygh.hosp;

import org.junit.jupiter.api.Test;
import org.springframework.format.datetime.DateFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @create: 2023/3/24
 * @Description:
 * @FileName: TestOther
 * @自定义内容：
 */
public class TestOther {
    @Test
    public void testStringDate() throws ParseException {

        DateFormatter dateFormatter = new DateFormatter("EEE MMM dd HH:mm:ss zzz yyyy");
        Date date = dateFormatter.parse("Thu Mar 23 15:30:00 CST 2023", Locale.US);
        System.out.println(date);
        dateFormatter.setPattern("yyyy-MM-dd");
        String buildDate = dateFormatter.print(date, Locale.US);
// 2020-01-16
        System.out.println(buildDate);

    }
}
