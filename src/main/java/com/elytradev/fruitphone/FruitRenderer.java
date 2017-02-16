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
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import io.github.elytra.probe.api.IProbeData;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
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
	
	// TODO entity support
	
	public static BlockPos currentDataPos;
	public static List<IProbeData> currentFormattedData;
	public static List<IProbeData> currentRawData;
	
	/**
	 * Sync data for the object the player is looking at and then render it. A
	 * spinner will be rendered while syncing.
	 * @param width The maximum width of the render
	 * @param height The maximum height of the render
	 */
	public static void renderAndSyncTarget(int width, int height) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		
		Vec3d eyes = player.getPositionEyes(ClientProxy.partialTicks);
		Vec3d look = player.getLook(ClientProxy.partialTicks);
		double dist = 4;
		Vec3d max = eyes.addVector(look.xCoord * dist, look.yCoord * dist, look.zCoord * dist);
		RayTraceResult rtr = player.world.rayTraceBlocks(eyes, max, false, false, false);
		
		if (rtr == null || rtr.typeOfHit != Type.BLOCK) return;
		
		BlockPos pos = rtr.getBlockPos();
		
		if (!Objects.equal(pos, currentDataPos)) {
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.color(1, 1, 1);
			renderSpinner(0, 0);
			GlStateManager.disableBlend();
		} else {
			if (currentRawData != null) {
				currentFormattedData = format(currentRawData, currentDataPos);
				currentRawData = null;
			}
			render(currentFormattedData);
		}
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
	public static List<IProbeData> format(List<IProbeData> data, BlockPos src) {
		List<IProbeData> newData = Lists.newArrayList();
		IBlockState b = Minecraft.getMinecraft().world.getBlockState(src);
		ItemStack pickblock = b.getBlock().getPickBlock(b, Minecraft.getMinecraft().objectMouseOver, Minecraft.getMinecraft().world, src, Minecraft.getMinecraft().player);
		FruitProbeData ident = new FruitProbeData();
		ident.withInventory(ImmutableList.of(pickblock));
		ident.withLabel(pickblock.getDisplayName());
		newData.add(ident);
		boolean first = true;
		for (IProbeData ipd : data) {
			if (first && ipd.hasBar() && !ident.hasBar() && (ipd.hasLabel() ? Strings.isNullOrEmpty(ipd.getBarUnit()) : false) && !ipd.hasInventory()) {
				FruitProbeData nw = new FruitProbeData();
				ident.withBar(ipd.getBarMinimum(), ipd.getBarCurrent(), ipd.getBarMaximum(), ipd.getBarUnit());
				if (ipd.hasLabel()) {
					if (Strings.isNullOrEmpty(ipd.getBarUnit())) {
						ident.setBarLabel(ipd.getLabel().getFormattedText());
					} else {
						nw.withLabel(ipd.getLabel());
					}
				}
				if (ipd.hasInventory()) {
					nw.withInventory(ipd.getInventory());
				}
				newData.add(nw);
			} else {
				newData.add(ipd);
			}
			first = false;
		}
		return newData;
	}
	
	/**
	 * Render the given data.
	 * @param data The IProbeData lines to render
	 * @see #format
	 */
	public static void render(List<IProbeData> data) {
		int x = 0;
		int y = 0;
		for (IProbeData d : data) {
			int lineSize = 0;
			int textPosY = y+2;
			boolean renderLabel = true;
			if (d.hasInventory() && !d.getInventory().isEmpty()) {
				RenderHelper.enableGUIStandardItemLighting();
				if (d.getInventory().size() == 1) {
					Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(d.getInventory().get(0), x, y);
					x += 20;
					textPosY += 2;
					lineSize = Math.max(lineSize, 20);
				}
				RenderHelper.disableStandardItemLighting();
			}
			if (d.hasBar()) {
				int barY = textPosY;
				if (d.hasLabel()) {
					textPosY -= 4;
					barY += 6;
				}
				
				float maxNormalized = d.getBarMaximum()-d.getBarMinimum();
				float currentNormalized = d.getBarCurrent()-d.getBarMinimum();
				float zero = (d.getBarMinimum() < 0 ? -d.getBarMinimum() : 0);
				
				int startX = (int)(x+1+((zero/maxNormalized)*69));
				int endX = (int)(x+1+((currentNormalized/maxNormalized)*69));
				
				Gui.drawRect(x, barY, x+70, barY+11, -1);
				GlStateManager.translate(0, 0, 40);
				Gui.drawRect(x+1, barY+1, x+69, barY+10, 0xFF000000);
				GlStateManager.translate(0, 0, 40);
				Gui.drawRect(startX, barY+1, endX, barY+10, 0xFFAA0000);
				
				GlStateManager.translate(0, 0, 40);
				String str;
				if (d instanceof FruitProbeData && ((FruitProbeData) d).getBarLabel() != null) {
					str = ((FruitProbeData) d).getBarLabel();
				} else if (d.hasLabel() && Strings.isNullOrEmpty(d.getBarUnit())) {
					str = d.getLabel().getFormattedText();
					renderLabel = false;
				} else {
					str = d.getBarCurrent()+d.getBarUnit();
				}
				FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
				fr.drawString(str, (x+69)-fr.getStringWidth(str), barY+2, -1, false);
				
				lineSize = Math.max(lineSize, d.hasLabel() ? 22 : 12);
			}
			if (renderLabel && d.hasLabel()) {
				Minecraft.getMinecraft().fontRenderer.drawString(d.getLabel().getFormattedText(), x, textPosY, -1, false);
				lineSize = Math.max(lineSize, 12);
			}
			if (d.hasInventory() && d.getInventory().size() > 1) {
				y += lineSize;
				lineSize = 18;
				for (ItemStack is : d.getInventory()) {
					if (is == null) is = ItemStack.EMPTY;
					Gui.drawRect(x+1, y+1, x+17, y+17, 0x22FFFFFF);
					Gui.drawRect(x, y, x+17, y+1, 0x77FFFFFF);
					Gui.drawRect(x+1, y+17, x+18, y+18, 0xDDFFFFFF);
					Gui.drawRect(x, y+1, x+1, y+17, 0x77FFFFFF);
					Gui.drawRect(x+17, y+1, x+18, y+17, 0xDDFFFFFF);
					Gui.drawRect(x, y+17, x+1, y+18, 0xAAFFFFFF);
					Gui.drawRect(x+17, y, x+18, y+1, 0xAAFFFFFF);
					RenderHelper.enableGUIStandardItemLighting();
					Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(is, x+1, y+1);
					Minecraft.getMinecraft().getRenderItem().renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRenderer, is, x+1, y+1, null);
					RenderHelper.disableStandardItemLighting();
					x += 18;
					if (x > 80) {
						x = 0;
						y += 18;
					}
				}
			}
			y += lineSize;
			x = 0;
		}
	}
	
	public static void renderSpinner(int x, int y) {
		Rendering.bindTexture(SPINNER);
		int tocks = (int)(ClientProxy.ticksConsiderPaused/2);
		Gui.drawModalRectWithCustomSizedTexture(x, y, 16*tocks, 0, 16, 16, 96, 16);
	}

	public static Dimension calculateFruitSize() {
		return new Dimension(0, 0);
	}
	
}
