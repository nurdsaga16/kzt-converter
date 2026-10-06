import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.TreeMap;

public class Converter {

    static final Map<String, BigDecimal> KZT_PER_UNIT = new TreeMap<>(Map.of(
            "KZT", new BigDecimal("1"),
            "USD", new BigDecimal("500"),
            "EUR", new BigDecimal("550"),
            "RUB", new BigDecimal("6")
    ));

    public static boolean supports(String currency) {
        return currency != null && KZT_PER_UNIT.containsKey(currency.toUpperCase());
    }

    public static BigDecimal convert(String from, String to, BigDecimal amount) {
        if (!supports(from) || !supports(to)) {
            throw new IllegalArgumentException("unsupported currency");
        }
        BigDecimal inKzt = amount.multiply(KZT_PER_UNIT.get(from.toUpperCase()));
        return inKzt.divide(KZT_PER_UNIT.get(to.toUpperCase()), 2, RoundingMode.HALF_UP);
    }

    public static String ratesText() {
        StringBuilder sb = new StringBuilder();
        KZT_PER_UNIT.forEach((code, rate) -> sb.append(code).append(' ').append(rate.toPlainString()).append('\n'));
        return sb.toString();
    }
}
