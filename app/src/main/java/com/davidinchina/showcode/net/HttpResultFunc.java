package com.davidinchina.showcode.net;



import rx.functions.Func1;

public class HttpResultFunc<T> implements Func1<JsonEntity<T>, T> {
    @Override
    public T call(JsonEntity<T> tJsonEntity) {
        if (tJsonEntity.getStatus_code() != 0) {//0为成功
            throw new ApiException(tJsonEntity.getStatus_code(), tJsonEntity.getMsg());
        }
        return tJsonEntity.getData();
    }
}
