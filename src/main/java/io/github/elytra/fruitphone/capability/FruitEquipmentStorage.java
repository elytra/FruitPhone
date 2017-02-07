package io.github.elytra.fruitphone.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class FruitEquipmentStorage implements IStorage<FruitEquipmentCapability> {

	@Override
	public NBTBase writeNBT(Capability<FruitEquipmentCapability> capability, FruitEquipmentCapability instance, EnumFacing side) {
		return instance.serializeNBT();
	}

	@Override
	public void readNBT(Capability<FruitEquipmentCapability> capability, FruitEquipmentCapability instance, EnumFacing side, NBTBase nbt) {
		if (nbt instanceof NBTTagCompound) {
			instance.deserializeNBT((NBTTagCompound)nbt);
		}
	}

}
