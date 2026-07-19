package com.ledgr.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class BudgetForm {

    @NotNull(message = "pick a month")
    @Min(value = 1, message = "month has to be between 1 and 12")
    @Max(value = 12, message = "month has to be between 1 and 12")
    private Integer month;

    @NotNull(message = "pick a year")
    @Min(value = 2000, message = "that year doesn't look right")
    private Integer year;

    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.01", message = "budget has to be more than 0")
    private BigDecimal amount;

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
