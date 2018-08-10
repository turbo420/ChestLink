package link;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {
	File locations = new File("plugins/ChestLink/", "Chestlocations.yml");

	// private ChestLink plugin = ChestLink.getPlugin(ChestLink.class);
	// File locations = new File(plugin.getDataFolder(), "Chestlocations.yml");

	public void ConfigMake() {
		// File locations = new File("plugins/ChestLink/",
		// "Chestlocations.yml");
		if (!locations.exists()) {
			try {
				locations.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void SetConfig(FileConfiguration configfile, String path, List<String> list2) {
		// File locations = new File("plugins/ChestLink", "locations.yml");
		configfile.set(path, list2);
		try {
			configfile.save(locations);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public FileConfiguration LoadConfig() {
		// File locations = new File("plugins/ChestLink", "locations.yml");
		FileConfiguration loc = YamlConfiguration.loadConfiguration(locations);
		return loc;
	}

}