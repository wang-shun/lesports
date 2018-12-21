package com.lesports.bole.api.web.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lesports.bole.model.BoleOlympicSettingDataSet;
import com.lesports.bole.model.BoleOlympicsLiveConfigSet;
import com.lesports.bole.service.BoleOlympicConfigService;
import com.lesports.bole.service.BoleOlympicSettingService;
import com.lesports.jersey.AlternateMediaType;
import com.lesports.jersey.annotation.LJSONP;
import com.lesports.jersey.core.LeStatus;
import com.lesports.jersey.exception.LeWebApplicationException;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.model.DictEntry;
import com.lesports.sms.model.Match;
import com.lesports.sms.model.MatchStats;
import com.lesports.utils.math.LeNumberUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import java.util.List;
import java.util.Map;

/**
 * BoleCompetitionResource
 *
 * @author denghui
 */
@Path("/olympics")
public class BoleOlympicsResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(BoleOlympicsResource.class);
    @Inject
    private BoleOlympicConfigService olympicConfigService;
    @Inject
    private BoleOlympicSettingService olympicSettingDataService;

    /**
     * 根据ID获取比赛
     *
     * @return
     */
    @LJSONP
    @GET
    @Path("/matches/{id}")
    @Produces(AlternateMediaType.UTF_8_APPLICATION_JSON)
    public Match getMatchById(@PathParam("id") long id) {
        try {
            return SbdsInternalApis.getMatchById(id);
        } catch (LeWebApplicationException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new LeWebApplicationException(e.getMessage(), LeStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 根据ID获取比赛
     *
     * @return
     */
    @LJSONP
    @GET
    @Path("/matches/stats/{id}")
    @Produces(AlternateMediaType.UTF_8_APPLICATION_JSON)
    public MatchStats getMatchStatsById(@PathParam("id") long id) {
        try {
            return SbdsInternalApis.getMatchStatsById(id);
        } catch (LeWebApplicationException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new LeWebApplicationException(e.getMessage(), LeStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 根据ID获取比赛
     *
     * @return
     */
    @LJSONP
    @GET
    @Path("/setting")
    @Produces(AlternateMediaType.UTF_8_APPLICATION_JSON)
    public List<String> getSettingDataById(
            @QueryParam("game_s_type") long gameStype,
            @QueryParam("setting_name") String configName) {
        try {
            return olympicSettingDataService.findConfigList(new Long(gameStype), configName);
        } catch (LeWebApplicationException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new LeWebApplicationException(e.getMessage(), LeStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 复制一个配置
     *
     * @return
     */
    @LJSONP
    @PUT
    @Path("/configCopy")
    @Produces(AlternateMediaType.UTF_8_APPLICATION_JSON)
    public Map<String, Object> saveOlympicsConfig(
            @QueryParam("game_s_type") long gameStype,
            @QueryParam("copy_id") long copyId
    ) {
        Map<String, Object> result = Maps.newHashMap();
        try {
            BoleOlympicsLiveConfigSet olympicsConfig = olympicConfigService.findOne(copyId);
            if (olympicsConfig == null) {
                result.put("result", false);
            } else {
                copySettingData(gameStype, copyId);
                BoleOlympicsLiveConfigSet currentOlympicsConfig = olympicConfigService.findOne(gameStype);
                if (currentOlympicsConfig == null) {
                    currentOlympicsConfig = new BoleOlympicsLiveConfigSet();
                    currentOlympicsConfig.setId(gameStype);
                    currentOlympicsConfig.setDeleted(false);
                }
                currentOlympicsConfig.setMatchExtendConfig(olympicsConfig.getMatchExtendConfig());
                currentOlympicsConfig.setTeamExtendConfig(olympicsConfig.getTeamExtendConfig());
                currentOlympicsConfig.setPlayerExtendConfig(olympicsConfig.getPlayerExtendConfig());
                currentOlympicsConfig.setCompetitorStatsConfig(olympicsConfig.getCompetitorStatsConfig());
                currentOlympicsConfig.setPlayerStatsConfig(olympicsConfig.getPlayerStatsConfig());
                currentOlympicsConfig.setTeamSectionResultConfig(olympicsConfig.getTeamSectionResultConfig());
                boolean saveResult = olympicConfigService.save(currentOlympicsConfig);
                result.put("result", saveResult);

            }
        } catch (LeWebApplicationException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new LeWebApplicationException(e.getMessage(), LeStatus.INTERNAL_SERVER_ERROR);
        }
        return result;
    }

    private void copySettingData(long gameSTypeId, long copyId) {
        BoleOlympicSettingDataSet settingDataSet = olympicSettingDataService.findOne(String.valueOf(copyId));
        if (settingDataSet == null) return;
        BoleOlympicSettingDataSet settingDataSet1 = olympicSettingDataService.findOne(String.valueOf(gameSTypeId));
        if (settingDataSet1 != null) return;
        settingDataSet.setId(String.valueOf(gameSTypeId));
        olympicSettingDataService.save(settingDataSet);
    }


    /**
     * 根据ID获取比赛
     *
     * @return
     */
    @LJSONP
    @GET
    @Path("/")
    @Produces(AlternateMediaType.UTF_8_APPLICATION_JSON)
    public List<BoleOlympicsLiveConfigSet.OlympicsConfig> getById(
            @QueryParam("game_s_type") long gameStype,
            @QueryParam("config_name") String configName) {
        try {
            return olympicConfigService.findConfigList(gameStype, configName);
        } catch (LeWebApplicationException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new LeWebApplicationException(e.getMessage(), LeStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 根据parent id获取子字典
     *
     * @return
     */
    @LJSONP
    @GET
    @Path("/dicts")
    @Produces(AlternateMediaType.UTF_8_APPLICATION_JSON)
    public List<DictEntry> getDictsByParentId(
            @QueryParam("parent_id") long parentId) {
        try {
            Long gameFTypeId = SbdsInternalApis.getDictEntriesByName("^奥运大项$").get(0).getId();
            Long disciplineId = SbdsInternalApis.getDictEntriesByName("^奥运分项$").get(0).getId();
            Long gameSTypeId = SbdsInternalApis.getDictEntriesByName("^奥运小项$").get(0).getId();
            if (parentId == gameFTypeId) {

                List<DictEntry> lists = SbdsInternalApis.getDictEntriesByParentId(parentId);
                if (CollectionUtils.isNotEmpty(lists)) {
                    List<DictEntry> newLists = Lists.newArrayList();
                    for (DictEntry currentDicts : lists) {
                        if (currentDicts.getCode() == null) continue;
                        newLists.add(currentDicts);
                    }
                    return newLists;
                }
                return null;
            }
            DictEntry parentDict = SbdsInternalApis.getDictById(parentId);
            if (parentDict == null) return null;
            if (LeNumberUtils.toLong(parentDict.getParentId()) == gameFTypeId) {
                LOGGER.info("the current id is a dict of gameFtypeId:{}", parentId);
                return SbdsInternalApis.getDictEntryByCodeAndParentId(parentDict.getCode(), disciplineId);
            } else if (LeNumberUtils.toLong(parentDict.getParentId()) == disciplineId) {
                LOGGER.info("the current id is a dict of disciplineId:{}", parentId);
                return SbdsInternalApis.getDictEntryByCodeAndParentId(parentDict.getCode().substring(3, 5), gameSTypeId);
            }
            return null;
        } catch (LeWebApplicationException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new LeWebApplicationException(e.getMessage(), LeStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 增加一个配置或者公式
     *
     * @return
     */
    @LJSONP
    @PUT
    @Path("/configs")
    @Produces(AlternateMediaType.UTF_8_APPLICATION_JSON)
    public Map<String, Object> saveOlympicsConfig(
            @QueryParam("game_s_type") long gameStype,
            @QueryParam("name") String configName,
            @QueryParam("element_path") String elementPath, @QueryParam("op") String op, @QueryParam("right_element_path") String rightElementPath, @QueryParam("attribute_re_name") String attributeReName, @QueryParam("position_key") String positionKey, @QueryParam("formatter_type") String formatterType) {
        Map<String, Object> result = Maps.newHashMap();
        try {
            List<BoleOlympicsLiveConfigSet.OlympicsConfig> lists = olympicConfigService.findConfigList(gameStype, configName);
            boolean isExist = false;
//            if (CollectionUtils.isNotEmpty(lists)) {
//                for (BoleOlympicsLiveConfigSet.OlympicsConfig curretn : lists) {
//                    if (curretn.getElementPath().equalsIgnoreCase(elementPath)) {
//                        isExist = true;
//                    }
//                }
//            }
            if (isExist) {
                result.put("result", false);
            } else {
                BoleOlympicsLiveConfigSet.OlympicsConfig olympicsConfig = new BoleOlympicsLiveConfigSet.OlympicsConfig();
                olympicsConfig.setAttributeReName(attributeReName);
                olympicsConfig.setElementPath(elementPath);
                olympicsConfig.setOp(op);
                olympicsConfig.setRightElementPath(rightElementPath);
                olympicsConfig.setPositionKey(positionKey);
                olympicsConfig.setFormatterType(formatterType);
                boolean saveResult = olympicConfigService.saveConfig(gameStype, configName, olympicsConfig);
                result.put("result", saveResult);
            }

        } catch (LeWebApplicationException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new LeWebApplicationException(e.getMessage(), LeStatus.INTERNAL_SERVER_ERROR);
        }
        return result;
    }

    /**
     * 修改配置
     *
     * @return
     */
    @LJSONP
    @PUT
    @Path("/configs/update")
    @Produces(AlternateMediaType.UTF_8_APPLICATION_JSON)
    public Map<String, Object> updateOlympicsConfig(
            @QueryParam("game_s_type") long gameStype,
            @QueryParam("name") String configName,
            @QueryParam("old_path") String oldPath,
            @QueryParam("element_path") String elementPath,
            @QueryParam("op") String op,
            @QueryParam("right_element_path") String rightElementPath,
            @QueryParam("attribute_re_name") String attributeReName,
            @QueryParam("position_key") String positionKey,
            @QueryParam("formatter_type") String formatterType) {
        Map<String, Object> result = Maps.newHashMap();
        try {
            if (oldPath.equalsIgnoreCase(positionKey)) {
                BoleOlympicsLiveConfigSet.OlympicsConfig olympicsConfig = new BoleOlympicsLiveConfigSet.OlympicsConfig();
                olympicsConfig.setRightElementPath(rightElementPath);
                olympicsConfig.setElementPath(elementPath);
                olympicsConfig.setOp(op);
                olympicsConfig.setAttributeReName(attributeReName);
                olympicsConfig.setElementPath(elementPath);
                olympicsConfig.setPositionKey(positionKey);
                olympicsConfig.setFormatterType(formatterType);
                boolean saveResult = olympicConfigService.saveConfig(gameStype, configName, olympicsConfig);
                result.put("result", saveResult);
            } else {
                List<BoleOlympicsLiveConfigSet.OlympicsConfig> lists = olympicConfigService.findConfigList(Long.valueOf(gameStype), configName);
                boolean isExist = false;
                if (CollectionUtils.isNotEmpty(lists)) {
                    for (BoleOlympicsLiveConfigSet.OlympicsConfig curretn : lists) {
                        if (curretn.getPositionKey().equalsIgnoreCase(positionKey)) {
                            isExist = true;
                        }
                    }
                }
                if (isExist) {
                    result.put("result", false);
                } else {
                    BoleOlympicsLiveConfigSet.OlympicsConfig olympicsConfig = new BoleOlympicsLiveConfigSet.OlympicsConfig();
                    olympicsConfig.setRightElementPath(rightElementPath);
                    olympicsConfig.setElementPath(elementPath);
                    olympicsConfig.setOp(op);
                    olympicsConfig.setAttributeReName(attributeReName);
                    olympicsConfig.setElementPath(elementPath);
                    olympicsConfig.setPositionKey(positionKey);
                    olympicsConfig.setFormatterType(formatterType);
                    boolean saveResult = olympicConfigService.saveConfig(gameStype, configName, olympicsConfig);
                    if (saveResult) {
                        olympicConfigService.deleteConfig(gameStype, configName, oldPath);
                    }
                    result.put("result", true);
                }
            }

        } catch (LeWebApplicationException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new LeWebApplicationException(e.getMessage(), LeStatus.INTERNAL_SERVER_ERROR);
        }
        return result;
    }


    /**
     * 删除一个配置
     *
     * @return
     */
    @LJSONP
    @DELETE
    @Path("/configs")
    @Produces(AlternateMediaType.UTF_8_APPLICATION_JSON)
    public Map<String, Object> deleteOlympicsConfig(
            @QueryParam("game_s_type") long gameStype,
            @QueryParam("name") String configName,
            @QueryParam("key") String key) {
        Map<String, Object> result = Maps.newHashMap();
        try {
            boolean saveResult = olympicConfigService.deleteConfig(gameStype, configName, key);
            result.put("result", saveResult);
        } catch (LeWebApplicationException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new LeWebApplicationException(e.getMessage(), LeStatus.INTERNAL_SERVER_ERROR);
        }
        return result;
    }

}
