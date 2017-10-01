package com.davidinchina.showcode.readingview.net;

import com.davidinchina.showcode.readingview.utils.LogUtil;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * author:davidinchina on 2017/7/24 10:42
 * email:davicdinchina@gmail.com
 * tip:
 */
public class LocalGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    LocalGsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        ResponseBody newBody = ResponseBody.create(value.contentType(), value.string());
        String responseString = newBody.string();
        LogUtil.error("返回数据：" + responseString);
        JSONObject result = new JSONObject();
        try {
            result = new JSONObject(responseString);
            if (null != result.getString("data")) {
                String data = result.getString("data");
                if (!"".equals(data)) {
                    if (data.startsWith("[")) {
                        String list = "{\"list\":" + data + "}";
                        result.putOpt("data", new JSONObject(list));//重新封装数据
                    }
                } else {
                    data = "{}";
                    result.putOpt("data", new JSONObject(data));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonReader jsonReader = gson.newJsonReader(ResponseBody.create(newBody.contentType(),
                result.toString()).charStream());
        try {
            return adapter.read(jsonReader);
        } finally {
            value.close();
        }
    }
}
