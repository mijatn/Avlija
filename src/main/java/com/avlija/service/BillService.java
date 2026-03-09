package com.avlija.service;

import com.avlija.model.BillSummary;
import com.avlija.repository.BillRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BillService {

    private static final Logger log = LoggerFactory.getLogger(BillService.class);

    private final BillRepository billRepository;

    public BillService(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    public List<BillSummary> getSummary(String targetDate, int waiterId, List<Integer> billNumbers) {
        log.debug("Getting bill summary for date={} waiter={} bills={}", targetDate, waiterId, billNumbers);
        List<BillSummary> result = billRepository.getSummary(targetDate, waiterId, billNumbers);
        log.debug("Found {} article rows", result.size());
        return result;
    }

    public List<Integer> getMissingBillNumbers(String targetDate, int waiterId, List<Integer> billNumbers) {
        log.debug("Validating bill numbers for date={} waiter={} bills={}", targetDate, waiterId, billNumbers);
        List<Integer> found = billRepository.findExistingBillNumbers(targetDate, waiterId, billNumbers);
        List<Integer> missing = new ArrayList<>();
        for (int num : billNumbers) {
            if (!found.contains(num)) missing.add(num);
        }
        if (!missing.isEmpty()) {
            log.warn("Missing bill numbers for date={} waiter={}: {}", targetDate, waiterId, missing);
        }
        return missing;
    }

    public String getMaxDate() {
        String date = billRepository.getMaxDate();
        log.debug("Max bill date: {}", date);
        return date;
    }

    public Integer getInvoiceNumberForBill(String targetDate, int waiterId, int billNumber) {
        log.debug("Checking if bill {} waiter {} on {} is already linked", billNumber, waiterId, targetDate);
        Integer invoiceNumber = billRepository.getInvoiceNumberForBill(targetDate, waiterId, billNumber);
        if (invoiceNumber != null) {
            log.info("Bill {} waiter {} on {} is already linked to invoice #{}", billNumber, waiterId, targetDate, invoiceNumber);
        }
        return invoiceNumber;
    }

    public java.math.BigDecimal getBillTotal(String targetDate, int waiterId, int billNumber) {
        log.debug("Getting total for bill {} waiter {} on {}", billNumber, waiterId, targetDate);
        return billRepository.getBillTotal(targetDate, waiterId, billNumber);
    }

    public Integer resolveBillId(String targetDate, int waiterId, int billNumber) {
        log.debug("Resolving bill ID for date={} waiter={} number={}", targetDate, waiterId, billNumber);
        return billRepository.resolveBillId(targetDate, waiterId, billNumber);
    }

    public List<BillSummary> getSummaryByIds(List<Integer> billIds) {
        log.debug("Getting bill summary by IDs: {}", billIds);
        return billRepository.getSummaryByIds(billIds);
    }

    public List<Integer> getBillIdsByNumbers(String targetDate, int waiterId, List<Integer> billNumbers) {
        log.debug("Resolving bill IDs for date={} waiter={} numbers={}", targetDate, waiterId, billNumbers);
        return billRepository.getBillIdsByNumbers(targetDate, waiterId, billNumbers);
    }
}