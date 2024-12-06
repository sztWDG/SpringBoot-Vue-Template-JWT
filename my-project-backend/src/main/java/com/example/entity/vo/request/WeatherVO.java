package com.example.entity.vo.request;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

@Data
public class WeatherVO {
    //位置信息，实时天气预报，未来预报
    JSONObject location;
    JSONObject now;
    JSONArray hourly;
}
