package mcp.mobius.waila.cbcore;

import net.minecraft.client.resources.I18n;

public class LangUtil {

	public static String translateG(String key, Object... format) {
		return I18n.format(key, format);
	}

}
