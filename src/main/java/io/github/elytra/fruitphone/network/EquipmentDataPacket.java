package io.github.elytra.fruitphone.network;

import java.util.Optional;

import io.github.elytra.concrete.Message;
import io.github.elytra.concrete.NetworkContext;
import io.github.elytra.concrete.annotation.field.MarshalledAs;
import io.github.elytra.concrete.annotation.type.ReceivedOn;
import io.github.elytra.fruitphone.FruitPhone;
import io.github.elytra.fruitphone.capability.FruitEquipmentCapability;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@ReceivedOn(Side.CLIENT)
public class EquipmentDataPacket extends Message {

	@MarshalledAs("i32")
	public int entityId;
	public NBTTagCompound tag;
	
	public EquipmentDataPacket(NetworkContext ctx) {
		super(ctx);
	}
	
	public EquipmentDataPacket(int entityId, NBTTagCompound tag) {
		super(FruitPhone.inst.NETWORK);
		this.entityId = entityId;
		this.tag = tag;
	}
	

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityPlayer sender) {
		Entity entity = sender.worldObj.getEntityByID(entityId);
		if (entity.hasCapability(FruitPhone.inst.CAPABILITY_EQUIPMENT, null)) {
			entity.getCapability(FruitPhone.inst.CAPABILITY_EQUIPMENT, null).deserializeNBT(tag);
		}
	}
	
	public static Optional<EquipmentDataPacket> forEntity(Entity e) {
		if (e.hasCapability(FruitPhone.inst.CAPABILITY_EQUIPMENT, null)) {
			FruitEquipmentCapability fec = e.getCapability(FruitPhone.inst.CAPABILITY_EQUIPMENT, null);
			return Optional.of(new EquipmentDataPacket(e.getEntityId(), fec.serializeNBT()));
		} else {
			return Optional.empty();
		}
	}

}
