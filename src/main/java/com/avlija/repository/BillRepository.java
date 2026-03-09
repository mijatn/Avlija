package com.avlija.repository;

import com.avlija.model.BillSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
public class BillRepository {

    private static final Logger log = LoggerFactory.getLogger(BillRepository.class);

    private final JdbcTemplate jdbc;

    public BillRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<BillSummary> getSummary(String targetDate, int waiterId, List<Integer> billNumbers) {
        String placeholders = String.join(",", Collections.nCopies(billNumbers.size(), "?"));
        String sql = "SELECT bd.articleid, bd.articlename, SUM(bd.quantity) AS total_qty, " +
                "bd.price AS total_price, SUM(bd.quantity * bd.price) AS total " +
                "FROM avlija.dbo.bill b " +
                "INNER JOIN avlija.dbo.billdetails bd ON b.id = bd.billid " +
                "WHERE b.ddate = ? AND b.waiterid = ? AND b.[number] IN (" + placeholders + ") " +
                "GROUP BY bd.articleid, bd.articlename, bd.price";

        Object[] params = new Object[billNumbers.size() + 2];
        params[0] = targetDate;
        params[1] = waiterId;
        for (int i = 0; i < billNumbers.size(); i++) params[i + 2] = billNumbers.get(i);

        log.debug("Executing bill summary query for date={} waiter={}", targetDate, waiterId);
        return jdbc.query(sql, params, (rs, rowNum) -> {
            BillSummary b = new BillSummary();
            b.setArticleId(rs.getInt("articleid"));
            b.setArticleName(rs.getString("articlename"));
            b.setTotalQty(rs.getBigDecimal("total_qty"));
            b.setTotalPrice(rs.getBigDecimal("total_price"));
            b.setTotal(rs.getBigDecimal("total"));
            return b;
        });
    }

    public List<Integer> findExistingBillNumbers(String targetDate, int waiterId, List<Integer> billNumbers) {
        String placeholders = String.join(",", Collections.nCopies(billNumbers.size(), "?"));
        String sql = "SELECT [number] FROM avlija.dbo.bill WHERE ddate = ? AND waiterid = ? AND [number] IN (" + placeholders + ")";

        Object[] params = new Object[billNumbers.size() + 2];
        params[0] = targetDate;
        params[1] = waiterId;
        for (int i = 0; i < billNumbers.size(); i++) params[i + 2] = billNumbers.get(i);

        return jdbc.queryForList(sql, Integer.class, params);
    }

    public String getMaxDate() {
        String sql = "SELECT CONVERT(varchar, MAX(ddate), 23) FROM avlija.dbo.bill";
        return jdbc.queryForObject(sql, String.class);
    }

    public Integer getInvoiceNumberForBill(String targetDate, int waiterId, int billNumber) {
        String sql = "SELECT i.[number] FROM avlija.dbo.bill b " +
                "INNER JOIN avlija.dbo.invoice_bill ib ON ib.billid = b.id " +
                "INNER JOIN avlija.dbo.invoice i ON i.id = ib.invoiceid " +
                "WHERE b.ddate = ? AND b.waiterid = ? AND b.[number] = ?";
        List<Integer> results = jdbc.queryForList(sql, Integer.class, targetDate, waiterId, billNumber);
        return results.isEmpty() ? null : results.get(0);
    }

    public java.math.BigDecimal getBillTotal(String targetDate, int waiterId, int billNumber) {
        String sql = "SELECT total FROM avlija.dbo.bill WHERE ddate = ? AND waiterid = ? AND [number] = ?";
        List<java.math.BigDecimal> results = jdbc.queryForList(sql, java.math.BigDecimal.class, targetDate, waiterId, billNumber);
        return results.isEmpty() ? null : results.get(0);
    }

    public Integer resolveBillId(String targetDate, int waiterId, int billNumber) {
        String sql = "SELECT id FROM avlija.dbo.bill WHERE ddate = ? AND waiterid = ? AND [number] = ?";
        List<Integer> results = jdbc.queryForList(sql, Integer.class, targetDate, waiterId, billNumber);
        return results.isEmpty() ? null : results.get(0);
    }

    public List<BillSummary> getSummaryByIds(List<Integer> billIds) {
        String placeholders = String.join(",", Collections.nCopies(billIds.size(), "?"));
        String sql = "SELECT bd.articleid, bd.articlename, SUM(bd.quantity) AS total_qty, " +
                "bd.price AS total_price, SUM(bd.quantity * bd.price) AS total " +
                "FROM avlija.dbo.bill b " +
                "INNER JOIN avlija.dbo.billdetails bd ON b.id = bd.billid " +
                "WHERE b.id IN (" + placeholders + ") " +
                "GROUP BY bd.articleid, bd.articlename, bd.price";
        Object[] params = billIds.stream().map(id -> (Object) id).toArray();
        log.debug("Executing bill summary by IDs: {}", billIds);
        return jdbc.query(sql, params, (rs, rowNum) -> {
            BillSummary b = new BillSummary();
            b.setArticleId(rs.getInt("articleid"));
            b.setArticleName(rs.getString("articlename"));
            b.setTotalQty(rs.getBigDecimal("total_qty"));
            b.setTotalPrice(rs.getBigDecimal("total_price"));
            b.setTotal(rs.getBigDecimal("total"));
            return b;
        });
    }

    public List<Integer> getBillIdsByNumbers(String targetDate, int waiterId, List<Integer> billNumbers) {
        String placeholders = String.join(",", Collections.nCopies(billNumbers.size(), "?"));
        String sql = "SELECT id FROM avlija.dbo.bill WHERE ddate = ? AND waiterid = ? AND [number] IN (" + placeholders + ")";

        Object[] params = new Object[billNumbers.size() + 2];
        params[0] = targetDate;
        params[1] = waiterId;
        for (int i = 0; i < billNumbers.size(); i++) params[i + 2] = billNumbers.get(i);

        log.debug("Resolving bill IDs for date={} waiter={} numbers={}", targetDate, waiterId, billNumbers);
        return jdbc.queryForList(sql, Integer.class, params);
    }
}