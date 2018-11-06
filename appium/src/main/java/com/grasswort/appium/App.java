package com.grasswort.appium;

import com.grasswort.appium.executable.DianZhangZhiPin;
import com.grasswort.appium.executable.Run;

/**
 * Hello world!
 *
 */
public class App 
{
	//选择要运行的应用
	private Run run = new DianZhangZhiPin();
	
	public void run() {
		if(run!=null)
			run.run();
	}
    public static void main( String[] args )
    {
        new App().run();
    }
    /*
	     1、Android SDK:http://tools.android-studio.org/index.php/sdk
	    	【国内代理】Tools => Options => HttpProxyServer:mirrors.opencas.cn 80 => others[force ...]
	     2、Appium:https://pan.baidu.com/s/1jGvAISu#list/path=%2F
	     3、.net framework 4.5 百度下载
	     4、Java环境
      */
}
