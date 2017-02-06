package io.github.elytra.fruitphone;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;

@Mod(modid=FruitPhone.MODID, name=FruitPhone.NAME, version=FruitPhone.VERSION)
public class FruitPhone {
	
	public static final String MODID = "fruitphone";
	public static final String NAME = "Fruit Phone";
	public static final String VERSION = "@VERSION@";
	
	public static final Logger log = LogManager.getLogger("FruitPhone");

	public static CreativeTabs tab = new CreativeTabs("fruitphone") {
		
		@Override
		public Item getTabIconItem() {
			return FruitItems.HANDHELD;
		}
	};
	
	@SidedProxy(clientSide="io.github.elytra.fruitphone.ClientProxy", serverSide="io.github.elytra.fruitphone.Proxy")
	public static Proxy proxy;
	
	@Instance
	public static FruitPhone inst;
	
	public boolean optionalMode;
	
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent e) {
		Configuration config = new Configuration(e.getSuggestedConfigurationFile());
		
		optionalMode = config.getBoolean("optional", "General", false,
				"Don't register any items or blocks. If set to true on a server,\n"
				+ "Fruit Phone will not be required to connect to the server. If\n"
				+ "set to true on a client, Fruit Phone will pretend you're\n"
				+ "wearing glasses at all times. Since this disables all of Fruit\n"
				+ "Phone's items, if you try to enable this on the client and\n"
				+ "connect to a server with it disabled, you will get a 'Fatally\n"
				+ "missing blocks and items' error and will not be able to connect.\n"
				+ "\n"
				+ "Not required to connect to servers that don't have Fruit\n"
				+ "Phone. The mod will pretend you're wearing glasses when\n"
				+ "connected to servers that lack the mod.");
		
		config.save();
		
		if (!optionalMode) {
			RecipeSorter.register("fruitphone:upgrade", FruitUpgradeRecipe.class, Category.SHAPED, "after:forge:shapedore");
			
			FruitItems.register();
			FruitRecipes.register();
			
			proxy.preInit();
		}
		MinecraftForge.EVENT_BUS.register(proxy);
	}
	
	@EventHandler
	public void onPostInit(FMLPostInitializationEvent e) {
		proxy.postInit();
	}
	
	@NetworkCheckHandler
	public boolean onConnectionOffered(Map<String, String> mods, Side offerer) {
		if (optionalMode) {
			return true;
		} else {
			if (offerer == Side.SERVER) return true;
			return mods.containsKey("fruitphone") && VERSION.equals(mods.get("fruitphone"));
		}
	}
}
