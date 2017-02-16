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

import java.util.List;

import org.lwjgl.util.Dimension;

import com.elytradev.fruitphone.proxy.ClientProxy;
import com.elytradev.fruitphone.proxy.Rendering;

import io.github.elytra.probe.api.IProbeData;
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
	
	/**
	 * Sync data for the object the player is looking at and then render it. A
	 * spinner will be rendered while syncing.
	 * @param width The maximum width of the render
	 * @param height The maximum height of the render
	 */
	public static void renderAndSyncTarget(int width, int height) {
		World world = Minecraft.getMinecraft().world;
		EntityPlayer player = Minecraft.getMinecraft().player;
		RayTraceResult rtr = Minecraft.getMinecraft().objectMouseOver;
		
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1, 1, 1);
		renderSpinner((width-16)/2, (height-16)/2);
		GlStateManager.disableBlend();
	}
	
	/**
	 * Formats the given data, making it better for display.
	 * <p>
	 * How this is done is intentionally left undefined. Do not rely on this
	 * method doing something specific, it is likely to change drastically in
	 * the future.
	 * @param data The IProbeData lines to clean
	 * @return The cleaned IProbeData lines
	 */
	public static List<IProbeData> format(List<IProbeData> data) {
		return data; // TODO
	}
	
	/**
	 * Render the given data.
	 * @param data The IProbeData lines to render
	 * @see #format
	 */
	public static void render(List<IProbeData> data) {
		
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
