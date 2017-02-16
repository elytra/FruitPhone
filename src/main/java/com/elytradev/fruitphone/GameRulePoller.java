package com.elytradev.fruitphone;

import java.util.Objects;
import java.util.function.Consumer;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;

import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

public class GameRulePoller {

	private final String rule;
	private final Consumer<String> listener;
	private final World world;

	private String lastValue;

	private GameRulePoller(String rule, Consumer<String> listener, World world) {
		this.rule = rule;
		this.listener = listener;
		this.world = world;
		lastValue = world.getGameRules().getString(rule);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onTick(WorldTickEvent e) {
		if (e.world == this.world) {
			String value = world.getGameRules().getString(rule);
			if (!Objects.equals(lastValue, value)) {
				lastValue = value;
				listener.accept(value);
			}
		}
	}

	@SubscribeEvent
	public void onUnload(WorldEvent.Unload e) {
		if (e.getWorld() == world) {
			MinecraftForge.EVENT_BUS.unregister(this);
		}
	}




	public static GameRulePoller forBooleanRule(String rule, World world, Consumer<Boolean> listener) {
		return new GameRulePoller(rule, (str) -> {
			listener.accept(Boolean.parseBoolean(str));
		}, world);
	}

	public static GameRulePoller forStringRule(String rule, World world, Consumer<String> listener) {
		return new GameRulePoller(rule, listener, world);
	}

	public static GameRulePoller forIntegerRule(String rule, World world, Consumer<Integer> listener) {
		return new GameRulePoller(rule, (str) -> {
			Integer i = Ints.tryParse(str);
			if (i != null) {
				listener.accept(i);
			} else {
				listener.accept(Boolean.parseBoolean(str) ? 1 : 0);
			}
		}, world);
	}

	public static GameRulePoller forDoubleRule(String rule, World world, Consumer<Double> listener) {
		return new GameRulePoller(rule, (str) -> {
			Double i = Doubles.tryParse(str);
			if (i != null) {
				listener.accept(i);
			} else {
				listener.accept(Boolean.parseBoolean(str) ? 1.0 : 0.0);
			}
		}, world);
	}

}
