package com.genersoft.iot.vmp.service.impl;

import com.genersoft.iot.vmp.storager.dao.UserMapper;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SmsService {

    @Autowired
    private UserMapper wvpUserMapper;
    private String smsSdkAppId = "1400927587";

    private String signName = "江苏祥钿能源科技";

//    报警信息推送
    public void sendSmsToUsers(String deviceId) {
        // 从环境变量中获取secretId和secretKey
        String secretId = System.getenv("TENCENTCLOUD_SECRET_ID");
        String secretKey = System.getenv("TENCENTCLOUD_SECRET_KEY");


        String templateId = "2226350";

        List<String> phoneNumbers = wvpUserMapper.findPhoneNumbers();

        Credential cred = new Credential(secretId, secretKey);
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setReqMethod("POST");
        httpProfile.setEndpoint("sms.tencentcloudapi.com");

        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setSignMethod("TC3-HMAC-SHA256");
        clientProfile.setHttpProfile(httpProfile);

        SmsClient client = new SmsClient(cred, "ap-guangzhou", clientProfile);

        deviceId = deviceId.substring(0, 6);
        for (String phoneNumber : phoneNumbers) {
            try {
                SendSmsRequest req = new SendSmsRequest();
                req.setSmsSdkAppId(smsSdkAppId);
                req.setSignName(signName);
                req.setTemplateId(templateId);
                req.setPhoneNumberSet(new String[]{phoneNumber});
                req.setTemplateParamSet(new String[]{deviceId});

                SendSmsResponse resp = client.SendSms(req);
                System.out.println("短信发送成功: " + phoneNumber + ", 响应: " + resp.toString());
            } catch (TencentCloudSDKException e) {
                System.out.println("发送短信失败: " + phoneNumber + ", 错误: " + e);
            }
        }
    }

//    注册用户短信推送
    public void sendSmsToRegisterUsers(String phoneNumber,String username) {
        // 从环境变量中获取secretId和secretKey
        String secretId = System.getenv("TENCENTCLOUD_SECRET_ID");
        String secretKey = System.getenv("TENCENTCLOUD_SECRET_KEY");

        String templateId = "2235053";

        Credential cred = new Credential(secretId, secretKey);
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setReqMethod("POST");
        httpProfile.setEndpoint("sms.tencentcloudapi.com");

        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setSignMethod("TC3-HMAC-SHA256");
        clientProfile.setHttpProfile(httpProfile);

        SmsClient client = new SmsClient(cred, "ap-guangzhou", clientProfile);
        try {
            SendSmsRequest req = new SendSmsRequest();
            req.setSmsSdkAppId(smsSdkAppId);
            req.setSignName(signName);
            req.setTemplateId(templateId);
            req.setPhoneNumberSet(new String[]{phoneNumber});
            req.setTemplateParamSet(new String[]{username});

            SendSmsResponse resp = client.SendSms(req);
            System.out.println("短信发送成功: " + phoneNumber + ", 响应: " + resp.toString());
        } catch (TencentCloudSDKException e) {
            System.out.println("发送短信失败: " + phoneNumber + ", 错误: " + e);
        }

    }
}
