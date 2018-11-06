package com.grasswort.appium.executable;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

public class DianZhangZhiPin implements Run {
	private Logger logger = LoggerFactory.getLogger(DianZhangZhiPin.class);
	//驱动代理
	private DriverProxy proxy = new DriverProxy(Apps.DZZP);
	//数据上传
	private final String URL_PREFIX = "数据上传地址";
	private static OkHttpClient client =new OkHttpClient.Builder()
			.readTimeout(20,TimeUnit.SECONDS)//设置读取超时时间
			.writeTimeout(10,TimeUnit.SECONDS)//设置写的超时时间
			.connectTimeout(10,TimeUnit.SECONDS)//设置连接超时时间
			.build();
	
	@Override
	public void run() {

    	logger.info("那么，开始咯！");
    	proxy.forceWait(10);
    	proxy.waitTargets(By.id("com.hpbr.directhires:id/tv_job_title"));//出现服务员tab
    	By j = By.id("com.hpbr.directhires:id/tv_codedec");
    	while(true){
    		List<AndroidElement> jobs = proxy.findAll(j);
    		if(jobs.isEmpty())
    			continue;
        	jobs.stream().forEach(job -> {
        		if(proxy.clickTarget(job, By.id("com.hpbr.directhires:id/tv_job"))) {
        			By address = By.id("com.hpbr.directhires:id/tv_location");
        			if(!swipUpLimitToFindElement(5, address))
        				return;
        			String data_address = proxy.getText(address);
				logger.info("【工作地址】：{}", data_address);

				By contact = By.id("com.hpbr.directhires:id/tv_title_job");
				if(!swipUpLimitToFindElement(5, contact))
					return;
				String data_contact = proxy.getText(contact);
				logger.info("【联系人】:{}", data_contact);

				By company = By.id("com.hpbr.directhires:id/tv_shop_name");
				if(!swipUpLimitToFindElement(5, company))
					return;
				String data_company = proxy.getText(company);
				logger.info("【公司名称】:{}", data_company);

				By phone = By.id("com.hpbr.directhires:id/tv_tel");
				if(!swipUpLimitToFindElement(5, phone))
					return;
				String data_phone = proxy.getText(phone);
				logger.info("【联系电话】:{}",data_phone);

				//上传抓取信息
				sendDzzpData(data_company, data_address, data_contact, data_phone);

				By back = By.id("com.hpbr.directhires:id/ic_back");
				proxy.clickTarget(back, j);
				}
			});
        	proxy.swipeToUp();
    	}
    	/*log.info("那么，结束咯！");
    	driver.quite();*/
    

	}
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
    			.url(String.format("%s/customerResource/dzzp/send", URL_PREFIX))
    			.post(body)
    			.build();
    	try {
		Response response = client.newCall(request).execute();
		logger.info("上传数据结果：{}",response.toString());
		response.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    			
    }
}
