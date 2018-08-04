package link;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class ChestLink extends JavaPlugin implements Listener {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (cmd.getName().equalsIgnoreCase("ChestLink") & sender instanceof Player) {

			if (sender instanceof Player) {
				Player player = (Player) sender;

				ItemStack i = new ItemStack(Material.CHEST);
				ItemStack e = new ItemStack(Material.EMERALD);
				ItemMeta im = i.getItemMeta();

				if (args.length == 0) {

					// im.setDisplayName(ChatColor.RED +
					// "ChestLock:"+player.getName() +" " + args[0].toString());
					im.setDisplayName(ChatColor.RED + args[0]);
				} else if (args.length == 1) {
					im.setDisplayName(ChatColor.DARK_AQUA + args[0].toString());
				} else if (args.length == 2) {
					im.setDisplayName(ChatColor.RED + "ChestLock:" + player.getName() + " " + args[0].toString() + " "
							+ args[1].toString());
				} else if (args.length == 3) {
					im.setDisplayName(ChatColor.RED + "ChestLock:" + player.getName() + " " + args[0].toString() + " "
							+ args[1].toString() + " " + args[2].toString());
				} else if (args.length == 4) {
					im.setDisplayName(ChatColor.RED + "ChestLock:" + player.getName() + " " + args[0].toString() + " "
							+ args[1].toString() + " " + args[2].toString() + " " + args[3].toString());

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
			}

		}
		return false;

	}

	private Inventory inv;

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		createInventory();
		registerConfig();
		// initializeItems();

	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {

		Player player = event.getPlayer();

		if (player.hasPlayedBefore()) {
			// player.sendMessage("shit it works!!!");
			return;
		} else {
			//// bug need to see config is null or all ready
			//// made////////////////
			List<String> list = this.getConfig().getStringList(player.getName());
			// list.add(player.getLocation().toString());
			getConfig().set(player.getName(), list);
			saveConfig();
			player.sendMessage("fist time!!!");
		}

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
					// player.sendMessage("I'm a Chest");

					e.setCancelled(true);
					// Location location = player.getLocation();

					///// should redo this a func
					List<String> name2 = getConfig().getStringList(player.getName());
					// Clear the inv every time to update
					inv.clear();

					for (String admin : name2) {

						String[] locationXYZ = admin.split(";");
						String d = locationXYZ[1];

						int x = Integer.parseInt(locationXYZ[3]);
						int y = Integer.parseInt(locationXYZ[4]);
						int z = Integer.parseInt(locationXYZ[5]);

						BlockState bs = player.getWorld().getBlockAt(x, y, z).getState();
						if (bs == null) {
							return;
						}

						Chest c = (Chest) bs;

						int itemsnum = 0;

						for (ItemStack chestinv : c.getInventory().getContents()) {
							if (chestinv == null)
								continue;
							itemsnum += chestinv.getAmount();

						}

						inv.addItem(createGuiItem(d,
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

		List<String> name2 = getConfig().getStringList(p.getName());
		if (name2 == null) {
			return;
		}

		// p.sendMessage(name2.toString());

		String[] locationXYZ = name2.get(e.getSlot()).split(";");
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

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {

		if (event.getBlock().getType().equals(Material.CHEST)) {

			Block block = event.getBlockPlaced();
			Player player = event.getPlayer();

			Chest chest = (Chest) block.getState();

			if (event.getBlock() == null)
				return;

			List<String> list = this.getConfig().getStringList(player.getName());

			list.add("ChestName:" + ";" + chest.getCustomName() + ";" + "World:" + player.getWorld().getName() + ";"
					+ block.getX() + ";" + block.getY() + ";" + block.getZ());

			getConfig().set(player.getName(), list);

			saveConfig();

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

			List<String> name2 = getConfig().getStringList(player.getName());

			if (name2 != null) {
				for (String admin : name2) {

					String[] locationXYZ = admin.split(";");
					int x = Integer.parseInt(locationXYZ[3]);
					int y = Integer.parseInt(locationXYZ[4]);
					int z = Integer.parseInt(locationXYZ[5]);

					if (bx == x && by == y && bz == z) {
						name2.remove(admin);
						getConfig().set(player.getName(), name2);

						saveConfig();

						break;
					}

				}
			}

		}
	}
}
