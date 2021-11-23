package com.kennycason.kumo.provider.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.font.FontWeight;
import com.kennycason.kumo.font.KumoFont;
import com.kennycason.kumo.font.scale.SqrtFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.nlp.tokenizers.ChineseWordTokenizer;
import com.kennycason.kumo.palette.ColorPalette;
import com.kennycason.kumo.provider.service.CircleChineseWordCloudService;
import com.kennycason.kumo.provider.vo.CircleWordCloudRequestVo;
import com.kennycason.kumo.util.ColorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.List;

/**
 * @author created by xkazxx
 * @version v0.0.1
 * description: com.kennycason.kumo.provider.controller
 * date:2021/11/19
 */
@Service
public class CircleChineseWordCloudServiceImpl implements CircleChineseWordCloudService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CircleChineseWordCloudServiceImpl.class);

    private static final String CHINESE_DEFAULT_FONT = "宋体";
    private static final int WORD_LIMIT = Integer.MAX_VALUE; // 词数限制，默认不限制
    private static final int RADIUS = 400;

    @Override
    public String convertToWorldCloud(CircleWordCloudRequestVo wordCloudRequestVo) {
        List<WordFrequency> wordFrequencies = wordCloudRequestVo.getWordFrequencies();
        final FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
        frequencyAnalyzer.setWordFrequenciesToReturn(WORD_LIMIT);
        frequencyAnalyzer.setMinWordLength(2);
        frequencyAnalyzer.setWordTokenizer(new ChineseWordTokenizer());

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
        String tempPath = FileUtil.getTmpDirPath() +  UUID.randomUUID().toString(true) + ".png";
        wordCloud.writeToFile(tempPath);
        return tempPath;
    }

}

