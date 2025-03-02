package fi.septicuss.cookies.utils;

import fi.septicuss.cookies.data.cookie.CookieData;
import fi.septicuss.cookies.data.cookie.CookieUpgrade;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class CookieFontUtils {

    private static final Key MENU_FONT = Key.key("cookies", "menu");
    private static final DecimalFormatSymbols DECIMAL_FORMAT_SYMBOLS = new DecimalFormatSymbols(Locale.US);

    private static final Map<Integer, Character> OFFSET_MAP;
    private static final Map<Character, Integer> WIDTH_MAP;

    static {
        OFFSET_MAP = Map.ofEntries(
                Map.entry(-1, '\uE001'),
                Map.entry(-2, '\uE002'),
                Map.entry(-4, '\uE003'),
                Map.entry(-8, '\uE004'),
                Map.entry(-16, '\uE005'),
                Map.entry(-32, '\uE006'),
                Map.entry(1, '\uE009'),
                Map.entry(2, '\uE010'),
                Map.entry(4, '\uE011'),
                Map.entry(8, '\uE012'),
                Map.entry(16, '\uE013'),
                Map.entry(32, '\uE014')
        );

        WIDTH_MAP = Map.ofEntries(
                Map.entry('1', 5),
                Map.entry('2', 5),
                Map.entry('3', 5),
                Map.entry('4', 5),
                Map.entry('5', 5),
                Map.entry('6', 5),
                Map.entry('7', 5),
                Map.entry('8', 5),
                Map.entry('9', 5),
                Map.entry('0', 5),
                Map.entry('.', 1),
                Map.entry('k', 5),
                Map.entry('m', 5),
                Map.entry('\uF001', 176),
                Map.entry('\uF002', 34),
                Map.entry('\uF003', 34),
                Map.entry('\uF004', 34),
                Map.entry('\uF005', 34),
                Map.entry('\uF006', 34),
                Map.entry('\uF007', 34)
        );
    }

    /**
     * @return A rendered adventure component used in an inventory title
     */
    public static Component render(final CookieData cookieData) {
        final long cookies = cookieData.getCookies();

        final boolean showBiscuit = canAfford(CookieUpgrade.BISCUIT, cookieData);
        final boolean showOven = canAfford(CookieUpgrade.OVEN, cookieData);
        final boolean showFactory = canAfford(CookieUpgrade.FACTORY, cookieData);

        final List<FontElement> elements = List.of(
                menuElement(),
                numberElement(cookies),
                upgradeElement(showBiscuit, showOven, showFactory)
        );

        final StringBuilder builder = new StringBuilder();
        builder.append(offset(-8));

        for (FontElement element : elements) {
            builder.append(element.element);
            builder.append(offset(-element.width));
        }

        return Component.text(builder.toString())
                .color(NamedTextColor.WHITE)
                .font(MENU_FONT);
    }

    private static FontElement menuElement() {
        return new FontElement("\uF001", WIDTH_MAP.get('\uF001'));
    }

    private static FontElement numberElement(long cookies) {
        final String numberString = formatNumber(cookies);
        final int width = width(numberString);
        final int inset = 164 - width;
        return new FontElement(offset(inset) + numberString, inset + width);
    }

    private static FontElement upgradeElement(boolean... upgrades) {
        final StringBuilder builder = new StringBuilder();

        final int shift = 7; // Shift from beginning of the menu to the right
        final int iconWidth = WIDTH_MAP.get('\uF002'); // All icons have the same width
        final String iconOffset = offset(-iconWidth - 1);

        builder.append(offset(shift));

        CookieUpgradeIcon[] icons = CookieUpgradeIcon.values();
        for (int i = 0; i < icons.length; i++) {
            final boolean available = upgrades[i];
            final boolean last = i == icons.length - 1;
            builder.append(available ? icons[i].available : icons[i].unavailable);
            if (!last) builder.append(iconOffset);
        }

        final int width = icons.length * iconWidth + icons.length;
        return new FontElement(builder.toString(), width);
    }

    /**
     * @return A formatted string for the given number, with support for k (thousand) and m (million) units
     */
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

        DecimalFormat format = new DecimalFormat(value % 1 == 0 ? "0" : "0.#", DECIMAL_FORMAT_SYMBOLS);
        return format.format(value) + suffix;
    }

    /**
     * @return An offset font string, with the given pixel width
     */
    private static String offset(int pixels) {
        if (pixels == 0) return "";

        StringBuilder builder = new StringBuilder();
        boolean negative = pixels < 0;
        pixels = Math.abs(pixels);

        while (pixels > 0) {
            int highestBit = Math.min(Integer.highestOneBit(pixels), 32);
            builder.append(OFFSET_MAP.get(negative ? -highestBit : highestBit));
            pixels -= highestBit;
        }

        return builder.toString();
    }

    /**
     * @return The pixel width of the value
     */
    private static int width(String value) {
        int width = value.length();
        for (char character : value.toCharArray()) {
            width += WIDTH_MAP.getOrDefault(character, 0);
        }
        return width;
    }

    private static boolean canAfford(CookieUpgrade upgrade, CookieData cookieData) {
        final int nextLevel = cookieData.getUpgrades().getOrDefault(upgrade, 0) + 1;
        return (cookieData.getCookies() >= upgrade.getPrice(nextLevel));
    }

    private enum CookieUpgradeIcon {
        BISCUIT('\uF002', '\uF005'),
        OVEN('\uF003', '\uF006'),
        FACTORY('\uF004', '\uF007');

        public final char available;
        public final char unavailable;

        CookieUpgradeIcon(char available, char unavailable) {
            this.available = available;
            this.unavailable = unavailable;
        }
    }

    private record FontElement(String element, int width) {
    }

}
