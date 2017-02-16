/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2017 Una Thompson (unascribed)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
