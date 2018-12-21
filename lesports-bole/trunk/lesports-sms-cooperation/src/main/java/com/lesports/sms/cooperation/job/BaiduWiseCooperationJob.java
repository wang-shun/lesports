package com.lesports.sms.cooperation.job;

import com.lesports.sms.cooperation.service.alad.BaiduWiseStatsService;
import com.lesports.sms.cooperation.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by zhonglin on 2017/3/9.
 */
@Service("baiduWiseCooperationJob")
public class BaiduWiseCooperationJob {

    private static Logger logger = LoggerFactory.getLogger(AladCooperationJob.class);

    @Resource
    private BaiduWiseStatsService baiduWiseStatsService;

    /**
     *  baidu wise live 数据
     */
    public void baiduWiseLiveStats() {
        Map<Long,String> baiduWiseMap = Constants.baiduWiseMap;
        for(Long id:baiduWiseMap.keySet()){
            String key =  baiduWiseMap.get(id);
            logger.info("【baidu "+baiduWiseMap.get(id)+" live mob   Stats】 start");
            baiduWiseStatsService.baiduWiseLiveStats(id,"2017"+key,"N_alad_"+key);
            logger.info("【baidu "+baiduWiseMap.get(id)+" live mob   Stats】 end");
        }

    }

    /**
     *  baidu wise highlights 数据
     */
    public void baiduWiseHighlightsStats() {
        Map<Long,String> baiduWiseMap = Constants.baiduWiseMap;
        for(Long id:baiduWiseMap.keySet()) {
            String key = baiduWiseMap.get(id);
            logger.info("【baidu " + baiduWiseMap.get(id) + " highlights mob   Stats】 start");
            baiduWiseStatsService.baiduWiseHighlightsStats(id, "2017" + key, "N_alad_" + key);
            logger.info("【baidu " + baiduWiseMap.get(id) + " highlights mob   Stats】 end");
        }

    }

    /**
     *  baidu wise hot 数据
     */
    public void baiduWiseHotStats() {
        Map<Long,String> baiduWiseMap = Constants.baiduWiseMap;
        for(Long id:baiduWiseMap.keySet()){
            String key =  baiduWiseMap.get(id);

            logger.info("【baidu "+baiduWiseMap.get(id)+" hot mob   Stats】 start");
            baiduWiseStatsService.baiduWiseHotStats(id,"2017"+key,"N_alad_"+key);
            logger.info("【baidu "+baiduWiseMap.get(id)+" hot mob   Stats】 end");
        }

    }

//    /**
//     *  baidu affccl live mob数据
//     */
//    public void baiduAfcclLiveStats() {
//        logger.info("【baidu affccl live mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseLiveStats(100234001L,"2017afccl","N_alad_yaguan");
//        logger.info("【baidu affccl live mob   Stats】 end");
//    }
//
//    /**
//     *  baidu affccl Highlights mob数据
//     */
//    public void baiduAfcclHighlightsStats() {
//        logger.info("【baidu affccl highlights mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseHighlightsStats(100234001L,"2017afccl","N_alad_yaguan");
//        logger.info("【baidu affccl highlights mob   Stats】 end");
//    }
//
//    /**
//     *  baidu affccl  mob数据
//     */
//    public void baiduAfcclHotStats() {
//        logger.info("【baidu affccl hot mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseHotStats(100234001L,"2017afccl","N_alad_yaguan");
//        logger.info("【baidu affccl hot mob   Stats】 end");
//    }

//    /**
//     *  baidu ouguan live mob数据
//     */
//    public void baiduOuGuanLiveStats() {
//        logger.info("【baidu ouguan live mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseLiveStats(192001L,"2017ouguan","N_alad_ouguan");
//        logger.info("【baidu ouguan live mob   Stats】 end");
//    }
//
//    /**
//     *  baidu ouguan Highlights mob数据
//     */
//    public void baiduOuguanHighlightsStats() {
//        logger.info("【baidu ouguan highlights mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseHighlightsStats(192001L,"2017ouguan","N_alad_ouguan");
//        logger.info("【baidu ouguan highlights mob   Stats】 end");
//    }
//
//    /**
//     *  baidu ouguan  mob数据
//     */
//    public void baiduOuguanHotStats() {
//        logger.info("【baidu ouguan hot mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseHotStats(192001L,"2017ouguan","N_alad_ouguan");
//        logger.info("【baidu ouguan hot mob   Stats】 end");
//    }
//
//    /**
//     *  baidu oulian live mob数据
//     */
//    public void baiduOulianLiveStats() {
//        logger.info("【baidu oulian live mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseLiveStats(100100001L,"2017oulian","N_alad_oulian");
//        logger.info("【baidu oulian live mob   Stats】 end");
//    }
//
//    /**
//     *  baidu oulian Highlights mob数据
//     */
//    public void baiduOulianHighlightsStats() {
//        logger.info("【baidu oulian highlights mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseHighlightsStats(100100001L,"2017oulian","N_alad_oulian");
//        logger.info("【baidu oulian highlights mob   Stats】 end");
//    }
//
//    /**
//     *  baidu oulian  mob数据
//     */
//    public void baiduOulianHotStats() {
//        logger.info("【baidu oulian hot mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseHotStats(100100001L,"2017oulian","N_alad_oulian");
//        logger.info("【baidu oulian hot mob   Stats】 end");
//    }
//
//    /**
//     *  baidu f1 live mob数据
//     */
//    public void baiduF1LiveStats() {
//        logger.info("【baidu f1 live mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseLiveStats( 50001L,"2017f1","N_alad_f1");
//        logger.info("【baidu f1 live mob   Stats】 end");
//    }
//
//    /**
//     *  baidu f1 Highlights mob数据
//     */
//    public void baiduF1HighlightsStats() {
//        logger.info("【baidu f1 highlights mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseHighlightsStats( 50001L,"2017f1","N_alad_f1");
//        logger.info("【baidu f1 highlights mob   Stats】 end");
//    }
//
//    /**
//     *  baidu f1  mob数据
//     */
//    public void baiduF1HotStats() {
//        logger.info("【baidu f1 hot mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseHotStats( 50001L,"2017f1","N_alad_f1");
//        logger.info("【baidu f1 hot mob   Stats】 end");
//    }
//
//    /**
//     *  baidu yingguan live mob数据
//     */
//    public void baiduYingguanLiveStats() {
//        logger.info("【baidu yingguan live mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseLiveStats(100037001L,"2017yingguan","N_alad_yingguan");
//        logger.info("【baidu yingguan live mob   Stats】 end");
//    }
//
//    /**
//     *  baidu yingguan  mob数据
//     */
//    public void baiduYingguanHighlightsStats() {
//        logger.info("【baidu yingguan highlights mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseHighlightsStats(100037001L,"2017yingguan","N_alad_yingguan");
//        logger.info("【baidu yingguan highlights mob   Stats】 end");
//    }
//
//    /**
//     *  baidu yingguan  mob数据
//     */
//    public void baiduYingguanHotStats() {
//        logger.info("【baidu yingguan hot mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseHotStats(100037001L,"2017yingguan","N_alad_yingguan");
//        logger.info("【baidu yingguan hot mob   Stats】 end");
//    }
//
//    /**
//     *  baidu fajia live mob数据
//     */
//    public void baiduFajiaLiveStats() {
//        logger.info("【baidu fajia live mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseLiveStats(35001L,"2017fajia","N_alad_fajia");
//        logger.info("【baidu fajia live mob   Stats】 end");
//    }
//
//    /**
//     *  baidu fajia fajia mob数据
//     */
//    public void baiduFajiaHighlightsStats() {
//        logger.info("【baidu fajia highlights mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseHighlightsStats(35001L,"2017fajia","N_alad_fajia");
//        logger.info("【baidu fajia highlights mob   Stats】 end");
//    }
//
//    /**
//     *  baidu fajia  mob数据
//     */
//    public void baiduFajiaHotStats() {
//        logger.info("【baidu fajia hot mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseHotStats(35001L,"2017fajia","N_alad_fajia");
//        logger.info("【baidu fajia hot mob   Stats】 end");
//    }
//
//    /**
//     *  baidu dejia live mob数据
//     */
//    public void baiduDejiaLiveStats() {
//        logger.info("【baidu dejia live mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseLiveStats(32001L,"2017dejia","N_alad_dejia");
//        logger.info("【baidu dejia live mob   Stats】 end");
//    }
//
//    /**
//     *  baidu dejia  mob数据
//     */
//    public void baiduDejiaHighlightsStats() {
//        logger.info("【baidu dejia highlights mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseHighlightsStats(32001L,"2017dejia","N_alad_dejia");
//        logger.info("【baidu dejia highlights mob   Stats】 end");
//    }
//
//    /**
//     *  baidu dejia  mob数据
//     */
//    public void baiduDejiaHotStats() {
//        logger.info("【baidu dejia hot mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseHotStats(32001L,"2017dejia","N_alad_dejia");
//        logger.info("【baidu dejia hot mob   Stats】 end");
//    }
//
//
//    /**
//     *  baidu deguobei live mob数据
//     */
//    public void baiduDeguobeiLiveStats() {
//        logger.info("【baidu deguobei live mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseLiveStats(120001L,"2017deguobei","N_alad_deguobei");
//        logger.info("【baidu deguobei live mob   Stats】 end");
//    }
//
//    /**
//     *  baidu deguobei  mob数据
//     */
//    public void baiduDeguobeiHighlightsStats() {
//        logger.info("【baidu deguobei highlights mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseHighlightsStats(120001L,"2017deguobei","N_alad_deguobei");
//        logger.info("【baidu deguobei highlights mob   Stats】 end");
//    }
//
//    /**
//     *  baidu deguobei  mob数据
//     */
//    public void baiduDeguobeiHotStats() {
//        logger.info("【baidu deguobei hot mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseHotStats(120001L,"2017deguobei","N_alad_deguobei");
//        logger.info("【baidu deguobei hot mob   Stats】 end");
//    }
//
//    /**
//     *  baidu zuxiebei live mob数据
//     */
//    public void baiduZuxiebeiLiveStats() {
//        logger.info("【baidu zuxiebei live mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseLiveStats(177001L,"2017zuxiebei","N_alad_zuxiebei");
//        logger.info("【baidu zuxiebei live mob   Stats】 end");
//    }
//
//    /**
//     *  baidu zuxiebei  mob数据
//     */
//    public void baiduZuxiebeiHighlightsStats() {
//        logger.info("【baidu zuxiebei highlights mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseHighlightsStats(177001L,"2017zuxiebei","N_alad_zuxiebei");
//        logger.info("【baidu zuxiebei highlights mob   Stats】 end");
//    }
//
//    /**
//     *  baidu zuxiebei  mob数据
//     */
//    public void baiduZuxiebeiHotStats() {
//        logger.info("【baidu zuxiebei hot mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseHotStats(177001L,"2017zuxiebei","N_alad_zuxiebei");
//        logger.info("【baidu zuxiebei hot mob   Stats】 end");
//    }
//
//    /**
//     *  baidu yijia live mob数据
//     */
//    public void baiduYijiaLiveStats() {
//        logger.info("【baidu yijia live mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseLiveStats(29001L,"2017yijia","N_alad_yijia");
//        logger.info("【baidu yijia live mob   Stats】 end");
//    }
//
//    /**
//     *  baidu yijia  mob数据
//     */
//    public void baiduYijiaHighlightsStats() {
//        logger.info("【baidu yijia highlights mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseHighlightsStats(29001L,"2017yijia","N_alad_yijia");
//        logger.info("【baidu yijia highlights mob   Stats】 end");
//    }
//
//    /**
//     *  baidu yijia  mob数据
//     */
//    public void baiduYijiaHotStats() {
//        logger.info("【baidu yijia hot mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseHotStats(29001L,"2017yijia","N_alad_yijia");
//        logger.info("【baidu yijia hot mob   Stats】 end");
//    }
//
//
//    /**
//     *  baidu zuzongbei live mob数据
//     */
//    public void baiduZuzongbeiLiveStats() {
//        logger.info("【baidu zuzongbei live mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseLiveStats(100228001L,"2017zuzongbei","N_alad_zuzongbei");
//        logger.info("【baidu zuzongbei live mob   Stats】 end");
//    }
//
//    /**
//     *  baidu zuzongbei  mob数据
//     */
//    public void baiduZuzongbeiHighlightsStats() {
//        logger.info("【baidu zuzongbei highlights mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseHighlightsStats(100228001L,"2017zuzongbei","N_alad_zuzongbei");
//        logger.info("【baidu zuzongbei highlights mob   Stats】 end");
//    }
//
//    /**
//     *  baidu zuzongbei  mob数据
//     */
//    public void baiduZuzongbeiHotStats() {
//        logger.info("【baidu zuzongbei hot mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseHotStats(100228001L,"2017zuzongbei","N_alad_zuzongbei");
//        logger.info("【baidu zuzongbei hot mob   Stats】 end");
//    }
//
//    /**
//     *  baidu shequdun live mob数据
//     */
//    public void baiduShequdunLiveStats() {
//        logger.info("【baidu shequdun live mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseLiveStats(101121001L, "2017shequdun", "N_alad_shequdun");
//        logger.info("【baidu shequdun live mob   Stats】 end");
//    }
//
//    /**
//     *  baidu shequdun  mob数据
//     */
//    public void baiduShequdunHighlightsStats() {
//        logger.info("【baidu shequdun highlights mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseHighlightsStats(101121001L,"2017shequdun","N_alad_shequdun");
//        logger.info("【baidu shequdun highlights mob   Stats】 end");
//    }
//
//    /**
//     *  baidu shequdun  mob数据
//     */
//    public void baiduShequdunHotStats() {
//        logger.info("【baidu shequdun hot mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseHotStats(101121001L,"2017shequdun","N_alad_shequdun");
//        logger.info("【baidu shequdun hot mob   Stats】 end");
//    }
//
//    /**
//     *  baidu puchao live mob数据
//     */
//    public void baiduPuchaoLiveStats() {
//        logger.info("【baidu puchao live mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseLiveStats(172001L,"2017puchao","N_alad_puchao");
//        logger.info("【baidu puchao live mob   Stats】 end");
//    }
//
//    /**
//     *  baidu puchao  mob数据
//     */
//    public void baiduPuchaoHighlightsStats() {
//        logger.info("【baidu puchao highlights mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseHighlightsStats(172001L,"2017puchao","N_alad_puchao");
//        logger.info("【baidu puchao highlights mob   Stats】 end");
//    }
//
//    /**
//     *  baidu puchao  mob数据
//     */
//    public void baiduPuchaoHotStats() {
//        logger.info("【baidu puchao hot mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseHotStats(172001L, "2017puchao", "N_alad_puchao");
//        logger.info("【baidu puchao hot mob   Stats】 end");
//    }
//
//    /**
//     *  baidu mls live mob数据
//     */
//    public void baiduMlsLiveStats() {
//        logger.info("【baidu mls live mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseLiveStats(62001L,"2017mls","N_alad_mls");
//        logger.info("【baidu mls live mob   Stats】 end");
//    }
//
//    /**
//     *  baidu mls  mob数据
//     */
//    public void baiduMlsHighlightsStats() {
//        logger.info("【baidu mls highlights mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseHighlightsStats(62001L,"2017mls","N_alad_mls");
//        logger.info("【baidu mls highlights mob   Stats】 end");
//    }
//
//    /**
//     *  baidu mls  mob数据
//     */
//    public void baiduMlsHotStats() {
//        logger.info("【baidu mls hot mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseHotStats(62001L,"2017mls","N_alad_mls");
//        logger.info("【baidu mls hot mob   Stats】 end");
//    }
//
//
//    /**
//     *  baidu ouguanlanqiu live mob数据
//     */
//    public void baiduOuguanlanqiuLiveStats() {
//        logger.info("【baidu ouguanlanqiu live mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseLiveStats(180001L,"2017ouguanlanqiu","N_alad_ouguanlanqiu");
//        logger.info("【baidu ouguanlanqiu live mob   Stats】 end");
//    }
//
//    /**
//     *  baidu ouguanlanqiu  mob数据
//     */
//    public void baiduOuguanlanqiuHighlightsStats() {
//        logger.info("【baidu ouguanlanqiu highlights mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseHighlightsStats(180001L,"2017ouguanlanqiu","N_alad_ouguanlanqiu");
//        logger.info("【baidu ouguanlanqiu highlights mob   Stats】 end");
//    }
//
//    /**
//     *  baidu ouguanlanqiu  mob数据
//     */
//    public void baiduOuguanlanqiuHotStats() {
//        logger.info("【baidu ouguanlanqiu hot mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseHotStats(180001L,"2017ouguanlanqiu","N_alad_ouguanlanqiu");
//        logger.info("【baidu ouguanlanqiu hot mob   Stats】 end");
//    }
//
//    /**
//     *  baidu wenwang live mob数据
//     */
//    public void baiduWenwangLiveStats() {
//        logger.info("【baidu wenwang live mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseLiveStats(100825001L,"2017wenwang","N_alad_wenwang");
//        logger.info("【baidu wenwang live mob   Stats】 end");
//    }
//
//    /**
//     *  baidu wenwang  mob数据
//     */
//    public void baiduWenwangHighlightsStats() {
//        logger.info("【baidu wenwang highlights mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseHighlightsStats(100825001L,"2017wenwang","N_alad_wenwang");
//        logger.info("【baidu wenwang highlights mob   Stats】 end");
//    }
//
//    /**
//     *  baidu wenwang  mob数据
//     */
//    public void baiduWenwangHotStats() {
//        logger.info("【baidu wenwang hot mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseHotStats(100825001L,"2017wenwang","N_alad_wenwang");
//        logger.info("【baidu wenwang hot mob   Stats】 end");
//    }
//
//    /**
//     *  baidu suchao live mob数据
//     */
//    public void baiduSuchaoLiveStats() {
//        logger.info("【baidu suchao live mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseLiveStats(503001L,"2017suchao","N_alad_suchao");
//        logger.info("【baidu suchao live mob   Stats】 end");
//    }
//
//    /**
//     *  baidu suchao  mob数据
//     */
//    public void baiduSuchaoHighlightsStats() {
//        logger.info("【baidu suchao highlights mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseHighlightsStats(503001L,"2017suchao","N_alad_suchao");
//        logger.info("【baidu suchao highlights mob   Stats】 end");
//    }
//
//    /**
//     *  baidu suchao  mob数据
//     */
//    public void baiduSuchaoHotStats() {
//        logger.info("【baidu suchao hot mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseHotStats(503001L,"2017suchao","N_alad_suchao");
//        logger.info("【baidu suchao hot mob   Stats】 end");
//    }
//
//    /**
//     *  baidu qingnianouguan live mob数据
//     */
//    public void baiduQingnianouguanLiveStats() {
//        logger.info("【baidu qingnianouguan live mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseLiveStats(100152001L,"2017qingnianouguan","N_alad_qingnianouguan");
//        logger.info("【baidu qingnianouguan live mob   Stats】 end");
//    }
//
//    /**
//     *  baidu qingnianouguan  mob数据
//     */
//    public void baiduQingnianouguanHighlightsStats() {
//        logger.info("【baidu qingnianouguan highlights mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseHighlightsStats(100152001L,"2017qingnianouguan","N_alad_qingnianouguan");
//        logger.info("【baidu qingnianouguan highlights mob   Stats】 end");
//    }
//
//    /**
//     *  baidu qingnianouguan  mob数据
//     */
//    public void baiduQingnianouguanHotStats() {
//        logger.info("【baidu qingnianouguan hot mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseHotStats(100152001L,"2017qingnianouguan","N_alad_qingnianouguan");
//        logger.info("【baidu qingnianouguan hot mob   Stats】 end");
//    }
//
//    /**
//     *  baidu jliansai live mob数据
//     */
//    public void baiduJliansaiLiveStats() {
//        logger.info("【baidu jliansai live mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseLiveStats(67001L,"2017jliansai","N_alad_jliansai");
//        logger.info("【baidu jliansai live mob   Stats】 end");
//    }
//
//    /**
//     *  baidu jliansai  mob数据
//     */
//    public void baiduJliansaiHighlightsStats() {
//        logger.info("【baidu jliansai highlights mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseHighlightsStats(67001L,"2017jliansai","N_alad_jliansai");
//        logger.info("【baidu jliansai highlights mob   Stats】 end");
//    }
//
//    /**
//     *  baidu jliansai  mob数据
//     */
//    public void baiduJliansaiHotStats() {
//        logger.info("【baidu jliansai hot mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseHotStats(67001L,"2017jliansai","N_alad_jliansai");
//        logger.info("【baidu jliansai hot mob   Stats】 end");
//    }
//
//    /**
//     *  baidu aochao live mob数据
//     */
//    public void baiduAochaoLiveStats() {
//        logger.info("【baidu aochao live mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseLiveStats(100170001L,"2017aochao","N_alad_aochao");
//        logger.info("【baidu aochao live mob   Stats】 end");
//    }
//
//    /**
//     *  baidu aochao  mob数据
//     */
//    public void baiduAochaoHighlightsStats() {
//        logger.info("【baidu aochao highlights mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseHighlightsStats(100170001L,"2017aochao","N_alad_aochao");
//        logger.info("【baidu aochao highlights mob   Stats】 end");
//    }
//
//    /**
//     *  baidu aochao  mob数据
//     */
//    public void baiduAochaoHotStats() {
//        logger.info("【baidu aochao hot mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseHotStats(100170001L,"2017aochao","N_alad_aochao");
//        logger.info("【baidu aochao hot mob   Stats】 end");
//    }
//
//    /**
//     *  baidu stankovic live mob数据
//     */
//    public void baiduStankovicLiveStats() {
//        logger.info("【baidu stankovic live mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseLiveStats(100920001L,"2017stankovic","N_alad_stankovic");
//        logger.info("【baidu stankovic live mob   Stats】 end");
//    }
//
//    /**
//     *  baidu stankovic  mob数据
//     */
//    public void baiduStankovicHighlightsStats() {
//        logger.info("【baidu stankovic highlights mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseHighlightsStats(100920001L,"2017stankovic","N_alad_stankovic");
//        logger.info("【baidu stankovic highlights mob   Stats】 end");
//    }
//
//    /**
//     *  baidu stankovic  mob数据
//     */
//    public void baiduStankovicHotStats() {
//        logger.info("【baidu stankovic hot mob   Stats】 start");
//        baiduWiseStatsService.baiduWiseHotStats(100920001L,"2017stankovic","N_alad_stankovic");
//        logger.info("【baidu stankovic hot mob   Stats】 end");
//    }
}
