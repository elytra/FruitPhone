package io.github.elytra.fruitphone.early;

import java.util.Map;

import net.minecraftforge.fml.relauncher.FMLInjectionData;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.Name;

@Name("FruitPhone")
public class FruitPhoneLoadingPlugin implements IFMLLoadingPlugin {

	@Override
	public String[] getASMTransformerClass() {
		// surprisingly enough, there's no ASM here!
		// this loading plugin just serves to inject a Waila dummy
		return null;
	}

	@Override
	public String getModContainerClass() {
		FMLInjectionData.containers.add("io.github.elytra.fruitphone.early.WailaDummyModContainer");
		return null;
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {

	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

}
