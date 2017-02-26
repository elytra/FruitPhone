package com.elytradev.fruitphone.compat.waila;

import com.elytradev.fruitphone.FruitPhone;

import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.utils.Constants;
import net.minecraftforge.common.config.Configuration;

public class WailaCompat {

	public static void init() {
		FruitPhone.log.info("Messing with Waila");
		ConfigHandler.instance().setConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SHOW, false);
	}

}
