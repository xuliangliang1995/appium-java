/**
 * 
 */
package com.grasswort.appium.executable;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.grasswort.appium.app.Apps;
import com.grasswort.appium.driver.DriverProxy;

import io.appium.java_client.android.AndroidElement;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 
 * 自动化短信发送
 * @author xuliangliang
 *
 */
public class MMS implements Run {
	
	private Logger logger = LoggerFactory.getLogger(MMS.class);
	// 驱动代理
	private DriverProxy proxy = new DriverProxy(Apps.MMS);
	// OKHttp
	private static OkHttpClient client =new OkHttpClient.Builder()
			.readTimeout(20,TimeUnit.SECONDS)//设置读取超时时间
			.writeTimeout(10,TimeUnit.SECONDS)//设置写的超时时间
			.connectTimeout(10,TimeUnit.SECONDS)//设置连接超时时间
			.build();
	
	private final String SMS_GET_URL = "http://qiye.jianzhibao.com:4007/admin/sms/getOne";
	
	private final String SMS_POST_AFTER_SENT_TEMPLATE = "http://qiye.jianzhibao.com:4007/admin/sms/%d/sent";
	
	final By CREATE_SMS = By.id("com.android.mms:id/action_new");
	
	final By CONTACT_INPUT = By.id("com.android.mms:id/recipients_editor");
	
	final By CONTENT_INPUT = By.id("com.android.mms:id/embedded_text_editor");
	
	final By SEND_BTN = By.id("com.android.mms:id/send_button");
	
	final By BACK = By.id("android:id/up");
	
	final By CANCEL = By.id("android:id/button2");
	
	public transient int smsid = 0;

	/* (non-Javadoc)
	 * @see com.grasswort.appium.executable.Run#run()
	 */
	@Override
	public void run() {
		logger.info("那么，开始咯！");
		proxy.forceWait(3);
		while(true) {
			Optional<SMS> sms = this.getSMS();
			if (sms.isPresent()) {
				if (! Objects.equals(sms.get().getId(), smsid)) {
					smsid = sms.get().getId();
					proxy.clickTarget(CREATE_SMS, CONTACT_INPUT);
					proxy.setValue(CONTACT_INPUT, sms.get().getPhone());
					proxy.setValue(CONTENT_INPUT, sms.get().getContent());
					proxy.click(SEND_BTN, true);
					//proxy.back();
					proxy.clickTarget(BACK, CREATE_SMS);
					markSent(sms.get());
				} 
			} else {
				proxy.swipeToUp();
				logger.info("暂无可发送短信！");
				proxy.forceWait(3);
			}
		}
	}
	/**
	 * 读取短信
	 *@author xuliangliang 
	 *@return
	 */
	private static AtomicLong key = new AtomicLong();
	private Optional<SMS> getSMS() {
		Request request = new Request.Builder()
    			.url(SMS_GET_URL)
    			.get()
    			.build();
		try {
			Response response = client.newCall(request).execute();
			if (response.isSuccessful()) {
				JSONObject result = JSONObject.parseObject(response.body().string());
				JSONObject smsObj = result.getJSONObject("data");
				if (smsObj != null) {
					SMS sms = new SMS();
					sms.id = smsObj.getIntValue("id");
					sms.phone = smsObj.getString("sendToPhone");
					sms.content = smsObj.getString("content");
					response.close();
					return Optional.of(sms);
				}
			}
			response.close();
		} catch (IOException e) {
			e.printStackTrace();
			return Optional.empty();
		} 
		return Optional.empty();
	}
	
	/**
	 * 
	 *@author xuliangliang 
	 *@param sms
	 */
	private void markSent(SMS sms) {
		Request request = new Request.Builder()
    			.url(String.format(SMS_POST_AFTER_SENT_TEMPLATE, sms.getId()))
    			.post(new FormBody.Builder().build())
    			.build();
		try {
			Response response = client.newCall(request).execute();
			response.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 短信信息载体
	 * @author xuliangliang
	 *
	 */
	private class SMS {
		private int id;
		private String phone;
		private String content;
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getPhone() {
			return phone;
		}
		public void setPhone(String phone) {
			this.phone = phone;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		
	}

}
