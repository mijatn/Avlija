package com.avlija.repository;

import com.avlija.model.Invoice;
import com.avlija.model.LinkedBill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InvoiceRepository {

    private static final Logger log = LoggerFactory.getLogger(InvoiceRepository.class);

    private final JdbcTemplate jdbc;

    public InvoiceRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public int getNextDailyNumber(String targetDate) {
        String sql = "SELECT ISNULL(MAX([number]), 0) + 1 FROM avlija.dbo.invoice WHERE ddate = ?";
        Integer result = jdbc.queryForObject(sql, Integer.class, targetDate);
        return result != null ? result : 1;
    }

    public int insertInvoice(int number, String targetDate, double total) {
        log.debug("Inserting invoice number={} date={} total={}", number, targetDate, total);
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbc)
                .withSchemaName("dbo")
                .withTableName("invoice")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> params = new HashMap<>();
        params.put("number", number);
        params.put("ddate", Date.valueOf(targetDate));
        params.put("total", total);
        params.put("paid", false);

        Number key = insert.executeAndReturnKey(params);
        log.debug("Invoice inserted with id={}", key.intValue());
        return key.intValue();
    }

    public void linkBills(int invoiceId, List<Integer> billIds) {
        log.debug("Linking {} bills to invoice id={}", billIds.size(), invoiceId);
        for (int billId : billIds) {
            jdbc.update("INSERT INTO avlija.dbo.invoice_bill (billid, invoiceid) VALUES (?, ?)", billId, invoiceId);
        }
    }

    public void unlinkBills(int invoiceId) {
        log.debug("Unlinking all bills from invoice id={}", invoiceId);
        jdbc.update("DELETE FROM avlija.dbo.invoice_bill WHERE invoiceid = ?", invoiceId);
    }

    public void updateTotal(int invoiceId, double total) {
        log.debug("Updating total for invoice id={} to {}", invoiceId, total);
        jdbc.update("UPDATE avlija.dbo.invoice SET total = ? WHERE id = ?", total, invoiceId);
    }

    public void deleteInvoice(int invoiceId) {
        log.debug("Deleting invoice id={}", invoiceId);
        jdbc.update("DELETE FROM avlija.dbo.invoice_bill WHERE invoiceid = ?", invoiceId);
        jdbc.update("DELETE FROM avlija.dbo.invoice WHERE id = ?", invoiceId);
    }

    public List<Invoice> findByDateRange(String dateFrom, String dateTo, Integer invoiceNumber) {
        StringBuilder sql = new StringBuilder(
                "SELECT id, number, ddate, total, paid FROM avlija.dbo.invoice " +
                        "WHERE ddate BETWEEN ? AND ?");
        if (invoiceNumber != null) sql.append(" AND number = ?");
        sql.append(" ORDER BY ddate DESC, number ASC");

        log.debug("Listing invoices from={} to={} number={}", dateFrom, dateTo, invoiceNumber);
        if (invoiceNumber != null) {
            return jdbc.query(sql.toString(), this::mapInvoice, dateFrom, dateTo, invoiceNumber);
        } else {
            return jdbc.query(sql.toString(), this::mapInvoice, dateFrom, dateTo);
        }
    }

    public List<LinkedBill> getLinkedBills(int invoiceId) {
        String sql = "SELECT b.id, b.[number], b.ddate, b.total, b.waiterid " +
                "FROM avlija.dbo.invoice_bill ib " +
                "INNER JOIN avlija.dbo.bill b ON ib.billid = b.id " +
                "WHERE ib.invoiceid = ? ORDER BY b.[number]";
        log.debug("Getting linked bills for invoice id={}", invoiceId);
        return jdbc.query(sql, (rs, rowNum) -> {
            LinkedBill lb = new LinkedBill();
            lb.setId(rs.getInt("id"));
            lb.setNumber(rs.getInt("number"));
            lb.setDdate(rs.getDate("ddate").toLocalDate());
            lb.setTotal(rs.getBigDecimal("total"));
            int waiterId = rs.getInt("waiterid");
            lb.setWaiterId(rs.wasNull() ? null : waiterId);
            return lb;
        }, invoiceId);
    }

    private Invoice mapInvoice(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        Invoice inv = new Invoice();
        inv.setId(rs.getInt("id"));
        inv.setNumber(rs.getInt("number"));
        inv.setDdate(rs.getDate("ddate").toLocalDate());
        inv.setTotal(rs.getBigDecimal("total"));
        inv.setPaid(rs.getBoolean("paid"));
        return inv;
    }
}