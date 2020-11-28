package th3doc.babysitter.enums;

public enum Perm {

    _opAdmin("babysit.op.admin"),
    _babysitCommand("babysit.command"),
    _vanishCommand("vanish.command"),
    _godCommand("god.command"),
    _flyCommand("fly.command"),
    _forceSpectator("force.spectator"),
    _invSee("inv.see.command"),
    /**
     * Minecraft Permissions
     */
    _giveCommand("minecraft.command.give");

    public String txt;

    Perm(String txt) {
        this.txt = txt;
    }
}
