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
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

@SuppressWarnings("deprecation")
public class MobSpawnerDataProvider  implements VanillaDataProvider<TileEntityMobSpawner> {
	
	@Override
	public void provideProbeData(TileEntityMobSpawner te, List<IProbeData> li) {
		NBTTagCompound tag = new NBTTagCompound();
		te.writeToNBT(tag);
		NBTTagCompound spawnData = tag.getCompoundTag("SpawnData");
		if (spawnData.hasKey("id")) {
			String entityId = spawnData.getString("id");
			EntityEntry entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(entityId));
			String entityName = I18n.translateToLocal("entity."+entry.getName()+".name");
			String mobSpawner = I18n.translateToLocal("tile.mobSpawner.name");
			
			li.add(new ProbeData()
					.withInventory(ImmutableList.of(new ItemStack(Blocks.MOB_SPAWNER)))
					.withLabel(I18n.translateToLocalFormatted("%1$s %2$s", entityName, mobSpawner)));
		}
		
	}

}
