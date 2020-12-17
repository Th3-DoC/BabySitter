package th3doc.babysitter.player.data;

public enum Perm {

    _babysitCommand("babysitter.command.bs"),
    _vanishCommand("babysitter.command.vanish"),
    _godCommand("babysitter.command.god"),
    _flyCommand("babysitter.command.fly"),
    _giveCommand("babysitter.command.give"),
    _invSeeCommand("babysitter.command.see"),
    _spawnCommand("babysitter.command.spawn"),
    _invEdit("babysitter.command.edit"),
    _forceSpectator("babysitter.bypass.spec"),
    _blockPlaceBypass("babysitter.bypass.blocks"),
    _giveBypass("babysitter.bypass.give"),
    _tpBypass("babysitter.bypass.tp"),
    _creativeBypass("babysitter.bypass.creative"),
    _permBypass("babysitter.bypass.perms"),
    _invBypass("babysitter.bypass.inv"),
    _seeBypass("babysitter.bypass.see"),
    _itemDropBypass("babysitter.bypass.item"),
    _flyBypass("babysitter.bypass.flight"),
    _vanishBypass("babysitter.bypass.vanish");

    public String txt;

    Perm(String txt) {
        this.txt = txt;
    }
}
