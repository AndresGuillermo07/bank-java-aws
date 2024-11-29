package com.lambda;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BankRequest {

    private BigDecimal amount; //monto
    private int term; //plazo
    private BigDecimal rate; //tasa

}
