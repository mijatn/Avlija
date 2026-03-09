package com.avlija.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Invoice {

    @JsonProperty("id")
    private int id;

    @JsonProperty("number")
    private int number;

    @JsonProperty("ddate")
    private LocalDate ddate;

    @JsonProperty("total")
    private BigDecimal total;

    @JsonProperty("paid")
    private Boolean paid;

    public Invoice() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }

    public LocalDate getDdate() { return ddate; }
    public void setDdate(LocalDate ddate) { this.ddate = ddate; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public Boolean getPaid() { return paid; }
    public void setPaid(Boolean paid) { this.paid = paid; }
}