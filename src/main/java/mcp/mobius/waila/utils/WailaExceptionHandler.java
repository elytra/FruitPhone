package mcp.mobius.waila.utils;

import java.util.List;

import com.elytradev.fruitphone.FruitPhone;

public class WailaExceptionHandler {

	public static List<String> handleErr(Throwable e, String name, List<String> tip) {
		FruitPhone.log.error("Caught exception while getting Waila data from {}", name, e);
		if (tip != null) {
			tip.add("<ERROR>");
		}
		return tip;
	}

}
