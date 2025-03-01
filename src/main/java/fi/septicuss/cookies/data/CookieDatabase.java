package fi.septicuss.cookies.data;

import fi.septicuss.cookies.data.cookie.CookieData;

public interface CookieDatabase {

    void initialize();

    void put(String key, CookieData cookieData);

    CookieData get(String key);

    void delete(String key);

    boolean connected();

}
