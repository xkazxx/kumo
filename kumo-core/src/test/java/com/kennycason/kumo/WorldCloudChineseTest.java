package com.kennycason.kumo;

import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.font.FontWeight;
import com.kennycason.kumo.font.KumoFont;
import com.kennycason.kumo.font.scale.SqrtFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.nlp.tokenizers.ChineseWordTokenizer;
import com.kennycason.kumo.palette.ColorPalette;
import com.kennycason.kumo.util.ColorUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


/**
 * @author created by xkazxx
 * @version v0.0.1
 * description: com.kennycason.kumo
 * date:2021/11/18
 */
public class WorldCloudChineseTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorldCloudChineseTest.class);

    private static final String CHINESE_DEFAULT_FONT = "宋体";
    private static final int WORD_LIMIT = Integer.MAX_VALUE; // 词数限制，默认不限制
    private static final int RADIUS = 400;

    @Test
    public void chineseCircle() throws IOException {
        final FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
        frequencyAnalyzer.setWordFrequenciesToReturn(600);
        frequencyAnalyzer.setMinWordLength(2);
        frequencyAnalyzer.setWordTokenizer(new ChineseWordTokenizer());

        final List<WordFrequency> wordFrequencies = frequencyAnalyzer.load(getInputStream("text/chinese_dragon.txt"));
        final Dimension dimension = new Dimension(RADIUS * 2, RADIUS * 2);
        final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
        wordCloud.setPadding(2);
        wordCloud.setKumoFont(new KumoFont(CHINESE_DEFAULT_FONT, FontWeight.PLAIN)); // 解决中文乱码
        wordCloud.setBackground(new CircleBackground(RADIUS));
        wordCloud.setBackgroundColor(Color.white); // 设置背景颜色
        wordCloud.setColorPalette(new ColorPalette(new Color(0xD5CFFA), new Color(0xBBB1FA), new Color(0x9A8CF5), new Color(0x806EF5)));
        wordCloud.setFontScalar(new SqrtFontScalar(12, 45));
        final long startTime = System.currentTimeMillis();
        wordCloud.build(wordFrequencies);
        LOGGER.info("Took " + (System.currentTimeMillis() - startTime) + "ms to build");
        wordCloud.writeToFile("output_test/chinese_language_circle.png");
    }

    @Test
    public void chineseCircleGb312() throws IOException {
        final FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
        frequencyAnalyzer.setWordFrequenciesToReturn(WORD_LIMIT);
        frequencyAnalyzer.setMinWordLength(2);
        frequencyAnalyzer.setWordTokenizer(new ChineseWordTokenizer());

        final List<WordFrequency> wordFrequencies = frequencyAnalyzer.load(getInputStream("text/gb2312.txt"), "gb2312");
        final Dimension dimension = new Dimension(RADIUS * 2, RADIUS * 2);
        final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
        wordCloud.setPadding(2);
        wordCloud.setKumoFont(new KumoFont(CHINESE_DEFAULT_FONT, FontWeight.PLAIN)); // 解决中文乱码
        wordCloud.setBackground(new CircleBackground(RADIUS)); // 设置背景轮廓
        wordCloud.setBackgroundColor(Color.white); // 设置背景颜色
        wordCloud.setColorPalette(new ColorPalette(new Color(0xD5CFFA), new Color(0xBBB1FA), new Color(0x9A8CF5), new Color(0x806EF5)));
        wordCloud.setFontScalar(new SqrtFontScalar(12, 45));
        final long startTime = System.currentTimeMillis();
        wordCloud.build(wordFrequencies);
        LOGGER.info("Took " + (System.currentTimeMillis() - startTime) + "ms to build");
        wordCloud.writeToFile("output_test/chinese_language_circle_gb2312.png");
    }

    @Test
    public void chineseCircleFont() throws IOException {
        final FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
        frequencyAnalyzer.setWordFrequenciesToReturn(WORD_LIMIT);
        frequencyAnalyzer.setMinWordLength(2);
        frequencyAnalyzer.setWordTokenizer(new ChineseWordTokenizer());

        final List<WordFrequency> wordFrequencies = frequencyAnalyzer.load(getInputStream("text/chinese_dragon.txt"));
        final Dimension dimension = new Dimension(RADIUS * 2, RADIUS * 2);
        final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
        wordCloud.setPadding(2);
        wordCloud.setKumoFont(new KumoFont(CHINESE_DEFAULT_FONT, FontWeight.PLAIN)); // 解决中文乱码
        wordCloud.setBackground(new CircleBackground(RADIUS));
        wordCloud.setBackgroundColor(Color.white); // 设置背景颜色
        wordCloud.setColorPalette(new ColorPalette(ColorUtil.getRandomColor(100)));
        wordCloud.setFontScalar(new SqrtFontScalar(12, 45));
        final long startTime = System.currentTimeMillis();
        wordCloud.build(wordFrequencies);
        LOGGER.info("Took " + (System.currentTimeMillis() - startTime) + "ms to build");
        wordCloud.writeToFile("output_test/chinese_language_circle_font.png");
    }


    private static InputStream getInputStream(final String path) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
    }
}
