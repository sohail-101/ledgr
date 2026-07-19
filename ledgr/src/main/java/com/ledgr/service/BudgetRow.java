package com.ledgr.service;

import com.ledgr.entity.Budget;

import java.math.BigDecimal;

public class BudgetRow {

    private final Budget budget;
    private final BigDecimal spent;
    private final int pctSpent;

    public BudgetRow(Budget budget, BigDecimal spent, int pctSpent) {
        this.budget = budget;
        this.spent = spent;
        this.pctSpent = pctSpent;
    }

    public Budget getBudget() {
        return budget;
    }

    public BigDecimal getSpent() {
        return spent;
    }

    public BigDecimal getRemaining() {
        return budget.getAmount().subtract(spent);
    }

    public int getPctSpent() {
        return pctSpent;
    }
}
