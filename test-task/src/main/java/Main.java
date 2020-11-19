import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Main {
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static void main(String[] args) {

        LocalDateTime fromDate = LocalDateTime.parse(args[2], FORMATTER);
        LocalDateTime toDate = LocalDateTime.parse(args[3], FORMATTER);

        List<String[]> transactions = parseTransactions(args[0]);
        List<String[]> res = getRequired(transactions, args[1], fromDate, toDate);
        for (String[] e: res) {
            System.out.println(Arrays.toString(e));
        }
    }

    private static List<String[]> getRequired(List<String[]> transactions,
                                              String Merchant,
                                              LocalDateTime fromDate,
                                              LocalDateTime toDate) {
        if (Merchant == null) {
            System.out.println("owibka");
        }
        Set<String> reversalTransactionsIds = transactions.stream().filter(t ->
                t[4].equals("REVERSAL")).map(t -> t[5]).collect(Collectors.toCollection(HashSet::new));

        return transactions.stream().filter(t ->
                t[3].equals(Merchant) && t[4].equals("PAYMENT")).filter(t ->
                !LocalDateTime.parse(t[1], FORMATTER).isBefore(fromDate) &&
                        !LocalDateTime.parse(t[1], FORMATTER).isAfter(toDate)).filter(t ->
                !reversalTransactionsIds.contains(t[0])).collect(Collectors.toList());
    }

    private static List<String[]> parseTransactions(String fileName) {
        CsvParserSettings settings = new CsvParserSettings();
        settings.getFormat().setLineSeparator("\n");
        settings.setNumberOfRowsToSkip(1);

        CsvParser parser = new CsvParser(settings);

        return parser.parseAll(new File("src/main/resources/" + fileName));
    }
}
