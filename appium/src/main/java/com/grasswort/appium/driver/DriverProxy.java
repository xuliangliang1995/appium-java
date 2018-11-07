package com.grasswort.appium.driver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grasswort.appium.app.Apps;

import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;

public class DriverProxy {
	private static Logger logger = LoggerFactory.getLogger(DriverProxy.class);
	//寻找一个元素的默认时间
	private static final int DEFAULT_FIND_TIME = 2;
	private AndroidDriver<AndroidElement> driver;
	private static final String DEFAULT_LOG_TEMPLATE = "============================={}=============================";
	private static final List<AndroidElement> EMPTY_ELEMENT_LIST = new ArrayList<AndroidElement>();
	
	/**
	 * 
	 * @param udid 设备的udid (adb devices)
	 * @param platformName 安卓自动化还是IOS自动化
	 * @param platformVersion 操作系统版本
	 * @param appPackage 被测app的包名
	 * @param appActivity 被测app的入口Activity名称
	 */
	public DriverProxy(Apps app) {
		super();
		String udid = app.getUdid(),
			   platformName = app.getPlatformName(),
			   platformVersion = app.getPlatformVersion(),
			   appPackage = app.getAppPackage(),
			   appActivity = app.getAppActivity();
		//初始化驱动
		DesiredCapabilities caps=new DesiredCapabilities();
    	caps.setCapability("automationName", "Appium");
		caps.setCapability("deviceName", udid);//设备名称
		caps.setCapability("platformName", platformName); //安卓自动化还是IOS自动化
		caps.setCapability("platformVersion", platformVersion); //安卓操作系统版本
		caps.setCapability("udid", udid); //设备的udid (adb devices 查看到的)
		caps.setCapability("appPackage",appPackage);//被测app的包名
		caps.setCapability("appActivity",appActivity);//被测app的入口Activity名称
		caps.setCapability("noReset", true);
		caps.setCapability("simpleIsVisibleCheck", false);
        caps.setCapability("autoGrantPermissions", true);
        caps.setCapability("clearSystemFiles", true);
        caps.setCapability("unicodeKeyboard" ,"True");
        caps.setCapability("resetKeyboard", "True");
        logger.info("{}版本:{}",platformName,platformVersion);
        try {
		    log("初始化驱动！");
		    this.driver = new AndroidDriver<AndroidElement>(new URL("http://127.0.0.1:4723/wd/hub"), caps);
 			this.driver.manage().timeouts().implicitlyWait(DEFAULT_FIND_TIME, TimeUnit.SECONDS); 
 			int X=this.driver.manage().window().getSize().getWidth();
 			int Y=this.driver.manage().window().getSize().getHeight();
 			log("手机分辨率:"+X+"*"+Y);
 		} catch (MalformedURLException e1) {
 			log("驱动初始化失败");
 		}
	}
	/* 返回内置driver对象*/
	public Optional<AndroidDriver<AndroidElement>> getInnnerDriver() {
		return Optional.ofNullable(driver);
	}
	
	/*判断?个元素是否存在*/
	public boolean exists(By by) {
		return driver.findElements(by).size()>0;
	}
	public boolean exist(By by,String text){
		logger.info("find 【{}】 from 【{}】" ,text,by.toString());
    	try {
			for (AndroidElement e : findAll(by)) {
				if(e.getText().equals(text)){
					logger.info("find success");
					return true;
				}
			}
			logger.info("find failure");
			return false;
		} catch (Exception e) {
			logger.info("find failure");
			return false;
		}
    }
	/* 寻找元素*/
	public Optional<AndroidElement> find(By by) {
		try {
			return Optional.ofNullable(driver.findElement(by));
		} catch (Exception e) {
			logger.info("find 【{}】 failure",by.toString());
			e.printStackTrace();
		}
		return Optional.empty();
	}
	
	public List<AndroidElement> findAll(By by){
		logger.info("find all 【{}】",by.toString());
    	try {
			return driver.findElements(by);
		} catch (Exception e) {
			return EMPTY_ELEMENT_LIST;
		}
	}
	
	/* 元素赋值*/
	public boolean setValue(By by,String value) {
		logger.info("【{}】赋值:{}",by.toString(),value);
		Optional<AndroidElement> e = find(by);
		if(e.isPresent()) {
			AndroidElement target = e.get();
			target.clear();
			target.sendKeys(value);
			return true;
		}
		return false;
	}
	/*点击*/
    public void click(By by,boolean force){
    	logger.info("click 【{}】",by.toString());
    	while(force){
    		try {
				driver.findElement(by).click();
				break;
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    }
    public void click(AndroidElement e,boolean force){
    	logger.info("click 【{}】",e.toString());
    	while(force){
    		try {
				e.click();
				break;
			} catch (Exception e1) {
				e1.printStackTrace();
			}		
    	}
    }
    public boolean clickTextFromBy(By by,String text){
    	for (AndroidElement travel : findAll(by)) {
			if(text.equals(travel.getText())){
				travel.click();
				return true;
			}
		}
    	return false;
    }
    /*获取文本内容*/
    public String getText(By by){
    	try {
			return find(by).map(AndroidElement::getText).orElse("");
		} catch (Exception e) {
			return "";
		}
    }
    public String getText(AndroidElement ele,By by){
    	try{
    		return ele.findElement(by).getText();
    	} catch (Exception e) {
			return "";
		}
    }
    public List<String> getTexts(AndroidElement ele,By by){
    	List<String> texts = new ArrayList<String>();
    	try {			
			List<MobileElement> subEles = ele.findElements(by);
			for (MobileElement subEle : subEles) {
				texts.add(subEle.getText());
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return texts;    	
    }
    /*强制等待*/
    public void forceWait(int seconds){
    	logger.info("force wait {} seconds",seconds);
    	try {
			Thread.sleep(seconds*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    /*上滑*/
    public  void swipeToUp() {  
    	logger.info("swip to up");
        try {
			int width = driver.manage().window().getSize().width;  
			int height = driver.manage().window().getSize().height;  
			//用3秒的时间,从屏幕高度7/8处上滑到2/8处（缓慢上滑、防止手机卡造成滑动失败）
			driver.swipe(width / 2, height * 7 / 8, width / 2, height * 2 / 8, 3000);
		} catch (Exception e) {
			swipeToUp();
		}  
        forceWait(1);//强制等待1秒
    }  
    public void leftSwipToUp(){
    	logger.info("swip left to up !");
        try {
			int width = driver.manage().window().getSize().width;  
			int height = driver.manage().window().getSize().height;  
			//左侧上滑，有时候只需要滑动屏幕的左侧，常用于级联操作
			driver.swipe(width / 4, height * 7 / 8, width / 4, height * 2 / 8, 3000);
		} catch (Exception e) {
			swipeToUp();
		}  
        forceWait(1);//强制等待1秒
    }
    public void rightSwipToUp(){
    	logger.info("swip right to up !");
        try {
			int width = driver.manage().window().getSize().width;  
			int height = driver.manage().window().getSize().height;  
			driver.swipe(width*3 / 4, height * 7 / 8, width*3 / 4, height * 2 / 8, 3000);
		} catch (Exception e) {
			swipeToUp();
		}  
        forceWait(1);//强制等待1秒
    }
    /*****************************************高级操作【由于某些操作存在一定的失败几率,所以会进行一些逻辑上的判断,保证一定的成功率】************************************************/
    /*批量消费*/
    public void batchHandle(By by,Consumer<AndroidElement> consumer) {
    	findAll(by).stream().forEach(e -> consumer.accept(e));
    }
    /*点击等待target中的都可能出现，哪个出现返回哪个*/
    public By clickWait(By click,By ... target) {
    	Optional<AndroidElement> e = find(click);
    	int i = 0;
    	do {
    		i++;
    		if(e.isPresent()) {
    			e.get().click();
    			Optional<By> opt = Arrays.stream(target).filter(t -> exists(t)).findFirst();
    			if(opt.isPresent()) {
    				return opt.get();
    			}
    		}else {
    			//ignore
    		}
    	}while(i<10);
		return null;
    }
    public By clickWait(AndroidElement e,By ... target) {
    	int i = 0;
    	do {
    		i++;
    			e.click();
    			Optional<By> opt = Arrays.stream(target).filter(t -> exists(t)).findFirst();
    			if(opt.isPresent()) {
    				return opt.get();
    			}
    	}while(i<10);
		return null;
    }
    /*【点击?个By直到出现目标 target,不出现则会继续尝试点击】*/
    public boolean clickTarget(By click,By target){
    	logger.info("点击【{}】==》【{}】",click.toString(),target.toString());
    	int i = 0;
    	do{
    		i++;
			if(i == 10) return false;
			Optional<AndroidElement> e = find(click);
    		if(e.isPresent()) {
    			e.get().click();
    		}else {
    			//ignore
    		}
    	}while(!exists(target));
    	return true;
    }
    public boolean clickTarget(By click,By target,String targetText){
    	logger.info("点击【{}】==》【{}#{}】",click.toString(),target.toString(),targetText);
    	int i = 0;
    	do{
    		i++;
			if(i == 10) return false;
			Optional<AndroidElement> e = find(click);
    		if(e.isPresent()) {
    			e.get().click();
    		}else {
    			//ignore
    		}		 		
    	}while(!exist(target,targetText));
    	return true;
    }
    public boolean clickTarget(AndroidElement click,By target){
    	logger.info("点击【{}】==》【{}】",click.toString(),target.toString());
    	int i = 0;
    	do{
    		i++;
			if(i == 10) return false;
			try {
				click.click();
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}while(!exists(target));
    	return true;
    }
    /*耐心等待多个目标出现*/
    public By waitTargets(By... targets){
    	logger.info("寻找目标:【{}】",Arrays.stream(targets).map(By::toString).reduce((a,b) -> a+","+b).orElse(""));
    	By by = null;
    	boolean exist=false;
    	do{
    		forceWait(1);
    		for (By target : targets) {
				if(exists(target)){
					exist=true;
					by = target;
					break;
				}
			}
    	}while(!exist);   
    	logger.info("目标出现:【{}】",by.toString());
    	return by;
    }

    /*依次点击,按照By数组顺序点击,用于快速导航进一个页面*/
    public void clickInturn(By... travels){
    	for (int i = 0; i < travels.length-1; i++) {
			clickTarget(travels[i],travels[i+1]);
		}
    }
    
    /*上滑找元素*/
    public void swipToFind(By by,By ifExistBreak){
    	logger.info("上滑寻找元素:【{}】",by.toString());
    	while(!exists(by)){
    		if(ifExistBreak!=null&&exists(ifExistBreak)){
    			logger.info("已找到！");
    			break;
    		}
    		swipeToUp();
    	}
    }
    public void leftSwipToFind(By by,By ifExistBreak){
    	logger.info("左侧上滑寻找元素:【{}】",by.toString());
    	while(!exists(by)){
    		if(ifExistBreak!=null&&exists(ifExistBreak)){
    			logger.info("已找到！");
    			break;
    		}
    		leftSwipToUp();
    	}
    }
    public void RightSwipToFind(By by,By ifExistBreak){
    	logger.info("右侧上滑寻找元素:【{}】",by.toString());
    	while(!exists(by)){
    		if(ifExistBreak!=null&&exists(ifExistBreak)){
    			logger.info("已找到！");
    			break;
    		}
    		rightSwipToUp();
    	}
    }
    /*如果存在就点击*/
    public void clickIfExist(By by){
    	logger.info("判断【{}】是否存在?");
    	if(exists(by)){
    		logger.info("存在、并点击！");
    		click(by,true);
    	}else {
    		logger.info("不存在、跳过！");
    	}
    }
    /*默认log*/
	private final void log(String info) {
		logger.info(DEFAULT_LOG_TEMPLATE,info);
	}
}
