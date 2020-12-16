package th3doc.babysitter.player.rewards;

import th3doc.babysitter.player.PlayerHandler;

public class Rewards {
    
    //VARIABLES
    private PlayerHandler player;
    private final RewardsConfig config;
    
    
    //CONSTRUCTOR
    public Rewards(PlayerHandler player)
    {
        this.player = player;
        this.config = new RewardsConfig(player);
        
    }
}
