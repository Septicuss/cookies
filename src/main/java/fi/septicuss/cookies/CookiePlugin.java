package fi.septicuss.cookies;

import com.github.retrooper.packetevents.PacketEvents;
import com.jeff_media.customblockdata.CustomBlockData;
import fi.septicuss.cookies.command.CookiesCommand;
import fi.septicuss.cookies.data.CookieDatabase;
import fi.septicuss.cookies.data.SQLiteCookieDatabase;
import fi.septicuss.cookies.listener.CookieBlockListener;
import fi.septicuss.cookies.listener.UiListener;
import fi.septicuss.cookies.manager.CookieBlockManager;
import fi.septicuss.cookies.manager.CookieDataManager;
import fi.septicuss.cookies.manager.UiManager;
import fi.septicuss.cookies.ui.title.TitleHandler;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class CookiePlugin extends JavaPlugin {

    private static CookiePlugin instance;

    private UiManager uiManager;
    private TitleHandler titleHandler;
    private CookieBlockManager cookieBlockManager;
    private CookieDataManager cookieDataManager;

    public static CookiePlugin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        final File databaseFile = this.getDatabaseFile();
        final CookieDatabase database = new SQLiteCookieDatabase(databaseFile.getAbsolutePath());
        database.initialize();

        this.uiManager = new UiManager(this);
        this.titleHandler = new TitleHandler();
        this.cookieDataManager = new CookieDataManager(database);
        this.cookieBlockManager = new CookieBlockManager(this);

        this.titleHandler.initialize();
        this.uiManager.initialize();

        this.registerEvents();
        this.registerCommand();

        CustomBlockData.registerListener(this);
    }

    @Override
    public void onDisable() {
        this.uiManager.closeAll();
        this.titleHandler.uninitialize();
    }

    private void registerCommand() {
        final CookiesCommand command = new CookiesCommand(this.cookieBlockManager);
        final PluginCommand pluginCommand = this.getCommand("cookies");
        assert pluginCommand != null;
        pluginCommand.setExecutor(command);
        pluginCommand.setTabCompleter(command);
    }

    private void registerEvents() {
        final var pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new UiListener(this.uiManager), this);
        pluginManager.registerEvents(new CookieBlockListener(this.cookieBlockManager), this);
    }

    private File getDatabaseFile() {
        final File databaseFile = new File(this.getDataFolder(), "data.sqlite");
        if (!databaseFile.exists()) {
            databaseFile.getParentFile().mkdirs();
            try {
                databaseFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return databaseFile;
    }

    public CookieBlockManager getCookieBlockManager() {
        return cookieBlockManager;
    }

    public CookieDataManager getCookieDataManager() {
        return cookieDataManager;
    }

    public UiManager getUiManager() {
        return uiManager;
    }

    public TitleHandler getTitleHandler() {
        return titleHandler;
    }
}
