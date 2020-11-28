package th3doc.babysitter.config;

public enum Config {
    /**
     *
     * FILES.YML
     *
     */
    _config("config.yml"),
    _invConfig("invConfig"),
    /**
     * FOLDERS
     */
    _playerData("playerData"),
    /**
     * DEFAULT CONFIG
     */
    _allowCreative("allowCreative"),
    _allowGive("allowGive"),
    _adminGive("adminGive"),
    _adminPermissionList("adminPermissionList"),
    _specialPermissionList("specialPermissionList"),
    _specialPermsAlways("specialPermsAlways"),
    _permission("permission"),
    _value("value"),
    _specialPermissions("specialPermissions"),
    _specialRanks("specialRanks"),
    _adminRank("adminRank"),
    /**
     * ADMIN CONFIG
     */
    _adminState("adminState"),
    _forceSpectate("forceSpectate"),
    _vanishState("vanishState"),
    _adminFlight("adminFlight"),
    _survivalInv("survivalInv"),
    _babysitInv("babysitInv"),
    _inv("inv"),
    _armour("armour"),
    _eChest("eChest"),
    _flyState("flyState"),
    _survivalLocation("survivalLocation");

    public String txt;

    Config(String txt) {
        this.txt = txt;
    }
}
