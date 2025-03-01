package fi.septicuss.cookies.manager;

import fi.septicuss.cookies.data.cookie.CookieData;
import fi.septicuss.cookies.data.CookieDatabase;

import java.util.UUID;

public class CookieDataManager {

    private final CookieDatabase database;

    public CookieDataManager(CookieDatabase database) {
        this.database = database;
    }

    public CookieData get(final UUID id) {
        if (!connected()) return new CookieData();


        CookieData retrievedData = this.database.get(id.toString());
        if (retrievedData == null) {
            retrievedData = new CookieData();
        }

        return retrievedData;
    }

    public void save(final UUID id, final CookieData cookieData) {
        if (!connected()) return;
        this.database.put(id.toString(), cookieData);
    }

    public boolean connected() {
        return this.database.connected();
    }


}
