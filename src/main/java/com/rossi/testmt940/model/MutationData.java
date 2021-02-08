package com.rossi.testmt940.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class MutationData {
    private String mutationDate;
    private String mutationType;
    private BigDecimal amount;
    private String description;
}
