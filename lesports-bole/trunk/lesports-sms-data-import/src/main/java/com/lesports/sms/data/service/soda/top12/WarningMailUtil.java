package com.lesports.sms.data.service.soda.top12;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;

/**
 * Created by qiaohongxin on 2016/9/30.
 */
@Service("warningMailUtil")
public class WarningMailUtil {
    @Resource
    private JavaMailSenderImpl javaMailSenderImpl;

    //发送报警邮件
    public Boolean sendMai(String subject, String fileName) {
        MimeMessage mimeMessage = javaMailSenderImpl.createMimeMessage();
        String sendTo[] = {"qiaohongxin@letv.com", "zhonglin@letv.com", "lufei1@letv.com","wangranyang@letv.com"};
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            mimeMessageHelper.setFrom("qiaohongxin@letv.com");
            mimeMessageHelper.setTo(sendTo);
            mimeMessageHelper.setSubject("【数据报障】data import error");
            mimeMessageHelper.setText(fileName, true);
            javaMailSenderImpl.send(mimeMessage);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }

    }
}
