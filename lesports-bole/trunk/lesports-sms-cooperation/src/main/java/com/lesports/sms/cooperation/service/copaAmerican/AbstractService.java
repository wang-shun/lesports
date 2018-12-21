package com.lesports.sms.cooperation.service.copaAmerican;

import com.google.common.collect.Lists;
import com.lesports.api.common.CountryCode;
import com.lesports.api.common.LanguageCode;
import com.lesports.sms.api.common.MatchStatus;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.client.SopsInternalApis;
import com.lesports.sms.cooperation.util.FtpUtil;
import com.lesports.sms.model.Episode;
import com.lesports.sms.model.TopList;
import com.lesports.utils.LeProperties;
import jersey.repackaged.com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public abstract class AbstractService {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractService.class);
    public static final String baidu_logo = "baidu_12";
    public static final String one_box_logo = "onebox";
    public static final String sogo_logo = "soalad";
    public static final Long cid = 101425001L;
    public static final Long csid = 101543002L;//234002L;//
    public static final String copa_start_date = "20160604";
    public static final String copa_end_date = "20160628";
    public static final String top12_start_date = "20160901";
    public static final String top12_end_date = "20170908";
    public static final Long teamRankingTypeId = 100162000L;
    public static final Long copaNewTagId = 100034008L;
    public static final String resourceUrl = "http://m.lesports.com/12?ch=";
    public static final String resourceMobileUrl = "http://www.m.lesports.com/12?ch=";
    public static final String goalRankingUrl = "http://www.lesports.com/column/2016copacup/index.shtml?ch=";
    public static final String assistRankingUrl = "http://www.lesports.com/column/2016copacup/index.shtml?ch=";
    public static final String teamRankingUrl = "http://m.lesports.com/12/standings?ch=";
    public static final String newsUrl = "http://www.lesports.com/topic/i/cac2016schedule/index.html?ch=";
    public static final String matchesUrl = "http://m.lesports.com/12/schedule?ch=";
    public static final String teamUrl = "http://www.lesports.com/team/?.html";
    public static final String playerUrl = "http://www.lesports.com/player/?.html";
    public static final String teamLogo = "/11_112_112.png";
    public static final Long StagingId = 100031000L;
    public static final Map<String, Long> idMaps = Maps.newHashMap();
    public static final Map<String, Long> idStageMaps = Maps.newHashMap();
    public static final Map<String, Long> idRoundMaps = Maps.newHashMap();
    public static final Map<Long, String> logoMaps = Maps.newHashMap();
    public static final Map<Long, String> eventMaps = Maps.newHashMap();
    public static final Map<String, String> weekMaps = Maps.newHashMap();

    static {
        idMaps.put("A", 100023000L);
        idMaps.put("B", 100024000L);
        idMaps.put("C", 100025000L);
        idMaps.put("D", 100026000L);
        idStageMaps.put("1/8决赛", 100078000L);
        idStageMaps.put("1/4决赛", 100079000l);
        idStageMaps.put("半决赛", 100080000L);
        idStageMaps.put("三四名决赛", 100081000L);
        idStageMaps.put("决赛", 100082000L);
        idRoundMaps.put("第1轮", 100031000L);
        idRoundMaps.put("第2轮", 100032000L);
        idRoundMaps.put("第3轮", 100033000L);
        eventMaps.put(100159000L, "1");
        eventMaps.put(100158000L, "3");
        eventMaps.put(100157000L, "4");
        eventMaps.put(104656000L, "5");

        logoMaps.put(1530006L, "http://f.hiphotos.baidu.com/image/pic/item/1c950a7b02087bf407966d2cf5d3572c10dfcff8.jpg");
        logoMaps.put(1527006L, "http://h.hiphotos.baidu.com/image/pic/item/8b13632762d0f70386fdaf190ffa513d2797c5a3.jpg");
        logoMaps.put(1522006L, "http://g.hiphotos.baidu.com/image/pic/item/a8ec8a13632762d0e1719e7aa7ec08fa503dc6a3.jpg");
        logoMaps.put(1524006L, "http://e.hiphotos.baidu.com/image/pic/item/71cf3bc79f3df8dc7d5db554ca11728b46102896.jpg");
        logoMaps.put(1523006L, "http://c.hiphotos.baidu.com/image/pic/item/6609c93d70cf3bc73560f729d600baa1cc112a96.jpg");
        logoMaps.put(1703006L, "http://f.hiphotos.baidu.com/image/pic/item/aec379310a55b3190ecbb8b044a98226cefc17c0.jpg");
        logoMaps.put(1525006L, "http://e.hiphotos.baidu.com/image/pic/item/f636afc379310a555716e431b04543a9832610c0.jpg");
        logoMaps.put(1536006L, "http://f.hiphotos.baidu.com/image/pic/item/09fa513d269759ee201f5a0eb5fb43166c22dfa3.jpg");
        logoMaps.put(1539006L, "http://a.hiphotos.baidu.com/image/pic/item/b7fd5266d0160924634598ded30735fae7cd3496.jpg");
        logoMaps.put(1545006L, "http://h.hiphotos.baidu.com/image/pic/item/38dbb6fd5266d016ce79a4ed902bd40734fa3596.jpg");
        logoMaps.put(1548006L, "http://a.hiphotos.baidu.com/image/pic/item/622762d0f703918fb8ab070f563d269758eec4f8.jpg");
        logoMaps.put(1694006l, "http://g.hiphotos.baidu.com/image/pic/item/0b7b02087bf40ad16c9efd26502c11dfa8eccef8.jpg");
        logoMaps.put(1688006L, "http://f.hiphotos.baidu.com/image/pic/item/060828381f30e924bda7a6f44b086e061c95f7f7.jpg");
        logoMaps.put(1542006L, "http://h.hiphotos.baidu.com/image/pic/item/8b82b9014a90f6039c82a6823e12b31bb151edc1.jpg");
        logoMaps.put(1533006L, "http://c.hiphotos.baidu.com/image/pic/item/b21c8701a18b87d6dd3d8ada000828381e30fdfc.jpg");
        logoMaps.put(1697006L, "http://c.hiphotos.baidu.com/image/pic/item/77094b36acaf2eddf9b76a2c8a1001e9380193b4.jpg");
        weekMaps.put("Sun", "周日");
        weekMaps.put("Sat", "周六");
        weekMaps.put("Fri", "周五");
        weekMaps.put("Thu", "周四");
        weekMaps.put("Wed", "周三");
        weekMaps.put("Tue", "周二");
        weekMaps.put("Mon", "周一");
    }


    public String getmatchStatus(MatchStatus status) {
        if (status.toString().equals("MATCH_END")) return "2";
        else if (status.toString().equals("MATCHING")) return "1";
        else return "0";
    }

    public String getScore(MatchStatus status, String homeScore, String awayScore) {
        if (status.toString().equals("MATCH_NOT_START")) {
            return "VS";

        } else return homeScore + "-" + awayScore;
    }

    public String getTeamrank(Long group, Long teamId) {
        List<TopList> topList = SbdsInternalApis.getTopListsByCsidAndTypeAndGroup(csid, teamRankingTypeId, idMaps.get(group));
        if (CollectionUtils.isNotEmpty(topList)) {
            List<TopList.TopListItem> items = topList.get(0).getItems();
            for (TopList.TopListItem item : items) {
                if (item.getCompetitorId().equals(teamId)) return item.getRank().toString();
                else continue;
            }
        }
        return "0";
    }

    public List<TopList.TopListItem> getOrderedTopListItems(TopList topList) {
        List<TopList.TopListItem> dataList = Lists.newArrayList(topList.getItems());

        //根据showOrder排序
        Collections.sort(dataList, new Comparator<TopList.TopListItem>() {
            @Override
            public int compare(TopList.TopListItem o1, TopList.TopListItem o2) {
                return o1.getShowOrder() - o2.getShowOrder();
            }
        });
        return dataList;
    }

    public String getTeamScore(Long group, Long teamId) {
        List<TopList> topList = SbdsInternalApis.getTopListsByCsidAndTypeAndGroup(csid, teamRankingTypeId, idMaps.get(group));
        if (CollectionUtils.isNotEmpty(topList)) {
            List<TopList.TopListItem> items = topList.get(0).getItems();
            for (TopList.TopListItem item : items) {
                if (item.getCompetitorId().equals(teamId))
                    return item.getStats().get("winMatch") + "胜" + item.getStats().get("lossMatch") + "负数";
                else continue;
            }
        }
        return "0" + "胜" + "0" + "负";
    }

    public static String matchPageUrl(String type, Long matchId, String logo) {
        String url = "http://m.lesports.com/match/" + matchId + ".html?ch=" + logo;
        if (StringUtils.isBlank(type)) return url;
        Episode currentEpisode = SopsInternalApis.getEpisodesByMidAndCountryAndLanguage(matchId, CountryCode.CN, LanguageCode.ZH_CN);
        if (null == currentEpisode || CollectionUtils.isEmpty(currentEpisode.getVideos())) {
            return url;
        }
        for (Episode.SimpleVideo video : currentEpisode.getVideos()) {
            if (video.getType().toString().equals(type)) {
                url = "http://www.lesports.com/match/" + matchId + ".html?" + type.toLowerCase() + "=" + video.getVid() + "&ch=" + logo + "#" + type.toLowerCase() + "/" + video.getVid();
                break;
            }
        }
        return url;
    }

    public Element createRooElement(String name) {
        Element root = new Element(name);
        return root;
    }

    public void updateElement(Element curentElement, List<Element> sonElements) {
        if (CollectionUtils.isEmpty(sonElements) || curentElement == null) return;
        for (int i = 0; i < sonElements.size(); i++)
            curentElement.addContent(sonElements.get(i));

    }

    public Element getElementWithAttributes(String name, Map<String, Object> attributes) {
        Element curentElement = new Element(name);
        if (MapUtils.isEmpty(attributes) || curentElement == null) return null;
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            if (entry == null) continue;
            curentElement.setAttribute(entry.getKey(), entry.getValue() == null ? null : entry.getValue().toString());
        }
        return curentElement;
    }

    public Element getElementWithAttributesAndContent(String name, String value, Map<String, Object> attributes) {
        Element curentElement = new Element(name);
        if (MapUtils.isEmpty(attributes) || curentElement == null) return null;
        curentElement.addContent(value);
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            if (entry == null) continue;
            curentElement.setAttribute(entry.getKey(), entry.getValue() == null ? null : entry.getValue().toString());
        }

        return curentElement;
    }

    public Element createElement(String elmentName, String textContent) {
        Element currentElment = new Element(elmentName);
        currentElment.addContent(textContent);
        return currentElment;
    }

    public Element createParentElement(String parentName, String elmentName, String textContent) {
        Element parentElement = new Element(parentName);
        Element currentElment = new Element(elmentName);
        currentElment.addContent(textContent);
        parentElement.addContent(currentElment);
        return parentElement;
    }

    public boolean uploadXmlFile() {
        if (getDocument() == null) return false;
        // String filePath = createXmlFile("/letv/data/hd/copaAmerican/" + getFilePath(), getDocument());
        String filePath = createXmlFile("D:\\" + getFilePath(), getDocument());
        String fileName = getFilePath();
        String srHost = LeProperties.getString("baidu.ftp.host");
        int srPort = LeProperties.getInt("baidu.ftp.port", 0);
        String srUserName = LeProperties.getString("baidu.ftp.userName");
        String srPassword = LeProperties.getString("baidu.ftp.password");
        FtpUtil ftpUtil = new FtpUtil(srHost, srPort, srUserName, srPassword);
        try {
            LOG.info("upload XmlFile execute begin" + filePath);
            if (!ftpUtil.loginFtp(60)) {
                LOG.error("login ftp: fail ");
                return false;
            }
            ftpUtil.uploadFile(filePath, fileName, "//hd//");
        } catch (Exception e) {
            LOG.error("statsCal createXmlFile  error", e);
            return false;
        } finally {
            ftpUtil.logOutFtp();
        }
        return true;
    }


    //生成xml文件
    public String createXmlFile(String file, Document doc) {
        try {

            XMLOutputter XMLOut = new XMLOutputter(FormatXML());
            XMLOut.output(doc, new FileOutputStream(file));
            return file;
        } catch (Exception e) {
            LOG.error("statsCal createXmlFile  error", e);
            return null;
        }
    }

    public Format FormatXML() {
        Format format = Format.getCompactFormat();
        format.setEncoding("utf-8");
        format.setIndent(" ");
        return format;
    }

    public String getValidLogo15(String logo) {
        if (logo == null) return "";
        String validLogo = logo.replace(".png", "/11_100_100.png");
        return validLogo;
    }

    public String getPlayerUrl(String logo, String logoLoal) {
        if (logo != null) {
            if (logo.contains(".jpg")) return logo.replace(".jpg", "/11_100_100.jpg");
            else if (logo.contains(".png")) return logo.replace(".png", "/11_100_100.png");
            else if (logoLoal.contains(".jpg")) {
                String tempLogal = "/11_100_100.jpg";
                return logo + tempLogal;
            } else if (logoLoal.contains(".png")) {
                String tempLogal = "/11_100_100.png";
                ;
                return logo + tempLogal;
            }
            return "http://c.hiphotos.baidu.com/image/pic/item/bd3eb13533fa828b0c66572afa1f4134960a5ab6.jpg";
        } else {
            return "http://c.hiphotos.baidu.com/image/pic/item/bd3eb13533fa828b0c66572afa1f4134960a5ab6.jpg ";
        }
    }

    public abstract Document getDocument();

    public abstract String getFilePath();
}
