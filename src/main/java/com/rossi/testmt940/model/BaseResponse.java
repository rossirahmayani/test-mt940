package com.rossi.testmt940.model;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class BaseResponse {
    private String responseCode;
    private String responseMessage;
    private Object responseData;
}
