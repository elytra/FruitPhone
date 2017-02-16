package com.elytradev.fruitphone.network;

import com.elytradev.fruitphone.FruitPhone;
import com.elytradev.fruitphone.proxy.ClientProxy;

import io.github.elytra.concrete.Message;
import io.github.elytra.concrete.NetworkContext;
import io.github.elytra.concrete.annotation.type.ReceivedOn;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@ReceivedOn(Side.CLIENT)
public class SetAlwaysOnPacket extends Message {

	public boolean alwaysOn;
	
	public SetAlwaysOnPacket(NetworkContext ctx) {
		super(ctx);
	}
	
	public SetAlwaysOnPacket(boolean alwaysOn) {
		super(FruitPhone.inst.NETWORK);
		this.alwaysOn = alwaysOn;
	}
	

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityPlayer sender) {
		((ClientProxy)FruitPhone.proxy).alwaysOn = this.alwaysOn;
	}

}
