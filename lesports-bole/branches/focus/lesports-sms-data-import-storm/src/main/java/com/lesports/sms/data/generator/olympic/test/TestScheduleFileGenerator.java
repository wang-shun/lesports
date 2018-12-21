package com.lesports.sms.data.generator.olympic.test;
import com.google.common.collect.Lists;
import com.lesports.sms.data.generator.FileGenerator;
import org.apache.commons.collections.CollectionUtils;
import java.io.File;
import java.util.*;

///**
// * lesports-bole.
// *
// * @author pangchuanxiao
// * @since 2016/3/23
// */
public class TestScheduleFileGenerator implements FileGenerator {
    @Override
    public List<String> getFileUrl() {
       String path = "D:/olympics/old/";
       File file = new File(path);
        File[] tempList = file.listFiles();
       List<String> fileList = new ArrayList<>();
      for (int i = 0; i < tempList.length; i++) {
          if (tempList[i].isFile()&&tempList[i].getName().contains("DT_SCHEDULE")) {
               fileList.add("file:///" + tempList[i].getAbsolutePath());
           }
       }
        Collections.sort(fileList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });

       return fileList;
    }
   }

