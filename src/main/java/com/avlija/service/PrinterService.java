package com.avlija.service;

import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Collections;

@Service
public class PrinterService {

    // ESC/POS control codes
    private static final byte ESC = 27;
    private static final byte GS = 29;

    public byte[] generateReceipt(List<Map<String, Object>> items, double total, String date, int invoiceNumber) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            // Initialize printer
            baos.write(new byte[]{ESC, '@'});

            // Set font to small
            baos.write(new byte[]{ESC, '!', 0});

            // Center align
            baos.write(new byte[]{ESC, 'a', 1});

            // Title - AVLIJA
            baos.write(boldText("AVLIJA").getBytes(StandardCharsets.UTF_8));
            baos.write("\n".getBytes(StandardCharsets.UTF_8));

            baos.write("Kafana\n".getBytes(StandardCharsets.UTF_8));
            baos.write("Guča, Srbija\n".getBytes(StandardCharsets.UTF_8));

            // Dashed line
            baos.write(dashedLine().getBytes(StandardCharsets.UTF_8));

            // Meta info - left align
            baos.write(new byte[]{ESC, 'a', 0});
            baos.write(formatMeta("Datum:", date).getBytes(StandardCharsets.UTF_8));

            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            baos.write(formatMeta("Vreme:", time).getBytes(StandardCharsets.UTF_8));

            if (invoiceNumber > 0) {
                baos.write(formatMeta("Broj računa:", "#" + invoiceNumber).getBytes(StandardCharsets.UTF_8));
            }

            // Dashed line
            baos.write(dashedLine().getBytes(StandardCharsets.UTF_8));

            // Table header
            baos.write(formatTableHeader().getBytes(StandardCharsets.UTF_8));

            // Solid line
            baos.write(solidLine().getBytes(StandardCharsets.UTF_8));

            // Items
            for (Map<String, Object> item : items) {
                String name = (String) item.get("articlename");
                double qty = Double.parseDouble(item.get("total_qty").toString());
                double price = Double.parseDouble(item.get("total_price").toString());
                double itemTotal = Double.parseDouble(item.get("total").toString());

                baos.write(formatTableRow(name, qty, price, itemTotal).getBytes(StandardCharsets.UTF_8));
            }

            // Solid line
            baos.write(solidLine().getBytes(StandardCharsets.UTF_8));

            // Total
            baos.write(new byte[]{ESC, 'a', 1}); // Center
            String totalLine = String.format("UKUPNO: %.2f RSD", total).replace('.', ',');
            baos.write(boldText(totalLine).getBytes(StandardCharsets.UTF_8));
            baos.write("\n".getBytes(StandardCharsets.UTF_8));

            // Dashed line
            baos.write(dashedLine().getBytes(StandardCharsets.UTF_8));

            // Footer
            baos.write(new byte[]{ESC, 'a', 1}); // Center
            baos.write("Hvala na poseti!\n".getBytes(StandardCharsets.UTF_8));
            baos.write("★ Dodjite nam ponovo ★\n".getBytes(StandardCharsets.UTF_8));

            // Cut paper
            baos.write(new byte[]{GS, 'V', 65, 0});

            return baos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    private String boldText(String text) {
        return "\u001B" + "E" + text + "\u001B" + "F";
    }

    private String formatMeta(String label, String value) {
        int totalWidth = 40; // 80mm / 2 chars per mm
        int labelWidth = label.length();
        int spaces = totalWidth - labelWidth - value.length();
        return label + repeatString(" ", Math.max(1, spaces)) + value + "\n";
    }

    private String formatTableHeader() {
        return String.format("%-24s %6s %6s %6s\n", "Artikal", "Kol", "Cena", "Ukupno");
    }

    private String formatTableRow(String name, double qty, double price, double total) {
        String qtyStr = String.format("%.0f", qty);
        String priceStr = String.format("%.2f", price).replace('.', ',');
        String totalStr = String.format("%.2f", total).replace('.', ',');

        // Truncate name to fit 80mm
        if (name.length() > 24) {
            name = name.substring(0, 24);
        }

        return String.format("%-24s %6s %6s %6s\n", name, qtyStr, priceStr, totalStr);
    }

    private String dashedLine() {
        return repeatString("-", 40) + "\n";
    }

    private String solidLine() {
        return repeatString("=", 40) + "\n";
    }

    // Java 8 compatible replacement for String.repeat()
    private String repeatString(String str, int count) {
        if (count <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }
}