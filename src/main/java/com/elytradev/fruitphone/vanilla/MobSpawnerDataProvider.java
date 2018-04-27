package com.elytradev.fruitphone.vanilla;

import java.util.List;

import com.elytradev.probe.api.IProbeData;
import com.elytradev.probe.api.impl.ProbeData;
import com.google.common.collect.ImmutableList;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class MobSpawnerDataProvider  implements VanillaDataProvider<TileEntityMobSpawner> {
	
	@Override
	public void provideProbeData(TileEntityMobSpawner te, List<IProbeData> li) {
		NBTTagCompound tag = new NBTTagCompound();
		te.writeToNBT(tag);
		NBTTagCompound spawnData = tag.getCompoundTag("SpawnData");
		if (spawnData.hasKey("id")) {
			String entityId = spawnData.getString("id");
			EntityEntry entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(entityId));
			
			TextComponentTranslation title = new TextComponentTranslation("fruitphone.mobSpawner.title", new TextComponentTranslation("entity."+entry.getName()+".name"), new TextComponentTranslation("tile.mobSpawner.name"));
			
			li.add(new ProbeData()
					.withInventory(ImmutableList.of(new ItemStack(Blocks.MOB_SPAWNER)))
					.withLabel(title));
		}
		
	}

}
