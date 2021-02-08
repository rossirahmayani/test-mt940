package com.rossi.testmt940.service;

import com.prowidesoftware.swift.io.parser.SwiftParser;
import com.prowidesoftware.swift.model.*;
import com.prowidesoftware.swift.model.field.*;
import com.prowidesoftware.swift.model.mt.mt9xx.MT940;
import com.rossi.testmt940.enums.MutationType;
import com.rossi.testmt940.enums.ResponseCode;
import com.rossi.testmt940.model.BankMutationRequest;
import com.rossi.testmt940.model.BankMutationResponse;
import com.rossi.testmt940.model.BaseResponse;
import com.rossi.testmt940.model.MutationData;
import com.rossi.testmt940.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class Mt940Service {

    @Autowired
    DateUtil dateUtil;

    @Autowired
    GenerateFileService generateFileService;

    private static final String FORMAT_DATE1 = "yyyy-MM-dd";
    private static final String FORMAT_DATE2 = "yyyyMMddHHmmss";
    private static final String FORMAT_DATE3 = "yyMMdd";

    public BaseResponse parse(String mtString) throws IOException {
        //parse mt940
        SwiftParser parser = new SwiftParser();
        parser.setReader(new StringReader(mtString));
        SwiftMessage message = parser.message();

        MT940 mt940 = new MT940(message);
        BigDecimal initBalance = mt940.getField60F().amount();
        String period = mt940.getField20().getReference();
        String bankAccNum = mt940.getField25().getAccount();
        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalCredit = BigDecimal.ZERO;

        List<MutationData> mutations = new ArrayList<>();
        for(int i = 0; i < mt940.getField61().size(); i++){
            Field61 field61 = mt940.getField61().get(i);
            Field86 field86 = mt940.getField86().get(i);

            String mutationType = field61.getDCMark();

            MutationData mutationData = new MutationData();
            mutationData.setMutationDate(field61.getValueDate());
            mutationData.setMutationType(MutationType.byCode(mutationType).getDescription());
            mutationData.setAmount(field61.getAmount());
            mutationData.setDescription(field86.getNarrative());
            mutations.add(mutationData);

            if (mutationType.equalsIgnoreCase(MutationType.CREDIT.getCode())){
                totalCredit = totalCredit.add(field61.amount());
            }
            else {
                totalDebit = totalDebit.add(field61.amount());
            }
        }

        BigDecimal lastBalance = initBalance.add(totalCredit).subtract(totalDebit);

        BankMutationResponse response = BankMutationResponse
                .builder().periodDate(period).totalMutations(mutations.size())
                .bankAccNum(bankAccNum).initialBalance(initBalance)
                .totalCredit(totalCredit).totalDebit(totalDebit)
                .mutations(mutations).countedLastBalance(lastBalance)
                .reportedLastBalance(mt940.getField62F().amount())
                .build();

        return BaseResponse.builder()
                .responseCode(ResponseCode.SUCCESS.getCode())
                .responseMessage(ResponseCode.SUCCESS.getMessage())
                .responseData(response)
                .build();
    }

    public BaseResponse generate(BankMutationRequest request){
        //generate mt940 component
        SwiftMessage message = new SwiftMessage();
        message.setBlock1(generateBlock1(request.getBankName()));
        message.setBlock2(generateBlock2());
        message.setBlock4(generateBlock4(request));

        MT940 mt940 = new MT940(message);
        String result = mt940.message();

        //generate file
        generateFileService.generateFile(result);

        return BaseResponse.builder()
                .responseCode(ResponseCode.SUCCESS.getCode())
                .responseMessage(ResponseCode.SUCCESS.getMessage())
                .responseData(result)
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
        block1.setLogicalTerminal(bankName.toUpperCase());
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

    private SwiftBlock4 generateBlock4(BankMutationRequest request){
        String formatDatePeriod = dateUtil.convertToOtherFormat(request.getPeriodDate(), FORMAT_DATE1, FORMAT_DATE2);
        String formatBalanceDate = dateUtil.convertToOtherFormat(request.getBalanceDate(), FORMAT_DATE3, FORMAT_DATE3);

        SwiftBlock4 block4 = new SwiftBlock4();

        Field20 field20 = new Field20();
        field20.setReference(formatDatePeriod);

        Field25 field25 = new Field25();
        field25.setAccount(request.getBankAccountNumber());

        Field28C field28C = new Field28C();
        field28C.setStatementNumber("00111");

        BigDecimal initialBalance = new BigDecimal(request.getInitialBalance());

        Field60F field60F = new Field60F();
        field60F.setAmount(request.getInitialBalance());
        field60F.setCurrency(request.getCurrency().toUpperCase());
        field60F.setDate(formatBalanceDate);
        field60F.setDCMark(MutationType.CREDIT.getCode());

        block4.append(field20, field25, field28C, field60F);

        BigDecimal lastBalance = initialBalance;
        for(MutationData m : request.getMutations()){
            String formatDateMutation = dateUtil.convertToOtherFormat(m.getMutationDate(), FORMAT_DATE1, FORMAT_DATE3);

            Field61 field61 = new Field61();
            field61.setValueDate(formatDateMutation);
            field61.setDCMark(m.getMutationType());
            field61.setAmount(m.getAmount());
            field61.setTransactionType("N");
            field61.setIdentificationCode("TRF");
            field61.setReferenceForTheAccountOwner("");
            field61.setReferenceOfTheAccountServicingInstitution("");

            Field86 field86 = new Field86();
            field86.setNarrative(m.getDescription());

            block4.append(field61, field86);

            MutationType type = MutationType.byCode(m.getMutationType());
            BigDecimal mutationAmount = getAmount(type, new BigDecimal(m.getAmount()));

            lastBalance = lastBalance.add(mutationAmount);
        }

        Field62F field62F = new Field62F();
        field62F.setAmount(lastBalance.toBigInteger().toString());
        field62F.setCurrency(request.getCurrency().toUpperCase());
        field62F.setDate(formatBalanceDate);
        field62F.setDCMark(MutationType.CREDIT.getCode());

        block4.append(field62F);

        return block4;
    }

}
