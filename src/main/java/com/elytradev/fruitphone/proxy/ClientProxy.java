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

package com.elytradev.fruitphone.proxy;

import org.lwjgl.opengl.GL11;

import com.elytradev.fruitphone.FruitEquipmentProperties;
import com.elytradev.fruitphone.FruitPhone;
import com.elytradev.fruitphone.FruitRenderer;
import com.elytradev.fruitphone.Gravity;
import com.elytradev.fruitphone.FruitRenderer.MultiDataSize;
import com.elytradev.fruitphone.client.gui.ScreenConfigureGlasses;
import com.elytradev.fruitphone.client.render.LayerFruitGlass;
import com.elytradev.fruitphone.item.FruitItems;
import com.elytradev.fruitphone.item.ItemFruitPassive;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends Proxy {
	private static boolean isServerVanilla = false;
	public boolean alwaysOn = false;
	
	public static float ticks;
	public static float partialTicks;
	public static float ticksConsiderPaused;
	
	private LayerFruitGlass layer;
	
	@Override
	public void preInit() {
		super.preInit();
	}

	@Override
	public void postInit() {
		super.postInit();
		RenderPlayer rp = (RenderPlayer)RenderManager.instance.entityRenderMap.get(EntityPlayer.class);
		layer = new LayerFruitGlass(rp.modelBipedMain.bipedHead);
	}
	
	@Override
	public void configureGlasses() {
		Minecraft.getMinecraft().displayGuiScreen(new ScreenConfigureGlasses());
	}

	@SubscribeEvent
	public void onRenderPlayer(RenderPlayerEvent.Specials.Post e) {
		layer.doRenderLayer(e.entityPlayer, 0, 0, partialTicks, e.entityPlayer.ticksExisted+e.partialRenderTick, e.entityPlayer.rotationYawHead, e.entityPlayer.rotationPitch, 0.0625f);
	}
	
	@SubscribeEvent
	public void onRenderTick(RenderTickEvent e) {
		if (e.phase == Phase.START) {
			partialTicks = e.renderTickTime;
			ticks = ((int)ticks)+e.renderTickTime;
			if (!Minecraft.getMinecraft().isGamePaused()) {
				ticksConsiderPaused = ((int)ticksConsiderPaused)+e.renderTickTime;
			}
		}
	}
	
	@SubscribeEvent
	public void onClientTick(ClientTickEvent e) {
		if (e.phase == Phase.START) {
			ticks++;
			if (!Minecraft.getMinecraft().isGamePaused()) {
				ticksConsiderPaused++;
			}
		}
	}
	
	@SubscribeEvent
	public void onClientConnectedToServer(ClientConnectedToServerEvent e) {
		isServerVanilla = !e.connectionType.equals("MODDED");
	}
	
	@SubscribeEvent
	public void onRenderHud(RenderGameOverlayEvent.Post e) {
		if (e.type == ElementType.ALL) {
			if (Minecraft.getMinecraft().currentScreen instanceof ScreenConfigureGlasses) {
				return;
			}
			// check if any of the following are true:
			// 1. optional mode is enabled
			// 2. the fruitphone:alwaysOn gamerule is set to true
			// 3. we are connected to a server without Fruit Phone
			// 4. the player has equipped glasses
			ItemStack glasses = null;
			if (
				FruitPhone.inst.optionalMode ||
				alwaysOn ||
				!doesServerHaveMod() ||
				(
					(glasses = ((FruitEquipmentProperties)Minecraft.getMinecraft().thePlayer.getExtendedProperties("fruitphone:equipment")).glasses) != null
				)) {
				int color = -1;
				if (glasses != null) {
					if (glasses.getItem() instanceof ItemFruitPassive) {
						ItemFruitPassive item = (ItemFruitPassive)glasses.getItem();
						color = item.getColor(glasses);
					}
				}
				
				Gravity g = FruitPhone.inst.glassesGravity;
				int xOfs = FruitPhone.inst.glassesXOffset;
				int yOfs = FruitPhone.inst.glassesYOffset;
				float confScale = FruitPhone.inst.glassesScale;
				int maxWidth = (int)((e.resolution.getScaledWidth() * FruitPhone.inst.maxGlassesWidth)/confScale);
				int maxHeight = (int)((e.resolution.getScaledHeight() * FruitPhone.inst.maxGlassesHeight)/confScale);
				
				GL11.glPushMatrix(); {
					MultiDataSize mds = FruitRenderer.calculateAndSyncTarget(90, 50, maxWidth, maxHeight);
					if (mds.clamped.getWidth() > 0 && mds.clamped.getHeight() > 0) {
						float scale = FruitRenderer.getContainScale(mds.clamped.getWidth(), mds.clamped.getHeight(), mds.actual.getWidth(), mds.actual.getHeight());
						float xScale = 1;
						float yScale = 1;
						if (mds.clamped.getWidth() < mds.clamped.getHeight()) {
							xScale = scale;
						} else if (mds.clamped.getHeight() < mds.clamped.getWidth()) {
							yScale = scale;
						} else {
							xScale = yScale = scale;
						}
						xScale *= confScale;
						yScale *= confScale;
						
						int objWidth = (int)(mds.clamped.getWidth()*xScale)+10;
						int objHeight = (int)(mds.clamped.getHeight()*yScale)+10;
						
						int x = g.resolveX(xOfs, e.resolution.getScaledWidth(), objWidth);
						int y = g.resolveY(yOfs, e.resolution.getScaledHeight(), objHeight);
						
						GL11.glPushMatrix(); {
							GL11.glTranslatef(x, y, 0);

							Gui.drawRect(0, 0, objWidth, objHeight, color);
							Gui.drawRect(1, 1, objWidth-1, objHeight-1, 0xFF0C1935);
							GL11.glTranslatef(5f, 5f, 40f);
							GL11.glScalef(confScale, confScale, 1);
							FruitRenderer.renderAndSyncTarget(mds.clamped.getWidth(), mds.clamped.getHeight(), true, mds.actual);
						} GL11.glPopMatrix();
					}
				} GL11.glPopMatrix();
			}
		}
	}
	
	public static boolean doesServerHaveMod() {
		return Item.itemRegistry.getNameForObject(FruitItems.PASSIVE) != null &&
				!isServerVanilla;
	}
	
}
