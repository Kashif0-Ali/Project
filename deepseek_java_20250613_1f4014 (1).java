package todoproject.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    private static final String DATE_PATTERN = "MM/dd/yyyy";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

    public static String format(LocalDate date) {
        if (date == null) {
            return null;
        }
        return DATE_FORMATTER.format(date);
    }

    public static LocalDate parse(String dateString) {
        try {
            return DATE_FORMATTER.parse(dateString, LocalDate::from);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean validDate(String dateString) {
        return parse(dateString) != null;
    }
}