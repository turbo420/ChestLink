package link;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ChestLink extends JavaPlugin implements Listener {
	Inventory inv;
	Config dConfig = new Config();
	FileConfiguration Ba;
	/// List<String> list2;

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (cmd.getName().equalsIgnoreCase("ChestLink") & sender instanceof Player) {

			if (sender instanceof Player) {
				Player player = (Player) sender;

				if (player.hasPermission("ChestLink.Make")) {

					ItemStack i = new ItemStack(Material.CHEST);
					ItemStack e = new ItemStack(Material.EMERALD);
					ItemMeta im = i.getItemMeta();

					if (args.length == 0) {

						// im.setDisplayName(ChatColor.RED +
						// "ChestLock:"+player.getName() +" " +
						// args[0].toString());
						im.setDisplayName(ChatColor.RED + args[0]);
					} else if (args.length == 1) {
						im.setDisplayName(ChatColor.DARK_AQUA + args[0].toString());
					} else if (args.length == 2) {
						im.setDisplayName(ChatColor.RED + "ChestLock:" + player.getName() + " " + args[0].toString()
								+ " " + args[1].toString());
					} else if (args.length == 3) {
						im.setDisplayName(ChatColor.RED + "ChestLock:" + player.getName() + " " + args[0].toString()
								+ " " + args[1].toString() + " " + args[2].toString());
					} else if (args.length == 4) {
						im.setDisplayName(ChatColor.RED + "ChestLock:" + player.getName() + " " + args[0].toString()
								+ " " + args[1].toString() + " " + args[2].toString() + " " + args[3].toString());

					} else if (args.length == 5) {

						player.sendMessage("Too many arguments!");
						return true;

					}

					im.setLore(Arrays.asList(ChatColor.BLUE + "ChestLink", ""));
					i.setItemMeta(im);

					player.getInventory().setItemInMainHand(i);// getWorld().dropItem(player.getLocation(),
																// i);
					player.getInventory().addItem(e);
					return true;
				} else {
					player.sendMessage("You Don't have Permissions");
				}
			}

		}
		return false;

	}

	@Override
	public void onEnable() {
		/// getServer().getPluginManager().registerEvents(this, this);
		InventoryE inventoryE = new InventoryE(this);
		PluginManager pluginManager = getServer().getPluginManager();
		pluginManager.registerEvents(inventoryE, this);
		pluginManager.registerEvents(this, this);

		inventoryE.createInventory();
		registerConfig();
		dConfig.ConfigMake();
		Ba = dConfig.LoadConfig();

		Metric metrics = new Metric(this);
		metrics.addCustomChart(new Metric.SingleLineChart("players", () -> Bukkit.getOnlinePlayers().size()));

	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		// Player player = event.getPlayer();

	}

	private void registerConfig() {

		getConfig().options().copyDefaults(false);
		saveConfig();
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockPlace(BlockPlaceEvent event) {

		if (event.getBlock().getType().equals(Material.CHEST)) {

			Block block = event.getBlockPlaced();
			Player player = event.getPlayer();
			if (!block.getType().equals(Material.CHEST)) {
				player.sendMessage("Database needs to reset");
				return;
			}
			Chest chest = (Chest) block.getState();
			List<String> list = Ba.getStringList(event.getPlayer().getName() + ".Chest");
			// List<String> list = Ba.getStringList(player.getName() +
			// ".Chest");
			list.add("ChestName:" + ";" + chest.getCustomName() + ";" + "World:" + player.getWorld().getName() + ";"
					+ block.getX() + ";" + block.getY() + ";" + block.getZ());
			// Setting and saving
			// World arena = Bukkit.getWorld("World");
			// arena.save();
			dConfig.SetConfig(Ba, player.getName() + ".Chest", list);

		}

	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreak(BlockBreakEvent event) {

		if (event.getBlock().getType().equals(Material.CHEST)) {
			Player player = event.getPlayer();
			// List<String> list4 = Ba.getStringList(event.getPlayer().getName()
			// + ".Chest");
			Location location = event.getBlock().getLocation();
			int bx = location.getBlockX();
			int by = location.getBlockY();
			int bz = location.getBlockZ();
			// List<String> name5 = Ba.getStringList(player.getName() +
			// ".Chest");
			List<String> list = Ba.getStringList(event.getPlayer().getName() + ".Chest");
			if (list.toString() == "[]") {
				event.setCancelled(true);
				player.sendMessage(
						ChatColor.DARK_RED + "[ChestLink]" + ChatColor.DARK_AQUA + "You Don't have any Chest");
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

						dConfig.SetConfig(Ba, player.getName() + ".Chest", list);
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
