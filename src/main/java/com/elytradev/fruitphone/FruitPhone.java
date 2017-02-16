/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2017 Aesen 'unascribed' Vismea
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.elytradev.fruitphone;

import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.elytradev.fruitphone.capability.FruitEquipmentCapability;
import com.elytradev.fruitphone.capability.FruitEquipmentStorage;
import com.elytradev.fruitphone.item.FruitItems;
import com.elytradev.fruitphone.network.EquipmentDataPacket;
import com.elytradev.fruitphone.network.SetAlwaysOnPacket;
import com.elytradev.fruitphone.proxy.ClientProxy;
import com.elytradev.fruitphone.proxy.Proxy;
import com.elytradev.fruitphone.recipe.FruitRecipes;
import com.elytradev.fruitphone.recipe.FruitUpgradeRecipe;

import io.github.elytra.concrete.NetworkContext;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.GameRules.ValueType;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;

@Mod(modid=FruitPhone.MODID, name=FruitPhone.NAME, version=FruitPhone.VERSION)
public class FruitPhone {
	
	public static final String MODID = "fruitphone";
	public static final String NAME = "Fruit Phone";
	public static final String VERSION = "@VERSION@";
	
	public static final Logger log = LogManager.getLogger("FruitPhone");

	public static CreativeTabs tab = new CreativeTabs("fruitphone") {
		
		private ItemStack icon;
		
		@Override
		@SideOnly(Side.CLIENT)
		public ItemStack getIconItemStack() {
			if (icon == null) {
				icon = new ItemStack(FruitItems.HANDHELD);
				icon.setTagCompound(new NBTTagCompound());
			}
			int iTicks = (int)ClientProxy.ticks;
			if (iTicks%20 == 0) {
				int col = FruitRecipes.craftableColors.get((iTicks/20) % FruitRecipes.craftableColors.size());
				icon.getTagCompound().setInteger("fruitphone:color", col);
			}
			return icon;
		}

		@Override
		public ItemStack getTabIconItem() {
			return getIconItemStack();
		}
		
	};
	
	@SidedProxy(clientSide="com.elytradev.fruitphone.proxy.ClientProxy", serverSide="com.elytradev.fruitphone.proxy.Proxy")
	public static Proxy proxy;
	
	@Instance
	public static FruitPhone inst;
	
	public boolean optionalMode;
	
	@CapabilityInject(FruitEquipmentCapability.class)
	public Capability<FruitEquipmentCapability> CAPABILITY_EQUIPMENT;
	public NetworkContext NETWORK;
	
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
				+ "connected to servers that lack the mod."
				+ "\n"
				+ "If you just want to have the mod pretend you're wearing Fruit\n"
				+ "Glass at all times, use /gamerule fruitphone:alwaysOn true");
		
		config.save();
		
		if (!optionalMode) {
			RecipeSorter.register("fruitphone:upgrade", FruitUpgradeRecipe.class, Category.SHAPED, "after:forge:shapedore");
			
			FruitItems.register();
			FruitRecipes.register();
			
			CapabilityManager.INSTANCE.register(FruitEquipmentCapability.class, new FruitEquipmentStorage(), FruitEquipmentCapability::new);
			
			proxy.preInit();
		}
		
		NETWORK = NetworkContext.forChannel("FruitPhone")
				.register(EquipmentDataPacket.class)
				.register(SetAlwaysOnPacket.class);
		
		MinecraftForge.EVENT_BUS.register(proxy);
		MinecraftForge.EVENT_BUS.register(this);
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
	
	@SubscribeEvent
	public void onStartTracking(PlayerEvent.StartTracking e) {
		EquipmentDataPacket.forEntity(e.getTarget()).ifPresent((m) -> m.sendTo(e.getEntityPlayer()));
	}
	
	@SubscribeEvent
	public void onPlayerJoin(PlayerLoggedInEvent e) {
		new SetAlwaysOnPacket(e.player.world.getGameRules().getBoolean("fruitphone:alwaysOn")).sendTo(e.player);
		EquipmentDataPacket.forEntity(e.player).ifPresent((m) -> m.sendTo(e.player));
	}
	
	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load e) {
		if (!e.getWorld().getGameRules().getBoolean("fruitphone:alwaysOn")) {
			// boolean rules default to false, so if it's false then we can set it to false
			// this has no effect if the gamerule already existed, and adds it to tabcomplete if it didn't
			// the important part is it won't overwrite an already-true gamerule
			e.getWorld().getGameRules().addGameRule("fruitphone:alwaysOn", "false", ValueType.BOOLEAN_VALUE);
		}
		World world = e.getWorld();
		GameRulePoller.forBooleanRule("fruitphone:alwaysOn", world, (newValue) -> {
			new SetAlwaysOnPacket(newValue).sendToAllIn(world);
		});
	}
	
	@SubscribeEvent
	public void onCapabilityAttach(AttachCapabilitiesEvent.Entity e) {
		if (optionalMode) return;
		if (e.getObject() instanceof EntityPlayer) {
			e.addCapability(new ResourceLocation(MODID, "equipment"), new FruitEquipmentCapability());
		}
	}
	
	@SubscribeEvent
	public void onClone(PlayerEvent.Clone e) {
		if (optionalMode) return;
		if (e.isWasDeath()) {
			e.getEntityPlayer().getCapability(CAPABILITY_EQUIPMENT, null).copyFrom(e.getOriginal().getCapability(CAPABILITY_EQUIPMENT, null));
		}
	}
}
