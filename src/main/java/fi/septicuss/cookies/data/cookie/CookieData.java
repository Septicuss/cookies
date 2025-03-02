package fi.septicuss.cookies.data.cookie;

import java.util.EnumMap;
import java.util.concurrent.atomic.AtomicLong;

public class CookieData {

    private final AtomicLong cookies;
    private final EnumMap<CookieUpgrade, Integer> upgrades;

    public CookieData() {
        this.cookies = new AtomicLong(0);
        this.upgrades = new EnumMap<>(CookieUpgrade.class);
    }

    public CookieData(long cookies, EnumMap<CookieUpgrade, Integer> upgrades) {
        this.cookies = new AtomicLong(cookies);
        this.upgrades = upgrades;
    }

    public long getCookies() {
        return cookies.get();
    }

    public void setCookies(long cookies) {
        this.cookies.set(cookies);
    }

    public EnumMap<CookieUpgrade, Integer> getUpgrades() {
        return upgrades;
    }

    public long getCookiesPerSecond() {
        if (this.upgrades.isEmpty()) return 0;
        return this.upgrades.entrySet().stream()
                .mapToLong(entry -> entry.getKey().getCookiesPerSecond() * entry.getValue().longValue())
                .sum();
    }

    public void addUpgrade(CookieUpgrade upgrade) {
        this.upgrades.merge(upgrade, 1, Integer::sum);
    }

    public void removeUpgrade(CookieUpgrade upgrade) {
        this.upgrades.merge(upgrade, -1, (prev, sub) -> Math.max(prev + sub, 0));
    }



}
