package HealthBar;

import org.bukkit.plugin.java.JavaPlugin;

public class main extends JavaPlugin
{
	@Override
	public void onEnable()
	{
		new HealthBar(this);
	}
}
