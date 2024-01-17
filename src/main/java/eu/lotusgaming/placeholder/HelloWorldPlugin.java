package eu.lotusgaming.placeholder;

import org.bukkit.plugin.java.JavaPlugin;

public class HelloWorldPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        this.getLogger().info("Hello World!");
    }

}
