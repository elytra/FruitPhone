package io.github.elytra.fruitphone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;

@Mod(modid="fruitphone", name="Fruit Phone", version="@VERSION@")
public class FruitPhone {
	public static final Logger log = LogManager.getLogger("FruitPhone");

	public static CreativeTabs tab = new CreativeTabs("fruitphone") {
		
		@Override
		public Item getTabIconItem() {
			return FruitItems.HANDHELD;
		}
	};
	
	@SidedProxy(clientSide="io.github.elytra.fruitphone.ClientProxy", serverSide="io.github.elytra.fruitphone.Proxy")
	public static Proxy proxy;
	
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent e) {
		RecipeSorter.register("fruitphone:upgrade", FruitUpgradeRecipe.class, Category.SHAPED, "after:forge:shapedore");
		
		FruitItems.register();
		FruitRecipes.register();
		
		proxy.preInit();
		MinecraftForge.EVENT_BUS.register(proxy);
	}
	
	@EventHandler
	public void onPostInit(FMLPostInitializationEvent e) {
		proxy.postInit();
	}
}
