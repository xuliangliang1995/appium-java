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
}
