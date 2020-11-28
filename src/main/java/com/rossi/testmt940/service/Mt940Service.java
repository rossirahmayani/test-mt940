package com.rossi.testmt940.service;

import com.prowidesoftware.swift.io.parser.SwiftParser;
import com.prowidesoftware.swift.model.*;
import com.prowidesoftware.swift.model.field.*;
import com.prowidesoftware.swift.model.mt.mt9xx.MT940;
import com.rossi.testmt940.enums.MutationType;
import com.rossi.testmt940.enums.ResponseCode;
import com.rossi.testmt940.model.BankMutationDataRequest;
import com.rossi.testmt940.model.BaseResponse;
import com.rossi.testmt940.model.MutationData;
import com.rossi.testmt940.util.DateUtil;
import com.rossi.testmt940.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Optional;

@Service
@Slf4j
public class Mt940Service {

    @Autowired
    DateUtil dateUtil;

    @Autowired
    JsonUtil jsonUtil;

    private static final String FORMAT_DATE1 = "yyyy-MM-dd";
    private static final String FORMAT_DATE2 = "yyyyMMddHHmmss";
    private static final String FORMAT_DATE3 = "yyMMdd";

    public BaseResponse parse(String mtString) throws IOException {
        SwiftParser parser = new SwiftParser();
        parser.setReader(new StringReader(mtString));
        SwiftMessage message = parser.message();
        MT940 mt940 = new MT940(message);
        String result = mt940.toJson();

        return BaseResponse.builder()
                .responseCode(ResponseCode.SUCCESS.getCode())
                .responseMessage(ResponseCode.SUCCESS.getMessage())
                .responseData(jsonUtil.convertJson(result, HashMap.class))
                .build();
    }

    public BaseResponse generate(BankMutationDataRequest request){
        SwiftMessage message = new SwiftMessage();
        message.setBlock1(generateBlock1(request.getBankName()));
        message.setBlock2(generateBlock2());
        message.setBlock4(generateBlock4(request));

        MT940 mt940 = new MT940(message);

        return BaseResponse.builder()
                .responseCode(ResponseCode.SUCCESS.getCode())
                .responseMessage(ResponseCode.SUCCESS.getMessage())
                .responseData(mt940.message())
                .build();
    }

    private BigDecimal getAmount (MutationType mutationType, BigDecimal amount){
        return Optional.ofNullable(mutationType)
                .filter(m -> m.equals(MutationType.DEBIT))
                .map(mt -> amount.negate())
                .orElseGet(() -> amount);
    }

    private SwiftBlock1 generateBlock1(String bankName){
        SwiftBlock1 block1 = new SwiftBlock1();
        block1.setApplicationId("F");
        block1.setServiceId("01");
        block1.setLogicalTerminal(bankName);
        block1.setSessionNumber("");
        block1.setSequenceNumber("");

        return block1;
    }

    private SwiftBlock2Input generateBlock2(){
        SwiftBlock2Input block2Input = new SwiftBlock2Input();
        block2Input.setMessageType("940");
        block2Input.setReceiverAddress("NUSA SATU INTI ARTHA");
        block2Input.setMessagePriority("");
        block2Input.setDeliveryMonitoring("");
        block2Input.setObsolescencePeriod("");
        return block2Input;
    }

    private SwiftBlock4 generateBlock4(BankMutationDataRequest request){
        String formatDatePeriod = dateUtil.convertToOtherFormat(request.getPeriodDate(), FORMAT_DATE1, FORMAT_DATE2);
        String formatBalanceDate = dateUtil.convertToOtherFormat(request.getBalanceDate(), FORMAT_DATE3, FORMAT_DATE2);

        SwiftBlock4 block4 = new SwiftBlock4();

        Field20 field20 = new Field20();
        field20.setReference(formatDatePeriod);

        Field25 field25 = new Field25();
        field25.setAccount(request.getBankAccountNumber());

        BigDecimal initialBalance = request.getInitialBalance();

        Field60F field60F = new Field60F();
        field60F.setAmount(initialBalance);
        field60F.setCurrency("IDR");
        field60F.setDate(formatBalanceDate);
        field60F.setDCMark(MutationType.CREDIT.getCode());

        block4.append(field20, field25, field60F);

        BigDecimal lastBalance = initialBalance;
        for(MutationData m : request.getMutations()){
            String formatDateMutation = dateUtil.convertToOtherFormat(m.getMutationDate(), FORMAT_DATE1, FORMAT_DATE3);

            Field61 field61 = new Field61();
            field61.setValueDate(formatDateMutation);
            field61.setDCMark(m.getMutationType());
            field61.setAmount(m.getAmount());

            Field86 field86 = new Field86();
            field86.setNarrative(m.getDescription());

            block4.append(field61, field86);

            MutationType type = MutationType.byCode(m.getMutationType());
            BigDecimal mutationAmount = getAmount(type, m.getAmount());

            lastBalance = lastBalance.add(mutationAmount);
        }


        Field62F field62F = new Field62F();
        field62F.setAmount(lastBalance);
        field62F.setCurrency("IDR");
        field62F.setDate(formatBalanceDate);
        field62F.setDCMark(MutationType.CREDIT.getCode());

        block4.append(field62F);

        return block4;
    }

}
