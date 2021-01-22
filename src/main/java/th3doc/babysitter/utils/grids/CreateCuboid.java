package th3doc.babysitter.utils.grids;

import org.bukkit.Location;
import th3doc.babysitter.Main;
import th3doc.babysitter.entities.player.BasicPlayer;

public class CreateCuboid {
    final private Main main;
    final private BasicPlayer player;
    private Location loc;
    
    public CreateCuboid(Main main, BasicPlayer player) {
        this.main = main;
        this.player = player;
        this.loc = null;
    }
    
    public boolean createCuboid(Location loc) {
        if(this.loc == null) { this.loc = loc; return false; }
        else {
            Location loc1 = this.loc;
            Cuboid cuboid = new Cuboid(player, loc, loc1);
            this.loc = null;
            return true;
        }
    }
}
