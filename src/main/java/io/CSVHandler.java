package io;

import java.io.File;
import java.io.FileReader;
import com.opencsv.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static repo.TransactionRepo.add;

public class CSVHandler {
    private static final DateTimeFormatter REVOLUT_TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    //Read CSV file line by line
    public static void readBankStatement(Path pathToFile, int accID, int incomeAccID, int expenseAccID){
        try{
            //create a file object
            File file = new File(pathToFile.toUri());

            // Create an object of filereader
            // class with CSV file as a parameter.
            FileReader filereader = new FileReader(file);

            // create csvReader object passing
            // file reader as a parameter
            CSVReader csvReader = new CSVReader(filereader);

            String[] header = csvReader.readNext();
            if (header == null) {
                return;
            }
            Map<String, Integer> idx = headerIndex(header);

            String[] first = csvReader.readNext();
            if (first == null) {
                return;
            }
            String[] second = csvReader.readNext();
            if (second == null) {
                processRow(first, null, idx, accID, incomeAccID, expenseAccID);
                return;
            }

            boolean descending = isDescending(first, second, idx);
            if (descending) {
                String[] current = first;
                String[] next = second;
                while (current != null) {
                    Integer deltaCents = null;
                    if (next != null) {
                        int currentBalanceCents = parseBalanceCents(current, idx);
                        int nextBalanceCents = parseBalanceCents(next, idx);
                        deltaCents = currentBalanceCents - nextBalanceCents;
                    }
                    processRow(current, deltaCents, idx, accID, incomeAccID, expenseAccID);
                    current = next;
                    next = csvReader.readNext();
                }
            } else {
                processRow(first, null, idx, accID, incomeAccID, expenseAccID);
                int prevBalanceCents = parseBalanceCents(first, idx);
                String[] row;
                while ((row = csvReader.readNext()) != null) {
                    int currentBalanceCents = parseBalanceCents(row, idx);
                    int deltaCents = currentBalanceCents - prevBalanceCents;
                    processRow(row, deltaCents, idx, accID, incomeAccID, expenseAccID);
                    prevBalanceCents = currentBalanceCents;
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void processRow(String[] row, Integer balanceDeltaOverride, Map<String, Integer> idx,
                                   int accID, int incomeAccID, int expenseAccID) {
        int deltaCents = balanceDeltaOverride != null ? balanceDeltaOverride : parseAmountCents(row, idx);
        if (deltaCents == 0) {
            return;
        }
        long counterId = deltaCents > 0 ? incomeAccID : expenseAccID;
        String description = buildDescription(row, idx);
        String occurredAtIso = row[idx.get("date")].trim();
        add(accID, counterId, deltaCents, description, occurredAtIso);
    }

    private static Map<String, Integer> headerIndex(String[] header) {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < header.length; i++) {
            String key = header[i].trim().toLowerCase();
            map.put(key, i);
        }
        if (!map.containsKey("date")) {
            map.put("date", 0);
            map.put("description", 1);
            map.put("reference", 2);
            map.put("amount", 3);
            map.put("currency", 4);
            map.put("balance", 5);
        }
        return map;
    }

    private static boolean isDescending(String[] first, String[] second, Map<String, Integer> idx) {
        LocalDateTime t1 = parseDate(first[idx.get("date")].trim());
        LocalDateTime t2 = parseDate(second[idx.get("date")].trim());
        if (t1 == null || t2 == null) {
            return true;
        }
        return t1.isAfter(t2);
    }

    private static LocalDateTime parseDate(String value) {
        try {
            return LocalDateTime.parse(value, REVOLUT_TS);
        } catch (Exception e) {
            return null;
        }
    }

    private static int parseAmountCents(String[] row, Map<String, Integer> idx) {
        String raw = row[idx.get("amount")].trim();
        return parseMoneyCents(raw);
    }

    private static int parseBalanceCents(String[] row, Map<String, Integer> idx) {
        String raw = row[idx.get("balance")].trim();
        return parseMoneyCents(raw);
    }

    private static int parseMoneyCents(String raw) {
        if (raw.isEmpty()) {
            return 0;
        }
        BigDecimal bd = new BigDecimal(raw);
        bd = bd.movePointRight(2).setScale(0, RoundingMode.HALF_UP);
        return bd.intValueExact();
    }

    private static String buildDescription(String[] row, Map<String, Integer> idx) {
        String desc = row[idx.get("description")].trim();
        String ref = row[idx.get("reference")].trim();
        if (ref.isEmpty()) {
            return desc;
        }
        return desc + " / " + ref;
    }
}
