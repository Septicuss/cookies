package fi.septicuss.cookies.data.cookie;

public enum CookieUpgrade {

    BISCUIT(1, 20),
    OVEN(5, 100),
    FACTORY(20, 1000);

    private static final float GROWTH_FACTOR = 1.3f;

    private final int cookiesPerSecond;
    private final int basePrice;

    CookieUpgrade(int cookiesPerSecond, int price) {
        this.cookiesPerSecond = cookiesPerSecond;
        this.basePrice = price;
    }

    public int getCookiesPerSecond() {
        return cookiesPerSecond;
    }

    public int getPrice(int level) {
        return (int) (basePrice * Math.pow(GROWTH_FACTOR, level));
    }
}
