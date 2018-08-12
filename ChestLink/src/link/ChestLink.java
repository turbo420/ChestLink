package link;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ChestLink extends JavaPlugin implements Listener {
	ConsoleCommandSender console;
	Config dConfig = new Config();
	FileConfiguration customConfig;
	InventoryE inventoryE;

	@Override
	public void onLoad() {
		console = Bukkit.getServer().getConsoleSender();
		console.sendMessage(ChatColor.RED + "-------------------------------");
		console.sendMessage(ChatColor.GREEN + "----------Loading--------------");
		console.sendMessage(ChatColor.GREEN + "---------ChestLink-------------");
		console.sendMessage(ChatColor.RED + "-------------------------------");

	}

	@Override
	public void onDisable() {

		console.sendMessage(ChatColor.RED + "-------------------------------");
		console.sendMessage(ChatColor.GREEN + "-----------Disabling-----------");
		console.sendMessage(ChatColor.GREEN + "-----------ChestLink-----------");
		console.sendMessage(ChatColor.RED + "-------------------------------");
	}

	@Override
	public void onEnable() {
		console.sendMessage(ChatColor.RED + "-------------------------------");
		console.sendMessage(ChatColor.GREEN + "----------ChestLink------------");
		console.sendMessage(ChatColor.GREEN + "---------Version 1.0-----------");
		console.sendMessage(ChatColor.RED + "-------------------------------");

		inventoryE = new InventoryE(this);
		PluginManager pluginManager = getServer().getPluginManager();
		pluginManager.registerEvents(inventoryE, this);
		pluginManager.registerEvents(this, this);

		inventoryE.createInventory();
		registerConfig();
		dConfig.ConfigMake();
		customConfig = dConfig.LoadConfig();

		Metric metrics = new Metric(this);

		metrics.addCustomChart(new Metric.SingleLineChart("play", () -> Bukkit.getOnlinePlayers().size()));

	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {

	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (cmd.getName().equalsIgnoreCase("ChestLink") & sender instanceof Player) {

			Player player = (Player) sender;

			if (player.hasPermission("ChestLink.Make")) {

				ItemStack i = new ItemStack(Material.CHEST);
				ItemStack e = new ItemStack(Material.EMERALD);
				ItemMeta im = i.getItemMeta();

				if (args.length == 0) {
					player.sendMessage(ChatColor.DARK_RED + "[ChestLink]" + ChatColor.DARK_AQUA + " Need a Chest name");
					return true;
				} else if (args.length == 1) {
					im.setDisplayName(ChatColor.DARK_AQUA + args[0].toString());

				} else if (args.length == 2) {

					player.sendMessage(
							ChatColor.DARK_RED + "[ChestLink]" + ChatColor.DARK_AQUA + " Too many arguments!");
					return true;

				}

				if (args[0].toString().equalsIgnoreCase("Chest")) {

					player.sendMessage(
							ChatColor.DARK_RED + "[ChestLink]" + ChatColor.DARK_AQUA + " Can't use the name Chest ");
					return true;
				}

				im.setLore(Arrays.asList(ChatColor.BLUE + "ChestLink", ""));
				i.setItemMeta(im);

				player.getInventory().setItemInMainHand(i);// getWorld().dropItem(player.getLocation(),
															// i);
				player.getInventory().addItem(e);
				return true;
			} else {
				player.sendMessage(ChatColor.DARK_RED + "[ChestLink]" + " You Don't have Permissions");
			}

		} else if (cmd.getName().equalsIgnoreCase("FixDataBase") & sender instanceof Player) {
			Player player = (Player) sender;
			if (args.length > 0) {
				player.sendMessage(ChatColor.DARK_RED + "[ChestLink]" + ChatColor.DARK_AQUA + " No arguments needed!");
				return true;
			}

			FixDataBase(player);
			return true;
		}

		return false;

	}

	public void FixDataBase(Player player) {
		if (player.hasPermission("ChestLink.FixDataBase")) {
			player.sendMessage(ChatColor.DARK_RED + "[ChestLink]" + ChatColor.DARK_AQUA + " Running FixDataBase");
			List<String> list = customConfig.getStringList(player.getName() + ".Chest");
			for (String admin : list) {

				String[] locationXYZ = admin.split(";");
				String Chestname = locationXYZ[1];

				int x = Integer.parseInt(locationXYZ[3]);
				int y = Integer.parseInt(locationXYZ[4]);
				int z = Integer.parseInt(locationXYZ[5]);

				BlockState bs = player.getWorld().getBlockAt(x, y, z).getState();

				if (!bs.getBlock().getType().equals(Material.CHEST)) {

					Location loc = player.getWorld().getBlockAt(x, y, z).getLocation();

					loc.getBlock().setType(Material.CHEST);
					Chest c = (Chest) loc.getBlock().getState();
					c.setCustomName(Chestname);
					c.update();

					player.sendMessage(ChatColor.DARK_RED + "[ChestLink]" + ChatColor.DARK_AQUA + " Database Fixed");

				}

			}
		} else if (!player.hasPermission("ChestLink.FixDataBase")) {
			player.sendMessage(ChatColor.DARK_RED + "[ChestLink]" + ChatColor.DARK_RED
					+ " You do not have Permission to use this");
		}
	}

	private void registerConfig() {
		saveDefaultConfig();
		// getConfig().options().copyDefaults(true);
		// saveConfig();
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockPlace(BlockPlaceEvent event) {

		if (event.getBlock().getType().equals(Material.CHEST)) {
			Player player = event.getPlayer();
			Block block = event.getBlockPlaced();

			if (!block.getType().equals(Material.CHEST)) {
				player.sendMessage("Database needs to reset");
				return;
			}
			Chest chest = (Chest) block.getState();
			// remove normal chest from the list
			if (chest.getInventory().getName().equalsIgnoreCase("chest")
					|| chest.getInventory().getName().equalsIgnoreCase("Large chest")) {
				// player.sendMessage("Normal CHEST");
				event.setCancelled(false);
				return;
			}

			List<String> list = customConfig.getStringList(event.getPlayer().getName() + ".Chest");

			list.add("ChestName:" + ";" + chest.getCustomName() + ";" + "World:" + player.getWorld().getName() + ";"
					+ block.getX() + ";" + block.getY() + ";" + block.getZ());
			// Setting and saving
			// World arena = Bukkit.getWorld("World");
			// arena.save();
			dConfig.SetConfig(customConfig, player.getName() + ".Chest", list);

		}

	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreak(BlockBreakEvent event) {

		if (event.getBlock().getType().equals(Material.CHEST)) {
			Chest block = (Chest) event.getBlock().getState();
			// remove normal chest from the list
			if (block.getInventory().getName().equalsIgnoreCase("chest")
					|| block.getInventory().getName().equalsIgnoreCase("Large chest")) {
				// player.sendMessage("Normal CHEST");
				event.setCancelled(false);
				return;
			}

			Player player = event.getPlayer();

			Location location = event.getBlock().getLocation();
			int bx = location.getBlockX();
			int by = location.getBlockY();
			int bz = location.getBlockZ();
			// List<String> name5 = Ba.getStringList(player.getName() +
			// ".Chest");
			List<String> list = customConfig.getStringList(event.getPlayer().getName() + ".Chest");
			if (list.toString() == "[]") {
				event.setCancelled(true);
				player.sendMessage(
						ChatColor.DARK_RED + "[ChestLink]" + ChatColor.DARK_AQUA + " You Don't have any Chest");
				return;
			}
			if (list != null) {

				for (String admin : list) {

					String[] locationXYZ = admin.split(";");
					int x = Integer.parseInt(locationXYZ[3]);
					int y = Integer.parseInt(locationXYZ[4]);
					int z = Integer.parseInt(locationXYZ[5]);
					if (!(bx == x && by == y && bz == z)) {
						event.setCancelled(true);
						// player.sendMessage(ChatColor.DARK_RED + "[ChestLink]"
						// + ChatColor.DARK_AQUA
						// + "You can not break this Chest");

					}
					if (bx == x && by == y && bz == z) {

						list.remove(admin);

						dConfig.SetConfig(customConfig, player.getName() + ".Chest", list);
						// World arena = Bukkit.getWorld("World");
						// arena.save();
						event.setCancelled(false);
						break;

					}
				}

			}

		}
	}

	@EventHandler
	public void onExplode(EntityExplodeEvent e) {
		if (e.getEntityType().equals(EntityType.PRIMED_TNT)) {
			Iterator<Block> bi = e.blockList().iterator();
			while (bi.hasNext())
				if (bi.next().getType() == Material.CHEST) {
					bi.remove();
				}

		}
	}

}
