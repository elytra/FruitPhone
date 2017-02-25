package mcp.mobius.waila.api.impl;

import mcp.mobius.waila.api.IWailaConfigHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import java.util.*;

public class ConfigHandler implements IWailaConfigHandler {

    /* SINGLETON */
    private static ConfigHandler _instance = null;
    private ConfigHandler() {
        _instance = this;
    }

    public void addModule(String modName, HashMap<String, String> options) {
    	// FruitPhone: no-op
    }

    public void addModule(String modName, ConfigModule options) {
    	// FruitPhone: no-op
    }

    @Override
    public Set<String> getModuleNames() {
    	// FruitPhone: no-op
        return Collections.emptySet();
    }

    @Override
    public HashMap<String, String> getConfigKeys(String modName) {
    	// FruitPhone: no-op
        return null;
    }

    public void addConfig(String modName, String key, String name) {
    	// FruitPhone: no-op
    }

    public void addConfig(String modName, String key, String name, boolean defvalue) {
    	// FruitPhone: no-op
    }

    public void addConfigServer(String modName, String key, String name) {
    	// FruitPhone: no-op
    }

    public void addConfigServer(String modName, String key, String name, boolean defvalue) {
    	// FruitPhone: no-op
    }

    @Override
    public boolean getConfig(String key) {
    	// FruitPhone: no-op
        return false;
    }

    @Override
    public boolean getConfig(String key, boolean defvalue) {
    	// FruitPhone: no-op
        return defvalue;
    }

    public boolean isServerRequired(String key) {
    	// FruitPhone: no-op
        return false;
    }

    public boolean getConfig(String category, String key, boolean default_) {
    	// FruitPhone: no-op
    	return default_;
    }

	
	
	
	
	
	/* GENERAL ACCESS METHODS TO GET/SET VALUES IN THE CONFIG FILE */

    public void setConfig(String category, String key, boolean state) {
    	// FruitPhone: no-op
    }

    public String getConfig(String category, String key, String default_) {
    	// FruitPhone: no-op
        return default_;
    }

    public void setConfig(String category, String key, String value) {
    	// FruitPhone: no-op
    }

    public int getConfig(String category, String key, int default_) {
    	// FruitPhone: no-op
    	return default_;
    }

    public void setConfig(String category, String key, int state) {
    	// FruitPhone: no-op
    }

    /* Some accessor helpers */
    public boolean showTooltip() {
        return true; // FruitPhone: no-op
    }

    public boolean hideFromList() {
        return true; // FruitPhone: no-op
    }

    public void loadDefaultConfig(FMLPreInitializationEvent event) {
        // FruitPhone: no-op
    }
	
	
	
	/* Default config loading */

    public static ConfigHandler instance() {
        return _instance == null ? new ConfigHandler() : _instance;
    }
}
