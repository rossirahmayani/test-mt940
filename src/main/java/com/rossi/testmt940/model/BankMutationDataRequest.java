package com.rossi.testmt940.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
public class BankMutationDataRequest {
    private String bankAccountNumber;
    private String bankName;
    private String periodDate;
    private BigDecimal initialBalance;
    private String balanceDate;
    private List<MutationData> mutations;
}
