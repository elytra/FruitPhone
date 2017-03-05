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

package com.elytradev.probe.api;

import java.text.NumberFormat;
import java.util.HashMap;

import javax.annotation.Nullable;

import com.elytradev.probe.api.impl.BinaryUnit;
import com.elytradev.probe.api.impl.SIUnit;
import com.elytradev.probe.api.impl.TicksUnit;
import com.elytradev.probe.api.impl.Unit;
import com.google.common.collect.HashBiMap;

import net.minecraftforge.fluids.Fluid;

/**
 * A central place for unit registrations. It's highly recommended to register your unit on both the client *and* the
 * server.
 */
public class UnitDictionary {
	private static UnitDictionary INSTANCE;
	
	//Fluids
	public static final Unit BUCKETS_ANY       = new SIUnit("buckets",          "B", 0x283593); //800 indigo
	//public static final Unit BUCKETS_WATER     = new SIUnit("buckets_water",    "B", 0x1976D2); //700 blue
	//public static final Unit BUCKETS_LAVA      = new SIUnit("buckets_lava",     "B", 0xFF8F00); //800 amber
	//public static final Unit BUCKETS_REDSTONE  = new SIUnit("buckets_redstone", "B", 0xE53935); //600 red
	//public static final Unit BUCKETS_OIL       = new SIUnit("buckets_oil",      "B", 0x212121); //900 grey
	//public static final Unit BUCKETS_STEAM     = new SIUnit("buckets_steam",    "B", 0xB0BEC5); //200 blue grey
	
	//Data
	public static final BinaryUnit BYTES       = new BinaryUnit("bytes",        "B",   0x76FF03); //A400 light green
	
	//Energy
	public static final Unit FORGE_ENERGY      = new SIUnit("forge_energy",     "FU",  0xD50000); //A700 red
	public static final Unit FU_PER_TICK       = new SIUnit("fu_per_tick",      "FU/t",0xD50000); //Also A700 red
	public static final Unit DANKS             = new SIUnit("danks",            "Dk",  0x512DA8); //700 deep purple (from Tesla capabilities)
	public static final Unit DANKS_PER_TICK    = new SIUnit("danks_per_tick",   "Dk/t",0x512DA8); //Also 700 deep purple
	
	//Temperature for ITemperature
	public static final Unit KELVIN            = new SIUnit("kelvin",           "°K",  0xFF0000); //Programmer Red
	
	public static final Unit PERCENT           = new Unit("percent",            "%",   0xAAAAAA, NumberFormat.getIntegerInstance(), false);
	
	//Time
	public static final Unit TICKS             = new TicksUnit("ticks",                0xAAAAAA); // Terrified Grey
	
	
	public static final UnitDictionary getInstance() {
		if (INSTANCE==null) INSTANCE = new UnitDictionary();
		return INSTANCE;
	}
	
	
	
	private HashMap<String, IUnit> registry = new HashMap<>();
	private HashBiMap<IUnit, Fluid> fluidUnits = HashBiMap.create();
	
	private UnitDictionary() {
		register(BUCKETS_ANY);
		
		register(BYTES);
		
		register(FORGE_ENERGY);
		register(FU_PER_TICK);
		register(DANKS);
		register(DANKS_PER_TICK);
		
		register(KELVIN);
		register(PERCENT);
		
		register(TICKS);
	}
	
	/**
	 * Register a unit with the dictionary.
	 * @param unit the unit to register.
	 */
	public void register(IUnit unit) {
		registry.put(unit.getFullName(), unit);
	}
	
	/**
	 * Registers this unit as a fluid
	 * @param unit the unit to register
	 * @param fluid the fluid the unit is associated with
	 */
	public void register(IUnit unit, Fluid fluid) {
		registry.put(unit.getFullName(), unit);
		fluidUnits.put(unit, fluid);
	}
	
	/**
	 * Finds the IUnit with the specified proper name
	 * @param fullName the name the IUnit was registered under
	 * @return the IUnit itself, or null if none was registered under that name.
	 */
	@Nullable
	public IUnit getUnit(String fullName) {
		return registry.get(fullName);
	}
	
	/**
	 * Returns true if the specified unit corresponds to buckets of a forge Fluid
	 * @param unit the Unit to test
	 * @return true if there is a known association between this Unit and a Fluid
	 */
	public boolean isFluid(IUnit unit) {
		return fluidUnits.containsKey(unit);
	}
	
	/**
	 * Finds the Fluid that the specified Unit is associated with
	 * @param unit the Unit to find a Fluid for
	 * @return the Fluid this Unit is associated with, or null if it isn't known to be a Fluid
	 */
	@Nullable
	public Fluid getFluid(IUnit unit) {
		return fluidUnits.get(unit);
	}
	
	/**
	 * Finds the IUnit that corresponds to this Fluid, if it exists
	 * @param fluid the Fluid to get a unit for
	 * @return the IUnit this Fluid is associated with, or null if this Fluid doesn't have a unit yet.
	 */
	@Nullable
	public IUnit getUnit(Fluid fluid) {
		return fluidUnits.inverse().get(fluid);
	}
}
