package com.davidinchina.showcode.net;


import rx.functions.Func1;


public class HttpEmptyResultFunc implements Func1<JsonEntity, JsonEntity> {
    @Override
    public JsonEntity call(JsonEntity tJsonEntity) {
        if (tJsonEntity.getStatus_code() != 0) {
            throw new ApiException(tJsonEntity.getStatus_code(), tJsonEntity.getMsg());
        }
        return tJsonEntity;
    }
}
