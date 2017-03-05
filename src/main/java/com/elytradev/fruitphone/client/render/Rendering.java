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

import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
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
		
		GL11.glColor4f(r, g, b, a);
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

		Tessellator tessellator = Tessellator.instance;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
		color4(color);
		tessellator.startDrawingQuads();
		tessellator.addVertex(left, bottom, 0);
		tessellator.addVertex(right, bottom, 0);
		tessellator.addVertex(right, top, 0);
		tessellator.addVertex(left, top, 0);
		tessellator.draw();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
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

		Tessellator tessellator = Tessellator.instance;
		GL11.glEnable(GL11.GL_BLEND);
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
		color4(color);
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(left, bottom, 0, minU, maxV);
		tessellator.addVertexWithUV(right, bottom, 0, maxU, maxV);
		tessellator.addVertexWithUV(right, top, 0, maxU, minV);
		tessellator.addVertexWithUV(left, top, 0, minU, minV);
		tessellator.draw();
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	public static void drawTexturedRect(double left, double top, double right, double bottom, IIcon textureSprite) {
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
		
		GL11.glColor3f(1, 1, 1);
		Tessellator tessellator = Tessellator.instance;
		double maxU = textureSprite.getMinU();
		double maxV = textureSprite.getMinV();
		double minU = textureSprite.getInterpolatedU(left-right);
		double minV = textureSprite.getInterpolatedV(top-bottom);
		tessellator.startDrawingQuads();;
		tessellator.addVertexWithUV(left, bottom, 0, minU, maxV);
		tessellator.addVertexWithUV(right, bottom, 0, maxU, maxV);
		tessellator.addVertexWithUV(right, top, 0, maxU, minV);
		tessellator.addVertexWithUV(left, top, 0, minU, minV);
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
	
	public static void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
		GUI.drawTexturedModalRect(x, y, textureX, textureY, width, height);
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
		public void drawHoveringText(List textLines, int x, int y) {
			super.drawHoveringText(textLines, x, y);
		}
		
		@Override
		public void drawHoveringText(List textLines, int x, int y, FontRenderer font) {
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
