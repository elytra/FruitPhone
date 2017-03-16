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

package com.elytradev.fruitphone;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.elytradev.fruitphone.client.render.Rendering;
import com.elytradev.fruitphone.proxy.ClientProxy;
import com.google.common.collect.Lists;

import mcp.mobius.waila.api.SpecialChars;
import mcp.mobius.waila.api.impl.DataAccessorCommon;
import mcp.mobius.waila.api.impl.MetaDataProvider;
import mcp.mobius.waila.api.impl.TipList;
import mcp.mobius.waila.cbcore.Layout;
import com.elytradev.probe.api.IProbeData;
import com.elytradev.probe.api.UnitDictionary;
import com.elytradev.probe.api.impl.ProbeData;
import com.elytradev.probe.api.impl.SIUnit;
import com.elytradev.probe.api.impl.Unit;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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

	public static class MultiDataSize {
		public final DataSize clamped;
		public final DataSize actual;
		public MultiDataSize(DataSize clamped, DataSize actual) {
			this.clamped = clamped;
			this.actual = actual;
		}
		
	}

	public static class DataSize {
		private int width;
		private int height;
		
		public int getWidth() {
			return width;
		}
		
		public int getHeight() {
			return height;
		}
		
		
		
		public void setWidthIfGreater(int width) {
			if (width > this.width) this.width = width;
		}
		
		public void addWidth(int width) {
			this.width += width;
		}
		
		public void setWidth(int width) {
			this.width = width;
		}
		
		
		
		public void setHeightIfGreater(int height) {
			if (height > this.height) this.height = height;
		}
		
		public void setHeight(int height) {
			this.height = height;
		}
		
		public void addHeight(int height) {
			this.height += height;
		}
	}

	private static final ResourceLocation SPINNER = new ResourceLocation("fruitphone", "textures/gui/spinner.png");
	private static final ResourceLocation SLOT = new ResourceLocation("fruitphone", "textures/gui/slot.png");
	
	// TODO entity support
	
	public static boolean hasData = false;
	public static int currentDataPosX;
	public static int currentDataPosY;
	public static int currentDataPosZ;
	public static List<IProbeData> currentFormattedData;
	public static List<IProbeData> currentRawData;
	
	private static final Unit DUMMY_UNIT = new SIUnit("", "", 0, Unit.FORMAT_STANDARD, false);
	
	private static Object mdp;
	
	public static void renderAndSyncTarget(int width, int height, boolean lit) {
		DataSize preferred = calculateAndSyncTargetUnclamped(width, height, width, height);
		renderAndSyncTarget(width, height, lit, preferred);
	}
	public static void renderAndSyncTarget(int width, int height, boolean lit, DataSize preferred) {
		GL11.glPushMatrix();
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		World world = Minecraft.getMinecraft().theWorld;
		
		Vec3 eyes = player.getPosition(1).addVector(0, player.getEyeHeight(), 0);
		Vec3 look = player.getLook(ClientProxy.partialTicks);
		double dist = 4;
		Vec3 max = eyes.addVector(look.xCoord * dist, look.yCoord * dist, look.zCoord * dist);
		MovingObjectPosition rtr = player.worldObj.rayTraceBlocks(eyes, max, false, false, false);
		
		if (rtr == null || rtr.typeOfHit != MovingObjectType.BLOCK) return;
		
		int x = rtr.blockX;
		int y = rtr.blockY;
		int z = rtr.blockZ;
		
		if (!hasData || x != currentDataPosX || y != currentDataPosY || z != currentDataPosZ) {
			Block b = world.getBlock(x, y, z);
			int meta = world.getBlockMetadata(x, y, z);
			if (!b.hasTileEntity(meta)) {
				hasData = true;
				currentDataPosX = x;
				currentDataPosY = y;
				currentDataPosZ = z;
				currentRawData = Collections.emptyList();
			} else {
				render(format(Collections.emptyList(), x, y, z), width, height, lit, preferred);
				if (ClientProxy.doesServerHaveMod()) {
					GL11.glEnable(GL11.GL_BLEND);
					GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					GL11.glEnable(GL11.GL_DEPTH_TEST);
					GL11.glColor3f(1, 1, 1);
					GL11.glTranslatef(0, 0, 500);
					renderSpinner(0, 0);
					GL11.glTranslatef(0, 0, -500);
					GL11.glDisable(GL11.GL_BLEND);
					GL11.glPopMatrix();
				}
				return;
			}
		}
		if (currentRawData != null) {
			currentFormattedData = format(currentRawData, currentDataPosX, currentDataPosY, currentDataPosZ);
			currentRawData = null;
		}
		render(currentFormattedData, width, height, lit, preferred);
		GL11.glPopMatrix();
	}
	
	public static MultiDataSize calculateAndSyncTarget(int preferredWidth, int preferredHeight, int maxWidth, int maxHeight) {
		DataSize actual = calculateAndSyncTargetUnclamped(preferredWidth, preferredHeight, maxWidth, maxHeight);
		DataSize clamped = new DataSize();
		clamped.setWidth(Math.min(maxWidth, actual.getWidth()));
		clamped.setHeight(Math.min(maxHeight, actual.getHeight()));
		return new MultiDataSize(clamped, actual);
	}
	
	public static DataSize calculateAndSyncTargetUnclamped(int preferredWidth, int preferredHeight, int maxWidth, int maxHeight) {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		World world = Minecraft.getMinecraft().theWorld;
		
		Vec3 eyes = player.getPosition(1).addVector(0, player.getEyeHeight(), 0);
		Vec3 look = player.getLook(ClientProxy.partialTicks);
		double dist = 4;
		Vec3 max = eyes.addVector(look.xCoord * dist, look.yCoord * dist, look.zCoord * dist);
		MovingObjectPosition rtr = player.worldObj.rayTraceBlocks(eyes, max, false, false, false);
		
		if (rtr == null || rtr.typeOfHit != MovingObjectType.BLOCK) return new DataSize();
		
		int x = rtr.blockX;
		int y = rtr.blockY;
		int z = rtr.blockZ;
		
		if (!hasData || x != currentDataPosX || y != currentDataPosY || z != currentDataPosZ) {
			Block b = world.getBlock(x, y, z);
			int meta = world.getBlockMetadata(x, y, z);
			if (!b.hasTileEntity(meta)) {
				hasData = true;
				currentDataPosX = x;
				currentDataPosY = y;
				currentDataPosZ = z;
				currentRawData = Collections.emptyList();
			} else {
				return calculatePreferredDataSize(format(Collections.emptyList(), x, y, z), preferredWidth, preferredHeight, maxWidth, maxHeight);
			}
		}
		if (currentRawData != null) {
			currentFormattedData = format(currentRawData, currentDataPosX, currentDataPosY, currentDataPosZ);
			currentRawData = null;
		}
		return calculatePreferredDataSize(currentFormattedData, preferredWidth, preferredHeight, maxWidth, maxHeight);
	}
	
	/**
	 * Formats the given data, making it better for display.
	 * <p>
	 * How this is done is intentionally left undefined. Do not rely on this
	 * method doing something specific, it is likely to change drastically in
	 * the future.
	 * @param data The IProbeData lines to clean
	 * @param src The BlockPos that this data came from
	 * @return The cleaned IProbeData lines
	 */
	public static List<IProbeData> format(List<IProbeData> data, int srcX, int srcY, int srcZ) {
		List<IProbeData> newData = Lists.newArrayList();
		Block b = Minecraft.getMinecraft().theWorld.getBlock(srcX, srcY, srcZ);
		ItemStack pickblock = b.getPickBlock(Minecraft.getMinecraft().objectMouseOver, Minecraft.getMinecraft().theWorld, srcX, srcY, srcZ, Minecraft.getMinecraft().thePlayer);
		FruitProbeData ident = new FruitProbeData();
		if (pickblock != null) {
			ident.withInventory(Collections.singletonList(pickblock));
			ident.withLabel(pickblock.getDisplayName());
		}
		newData.add(ident);
		boolean first = true;
		for (IProbeData ipd : data) {
			if (ipd instanceof WailaProbeData) {
				newData.add(0, ipd);
			} else if (first && ipd.hasBar() && !ident.hasBar() && (ipd.hasLabel() ? ipd.getBarUnit() == null : false) && !ipd.hasInventory()) {
				FruitProbeData nw = new FruitProbeData();
				ident.withBar(ipd.getBarMinimum(), ipd.getBarCurrent(), ipd.getBarMaximum(), ipd.getBarUnit());
				if (ipd.hasLabel()) {
					if (ipd.getBarUnit() == null) {
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
	
	public static DataSize calculatePreferredDataSize(List<IProbeData> data, int preferredWidth, int preferredHeight, int maxWidth, int maxHeight) {
		data = injectWailaData(data);
		DataSize ds = new DataSize();
		int x = 0;
		int y = 0;
		int slots = 0;
		boolean onlyOne = data.size() == 1;
		for (IProbeData d : data) {
			int lineSize = 0;
			if (!onlyOne) {
				y += 2;
			}
			boolean renderLabel = true;
			if (d.hasInventory() && !d.getInventory().isEmpty() && d.getInventory().get(0) != null) {
				if (d.getInventory().size() == 1 && (d.hasLabel() || d.hasBar())) {
					ds.setWidthIfGreater(x+16);
					y -= 2;
					x += 20;
					lineSize = Math.max(lineSize, 16);
				}
			}
			if (d.hasBar()) {
				String str;
				if (d instanceof FruitProbeData && ((FruitProbeData) d).getBarLabel() != null) {
					str = ((FruitProbeData) d).getBarLabel();
				} else if (d.hasLabel() && d.getBarUnit() == null) {
					str = d.getLabel().getFormattedText();
					renderLabel = false;
				} else {
					str = d.getBarUnit() == null ? Unit.FORMAT_STANDARD.format(d.getBarCurrent()) : d.getBarUnit().format(d.getBarCurrent());
				}
				
				ds.setWidthIfGreater(preferredWidth);
				ds.setWidthIfGreater(x+4+(Minecraft.getMinecraft().fontRendererObj.getStringWidth(str)));
				
				lineSize = Math.max(lineSize, d.hasLabel() ? 22 : 11);
			}
			if (renderLabel && d.hasLabel()) {
				ds.setWidthIfGreater(x+(Minecraft.getMinecraft().fontRendererObj.getStringWidth(d.getLabel().getFormattedText())));
				lineSize = Math.max(lineSize, 8);
			}
			if (d.hasInventory() && ((!d.hasBar() && !d.hasLabel()) || d.getInventory().size() > 1)) {
				y += lineSize+2;
				if (d.getInventory().size() == 9) {
					ds.setWidthIfGreater(18*3);
					lineSize = 18*3;
				} else {
					slots += d.getInventory().size();
				}
			}
			y += lineSize;
			x = 0;
		}
		ds.setWidthIfGreater(x);
		ds.setHeightIfGreater(y);
		int slotsPerRow = Math.max(Math.min(9, maxWidth/18), 1);
		ds.setWidthIfGreater(x + (slots >= slotsPerRow ? 18*slotsPerRow : 18*slots));
		ds.addHeight(2+(slots/slotsPerRow)*18);
		if (slots % slotsPerRow > 0) {
			ds.addHeight(18);
		}
		return ds;
	}
	
	public static float getContainScale(int canvasWidth, int canvasHeight, int dataWidth, int dataHeight) {
		// no need to scale if the data fits
		if (dataWidth <= canvasWidth && dataHeight <= canvasHeight) return 1;
		return Math.min(((float)canvasWidth)/((float)dataWidth), ((float)canvasHeight)/((float)dataHeight));
	}
	
	public static void render(List<IProbeData> data, int width, int height, int maxWidth, int maxHeight, boolean glasses) {
		DataSize preferred = calculatePreferredDataSize(data, width, height, maxWidth, maxHeight);
		render(data, width, height, glasses, preferred);
	}
		
	public static void render(List<IProbeData> data, int width, int height, boolean glasses, DataSize preferred) {
		data = injectWailaData(data);
		
		
		float contain = getContainScale(width, height, preferred.width, preferred.height);
		
		if (!glasses) {
			preferred = calculatePreferredDataSize(data, (int)(width/contain), (int)(height/contain), (int)(width/contain), (int)(height/contain));
			contain = getContainScale(width, height, preferred.width, preferred.height);
		}
		
		if (Minecraft.getMinecraft().gameSettings.showDebugInfo) {
			Gui.drawRect(0, 0, width, height, 0xFF00FF00);
			GL11.glTranslatef(0, 0, 40);
		}
		GL11.glScalef(contain, contain, 1);
		if (Minecraft.getMinecraft().gameSettings.showDebugInfo) {
			Gui.drawRect(0, 0, preferred.width, preferred.height, 0xAAFF0000);
			GL11.glTranslatef(0, 0, 40);
		}
		
		int actualWidth = glasses ? width : (int) (width/contain);
		
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		
		int x = 0;
		int y = 0;
		for (IProbeData d : data) {
			int lineSize = 0;
			int textPosY = y+2;
			y += 2;
			boolean renderLabel = true;
			if (d.hasInventory() && !d.getInventory().isEmpty() && d.getInventory().get(0) != null) {
				RenderHelper.enableGUIStandardItemLighting();
				if (d.getInventory().size() == 1 && (d.hasLabel() || d.hasBar())) {
					RenderItem.getInstance().renderItemAndEffectIntoGUI(Minecraft.getMinecraft().fontRendererObj, Minecraft.getMinecraft().renderEngine, d.getInventory().get(0), x, y-2);
					RenderItem.getInstance().renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRendererObj, Minecraft.getMinecraft().renderEngine, d.getInventory().get(0), x, y-2, "");
					x += 20;
					if (d.hasBar()) {
						textPosY -= 2;
					} else {
						textPosY += 2;
					}
					lineSize = Math.max(lineSize, 16);
				}
				RenderHelper.disableStandardItemLighting();
			}
			if (d.hasBar()) {
				int barY = textPosY;
				if (d.hasLabel()) {
					barY += 10;
				}
				
				double maxNormalized = d.getBarMaximum()-d.getBarMinimum();
				double currentNormalized = d.getBarCurrent()-d.getBarMinimum();
				double zero = (d.getBarMinimum() < 0 ? -d.getBarMinimum() : 0);
				
				double startX = (x+1+((zero/maxNormalized)*((actualWidth-x)-2)));
				double endX = (x+1+((currentNormalized/maxNormalized)*((actualWidth-x)-2)));
				
				if (startX < x+1) {
					startX = x+1;
				} else if (startX > actualWidth-1) {
					startX = actualWidth-1;
				}
				
				if (endX > actualWidth-1) {
					endX = actualWidth-1;
				} else if (endX < x+1) {
					endX = x+1;
				}
				
				int color = d.getBarUnit() == null ? 0xFFAAAAAA : d.getBarUnit().getBarColor()|0xFF000000;
				
				Rendering.drawRect(x, barY, actualWidth, barY+11, -1);
				GL11.glTranslatef(0, 0, 40);
				Rendering.drawRect(x+1, barY+1, actualWidth-1, barY+10, 0xFF000000);
				GL11.glTranslatef(0, 0, 40);
				if (d.getBarUnit() != null && UnitDictionary.getInstance().isFluid(d.getBarUnit())) {
					Fluid f = UnitDictionary.getInstance().getFluid(d.getBarUnit());
					IIcon tex = f.getStillIcon();
					Rendering.bindTexture(TextureMap.locationBlocksTexture);
					int segments = (int)((endX-startX) / 16);
					for (int i = 0; i < segments; i++) {
						Rendering.drawTexturedRect(startX+(i*16), barY+1, startX+((i+1)*16), barY+10, tex);
					}
					Rendering.drawTexturedRect(startX+(segments*16), barY+1, endX, barY+10, tex);
				} else {
					Rendering.drawRect(startX, barY+1, endX, barY+10, color);
				}
				
				GL11.glTranslatef(0, 0, 40);
				String str;
				if (d instanceof FruitProbeData && ((FruitProbeData) d).getBarLabel() != null) {
					str = ((FruitProbeData) d).getBarLabel();
				} else if (d.hasLabel() && d.getBarUnit() == null) {
					str = d.getLabel().getFormattedText();
					renderLabel = false;
				} else {
					str = d.getBarUnit() == null ? Unit.FORMAT_STANDARD.format(d.getBarCurrent()) : d.getBarUnit().format(d.getBarCurrent());
				}
				FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
				GL11.glEnable(GL11.GL_BLEND);
				OpenGlHelper.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR,
						GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				fr.drawString(str, (actualWidth-1)-fr.getStringWidth(str), barY+2, -1, false);
				GL11.glDisable(GL11.GL_BLEND);
				
				lineSize = Math.max(lineSize, d.hasLabel() ? 22 : 11);
			}
			if (renderLabel && d.hasLabel()) {
				Minecraft.getMinecraft().fontRendererObj.drawString(d.getLabel().getFormattedText(), x, textPosY, -1, false);
				lineSize = Math.max(lineSize, 8);
			}
			if (d.hasInventory() && ((!d.hasBar() && !d.hasLabel()) || d.getInventory().size() > 1)) {
				y += lineSize+2;
				lineSize = 0;
				int perRow = d.getInventory().size() == 9 ? 3 : Math.min(9, actualWidth/18);
				int i = 0;
				for (ItemStack is : d.getInventory()) {
					Rendering.bindTexture(SLOT);
					GL11.glColor3f(1, 1, 1);
					GL11.glEnable(GL11.GL_BLEND);
					GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, 18, 18, 18, 18);
					if (is != null) {
						RenderHelper.enableGUIStandardItemLighting();
						int count = is.stackSize;
						RenderItem.getInstance().renderItemIntoGUI(Minecraft.getMinecraft().fontRendererObj, Minecraft.getMinecraft().renderEngine, is, x+1, y+1);
						RenderItem.getInstance().renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRendererObj, Minecraft.getMinecraft().renderEngine, is, x+1, y+1, count >= 100 ? "" : null);
						RenderHelper.disableStandardItemLighting();
						if (count >= 100) {
							GL11.glPushMatrix(); {
								GL11.glScalef(0.5f, 0.5f, 1);
								GL11.glTranslatef(0, 0, 400);
								String str = DUMMY_UNIT.format(count);
								FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
								fr.drawStringWithShadow(str, ((x*2)+34)-fr.getStringWidth(str), ((y*2)+34)-fr.FONT_HEIGHT, -1);
							} GL11.glPopMatrix();
						}
					}
					x += 18;
					i++;
					if (i >= perRow) {
						i = 0;
						x = 0;
						y += 18;
					}
				}
				if (i > 0) {
					lineSize = 18;
				}
			}
			y += lineSize;
			x = 0;
		}
		
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
	}

	private static List<IProbeData> injectWailaData(List<IProbeData> data) {
		if (FruitPhone.inst.showWailaInformation) {
			List<IProbeData> unchangedData = data;
			try {
				if (mdp == null) mdp = new MetaDataProvider();
				MetaDataProvider mdp = (MetaDataProvider)FruitRenderer.mdp;
				
				data = Lists.newArrayList(data);
				
				EntityPlayer player = Minecraft.getMinecraft().thePlayer;
				DataAccessorCommon dac = DataAccessorCommon.instance;
				MovingObjectPosition rtr = Minecraft.getMinecraft().objectMouseOver;
				World world = Minecraft.getMinecraft().theWorld;
				
				NBTTagCompound wailaData = null;
				
				Iterator<IProbeData> iter = data.iterator();
				while (iter.hasNext()) {
					IProbeData ipd = iter.next();
					if (ipd instanceof WailaProbeData) {
						iter.remove();
						wailaData = ((WailaProbeData) ipd).data;
					}
				}
				
				dac.set(world, player, rtr, null, ClientProxy.partialTicks);
				dac.setNBTData(wailaData);
		
				ItemStack stack = mdp.identifyBlockHighlight(world, player, rtr, dac);
				if (stack == null) {
					stack = data.get(0).hasInventory() ? data.get(0).getInventory().get(0) : null;
				} else {
					((ProbeData)data.get(0))
						.withInventory(Collections.singletonList(stack))
						.withLabel(stack.getDisplayName());
				}
				
				List<String> wailaHead = Collections.emptyList();//mdp.handleBlockTextData(stack, world, player, rtr, dac, new TipList<String, String>(), Layout.HEADER);
				List<String> wailaBody = mdp.handleBlockTextData(stack, world, player, rtr, dac, new TipList<String, String>(), Layout.BODY);
				List<String> wailaTail = mdp.handleBlockTextData(stack, world, player, rtr, dac, new TipList<String, String>(), Layout.FOOTER);
				
				int idx = 1;
				for (String s : wailaHead) {
					if ("<ERROR>".equals(s)) s = I18n.format("fruitphone.wailaError");
					if (s.startsWith(SpecialChars.RENDER)) continue;
					data.add(idx++, new ProbeData(s));
				}
				for (String s : wailaBody) {
					if ("<ERROR>".equals(s)) s = I18n.format("fruitphone.wailaError");
					if (s.startsWith(SpecialChars.RENDER)) continue;
					data.add(idx++, new ProbeData(s));
				}
				for (String s : wailaTail) {
					if ("<ERROR>".equals(s)) s = I18n.format("fruitphone.wailaError");
					if (s.startsWith(SpecialChars.RENDER)) continue;
					data.add(new ProbeData(s));
				}
			} catch (Throwable t) {
				data = Lists.newArrayList(unchangedData);
				FruitPhone.log.info("Error while retreiving Waila data", t);
				unchangedData.add(new ProbeData(new ChatComponentTranslation("fruitphone.wailaError")));
			}
		}
		return data;
	}
	
	public static void renderSpinner(int x, int y) {
		Rendering.bindTexture(SPINNER);
		int tocks = (int)(ClientProxy.ticksConsiderPaused/2);
		Gui.drawModalRectWithCustomSizedTexture(x, y, 16*tocks, 0, 15, 16, 96, 16);
	}

	
}

