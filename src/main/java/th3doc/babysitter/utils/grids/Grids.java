package th3doc.babysitter.utils.grids;

import th3doc.babysitter.Main;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Grids {
    final private Main main;
    final private HashMap<String, Set<Cuboid>> grids;
    
    public Grids(Main main) {
        this.main = main;
        this.grids = new HashMap<>();
    }
    
    public void addGrid(UUID uuid, Cuboid cuboid) {
        Set<Cuboid> cuboids = new HashSet<>();
        if(grids.containsKey(uuid.toString())) {
            cuboids = grids.get(uuid.toString());
        }
        cuboids.add(cuboid);
        grids.put(uuid.toString(), cuboids);
    }
}
