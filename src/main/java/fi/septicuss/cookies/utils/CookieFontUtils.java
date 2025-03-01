package fi.septicuss.cookies.utils;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class CookieFontUtils {



    public static Component render(long cookies) {

        return Component.text("\uF000\uF001\uF002")
                .color(NamedTextColor.WHITE)
                .font(Key.key("cookies", "menu"))
                .append(Component.text(formatNumber(cookies))
                        .color(NamedTextColor.WHITE)
                        .font(Key.key("cookies", "numbers")));

    }

    public static String formatNumber(long number) {
        if (number < 1000) return String.valueOf(number);

        double value;
        String suffix;

        if (number < 1_000_000) {
            value = number / 1000.0;
            suffix = "k";
        } else {
            value = number / 1_000_000.0;
            suffix = "m";
        }

        // Format to max 2 decimal places
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat df = new DecimalFormat(value % 1 == 0 ? "0" : "0.#", symbols);
        return df.format(value) + suffix;
    }

}
