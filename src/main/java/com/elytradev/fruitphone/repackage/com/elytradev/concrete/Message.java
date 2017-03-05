package com.elytradev.fruitphone.repackage.com.elytradev.concrete;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.elytradev.fruitphone.repackage.com.elytradev.concrete.NetworkContext;
import com.elytradev.fruitphone.repackage.com.elytradev.concrete.annotation.type.ReceivedOn;
import com.elytradev.fruitphone.repackage.com.elytradev.concrete.exception.BadMessageException;
import com.elytradev.fruitphone.repackage.com.elytradev.concrete.exception.WrongSideException;
import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class Message {
	private static final class ClassInfo {
		public final Side side;
		public ClassInfo(Side side) {
			this.side = side;
		}
	}
	private static final Map<Class<?>, ClassInfo> classInfo = Maps.newHashMap();
	
	
	private transient final NetworkContext ctx;
	
	private transient final Side side;
	
	public Message(NetworkContext ctx) {
		this.ctx = ctx;
		
		ClassInfo ci = classInfo.get(getClass());
		if (ci == null) {
			ReceivedOn ro = getClass().getDeclaredAnnotation(ReceivedOn.class);
			if (ro == null) {
				throw new BadMessageException("Must specify @ReceivedOn");
			} else {
				side = ro.value();
			}
			
			ci = new ClassInfo(side);
		} else {
			side = ci.side;
		}
		
	}
	
	@SideOnly(Side.CLIENT)
	void doHandleClient() {
		handle(Minecraft.getMinecraft().thePlayer);
	}
	
	void doHandleServer(EntityPlayer sender) {
		handle(sender);
	}
	
	protected abstract void handle(EntityPlayer sender);
	
	Side getSide() {
		return side;
	}
	
	/**
	 * For use on the server-side. Sends this Message to the given player.
	 */
	public final void sendTo(EntityPlayer player) {
		if (side.isServer()) wrongSide();
		if (player instanceof EntityPlayerMP) {
			for (Packet p : toClientboundVanillaPackets()) {
				((EntityPlayerMP)player).playerNetServerHandler.sendPacket(p);
			}
		}
	}
	
	
	/**
	 * For use on the server-side. Sends this Message to every player that is
	 * within the given radius of the given position. <i>It is almost always
	 * better to use {@link #sendToAllWatching(Entity)}, this is only useful for
	 * certain special cases.</i>
	 */
	public final void sendToAllAround(World world, Entity entity, double radius) {
		sendToAllAround(world, entity.posX, entity.posY, entity.posZ, radius);
	}
	
	/**
	 * For use on the server-side. Sends this Message to every player that is
	 * within the given radius of the given position. <i>It is almost always
	 * better to use {@link #sendToAllWatching(World, int, int, int)}, this is only
	 * useful for certain special cases.</i>
	 */
	public final void sendToAllAround(World world, int x, int y, int z, double radius) {
		sendToAllAround(world, x+0.5, y+0.5, z+0.5, radius);
	}
	
	/**
	 * For use on the server-side. Sends this Message to every player that is
	 * within the given radius of the given position.
	 */
	public final void sendToAllAround(World world, double x, double y, double z, double radius) {
		if (side.isServer()) wrongSide();
		double sq = radius*radius;
		List<Packet> packets = toClientboundVanillaPackets();
		for (EntityPlayerMP ep : (List<EntityPlayerMP>)world.playerEntities) {
			if (ep.getDistanceSq(x, y, z) <= sq) {
				for (Packet packet : packets) {
					ep.playerNetServerHandler.sendPacket(packet);
				}
			}
		}
	}
	
	/**
	 * For use on the server-side. Sends this Message to every player that can
	 * see the given block.
	 */
	public final void sendToAllWatching(World world, int x, int y, int z) {
		if (side.isServer()) wrongSide();
		if (world instanceof WorldServer) {
			WorldServer srv = (WorldServer)world;
			Chunk c = srv.getChunkFromBlockCoords(x, z);
			if (srv.getPlayerManager().func_152621_a(c.xPosition, c.zPosition)) {
				List<Packet> packets = toClientboundVanillaPackets();
				for (EntityPlayerMP ep : (List<EntityPlayerMP>)world.playerEntities) {
					if (srv.getPlayerManager().isPlayerWatchingChunk(ep, c.xPosition, c.zPosition)) {
						for (Packet packet : packets) {
							ep.playerNetServerHandler.sendPacket(packet);
						}
					}
				}
			}
		}
	}
	
	/**
	 * For use on the server-side. Sends this Message to every player that can
	 * see the given tile entity.
	 */
	public final void sendToAllWatching(TileEntity te) {
		sendToAllWatching(te.getWorld(), te.xCoord, te.yCoord, te.zCoord);
	}
	
	
	/**
	 * For use on the server-side. Sends this Message to every player that can
	 * see the given entity.
	 */
	public final void sendToAllWatching(Entity e) {
		if (side.isServer()) wrongSide();
		if (e.worldObj instanceof WorldServer) {
			WorldServer srv = (WorldServer)e.worldObj;
			List<Packet> packets = toClientboundVanillaPackets();
			for (Packet packet : packets) {
				srv.getEntityTracker().sendToAllTrackingEntity(e, packet);
			}
		}
	}
	
	/**
	 * For use on the server-side. Sends this Message to every player in the
	 * given world.
	 */
	public final void sendToAllIn(World world) {
		if (side.isServer()) wrongSide();
		List<Packet> packets = toClientboundVanillaPackets();
		for (EntityPlayerMP ep : (List<EntityPlayerMP>)world.playerEntities) {
			for (Packet packet : packets) {
				ep.playerNetServerHandler.sendPacket(packet);
			}
		}
	}
	
	
	/**
	 * For use on the server-side. Sends this Message to every player currently
	 * connected to the server. Use sparingly, you almost never need to send
	 * a packet to everyone.
	 */
	public final void sendToEveryone() {
		if (side.isServer()) wrongSide();
		List<Packet> packets = toClientboundVanillaPackets();
		for (EntityPlayerMP ep : (List<EntityPlayerMP>)FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList) {
			for (Packet packet : packets) {
				ep.playerNetServerHandler.sendPacket(packet);
			}
		}
	}
	
	/**
	 * For use on the <i>client</i>-side. This is the only valid method for use
	 * on the client side.
	 */
	@SideOnly(Side.CLIENT)
	public final void sendToServer() {
		if (side.isClient()) wrongSide();
		NetHandlerPlayClient conn = Minecraft.getMinecraft().getNetHandler();
		if (conn == null) throw new IllegalStateException("Cannot send a message while not connected");
		conn.addToSendQueue(toServerboundVanillaPacket());
	}
	
	/**
	 * Mainly intended for internal use, but can be useful for more complex
	 * use cases.
	 */
	public final Packet toServerboundVanillaPacket() {
		return ctx.getPacketFrom(this).toC17Packet();
	}
	
	/**
	 * Mainly intended for internal use, but can be useful for more complex
	 * use cases.
	 */
	public final List<Packet> toClientboundVanillaPackets() {
		return Collections.singletonList(ctx.getPacketFrom(this).toS3FPacket());
	}
	
	
	private void wrongSide() {
		throw new WrongSideException(getClass()+" cannot be sent from side "+side);
	}
}
