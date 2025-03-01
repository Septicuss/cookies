package fi.septicuss.cookies.data.cookie;

import java.util.EnumMap;

public class CookieData {

    private long cookies;
    private long lastAccessed;
    private final EnumMap<CookieUpgrade, Integer> upgrades;

    public CookieData() {
        this.cookies = 0;
        this.lastAccessed = System.currentTimeMillis();
        this.upgrades = new EnumMap<>(CookieUpgrade.class);
    }

    public CookieData(long cookies, long lastAccessed, EnumMap<CookieUpgrade, Integer> upgrades) {
        this.cookies = cookies;
        this.lastAccessed = lastAccessed;
        this.upgrades = upgrades;
    }

    public long getCookies() {
        return cookies;
    }

    public void setCookies(long cookies) {
        this.cookies = cookies;
    }

    public long getLastAccessed() {
        return lastAccessed;
    }

    public void updateLastAccessed() {
        this.lastAccessed = System.currentTimeMillis();
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
