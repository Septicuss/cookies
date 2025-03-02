package fi.septicuss.cookies.utils;

import fi.septicuss.cookies.data.cookie.CookieData;
import fi.septicuss.cookies.data.cookie.CookieUpgrade;
import mjson.Json;

import java.util.EnumMap;
import java.util.Map;

public class CookieSerializationUtils {

    public static String serializeToJson(CookieData data) {
        final long cookies = data.getCookies();
        final EnumMap<CookieUpgrade, Integer> upgrades = data.getUpgrades();

        final Json upgradesJson = Json.object();

        for (Map.Entry<CookieUpgrade, Integer> entry : upgrades.entrySet()) {
            upgradesJson.set(entry.getKey().toString(), entry.getValue());
        }

        return Json.object()
                .set("cookies", cookies)
                .set("upgrades", upgradesJson)
                .toString();
    }

    public static CookieData deserializeFromJson(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }

        final Json object = Json.read(json);

        final long cookies = object.at("cookies").asLong();
        final EnumMap<CookieUpgrade, Integer> upgrades = new EnumMap<>(CookieUpgrade.class);

        object.at("upgrades")
                .asMap()
                .forEach((key, value) -> {
                    for (CookieUpgrade upgrade : CookieUpgrade.values())
                        if (upgrade.name().equals(key.toUpperCase()))
                            upgrades.put(CookieUpgrade.valueOf(key), Integer.parseInt(value.toString()));
                }
        );

        return new CookieData(cookies, upgrades);
    }

}
