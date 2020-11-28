package com.rossi.testmt940.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

@AllArgsConstructor
@Getter
public enum Bank {
    MANDIRI("008", "BANK MANDIRI"),
    BSM("451", "BANK SYARIAH MANDIRI"),
    BNI("009", "BANK NEGARA INDONESIA"),
    BRI("002", "BANK RAKYAT INDONESIA");

    private String code;
    private String name;

    public static Bank byCode(String code) {
        Predicate<Bank> isEqual = bank -> Optional.ofNullable(bank)
                .map(Bank::getCode)
                .map(code::equals)
                .orElse(false);
        return Stream.of(values())
                .filter(isEqual)
                .findAny()
                .orElse(null);
    }

}
