package fi.septicuss.cookies.command;

import fi.septicuss.cookies.manager.CookieBlockManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public class CookiesCommand implements CommandExecutor, TabCompleter {

    private final CookieBlockManager cookieBlockManager;

    public CookiesCommand(CookieBlockManager cookieBlockManager) {
        this.cookieBlockManager = cookieBlockManager;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!checkPermission(commandSender)) {
            commandSender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(commandSender);
            return true;
        }

        final String firstArgument = args[0];

        if (firstArgument.equalsIgnoreCase("get")) {
            if (!(commandSender instanceof Player player)) {
                commandSender.sendMessage(ChatColor.RED + "You must be a player to get a cookie block.");
                return true;
            }

            this.giveCookieBlock(player);
            return true;
        }


        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        if (!checkPermission(commandSender)) {
            return null;
        }

        if (args.length == 1) {
            return List.of("get");
        }

        return List.of();
    }

    private void giveCookieBlock(Player player) {
        player.getInventory().addItem(this.cookieBlockManager.getCookieBlockItem());
    }


    private void sendHelp(CommandSender commandSender) {
        commandSender.sendMessage(ChatColor.GREEN + "Cookies");
        commandSender.sendMessage(ChatColor.GREEN + "/cookies get " + ChatColor.GRAY + "Receive a cookie block");
    }

    private boolean checkPermission(CommandSender sender) {
        return true;
    }

}
