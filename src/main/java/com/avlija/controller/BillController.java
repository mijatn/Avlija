package com.avlija.controller;

import com.avlija.model.BillSummary;
import com.avlija.service.BillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bills")
@CrossOrigin(origins = "*")
public class BillController {

    private static final Logger log = LoggerFactory.getLogger(BillController.class);

    private final BillService billService;

    public BillController(BillService billService) {
        this.billService = billService;
    }

    @GetMapping("/resolve")
    public Map<String, Object> resolveBill(
            @RequestParam("target_date") String targetDate,
            @RequestParam("waiter_id") int waiterId,
            @RequestParam("bill_number") int billNumber) {
        log.debug("GET /bills/resolve date={} waiter={} bill={}", targetDate, waiterId, billNumber);
        Integer id = billService.resolveBillId(targetDate, waiterId, billNumber);
        Map<String, Object> result = new HashMap<>();
        result.put("id", id);
        return result;
    }

    @GetMapping("/summary-by-ids")
    public List<BillSummary> getSummaryByIds(
            @RequestParam("bill_ids") List<Integer> billIds) {
        log.info("GET /bills/summary-by-ids ids={}", billIds);
        return billService.getSummaryByIds(billIds);
    }

    @GetMapping("/summary")
    public List<BillSummary> getSummary(
            @RequestParam("target_date") String targetDate,
            @RequestParam("waiter_id") int waiterId,
            @RequestParam("bill_numbers") List<Integer> billNumbers) {
        log.info("GET /bills/summary date={} waiter={} bills={}", targetDate, waiterId, billNumbers);
        return billService.getSummary(targetDate, waiterId, billNumbers);
    }

    @GetMapping("/validate")
    public Map<String, Object> validate(
            @RequestParam("target_date") String targetDate,
            @RequestParam("waiter_id") int waiterId,
            @RequestParam("bill_numbers") List<Integer> billNumbers) {
        log.debug("GET /bills/validate date={} waiter={} bills={}", targetDate, waiterId, billNumbers);
        List<Integer> missing = billService.getMissingBillNumbers(targetDate, waiterId, billNumbers);
        Map<String, Object> result = new HashMap<>();
        result.put("missing", missing);
        return result;
    }

    @GetMapping("/max-date")
    public Map<String, String> getMaxDate() {
        log.debug("GET /bills/max-date");
        Map<String, String> result = new HashMap<>();
        result.put("max_date", billService.getMaxDate());
        return result;
    }

    @GetMapping("/check-linked")
    public Map<String, Object> checkLinked(
            @RequestParam("target_date") String targetDate,
            @RequestParam("waiter_id") int waiterId,
            @RequestParam("bill_number") int billNumber) {
        log.debug("GET /bills/check-linked date={} waiter={} bill={}", targetDate, waiterId, billNumber);
        Integer invoiceNumber = billService.getInvoiceNumberForBill(targetDate, waiterId, billNumber);
        Map<String, Object> result = new HashMap<>();
        result.put("invoice_number", invoiceNumber);
        return result;
    }

    @GetMapping("/total")
    public Map<String, Object> getBillTotal(
            @RequestParam("target_date") String targetDate,
            @RequestParam("waiter_id") int waiterId,
            @RequestParam("bill_number") int billNumber) {
        log.debug("GET /bills/total date={} waiter={} bill={}", targetDate, waiterId, billNumber);
        java.math.BigDecimal total = billService.getBillTotal(targetDate, waiterId, billNumber);
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        return result;
    }
}