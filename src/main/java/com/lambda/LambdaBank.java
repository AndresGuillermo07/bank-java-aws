package com.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/*
* p -> monto del prestamo
* i -> tasa de interes mensual
* n = plazo del credito en meses
*
* Cuota mensual = (p * i) / (1 - (1 + i) ^ (-n))
* */


public class LambdaBank implements RequestHandler<BankRequest,BankResponse> {

    @Override
    public BankResponse handleRequest(BankRequest bankRequest, Context context) {

        MathContext mathContext = MathContext.DECIMAL128; // Presicion de Operaciones

        // -----------------------------------------------------------

        BigDecimal amount = bankRequest
                .getAmount()
                .setScale(2, RoundingMode.HALF_UP); // Cantidad de decimales - Redondeo

        // -----------------------------------------------------------

        BigDecimal monthlyRate = bankRequest
                .getRate()
                .setScale(2, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(100), mathContext);

        // -----------------------------------------------------------

        BigDecimal monthlyRateWithAccount = bankRequest
                .getRate()
                .subtract(BigDecimal.valueOf(0.2), mathContext)
                .setScale(2, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(100), mathContext);

        // -----------------------------------------------------------

        Integer term = bankRequest.getTerm();

        BigDecimal monthlyPayement = this.calculateQuote(amount,monthlyRate,term,mathContext);
        BigDecimal monthlyPaymentWithAccount = this.calculateQuote(amount,monthlyRateWithAccount,term,mathContext);

        BankResponse bankResponse = new BankResponse();
        bankResponse.setQuote(monthlyPayement);
        bankResponse.setRate(monthlyRate.multiply(BigDecimal.valueOf(100)));
        bankResponse.setTerm(term);
        bankResponse.setQuoteWithAccount(monthlyPaymentWithAccount);
        bankResponse.setRateWithAccount(monthlyRateWithAccount);
        bankResponse.setTermWithAccount(term);



        return bankResponse;
    }

    public BigDecimal calculateQuote(BigDecimal amount, BigDecimal rate, Integer term, MathContext mathContext){

        // (p * i) / (1 - (1 + i) ^ (-n)) -> Formula

        // Calcular (1 + i)
        BigDecimal onePlusRate  = rate.add(BigDecimal.ONE, mathContext);

        // calcular (1 + i) ^ (n) y luego tomar el reciproco para obtener (1 + i) ^ (-n)
        BigDecimal onePlusRateToN = onePlusRate.pow(term, mathContext);
        BigDecimal onePlusRateToNegativeN = BigDecimal.ONE.divide(onePlusRateToN, mathContext);

        // Calcular (p * i)
        BigDecimal numerator = amount.multiply(rate, mathContext);
        BigDecimal denominator = BigDecimal.ONE.subtract(onePlusRateToNegativeN, mathContext);

        BigDecimal monthlyPaymet = numerator.divide(denominator, mathContext);
        monthlyPaymet = monthlyPaymet.setScale(2, RoundingMode.HALF_UP);

        return monthlyPaymet;

    }
}
