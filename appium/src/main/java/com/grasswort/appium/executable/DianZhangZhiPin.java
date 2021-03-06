package com.grasswort.appium.executable;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grasswort.appium.app.Apps;
import com.grasswort.appium.driver.DriverProxy;

import io.appium.java_client.android.AndroidElement;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
/**
 * 店长直聘数据抓取
 * @author xuliangliang
 *
 */
public class DianZhangZhiPin implements Run {
	private Logger logger = LoggerFactory.getLogger(DianZhangZhiPin.class);
	//驱动代理
	private DriverProxy proxy = new DriverProxy(Apps.DZZP);
	//数据上传
	private final String UPLOAD_URL = "http://qiye.jianzhibao.com:4007/customerResource/dzzp/send";
	private static OkHttpClient client =new OkHttpClient.Builder()
			.readTimeout(20,TimeUnit.SECONDS)//设置读取超时时间
			.writeTimeout(10,TimeUnit.SECONDS)//设置写的超时时间
			.connectTimeout(10,TimeUnit.SECONDS)//设置连接超时时间
			.build();

	
	//首页职位名称
	final By TITLES = By.id("com.hpbr.directhires:id/tv_job_name");
	//详情页职位名称
	final By INNER_TITLE = By.id("com.hpbr.directhires:id/tv_job");
	//详情页职位地址
	final By INNER_ADDRESS = By.id("com.hpbr.directhires:id/tv_location");
	//详情页公司名称
	final By INNER_COMPANY = By.id("com.hpbr.directhires:id/tv_shop_name");
	//详情页联系人
	final By INNER_CONTACT = By.id("com.hpbr.directhires:id/tv_title_job");
	//详情页联系方式
	final By INNER_PHONE = By.id("com.hpbr.directhires:id/tv_tel");
	//详情页返回按钮
	final By BACK_BUTTON = By.id("com.hpbr.directhires:id/ic_back");
	
	final By TITLES_CLICK_ERROR = By.id("com.hpbr.directhires:id/tv_filter");
	
	final By TITLES_CLICK_ERROR2 = By.id("com.hpbr.directhires:id/title_iv_back");
	
	@Override
	public void run() {
		logger.info("那么，开始咯！");
		proxy.forceWait(10);
		proxy.waitTargets(TITLES);
		while(true) {
			proxy.batchHandle(TITLES, TITLE_CONSUMER);
			proxy.swipeToUp();
		}
		
	}
	
	//首页职位消费者
	final Consumer<AndroidElement> TITLE_CONSUMER = e -> {
		//点击元素e可能出现两种情况,返回出现的结果
		final By CLICK_RESULT = proxy.clickWait(e, INNER_TITLE,TITLES_CLICK_ERROR,TITLES_CLICK_ERROR2);
		
		if(INNER_TITLE.equals(CLICK_RESULT)) {
			try {
				if(!swipUpLimitToFindElement(2, INNER_ADDRESS))
					return;
				String data_address = proxy.getText(INNER_ADDRESS);
				logger.info("【工作地址】：{}", data_address);
				
				if(!swipUpLimitToFindElement(2, INNER_CONTACT))
					return;
				String data_contact = proxy.getText(INNER_CONTACT);
				logger.info("【联系人】:{}", data_contact);
				
				if(!swipUpLimitToFindElement(2, INNER_COMPANY))
					return;
				String data_company = proxy.getText(INNER_COMPANY);
				logger.info("【公司名称】:{}", data_company);
				
				if(!swipUpLimitToFindElement(2, INNER_PHONE))
					return;
				String data_phone = proxy.getText(INNER_PHONE);
				logger.info("【联系电话】:{}",data_phone);
				
				//上传抓取信息
				sendDzzpData(data_company, data_address, data_contact, data_phone);
			}finally {
				//保证返回必须执行
        		proxy.clickTarget(BACK_BUTTON,TITLES);
			}
		}else if(TITLES_CLICK_ERROR.equals(CLICK_RESULT)){
			//点击出错的一种情况。可能会误点出商圈搜索,再次点击收回搜索框。并跳过本次点击。
			e.click();
		}else if(TITLES_CLICK_ERROR2.equals(CLICK_RESULT)) {
			proxy.clickIfExist(TITLES_CLICK_ERROR2);
		}
	};
	//上滑一定次数去寻找元素
    private boolean swipUpLimitToFindElement(int count , By by) {
    	int i = 0;
    	while(!proxy.exists(by)){
			proxy.swipeToUp();//上滑
			i++;
			if(i>3) {
				return false;
			}
		}
    	return true;
    }
    
    //上传数据
    private void sendDzzpData(String companyName,String address,String contact,String contactPhone) {
    	if(StringUtils.isBlank(companyName)||StringUtils.isBlank(contactPhone))
    		return;
    	FormBody body = new FormBody.Builder()
    			.add("company_name", companyName)
    			.add("address", address)
    			.add("contact", contact)
    			.add("contact_phone", contactPhone)
    			.build();
    	Request request = new Request.Builder()
    			.url(UPLOAD_URL)
    			.post(body)
    			.build();
    	try {
			Response response = client.newCall(request).execute();
			logger.info("上传数据结果：{}",response.toString());
			response.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
