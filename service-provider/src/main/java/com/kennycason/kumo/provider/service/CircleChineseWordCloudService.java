package com.kennycason.kumo.provider.service;

import com.kennycason.kumo.provider.vo.CircleWordCloudRequestVo;

/**
 * @author created by xkazxx
 * @version v0.0.1
 * description: com.kennycason.kumo.provider.service
 * date:2021/11/19
 */
public interface CircleChineseWordCloudService {
    String convertToWorldCloud(CircleWordCloudRequestVo wordCloudRequestVo);
}
