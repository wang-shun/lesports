package com.lesports.sms.cooperation.service.alad;

import client.SopsApis;
import com.google.common.collect.Lists;
import com.lesports.LeConstants;
import com.lesports.api.common.*;
import com.lesports.api.common.Sort;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.GroundType;
import com.lesports.sms.api.common.LiveShowStatus;
import com.lesports.sms.api.common.MatchStatus;
import com.lesports.sms.api.service.GetEpisodesOfSeasonByMetaEntryIdParam;
import com.lesports.sms.api.vo.TComboEpisode;
import com.lesports.sms.api.vo.TCompetitor;
import com.lesports.sms.api.vo.TMatch;
import com.lesports.sms.client.SbdsApis;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.client.SopsInternalApis;
import com.lesports.sms.cooperation.util.BaiduXmlUtil;
import com.lesports.sms.cooperation.util.Constants;
import com.lesports.sms.cooperation.util.StatsConstants;
import com.lesports.sms.cooperation.util.XmlUtil;
import com.lesports.sms.model.*;
import com.lesports.utils.LeDateUtils;
import com.lesports.utils.PageUtils;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhonglin on 2016/8/12.
 */
@Service("baiduEplStatsService")
public class BaiduEplStatsService {

    private static final Logger LOG = LoggerFactory.getLogger(BaiduEplStatsService.class);
    private static final String MATCH_URL = "http://sports.letv.com/match/%s.html";
    private static final String EPL_CH = "alad_epl";

    //生成给360的赛程xml，并且上传到ftp服务器
    public void baiduEplStats() {
        String fileName = "2015epl"+ Constants.fileextraname+".xml";
        Boolean flag = createXmlFile(Constants.filelocalpath+fileName);
        //生成文件成功上传文件
        if(flag){
            XmlUtil.uploadXmlFile(fileName, Constants.filelocalpath, Constants.fileuploadpath);
        }
        else{
            LOG.error("ftpXmlFile error file: {}" ,fileName);
        }

    }

    //生成xml文件
    public boolean createXmlFile(String file){
        try{
            CompetitionSeason competitionSeason = SbdsInternalApis.getLatestCompetitionSeasonByCid(Constants.EPL_COMPETITION_ID);
            if(competitionSeason == null){
                LOG.error("competitionSeason is  null cid:{}", Constants.EPL_COMPETITION_ID);
                return false;
            }

            Element root = new Element("urlset");
            Document Doc  = new Document(root);

            for(int i=1;i<39;i++){
                DictEntry dictEntry = SbdsInternalApis.getDictEntryByNameAndParentId("第"+i+"轮",Constants.ROUND_PARENT_KD);
                if(dictEntry == null)continue;

                Pageable pageable = PageUtils.createPageable(0, 60);
                InternalQuery internalQuery = new InternalQuery();
                internalQuery.addCriteria(InternalCriteria.where("deleted").is(false));
                internalQuery.addCriteria(InternalCriteria.where("cid").is(Constants.EPL_COMPETITION_ID));
                internalQuery.addCriteria(InternalCriteria.where("csid").is(competitionSeason.getId()));
                internalQuery.addCriteria(InternalCriteria.where("round").is(dictEntry.getId()));
                internalQuery.addCriteria(InternalCriteria.where("allow_countries").is(CountryCode.CN));
                internalQuery.setPageable(pageable);
                internalQuery.setSort(new org.springframework.data.domain.Sort(org.springframework.data.domain.Sort.Direction.ASC, "start_time"));
                List<com.lesports.sms.model.Match> matches = SbdsInternalApis.getMatchsByQuery(internalQuery);
                if(CollectionUtils.isNotEmpty(matches)){
                    for(Match match:matches){
                        Element url = createOneContentElement(match,i);
                        if(url != null){
                            root.addContent(url);
                        }
                    }
                }
            }


            XMLOutputter XMLOut = new XMLOutputter(XmlUtil.FormatXML());
            XMLOut.output(Doc, new FileOutputStream(file));
        }
        catch (Exception e){
            LOG.error("statsCal createXmlFile  error", e);
            return false;
        }
        return true;
    }

    //生成每场比赛的Element
    public Element createOneContentElement(Match match,int round) throws Exception{
        Element onecontent = new Element("url");
        Element loc = new Element("loc");
        loc.addContent(String.format(MATCH_URL,match.getId())+"?ch="+EPL_CH);
        onecontent.addContent(loc);

        Element data = new Element("data");
        Element display = new Element("display");

        Element matchName = new Element("match_name");
        matchName.addContent("英超");
        display.addContent(matchName);

        Element matchId = new Element("match_id");
        matchId.addContent(round+"");
        display.addContent(matchId);

        Element  startTime = new Element("start_time");
        startTime.addContent(LeDateUtils.formatYMDHMS(LeDateUtils.parseYYYYMMDDHHMMSS(match.getStartTime())));
        display.addContent(startTime);

        Element  endTime = new Element("end_time");
        Date startDate = LeDateUtils.parseYYYYMMDDHHMMSS(match.getStartTime());
        Date endDate = LeDateUtils.addHour(startDate,2);
        endTime.addContent(LeDateUtils.formatYMDHMS(endDate));
        display.addContent(endTime);

        Element vsTeams = new Element("vs_teams");
        Set<Match.Competitor> competitors = match.getCompetitors();

        String homeName = "";
        String awayName = "";
        String homeResult = "";
        String awayResult = "";
        long homeId = 0;
        long awayId = 0;

        for(Match.Competitor competitor:competitors){
            if (competitor.getGround().equals(GroundType.HOME)) {
                homeResult = competitor.getFinalResult();
                homeId = competitor.getCompetitorId();
                Team homeTeam = SbdsInternalApis.getTeamById(homeId);
                if(homeTeam != null ){
                    homeName = homeTeam.getName();
                }
            } else if (competitor.getGround().equals(GroundType.AWAY)) {
                awayResult = competitor.getFinalResult();
                awayId = competitor.getCompetitorId();
                Team awayTeam = SbdsInternalApis.getTeamById(awayId);
                if(awayTeam != null ){
                    awayName = awayTeam.getName();
                }
            }
        }

        if(match.getStatus().equals(MatchStatus.MATCH_END)){
            vsTeams.addContent(homeName+homeResult+"-"+awayResult+awayName);
        }
        else{
            vsTeams.addContent(homeName+"vs"+awayName);
        }
        display.addContent(vsTeams);

        Element videoId = new Element("video_id");
        videoId.addContent(StatsConstants.eplMap.get(homeId+","+ awayId)+"");
        display.addContent(videoId);


        Element source = new Element("source");
        source.addContent("乐视网");
        display.addContent(source);

        Element videoLink = new Element("video_link");
        videoLink.addContent(String.format(MATCH_URL,match.getId())+"?ch="+EPL_CH);
        display.addContent(videoLink);

        Element status = new Element("status");
        if(match.getXinyingPay()!=null && match.getXinyingPay()){
            return  null;
        }
        status.addContent("免费");
        display.addContent(status);

        data.addContent(display);
        onecontent.addContent(data);

        return onecontent;
    }

    public static void main(String[] args){
        int i;
        Sheet sheet;
        Workbook book;
        Cell cell1,cell2,cell3;
        try {
            //t.xls为要读取的excel文件名

            book= Workbook.getWorkbook(new File("E:\\epl.xls"));

            //获得第一个工作表对象(ecxel中sheet的编号从0开始,0,1,2,3,....)
            sheet=book.getSheet(0);

            if(sheet == null){
                System.out.println("sheet is null");
            }

            i=1;
            while(true)
            {
                if(i>760)break;
                //获取每一行的单元格
                cell1=sheet.getCell(0,i);//（列，行）
                cell2=sheet.getCell(3,i);
                cell3=sheet.getCell(4,i);
//                System.out.println("i: " + i+ " id: " + cell1.getContents().trim() + " name1: " + cell2.getContents().trim() + " name2: " + cell3.getContents().trim());
                i++;
                //如果读取的数据为空
                if(StringUtils.isBlank(cell1.getContents())){
                    continue;
                }
                System.out.println("eplMap.put(\"" + StatsConstants.eplTeamMap.get(cell2.getContents().trim()) + "," + StatsConstants.eplTeamMap.get(cell3.getContents().trim()) + "\"," + cell1.getContents() + "L);");

            }
            book.close();
        }
        catch(Exception e)  {
            e.printStackTrace();
        }
    }

}
