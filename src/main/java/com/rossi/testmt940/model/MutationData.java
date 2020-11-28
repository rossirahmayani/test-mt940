package com.rossi.testmt940.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MutationData {
    private String mutationDate;
    private String mutationType;
    private String amount;
    private String description;
}
