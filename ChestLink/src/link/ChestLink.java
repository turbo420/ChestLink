package link;

import java.util.ArrayList;
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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class ChestLink extends JavaPlugin implements Listener {
	private Inventory inv;
	Config dConfig = new Config();
	FileConfiguration Ba;
	List<String> list2;

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
		getServer().getPluginManager().registerEvents(this, this);
		createInventory();
		registerConfig();
		dConfig.ConfigMake();
		Ba = dConfig.LoadConfig();

		Metric metrics = new Metric(this);
		metrics.addCustomChart(new Metric.SingleLineChart("players", () -> Bukkit.getOnlinePlayers().size()));

	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		// Player player = event.getPlayer();
		list2 = Ba.getStringList(event.getPlayer().getName() + ".Chest");

	}

	public void createInventory() {
		inv = Bukkit.createInventory(null, 18, ChatColor.RED + "ChestLink");

	}

	private void registerConfig() {

		getConfig().options().copyDefaults(false);
		saveConfig();
	}

	// You can call this whenever you want to put the items in
	public void initializeItems() {
		inv.addItem(createGuiItem("Example Sword", new ArrayList<String>(Arrays.asList("This is an example!")),
				Material.DIAMOND_SWORD));
		inv.addItem(createGuiItem("Example Shovel", new ArrayList<String>(Arrays.asList("This is an example!")),
				Material.ACACIA_DOOR));
	}

	// Create a gui item with a custom name, and description
	public ItemStack createGuiItem(String name, ArrayList<String> desc, Material mat) {
		ItemStack i = new ItemStack(mat, 1);
		ItemMeta iMeta = i.getItemMeta();
		iMeta.setDisplayName(name);
		iMeta.setLore(desc);
		i.setItemMeta(iMeta);
		return i;
	}

	// You can open the inventory with this
	public void openInventory(Player p) {
		p.openInventory(inv);
		return;
	}

	@EventHandler
	public void OnInventory(PlayerInteractEvent e) {
		Action action = e.getAction();
		ItemStack iStack = e.getItem();
		Player player = e.getPlayer();
		if (action == Action.PHYSICAL || iStack == null || iStack.getType() == Material.AIR)
			return;
		if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if (e.getItem() != null && e.getClickedBlock() != null) {

				if (e.getClickedBlock().getType().equals(Material.CHEST)
						&& e.getItem().getType().equals(Material.EMERALD)) {
					// Cancelling the chest from opening to open are inventory
					e.setCancelled(true);

					// Clear the inv every time to update
					inv.clear();

					for (String admin : list2) {

						String[] locationXYZ = admin.split(";");
						String Chestname = locationXYZ[1];

						int x = Integer.parseInt(locationXYZ[3]);
						int y = Integer.parseInt(locationXYZ[4]);
						int z = Integer.parseInt(locationXYZ[5]);

						BlockState bs = player.getWorld().getBlockAt(x, y, z).getState();

						if (bs == null) {
							return;
						}

						Chest c = (Chest) bs;

						// Checking how many items in chest
						int itemsnum = 0;

						for (ItemStack chestinv : c.getInventory().getContents()) {
							if (chestinv == null)
								continue;
							itemsnum += chestinv.getAmount();

						}

						inv.addItem(createGuiItem(Chestname,
								new ArrayList<String>(Arrays.asList(ChatColor.YELLOW + "ChestLink:",
										ChatColor.DARK_GREEN + "Number of Items: " + ChatColor.DARK_AQUA
												+ Integer.toString(itemsnum),
										ChatColor.DARK_RED + "Chest Location:" + " X:" + x + " Y:" + y + " Z:" + z)),
								Material.CHEST));

					}

					player.openInventory(inv);

				}

			}

		}
	}

	// Check for clicks on items
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (!(e.getWhoClicked() instanceof Player)) {
			return;
		}

		String invName = e.getInventory().getName();
		if (!invName.equals(inv.getName())) {
			return;
		}

		e.setCancelled(true);

		Player p = (Player) e.getWhoClicked();
		ItemStack clickedItem = e.getCurrentItem();

		// What if the clicked item is null? Nullpointer, so return.
		if (clickedItem == null) {
			return;
		}

		// What if the clicked item doesn't have itemmeta? Null pointer, so
		// return.
		if (!clickedItem.hasItemMeta()) {
			return;
		}

		ItemMeta meta = clickedItem.getItemMeta();

		// What if the clicked item has no display name? Null pointer, so
		// return.
		if (!meta.hasDisplayName()) {
			return;
		}

		String[] locationXYZ = list2.get(e.getSlot()).split(";");
		int x = Integer.parseInt(locationXYZ[3]);
		int y = Integer.parseInt(locationXYZ[4]);
		int z = Integer.parseInt(locationXYZ[5]);

		BlockState bs = p.getWorld().getBlockAt(x, y, z).getState();
		if (bs == null) {
			return;
		}

		Chest c = (Chest) bs;
		p.getPlayer().openInventory(c.getInventory());

		return;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockPlace(BlockPlaceEvent event) {

		if (event.getBlock().getType().equals(Material.CHEST)) {

			Block block = event.getBlockPlaced();
			Player player = event.getPlayer();

			Chest chest = (Chest) block.getState();

			if (event.getBlock() == null)
				return;

			// List<String> list = Ba.getStringList(player.getName() +
			// ".Chest");
			list2.add("ChestName:" + ";" + chest.getCustomName() + ";" + "World:" + player.getWorld().getName() + ";"
					+ block.getX() + ";" + block.getY() + ";" + block.getZ());
			// Setting and saving
			dConfig.SetConfig(Ba, player.getName() + ".Chest", list2);

		}

	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreak(BlockBreakEvent event) {

		if (event.getBlock().getType().equals(Material.CHEST)) {
			Player player = event.getPlayer();

			Location location = event.getBlock().getLocation();
			int bx = location.getBlockX();
			int by = location.getBlockY();
			int bz = location.getBlockZ();
			// List<String> name2 = Ba.getStringList(player.getName() +
			// ".Chest");

			if (list2.toString() == "[]") {
				event.setCancelled(true);
				player.sendMessage(
						ChatColor.DARK_RED + "[ChestLink]" + ChatColor.DARK_AQUA + "You Don't have any Chest");
				return;
			}
			if (list2 != null) {

				for (String admin : list2) {

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

						list2.remove(admin);

						dConfig.SetConfig(Ba, player.getName() + ".Chest", list2);

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
