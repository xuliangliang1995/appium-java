package com.grasswort.appium.executable;

import com.grasswort.appium.app.Apps;
import com.grasswort.appium.driver.DriverProxy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidElement;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class DZDP implements Run {

    private Logger logger = LoggerFactory.getLogger(DZDP.class);
    // 驱动代理
    private DriverProxy proxy = new DriverProxy(Apps.DZDP);
    private final String UPLOAD_URL = "http://qiye.jianzhibao.com:4007/customerResource/dzdp/send";
    private static OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(20, TimeUnit.SECONDS)//设置读取超时时间
            .writeTimeout(10,TimeUnit.SECONDS)//设置写的超时时间
            .connectTimeout(10,TimeUnit.SECONDS)//设置连接超时时间
            .build();

    @Override
    public void run() {
        // 美食
        final By CATE_BTN = By.id("com.dianping.v1:id/title");
        proxy.waitTargets(CATE_BTN);
        proxy.clickTextFromBy(CATE_BTN, "美食");
        // 筛选com.dianping.v1:id/txt_filter_title
        final By FILTRATE_BTN = By.id("com.dianping.v1:id/txt_filter_title");
        proxy.waitTargets(FILTRATE_BTN);
       /* proxy.clickTextFromBy(FILTRATE_BTN, "筛选");
        proxy.click(By.name("筛选"), false);*/

        AndroidElement filtrateBtn = proxy.findAll(FILTRATE_BTN).stream().filter(e -> Objects.equals(e.getText(), "筛选"))
                .findFirst().get();
        proxy.getInnnerDriver().get().tap(1, filtrateBtn.getCenter().getX(), filtrateBtn.getCenter().getY(), 300);
        // 新店
        final By NEW_SHOP_BTN = By.id("com.dianping.v1:id/filter_name");
        proxy.waitTargets(NEW_SHOP_BTN);
        proxy.clickTextFromBy(NEW_SHOP_BTN, "新店");
        // 确定
        final By CONFIRM_BTN = By.id("com.dianping.v1:id/confirm_btn");
        proxy.waitTargets(CONFIRM_BTN);
        proxy.clickTextFromBy(CONFIRM_BTN, "确定");

        String stopMarker = "";
        boolean isFirstPage = true;
        while (true) {
            final By SHOP_ITEM = By.id("com.dianping.v1:id/line_container");
            final By SHOP_NAME = By.id("com.dianping.v1:id/tv_shop_title");
            List<AndroidElement> eles = proxy.findAll(SHOP_ITEM);
            String marker = eles.stream().map(item -> item.findElement(SHOP_NAME).getText())
                    .reduce((a, b) -> a + "," + b).orElse("");
            if (Objects.equals(marker, stopMarker)) {
                break;
            }
            if (isFirstPage) {
                // ignore
            } else {
                // 去头去尾(因为可以确保超过3个以上所以不做索引判断)
                eles.remove(0);
                eles.remove(1);
                eles.remove(eles.size() - 1);
            }
            isFirstPage = false;
            for (AndroidElement e : eles) {
                final String SHOP = e.findElement(SHOP_NAME).getText();
                final By COOKING_STYLE_HANDLE = By.id("com.dianping.v1:id/category_name");
                boolean bo = e.findElements(COOKING_STYLE_HANDLE).size() > 0;
                final String COOKING_STYLE = bo ? e.findElement(COOKING_STYLE_HANDLE).getText() : "";

                e.findElement(SHOP_NAME).click();
                int i = 0;
                while (proxy.exists(SHOP_ITEM)) {
                    i ++;
                    if (i == 3) {
                        continue;
                    }
                    e.findElement(SHOP_NAME).click();
                }

                final By PHONE_ICON = By.id("com.dianping.v1:id/phone_icon");
                proxy.swipToFind(PHONE_ICON, By.name("推荐菜"));
                boolean exists = proxy.exists(PHONE_ICON);
                if (exists) {
                    AndroidElement ae = proxy.find(PHONE_ICON).get();
                    proxy.getInnnerDriver().get().tap(1, ae.getCenter().getX(), ae.getCenter().getY(), 300);
                    final By MULTI_PHONE = By.id("android:id/text1");

                    String dialPhone = getDianxinDialPhone();
                    if (StringUtils.isBlank(dialPhone)) {
                        dialPhone = proxy.findAll(MULTI_PHONE).stream().map(AndroidElement::getText).filter(phone -> isValidPhone(phone))
                                .findFirst().orElse("");
                    }
                    final String PHONE = dialPhone;

                    proxy.back();

                    final By CONSUMPTION_HANDLE = By.id("com.dianping.v1:id/price_avg");
                    final String CONSUMPTION = proxy.exists(CONSUMPTION_HANDLE)
                            ? proxy.getText(By.id("com.dianping.v1:id/price_avg"))
                            : "--";

                    final String POSITION = proxy.getText(By.id("com.dianping.v1:id/address_title"))
                            + "(" + proxy.getText(By.id("com.dianping.v1:id/address_subtitle")) + ")";


                    logger.info("店铺名称:".concat(SHOP));
                    logger.info("电话号码:".concat(PHONE));
                    logger.info("人均消费:".concat(CONSUMPTION));
                    logger.info("店铺地址:".concat(POSITION));
                    logger.info("菜系:".concat(COOKING_STYLE));

                    if (StringUtils.isNotBlank(PHONE)) {
                        this.upload(SHOP, POSITION, PHONE, COOKING_STYLE, CONSUMPTION);
                    }

                }
                proxy.clickTarget(By.id("com.dianping.v1:id/title_background_back"), SHOP_ITEM);
            }

            proxy.swipeToUp();
        }
        proxy.forceWait(10);
        proxy.quit();

    }

    /**
     * 上传到 CRM 系统中
     */
    private void upload(String shopName, String shopPosition, String phone, String cookingStyle, String consumption) {
        FormBody body = new FormBody.Builder()
                .add("shop_name", shopName)
                .add("shop_position", shopPosition)
                .add("phone", phone)
                .add("cooking_style", cookingStyle)
                .add("consumption_per_person", consumption)
                .build();
        Request request = new Request.Builder()
                .url(UPLOAD_URL)
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            logger.info("上传数据结果：{}", response.toString());
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断手机号是否合法
     * @param phone
     * @return
     */
    private boolean isValidPhone(String phone) {
        System.out.println("==" + phone + "==");
        return StringUtils.isNotBlank(phone)
                && phone.length() == 11
                && phone.substring(0, 1).equals("1");
    }

    /**
     * 获取点心拨号手机号
     * @return
     */
    private String getDianxinDialPhone() {
        if (proxy.exists(By.id("com.dianxinos.dxbb:id/embedded_autofit_text"))) {
            String phone = proxy.getText(By.id("com.dianxinos.dxbb:id/embedded_autofit_text"));
            System.out.println("读取到的电话：" + phone);
            return isValidPhone(phone) ? phone : "";
        }
        return "";
    }
}
