package com.rossi.testmt940.util;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class CommonUtil {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private Utils utils;
    
    public String getSingleErrorMessage(List<FieldError> errorFields, List<ObjectError> getGlobalErrors) {
        return Optional.ofNullable(getErrorFieldsMessage(errorFields))
                .orElse(getGlobalErrorsMessage(getGlobalErrors));
    }

    private String getErrorFieldsMessage(List<FieldError> errorFields) {
        return Optional.ofNullable(errorFields)
                .filter(list -> utils.isNotEmpty(list))
                .map(listErrorFields -> listErrorFields.stream()
                        .findFirst()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .orElse(null)
                ).orElse(null);
    }


    private String getGlobalErrorsMessage(List<ObjectError> globalErrors) {
        return Optional.ofNullable(globalErrors)
                .filter(list -> utils.isNotEmpty(list))
                .map(listGlobalErrors -> listGlobalErrors.stream()
                        .findFirst()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .orElse(null)
                ).orElse(null);
    }
}
