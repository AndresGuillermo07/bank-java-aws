package com.lambda;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BankResponse {

    private BigDecimal quote;
    private BigDecimal rate;
    private Integer term;

    private BigDecimal quoteWithAccount;
    private BigDecimal rateWithAccount;
    private Integer termWithAccount;

}
