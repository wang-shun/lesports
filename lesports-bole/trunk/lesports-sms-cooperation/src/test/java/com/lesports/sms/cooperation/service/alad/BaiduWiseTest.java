package com.lesports.sms.cooperation.service.alad;

import com.lesports.AbstractIntegrationTest;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * Created by zhonglin on 2017/3/10.
 */
public class BaiduWiseTest extends AbstractIntegrationTest {
    @Resource
    private BaiduWiseStatsService baiduWiseStatsService;


//    //亚冠
//    @Test
//    public void testBaiduWiseLiveStats(){
//        baiduWiseStatsService.baiduWiseLiveStats(100234001L,"2017afccl","N_alad_yaguan");
//    }
//    @Test
//    public void testBaiduWiseHighlightsStats(){
//        baiduWiseStatsService.baiduWiseHighlightsStats(100234001L,"2017afccl","N_alad_yaguan");
//    }
//
//    @Test
//    public void testBaiduWiseHotStats(){
//        baiduWiseStatsService.baiduWiseHotStats(100234001L,"2017afccl","N_alad_yaguan");
//    }

    //欧冠
    @Test
    public void testBaiduOuguanWiseLiveStats(){
        baiduWiseStatsService.baiduWiseLiveStats(192001L,"2017ouguan","N_alad_ouguan");
    }
    @Test
    public void testBaiduOuguanWiseHighlightsStats(){
        baiduWiseStatsService.baiduWiseHighlightsStats(192001L,"2017ouguan","N_alad_ouguan");
    }

    @Test
    public void testBaiduOuguanWiseHotStats(){
        baiduWiseStatsService.baiduWiseHotStats(192001L,"2017ouguan","N_alad_ouguan");
    }

    //欧联
    @Test
    public void testBaiduOulianWiseLiveStats(){
        baiduWiseStatsService.baiduWiseLiveStats(100100001L,"2017oulian","N_alad_oulian");
    }
    @Test
    public void testBaiduOulianWiseHighlightsStats(){
        baiduWiseStatsService.baiduWiseHighlightsStats(100100001L,"2017oulian","N_alad_oulian");
    }

    @Test
    public void testBaiduOulianWiseHotStats(){
        baiduWiseStatsService.baiduWiseHotStats(100100001L,"2017oulian","N_alad_oulian");
    }

    //F1
    @Test
    public void testBaiduF1WiseLiveStats(){
        baiduWiseStatsService.baiduWiseLiveStats( 50001L,"2017f1","N_alad_f1");
    }
    @Test
    public void testBaiduF1WiseHighlightsStats(){
        baiduWiseStatsService.baiduWiseHighlightsStats( 50001L,"2017f1","N_alad_f1");
    }

    @Test
    public void testBaiduF1WiseHotStats(){
        baiduWiseStatsService.baiduWiseHotStats( 50001L,"2017f1","N_alad_f1");
    }

    //英冠
    @Test
    public void testBaiduYingguanWiseLiveStats(){
        baiduWiseStatsService.baiduWiseLiveStats(100037001L,"2017yingguan","N_alad_yingguan");
    }
    @Test
    public void testBaiduYingguanWiseHighlightsStats(){
        baiduWiseStatsService.baiduWiseHighlightsStats(100037001L,"2017yingguan","N_alad_yingguan");
    }

    @Test
    public void testBaiduYingguanWiseHotStats(){
        baiduWiseStatsService.baiduWiseHotStats(100037001L,"2017yingguan","N_alad_yingguan");
    }

    //法甲
    @Test
    public void testBaiduFajiaWiseLiveStats(){
        baiduWiseStatsService.baiduWiseLiveStats(35001L,"2017fajia","N_alad_fajia");
    }
    @Test
    public void testBaiduFajiaWiseHighlightsStats(){
        baiduWiseStatsService.baiduWiseHighlightsStats(35001L,"2017fajia","N_alad_fajia");
    }

    @Test
    public void testBaiduFajiaWiseHotStats(){
        baiduWiseStatsService.baiduWiseHotStats(35001L,"2017fajia","N_alad_fajia");
    }

    //德甲
    @Test
    public void testBaiduDejiaWiseLiveStats(){
        baiduWiseStatsService.baiduWiseLiveStats(32001L,"2017dejia","N_alad_dejia");
    }
    @Test
    public void testBaiduDejiaWiseHighlightsStats(){
        baiduWiseStatsService.baiduWiseHighlightsStats(32001L,"2017dejia","N_alad_dejia");
    }

    @Test
    public void testBaiduDejiaWiseHotStats(){
        baiduWiseStatsService.baiduWiseHotStats(32001L,"2017dejia","N_alad_dejia");
    }

    //德国杯
    @Test
    public void testBaiduDeguobeiWiseLiveStats(){
        baiduWiseStatsService.baiduWiseLiveStats(120001L,"2017deguobei","N_alad_deguobei");
    }
    @Test
    public void testBaiduDeguobeiWiseHighlightsStats(){
        baiduWiseStatsService.baiduWiseHighlightsStats(120001L,"2017deguobei","N_alad_deguobei");
    }

    @Test
    public void testBaiduDeguobeiWiseHotStats(){
        baiduWiseStatsService.baiduWiseHotStats(120001L,"2017deguobei","N_alad_deguobei");
    }

    //足协杯
    @Test
    public void testBaiduZuxiebeiWiseLiveStats(){
        baiduWiseStatsService.baiduWiseLiveStats(177001L,"2017zuxiebei","N_alad_zuxiebei");
    }
    @Test
    public void testBaiduZuxiebeiWiseHighlightsStats(){
        baiduWiseStatsService.baiduWiseHighlightsStats(177001L,"2017zuxiebei","N_alad_zuxiebei");
    }

    @Test
    public void testBaiduZuxiebeiWiseHotStats(){
        baiduWiseStatsService.baiduWiseHotStats(177001L,"2017zuxiebei","N_alad_zuxiebei");
    }

    //意甲
    @Test
    public void testBaiduYijiaWiseLiveStats(){
        baiduWiseStatsService.baiduWiseLiveStats(29001L,"2017yijia","N_alad_yijia");
    }
    @Test
    public void testBaiduYijiaWiseHighlightsStats(){
        baiduWiseStatsService.baiduWiseHighlightsStats(29001L,"2017yijia","N_alad_yijia");
    }

    @Test
    public void testBaiduYijiaWiseHotStats(){
        baiduWiseStatsService.baiduWiseHotStats(29001L,"2017yijia","N_alad_yijia");
    }

    //足总杯
    @Test
    public void testBaiduZuzongbeiWiseLiveStats(){
        baiduWiseStatsService.baiduWiseLiveStats(100228001L,"2017zuzongbei","N_alad_zuzongbei");
    }
    @Test
    public void testBaiduZuzongbeiWiseHighlightsStats(){
        baiduWiseStatsService.baiduWiseHighlightsStats(100228001L,"2017zuzongbei","N_alad_zuzongbei");
    }

    @Test
    public void testBaiduZuzongbeiWiseHotStats(){
        baiduWiseStatsService.baiduWiseHotStats(100228001L,"2017zuzongbei","N_alad_zuzongbei");
    }

    //社区盾
    @Test
    public void testBaiduShequdunWiseLiveStats(){
        baiduWiseStatsService.baiduWiseLiveStats(101121001L, "2017shequdun", "N_alad_shequdun");
    }
    @Test
    public void testBaiduShequdunWiseHighlightsStats(){
        baiduWiseStatsService.baiduWiseHighlightsStats(101121001L,"2017shequdun","N_alad_shequdun");
    }

    @Test
    public void testBaiduShequdunWiseHotStats(){
        baiduWiseStatsService.baiduWiseHotStats(101121001L,"2017shequdun","N_alad_shequdun");
    }

    //葡超
    @Test
    public void testBaiduPuchaoWiseLiveStats(){
        baiduWiseStatsService.baiduWiseLiveStats(172001L,"2017puchao","N_alad_puchao");
    }
    @Test
    public void testBaiduPuchaoWiseHighlightsStats(){
        baiduWiseStatsService.baiduWiseHighlightsStats(172001L,"2017puchao","N_alad_puchao");
    }

    @Test
    public void testBaiduPuchaoWiseHotStats(){
        baiduWiseStatsService.baiduWiseHotStats(172001L, "2017puchao", "N_alad_puchao");
    }

    //美国职业足球大联盟
    @Test
    public void testBaiduMlsWiseLiveStats(){
        baiduWiseStatsService.baiduWiseLiveStats(62001L,"2017mls","N_alad_mls");
    }
    @Test
    public void testBaiduMlsWiseHighlightsStats(){
        baiduWiseStatsService.baiduWiseHighlightsStats(62001L,"2017mls","N_alad_mls");
    }

    @Test
    public void testBaiduMlsWiseHotStats(){
        baiduWiseStatsService.baiduWiseHotStats(62001L,"2017mls","N_alad_mls");
    }

    //欧冠篮球
    @Test
    public void testBaiduOuguanlanqiuWiseLiveStats(){
        baiduWiseStatsService.baiduWiseLiveStats(180001L,"2017ouguanlanqiu","N_alad_ouguanlanqiu");
    }
    @Test
    public void testBaiduOuguanlanqiuWiseHighlightsStats(){
        baiduWiseStatsService.baiduWiseHighlightsStats(180001L,"2017ouguanlanqiu","N_alad_ouguanlanqiu");
    }

    @Test
    public void testBaiduOuguanlanqiuWiseHotStats(){
        baiduWiseStatsService.baiduWiseHotStats(180001L,"2017ouguanlanqiu","N_alad_ouguanlanqiu");
    }

    //温网
    @Test
    public void testBaiduWenwangWiseLiveStats(){
        baiduWiseStatsService.baiduWiseLiveStats(100825001L,"2017wenwang","N_alad_wenwang");
    }
    @Test
    public void testBaiduWenwangWiseHighlightsStats(){
        baiduWiseStatsService.baiduWiseHighlightsStats(100825001L,"2017wenwang","N_alad_wenwang");
    }

    @Test
    public void testBaiduWenwangWiseHotStats(){
        baiduWiseStatsService.baiduWiseHotStats(100825001L,"2017wenwang","N_alad_wenwang");
    }

    //苏超
    @Test
    public void testBaiduSuchaoWiseLiveStats(){
        baiduWiseStatsService.baiduWiseLiveStats(503001L,"2017suchao","N_alad_suchao");
    }
    @Test
    public void testBaiduSuchaoWiseHighlightsStats(){
        baiduWiseStatsService.baiduWiseHighlightsStats(503001L,"2017suchao","N_alad_suchao");
    }

    @Test
    public void testBaiduSuchaoWiseHotStats(){
        baiduWiseStatsService.baiduWiseHotStats(503001L,"2017suchao","N_alad_suchao");
    }

    //青年欧冠
    @Test
    public void testBaiduQingnianouguanWiseLiveStats(){
        baiduWiseStatsService.baiduWiseLiveStats(100152001L,"2017qingnianouguan","N_alad_qingnianouguan");
    }
    @Test
    public void testBaiduQingnianouguanWiseHighlightsStats(){
        baiduWiseStatsService.baiduWiseHighlightsStats(100152001L,"2017qingnianouguan","N_alad_qingnianouguan");
    }

    @Test
    public void testBaiduQingnianouguanWiseHotStats(){
        baiduWiseStatsService.baiduWiseHotStats(100152001L,"2017qingnianouguan","N_alad_qingnianouguan");
    }

    //J联赛
    @Test
    public void testBaiduJliansaiWiseLiveStats(){
        baiduWiseStatsService.baiduWiseLiveStats(67001L,"2017jliansai","N_alad_jliansai");
    }
    @Test
    public void testBaiduJliansaiWiseHighlightsStats(){
        baiduWiseStatsService.baiduWiseHighlightsStats(67001L,"2017jliansai","N_alad_jliansai");
    }

    @Test
    public void testBaiduJliansaiWiseHotStats(){
        baiduWiseStatsService.baiduWiseHotStats(67001L,"2017jliansai","N_alad_jliansai");
    }

    //澳超
    @Test
    public void testBaiduAochaoWiseLiveStats(){
        baiduWiseStatsService.baiduWiseLiveStats(100170001L,"2017aochao","N_alad_aochao");
    }
    @Test
    public void testBaiduAochaoWiseHighlightsStats(){
        baiduWiseStatsService.baiduWiseHighlightsStats(100170001L,"2017aochao","N_alad_aochao");
    }

    @Test
    public void testBaiduAochaoWiseHotStats(){
        baiduWiseStatsService.baiduWiseHotStats(100170001L,"2017aochao","N_alad_aochao");
    }

    //斯坦科维奇杯
    @Test
    public void testBaiduStankovicWiseLiveStats(){
        baiduWiseStatsService.baiduWiseLiveStats(100920001L,"2017stankovic","N_alad_stankovic");
    }
    @Test
    public void testBaiduStankovicWiseHighlightsStats(){
        baiduWiseStatsService.baiduWiseHighlightsStats(100920001L,"2017stankovic","N_alad_stankovic");
    }

    @Test
    public void testBaiduStankovicWiseHotStats(){
        baiduWiseStatsService.baiduWiseHotStats(100920001L,"2017stankovic","N_alad_stankovic");
    }
}
