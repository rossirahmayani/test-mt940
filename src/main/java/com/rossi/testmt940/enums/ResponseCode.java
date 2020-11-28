package com.rossi.testmt940.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

@AllArgsConstructor
@Getter
public enum ResponseCode {
    SUCCESS("0000", "Success"),
    PARAMETER_NOT_COMPLETE("0001", "Parameter not Complete"),
    ERROR_UNKNOWN("9999", "Error Unknown");

    private String code;
    private String message;

    public static ResponseCode byCode(String code) {
        Predicate<ResponseCode> isEqual = responseCode -> Optional.ofNullable(responseCode)
                .map(ResponseCode::getCode)
                .map(code::equals)
                .orElse(false);
        return Stream.of(values())
                .filter(isEqual)
                .findAny()
                .orElse(null);
    }
}
