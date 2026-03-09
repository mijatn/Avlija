package com.avlija.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;

public class LinkedBill {

    @JsonProperty("id")
    private int id;

    @JsonProperty("number")
    private int number;

    @JsonProperty("ddate")
    private LocalDate ddate;

    @JsonProperty("waiterid")
    private Integer waiterId;

    @JsonProperty("total")
    private BigDecimal total;

    public LinkedBill() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }

    public LocalDate getDdate() { return ddate; }
    public void setDdate(LocalDate ddate) { this.ddate = ddate; }

    public Integer getWaiterId() { return waiterId; }
    public void setWaiterId(Integer waiterId) { this.waiterId = waiterId; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
}