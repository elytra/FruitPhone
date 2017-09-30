/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2017 William Thompson (unascribed)
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

import com.elytradev.fruitphone.capability.FruitEquipmentCapability;
import com.elytradev.fruitphone.capability.FruitEquipmentStorage;
import com.elytradev.fruitphone.compat.waila.WailaCompat;
import com.elytradev.fruitphone.item.FruitItems;
import com.elytradev.fruitphone.network.EquipmentDataPacket;
import com.elytradev.fruitphone.network.ProbeDataPacket;
import com.elytradev.fruitphone.network.SetAlwaysOnPacket;
import com.elytradev.fruitphone.proxy.ClientProxy;
import com.elytradev.fruitphone.proxy.Proxy;
import com.elytradev.fruitphone.recipe.FruitRecipes;
import com.elytradev.fruitphone.recipe.FruitUpgradeRecipe;
import com.elytradev.fruitphone.vanilla.VanillaProviders;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import io.github.elytra.concrete.NetworkContext;
import com.elytradev.probe.api.IProbeData;
import com.elytradev.probe.api.IProbeDataProvider;
import com.elytradev.probe.api.IUnit;
import com.elytradev.probe.api.UnitDictionary;
import com.elytradev.probe.api.impl.ProbeData;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules.ValueType;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;

@Mod(modid=FruitPhone.MODID, name=FruitPhone.NAME, version=FruitPhone.VERSION, dependencies=FruitPhone.DEPENDENCIES)
public class FruitPhone {
	
	public static final String MODID = "fruitphone";
	public static final String NAME = "Fruit Phone";
	public static final String VERSION = "@VERSION@";
	public static final String DEPENDENCIES = "required-after:probedataprovider;after:waila";

	public static final String SYNC_TAG = "fruitphone:resyncNextTick";
	
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
	
	@CapabilityInject(FruitEquipmentCapability.class)
	public static Capability<FruitEquipmentCapability> CAPABILITY_EQUIPMENT;
	@CapabilityInject(IProbeDataProvider.class)
	public static Capability<IProbeDataProvider> CAPABILITY_PROBE;
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
			disableWaila = false;
			showWailaInformation = false;
		}
		
		config.save();
		
		if (!optionalMode) {
			RecipeSorter.register("fruitphone:upgrade", FruitUpgradeRecipe.class, Category.SHAPED, "after:forge:shapedore");
			
			FruitItems.register();
			FruitRecipes.register();
			FruitSounds.register();
			
			CapabilityManager.INSTANCE.register(FruitEquipmentCapability.class, new FruitEquipmentStorage(), FruitEquipmentCapability::new);
			
			proxy.preInit();
		}
		
		NETWORK = NetworkContext.forChannel("FruitPhone")
				.register(EquipmentDataPacket.class)
				.register(SetAlwaysOnPacket.class)
				.register(ProbeDataPacket.class);
		
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
	public void onStartTracking(PlayerEvent.StartTracking e) {
		EquipmentDataPacket.forEntity(e.getTarget()).ifPresent((m) -> m.sendTo(e.getEntityPlayer()));
	}
	
	@SubscribeEvent
	public void onPlayerJoin(PlayerLoggedInEvent e) {
		new SetAlwaysOnPacket(e.player.worldObj.getGameRules().getBoolean("fruitphone:alwaysOn")).sendTo(e.player);
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
			log.info("Always-on mode {}abled", newValue ? "en" : "dis");
			new SetAlwaysOnPacket(newValue).sendToAllIn(world);
		});
	}
	
	@SubscribeEvent
	public void onCapabilityAttachEntity(AttachCapabilitiesEvent<Entity> e) {
		if (optionalMode) return;
		if (e.getObject() instanceof EntityPlayer) {
			e.addCapability(new ResourceLocation(MODID, "equipment"), new FruitEquipmentCapability());
		}
	}
	
	@SubscribeEvent
	public void onSwitchDimensions(PlayerChangedDimensionEvent e) {
		new SetAlwaysOnPacket(e.player.worldObj.getGameRules().getBoolean("fruitphone:alwaysOn")).sendTo(e.player);
		EquipmentDataPacket.forEntity(e.player).ifPresent((m) -> m.sendTo(e.player));
	}
	
	@SubscribeEvent
	public void onPlayerClone(PlayerEvent.Clone e) {
		if (!e.getOriginal().hasCapability(CAPABILITY_EQUIPMENT, null) ||
				!e.getEntityPlayer().hasCapability(CAPABILITY_EQUIPMENT, null))
			return;

		if ((!e.isWasDeath() || e.getEntityPlayer().getEntityWorld().getGameRules().getBoolean("keepInventory"))) {
			e.getEntityPlayer().getCapability(CAPABILITY_EQUIPMENT, null).copyFrom(e.getOriginal().getCapability(CAPABILITY_EQUIPMENT, null));
			if (e.isWasDeath()) {
				e.getEntityPlayer().addTag(SYNC_TAG);
			}
		}
	}
	
	private Map<EntityPlayer, ProbeDataPacket> lastData = new WeakHashMap<>();
	
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent e) {
		if (e.phase == Phase.START) {
			if (e.player.worldObj.isRemote) return;
			if (e.player.removeTag(SYNC_TAG)) {
				EquipmentDataPacket.forEntity(e.player).ifPresent((m) -> m.sendTo(e.player));
			}
			Vec3d eyes = new Vec3d(e.player.posX, e.player.posY + e.player.getEyeHeight(), e.player.posZ);;
			Vec3d look = e.player.getLookVec();
			double dist = 4;
			Vec3d max = eyes.addVector(look.xCoord * dist, look.yCoord * dist, look.zCoord * dist);
			RayTraceResult rtr = e.player.worldObj.rayTraceBlocks(eyes, max, false, false, false);
			if (rtr != null && rtr.typeOfHit == Type.BLOCK) {
				List<IProbeData> list = Lists.newArrayList();
				BlockPos pos = rtr.getBlockPos();
				TileEntity te = e.player.worldObj.getTileEntity(pos);
				if (te != null) {
					NBTTagCompound tag = generateProbeData(e.player, te, rtr.sideHit, list);
					ProbeDataPacket pkt = new ProbeDataPacket(pos, list, tag);
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
	
	public NBTTagCompound generateProbeData(EntityPlayer player, TileEntity te, EnumFacing sideHit, List<IProbeData> list) {
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
					tag.setInteger("x", te.getPos().getX());
					tag.setInteger("y", te.getPos().getY());
					tag.setInteger("z", te.getPos().getZ());
					tag.setString("id", classToNameMap.get(te.getClass()));
					
					for (List<IWailaDataProvider> li : ModuleRegistrar.instance().getNBTProviders(te.getBlockType()).values()) {
						for (IWailaDataProvider iwdp : li) {
							tag = iwdp.getNBTData((EntityPlayerMP)player, te, tag, player.worldObj, te.getPos());
						}
					}
					for (List<IWailaDataProvider> li : ModuleRegistrar.instance().getNBTProviders(te).values()) {
						for (IWailaDataProvider iwdp : li) {
							tag = iwdp.getNBTData((EntityPlayerMP)player, te, tag, player.worldObj, te.getPos());
						}
					}
				}
				tag.setInteger("WailaX", te.getPos().getX());
				tag.setInteger("WailaY", te.getPos().getY());
				tag.setInteger("WailaZ", te.getPos().getZ());
				tag.setString("WailaID", classToNameMap.get(te.getClass()));
			}
		} catch (Throwable t) {
			log.warn("Exception thrown while building Waila data for {}, {}, {} in DIM{}",
					te.getPos().getX(), te.getPos().getY(), te.getPos().getZ(), te.getWorld().provider.getDimension(), t);
			tag = new NBTTagCompound();
			list.add(new ProbeData()
					.withLabel(new TextComponentTranslation("fruitphone.wailaError")));
		}
		
		try {
			if (te.hasCapability(CAPABILITY_PROBE, sideHit)) {
				te.getCapability(CAPABILITY_PROBE, sideHit).provideProbeData(list);
				return tag;
			} else if (te.hasCapability(CAPABILITY_PROBE, null)) {
				te.getCapability(CAPABILITY_PROBE, null).provideProbeData(list);
				return tag;
			}
		} catch (Throwable t) {
			log.warn("Exception thrown while building probe data for {}, {}, {} in DIM{}",
					te.getPos().getX(), te.getPos().getY(), te.getPos().getZ(), te.getWorld().provider.getDimension(), t);
			list.clear();
			list.add(new ProbeData()
					.withLabel(new TextComponentTranslation("fruitphone.probeError")));
			return new NBTTagCompound();
		}
		
		try {
			VanillaProviders.provideProbeData(te, list);
			
			// Be EXTREMELY careful when looking for sideless caps.
			// Some modders don't know about, or at least don't test, sideless capabilities.
			
			IEnergyStorage sidelessEnergy = null;
			try {
				if (te.hasCapability(CapabilityEnergy.ENERGY, null)) {
					sidelessEnergy = te.getCapability(CapabilityEnergy.ENERGY, null);
				}
			} catch (Throwable t) {}
			
			IFluidHandler sidelessFluid = null;
			try {
				if (te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
					sidelessFluid = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
				}
			} catch (Throwable t) {}
			
			IItemHandler sidelessItem = null;
			try {
				if (te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
					sidelessItem = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
				}
			} catch (Throwable t) {}
			if (sidelessItem == null && te instanceof IInventory) {
				sidelessItem = new InvWrapper((IInventory)te);
			}
			
			
			IEnergyStorage sidedEnergy = null;
			if (te.hasCapability(CapabilityEnergy.ENERGY, sideHit)) {
				sidedEnergy = te.getCapability(CapabilityEnergy.ENERGY, sideHit);
			}
			
			IFluidHandler sidedFluid = null;
			if (te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, sideHit)) {
				sidedFluid = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, sideHit);
			}
			
			IItemHandler sidedItem = null;
			if (te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, sideHit)) {
				sidedItem = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, sideHit);
			} else if (te instanceof ISidedInventory) {
				sidedItem = new SidedInvWrapper((ISidedInventory)te, sideHit);
			}

			// Again, be careful when attempting to use sideless caps. 
			
			if (sidelessEnergy != null) {
				try {
					addEnergyData(list, sidelessEnergy);
				} catch (Throwable t) {
					if (sidedEnergy != null) {
						addEnergyData(list, sidedEnergy);
					}
				}
			} else if (sidedEnergy != null) {
				addEnergyData(list, sidedEnergy);
			}
			
			if (sidelessFluid != null) {
				try {
					addFluidData(list, sidelessFluid);
				} catch (Throwable t) {
					if (sidedFluid != null) {
						addFluidData(list, sidedFluid);
					}
				}
			} else if (sidedFluid != null) {
				addFluidData(list, sidedFluid);
			}
			
			if (sidelessItem != null) {
				try {
					addItemData(list, sidelessItem);
				} catch (Throwable t) {
					if (sidedItem != null) {
						addItemData(list, sidedItem);
					}
				}
			} else if (sidedItem != null) {
				addItemData(list, sidedItem);
			}
			
			return tag;
		} catch (Throwable t) {
			log.warn("Exception thrown while building default probe data for {}, {}, {} in DIM{}",
					te.getPos().getX(), te.getPos().getY(), te.getPos().getZ(), te.getWorld().provider.getDimension(), t);
			list.clear();
			list.add(new ProbeData()
					.withLabel(new TextComponentTranslation("fruitphone.capError")));
			return new NBTTagCompound();
		}
	}

	private void addItemData(List<IProbeData> list, IItemHandler item) {
		List<ItemStack> is = Lists.newArrayListWithCapacity(item.getSlots());
		for (int i = 0; i < item.getSlots(); i++) {
			ItemStack stack = item.getStackInSlot(i);
			is.add(stack == null ? null : stack.copy());
		}
		list.add(new ProbeData()
				.withInventory(is));
	}

	private void addFluidData(List<IProbeData> list, IFluidHandler fluid) {
		for (IFluidTankProperties tank : fluid.getTankProperties()) {
			IUnit unit;
			int amt;
			if (tank.getContents() == null) {
				unit = UnitDictionary.BUCKETS_ANY;
				amt = 0;
			} else {
				unit = UnitDictionary.getInstance().getUnit(tank.getContents().getFluid());
				amt = tank.getContents().amount;
			}
			list.add(new ProbeData()
					.withBar(0, amt/1000D, tank.getCapacity()/1000D, unit));
		}
	}

	private void addEnergyData(List<IProbeData> list, IEnergyStorage energy) {
		list.add(new ProbeData()
				.withBar(0, energy.getEnergyStored(), energy.getMaxEnergyStored(), UnitDictionary.FORGE_ENERGY));
	}

	@SubscribeEvent
	public void onDrops(PlayerDropsEvent e) {
		if (optionalMode) return;
		if (e.getEntityPlayer().hasCapability(CAPABILITY_EQUIPMENT, null)) {
			ItemStack glasses = e.getEntityPlayer().getCapability(CAPABILITY_EQUIPMENT, null).glasses;
			if (glasses != null) {
				e.getEntityPlayer().entityDropItem(glasses, 1.2f);
			}
		}
	}
}
