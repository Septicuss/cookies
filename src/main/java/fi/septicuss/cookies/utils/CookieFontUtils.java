package fi.septicuss.cookies.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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

public class CookieFontUtils {

    private record FontElement(String element, int width) {}

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

    private static final String NAMESPACE = "cookies";
    private static final Key MENU_FONT = Key.key(NAMESPACE, "menu");
    private static final Map<Integer, Character> OFFSET_MAP;
    private static final Map<Character, Integer> WIDTH_MAP;

    static {
        OFFSET_MAP = Maps.newHashMap();
        OFFSET_MAP.put(-1, '\uE001');
        OFFSET_MAP.put(-2, '\uE002');
        OFFSET_MAP.put(-4, '\uE003');
        OFFSET_MAP.put(-8, '\uE004');
        OFFSET_MAP.put(-16, '\uE005');
        OFFSET_MAP.put(-32, '\uE006');
        OFFSET_MAP.put(1, '\uE009');
        OFFSET_MAP.put(2, '\uE010');
        OFFSET_MAP.put(4, '\uE011');
        OFFSET_MAP.put(8, '\uE012');
        OFFSET_MAP.put(16, '\uE013');
        OFFSET_MAP.put(32, '\uE014');

        WIDTH_MAP = Maps.newHashMap();
        WIDTH_MAP.put('1', 5);
        WIDTH_MAP.put('2', 5);
        WIDTH_MAP.put('3', 5);
        WIDTH_MAP.put('4', 5);
        WIDTH_MAP.put('5', 5);
        WIDTH_MAP.put('6', 5);
        WIDTH_MAP.put('7', 5);
        WIDTH_MAP.put('8', 5);
        WIDTH_MAP.put('9', 5);
        WIDTH_MAP.put('0', 5);
        WIDTH_MAP.put('.', 1);
        WIDTH_MAP.put('k', 5);
        WIDTH_MAP.put('m', 5);
        WIDTH_MAP.put('\uF001', 176);
        WIDTH_MAP.put('\uF002', 34);
        WIDTH_MAP.put('\uF003', 34);
        WIDTH_MAP.put('\uF004', 34);
        WIDTH_MAP.put('\uF005', 34);
        WIDTH_MAP.put('\uF006', 34);
        WIDTH_MAP.put('\uF007', 34);
    }


    public static Component render(final CookieData cookieData) {
        final long cookies = cookieData.getCookies();
        final var upgrades = cookieData.getUpgrades();
        final boolean showBiscuit = cookies >= CookieUpgrade.BISCUIT.getPrice(upgrades.getOrDefault(CookieUpgrade.BISCUIT, 0) + 1);
        final boolean showOven = cookies >= CookieUpgrade.OVEN.getPrice(upgrades.getOrDefault(CookieUpgrade.OVEN, 0) + 1);
        final boolean showFactory = cookies >= CookieUpgrade.FACTORY.getPrice(upgrades.getOrDefault(CookieUpgrade.FACTORY, 0) + 1);

        final List<FontElement> elements = Lists.newArrayList();
        elements.add(menu());
        elements.add(number(cookies));
        elements.add(upgrades(showBiscuit, showOven, showFactory));

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

    public static int width(String value) {
        int width = value.length();
        for (char character : value.toCharArray()) {
            width += WIDTH_MAP.get(character);
        }
        return width;
    }

    private static FontElement menu() {
        return new FontElement("\uF001", WIDTH_MAP.get('\uF001'));
    }

    private static FontElement number(long cookies) {
        final int prefixOffset = 102;
        final String numberString = formatNumber(cookies);
        final int width = prefixOffset + width(numberString);
        return new FontElement(offset(prefixOffset) + numberString, width);
    }

    private static FontElement upgrades(boolean first, boolean second, boolean third) {
        final StringBuilder builder = new StringBuilder();
        final int icons = 3;
        final int shift = 7;
        final int iconWidth = WIDTH_MAP.get('\uF002');
        final String iconOffset = offset(-iconWidth - 1);
        builder
                .append(offset(shift))
                .append((first ? CookieUpgradeIcon.BISCUIT.available : CookieUpgradeIcon.BISCUIT.unavailable))
                .append(iconOffset)
                .append((second ? CookieUpgradeIcon.OVEN.available : CookieUpgradeIcon.OVEN.unavailable))
                .append(iconOffset)
                .append((third ? CookieUpgradeIcon.FACTORY.available : CookieUpgradeIcon.FACTORY.unavailable));
        return new FontElement(builder.toString(), iconWidth * icons + icons);
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

    public static String offset(int pixels) {
        final StringBuilder builder = new StringBuilder();
        if (pixels == 0)
            return builder.toString(); // Return empty string

        final boolean negative = Integer.signum(pixels) == -1;
        pixels = Math.abs(pixels); // Get negative pixels absolute value

        while (pixels > 0) {
            int highestBit = Integer.highestOneBit(pixels);
            if (highestBit > 32)
                highestBit = 32; // Max is 128
            builder.append(OFFSET_MAP.get(negative ? -highestBit : highestBit));

            pixels -= highestBit;
        }

        return builder.toString();
    }

}
