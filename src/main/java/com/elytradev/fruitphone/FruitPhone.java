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

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.elytradev.fruitphone.compat.waila.WailaCompat;
import com.elytradev.fruitphone.item.FruitItems;
import com.elytradev.fruitphone.network.EquipmentDataPacket;
import com.elytradev.fruitphone.network.ProbeDataPacket;
import com.elytradev.fruitphone.network.SetAlwaysOnPacket;
import com.elytradev.fruitphone.proxy.ClientProxy;
import com.elytradev.fruitphone.proxy.Proxy;
import com.elytradev.fruitphone.recipe.FruitRecipes;
import com.elytradev.fruitphone.recipe.FruitUpgradeRecipe;
import com.elytradev.fruitphone.repackage.com.elytradev.concrete.NetworkContext;
import com.elytradev.fruitphone.vanilla.VanillaProviders;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import cofh.api.energy.IEnergyStorage;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import com.elytradev.probe.api.IProbeData;
import com.elytradev.probe.api.IProbeDataProvider;
import com.elytradev.probe.api.IUnit;
import com.elytradev.probe.api.UnitDictionary;
import com.elytradev.probe.api.impl.ProbeData;
import com.elytradev.probe.api.impl.SIUnit;
import com.elytradev.probe.api.impl.Unit;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.common.network.NetworkCheckHandler;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;

@Mod(modid=FruitPhone.MODID, name=FruitPhone.NAME, version=FruitPhone.VERSION, dependencies=FruitPhone.DEPENDENCIES)
public class FruitPhone {
	
	public static final String MODID = "fruitphone";
	public static final String NAME = "Fruit Phone";
	public static final String VERSION = "@VERSION@";
	public static final String DEPENDENCIES = "after:Waila";
	
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
		public Item getTabIconItem() {
			return FruitItems.HANDHELD;
		}
		
	};
	
	@SidedProxy(clientSide="com.elytradev.fruitphone.proxy.ClientProxy", serverSide="com.elytradev.fruitphone.proxy.Proxy")
	public static Proxy proxy;
	
	@Instance
	public static FruitPhone inst;
	
	public Configuration config;
	
	public Gravity glassesGravity;
	
	public int glassesXOffset;
	public int glassesYOffset;
	
	public float maxGlassesWidth;
	public float maxGlassesHeight;
	
	public float glassesScale;
	
	public boolean optionalMode;
	public boolean disableWaila;
	public boolean showWailaInformation;
	public boolean synthesizeProbeInformation;
	
	public NetworkContext NETWORK;
	
	private final Field classToNameMap = ReflectionHelper.findField(TileEntity.class, "field_145853_j", "classToNameMap", "g");
	
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent e) {
		config = new Configuration(e.getSuggestedConfigurationFile());
		
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
				+ "connected to servers that lack the mod.\n"
				+ "\n"
				+ "If you just want to have the mod pretend you're wearing Fruit\n"
				+ "Glass at all times, use /gamerule fruitphone:alwaysOn true\n"
				+ "\n");
		
		disableWaila = config.getBoolean("disableWaila", "General", true,
				"If true and Waila is installed, Fruit Phone will disable it. Automatically turned off on first run.");
		showWailaInformation = config.getBoolean("showWailaInformation", "General", true,
				"If true and Waila is installed, Fruit Phone will display information from Waila plugins.");
		synthesizeProbeInformation = config.getBoolean("synthesizeProbeInformation", "General", true,
				"If true, Fruit Phone will look for known interfaces and add probe data (e.g. bars) for them. 1.7.10 exclusive option.");
	
		Gravity[] grav = Gravity.values();
		String[] valid = new String[grav.length];
		
		for (int i = 0; i < valid.length; i++) {
			valid[i] = grav[i].toString();
		}
		
		glassesGravity = Gravity.valueOf(config.getString("gravity", "Glasses", "north_west", "The gravity for the glasses overlay.", valid).toUpperCase(Locale.ROOT));
		glassesXOffset = config.getInt("xOffset", "Glasses", 10, 0, 65535, "The X offset for the glasses overlay, dependent on gravity");
		glassesYOffset = config.getInt("yOffset", "Glasses", 10, 0, 65535, "The Y offset for the glasses overlay, dependent on gravity");
		maxGlassesWidth = config.getFloat("maxWidth", "Glasses", 100/3f, 0, 100, "The maximum width for the glasses overlay, as a percentage of the screen width")/100f;
		maxGlassesHeight = config.getFloat("maxHeight", "Glasses", 200/3f, 0, 100, "The maximum height for the glasses overlay, as a percentage of the screen height")/100f;
		glassesScale = config.getFloat("scale", "Glasses", 100, 0, 100000, "The scale for the glasses overlay, as a percentage of the default")/100f;
		
		config.setCategoryComment("Glasses", "Configuration for the glasses overlay. This can be configured ingame much more easily with the Power Drill.");
		
		if (!Loader.isModLoaded("Waila")) {
			showWailaInformation = false;
			disableWaila = false;
		}
		
		config.save();
		
		if (!optionalMode) {
			RecipeSorter.register("fruitphone:upgrade", FruitUpgradeRecipe.class, Category.SHAPED, "after:forge:shapedore");
			
			FruitItems.register();
			FruitRecipes.register();
			
			proxy.preInit();
		}
		
		NETWORK = NetworkContext.forChannel("FruitPhone")
				.register(EquipmentDataPacket.class)
				.register(SetAlwaysOnPacket.class)
				.register(ProbeDataPacket.class);
		
		FMLCommonHandler.instance().bus().register(proxy);
		FMLCommonHandler.instance().bus().register(this);
		MinecraftForge.EVENT_BUS.register(proxy);
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	public void saveConfig() {
		config.get("Glasses", "gravity", "north_west").set(glassesGravity.toString());
		config.get("Glasses", "xOffset", 10).set(glassesXOffset);
		config.get("Glasses", "yOffset", 10).set(glassesYOffset);
		config.get("Glasses", "maxWidth", 100/3f).set(maxGlassesWidth*100f);
		config.get("Glasses", "maxHeight", 200/3f).set(maxGlassesHeight*100f);
		config.get("Glasses", "scale", 100f).set(glassesScale*100f);
		
		config.get("General", "optional", false).set(optionalMode);
		
		config.save();
	}
	
	@EventHandler
	public void onPostInit(FMLPostInitializationEvent e) {
		UnitDictionary dict = UnitDictionary.getInstance();
		for(Fluid fluid : FluidRegistry.getRegisteredFluids().values()) {
			Unit fluidUnit = new SIUnit("buckets_"+fluid.getName(), "B", fluid.getColor());
			dict.register(fluidUnit, fluid);
		}
		proxy.postInit();
		if (disableWaila) {
			WailaCompat.init();
			config.get("General", "disableWaila", true).set(false);
			config.save();
		}
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
	public void onConstruct(EntityConstructing e) {
		if (optionalMode) return;
		if (e.entity instanceof EntityPlayer) {
			e.entity.registerExtendedProperties("fruitphone:equipment", new FruitEquipmentProperties());
		}
	}
	
	@SubscribeEvent
	public void onStartTracking(PlayerEvent.StartTracking e) {
		EquipmentDataPacket.forEntity(e.target).transform((m) -> {m.sendTo(e.entityPlayer); return Void.TYPE;});
	}
	
	@SubscribeEvent
	public void onPlayerJoin(PlayerLoggedInEvent e) {
		new SetAlwaysOnPacket(e.player.worldObj.getGameRules().getGameRuleBooleanValue("fruitphone:alwaysOn")).sendTo(e.player);
		EquipmentDataPacket.forEntity(e.player).transform((m) -> {m.sendTo(e.player); return Void.TYPE;});
	}
	
	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load e) {
		if (!e.world.getGameRules().getGameRuleBooleanValue("fruitphone:alwaysOn")) {
			// boolean rules default to false, so if it's false then we can set it to false
			// this has no effect if the gamerule already existed, and adds it to tabcomplete if it didn't
			// the important part is it won't overwrite an already-true gamerule
			e.world.getGameRules().addGameRule("fruitphone:alwaysOn", "false");
		}
		World world = e.world;
		GameRulePoller.forBooleanRule("fruitphone:alwaysOn", world, (newValue) -> {
			log.info("Always-on mode {}abled", newValue ? "en" : "dis");
			new SetAlwaysOnPacket(newValue).sendToAllIn(world);
		});
	}
	
	@SubscribeEvent
	public void onSwitchDimensions(PlayerChangedDimensionEvent e) {
		new SetAlwaysOnPacket(e.player.worldObj.getGameRules().getGameRuleBooleanValue("fruitphone:alwaysOn")).sendTo(e.player);
		EquipmentDataPacket.forEntity(e.player).transform((m) -> {m.sendTo(e.player); return Void.TYPE;});
	}
	
	@SubscribeEvent
	public void onPlayerClone(PlayerEvent.Clone e) {
		if (!e.wasDeath) {
			FruitEquipmentProperties orig = ((FruitEquipmentProperties)e.original.getExtendedProperties("fruitphone:equipment"));
			FruitEquipmentProperties nw = ((FruitEquipmentProperties)e.entityPlayer.getExtendedProperties("fruitphone:equipment"));
			nw.glasses = orig.glasses;
		}
	}
	
	private Map<EntityPlayer, ProbeDataPacket> lastData = new WeakHashMap<>();
	
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent e) {
		if (e.phase == Phase.START) {
			if (e.player.worldObj.isRemote) return;
			Vec3 eyes = Vec3.createVectorHelper(e.player.posX, e.player.posY + e.player.getEyeHeight(), e.player.posZ);;
			Vec3 look = e.player.getLookVec();
			double dist = 4;
			Vec3 max = eyes.addVector(look.xCoord * dist, look.yCoord * dist, look.zCoord * dist);
			MovingObjectPosition rtr = e.player.worldObj.rayTraceBlocks(eyes, max, false, false, false);
			if (rtr != null && rtr.typeOfHit == MovingObjectType.BLOCK) {
				List<IProbeData> list = Lists.newArrayList();
				int x = rtr.blockX;
				int y = rtr.blockY;
				int z = rtr.blockZ;
				TileEntity te = e.player.worldObj.getTileEntity(x, y, z);
				if (te != null) {
					NBTTagCompound tag = generateProbeData(e.player, te, ForgeDirection.getOrientation(rtr.sideHit), list);
					ProbeDataPacket pkt = new ProbeDataPacket(x, y, z, list, tag);
					if (!Objects.equal(pkt, lastData.get(e.player))) {
						pkt.sendTo(e.player);
					}
					lastData.put(e.player, pkt);
				} else {
					lastData.remove(e.player);
				}
			}
		}
	}
	
	public NBTTagCompound generateProbeData(EntityPlayer player, TileEntity te, ForgeDirection sideHit, List<IProbeData> list) {
		NBTTagCompound tag = new NBTTagCompound();
		try {
			if (player instanceof EntityPlayerMP && showWailaInformation) {
				Map<Class<? extends TileEntity>, String> classToNameMap;
				try {
					classToNameMap = (Map<Class<? extends TileEntity>, String>) this.classToNameMap.get(null);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				boolean hasBlockOrTile = false;
				if (ModuleRegistrar.instance().hasNBTProviders(te.getBlockType())) {
					hasBlockOrTile = true;
				}
				if (ModuleRegistrar.instance().hasNBTProviders(te)) {
					hasBlockOrTile = true;
				}
				if (hasBlockOrTile) {
					tag.setInteger("x", te.xCoord);
					tag.setInteger("y", te.yCoord);
					tag.setInteger("z", te.zCoord);
					tag.setString("id", classToNameMap.get(te.getClass()));
					
					for (List<IWailaDataProvider> li : ModuleRegistrar.instance().getNBTProviders(te.getBlockType()).values()) {
						for (IWailaDataProvider iwdp : li) {
							tag = iwdp.getNBTData((EntityPlayerMP)player, te, tag, player.worldObj, te.xCoord, te.yCoord, te.zCoord);
						}
					}
					for (List<IWailaDataProvider> li : ModuleRegistrar.instance().getNBTProviders(te).values()) {
						for (IWailaDataProvider iwdp : li) {
							tag = iwdp.getNBTData((EntityPlayerMP)player, te, tag, player.worldObj, te.xCoord, te.yCoord, te.zCoord);
						}
					}
				}
				tag.setInteger("WailaX", te.xCoord);
				tag.setInteger("WailaY", te.yCoord);
				tag.setInteger("WailaZ", te.zCoord);
				tag.setString("WailaID", classToNameMap.get(te.getClass()));
			}
		} catch (Throwable t) {
			log.warn("Exception thrown while building Waila data for {}, {}, {} in DIM{}",
					te.xCoord, te.yCoord, te.zCoord, te.getWorld().provider.dimensionId, t);
			tag = new NBTTagCompound();
			list.add(new ProbeData()
					.withLabel(new ChatComponentTranslation("fruitphone.wailaError")));
		}
		if (!synthesizeProbeInformation) {
			// If the user doesn't want probe-native info, we're done here.
			return tag;
		}
		try {
			if (te instanceof IProbeDataProvider) {
				((IProbeDataProvider)te).provideProbeData(list);
				return tag;
			}
		} catch (Throwable t) {
			log.warn("Exception thrown while building probe data for {}, {}, {} in DIM{}",
					te.xCoord, te.yCoord, te.zCoord, te.getWorld().provider.dimensionId, t);
			list.clear();
			list.add(new ProbeData()
					.withLabel(new ChatComponentTranslation("fruitphone.probeError")));
			return new NBTTagCompound();
		}
		
		try {
			VanillaProviders.provideProbeData(te, list);
			
			if (te instanceof TileEntityChest) {
				TileEntityChest c = ((TileEntityChest)te);
				Block b = te.getBlockType();
				if (b instanceof BlockChest) {
					c.checkForAdjacentChests();
					addItemData(list, ((BlockChest)b).getInventory(te.getWorld(), te.xCoord, te.yCoord, te.zCoord));
				} else {
					addItemData(list, (IInventory)te);
				}
			} else if (te instanceof IInventory) {
				addItemData(list, (IInventory)te);
			}
			if (te instanceof IEnergyStorage) {
				addEnergyData(list, (IEnergyStorage)te);
			}
			if (te instanceof IFluidHandler) {
				addFluidData(list, (IFluidHandler)te);
			}
			
			return tag;
		} catch (Throwable t) {
			log.warn("Exception thrown while building default probe data for {}, {}, {} in DIM{}",
					te.xCoord, te.yCoord, te.zCoord, te.getWorld().provider.dimensionId, t);
			list.clear();
			list.add(new ProbeData()
					.withLabel(new ChatComponentTranslation("fruitphone.capError")));
			return new NBTTagCompound();
		}
	}

	private void addItemData(List<IProbeData> list, IInventory item) {
		List<ItemStack> is = Lists.newArrayListWithCapacity(item.getSizeInventory());
		for (int i = 0; i < item.getSizeInventory(); i++) {
			ItemStack stack = item.getStackInSlot(i);
			is.add(stack == null ? null : stack.copy());
		}
		list.add(new ProbeData()
				.withInventory(is));
	}

	private void addFluidData(List<IProbeData> list, IFluidHandler fluid) {
		for (FluidTankInfo tank : fluid.getTankInfo(ForgeDirection.UNKNOWN)) {
			IUnit unit;
			int amt;
			if (tank.fluid == null) {
				unit = UnitDictionary.BUCKETS_ANY;
				amt = 0;
			} else {
				unit = UnitDictionary.getInstance().getUnit(tank.fluid.getFluid());
				amt = tank.fluid.amount;
			}
			list.add(new ProbeData()
					.withBar(0, amt/1000D, tank.capacity/1000D, unit));
		}
	}

	private void addEnergyData(List<IProbeData> list, IEnergyStorage energy) {
		list.add(new ProbeData()
				.withBar(0, energy.getEnergyStored(), energy.getMaxEnergyStored(), UnitDictionary.FORGE_ENERGY));
	}

	@SubscribeEvent
	public void onDrops(PlayerDropsEvent e) {
		if (optionalMode) return;
		FruitEquipmentProperties props = (FruitEquipmentProperties)e.entityPlayer.getExtendedProperties("fruitphone:equipment");
		ItemStack glasses = props.glasses;
		if (glasses != null) {
			e.entityPlayer.entityDropItem(glasses, 1.2f);
		}
	}
}
