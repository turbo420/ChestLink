package link;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryE implements Listener {
	ChestLink plugin;
	Inventory inv;

	public InventoryE(ChestLink plugin) {
		this.plugin = plugin;
	}

	public void FixData(Player player) {
		if (player.hasPermission("ChestLink.fixdata")) {
			player.sendMessage(ChatColor.RED + "Running");
			List<String> list = plugin.Ba.getStringList(player.getName() + ".Chest");
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

					player.sendMessage("Database Fixed");

				}

			}
		} else if (!player.hasPermission("ChestLink.fixdata")) {
			player.sendMessage(ChatColor.RED + "You do not have Permission to use this");
		}
	}

	// You can open the inventory with this
	public void openInventory(Player p) {
		p.openInventory(inv);
		return;
	}

	public void createInventory() {
		inv = Bukkit.createInventory(null, 18, ChatColor.RED + "ChestLink");

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

					// we have to Cancelled it then call Chestlink inventory
					e.setCancelled(true);
					player.openInventory(inv);
				}

			}

		}

	}

	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent e) {

		Player player = (Player) e.getPlayer();

		inv.clear();
		List<String> list = plugin.Ba.getStringList(e.getPlayer().getName() + ".Chest");
		for (String admin : list) {

			String[] locationXYZ = admin.split(";");
			String Chestname = locationXYZ[1];

			int x = Integer.parseInt(locationXYZ[3]);
			int y = Integer.parseInt(locationXYZ[4]);
			int z = Integer.parseInt(locationXYZ[5]);

			BlockState bs = player.getWorld().getBlockAt(x, y, z).getState();

			if (!bs.getBlock().getType().equals(Material.CHEST)) {
				player.sendMessage("Database needs to reset");

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

		Player player = (Player) e.getWhoClicked();
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
		List<String> list3 = plugin.Ba.getStringList(e.getWhoClicked().getName() + ".Chest");
		String[] locationXYZ = list3.get(e.getSlot()).split(";");
		int x = Integer.parseInt(locationXYZ[3]);
		int y = Integer.parseInt(locationXYZ[4]);
		int z = Integer.parseInt(locationXYZ[5]);

		BlockState bs = player.getWorld().getBlockAt(x, y, z).getState();
		if (!bs.getBlock().getType().equals(Material.CHEST)) {
			player.sendMessage("Database needs to reset");
			return;
		}

		Chest c = (Chest) bs;
		player.getPlayer().openInventory(c.getInventory());

		return;
	}

}
