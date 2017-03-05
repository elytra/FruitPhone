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

package com.elytradev.fruitphone.client.gui;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.elytradev.fruitphone.FruitEquipmentProperties;
import com.elytradev.fruitphone.FruitPhone;
import com.elytradev.fruitphone.FruitProbeData;
import com.elytradev.fruitphone.FruitRenderer;
import com.elytradev.fruitphone.Gravity;
import com.elytradev.fruitphone.FruitRenderer.DataSize;
import com.elytradev.fruitphone.client.render.Rendering;
import com.elytradev.fruitphone.item.ItemFruitPassive;
import com.elytradev.fruitphone.proxy.ClientProxy;
import com.elytradev.probe.api.IProbeData;
import com.elytradev.probe.api.UnitDictionary;
import com.elytradev.probe.api.impl.ProbeData;
import com.elytradev.probe.api.impl.Unit;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;
import cpw.mods.fml.client.config.GuiButtonExt;

public class ScreenConfigureGlasses extends GuiScreen {

	private enum DragTarget {
		OVERLAY_SIZE,
		OVERLAY_POSITION,
		CLAMP_REGION_SIZE
	}
	
	private static final ResourceLocation CHECKBOX = new ResourceLocation("fruitphone", "textures/gui/checkbox.png");
	private static final ResourceLocation HANDLE = new ResourceLocation("fruitphone", "textures/gui/resize_handle.png");
	
	private static final double SQRT2 = Math.sqrt(2);
	
	private static final float[] CLAMP_REGION_SNAP_POINTS = {
			1/4f,
			1/3f,
			1/2f,
			2/3f,
			3/4f,
			1
		};;
	
	private boolean snapToGuides = true;
	
	private DragTarget dragTarget;
	
	private boolean dragSnapped = false;
	
	private boolean dragSnappedX = false;
	private boolean dragSnappedY = false;
	
	private int lastMouseX;
	private int lastMouseY;
	
	private Gravity dragGravity;
	private int dragX;
	private int dragY;
	
	private int overlayHandleX;
	private int overlayHandleY;
	
	private int clampHandleX;
	private int clampHandleY;
	
	private int checkboxX;
	private int checkboxY;
	
	private int x;
	private int y;
	private int objWidth;
	private int objHeight;
	
	private ScaledResolution res;
	
	private ItemStack iron = new ItemStack(Blocks.iron_ore, 0);
	private ItemStack gold = new ItemStack(Blocks.gold_ore, 0);
	private ItemStack coal = new ItemStack(Items.coal, 0);
	private ItemStack diamond = new ItemStack(Items.diamond, 0);
	private ItemStack cobble = new ItemStack(Blocks.cobblestone, 0);
	
	private ItemStack furnaceCoal = new ItemStack(Items.coal, Integer.MAX_VALUE);
	private ItemStack furnaceCobble = new ItemStack(Blocks.cobblestone, Integer.MAX_VALUE);
	private ItemStack furnaceStone = new ItemStack(Blocks.stone, 0);
	
	private long energy = Long.MAX_VALUE;
	
	private Supplier<List<IProbeData>> probeDataSupplier = this::magicBoxData;
	
	private ItemStack[] chestData = new ItemStack[54];
	
	private Random rand = new Random();
	
	public ScreenConfigureGlasses() {
		Arrays.fill(chestData, null);
	}
	
	@Override
	public void initGui() {
		super.initGui();
		Gravity g = FruitPhone.inst.glassesGravity.opposite();
		if (!g.isCorner()) {
			if (buttonList.isEmpty()) {
				g = Gravity.SOUTH_EAST;
			} else {
				return;
			}
		}
		buttonList.clear();
		int confX = g.resolveX(10, width, 108);
		int confY = g.resolveY(10, height, 64);
		buttonList.add(new GuiButtonExt(0, confX, confY+22, 108, 20, I18n.format("fruitphone.gui.restoreDefaults")));
		buttonList.add(new GuiButtonExt(1, confX, confY+44, 108, 20, I18n.format("gui.done")));
		buttonList.add(new GuiButtonExt(2, confX, confY, 20, 20, ""));
		buttonList.add(new GuiButtonExt(3, confX+22, confY, 20, 20, ""));
		buttonList.add(new GuiButtonExt(4, confX+44, confY, 20, 20, ""));
		buttonList.add(new GuiButtonExt(5, confX+66, confY, 20, 20, ""));
		buttonList.add(new GuiButtonExt(6, confX+88, confY, 20, 20, ""));
		checkboxX = g.flipVertical().resolveX(10, width, 10);
		checkboxY = g.flipVertical().resolveY(10, height, 10);
		res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
	}
	
	private List<IProbeData> magicBoxData() {
		return Arrays.asList(
				new ProbeData()
					.withLabel("Magic Box")
					.withInventory(Collections.singletonList(new ItemStack(Blocks.iron_block)))
					.withBar(0, (int)(ClientProxy.ticks%200)/2, 100, UnitDictionary.PERCENT),
				new ProbeData()
					.withBar(0, energy, Long.MAX_VALUE, UnitDictionary.DANKS),
				new ProbeData()
					.withInventory(Arrays.asList(
							iron.stackSize == 0 ? null : iron,
							gold.stackSize == 0 ? null : gold,
							coal.stackSize == 0 ? null : coal,
							diamond.stackSize == 0 ? null : diamond,
							cobble.stackSize == 0 ? null : cobble
							))
			);
	}
	
	private List<IProbeData> grassData() {
		return Arrays.asList(
				new ProbeData()
					.withLabel(new ItemStack(Blocks.grass).getDisplayName())
					.withInventory(Collections.singletonList(new ItemStack(Blocks.grass)))
			);
	}
	
	private List<IProbeData> chestData() {
		return Arrays.asList(
				new ProbeData()
					.withLabel(new ItemStack(Blocks.chest).getDisplayName())
					.withInventory(Collections.singletonList(new ItemStack(Blocks.chest))),
				new ProbeData()
					.withInventory(Arrays.asList(chestData))
			);
	}
	
	private List<IProbeData> furnaceData() {
		return Arrays.asList(
				new FruitProbeData()
					.withInventory(Arrays.asList(new ItemStack(Blocks.furnace)))
					.withLabel(new ItemStack(Blocks.furnace).getDisplayName()),
				new ProbeData()
					.withLabel(new ChatComponentTranslation("fruitphone.furnace.fuel"))
					.withBar(0, 1600-(ClientProxy.ticks%1600), 1600, UnitDictionary.TICKS),
				new ProbeData()
					.withLabel(new ChatComponentTranslation("fruitphone.furnace.progress"))
					.withBar(0, (ClientProxy.ticks%200)/2, 100, UnitDictionary.PERCENT),
				new ProbeData()
					.withInventory(Arrays.asList(
							furnaceCobble.stackSize == 0 ? null : furnaceCobble,
							furnaceCoal.stackSize == 0 ? null : furnaceCoal,
							furnaceStone.stackSize == 0 ? null : furnaceStone
							))
			);
	}
	
	private List<IProbeData> tankData() {
		return Arrays.asList(
				new ProbeData()
					.withLabel("Tank")
					.withInventory(Collections.singletonList(new ItemStack(Blocks.glass))),
				new ProbeData()
					.withBar(0, (ClientProxy.ticks%4000)/1000D, 4, UnitDictionary.getInstance().getUnit(FluidRegistry.LAVA)),
				new ProbeData()
					.withBar(0, ((ClientProxy.ticks+1283)%4000)/1000D, 4, UnitDictionary.getInstance().getUnit(FluidRegistry.WATER))
			);
	}
	
	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.id == 0) {
			FruitPhone.inst.glassesGravity = Gravity.NORTH_WEST;
			FruitPhone.inst.glassesScale = 1;
			FruitPhone.inst.glassesXOffset = 10;
			FruitPhone.inst.glassesYOffset = 10;
			FruitPhone.inst.maxGlassesHeight = 2/3f;
			FruitPhone.inst.maxGlassesWidth = 1/3f;
			initGui();
		} else if (button.id == 1) {
			mc.getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("fruitphone", "drill"), 0.875f+(rand.nextFloat()*0.25f)));
			mc.displayGuiScreen(null);
		} else if (button.id == 2) {
			probeDataSupplier = this::magicBoxData;
		} else if (button.id == 3) {
			probeDataSupplier = this::grassData;
		} else if (button.id == 4) {
			Arrays.fill(chestData, null);
			probeDataSupplier = this::chestData;
		} else if (button.id == 5) {
			probeDataSupplier = this::furnaceData;
		} else if (button.id == 6) {
			probeDataSupplier = this::tankData;
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		// this IS possible, I have a crash report to prove it
		if (mc == null) return;
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		String checkboxStr = I18n.format("fruitphone.gui.snapToGuides");
		
		int checkboxTextCol = -1;
		int v = 10;
		if (mouseX >= checkboxX && mouseX <= checkboxX+10+fontRendererObj.getStringWidth(checkboxStr)+2 && mouseY >= checkboxY && mouseY <= checkboxY+10) {
			v = 20;
			checkboxTextCol = 0xFFFFFFA0;
		}
		
		RenderHelper.enableGUIStandardItemLighting();
		drawButtonIcon((GuiButton)buttonList.get(2), new ItemStack(Blocks.iron_block));
		drawButtonIcon((GuiButton)buttonList.get(3), new ItemStack(Blocks.grass));
		drawButtonIcon((GuiButton)buttonList.get(4), new ItemStack(Blocks.chest));
		drawButtonIcon((GuiButton)buttonList.get(5), new ItemStack(Blocks.furnace));
		drawButtonIcon((GuiButton)buttonList.get(6), new ItemStack(Blocks.glass));
		RenderHelper.disableStandardItemLighting();
		
		GL11.glColor3f(1, 1, 1);
		Rendering.bindTexture(CHECKBOX);
		drawModalRectWithCustomSizedTexture(checkboxX, checkboxY, 0, v, 10, 10, 20, 30);
		if (snapToGuides) {
			drawModalRectWithCustomSizedTexture(checkboxX, checkboxY, 10, v, 10, 10, 20, 30);
		}
		int checkboxTextX = checkboxX+12;
		int checkboxTextW = fontRendererObj.getStringWidth(checkboxStr);
		if (checkboxTextX+checkboxTextW >= width) {
			checkboxTextX = checkboxX-checkboxTextW-2;
		}
		fontRendererObj.drawStringWithShadow(checkboxStr, checkboxTextX, checkboxY+1, checkboxTextCol);
		
		int color = -1;
		FruitEquipmentProperties props = (FruitEquipmentProperties)mc.thePlayer.getExtendedProperties("fruitphone:equipment");
		ItemStack glasses = props == null ? null : props.glasses;
		if (glasses != null && glasses.getItem() instanceof ItemFruitPassive) {
			ItemFruitPassive item = (ItemFruitPassive)glasses.getItem();
			color = item.getColor(glasses);
		}
		
		Gravity g = FruitPhone.inst.glassesGravity;
		int xOfs = FruitPhone.inst.glassesXOffset;
		int yOfs = FruitPhone.inst.glassesYOffset;
		float confScale = FruitPhone.inst.glassesScale;
		float maxWidthRaw = width * FruitPhone.inst.maxGlassesWidth;
		float maxHeightRaw = height * FruitPhone.inst.maxGlassesHeight;
		int maxWidth = (int)(maxWidthRaw/confScale);
		int maxHeight = (int)(maxHeightRaw/confScale);
		
		if (dragTarget != DragTarget.OVERLAY_POSITION) {
			int regionWidth = (int)Math.min(width, maxWidthRaw);
			int regionHeight = (int)Math.min(height, maxHeightRaw);
			
			int regionX = g.resolveX(xOfs, width, regionWidth);
			int regionY = g.resolveY(yOfs, height, regionHeight);
			
			Gui.drawRect(regionX, regionY, regionX+regionWidth, regionY+regionHeight, 0x88AAAAAA);
			
			clampHandleX = regionX+g.opposite().resolveX(0, regionWidth, 10);
			clampHandleY = regionY+g.opposite().resolveY(0, regionHeight, 10);
			
			String clampStr = I18n.format("fruitphone.gui.clampRegion");
			fontRendererObj.drawString(clampStr, regionX+g.opposite().resolveX(12, regionWidth, fontRendererObj.getStringWidth(clampStr)), regionY+g.opposite().resolveY(1, regionHeight, 8), -1);
		}
		if (snapToGuides) {
			if (dragTarget == DragTarget.CLAMP_REGION_SIZE) {
				for (float snap : CLAMP_REGION_SNAP_POINTS) {
					drawHorizontalLine(0, width, g.resolveY((int)(snap*height)+yOfs, height, 0), 0x55FFFFA0);
					drawVerticalLine(g.resolveX((int)(snap*width)+xOfs, width, 0), 0, height, 0x55FFFFA0);
				}
			} else if (dragTarget == DragTarget.OVERLAY_SIZE) {
				// TODO: overlay sizing is weird with where the mouse has to be, so there's no good place to put these
			} else if (dragTarget == DragTarget.OVERLAY_POSITION) {
				drawHorizontalLine(0, width, height/2, 0x55FFFFA0);
				drawVerticalLine(width/2, 0, height, 0x55FFFFA0);
				if (!g.isVerticalCenter()) {
					drawHorizontalLine(0, width, g.resolveY(10, height, 1), 0x55FFFFA0);
				}
				if (!g.isHorizontalCenter()) {
					drawVerticalLine(g.resolveX(10, width, 1), 0, height, 0x55FFFFA0);
				}
				if (g.isCorner()) {
					GL11.glPushMatrix(); {
						float ang;
						GL11.glTranslatef(g.resolveX(0, width, 0), g.resolveY(0, height, 0), 0);
						switch (g) {
							case NORTH_WEST:
								ang = 45;
								break;
							case NORTH_EAST:
								ang = 135;
								break;
							case SOUTH_EAST:
								ang = 225;
								break;
							case SOUTH_WEST:
								ang = 315;
								break;
							default:
								throw new AssertionError("Missing case for "+g);
						}
						GL11.glRotatef(ang, 0, 0, 1);
						drawHorizontalLine(0, (int)(Math.min(width, height)*SQRT2), 0, 0x55FFFFA0);
					} GL11.glPopMatrix();
				}
			}
		}
		
		List<IProbeData> probeData = probeDataSupplier.get();
		
		GL11.glPushMatrix(); {
			DataSize actual = FruitRenderer.calculatePreferredDataSize(probeData, 90, 50, maxWidth, maxHeight);
			DataSize clamped = new DataSize();
			clamped.setWidth(Math.min(maxWidth, actual.getWidth()));
			clamped.setHeight(Math.min(maxHeight, actual.getHeight()));
			if (clamped.getWidth() > 0 && clamped.getHeight() > 0) {
				float scale = FruitRenderer.getContainScale(clamped.getWidth(), clamped.getHeight(), actual.getWidth(), actual.getHeight());
				float xScale = 1;
				float yScale = 1;
				if (clamped.getWidth() < clamped.getHeight()) {
					xScale = scale;
				} else if (clamped.getHeight() < clamped.getWidth()) {
					yScale = scale;
				} else {
					xScale = yScale = scale;
				}
				xScale *= confScale;
				yScale *= confScale;
				
				objWidth = (int)(clamped.getWidth()*xScale)+10;
				objHeight = (int)(clamped.getHeight()*yScale)+10;
				
				x = g.resolveX(xOfs, width, objWidth);
				y = g.resolveY(yOfs, height, objHeight);
				
				overlayHandleX = x+g.opposite().resolveX(0, objWidth, 10);
				overlayHandleY = y+g.opposite().resolveY(0, objHeight, 10);
				
				GL11.glPushMatrix(); {
					GL11.glTranslatef(x, y, 0);

					Gui.drawRect(0, 0, objWidth, objHeight, color);
					Gui.drawRect(1, 1, objWidth-1, objHeight-1, 0xFF0C1935);
					GL11.glTranslatef(5f, 5f, 40f);
					GL11.glScalef(confScale, confScale, 1);
					FruitRenderer.render(probeData, clamped.getWidth(), clamped.getHeight(), true, actual);
				} GL11.glPopMatrix();
				
				int overlayHandleColor = 0x55FFFFFF;
				int clampHandleColor = 0x55FFFFFF;
				
				if (dragTarget == DragTarget.OVERLAY_SIZE) {
					overlayHandleColor = 0xAAFFFFA0;
				} else if (dragTarget == DragTarget.CLAMP_REGION_SIZE) {
					clampHandleColor = 0xAAFFFFA0;
				} else if (Mouse.isInsideWindow()) {
					if (mouseX >= x && mouseY >= y && mouseX <= x+objWidth && mouseY <= y+objHeight) {
						if (mouseX >= overlayHandleX && mouseY >= overlayHandleY && mouseX <= overlayHandleX+10 && mouseY <= overlayHandleY+10) {
							overlayHandleColor = 0xAAFFFFA0;
						}
					}
					if (mouseX >= clampHandleX && mouseY >= clampHandleY && mouseX <= clampHandleX+10 && mouseY <= clampHandleY+10) {
						clampHandleColor = 0xAAFFFFA0;
					}
				}
				
				GL11.glTranslatef(0, 0, 400f);
				Rendering.bindTexture(HANDLE);
				int handleU = (g.opposite().ordinal()%3)*10;
				int handleV = (g.opposite().ordinal()/3)*10;
				Rendering.color4(overlayHandleColor);
				drawModalRectWithCustomSizedTexture(overlayHandleX, overlayHandleY, handleU, handleV, 10, 10, 30, 30);
				if (dragTarget != DragTarget.OVERLAY_POSITION) {
					Rendering.color4(clampHandleColor);
					drawModalRectWithCustomSizedTexture(clampHandleX, clampHandleY, handleU, handleV, 10, 10, 30, 30);
				}
				List<String> tt = Lists.newArrayList();
				if (dragTarget == DragTarget.OVERLAY_SIZE) {
					// :V
					tt.add((dragSnapped?"\u00A7e":"")+Unit.FORMAT_STANDARD.format(FruitPhone.inst.glassesScale*100f)+"%");
				} else if (dragTarget == DragTarget.OVERLAY_POSITION) {
					tt.add((dragSnapped?"\u00A7e":"")+I18n.format("fruitphone.gravity."+(Keyboard.isKeyDown(Keyboard.KEY_GRAVE) ? "egg." : "")+dragGravity.toString()));
					tt.add((dragSnappedX?"\u00A7e":"")+FruitPhone.inst.glassesXOffset+"\u00A7r, "+(dragSnappedY?"\u00A7e":"")+FruitPhone.inst.glassesYOffset);
				} else if (dragTarget == DragTarget.CLAMP_REGION_SIZE) {
					tt.add((dragSnappedX?"\u00A7e":"")+Unit.FORMAT_STANDARD.format(FruitPhone.inst.maxGlassesWidth*100f)+"%\u00A7r x "+
							(dragSnappedY?"\u00A7e":"")+Unit.FORMAT_STANDARD.format(FruitPhone.inst.maxGlassesHeight*100f)+"%");
				}
				if (!tt.isEmpty()) {
					drawHoveringText(tt, mouseX, mouseY);
				}
			}
		} GL11.glPopMatrix();
		
	}
	
	private void drawButtonIcon(GuiButton button, ItemStack is) {
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		RenderHelper.enableGUIStandardItemLighting();
		RenderItem.getInstance().renderItemAndEffectIntoGUI(mc.fontRendererObj, mc.renderEngine, is, button.xPosition+2, button.yPosition+2);
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		FruitPhone.inst.saveConfig();
	}
	
	@Override
	public void updateScreen() {
		super.updateScreen();
		// this fictional machine which is Balanced Because It Takes Effort uses 300 Danks per tick and 1 million Danks per operation.
		energy -= 300;
		if (((int)ClientProxy.ticks)%200 == 0) {
			energy -= 1000000L;
			cobble.stackSize += rand.nextInt(100)+1;
			if (rand.nextInt(4) == 0) {
				coal.stackSize++;
			}
			if (rand.nextInt(8) == 0) {
				iron.stackSize++;
			}
			if (rand.nextInt(12) == 0) {
				gold.stackSize++;
			}
			if (rand.nextInt(24) == 0) {
				diamond.stackSize++;
			}
			
			furnaceCobble.stackSize--;
			furnaceStone.stackSize++;
		}
		if (((int)ClientProxy.ticks)%1600 == 0) {
			furnaceCoal.stackSize--;
		}
		int idx = rand.nextInt(54);
		ItemStack is = chestData[idx];
		if (is == null) {
			Iterator<Item> iter = Item.itemRegistry.iterator();
			Item item = iter.next();
			int seen = 1;
			while (iter.hasNext()) {
				seen++;
				int j = rand.nextInt(seen);
				if (j == 0) {
					item = iter.next();
				} else {
					iter.next();
				}
			}
			List<ItemStack> nnl = Lists.newArrayList();
			item.getSubItems(item, item.getCreativeTab(), nnl);
			if (!nnl.isEmpty()) {
				is = nnl.get(rand.nextInt(nnl.size()));
				if (item.isDamageable()) {
					is.setMetadata(rand.nextInt(is.getMaxDurability()));
				}
				chestData[idx] = is;
			}
		} else if (is.isStackable()) {
			is.stackSize++;
		}
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		String str = I18n.format("fruitphone.gui.snapToCenter");
		if (mouseButton == 0) {
			if (mouseX >= checkboxX && mouseX <= checkboxX+10+fontRendererObj.getStringWidth(str)+2 && mouseY >= checkboxY && mouseY <= checkboxY+10) {
				mc.getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1));
				snapToGuides = !snapToGuides;
			} else if (mouseX >= overlayHandleX && mouseY >= overlayHandleY && mouseX <= overlayHandleX+10 && mouseY <= overlayHandleY+10) {
				dragTarget = DragTarget.OVERLAY_SIZE;
				lastMouseX = mouseX;
				lastMouseY = mouseY;
			} else if (mouseX >= x && mouseY >= y && mouseX <= x+objWidth && mouseY <= y+objHeight) {
				dragTarget = DragTarget.OVERLAY_POSITION;
				dragGravity = FruitPhone.inst.glassesGravity;
				lastMouseX = mouseX;
				lastMouseY = mouseY;
				dragX = FruitPhone.inst.glassesXOffset;
				dragY = FruitPhone.inst.glassesYOffset;
			} else if (mouseX >= clampHandleX && mouseY >= clampHandleY && mouseX <= clampHandleX+10 && mouseY <= clampHandleY+10) {
				dragTarget = DragTarget.CLAMP_REGION_SIZE;
				lastMouseX = mouseX;
				lastMouseY = mouseY;
			}
		}
	}
	
	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
		if (clickedMouseButton == 0) {
			if (dragTarget == DragTarget.OVERLAY_SIZE) {
				int xOfs = FruitPhone.inst.glassesXOffset-5;
				int yOfs = FruitPhone.inst.glassesYOffset-5;
				
				Gravity g = FruitPhone.inst.glassesGravity;
				int resolvedX = g.resolveX(mouseX, 0, 0);
				int resolvedY = g.resolveY(mouseY, 0, 0);
				
				if (g.isHorizontalCenter()) {
					resolvedX = mouseX;
				}
				if (g.isVerticalCenter()) {
					resolvedY = mouseY;
				}
				
				if (resolvedX < 0) {
					resolvedX = width+resolvedX;
				}
				if (resolvedY < 0) {
					resolvedY = height+resolvedY;
				}
				
				List<IProbeData> probeData = probeDataSupplier.get();
				int maxWidth = (int)(width * FruitPhone.inst.maxGlassesWidth);
				int maxHeight = (int)(height * FruitPhone.inst.maxGlassesHeight);
				DataSize actual = FruitRenderer.calculatePreferredDataSize(probeData, 90, 50, maxWidth, maxHeight);
				
				int w = Math.min(maxWidth, actual.getWidth())+10;
				int h = Math.min(maxHeight, actual.getHeight())+10;
				
				float max = ((width*FruitPhone.inst.maxGlassesWidth))/w;
				float min = 1f/res.getScaleFactor();
				
				int normalHandleX = xOfs+w;
				int normalHandleY = yOfs+h;
				
				int dist = Math.max(resolvedX-normalHandleX, resolvedY-normalHandleY);
				
				float scale = 1+(dist/(float)w);
				dragSnapped = false;
				if (snapToGuides) {
					int round = (int)scale;
					float mult = res.getScaleFactor()*Math.max(1f, round);
					float tolerance = 0.025f;
					for (int i = 0; i <= mult; i++) {
						float snap = round+(i/mult);
						if (Math.abs(scale - snap) < tolerance) {
							scale = snap;
							dragSnapped = true;
							break;
						}
					}
				}
				if (scale < min) {
					scale = min;
					dragSnapped = true;
				} else if (scale > max) {
					scale = max;
					dragSnapped = true;
				}
				FruitPhone.inst.glassesScale = scale;
			} else if (dragTarget == DragTarget.CLAMP_REGION_SIZE) {
				int xOfs = FruitPhone.inst.glassesXOffset-5;
				int yOfs = FruitPhone.inst.glassesYOffset-5;
				
				Gravity g = FruitPhone.inst.glassesGravity;
				int resolvedX = g.resolveX(mouseX, 0, 0);
				int resolvedY = g.resolveY(mouseY, 0, 0);
				
				if (g.isHorizontalCenter()) {
					resolvedX = mouseX;
				}
				if (g.isVerticalCenter()) {
					resolvedY = mouseY;
				}
				
				if (resolvedX < 0) {
					resolvedX = width+resolvedX;
				}
				if (resolvedY < 0) {
					resolvedY = height+resolvedY;
				}
				
				float newWidth = (float)(resolvedX-xOfs)/width;
				float newHeight = (float)(resolvedY-yOfs)/height;
				
				dragSnappedX = false;
				dragSnappedY = false;
				if (snapToGuides) {
					float toleranceX = newWidth > 0.75f ? 0.07f : 0.025f;
					float toleranceY = newHeight > 0.75f ? 0.11f : 0.025f;
					for (float snap : CLAMP_REGION_SNAP_POINTS) {
						drawHorizontalLine(0, width, (int)(snap*height), 0x55FFFFA0);
						drawVerticalLine((int)(snap*width), 0, height, 0x55FFFFA0);
						if (Math.abs(newWidth - snap) < toleranceX) {
							newWidth = snap;
							dragSnappedX = true;
						}
						if (Math.abs(newHeight - snap) < toleranceY) {
							newHeight = snap;
							dragSnappedY = true;
						}
					}
				}
				
				FruitPhone.inst.maxGlassesWidth = newWidth;
				FruitPhone.inst.maxGlassesHeight = newHeight;
			} else if (dragTarget == DragTarget.OVERLAY_POSITION) {
				int movementX = mouseX-lastMouseX;
				int movementY = mouseY-lastMouseY;
				Gravity g = FruitPhone.inst.glassesGravity;
				
				int resolvedX = g.resolveX(movementX, 0, 0);
				int resolvedY = g.resolveY(movementY, 0, 0);
				
				if (g.isHorizontalCenter()) {
					resolvedX = movementX;
				}
				if (g.isVerticalCenter()) {
					resolvedY = movementY;
				}
				
				int newX = dragX += resolvedX;
				int newY = dragY += resolvedY;
				
				if (!g.isHorizontalCenter()) {
					if (newX < 0) {
						newX = 0;
						dragSnappedX = true;
					}
				}
				if (!g.isVerticalCenter()) {
					if (newY < 0) {
						newY = 0;
						dragSnappedY = true;
					}
				}
				
				dragSnapped = false;
				dragSnappedX = false;
				dragSnappedY = false;

				int tolerance = 2;
				if (snapToGuides) {
					tolerance = 5;
					if (Math.abs(newX-10) < tolerance) {
						newX = 10;
						dragSnappedX = true;
					}
					if (Math.abs(newY-10) < tolerance) {
						newY = 10;
						dragSnappedY = true;
					}
					
					if (g.isCorner()) {
						if (Math.abs(newX-newY) < tolerance) {
							newX = newY;
							dragSnappedX = true;
							dragSnappedY = true;
						}
					}
				}
				if (!g.isHorizontalCenter()) {
					if (Math.abs((newX+objWidth/2)-(width/2)) < tolerance) {
						dragGravity = g.getHorizontalCenter();
						dragSnapped = true;
						dragX = 0;
						newX = 0;
					}
				} else {
					if (Math.abs(newX) > tolerance) {
						dragGravity = newX > 0 ? g.eastmost() : g.westmost();
						newX = ((width/2)-(objWidth/2))-Math.abs(newX);
						dragX = newX;
					} else {
						dragSnapped = true;
					}
				}
				if (!g.isVerticalCenter()) {
					if (Math.abs((newY+objHeight/2)-(height/2)) < tolerance) {
						dragGravity = g.getVerticalCenter();
						dragSnapped = true;
						dragY = 0;
						newY = 0;
					}
				} else {
					if (Math.abs(newY) > tolerance) {
						dragGravity = newY > 0 ? g.southmost() : g.northmost();
						newY = ((height/2)-(objHeight/2))-Math.abs(newY);
						dragY = newY;
					} else {
						dragSnapped = true;
					}
				}
				
				if (newX > width/2) {
					dragGravity = g.flipHorizontal();
					newX -= objWidth;
					dragX -= objWidth;
				}
				if (newY > height/2) {
					dragGravity = g.flipVertical();
					newY -= objHeight;
					dragY -= objHeight;
				}
				
				
				
				lastMouseX = mouseX;
				lastMouseY = mouseY;
				
				if (FruitPhone.inst.glassesGravity != dragGravity) {
					FruitPhone.inst.glassesGravity = dragGravity;
					initGui();
				}
				
				FruitPhone.inst.glassesXOffset = newX;
				FruitPhone.inst.glassesYOffset = newY;
			}
		}
	}
	
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
		super.mouseReleased(mouseX, mouseY, mouseButton);
		if (mouseButton == 0) {
			dragTarget = null;
		}
	}
	
}
