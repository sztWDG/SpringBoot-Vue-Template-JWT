package com.example.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.example.entity.vo.request.WeatherVO;
import com.example.service.WeatherService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Service
public class WeatherImpl implements WeatherService {
    @Resource
    RestTemplate rest;

    @Resource
    StringRedisTemplate template;

    @Value("${spring.weather.key}")
    String weatherKey;

    //通过所在城市的经纬度来确定天气信息
    @Override
    public WeatherVO fetchWeather(double longitude, double latitude) {
        return fetchFromCache(longitude, latitude);
    }

    /*
    设置一个数据缓存，避免用户每次进入都要申请一次api，太费钱了。


    */
    private WeatherVO fetchFromCache(double longitude, double latitude) {
        JSONObject geo = this.decompressStringToJson(rest.getForObject(
                "https://geoapi.qweather.com/v2/city/lookup?location="
                        + longitude + "," + latitude + "&key=" + weatherKey, byte[].class));

        //location为空
        if (geo == null) return null;
        //这边location需要复用，所以以上的location转为geo。
        //注意：这边获取的json是个数组形式，虽然只有一组数据，但是也得getJSONArray，并且选择index为0的数据
        JSONObject location = geo.getJSONArray("location").getJSONObject(0);
        int id = location.getInteger("id");
        String key = "weather:" + id;
        String cache = template.opsForValue().get(key);

        if (cache != null) return JSONObject.parseObject(cache).to(WeatherVO.class);
        WeatherVO vo = this.fetchFromAPI(id, location);
        if (vo == null) return null;
        template.opsForValue().set(key, JSONObject.from(vo).toJSONString(), 1, TimeUnit.HOURS);
        return vo;

    }

    //从api中获取天气
    private WeatherVO fetchFromAPI(int id, JSONObject location) {
        WeatherVO vo = new WeatherVO();
        vo.setLocation(location);

        //实时天气
        JSONObject now = this.decompressStringToJson(rest.getForObject(
                "https://devapi.qweather.com/v7/weather/now?location="
                        + id + "&key=" + weatherKey, byte[].class));
        if (now == null) return null;
        //注意要提取json中的now
        vo.setNow(now.getJSONObject("now"));

        //24小时预测天气
        JSONObject hourly = this.decompressStringToJson(rest.getForObject(
                "https://devapi.qweather.com/v7/weather/24h?location="
                        + id + "&key=" + weatherKey, byte[].class));
        if (hourly == null) return null;
        //注意要提取jsonArray中的hourly，限制获取前五个数据
        vo.setHourly(new JSONArray(hourly.getJSONArray("hourly").stream().limit(5).toList()));

        return vo;
    }


    //解压Gzip，这里难点，很陌生
    private JSONObject decompressStringToJson(byte[] data) {
        //输出流
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(data));
            byte[] buffer = new byte[1024];
            int read;
            //判断有数据就继续读
            while ((read = gzip.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            gzip.close();
            outputStream.close();
            return JSONObject.parseObject(outputStream.toString());
        } catch (IOException e) {
            return null;
        }
    }
}
