package th3doc.babysitter.player;

import th3doc.babysitter.Main;

public class PlayerHandler {

    //CONSTRUCTOR
    private AdminPlayer adminPlayer;
    public AdminPlayer admin() { return adminPlayer; }
    public PlayerHandler(Main main)
    {
        adminPlayer = new AdminPlayer(main);
    }
}
