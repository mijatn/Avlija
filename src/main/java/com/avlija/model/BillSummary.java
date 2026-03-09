package com.avlija.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class BillSummary {

    @JsonProperty("articleid")
    private int articleId;

    @JsonProperty("articlename")
    private String articleName;

    @JsonProperty("total_qty")
    private BigDecimal totalQty;

    @JsonProperty("total_price")
    private BigDecimal totalPrice;

    @JsonProperty("total")
    private BigDecimal total;

    public BillSummary() {}

    public int getArticleId() { return articleId; }
    public void setArticleId(int articleId) { this.articleId = articleId; }

    public String getArticleName() { return articleName; }
    public void setArticleName(String articleName) { this.articleName = articleName; }

    public BigDecimal getTotalQty() { return totalQty; }
    public void setTotalQty(BigDecimal totalQty) { this.totalQty = totalQty; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
}