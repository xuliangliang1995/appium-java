package com.grasswort.appium.app;

public enum Apps {
	DZZP("7TN7HASCQC7HV865","Android","4.4.2","com.hpbr.directhires","com.hpbr.directhires.module.WelAct");
	/**
	  * 查看连接设备：adb devices
	  *查看apk报名以及入口Activity名称：aapt dump badging 'D:\apk\com.hpbr.directhires_401040.apk'
	 * @param udid 设备的udid (adb devices)
	 * @param platformName 安卓自动化还是IOS自动化
	 * @param platformVersion 操作系统版本
	 * @param appPackage 被测app的包名
	 * @param appActivity 被测app的入口Activity名称
	 */
	private Apps(String udid, String platformName, String platformVersion, String appPackage, String appActivity) {
		this.udid = udid;
		this.platformName = platformName;
		this.platformVersion = platformVersion;
		this.appPackage = appPackage;
		this.appActivity = appActivity;
	}
	private String udid;
	private String platformName;
	private String platformVersion;
	private String appPackage;
	private String appActivity;
	public String getUdid() {
		return udid;
	}
	public void setUdid(String udid) {
		this.udid = udid;
	}
	public String getPlatformName() {
		return platformName;
	}
	public void setPlatformName(String platformName) {
		this.platformName = platformName;
	}
	public String getPlatformVersion() {
		return platformVersion;
	}
	public void setPlatformVersion(String platformVersion) {
		this.platformVersion = platformVersion;
	}
	public String getAppPackage() {
		return appPackage;
	}
	public void setAppPackage(String appPackage) {
		this.appPackage = appPackage;
	}
	public String getAppActivity() {
		return appActivity;
	}
	public void setAppActivity(String appActivity) {
		this.appActivity = appActivity;
	}
	//【店长直聘示例相关信息】
		/*package: name='com.hpbr.directhires' versionCode='401040' versionName='4.14' platformBuildVersionName='7.1.1'
		sdkVersion:'15'
		targetSdkVersion:'23'
		uses-permission: name='android.permission.RECEIVE_SMS'
		uses-permission: name='android.permission.READ_SMS'
		uses-permission: name='android.permission.ACCESS_COARSE_LOCATION'
		uses-permission: name='android.permission.ACCESS_FINE_LOCATION'
		uses-permission: name='android.permission.ACCESS_LOCATION_EXTRA_COMMANDS'
		uses-permission: name='android.permission.ACCESS_WIFI_STATE'
		uses-permission: name='android.permission.ACCESS_NETWORK_STATE'
		uses-permission: name='android.permission.CHANGE_WIFI_STATE'
		uses-permission: name='android.permission.READ_PHONE_STATE'
		uses-permission: name='android.permission.WRITE_EXTERNAL_STORAGE'
		uses-permission: name='android.permission.WRITE_SETTINGS'
		uses-permission: name='android.permission.READ_EXTERNAL_STORAGE'
		uses-permission: name='android.permission.INTERNET'
		uses-permission: name='android.permission.MOUNT_UNMOUNT_FILESYSTEMS'
		uses-permission: name='android.permission.READ_LOGS'
		uses-permission: name='android.permission.RECORD_AUDIO'
		uses-permission: name='android.permission.VIBRATE'
		uses-permission: name='android.permission.CAMERA'
		uses-permission: name='android.permission.UPDATE_APP_OPS_STATS'
		uses-permission: name='android.permission.SYSTEM_ALERT_WINDOW'
		uses-permission: name='android.permission.SYSTEM_OVERLAY_WINDOW'
		uses-permission: name='android.permission.CALL_PHONE'
		uses-permission: name='android.permission.SEND_SMS'
		uses-permission: name='android.permission.FLAG_GRANT_WRITE_URI_PERMISSION'
		uses-permission: name='android.permission.GET_TASKS'
		uses-permission: name='android.permission.MODIFY_AUDIO_SETTINGS'
		uses-permission: name='android.permission.BLUETOOTH_ADMIN'
		uses-permission: name='android.permission.BLUETOOTH'
		uses-permission: name='android.permission.RECEIVE_BOOT_COMPLETED'
		uses-permission: name='android.permission.WAKE_LOCK'
		uses-permission: name='com.sonyericsson.home.permission.BROADCAST_BADGE'
		uses-permission: name='android.permission.REORDER_TASKS'
		uses-permission: name='com.hpbr.directhires.permission.MIPUSH_RECEIVE'
		uses-permission: name='android.intent.action.MEDIA_MOUNTED'
		uses-permission: name='android.permission.RECORD_VIDEO'
		uses-permission: name='com.twl.mms.RECV_SH_DIRECTHIRES'
		application-label:'店长直聘'
		application-icon-120:'res/mipmap-hdpi-v4/logo.png'
		application-icon-160:'res/mipmap-hdpi-v4/logo.png'
		application-icon-240:'res/mipmap-hdpi-v4/logo.png'
		application-icon-320:'res/mipmap-xhdpi-v4/logo.png'
		application-icon-480:'res/mipmap-xxhdpi-v4/logo.png'
		application-icon-640:'res/mipmap-xxxhdpi-v4/logo.png'
		application-icon-65534:'res/mipmap-hdpi-v4/logo.png'
		application: label='店长直聘' icon='res/mipmap-hdpi-v4/logo.png'
		launchable-activity: name='com.hpbr.directhires.module.WelAct'  label='' icon=''
		feature-group: label=''
		  uses-feature-not-required: name='android.hardware.camera'
		  uses-feature-not-required: name='android.hardware.camera.autofocus'
		  uses-feature-not-required: name='android.hardware.camera.flash'
		  uses-feature-not-required: name='android.hardware.camera.front'
		  uses-feature-not-required: name='android.hardware.microphone'
		  uses-feature-not-required: name='android.hardware.screen.landscape'
		  uses-feature-not-required: name='android.hardware.wifi'
		  uses-feature: name='android.hardware.bluetooth'
		  uses-implied-feature: name='android.hardware.bluetooth' reason='requested android.permission.BLUETOOTH permission, requested android.permission.BLUETOOTH_ADMIN permission, and targetSdkVersion > 4'
		  uses-feature: name='android.hardware.faketouch'
		  uses-implied-feature: name='android.hardware.faketouch' reason='default feature for all apps'
		  uses-feature: name='android.hardware.location'
		  uses-implied-feature: name='android.hardware.location' reason='requested android.permission.ACCESS_COARSE_LOCATION permission, requested android.permission.ACCESS_FINE_LOCATION permission, and requested android.permission.ACCESS_LOCATION_EXTRA_COMMANDS permission'
		  uses-feature: name='android.hardware.screen.portrait'
		  uses-implied-feature: name='android.hardware.screen.portrait' reason='one or more activities have specified a portrait orientation'
		  uses-feature: name='android.hardware.telephony'
		  uses-implied-feature: name='android.hardware.telephony' reason='requested a telephony permission'
		main
		other-activities
		other-receivers
		other-services
		supports-screens: 'small' 'normal' 'large' 'xlarge'
		supports-any-density: 'true'
		locales: '--_--'
		densities: '120' '160' '240' '320' '480' '640' '65534'
		native-code: 'armeabi-v7a'*/

}
