package th3doc.babysitter.utils.debug;

import th3doc.babysitter.Main;
import th3doc.babysitter.entities.npc.Entities;
import th3doc.babysitter.utils.config.Config;

public class Debug
{
    
    
    public enum Paths
    {
        DEBUG_YML("Debug.yml", "debug."),
        DEBUG_ALL("Debug_ALL", ""),
        DEBUG_CMDS("Debug_Cmds", "debug.debug-cmds"),
        DEBUG_UTILS("Debug_Utils", "debug.debug-utils"),
        DEBUG_PLAYER("Debug_Player", "debug.debug-player"),
        DEBUG_ENTITY("Debug_Entity", "debug.debug-entity"),
        DEBUG_EVENTS("Debug_Events", "debug.debug-events"),
        DEBUG_REWARDS("Debug_Rewards", "debug.debug-rewards");
    
        public String txt;
        public String path;
        Paths(String txt, String path)
        {
            this.txt = txt;
            this.path = path;
        }
    }
    
    //VARS
    final private Main main;
    final private Config config;
    private boolean isDebugEnabled;
    private boolean cmds;
    private boolean utils;
    private boolean player;
    private boolean entity;
    private boolean events;
    private boolean rewards;
    
    
    //CONSTRUCT
    public Debug(Main main)
    {
        this.main = main;
        isDebugEnabled = false;
        config = new Config(main,
                             Entities.Paths.DATA_FOLDER.txt,
                             "",
                             Paths.DEBUG_YML.txt);
        boolean save = false;
        if(!config.isSet(Paths.DEBUG_YML.path))
        {
            config.set(Paths.DEBUG_CMDS.path, false);
            config.set(Paths.DEBUG_UTILS.path, false);
            config.set(Paths.DEBUG_PLAYER.path, false);
            config.set(Paths.DEBUG_EVENTS.path, false);
            config.set(Paths.DEBUG_ENTITY.path, false);
            config.set(Paths.DEBUG_REWARDS.path, false);
            save = true;
        }
        if(save) { this.config.save(); }
        cmds = config.getBoo(Paths.DEBUG_CMDS.path);
        utils = config.getBoo(Paths.DEBUG_UTILS.path);
        player = config.getBoo(Paths.DEBUG_PLAYER.path);
        entity = config.getBoo(Paths.DEBUG_ENTITY.path);
        events = config.getBoo(Paths.DEBUG_EVENTS.path);
        rewards = config.getBoo(Paths.DEBUG_REWARDS.path);
        isDebugEnabled = true;
    }
    
    
    //GETTERS
    public boolean cmds() { if(isDebugEnabled) { return cmds; } return false; }
    public boolean utils() { if(isDebugEnabled) { return utils; } return false; }
    public boolean players() { if(isDebugEnabled) { return player; } return false; }
    public boolean entities() { if(isDebugEnabled) { return entity; } return false; }
    public boolean events() { if(isDebugEnabled) { return events; } return false; }
    public boolean rewards() { if(isDebugEnabled) { return rewards; } return false; }
    
    
    //SETTERS
    public void setDebug(String section, String b)
    {
        boolean b1 = Boolean.parseBoolean(b);
        switch(Paths.valueOf(section.toUpperCase()))
        {
            case DEBUG_CMDS:
                cmds = b1;
                config.set(Paths.DEBUG_CMDS.path, cmds);
                message("cmds set to " + b1);
                break;
            case DEBUG_UTILS:
                utils = b1;
                config.set(Paths.DEBUG_UTILS.path, utils);
                message("utils set to " + b1);
                break;
            case DEBUG_PLAYER:
                player = b1;
                config.set(Paths.DEBUG_PLAYER.path, player);
                message("player set to " + b1);
                break;
            case DEBUG_ENTITY:
                entity = b1;
                config.set(Paths.DEBUG_ENTITY.path, entity);
                message("entity set to " + b1);
                break;
            case DEBUG_EVENTS:
                events = b1;
                config.set(Paths.DEBUG_EVENTS.path, events);
                message("events set to " + b1);
                break;
            case DEBUG_REWARDS:
                rewards = b1;
                config.set(Paths.DEBUG_REWARDS.path, rewards);
                message("rewards set to " + b1);
                break;
            case DEBUG_ALL:
                cmds = b1;
                config.set(Paths.DEBUG_CMDS.path, cmds);
                utils = b1;
                config.set(Paths.DEBUG_UTILS.path, utils);
                player = b1;
                config.set(Paths.DEBUG_PLAYER.path, player);
                entity = b1;
                config.set(Paths.DEBUG_ENTITY.path, entity);
                events = b1;
                config.set(Paths.DEBUG_EVENTS.path, events);
                rewards = b1;
                config.set(Paths.DEBUG_REWARDS.path, rewards);
                message("ALL set to " + b1);
                break;
            default: main.getLogger().info("Invalid Debug Section");
        }
        config.save();
    }
    
    
    //SEND MESSAGE
    public void message(String msg) { /*main.massMessage(msg);*/ main.getLogger().info(msg); }
    
    
    
}
