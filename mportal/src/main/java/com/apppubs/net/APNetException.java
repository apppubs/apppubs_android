package com.apppubs.net;

import com.apppubs.constant.APError;

import java.io.IOException;

/**
 * Created by siger on 2018/4/19.
 */

public class APNetException extends IOException {

    private APError error;

    public APNetException(APError error){
        this.error = error;
    }
}
