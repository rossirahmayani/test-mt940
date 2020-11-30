package com.rossi.testmt940.util;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Utils {
    public boolean isNotEmpty(List value){
        return !value.isEmpty();
    }

    public boolean isNotEmpty(Object[] value){
        return value.length > 0;
    }

}
