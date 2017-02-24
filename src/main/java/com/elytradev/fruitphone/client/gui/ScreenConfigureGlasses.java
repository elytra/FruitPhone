package com.elytradev.fruitphone.client.gui;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import com.elytradev.fruitphone.FruitPhone;
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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class ScreenConfigureGlasses extends GuiScreen {

	private enum DragTarget {
		OVERLAY_SIZE,
		OVERLAY_POSITION,
		CLAMP_REGION_SIZE
	}
	
	private static final ResourceLocation CHECKBOX = new ResourceLocation("fruitphone", "textures/gui/checkbox.png");
	private static final ResourceLocation HANDLE = new ResourceLocation("fruitphone", "textures/gui/resize_handle.png");
	
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
	
	private ItemStack iron = new ItemStack(Blocks.IRON_ORE, 0);
	private ItemStack gold = new ItemStack(Blocks.GOLD_ORE, 0);
	private ItemStack coal = new ItemStack(Items.COAL, 0);
	private ItemStack diamond = new ItemStack(Items.DIAMOND, 0);
	private ItemStack cobble = new ItemStack(Blocks.COBBLESTONE, 0);
	
	private long energy = Long.MAX_VALUE;
	
	private Random rand = new Random();
	
	@Override
	public void initGui() {
		super.initGui();
		addButton(new GuiButtonExt(0, width-110, height-30, 100, 20, "Restore Defaults"));
		checkboxX = width-110;
		checkboxY = height-45;
		res = new ScaledResolution(Minecraft.getMinecraft());
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 0) {
			FruitPhone.inst.glassesGravity = Gravity.NORTH_WEST;
			FruitPhone.inst.glassesScale = 1;
			FruitPhone.inst.glassesXOffset = 10;
			FruitPhone.inst.glassesYOffset = 10;
			FruitPhone.inst.maxGlassesHeight = 2/3f;
			FruitPhone.inst.maxGlassesWidth = 1/3f;
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
		if (mouseX >= checkboxX && mouseX <= checkboxX+10+fontRenderer.getStringWidth(checkboxStr)+2 && mouseY >= checkboxY && mouseY <= checkboxY+10) {
			v = 20;
			checkboxTextCol = 0xFFFFFFA0;
		}
		
		GlStateManager.color(1, 1, 1);
		Rendering.bindTexture(CHECKBOX);
		drawModalRectWithCustomSizedTexture(checkboxX, checkboxY, 0, v, 10, 10, 20, 30);
		if (snapToGuides) {
			drawModalRectWithCustomSizedTexture(checkboxX, checkboxY, 10, v, 10, 10, 20, 30);
		}
		fontRenderer.drawStringWithShadow(checkboxStr, checkboxX+12, checkboxY+1, checkboxTextCol);
		
		int color = -1;
		if (Minecraft.getMinecraft().player.hasCapability(FruitPhone.CAPABILITY_EQUIPMENT, null)) {
			ItemStack glasses = Minecraft.getMinecraft().player.getCapability(FruitPhone.CAPABILITY_EQUIPMENT, null).glasses;
			if (glasses.getItem() instanceof ItemFruitPassive) {
				ItemFruitPassive item = (ItemFruitPassive)glasses.getItem();
				color = item.getColor(glasses);
			}
		}
		List<IProbeData> li = Lists.newArrayList(
				new ProbeData()
					.withLabel("Magic Box")
					.withInventory(ImmutableList.of(new ItemStack(Blocks.IRON_BLOCK)))
					.withBar(0, (int)(ClientProxy.ticks%200)/2, 100, UnitDictionary.PERCENT),
				new ProbeData()
					.withBar(0, energy, Long.MAX_VALUE, UnitDictionary.DANKS),
				new ProbeData()
					.withInventory(ImmutableList.of(
							iron,
							gold,
							coal,
							diamond,
							cobble
							))
				);
		
		Gravity g = FruitPhone.inst.glassesGravity;
		int xOfs = FruitPhone.inst.glassesXOffset;
		int yOfs = FruitPhone.inst.glassesYOffset;
		float confScale = FruitPhone.inst.glassesScale;
		int maxWidth = (int)(width * FruitPhone.inst.maxGlassesWidth);
		int maxHeight = (int)(height * FruitPhone.inst.maxGlassesHeight);
		
		if (dragTarget != DragTarget.OVERLAY_POSITION) {
			int regionWidth = Math.min(width, maxWidth);
			int regionHeight = Math.min(height, maxHeight);
			
			int regionX = g.resolveX(xOfs, width, regionWidth);
			int regionY = g.resolveY(yOfs, height, regionHeight);
			
			Gui.drawRect(regionX, regionY, regionX+regionWidth, regionY+regionHeight, 0x88AAAAAA);
			
			clampHandleX = regionX+g.opposite().resolveX(0, regionWidth, 10);
			clampHandleY = regionY+g.opposite().resolveY(0, regionHeight, 10);
			
			String clampStr = I18n.format("fruitphone.gui.clampRegion");
			fontRenderer.drawString(clampStr, regionX+g.opposite().resolveX(12, regionWidth, fontRenderer.getStringWidth(clampStr)), regionY+g.opposite().resolveY(1, regionHeight, 8), -1);
		}
		if (snapToGuides) {
			if (dragTarget == DragTarget.CLAMP_REGION_SIZE) {
				for (float snap : CLAMP_REGION_SNAP_POINTS) {
					drawHorizontalLine(0, width, (int)(snap*height)+yOfs, 0x55FFFFA0);
					drawVerticalLine((int)(snap*width)+xOfs, 0, height, 0x55FFFFA0);
				}
			} else if (dragTarget == DragTarget.OVERLAY_SIZE) {
				// TODO: overlay sizing is weird with where the mouse has to be, so there's no good place to put these
			} else if (dragTarget == DragTarget.OVERLAY_POSITION) {
				drawHorizontalLine(0, width, height/2, 0x55FFFFA0);
				drawVerticalLine(width/2, 0, height, 0x55FFFFA0);
				drawHorizontalLine(0, width, 10, 0x55FFFFA0);
				drawVerticalLine(10, 0, height, 0x55FFFFA0);
				GlStateManager.pushMatrix(); {
					GlStateManager.rotate(45, 0, 0, 1);
					drawHorizontalLine(0, width, 0, 0x55FFFFA0);
				} GlStateManager.popMatrix();
			}
		}
		
		GlStateManager.pushMatrix(); {
			DataSize actual = FruitRenderer.calculatePreferredDataSize(li, 90, 50, maxWidth, maxHeight);
			DataSize clamped = new DataSize();
			clamped.setWidth(Math.min((int)(maxWidth/confScale), actual.getWidth()));
			clamped.setHeight(Math.min((int)(maxHeight/confScale), actual.getHeight()));
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
				
				GlStateManager.pushMatrix(); {
					GlStateManager.translate(x, y, 0);

					Gui.drawRect(0, 0, objWidth, objHeight, color);
					Gui.drawRect(1, 1, objWidth-1, objHeight-1, 0xFF0C1935);
					GlStateManager.translate(5f, 5f, 40f);
					GlStateManager.scale(confScale, confScale, 1);
					FruitRenderer.render(li, clamped.getWidth(), clamped.getHeight(), true, actual);
				} GlStateManager.popMatrix();
				
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
				
				GlStateManager.translate(0, 0, 400f);
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
					tt.add(I18n.format("fruitphone.gravity."+(Keyboard.isKeyDown(Keyboard.KEY_GRAVE) ? "egg." : "")+dragGravity.toString()));
					tt.add((dragSnappedX?"\u00A7e":"")+FruitPhone.inst.glassesXOffset+"\u00A7r, "+(dragSnappedY?"\u00A7e":"")+FruitPhone.inst.glassesYOffset);
				} else if (dragTarget == DragTarget.CLAMP_REGION_SIZE) {
					tt.add((dragSnappedX?"\u00A7e":"")+Unit.FORMAT_STANDARD.format(FruitPhone.inst.maxGlassesWidth*100f)+"%\u00A7r x "+
							(dragSnappedY?"\u00A7e":"")+Unit.FORMAT_STANDARD.format(FruitPhone.inst.maxGlassesHeight*100f)+"%");
				}
				if (!tt.isEmpty()) {
					drawHoveringText(tt, mouseX, mouseY);
				}
			}
		} GlStateManager.popMatrix();
		
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
			cobble.setCount(cobble.getCount()+(rand.nextInt(100)+1));
			if (rand.nextInt(4) == 0) {
				coal.setCount(coal.getCount()+1);
			}
			if (rand.nextInt(8) == 0) {
				iron.setCount(iron.getCount()+1);
			}
			if (rand.nextInt(12) == 0) {
				gold.setCount(gold.getCount()+1);
			}
			if (rand.nextInt(24) == 0) {
				diamond.setCount(diamond.getCount()+1);
			}
		}
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		String str = I18n.format("fruitphone.gui.snapToCenter");
		if (mouseButton == 0) {
			if (mouseX >= checkboxX && mouseX <= checkboxX+10+fontRenderer.getStringWidth(str)+2 && mouseY >= checkboxY && mouseY <= checkboxY+10) {
				mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1));
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
				int xDist = mouseX-lastMouseX;
				int yDist = mouseY-lastMouseY;
				Gravity g = FruitPhone.inst.glassesGravity;
				xDist = g.resolveX(xDist, 0, 0);
				yDist = g.resolveY(yDist, 0, 0);
				int dist;
				if (Math.abs(xDist) > Math.abs(yDist)) {
					dist = xDist;
				} else {
					dist = yDist;
				}
				float max = ((width*FruitPhone.inst.maxGlassesWidth))/(90+(10/FruitPhone.inst.glassesScale));
				float min = 1f/res.getScaleFactor();
				float scale = FruitPhone.inst.glassesScale+(dist/(float)objWidth);
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
				if (!dragSnapped) {
					lastMouseX = mouseX;
					lastMouseY = mouseY;
				}
				FruitPhone.inst.glassesScale = scale;
			} else if (dragTarget == DragTarget.CLAMP_REGION_SIZE) {
				int xOfs = FruitPhone.inst.glassesXOffset-5;
				int yOfs = FruitPhone.inst.glassesYOffset-5;
				
				float newWidth = (float)(mouseX-xOfs)/width;
				float newHeight = (float)(mouseY-yOfs)/height;
				
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
				movementX = g.resolveX(movementX, 0, 0);
				movementY = g.resolveY(movementY, 0, 0);
				
				int newX = dragX += movementX;
				int newY = dragY += movementY;
				
				if (newX < 0) {
					newX = 0;
					dragSnappedX = true;
				}
				if (newY < 0) {
					newY = 0;
					dragSnappedY = true;
				}
				
				dragSnappedX = false;
				dragSnappedY = false;
				
				if (snapToGuides) {
					int tolerance = 5;
					if (Math.abs(newX-10) < tolerance) {
						newX = 10;
						dragSnappedX = true;
					}
					if (Math.abs(newY-10) < tolerance) {
						newY = 10;
						dragSnappedY = true;
					}
					if (Math.abs(newX-newY) < tolerance) {
						newX = newY;
					}
				}
				
				if (newX > width/2) {
					dragGravity = g.flipHorizontal();
				}
				if (newY > height/2) {
					dragGravity = g.flipVertical();
				}
				
				lastMouseX = mouseX;
				lastMouseY = mouseY;
				
				FruitPhone.inst.glassesGravity = dragGravity;
				
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
