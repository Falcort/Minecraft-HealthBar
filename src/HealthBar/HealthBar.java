package HealthBar;

import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import net.md_5.bungee.api.ChatColor;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class HealthBar implements Listener
{
	private Scoreboard sb;
	private Objective bellowObj;
	private static Map<Integer, String> namesTable = new HashMap<Integer, String>();
	private main plugin;
	
	/**
	 * JavaDoc HealthBar
	 * This class display HealthBar for Player and Mobs
	 * 
	 * @param plugin
	 * @author Falcort alias Thibault SOUQUET
	 * @version 0.2
	 */
	public HealthBar(main plugin)
	{
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		sb = plugin.getServer().getScoreboardManager().getMainScoreboard();
		setupBellow();
	}
	
	/**
	 * JavaDoc eventDoer
	 * This method is use by all the EventHandler to update healthbar
	 * it detect if the mobs have custom name and i if the customName contains a ❤
	 * If it does it register to a table the mob
	 * If the mob have a customName and the mob is register in the namestable it update with the customName
	 * Else if update normaly
	 * 
	 * @param entity
	 * @author Falcort alias Thibault SOUQUET
	 * @version 0.2
	 */
	public void eventDoer(Entity entity)
	{
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable()
		{
			public void run()
			{
				if(!(entity instanceof LivingEntity))
				{
					return;
				}
				LivingEntity living = (LivingEntity) entity;
				Double MaxHealth = living.getMaxHealth();
				Double CurrentHealth = living.getHealth();
				
				String customName = entity.getCustomName();
				if (customName != null && !(customName.contains("❤")))
				{
					if(!(namesTable.containsKey(entity.getEntityId())))
					{
						namesTable.put(entity.getEntityId(), customName);
						updateHealthBar(entity, living, MaxHealth, CurrentHealth, entity.getEntityId());
					}
					else
					{
						updateHealthBar(entity, living, MaxHealth, CurrentHealth, entity.getEntityId());
					}
				}
				else if (namesTable.containsKey(entity.getEntityId()))
				{
					updateHealthBar(entity, living, MaxHealth, CurrentHealth, entity.getEntityId());
				}
				else
				{
					updateHealthBar(entity, living, MaxHealth, CurrentHealth);
				}

			}
		}, 5);
	}
	/**
	 * JavaDoc onEntityDamageEvent
	 * This method listen for entity damage and update the HealthBar
	 * 
	 * @param event
	 * @author Falcort alias Thibault SOUQUET
	 * @version 0.2
	 */
	@EventHandler
	public void onEntityDamageEvent(EntityDamageEvent event)
	{
		eventDoer(event.getEntity());
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event)
	{
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable()
		{
			public void run()
			{
				if(event.getEntity().getCustomName() != null)
				{
					namesTable.remove(event.getEntity().getEntityId());
				}
			}
		}, 60);
		
	}
	/**
	 * JavaDoc onEntityRegainHealthEvent
	 * This method listen for regain of life for Player and Entity
	 * And update the HealthBar
	 * 
	 * @param event
	 * @author Falcort alias Thibault SOUQUET
	 * @version 0.2
	 */
	@EventHandler
	public void onEntityRegainHealthEvent(EntityRegainHealthEvent event)
	{
		eventDoer(event.getEntity());
	}
	
	/**
	 * JavaDoc onEntitySpawn
	 * This method listen for spawn of Entity and update HealthBar
	 * 
	 * @param event
	 * @author Falcort alias Thibault SOUQUET
	 * @version 0.2
	 */
	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent event)
	{
		eventDoer(event.getEntity());
	}
	
	/**
	 * JavaDoc updateHealthBar
	 * This method update the :
	 * BelowName for player
	 * And
	 * Next to the name of the mobs there life
	 * 
	 * 
	 * @param entity
	 * @param living
	 * @param MaxHealth
	 * @param CurrentHealth
	 * @author Falcort alias Thibault SOUQUET
	 * @version 0.2
	 */
	private void updateHealthBar(Entity entity, LivingEntity living, Double MaxHealth, Double CurrentHealth)
	{
		int max = (int) Math.floor(MaxHealth);
		int current = (int) Math.floor(CurrentHealth);
		if (living instanceof Player)
		{
			Player player = (Player) entity;
			bellowObj.getScore(player.getName()).setScore(current);
		}
		else
		{
			int result = getColor(CurrentHealth, MaxHealth);
			if (result == 75)
			{
				entity.setCustomName(entity.getType() + " " + ChatColor.GREEN + current + ChatColor.WHITE + " / " + max + ChatColor.RED + "❤");
			}
			else if(result == 50)
			{
				entity.setCustomName(entity.getType() + " " + ChatColor.YELLOW + current + ChatColor.WHITE + " / " + max + ChatColor.RED + "❤");
			}
			else if (result == 25)
			{
				entity.setCustomName(entity.getType() + " " + ChatColor.GOLD + current + ChatColor.WHITE + " / " + max + ChatColor.RED + "❤");
			}
			else
			{
				entity.setCustomName(entity.getType() + " " + ChatColor.RED + current + ChatColor.WHITE + " / " + max + ChatColor.RED + "❤");
			}
		}
	}
	
	/**
	 * JavaDoc updateHealthBar
	 * This method is used when mobs have custom names
	 * to keep the customs names
	 * 
	 * @param entity
	 * @param living
	 * @param MaxHealth
	 * @param CurrentHealth
	 * @param ID
	 * @author Falcort alias Thibault SOUQUET
	 * @version 0.2
	 */
	private void updateHealthBar(Entity entity, LivingEntity living, Double MaxHealth, Double CurrentHealth, int ID)
	{
		int max = (int) Math.floor(MaxHealth);
		int current = (int) Math.floor(CurrentHealth);
		if (living instanceof Player)
		{
			Player player = (Player) entity;
			bellowObj.getScore(player.getName()).setScore(current);
		}
		else
		{
			int result = getColor(CurrentHealth, MaxHealth);
			if (result == 75)
			{
				entity.setCustomName(namesTable.get(ID) + " " + ChatColor.GREEN + current + ChatColor.WHITE + " / " + max + ChatColor.RED + "❤");
			}
			else if(result == 50)
			{
				entity.setCustomName(namesTable.get(ID) + " " + ChatColor.YELLOW + current + ChatColor.WHITE + " / " + max + ChatColor.RED + "❤");
			}
			else if (result == 25)
			{
				entity.setCustomName(namesTable.get(ID) + " " + ChatColor.GOLD + current + ChatColor.WHITE + " / " + max + ChatColor.RED + "❤");
			}
			else
			{
				entity.setCustomName(namesTable.get(ID) + " " + ChatColor.RED + current + ChatColor.WHITE + " / " + max + ChatColor.RED + "❤");
			}
		}
	}
	
	/**
	 * JavaDoc getColor
	 * This method make a ratio of the health and max health
	 * to display a color of the % of health
	 * 
	 * @param health
	 * @param max
	 * @return % of life
	 * @author Falcort alias Thibault SOUQUET
	 * @version 0.2
	 */
	public int getColor (double health, double max)
	{
		double ratio = health/max;
		if (ratio > 0.75)
		{
			return 75;
		}
		else if (ratio > 0.5)
		{
			return 50;
		}
		else if (ratio > 0.25)
		{
			return 25;
		}
		else
		{
			return 0;
		}
	}
	
	/**
	 * JavaDoc setupBellow
	 * This method create the scoreboard for the Player HealthBar
	 * 
	 * @author Falcort alias Thibault SOUQUET
	 * @version 0.2
	 */
	public void setupBellow()
	{
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable()
		{
			public void run()
			{
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "scoreboard objectives remove healthbarbelow");
				bellowObj = sb.registerNewObjective("healthbarbelow", "dummy");
				bellowObj.setDisplayName(ChatColor.RED + "❤");
				bellowObj.setDisplaySlot(DisplaySlot.BELOW_NAME);
			}
		}, 5);
	}
}
