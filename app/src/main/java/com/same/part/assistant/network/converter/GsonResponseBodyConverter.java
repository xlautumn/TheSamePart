package com.same.part.assistant.network.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;

public class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private Gson gson;
    private Type type;

    public GsonResponseBodyConverter(Gson gson, Type type) {
        this.gson = gson;
        this.type = type;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        String response = value.string();
        try {
            GsonResponse gsonResponse = gson.fromJson(response, GsonResponse.class);
            JSONObject jsonObject = JSON.parseObject(response);
            if (gsonResponse.isSucces()) {
                jsonObject.put("data", gsonResponse.getMsg());
            } else {
                jsonObject.put("errorMsg", gsonResponse.getMsg().toString());
            }
            response = JSONObject.toJSONString(jsonObject);
            return gson.fromJson(response, type);
        } finally {
            value.close();
        }
    }

}
