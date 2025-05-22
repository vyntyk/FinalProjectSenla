package org.example.foodmonitoring.util;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalDateTimeConverter extends AbstractBeanField<LocalDateTime, String> {

    // Define the expected date-time format(s)
    // ISO_LOCAL_DATE_TIME is "yyyy-MM-dd'T'HH:mm:ss" but can be extended
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    protected LocalDateTime convert(String value) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        if (value == null || value.trim().isEmpty()) {
            return null; // Handle empty strings as null, or throw exception if required
        }
        try {
            return LocalDateTime.parse(value.trim(), FORMATTER);
        } catch (DateTimeParseException e) {
            throw new CsvDataTypeMismatchException(value, field.getType(),
                    "Failed to parse date-time: '" + value + "'. Expected format: " + FORMATTER.toString());
        }
    }

    @Override
    protected String convertToWrite(Object value) throws CsvDataTypeMismatchException {
        if (value == null) {
            return "";
        }
        if (!(value instanceof LocalDateTime)) {
            throw new CsvDataTypeMismatchException("Cannot convert " + value.getClass().getSimpleName() + " to CSV string. Expected LocalDateTime.");
        }
        return ((LocalDateTime) value).format(FORMATTER);
    }
}
