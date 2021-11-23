package com.kennycason.kumo.provider.vo;

import com.kennycason.kumo.WordFrequency;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author created by xkazxx
 * @version v0.0.1
 * description: com.kennycason.kumo.provider.vo
 * date:2021/11/19
 */
@Data

public class CircleWordCloudRequestVo {

    private List<WordFrequency> wordFrequencies;
    private String fileName;

}
