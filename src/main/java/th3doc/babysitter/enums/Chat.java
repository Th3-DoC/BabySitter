package th3doc.babysitter.enums;

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
    _fakeLogOut(" left the game"),
    _fakeLogIn(" joined the game"),
    /**
     * PERMISSION
     */
    _noConsole(ChatColor.RED + "This Command Cannot Be Run From Console!"),
    _invalidGive("Invalid Arguments <USE>/give <player> <item> <count>"),
    _invalidViewerCommand(ChatColor.RED + "Invalid Selection <USE>/see <player> <inventoryType> <edit>"),
    _creativeDisabled(ChatColor.RED + "Creative Game Mode Disabled."),
    _giveDisabled(ChatColor.RED + "Give Command is Disabled."),
    _adminInSurvival(ChatColor.RED + "You Can Not /Give Admin's In Survival."),
    _noSLoc(ChatColor.RED + "Error Loading Survival Location, Please Advise An Admin."),
    _targetInvalid(ChatColor.RED + "Invalid Target. <USE>/bs <player>");

    public String txt;

    Chat(String txt) {
        this.txt = txt;
    }
}
