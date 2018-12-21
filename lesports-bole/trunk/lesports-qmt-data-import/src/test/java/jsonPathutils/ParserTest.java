package jsonPathutils;

import com.lesports.sms.data.util.HttpRestUtil;
import com.lesports.sms.data.util.JsonPath.JsonParser;
import com.lesports.sms.data.util.JsonPath.JsonParserFactory;
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
        String jsonData = HttpRestUtil.getData("http://ziliaoku.sports.qq.com/cube/index?cubeId=10&dimId=9,10&params=t2:2016|t3:1|t1:3704&from=sportsdatabase");
        JsonParser<ScheduleTest> parser = new JsonParserFactory().createJsonParser(ScheduleTest.class);
        ScheduleTest result = parser.parse("JsonPathQQ", jsonData);
        Assert.assertNotNull(result);
    }


}
