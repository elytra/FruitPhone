package io.github.elytra.fruitphone.capability;

import io.github.elytra.fruitphone.FruitPhone;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.Constants.NBT;

public class FruitEquipmentCapability implements ICapabilityProvider, INBTSerializable<NBTTagCompound> {

	public ItemStack glasses = ItemStack.EMPTY;
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == FruitPhone.inst.CAPABILITY_EQUIPMENT;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return capability == FruitPhone.inst.CAPABILITY_EQUIPMENT ? (T)this : null;
	}

	public void copyFrom(FruitEquipmentCapability that) {
		this.glasses = that.glasses.copy();
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		if (!glasses.isEmpty()) {
			tag.setTag("Glasses", glasses.serializeNBT());
		}
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("Glasses", NBT.TAG_COMPOUND)) {
			glasses = new ItemStack(nbt.getCompoundTag("Glasses"));
		} else {
			glasses = ItemStack.EMPTY;
		}
	}

}
