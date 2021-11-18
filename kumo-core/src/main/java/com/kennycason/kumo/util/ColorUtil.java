package com.kennycason.kumo.util;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author created by xkazxx
 * @version v0.0.1
 * description: com.kennycason.kumo.util
 * date:2021/11/18
 */
public class ColorUtil {

    /**
     * 生成随机total个颜色
     *
     * @param total 随机颜色数量
     * @return
     */
    public static List<java.awt.Color> getRandomColor(int total) {
        String red;
        String green;
        String blue;
        Random random = new Random();
        List<Color> colorList = new ArrayList<>(total > 0 ? total : 16);
        for (int i = 0; i < total; i++) {
            red = Integer.toHexString(random.nextInt(256)).toUpperCase(); //生成红色颜色代码
            green = Integer.toHexString(random.nextInt(256)).toUpperCase(); //生成绿色颜色代码
            blue = Integer.toHexString(random.nextInt(256)).toUpperCase(); //生成蓝色颜色代码
            red = red.length() == 1 ? "0" + red : red; //判断红色代码的位数
            green = green.length() == 1 ? "0" + green : green; //判断绿色代码的位数
            //判断蓝色代码的位数
            blue = blue.length() == 1 ? "0" + blue : blue; //生成十六进制颜色值
            String color = red + green + blue;
            colorList.add(new Color(Integer.parseInt(color, 16)));
        }
        return colorList;
    }

}
