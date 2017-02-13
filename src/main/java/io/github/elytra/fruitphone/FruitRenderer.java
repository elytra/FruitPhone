package io.github.elytra.fruitphone;

import org.lwjgl.util.Dimension;

import io.github.elytra.fruitphone.proxy.ClientProxy;
import io.github.elytra.fruitphone.proxy.Rendering;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Public API. Feel free to re-use the intentionally generic Fruit Phone
 * renderer for other things.
 * <p>
 * Breaking API or ABI compatibility will be avoided, though if neccessary will
 * still happen. Let unascribed know if you use this API so you can be informed
 * if it's going to get broken.
 * <p>
 * <i>Should</i> work when passed sizes that are not used by Fruit Phone itself,
 * though I obviously don't really test that. Anything that causes the renderer
 * to act weird on nonstandard sizes should be considered a bug.
 */
@SideOnly(Side.CLIENT)
public class FruitRenderer {

	private static final ResourceLocation SPINNER = new ResourceLocation("fruitphone", "textures/gui/spinner.png");
	
	public static void renderFruit(int width, int height) {
		World world = Minecraft.getMinecraft().theWorld;
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		RayTraceResult rtr = Minecraft.getMinecraft().objectMouseOver;
		
		
		
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1, 1, 1);
		renderSpinner((width-16)/2, (height-16)/2);
		GlStateManager.disableBlend();
	}
	
	public static void renderSpinner(int x, int y) {
		Rendering.bindTexture(SPINNER);
		int tocks = (int)(ClientProxy.ticks/2);
		Gui.drawModalRectWithCustomSizedTexture(x, y, 16*tocks, 0, 16, 16, 96, 16);
	}

	public static Dimension calculateFruitSize() {
		return new Dimension(0, 0);
	}
	
}
