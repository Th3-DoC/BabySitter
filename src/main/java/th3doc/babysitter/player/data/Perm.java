package th3doc.babysitter.player.data;

public enum Perm {

    _opAdmin("babysitter.admin.*"),
    _babysitCommand("babysitter.command.bs"),
    _vanishCommand("babysitter.command.vanish"),
    _godCommand("babysitter.command.god"),
    _flyCommand("babysitter.command.fly"),
    _invSee("babysitter.command.see"),
    _invEdit("babysitter.command.edit"),
    _give("babysitter.command.give"),
    _forceSpectator("babysitter.bypass.spec"),
    _giveBypass("babysitter.bypass.give"),
    _tpBypass("babysitter.bypass.tp"),
    /**
     * Minecraft Permissions
     */
    _giveCommand("minecraft.command.give");

    public String txt;

    Perm(String txt) {
        this.txt = txt;
    }
}
