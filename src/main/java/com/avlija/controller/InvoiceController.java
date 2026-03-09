package com.avlija.controller;

import com.avlija.model.Invoice;
import com.avlija.model.LinkedBill;
import com.avlija.service.InvoiceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/invoice")
@CrossOrigin(origins = "*")
public class InvoiceController {

    private static final Logger log = LoggerFactory.getLogger(InvoiceController.class);

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping("/save-by-ids")
    public Map<String, Object> saveInvoiceByIds(
            @RequestParam("target_date") String targetDate,
            @RequestParam("bill_ids") List<Integer> billIds,
            @RequestParam("total") double total) {
        log.info("POST /invoice/save-by-ids date={} total={} ids={}", targetDate, total, billIds);
        return invoiceService.saveInvoiceWithBillIds(targetDate, billIds, total);
    }

    @PostMapping("/update-by-ids")
    public Map<String, Object> updateInvoiceByIds(
            @RequestParam("invoice_id") int invoiceId,
            @RequestParam("bill_ids") List<Integer> billIds,
            @RequestParam("total") double total) {
        log.info("POST /invoice/update-by-ids id={} total={} ids={}", invoiceId, total, billIds);
        invoiceService.updateInvoiceWithBillIds(invoiceId, billIds, total);
        Map<String, Object> result = new HashMap<>();
        result.put("invoice_id", invoiceId);
        result.put("updated", true);
        return result;
    }

    @GetMapping("/next-number")
    public Map<String, Object> getNextNumber(@RequestParam("target_date") String targetDate) {
        log.debug("GET /invoice/next-number date={}", targetDate);
        Map<String, Object> result = new HashMap<>();
        result.put("number", invoiceService.getNextDailyNumber(targetDate));
        return result;
    }

    @PostMapping("/save")
    public Map<String, Object> saveInvoice(
            @RequestParam("target_date") String targetDate,
            @RequestParam("waiter_id") int waiterId,
            @RequestParam("total") double total,
            @RequestParam("bill_numbers") List<Integer> billNumbers) {
        log.info("POST /invoice/save date={} waiter={} total={} bills={}", targetDate, waiterId, total, billNumbers);
        return invoiceService.saveInvoiceWithBills(targetDate, waiterId, total, billNumbers);
    }

    @PostMapping("/update-bills")
    public Map<String, Object> updateBills(
            @RequestParam("invoice_id") int invoiceId,
            @RequestParam("target_date") String targetDate,
            @RequestParam("waiter_id") int waiterId,
            @RequestParam("bill_numbers") List<Integer> billNumbers,
            @RequestParam("total") double total) {
        log.info("POST /invoice/update-bills id={} date={} waiter={} total={}", invoiceId, targetDate, waiterId, total);
        invoiceService.updateInvoiceWithBills(invoiceId, targetDate, waiterId, billNumbers, total);
        Map<String, Object> result = new HashMap<>();
        result.put("invoice_id", invoiceId);
        result.put("updated", true);
        return result;
    }

    @DeleteMapping("/{invoiceId}")
    public Map<String, Object> deleteInvoice(@PathVariable int invoiceId) {
        log.info("DELETE /invoice/{}", invoiceId);
        invoiceService.deleteInvoice(invoiceId);
        Map<String, Object> result = new HashMap<>();
        result.put("id", invoiceId);
        result.put("deleted", true);
        return result;
    }

    @GetMapping("/list")
    public List<Invoice> listInvoices(
            @RequestParam("date_from") String dateFrom,
            @RequestParam("date_to") String dateTo,
            @RequestParam(value = "invoice_number", required = false) Integer invoiceNumber) {
        log.debug("GET /invoice/list from={} to={} number={}", dateFrom, dateTo, invoiceNumber);
        return invoiceService.listInvoices(dateFrom, dateTo, invoiceNumber);
    }

    @GetMapping("/{invoiceId}/bills-detail")
    public List<LinkedBill> getLinkedBills(@PathVariable int invoiceId) {
        log.debug("GET /invoice/{}/bills-detail", invoiceId);
        return invoiceService.getLinkedBills(invoiceId);
    }
}