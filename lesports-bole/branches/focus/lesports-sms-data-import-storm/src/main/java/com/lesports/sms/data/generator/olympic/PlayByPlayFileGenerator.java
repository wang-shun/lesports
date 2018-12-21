package com.lesports.sms.data.generator.olympic;

import com.lesports.sms.data.generator.AbstractStatsFileGenerator;

import java.util.List;

/**
 * lesports-bole.
 *
 * @author pangchuanxiao
 * @since 2016/3/23
 */
public class PlayByPlayFileGenerator extends AbstractStatsFileGenerator {
    @Override
    public List<String> getFileName() {
               return getAllFiles("DT_PLAY_BY_PLAY");
    }
}
