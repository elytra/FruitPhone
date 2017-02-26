/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2017 Aesen 'unascribed' Vismea
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

package com.elytradev.fruitphone.client.render;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class Rendering {

	public static void bindTexture(ResourceLocation resloc) {
		Minecraft.getMinecraft().renderEngine.bindTexture(resloc);
	}
	
	public static void drawRect(int left, int top, int right, int bottom, int color) {
		Gui.drawRect(left, top, right, bottom, color);
	}
	
	public static void color3(int color) {
		color4(color | 0xFF000000);
	}
	
	public static void color4(int color) {
		float a = (color >> 24 & 255) / 255f;
		float r = (color >> 16 & 255) / 255f;
		float g = (color >> 8 & 255) / 255f;
		float b = (color & 255) / 255f;
		
		GlStateManager.color(r, g, b, a);
	}
	
	public static void drawRect(double left, double top, double right, double bottom, int color) {
		if (left < right) {
			double swap = left;
			left = right;
			right = swap;
		}

		if (top < bottom) {
			double swap = top;
			top = bottom;
			bottom = swap;
		}

		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tessellator.getBuffer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(
				GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
				GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);
		color4(color);
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION);
		vertexbuffer.pos(left, bottom, 0).endVertex();
		vertexbuffer.pos(right, bottom, 0).endVertex();
		vertexbuffer.pos(right, top, 0).endVertex();
		vertexbuffer.pos(left, top, 0).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}
	
	public static void drawTexturedRect(double left, double top, double right, double bottom, float minU, float minV, float maxU, float maxV, int color) {
		if (left < right) {
			double swap = left;
			left = right;
			right = swap;
		}

		if (top < bottom) {
			double swap = top;
			top = bottom;
			bottom = swap;
		}

		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tessellator.getBuffer();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(
				GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
				GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);
		color4(color);
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos(left, bottom, 0).tex(minU, maxV).endVertex();
		vertexbuffer.pos(right, bottom, 0).tex(maxU, maxV).endVertex();
		vertexbuffer.pos(right, top, 0).tex(maxU, minV).endVertex();
		vertexbuffer.pos(left, top, 0).tex(minU, minV).endVertex();
		tessellator.draw();
		GlStateManager.disableBlend();
	}
	
	public static void drawTexturedRect(double left, double top, double right, double bottom, TextureAtlasSprite textureSprite) {
		if (left < right) {
			double swap = left;
			left = right;
			right = swap;
		}

		if (top < bottom) {
			double swap = top;
			top = bottom;
			bottom = swap;
		}
		
		GlStateManager.color(1, 1, 1);
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tessellator.getBuffer();
		double maxU = textureSprite.getMinU();
		double maxV = textureSprite.getMinV();
		double minU = textureSprite.getInterpolatedU(left-right);
		double minV = textureSprite.getInterpolatedV(top-bottom);
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos(left, bottom, 0).tex(minU, maxV).endVertex();
		vertexbuffer.pos(right, bottom, 0).tex(maxU, maxV).endVertex();
		vertexbuffer.pos(right, top, 0).tex(maxU, minV).endVertex();
		vertexbuffer.pos(left, top, 0).tex(minU, minV).endVertex();
		tessellator.draw();
	}
	
	
	private static final DummyScreen GUI = new DummyScreen();
	
	
	public static void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
		GUI.drawGradientRect(left, top, right, bottom, startColor, endColor);
	}
	
	public static void drawHorizontalLine(int startX, int endX, int y, int color) {
		GUI.drawHorizontalLine(startX, endX, y, color);
	}
	
	public static void drawHoveringText(List<String> textLines, int x, int y) {
		GUI.drawHoveringText(textLines, x, y);
	}
	
	public static void drawHoveringText(List<String> textLines, int x, int y, FontRenderer font) {
		GUI.drawHoveringText(textLines, x, y, font);
	}
	
	public static void drawVerticalLine(int x, int startY, int endY, int color) {
		GUI.drawVerticalLine(x, startY, endY, color);
	}
	
	public static void renderToolTip(ItemStack stack, int x, int y) {
		GUI.renderToolTip(stack, x, y);
	}
	
	public static void drawBackground(int tint) {
		GUI.drawBackground(tint);
	}
	
	public static void drawCenteredString(FontRenderer fontRendererIn, String text, int x, int y, int color) {
		GUI.drawCenteredString(fontRendererIn, text, x, y, color);
	}
	
	public static void drawDefaultBackground() {
		GUI.drawDefaultBackground();
	}
	
	public static void drawString(FontRenderer fontRendererIn, String text, int x, int y, int color) {
		GUI.drawString(fontRendererIn, text, x, y, color);
	}
	
	public static void drawTexturedModalRect(float xCoord, float yCoord, int minU, int minV, int maxU, int maxV) {
		GUI.drawTexturedModalRect(xCoord, yCoord, minU, minV, maxU, maxV);
	}
	
	public static void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
		GUI.drawTexturedModalRect(x, y, textureX, textureY, width, height);
	}
	
	public static void drawTexturedModalRect(int xCoord, int yCoord, TextureAtlasSprite textureSprite, int widthIn, int heightIn) {
		GUI.drawTexturedModalRect(xCoord, yCoord, textureSprite, widthIn, heightIn);
	}
	
	public static void drawWorldBackground(int tint) {
		GUI.drawWorldBackground(tint);
	}

	
	private Rendering() {}
	
	private static class DummyScreen extends GuiScreen {

		@Override
		public void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
			super.drawGradientRect(left, top, right, bottom, startColor, endColor);
		}
		
		@Override
		public void drawHorizontalLine(int startX, int endX, int y, int color) {
			super.drawHorizontalLine(startX, endX, y, color);
		}
		
		@Override
		public void drawHoveringText(List<String> textLines, int x, int y) {
			super.drawHoveringText(textLines, x, y);
		}
		
		@Override
		public void drawHoveringText(List<String> textLines, int x, int y, FontRenderer font) {
			super.drawHoveringText(textLines, x, y, font);
		}
		
		@Override
		public void drawVerticalLine(int x, int startY, int endY, int color) {
			super.drawVerticalLine(x, startY, endY, color);
		}
		
		@Override
		public void renderToolTip(ItemStack stack, int x, int y) {
			super.renderToolTip(stack, x, y);
		}
		
		
	}

}
