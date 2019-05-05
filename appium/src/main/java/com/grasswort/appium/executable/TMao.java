package com.grasswort.appium.executable;

import com.alibaba.fastjson.JSONObject;
import com.grasswort.appium.app.Apps;
import com.grasswort.appium.driver.DriverProxy;
import io.appium.java_client.android.AndroidElement;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TMao implements Run{

    private Logger logger = LoggerFactory.getLogger(TMao.class);
    // 驱动代理
    private DriverProxy proxy = new DriverProxy(Apps.TMALL);

    private static OkHttpClient client =new OkHttpClient.Builder()
            .readTimeout(20, TimeUnit.SECONDS)//设置读取超时时间
            .writeTimeout(10,TimeUnit.SECONDS)//设置写的超时时间
            .connectTimeout(10,TimeUnit.SECONDS)//设置连接超时时间
            .build();

    private static List<Light> lights = new ArrayList<>();
    private static Map<String, String> base64Map = new HashMap<>();
    /**
     * 调试场景
     * @throws Exception
     */
    public void init() throws Exception {
        proxy.forceWait(40);

        /*lights.add(new Light(1,985, 477, proxy));
        lights.add(new Light(2,985, 765, proxy));
        lights.add(new Light(3,985, 1045, proxy));
        lights.add(new Light(4,985, 1350, proxy));*/
        lights.add(new Light(1,652, 327, proxy));
        lights.add(new Light(2,652, 513, proxy));
        lights.add(new Light(3,652, 708, proxy));
        lights.add(new Light(4,652, 897, proxy));
        lights.add(new Light(5,652, 1080, proxy));

        File sceneMapJson = new File("D:/tmp/scenceMap.json");
        if (sceneMapJson.exists()) {
            InputStream is = new FileInputStream(sceneMapJson);
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            String sceneJson = new String(bytes);
           base64Map = (Map)JSONObject.parseObject(sceneJson);
           return;
        }

        List<String> scene = new ArrayList<>(32);
        scene.add("1,0,0,0,0");
        scene.add("0,1,0,0,0");
        scene.add("1,1,0,0,0");
        scene.add("0,0,1,0,0");
        scene.add("1,0,1,0,0");
        scene.add("0,1,1,0,0");
        scene.add("1,1,1,0,0");
        scene.add("0,0,0,1,0");
        scene.add("1,0,0,1,0");
        scene.add("0,1,0,1,0");
        scene.add("1,1,0,1,0");
        scene.add("0,0,1,1,0");
        scene.add("1,0,1,1,0");
        scene.add("0,1,1,1,0");
        scene.add("1,1,1,1,0");
        scene.add("0,0,0,0,0");
        scene.add("1,0,0,0,1");
        scene.add("0,1,0,0,1");
        scene.add("1,1,0,0,1");
        scene.add("0,0,1,0,1");
        scene.add("1,0,1,0,1");
        scene.add("0,1,1,0,1");
        scene.add("1,1,1,0,1");
        scene.add("0,0,0,1,1");
        scene.add("1,0,0,1,1");
        scene.add("0,1,0,1,1");
        scene.add("1,1,0,1,1");
        scene.add("0,0,1,1,1");
        scene.add("1,0,1,1,1");
        scene.add("0,1,1,1,1");
        scene.add("1,1,1,1,1");
        scene.add("0,0,0,0,1");


        for (String s: scene) {
            logger.info("调试场景：" + s);
            String[] lightStatus = s.split(",");
            for (int i = 0; i < lightStatus.length; i++) {
                boolean lighting = "1".equals(lightStatus[i]);
                lights.get(i).control(lighting);
            }

            String shotBase64 = this.generateShotPicBase64();
            logger.info("场景快照" + shotBase64.substring(0, 18) + "...");
            base64Map.put(shotBase64, s);
        }

        String sceneJson = JSONObject.toJSONString(base64Map);
        OutputStream os = new FileOutputStream(sceneMapJson);
        os.write(sceneJson.getBytes());
        os.close();
        logger.info("调试完毕");
    }

    @Override
    public void run() {
        try {
            this.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
        while (true) {
            this.updateLightStatus();
        }
    }

    /**
     * 灯
     */
    public class Light {
        private int lightNo;
        private int toggleCoordsX;
        private int toggleCoordsY;
        private boolean lighting;
        private DriverProxy driverProxy;

        private Light(int lightNo, int toggleCoordsX, int toggleCoordsY, DriverProxy driverProxy) {
            this.lightNo = lightNo;
            this.toggleCoordsX = toggleCoordsX;
            this.toggleCoordsY = toggleCoordsY;
            this.lighting = false;
            this.driverProxy = driverProxy;
        }

        private void holder(boolean light) {
            this.lighting = light;
            logger.info(lightNo + "号灯===" + (lighting ? "点亮" : "熄灭"));
        }

        private void control(boolean light) {
            if (light && lighting) {
                return;
            }
            if (! light && ! lighting) {
                return;
            }
            this.lighting = light;
            logger.info(lightNo + "号灯==>" + (lighting ? "点亮" : "熄灭"));
            driverProxy.getInnnerDriver().get().tap(1, toggleCoordsX, toggleCoordsY, 1000);
            driverProxy.forceWait(5);
        }
    }


    /**
     * 获取远程灯的状态
     * @return
     */
    public String getLightStatus() {
        Request request = new Request.Builder()
                .url("http://qiye.jianzhibao.com:4007/tmall/light/status")
                .get()
                .build();
        try {
            Response response = client.newCall(request).execute();
            JSONObject result = JSONObject.parseObject(response.body().string());
            String lightStatus = result.getString("data");
            logger.info("远程获取照明状态：{}",lightStatus);
            response.close();
            return lightStatus;
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("远程获取照明状态失败：{}", "0,0,0,0,0");
        return "0,0,0,0";
    }

    /**
     * 生成快照Base64
     * @return
     * @throws Exception
     */
    public String generateShotPicBase64() throws Exception{
        // 生成全局图片
        File file = proxy.getInnnerDriver().get().getScreenshotAs(OutputType.FILE);
        By img = By.name("灯");
        File savePic = new File("D:/tmp/shot.png");
        if (savePic.exists()) {
            savePic.delete();
        }
        FileUtils.copyFile(file, savePic);
        BufferedImage shotPic = ImageIO.read(file);

        if (null == subImg) {
            // 生成局部图片
            AndroidElement e = proxy.find(img).get();
            Point picPoint = e.getLocation();
            subImg = new SubImg();
            subImg.x = 0;
            subImg.y = picPoint.getY();
            subImg.w = shotPic.getWidth();
            subImg.h = shotPic.getHeight() - picPoint.getY();
        }

        BufferedImage subPic = shotPic.getSubimage(subImg.x, subImg.y, subImg.w, subImg.h);

        File p = new File(String.format("D:/tmp/tmp.png"));
        if(p.exists()){
            p.delete();
        }
        ImageIO.write(subPic, "PNG", p);
        String base64 = encodeBase64File(p);
        savePic.delete();
        p.delete();
        return base64;
    }

    /**
     * 解析当前灯状态
     */
    private void resolverCurrentStatus() {
        try {
            String base64 = this.generateShotPicBase64();
            String scene = base64Map.get(base64);
            String[] lightStatus = scene.split(",");
            for (int i = 0; i < lightStatus.length; i++) {
                lights.get(i).holder("1".equals(lightStatus[i]));
            }
        } catch (Exception e) {
            e.printStackTrace();
            //proxy.quit();
        }
        proxy.getInnnerDriver().get().tap(1, 566, 192, 300);
        proxy.forceWait(1);
    }

    /**
     * 更新灯状态
     */
    public void updateLightStatus() {
        this.resolverCurrentStatus();
        String scene = this.getLightStatus();
        String[] lightStatus = scene.split(",");
        for (int i = 0; i < lightStatus.length; i++) {
            lights.get(i).control("1".equals(lightStatus[i]));
        }
    }

    private String encodeBase64File(File file) throws Exception {
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int)file.length()];
        inputFile.read(buffer);
        inputFile.close();
        return new String(Base64.encodeBase64(buffer));
    }

    private static SubImg subImg;
    public class SubImg {
        int x,y,w,h;
    }
}
