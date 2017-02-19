package com.elytradev.fruitphone.network;

import java.util.List;

import com.elytradev.fruitphone.FruitPhone;
import com.elytradev.fruitphone.FruitRenderer;
import com.elytradev.concrete.Message;
import com.elytradev.concrete.NetworkContext;
import com.elytradev.concrete.annotation.field.MarshalledAs;
import com.elytradev.concrete.annotation.type.Asynchronous;
import com.elytradev.concrete.annotation.type.ReceivedOn;
import io.github.elytra.probe.api.IProbeData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@ReceivedOn(Side.CLIENT)
@Asynchronous
public class ProbeDataPacket extends Message {

	@MarshalledAs(ProbeDataListMarshaller.NAME)
	private List<IProbeData> data;
	private BlockPos pos;
	
	public ProbeDataPacket(NetworkContext ctx) {
		super(ctx);
	}
	
	public ProbeDataPacket(BlockPos pos, List<IProbeData> data) {
		super(FruitPhone.inst.NETWORK);
		this.pos = pos;
		this.data = data;
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityPlayer sender) {
		FruitRenderer.currentDataPos = pos;
		FruitRenderer.currentRawData = data;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((pos == null) ? 0 : pos.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ProbeDataPacket other = (ProbeDataPacket) obj;
		if (data == null) {
			if (other.data != null) {
				return false;
			}
		} else if (!data.equals(other.data)) {
			return false;
		}
		if (pos == null) {
			if (other.pos != null) {
				return false;
			}
		} else if (!pos.equals(other.pos)) {
			return false;
		}
		return true;
	}
	
	
	
	
	
}
