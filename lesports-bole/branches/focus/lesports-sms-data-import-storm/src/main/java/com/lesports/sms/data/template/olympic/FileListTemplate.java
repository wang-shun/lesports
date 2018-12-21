package com.lesports.sms.data.template.olympic;

import com.google.common.collect.Lists;
import com.lesports.sms.data.template.XmlTemplate;
import com.lesports.utils.xml.annotation.XPath;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * lesports-projects
 *
 * @author pangchuanxiao
 * @since 16-2-18
 */
public class FileListTemplate extends XmlTemplate {
    @XPath({"/file-list/file"})
    private List<FileDes> files;

    @Override
    public List<Object> getData() {
        List<Object> res = Lists.newArrayList();
        if (CollectionUtils.isEmpty(files)) {
            return Collections.emptyList();
        }
        for (FileDes code : files) {
            String fileName = code.getName();
            res.add(fileName);
        }

        Collections.sort(res, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                if (null == o1||null ==o2) {
                    return 0;
                }
                return ((String)o1).compareTo((String)o2);
            }
        });

        return res;
    }

    public static class FileDes {
        @XPath("./@name")
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }
}


