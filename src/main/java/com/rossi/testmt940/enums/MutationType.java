package com.rossi.testmt940.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

@AllArgsConstructor
@Getter
public enum MutationType {

    CREDIT("C", "Credit"),
    DEBIT("D", "Debit");

    private String code;
    private String description;

    public static MutationType byCode(String code) {
        Predicate<MutationType> isEqual = type -> Optional.ofNullable(type)
                .map(MutationType::getCode)
                .map(code::equals)
                .orElse(false);
        return Stream.of(values())
                .filter(isEqual)
                .findAny()
                .orElse(null);
    }

    public static MutationType byDescription(String description) {
        Predicate<MutationType> isEqual = type -> Optional.ofNullable(type)
                .map(MutationType::getDescription)
                .map(description::equals)
                .orElse(false);
        return Stream.of(values())
                .filter(isEqual)
                .findAny()
                .orElse(null);
    }
}
