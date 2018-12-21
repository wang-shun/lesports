package com.lesports.sms.download.util;

import client.SopsApis;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.TTag;
import com.lesports.sms.api.common.VideoContentType;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.client.SopsInternalApis;
import com.lesports.sms.model.Episode;
import com.lesports.sms.model.Match;
import com.lesports.sms.model.Tag;
import com.lesports.sms.model.Video;
import com.lesports.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jdom.output.XMLOutputter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by zhonglin on 2016/3/28.
 */
public class Constants {

    //头条本地路径
    public static final String filetoutiaovideorootpath = LeProperties.getString("file.toutiao.video.root.path");
    //头条下载网址
    public static final String filetoutiaovideodownloadurl = LeProperties.getString("file.toutiao.video.download.url");
    //头条上传地址
    public static final String filetoutiaovideouploadpath = LeProperties.getString("file.toutiao.video.upload.path");
    //头条文件名
    public static final String filetoutiaovideodatafilename = "csl_videos.xml";
    //头条接口地址
    public static final String filetoutiaovideodomain = "http://coop.download.lesports.com/";
    public static String statsUserName ="beitai-LeTV";//LeProperties.getString("stats.http.userName");
    public static String statsPassWord ="letv2015";//LeProperties.getString("stats.http.password");

    public static Map<String,String> existVideosMap = Maps.newHashMap();//已存在赛事id;

    //中超赛事id
    public static final Long CSL_COMPETITION_ID = 47001L;

    static {

        existVideosMap.put("121806005","26338260");
        existVideosMap.put("307459005","26169560");
        existVideosMap.put("121786005","26246123");
        existVideosMap.put("121777005","26085954");
        existVideosMap.put("121703005","25218108");
        existVideosMap.put("121780005","26085151");
        existVideosMap.put("121750005","25813352");
        existVideosMap.put("307432005","25169227");
        existVideosMap.put("121754005","25855092");
        existVideosMap.put("121767005","25982751");
        existVideosMap.put("121735005","25581057");
        existVideosMap.put("307427005","24860105");
        existVideosMap.put("121776005","26086517");
        existVideosMap.put("121736005","25580314");
        existVideosMap.put("307426005","24871811");
        existVideosMap.put("121745005","25808019");
        existVideosMap.put("121675005","24808864");
        existVideosMap.put("307446005","25815437");
        existVideosMap.put("121698005","25169220");
        existVideosMap.put("121716005","25314859");
        existVideosMap.put("121691005","25079951");
        existVideosMap.put("121738005","25588767");
        existVideosMap.put("121771005","26043022");
        existVideosMap.put("121784005","26113733");
        existVideosMap.put("121788005","26156830");
        existVideosMap.put("1013224005","25261891");
        existVideosMap.put("307437005","25312071");
        existVideosMap.put("121741005","25767598");
        existVideosMap.put("1013311005","25261705");
        existVideosMap.put("121731005","25557510");
        existVideosMap.put("121797005","25781330");
        existVideosMap.put("307435005","25269180");
        existVideosMap.put("307460005","25778063");
        existVideosMap.put("121787005","26114693");
        existVideosMap.put("1013309005","24801859");
        existVideosMap.put("307433005","25217966");
        existVideosMap.put("307451005","25971647");
        existVideosMap.put("121705005","25205206");
        existVideosMap.put("121702005","25166311");
        existVideosMap.put("121790005","26163645");
        existVideosMap.put("121700005","25146514");
        existVideosMap.put("121710005","25261891");
        existVideosMap.put("121810005","26344300");
        existVideosMap.put("121761005","25937704");
        existVideosMap.put("121706005","25209394");
        existVideosMap.put("307447005","25850327");
        existVideosMap.put("307456005","26093253");
        existVideosMap.put("307434005","25218311");
        existVideosMap.put("121715005","25319682");
        existVideosMap.put("121792005","26169848");
        existVideosMap.put("121674005","24834038");
        existVideosMap.put("121712005","25258261");
        existVideosMap.put("307457005","26114666");
        existVideosMap.put("1013310005","25209394");
        existVideosMap.put("121799005","25780212");
        existVideosMap.put("121793005","26169846");
        existVideosMap.put("121766005","25981285");
        existVideosMap.put("121734005","25581188");
        existVideosMap.put("121711005","25261705");
        existVideosMap.put("121762005","25939340");
        existVideosMap.put("307453005","26042704");
        existVideosMap.put("121701005","25169221");
        existVideosMap.put("121696005","25085752");
        existVideosMap.put("307438005","25332080");
        existVideosMap.put("121768005","25977811");
        existVideosMap.put("307439005","25399797");
        existVideosMap.put("121746005","25803587");
        existVideosMap.put("307458005","26169463");
        existVideosMap.put("121774005","26048899");
        existVideosMap.put("121679005","24849011");
        existVideosMap.put("121699005","25151056");
        existVideosMap.put("121764005","25982760");
        existVideosMap.put("121763005","25976163");
        existVideosMap.put("121811005","26344128");
        existVideosMap.put("121704005","25201951");
        existVideosMap.put("121737005","25606652");
        existVideosMap.put("121802005","26280244");
        existVideosMap.put("121796005","25772050");
        existVideosMap.put("121708005","25208723");
        existVideosMap.put("307428005","25019507");
        existVideosMap.put("121709005","25256768");
        existVideosMap.put("307429005","25046152");
        existVideosMap.put("121733005","25577266");
        existVideosMap.put("307436005","25268321");
        existVideosMap.put("121682005","24890508");
        existVideosMap.put("121803005","26286344");
        existVideosMap.put("121725005","25398941");
        existVideosMap.put("121809005","26344323");
        existVideosMap.put("121692005","25084164");
        existVideosMap.put("307479005","25813365");
        existVideosMap.put("121730005","25482858");
        existVideosMap.put("121673005","24814557");
        existVideosMap.put("307424005","24813794");
        existVideosMap.put("307441005","25489703");
        existVideosMap.put("121684005","25020780");
        existVideosMap.put("121683005","24873313");
        existVideosMap.put("121807005","26338426");
        existVideosMap.put("121677005","24889431");
        existVideosMap.put("307431005","25169225");
        existVideosMap.put("307444005","25758760");
        existVideosMap.put("121769005","26042733");
        existVideosMap.put("121724005","25397830");
        existVideosMap.put("121753005","25879016");
        existVideosMap.put("121686005","25026248");
        existVideosMap.put("121760005","25933166");
        existVideosMap.put("121723005","25384547");
        existVideosMap.put("121717005","25317771");
        existVideosMap.put("121752005","25855654");
        existVideosMap.put("121800005","26278419");
        existVideosMap.put("121714005","25265827");
        existVideosMap.put("1013225005","25258261");
        existVideosMap.put("307454005","26048629");
        existVideosMap.put("121770005","26040967");
        existVideosMap.put("307443005","25588893");
        existVideosMap.put("121755005","25860885");
        existVideosMap.put("121688005","25033148");
        existVideosMap.put("121751005","25854622");
        existVideosMap.put("307450005","25926693");
        existVideosMap.put("307449005","25932320");
        existVideosMap.put("121720005","25320464");
        existVideosMap.put("121805005","26286178");
        existVideosMap.put("307464005","26332300");
        existVideosMap.put("121757005","25926748");
        existVideosMap.put("121727005","25484342");
        existVideosMap.put("121729005","25489135");
        existVideosMap.put("121740005","25741706");
        existVideosMap.put("307462005","26273503");
        existVideosMap.put("121801005","26280262");
        existVideosMap.put("121773005","26049308");
        existVideosMap.put("121804005","26286220");
        existVideosMap.put("307445005","25755845");
        existVideosMap.put("121789005","26161686");
        existVideosMap.put("121749005","25812648");
        existVideosMap.put("121742005","25742072");
        existVideosMap.put("307440005","25493197");
        existVideosMap.put("121791005","26164042");
        existVideosMap.put("121783005","26108461");
        existVideosMap.put("121672005","24801859");
        existVideosMap.put("121694005","25097800");
        existVideosMap.put("307478005","25371941");
        existVideosMap.put("121747005","25809467");
        existVideosMap.put("121759005","25939019");
        existVideosMap.put("121687005","25025748");
        existVideosMap.put("121689005","25030196");
        existVideosMap.put("307425005","24817405");
        existVideosMap.put("121693005","25085366");
        existVideosMap.put("121775005","26085015");
        existVideosMap.put("121744005","25777651");
        existVideosMap.put("121695005","25091092");
        existVideosMap.put("121748005","25819660");
        existVideosMap.put("121772005","26042815");
        existVideosMap.put("121732005","25489228");
        existVideosMap.put("121808005","26338532");
        existVideosMap.put("307442005","25586154");
        existVideosMap.put("121758005","25932804");
        existVideosMap.put("121743005","25760064");
        existVideosMap.put("121713005","25261192");
        existVideosMap.put("121685005","25033553");
        existVideosMap.put("307455005","26086489");
        existVideosMap.put("121680005","24851881");
        existVideosMap.put("121798005","25777529");
        existVideosMap.put("1013312005","25256768");
        existVideosMap.put("307461005","26011624");
        existVideosMap.put("307430005","25077912");
        existVideosMap.put("121707005","25218089");
        existVideosMap.put("121795005","25783517");
        existVideosMap.put("121719005","25321422");
        existVideosMap.put("121781005","26106641");
        existVideosMap.put("121697005","25165398");
        existVideosMap.put("121722005","25403800");
        existVideosMap.put("307465005","26344337");
        existVideosMap.put("121728005","25478533");
        existVideosMap.put("121765005","25977552");
        existVideosMap.put("121721005","25559577");
        existVideosMap.put("307452005","25983151");
        existVideosMap.put("121681005","24871921");
        existVideosMap.put("307463005","26280282");
        existVideosMap.put("307448005","25863256");
        existVideosMap.put("121756005","25879016");
        existVideosMap.put("121739005","25750830");
        existVideosMap.put("121690005","25083832");
        existVideosMap.put("121726005","25376595");
        existVideosMap.put("121678005","25438571");
        existVideosMap.put("121785005","26113556");
        existVideosMap.put("121676005","24833782");
        existVideosMap.put("121782005","26108122");
        existVideosMap.put("121718005","25320944");
    }


//    public static void main(String args[]){
//
//        try{
//            org.jdom.Element root = new org.jdom.Element("DOCUMENT");
//            org.jdom.Document Doc  = new org.jdom.Document(root);
//
//
//            List<Long> list = Lists.newArrayList();
//            list.add(26374340L);
//            list.add(26365235L);
//            list.add(26336018L);
//            list.add(26356097L);
//            list.add(26351468L);
//            list.add(26342437L);
//            list.add(26345356L);
//            list.add(26344594L);
//            list.add(26345382L);
//            list.add(26353590L);
//            list.add(26365229L);
//            list.add(26375244L);
//
//            for(Long vid:list){
//                Video video = SopsInternalApis.getVideoById(vid);
//
//                if(video == null)continue;
//
//                org.jdom.Element videoElement = new org.jdom.Element("item");
//
//                org.jdom.Element type = new org.jdom.Element("type");
//                type.addContent("0");
//                videoElement.addContent(type);
//
//                org.jdom.Element title1 = new org.jdom.Element("title_1");
//                title1.addContent(video.getName());
//                videoElement.addContent(title1);
//
//                org.jdom.Element pubDate1 = new org.jdom.Element("pubDate_1");
//                pubDate1.addContent(LeDateUtils.formatYMDHMS(LeDateUtils.parseYYYYMMDDHHMMSS(video.getCreateAt())));
//                videoElement.addContent(pubDate1);
//
//                String image = "";
//                if(video.getImages().get("1080*608") !=null ){
//                    image = video.getImages().get("1080*608") ;
//                }
//                else if(video.getImages().get("1280*640") != null){
//                    image = video.getImages().get("1280*640") ;
//                }
//                else if(video.getImages().get("960*540") != null){
//                    image = video.getImages().get("960*540") ;
//                }
//                else if(video.getImages().get("400*225") != null){
//                    image = video.getImages().get("400*225") ;
//                }
//
//                org.jdom.Element videoCoverUrl1 = new org.jdom.Element("video_cover_url_1");
//                videoCoverUrl1.addContent(image);
//                videoElement.addContent(videoCoverUrl1);
//
//                org.jdom.Element period1 = new org.jdom.Element("period_1");
//                period1.addContent(video.getDuration()+"");
//                videoElement.addContent(period1);
//
//                org.jdom.Element playCount1 = new org.jdom.Element("play_count_1");
//                playCount1.addContent(PlayApis.getPlayNum(video.getId() + "")+"");
//                videoElement.addContent(playCount1);
//
//                String tags = "";
//                if(CollectionUtils.isNotEmpty(video.getTagIds())){
//                    for(Long tagId:video.getTagIds()){
//                        Tag tag = SopsInternalApis.getTagById(tagId);
//                        if(StringUtils.isBlank(tags))tags =tag.getName();
//                        else tags += ","+tag.getName();
//                    }
//                }
//
//                org.jdom.Element tags1 = new org.jdom.Element("tags_1");
//                tags1.addContent(tags);
//                videoElement.addContent(tags1);
//
//                org.jdom.Element link1 = new org.jdom.Element("link_1");
//                link1.addContent("http://coop.download.lesports.com/toutiao/top12/" + vid +".mp4");
//                videoElement.addContent(link1);
//
//                org.jdom.Element description1 = new org.jdom.Element("description_1");
//                description1.addContent(video.getDesc());
//                videoElement.addContent(description1);
//
//                org.jdom.Element videoId = new org.jdom.Element("video_id");
//                videoId.addContent(video.getId()+"");
//                videoElement.addContent(videoId);
//
//                root.addContent(videoElement);
//            }
//
//
//            XMLOutputter XMLOut = new XMLOutputter(XmlUtil.FormatXML());
//            XMLOut.output(Doc, new FileOutputStream("E:\\top12\\top12.xml"));
//        }
//        catch(Exception e){
//            e.printStackTrace();
//        }
//
//    }


    public static void main(String args[]){

        try{

            org.jdom.Element root = new org.jdom.Element("DOCUMENT");
            org.jdom.Document Doc  = new org.jdom.Document(root);


            for(String key:existVideosMap.keySet()){
                Episode episode = SopsInternalApis.getEpisodeById(Long.parseLong(key));
                if(episode == null) continue;

                Pageable pageable = PageUtils.createPageable(0, 5);
                InternalQuery internalQuery = new InternalQuery();
                internalQuery.addCriteria(InternalCriteria.where("deleted").is(false));
                internalQuery.addCriteria(InternalCriteria.where("mids").is(episode.getMid()));
                internalQuery.addCriteria(InternalCriteria.where("type").is(VideoContentType.OTHER));

                internalQuery.setPageable(pageable);
                internalQuery.setSort(new Sort(Sort.Direction.DESC, "create_at"));
                List<com.lesports.sms.model.Video> videos = SopsInternalApis.getVideosByQuery(internalQuery);

                if(CollectionUtils.isEmpty(videos))continue;

                for(Video video:videos){
                    org.jdom.Element videoElement = new org.jdom.Element("item");

                    org.jdom.Element id = new org.jdom.Element("id");
                    id.addContent(episode.getId()+"");
                    videoElement.addContent(id);

                    org.jdom.Element type = new org.jdom.Element("type");
                    type.addContent("0");
                    videoElement.addContent(type);

                    org.jdom.Element title1 = new org.jdom.Element("title_1");
                    title1.addContent(video.getName());
                    videoElement.addContent(title1);

                    org.jdom.Element pubDate1 = new org.jdom.Element("pubDate_1");
                    pubDate1.addContent(LeDateUtils.formatYMDHMS(LeDateUtils.parseYYYYMMDDHHMMSS(video.getCreateAt())));
                    videoElement.addContent(pubDate1);

                    String image = "";
                    if(video.getImages().get("1080*608") !=null ){
                        image = video.getImages().get("1080*608") ;
                    }
                    else if(video.getImages().get("1280*640") != null){
                        image = video.getImages().get("1280*640") ;
                    }
                    else if(video.getImages().get("960*540") != null){
                        image = video.getImages().get("960*540") ;
                    }
                    else if(video.getImages().get("400*225") != null){
                        image = video.getImages().get("400*225") ;
                    }

                    org.jdom.Element videoCoverUrl1 = new org.jdom.Element("video_cover_url_1");
                    videoCoverUrl1.addContent(image);
                    videoElement.addContent(videoCoverUrl1);

                    org.jdom.Element period1 = new org.jdom.Element("period_1");
                    period1.addContent(video.getDuration()+"");
                    videoElement.addContent(period1);

                    org.jdom.Element playCount1 = new org.jdom.Element("play_count_1");
                    playCount1.addContent(PlayApis.getPlayNum(video.getId() + "")+"");
                    videoElement.addContent(playCount1);

                    String tags = "";
                    if(CollectionUtils.isNotEmpty(video.getTagIds())){
                        for(Long tagId:video.getTagIds()){
                            Tag tag = SopsInternalApis.getTagById(tagId);
                            if(StringUtils.isBlank(tags))tags =tag.getName();
                            else tags += ","+tag.getName();
                        }
                    }

                    org.jdom.Element tags1 = new org.jdom.Element("tags_1");
                    tags1.addContent(tags);
                    videoElement.addContent(tags1);

                    org.jdom.Element link1 = new org.jdom.Element("link_1");
                    link1.addContent("http://coop.download.lesports.com/toutiao/csl/" + episode.getId()+"_" + video.getId() + ".mp4");
                    videoElement.addContent(link1);

                    org.jdom.Element description1 = new org.jdom.Element("description_1");
                    description1.addContent(video.getDesc());
                    videoElement.addContent(description1);

                    org.jdom.Element videoId = new org.jdom.Element("video_id");
                    videoId.addContent(video.getId()+"");
                    videoElement.addContent(videoId);

                    root.addContent(videoElement);
                }
            }

            XMLOutputter XMLOut = new XMLOutputter(XmlUtil.FormatXML());
            XMLOut.output(Doc, new FileOutputStream("E:\\toutiao\\csl.xml"));
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }
}
