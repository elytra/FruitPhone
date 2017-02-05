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
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid="fruitphone", name="Fruit Phone", version="@VERSION@")
public class FruitPhone {
	public static final Logger log = LogManager.getLogger("FruitPhone");

	public static CreativeTabs tab = new CreativeTabs("fruitphone") {
		
		@Override
		public Item getTabIconItem() {
			return handheld;
		}
	};
	
	public static ItemFruitHandheld handheld;
	public static ItemFruitPassive passive;
	
	@SidedProxy(clientSide="io.github.elytra.fruitphone.ClientProxy", serverSide="io.github.elytra.fruitphone.Proxy")
	public static Proxy proxy;
	
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent e) {
		handheld = new ItemFruitHandheld();
		handheld.setRegistryName("handheld");
		handheld.setCreativeTab(tab);
		handheld.setUnlocalizedName("fruitphone.handheld");
		GameRegistry.register(handheld);
		
		passive = new ItemFruitPassive();
		passive.setRegistryName("passive");
		passive.setCreativeTab(tab);
		passive.setUnlocalizedName("fruitphone.passive");
		GameRegistry.register(passive);
		
		proxy.preInit();
		MinecraftForge.EVENT_BUS.register(proxy);
	}
	
	@EventHandler
	public void onPostInit(FMLPostInitializationEvent e) {
		proxy.postInit();
	}
}
