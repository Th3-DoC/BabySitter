package th3doc.babysitter.config;

public enum Config {
    /**
     *
     * FILES.YML
     *
     */
    _config("config.yml"),
    _invConfig("invConfig.yml"),
    _adminConfig("adminConfig.yml"),
    _playerConfig("playerConfig.yml"),
    _playerListConfig("playerList.yml"),
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
    _adminRanks("adminRanks"),
    _safeBlockPlace("safeBlockPlace"),
    _safeBlocks("safeBlocks"),
    _safeItemDrop("safeItemDrops"),
    /**
     * ADMIN CONFIG
     */
    _states("states"),
    _adminState("adminState"),
    _flyState("flyState"),
    _vanishState("vanishState"),
    _forceSpectate("forceSpectate"),
    _adminFlight("adminFlight"),
    /**
     * INV CONFIG
     */
    _survivalInv("survivalInv"),
    _babysitInv("babysitInv"),
    _inv("inv"),
    _armour("armour"),
    _eChest("eChest"),
    _invBypass("invBypass"),
    _edited("edited"),
    /**
     * PLAYER CONFIG
     */
    _playerList("playerList"),
    _playerName("name"),
    _playerUUID("UUID"),
    _joinDate("joinDate"),
    _survivalLocation("survivalLocation");

    public String txt;

    Config(String txt) {
        this.txt = txt;
    }
}
