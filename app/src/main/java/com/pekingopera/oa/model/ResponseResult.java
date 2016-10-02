package com.pekingopera.oa.model;

import java.util.List;

/**
 * Created by wayne on 10/2/2016.
 */

public class ResponseResult<T> {
    private List<T> list;

    private ResponseBase error;

    public List<T> getList() {
        return list;
    }

    public ResponseBase getError() {
        return error;
    }
}
