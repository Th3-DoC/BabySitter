package th3doc.babysitter.player.data;

import org.bukkit.ChatColor;

public enum Chat {
    /**
     * CONFIG
     */
    _onEnable(ChatColor.GREEN + "" + ChatColor.ITALIC + "BabySitter Enabled !"),
    _configMissing(ChatColor.GREEN + "" + ChatColor.ITALIC + "Configuration file not found! Creating file."),
    _configFound(ChatColor.GREEN + "" + ChatColor.ITALIC + "Configuration file found! Loading file."),
    /**
     * BABYSITTER
     */
    _babySittingTime(ChatColor.GREEN + "" + ChatColor.ITALIC + "BabySitting Time!"),
    _babySittingDone(ChatColor.GREEN + "" + ChatColor.ITALIC + "BabySitting Done, For Now..."),
    _godOff(ChatColor.GREEN + "" + ChatColor.ITALIC + "GodMode OFF"),
    _godOn(ChatColor.GREEN + "" + ChatColor.ITALIC + "GodMode ON"),
    _flyOn(ChatColor.GREEN + "" + ChatColor.ITALIC + "FlyMode ON"),
    _flyOff(ChatColor.GREEN + "" + ChatColor.ITALIC + "FlyMode OFF"),
    _vanishOn(ChatColor.YELLOW + "PSSSST Vanish Is Still Active XD"),
    _fakeLogOut(" left the game"),//Chat Colour Handled by before p.getname()
    _fakeLogIn(" joined the game"),
    /**
     * PERMISSION
     */
    _noConsole(ChatColor.RED + "" + ChatColor.ITALIC + "This Command Cannot Be Run From Console!"),
    _cancelBlockPlace(ChatColor.RED + "" + ChatColor.ITALIC + "That Is Not A Safe Block To Place There."),
    _invalidGive(ChatColor.RED + "" + ChatColor.ITALIC + "Invalid Arguments <USE>/give <player> <item> <count>"),
    _invalidViewerCommand(ChatColor.RED + "" + ChatColor.ITALIC + "Invalid Selection <USE>/see <player> <inventoryType> <edit>"),
    _creativeDisabled(ChatColor.RED + "" + ChatColor.ITALIC + "Creative Game Mode Disabled."),
    _giveDisabled(ChatColor.RED + "" + ChatColor.ITALIC + "Give Command is Disabled."),
    _adminInSurvival(ChatColor.RED + "" + ChatColor.ITALIC + "You Can Not /Give Admin's In Survival."),
    _noSLoc(ChatColor.RED + "" + ChatColor.ITALIC + "Error Loading Survival Location, Please Advise An Admin."),
    _tpAdminOnly(ChatColor.RED + "" + ChatColor.ITALIC + "You May Only Teleport Yourself."),
    _cancelItemDrop(ChatColor.RED + "" + ChatColor.ITALIC + "It Is Not Safe To Drop That Item."),
    _targetInvalid(ChatColor.RED + "" + ChatColor.ITALIC + "Invalid Target. <USE>/bs <player>");

    public String txt;

    Chat(String txt) {
        this.txt = txt;
    }
}
