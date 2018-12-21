package com.lesports.sms.data.parser;

import com.lesports.sms.data.Constants;
import com.lesports.utils.excel.ExcelParser;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

import static com.lesports.sms.data.Constants.*;

/**
 * lesports-bole.
 *
 * @author pangchuanxiao
 * @since 2016/4/18
 */
public class FileParserFactory implements Serializable {
    private final XmlFileParser xmlParser = new XmlFileParser();
    private final ExcelFileParser excelParser = new ExcelFileParser();
    private final DocumentFileParser documentParser = new DocumentFileParser();

    public FileParser getFileParser(String code, String fileType) {
        if (fileType.endsWith(StringUtils.upperCase(FILE_TYPE_EXCEL)) || fileType.endsWith(StringUtils.lowerCase(FILE_TYPE_EXCEL))) {
            return excelParser;
        } else if ((fileType.endsWith(StringUtils.upperCase(FILE_TYPE_XML)) || fileType.endsWith(StringUtils.lowerCase(FILE_TYPE_XML)))) {
            if (code.equalsIgnoreCase(CODE_LIVE_RESULT)) {
                return documentParser;
            }
            return xmlParser;
        }
        return null;
    }
}
