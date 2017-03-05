package com.elytradev.fruitphone.repackage.com.elytradev.concrete;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.elytradev.fruitphone.repackage.com.elytradev.concrete.Message;
import com.elytradev.fruitphone.repackage.com.elytradev.concrete.NetworkContext;
import com.elytradev.fruitphone.repackage.com.elytradev.concrete.WireField;
import com.elytradev.fruitphone.repackage.com.elytradev.concrete.annotation.field.Optional;
import com.elytradev.fruitphone.repackage.com.elytradev.concrete.exception.BadMessageException;
import com.elytradev.fruitphone.repackage.com.elytradev.concrete.exception.WrongSideException;
import com.elytradev.fruitphone.repackage.com.elytradev.concrete.reflect.instanciator.Instanciator;
import com.elytradev.fruitphone.repackage.com.elytradev.concrete.reflect.instanciator.Instanciators;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class NetworkContext {
	static final Logger log = LogManager.getLogger("Concrete");
	
	private static final String DEFAULT_PACKAGE;
		
	static {
		// we have to do this so the shadow plugin doesn't remap the string
		char[] c = {
				'c','o','m','.','e','l','y','t','r','a','d','e','v','.','c','o','n','c','r','e','t','e'
		};
		DEFAULT_PACKAGE = new String(c);
	}
	
	protected static final Map<Class<? extends Message>, Instanciator<? extends Message>> instanciators = Maps.newHashMap();
	
	protected final BiMap<Class<? extends Message>, Integer> packetIds = HashBiMap.create();
	protected final Map<Class<? extends Message>, List<WireField<?>>> marshallers = Maps.newHashMap();
	protected final Multiset<Class<? extends Message>> booleanCount = HashMultiset.create();
	protected final Multiset<Class<? extends Message>> optionalCount = HashMultiset.create();
	
	protected final String channel;
	
	private int nextPacketId = 0;
	
	private NetworkContext(String channel) {
		if (NetworkContext.class.getName().startsWith(DEFAULT_PACKAGE)) {
			if (!((Boolean)Launch.blackboard.get("fml.deobfuscatedEnvironment"))) {
				throw new RuntimeException("Concrete is designed to be shaded and must not be left in the default package! (Offending mod: "+Loader.instance().activeModContainer().getName()+")");
			} else {
				log.warn("Concrete is in the default package. This is not a fatal error, as you are in a development environment, but remember to repackage it!");
			}
		}
		this.channel = channel;
		NetworkRegistry.INSTANCE.newEventDrivenChannel(channel).register(this);;
	}
	
	public NetworkContext register(Class<? extends Message> clazz) {
		if (packetIds.containsKey(clazz)) {
			log.warn("{} was registered twice", clazz);
			return this;
		}
		packetIds.put(clazz, nextPacketId++);
		List<WireField<?>> fields = Lists.newArrayList();
		Class<?> cursor = clazz;
		while (cursor != null && cursor != Object.class) {
			for (Field f : cursor.getDeclaredFields()) {
				if (!Modifier.isTransient(f.getModifiers()) && !Modifier.isStatic(f.getModifiers())) {
					if (f.getType() == Boolean.TYPE) {
						booleanCount.add(clazz);
					}
					if (f.getAnnotation(Optional.class) != null) {
						optionalCount.add(clazz);
					}
					WireField<?> wf = new WireField<>(f);
					fields.add(wf);
				}
			}
			cursor = cursor.getSuperclass();
		}
		marshallers.put(clazz, fields);
		return this;
	}
	
	
	public String getChannel() {
		return channel;
	}
	
	
	
	protected FMLProxyPacket getPacketFrom(Message m) {
		if (!packetIds.containsKey(m.getClass())) throw new BadMessageException(m.getClass()+" is not registered");
		PacketBuffer payload = new PacketBuffer(Unpooled.buffer());
		payload.writeByte(packetIds.get(m.getClass()));
		int bools = booleanCount.count(m.getClass())+optionalCount.count(m.getClass());
		if (bools > 0) {
			List<Boolean> li = Lists.newArrayListWithCapacity(bools);
			for (WireField<?> wf : marshallers.get(m.getClass())) {
				if (wf.getType() == Boolean.TYPE) {
					li.add((Boolean)wf.get(m));
				} else if (wf.isOptional()) {
					li.add(wf.get(m) != null);
				}
			}
			for (int i = 0; i < (bools+7)/8; i++) {
				int by = 0;
				for (int j = i*8; j < Math.min(li.size(), i+8); j++) {
					if (li.get(j)) {
						by |= (1 << j);
					}
				}
				payload.writeByte(by);
			}
		}
		Iterator<WireField<?>> iter = Iterators.filter(marshallers.get(m.getClass()).iterator(), (it) -> it.getType() != Boolean.TYPE);
		while (iter.hasNext()) {
			WireField<?> wf = iter.next();
			wf.marshal(m, payload);
		}
		return new FMLProxyPacket(payload, channel);
	}


	@SubscribeEvent
	public void onServerCustomPacket(ServerCustomPacketEvent e) {
		ByteBuf payload = e.packet.payload();
		Message m = readPacket(e.side(), payload);
		m.doHandleServer(((NetHandlerPlayServer)e.handler).playerEntity);
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onClientCustomPacket(ClientCustomPacketEvent e) {
		ByteBuf payload = e.packet.payload();
		Message m = readPacket(e.side(), payload);
		m.doHandleClient();
	}
	
	
	private Message readPacket(Side side, ByteBuf payload) {
		int id = payload.readUnsignedByte();
		if (!packetIds.containsValue(id)) {
			throw new IllegalArgumentException("Unknown packet id "+id);
		}
		Class<? extends Message> clazz = packetIds.inverse().get(id);
		Message m;
		try {
			if (!instanciators.containsKey(clazz)) {
				Constructor<? extends Message> cons = clazz.getConstructor(NetworkContext.class);
				instanciators.put(clazz, Instanciators.from(cons));
			}
			m = instanciators.get(clazz).newInstance(this);
		} catch (Throwable t) {
			throw new BadMessageException("Cannot instanciate message class "+clazz, t);
		}
		if (m.getSide() != side) {
			throw new WrongSideException("Cannot receive packet of type "+clazz+" on side "+side);
		}
		Set<WireField<?>> present = Sets.newHashSetWithExpectedSize(marshallers.get(m.getClass()).size());
		int bools = booleanCount.count(m.getClass())+optionalCount.count(m.getClass());
		if (bools > 0) {
			List<Consumer<Boolean>> li = Lists.newArrayListWithCapacity(bools);
			for (WireField<?> wf : marshallers.get(m.getClass())) {
				if (wf.getType() == Boolean.TYPE) {
					li.add((b) -> ((WireField<Boolean>)wf).set(m, b));
					present.add(wf);
				} else if (wf.isOptional()) {
					li.add((b) -> { if (b) { present.add(wf); } });
				} else {
					present.add(wf);
				}
			}
			for (int i = 0; i < (bools+7)/8; i++) {
				int by = payload.readUnsignedByte();
				for (int j = i*8; j < Math.min(li.size(), i+8); j++) {
					boolean val = (by & (1 << (j-i))) != 0;
					li.get(j).accept(val);
				}
			}
		} else {
			for (WireField<?> wf : marshallers.get(m.getClass())) {
				present.add(wf);
			}
		}
		Iterator<WireField<?>> iter = Iterators.filter(marshallers.get(m.getClass()).iterator(), (it) -> it.getType() != Boolean.TYPE);
		while (iter.hasNext()) {
			WireField<?> wf = iter.next();
			if (present.contains(wf)) {
				wf.unmarshal(m, payload);
			}
		}
		return m;
	}
	
	
	public static NetworkContext forChannel(String channel) {
		if (channel.length() > 20)
			throw new IllegalArgumentException("Channel name too long, must be at most 20 characters");
		return new NetworkContext(channel);
	}


}
