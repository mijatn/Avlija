package com.avlija.controller;

import com.avlija.service.PrinterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/printer")
public class PrinterController {

    @Autowired
    private PrinterService printerService;

    @PostMapping(value = "/print", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> printReceipt(@RequestBody PrintRequest request) {
        byte[] escPosData = printerService.generateReceipt(
                request.getItems(),
                request.getTotal(),
                request.getDate(),
                request.getInvoiceNumber()
        );

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"receipt.bin\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(escPosData);
    }

    public static class PrintRequest {
        private List<Map<String, Object>> items;
        private double total;
        private String date;
        private int invoiceNumber;

        // Getters and Setters
        public List<Map<String, Object>> getItems() { return items; }
        public void setItems(List<Map<String, Object>> items) { this.items = items; }

        public double getTotal() { return total; }
        public void setTotal(double total) { this.total = total; }

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }

        public int getInvoiceNumber() { return invoiceNumber; }
        public void setInvoiceNumber(int invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    }
}