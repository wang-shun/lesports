package jsonPathutils;

import com.lesports.sms.data.util.HttpRestUtil;
import com.lesports.sms.data.util.JsonPath.JsonParser;
import com.lesports.sms.data.util.JsonPath.JsonParserFactory;
import com.lesports.utils.xml.Parser;
import com.lesports.utils.xml.ParserFactory;
import com.lesports.utils.xml.ParserFactory2;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;

/**
 * lesports-projects
 *
 * @author pangchuanxiao
 * @since 16-2-16
 */
public class ParserTest extends TestCase {
    @Test
    public void testParse() throws Exception {
        String jsonData = HttpRestUtil.getData("http://matchweb.sports.qq.com/kbs/list?from=NBA_PC&columnId=100000&startTime=2016-12-22&endTime=2016-12-28&_=1482387145120");
        JsonParser<ScheduleTest> parser = new JsonParserFactory().createJsonParser(ScheduleTest.class);
        ScheduleTest result = parser.parse("JsonPathQQ", jsonData);
        Assert.assertNotNull(result);
    }


}
