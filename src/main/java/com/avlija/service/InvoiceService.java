package com.avlija.service;

import com.avlija.model.Invoice;
import com.avlija.model.LinkedBill;
import com.avlija.repository.BillRepository;
import com.avlija.repository.InvoiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InvoiceService {

    private static final Logger log = LoggerFactory.getLogger(InvoiceService.class);

    private final InvoiceRepository invoiceRepository;
    private final BillRepository billRepository;

    public InvoiceService(InvoiceRepository invoiceRepository, BillRepository billRepository) {
        this.invoiceRepository = invoiceRepository;
        this.billRepository = billRepository;
    }

    public int getNextDailyNumber(String targetDate) {
        int next = invoiceRepository.getNextDailyNumber(targetDate);
        log.debug("Next daily invoice number for {}: {}", targetDate, next);
        return next;
    }

    @Transactional
    public Map<String, Object> saveInvoiceWithBillIds(String targetDate, List<Integer> billIds, double total) {
        log.info("Saving invoice for date={} total={} billIds={}", targetDate, total, billIds);
        int number = invoiceRepository.getNextDailyNumber(targetDate);
        int invoiceId = invoiceRepository.insertInvoice(number, targetDate, total);
        invoiceRepository.linkBills(invoiceId, billIds);
        log.info("Invoice saved: id={} number={} linkedBills={}", invoiceId, number, billIds.size());
        Map<String, Object> result = new HashMap<>();
        result.put("id", invoiceId);
        result.put("number", number);
        return result;
    }

    @Transactional
    public void updateInvoiceWithBillIds(int invoiceId, List<Integer> billIds, double total) {
        log.info("Updating invoice id={} total={} billIds={}", invoiceId, total, billIds);
        invoiceRepository.unlinkBills(invoiceId);
        invoiceRepository.linkBills(invoiceId, billIds);
        invoiceRepository.updateTotal(invoiceId, total);
        log.info("Invoice id={} updated successfully", invoiceId);
    }

    @Transactional
    public Map<String, Object> saveInvoiceWithBills(String targetDate, int waiterId, double total, List<Integer> billNumbers) {
        log.info("Saving invoice for date={} waiter={} total={} bills={}", targetDate, waiterId, total, billNumbers);
        int number = invoiceRepository.getNextDailyNumber(targetDate);
        int invoiceId = invoiceRepository.insertInvoice(number, targetDate, total);
        List<Integer> billIds = billRepository.getBillIdsByNumbers(targetDate, waiterId, billNumbers);
        invoiceRepository.linkBills(invoiceId, billIds);
        log.info("Invoice saved: id={} number={} linkedBills={}", invoiceId, number, billIds.size());

        Map<String, Object> result = new HashMap<>();
        result.put("id", invoiceId);
        result.put("number", number);
        return result;
    }

    @Transactional
    public void updateInvoiceWithBills(int invoiceId, String targetDate, int waiterId, List<Integer> billNumbers, double total) {
        log.info("Updating invoice id={} date={} waiter={} total={} bills={}", invoiceId, targetDate, waiterId, total, billNumbers);
        invoiceRepository.unlinkBills(invoiceId);
        List<Integer> billIds = billRepository.getBillIdsByNumbers(targetDate, waiterId, billNumbers);
        invoiceRepository.linkBills(invoiceId, billIds);
        invoiceRepository.updateTotal(invoiceId, total);
        log.info("Invoice id={} updated successfully", invoiceId);
    }

    @Transactional
    public void deleteInvoice(int invoiceId) {
        log.info("Deleting invoice id={}", invoiceId);
        invoiceRepository.deleteInvoice(invoiceId);
        log.info("Invoice id={} deleted", invoiceId);
    }

    public List<Invoice> listInvoices(String dateFrom, String dateTo, Integer invoiceNumber) {
        log.debug("Listing invoices from={} to={} number={}", dateFrom, dateTo, invoiceNumber);
        List<Invoice> result = invoiceRepository.findByDateRange(dateFrom, dateTo, invoiceNumber);
        log.debug("Found {} invoices", result.size());
        return result;
    }

    public List<LinkedBill> getLinkedBills(int invoiceId) {
        log.debug("Getting linked bills for invoice id={}", invoiceId);
        return invoiceRepository.getLinkedBills(invoiceId);
    }
}