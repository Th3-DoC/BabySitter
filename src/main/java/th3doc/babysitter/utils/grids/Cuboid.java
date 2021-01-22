package th3doc.babysitter.utils.grids;

import org.bukkit.Location;
import th3doc.babysitter.entities.player.BasicPlayer;

import java.util.*;

public class Cuboid {
    public enum Direction {
        UP,
        DOWN,
        NORTH,
        SOUTH,
        EAST,
        WEST
    }
    
    
    //player who owns the region? seperate class?
    final private BasicPlayer player;
    final private List<UUID> trusted;
    private int xMin;
    private int xMax;
    private int yMin;
    private int yMax;
    private int zMin;
    private int zMax;
    
    public Cuboid(BasicPlayer player, Location c1, Location c2) throws IllegalArgumentException {
        if(c1.getWorld() == c2.getWorld()) { //initial size of no more then 500 blocks? expand from there ?
            xMin = Math.min(c1.getBlockX(), c2.getBlockX());
            xMax = Math.max(c1.getBlockX(), c2.getBlockX());
            yMin = Math.min(c1.getBlockY(), c2.getBlockY());
            yMax = Math.max(c1.getBlockY(), c2.getBlockY());
            zMin = Math.min(c1.getBlockZ(), c2.getBlockZ());
            zMax = Math.max(c1.getBlockZ(), c2.getBlockZ());
        } else {
            throw new IllegalArgumentException();
        }
        this.player = player;
        this.trusted = new ArrayList<>();
    }
    
    public boolean contains(Location loc) {
        return loc.getBlockX() >= xMin && loc.getBlockX() <= xMax && loc.getBlockY() >= yMin && loc.getBlockY() <= yMax && loc.getBlockZ() >= zMin && loc.getBlockZ() <= zMax;
    }
    
    public void changeSize(Location playerLoc, Direction direction, int amount) {
        if(contains(playerLoc)) {
            //shrink
        } else {
            //check player locat
            //expand
        }
    }
    
    public void expand(Direction direction, int amount) {
        switch(direction) {
            case UP:
                //move y+
                yMax += amount;
            case DOWN:
                //move y-
                yMin -= amount;
            case NORTH:
                //move z-
                zMin -= amount;
            case SOUTH:
                //move z+
                zMax += amount;
            case EAST:
                //move x+
                xMax += amount;
            case WEST:
                //move x-
                xMin -= amount;
            default: player.message("Invalid Direction");
        }
    }
    
    public void buildHollow() {
    
    }
    public void showBoarders() {
    
    }
}
