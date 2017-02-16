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

package com.elytradev.fruitphone.proxy;

import java.util.Map;
import org.lwjgl.opengl.GL11;

import com.elytradev.fruitphone.FruitPhone;
import com.elytradev.fruitphone.FruitRenderer;
import com.elytradev.fruitphone.client.render.LayerFruitGlass;
import com.elytradev.fruitphone.item.FruitItems;
import com.elytradev.fruitphone.item.ItemFruit;
import com.elytradev.fruitphone.item.ItemFruitPassive;
import com.google.common.base.Objects;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends Proxy {
	private boolean isServerVanilla = false;
	public boolean alwaysOn = false;
	
	public static float ticks;
	
	@Override
	public void preInit() {
		super.preInit();

		if (!FruitPhone.inst.optionalMode) {
			ModelLoader.setCustomModelResourceLocation(FruitItems.HANDHELD, 0, new ModelResourceLocation("fruitphone:handheld#inventory"));
			ModelLoader.setCustomModelResourceLocation(FruitItems.HANDHELD, 1, new ModelResourceLocation("fruitphone:handheld_mini#inventory"));
			ModelLoader.setCustomModelResourceLocation(FruitItems.PASSIVE, 0, new ModelResourceLocation("fruitphone:passive#inventory"));
			ModelLoader.setCustomModelResourceLocation(FruitItems.REMOVER, 0, new ModelResourceLocation("fruitphone:remover#inventory"));
		}
	}

	@Override
	public void postInit() {
		super.postInit();

		if (!FruitPhone.inst.optionalMode) {
			Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, tintIndex) -> {
				return ((ItemFruit)stack.getItem()).getColor(stack);
			}, FruitItems.HANDHELD, FruitItems.PASSIVE);
			RenderManager manager = Minecraft.getMinecraft().getRenderManager();
			Map<String, RenderPlayer> renders = manager.getSkinMap();
			for (RenderPlayer render : renders.values()) {
				render.addLayer(new LayerFruitGlass(render.getMainModel().bipedHead));
			}
		}
	}

	@SubscribeEvent
	public void onRenderTick(RenderTickEvent e) {
		if (e.phase == Phase.START) {
			ticks = ((int)ticks)+e.renderTickTime;
		}
	}
	
	@SubscribeEvent
	public void onClientTick(ClientTickEvent e) {
		if (e.phase == Phase.START) {
			ticks++;
		}
	}
	
	@SubscribeEvent
	public void onClientConnectedToServer(ClientConnectedToServerEvent e) {
		isServerVanilla = !e.getConnectionType().equals("MODDED");
	}
	
	@SubscribeEvent
	public void onRenderHud(RenderGameOverlayEvent.Post e) {
		if (e.getType() == ElementType.ALL) {
			// check if any of the following are true:
			// 1. optional mode is enabled
			// 2. the fruitphone:alwaysOn gamerule is set to true
			// 3. we are connected to a Forge server without Fruit Phone
			// 4. we are connected to a vanilla server
			// 5. the player has equipped glasses
			ItemStack glasses = null;
			if (
				FruitPhone.inst.optionalMode ||
				alwaysOn ||
				Item.REGISTRY.getNameForObject(FruitItems.PASSIVE) == null ||
				isServerVanilla ||
				(
					Minecraft.getMinecraft().player.hasCapability(FruitPhone.inst.CAPABILITY_EQUIPMENT, null) &&
					!(glasses = Minecraft.getMinecraft().player.getCapability(FruitPhone.inst.CAPABILITY_EQUIPMENT, null).glasses).isEmpty()
				)) {
				int color = -1;
				if (glasses != null) {
					if (glasses.getItem() instanceof ItemFruitPassive) {
						ItemFruitPassive item = (ItemFruitPassive)glasses.getItem();
						color = item.getColor(glasses);
					}
				}
				GlStateManager.pushMatrix(); {
					Gui.drawRect(10, 10, 110, 70, color);
					Gui.drawRect(11, 11, 109, 69, 0xFF000000);
					Gui.drawRect(11, 11, 109, 69, 0x88172E64);
					GlStateManager.translate(15f, 15f, 0f);
					FruitRenderer.renderAndSyncTarget(90, 50);
				} GlStateManager.popMatrix();
			}
		}
	}
	
	@SubscribeEvent
	public void onRenderHand(RenderSpecificHandEvent e) {
		if (FruitPhone.inst.optionalMode) return;
		if (e.getItemStack() != null && e.getItemStack().getItem() == FruitItems.HANDHELD) {
			Minecraft mc = Minecraft.getMinecraft();
			
			AbstractClientPlayer p = mc.player;
			EnumHand hand = e.getHand();
			ItemRenderer ir = mc.getItemRenderer();
			
			boolean isMain = (hand == EnumHand.MAIN_HAND);
			EnumHandSide handSide = isMain ? p.getPrimaryHand() : p.getPrimaryHand().opposite();
			
			float prevEquippedProgress;
			float equippedProgress;
			
			if (isMain) {
				prevEquippedProgress = ir.prevEquippedProgressMainHand;
				equippedProgress = ir.equippedProgressMainHand;
			} else {
				prevEquippedProgress = ir.prevEquippedProgressOffHand;
				equippedProgress = ir.equippedProgressOffHand;
			}
			
			float partialTicks = e.getPartialTicks();
			
			float swingProgress = p.getSwingProgress(partialTicks);
			EnumHand swingingHand = Objects.firstNonNull(p.swingingHand, EnumHand.MAIN_HAND);
			
			float interpPitch = p.prevRotationPitch + (p.rotationPitch - p.prevRotationPitch) * partialTicks;
			float interpYaw = p.prevRotationYaw + (p.rotationYaw - p.prevRotationYaw) * partialTicks;
			
			float swing = swingingHand == hand ? swingProgress : 0.0F;
			float equip = 1.0F - (prevEquippedProgress + (equippedProgress - prevEquippedProgress) * partialTicks);
			
			ir.rotateArroundXAndY(interpPitch, interpYaw);
			ir.setLightmap();
			ir.rotateArm(partialTicks);
			GlStateManager.enableRescaleNormal();

			ir.renderItemInFirstPerson(p, partialTicks, interpPitch, hand, swing, e.getItemStack(), equip);
			
			TransformType transform = (handSide == EnumHandSide.RIGHT ? TransformType.FIRST_PERSON_RIGHT_HAND : TransformType.FIRST_PERSON_LEFT_HAND);
			
			IBakedModel model = mc.getRenderItem().getItemModelWithOverrides(e.getItemStack(), mc.world, p);
			
			GlStateManager.pushMatrix();
				float f = -0.4F * MathHelper.sin(MathHelper.sqrt(swing) * (float) Math.PI);
				float f1 = 0.2F * MathHelper.sin(MathHelper.sqrt(swing) * ((float) Math.PI * 2F));
				float f2 = -0.2F * MathHelper.sin(swing * (float) Math.PI);
				int i = isMain ? 1 : -1;
				GlStateManager.translate(i * f, f1, f2);
				ir.transformSideFirstPerson(handSide, equip);
				ir.transformFirstPerson(handSide, swing);
				ForgeHooksClient.handleCameraTransforms(model, transform, handSide == EnumHandSide.LEFT);
				GlStateManager.disableCull();
				GlStateManager.translate(-0.5f, 0.5f, 0.03225f);
				// 10 GUI pixels = 1 item pixel
				// Z is extremely clamped since RenderItem translates to some ridiculous Z before rendering
				GlStateManager.scale(0.00625, -0.00625, 0.000001f);
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
				GL11.glDisable(GL11.GL_LIGHTING);
				GlStateManager.enableBlend();
				GlStateManager.disableAlpha();
				GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
				// now that all that is over, we can do what was super easy in 1.7... render stuff ON the item.
				// M U H  I M M E R S H U N
				GlStateManager.translate(30, 50, 40);
				Gui.drawRect(0, 0, 100, 60, 0x88172E64);
				
				GlStateManager.translate(5, 5, 40);
				FruitRenderer.renderAndSyncTarget(90, 50);
				
				GL11.glEnable(GL11.GL_LIGHTING);
				GlStateManager.enableLighting();
				GlStateManager.disableBlend();
				GlStateManager.enableAlpha();
				ir.setLightmap();
				GlStateManager.enableCull();
			GlStateManager.popMatrix();
			
			GlStateManager.disableRescaleNormal();
			RenderHelper.disableStandardItemLighting();
			
		}
	}
	
}
