package com.rossi.testmt940.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@Builder
public class BankMutationResponse {
    private String periodDate;
    private String bankAccNum;
    private BigDecimal initialBalance;
    private Integer totalMutations;
    private BigDecimal totalCredit;
    private BigDecimal totalDebit;
    private BigDecimal countedLastBalance;
    private BigDecimal reportedLastBalance;
    private List<MutationData> mutations;
}
