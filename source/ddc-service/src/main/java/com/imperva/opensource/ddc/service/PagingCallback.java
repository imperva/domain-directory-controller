package com.imperva.opensource.ddc.service;

import com.imperva.opensource.ddc.core.query.EntityResponse;

import java.util.List;

/**
 * Created by gabi.beyo on 4/30/2017.
 */
public interface PagingCallback {
    boolean callback(List<EntityResponse> data, PagingCallbackContext pagingCallbackContext);
}
