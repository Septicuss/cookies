package fi.septicuss.cookies.data.cookie;

public enum CookieUpgrade {
    PLANT(1),
    TREE(5),
    FARM(20);


    private final int cookiesPerSecond;

    CookieUpgrade(int cookiesPerSecond) {
        this.cookiesPerSecond = cookiesPerSecond;
    }

    public int getCookiesPerSecond() {
        return cookiesPerSecond;
    }
}
