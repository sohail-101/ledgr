package com.ledgr.service;

import java.math.BigDecimal;

public class MonthlyPoint {

    private final String label;
    private final BigDecimal income;
    private final BigDecimal expense;

    public MonthlyPoint(String label, BigDecimal income, BigDecimal expense) {
        this.label = label;
        this.income = income;
        this.expense = expense;
    }

    public String getLabel() {
        return label;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public BigDecimal getExpense() {
        return expense;
    }
}
