package quaternary.qsilkspawners;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ModConfig {
	public static boolean SERVERSIDE_ONLY_MODE = false;
	
	public static Configuration config;
	
	public static void preinit(FMLPreInitializationEvent e) {
		config = new Configuration(e.getSuggestedConfigurationFile(), "1");
		
		readConfig();
		saveConfig();
	}
	
	public static void readConfig() {
		SERVERSIDE_ONLY_MODE = config.getBoolean("serversideOnlyMode", "general", false, "If this is true, servers will write the name of the mob in the spawner to Lore, allowing clients without the mod installed to view the mob name on the tooltip.");
	}
	
	public static void saveConfig() {
		if(config.hasChanged())	config.save();
	}
	
	@SubscribeEvent
	public static void changed(ConfigChangedEvent.OnConfigChangedEvent e) {
		if(e.getModID().equals(QSilkSpawners.MODID)) {
			readConfig();
			saveConfig();
		}
	}
}
