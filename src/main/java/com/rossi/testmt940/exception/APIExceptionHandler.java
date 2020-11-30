package com.rossi.testmt940.exception;

import com.rossi.testmt940.enums.ResponseCode;
import com.rossi.testmt940.model.BaseResponse;
import com.rossi.testmt940.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;

@Slf4j
@ControllerAdvice
public class APIExceptionHandler {
    @Qualifier("defaultValidator")
    @Autowired
    private Validator validator;

    @Autowired
    private CommonUtil commonUtil;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(validator);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<BaseResponse> handleValidationException(ValidationException ex){
        log.warn("Validation API exception caught");
        BaseResponse response = BaseResponse.builder()
                .responseCode(ex.getResponseCode().getCode())
                .responseMessage(ex.getResponseCode().getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<BaseResponse> unknownException(Exception ex) {
        log.error("Handle Unkown Exception => {} ", ex);
        BaseResponse response = BaseResponse.builder()
                .responseCode(ResponseCode.ERROR_UNKNOWN.getCode())
                .responseMessage(ResponseCode.ERROR_UNKNOWN.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<BaseResponse> handleBindException(BindException ex) {
        log.warn("Handle BindException ...");
        String responseCode = commonUtil.getSingleErrorMessage(ex.getFieldErrors(), ex.getGlobalErrors());
        BaseResponse response = BaseResponse.builder().build();
        response.setResponseCode(responseCode);
        response.setResponseMessage(ResponseCode.byCode(responseCode).getMessage());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}

