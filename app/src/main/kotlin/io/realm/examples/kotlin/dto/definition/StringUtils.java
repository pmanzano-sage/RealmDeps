package io.realm.examples.kotlin.dto.definition;

public final class StringUtils {

    private StringUtils() {
    }

    /**
     * Returns {@code true} if the given string is null or empty. Returns {@code false} otherwise.
     *
     * @param text
     * @return
     */
    public static boolean isEmpty(final String text) {
        return text == null || text.trim().isEmpty();
    }

    /**
     * Compares two {@link String}s, ignoring the case, and also make sure null values are compared as well.
     *
     * @param string1
     * @param string2
     * @return
     */
    public static int compareToNullSafe(final String string1, final String string2) {
        return compareToNullSafe(string1, string2, true);
    }

    /**
     * Compares two {@link String}s and also make sure null values are compared as well.
     *
     * @param string1
     * @param string2
     * @param ignoreCase
     * @return
     */
    public static int compareToNullSafe(final String string1, final String string2, final boolean ignoreCase) {
        if (string1 == null ^ string2 == null) {
            return (string1 == null) ? -1 : 1;
        }

        if (string1 == null) {
            return 0;
        }

        if (ignoreCase) {
            return string1.compareToIgnoreCase(string2);
        } else {
            return string1.compareTo(string2);
        }
    }

    /**
     * @param text
     * @return
     */
    public static String firstLetterUppercase(final String text) {
        if (isEmpty(text)) {
            return null;
        }

        final StringBuilder builder = new StringBuilder(text.substring(0, 1).toUpperCase());
        if (text.length() > 1) {
            builder.append(text.substring(1));
        }

        return builder.toString();
    }

    /**
     * @param taxNumber
     * @return taxNumber if it is not null, otherwise an empty String.
     */
    public static String emptyStringIfNull(final String taxNumber) {
        return taxNumber == null ? "" : taxNumber;
    }

    /**
     * Returns a {@link String} truncated if its size is > than the given {@code limit}.
     *
     * @param limit Maximum length of the given string
     * @return
     */
    public static String truncate(final String text, final int limit) {
        return isEmpty(text) ? null : text.substring(0, Math.min(text.length(), limit));
    }
}
