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

package com.elytradev.fruitphone.client.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.elytradev.fruitphone.FruitEquipmentProperties;
import com.elytradev.fruitphone.item.ItemFruitPassive;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class LayerFruitGlass {

	private static final ResourceLocation PASSIVE = new ResourceLocation("fruitphone", "textures/model/passive.png");
	private static final ResourceLocation PASSIVE_OVERLAY = new ResourceLocation("fruitphone", "textures/model/passive_overlay.png");
	
	private ModelRenderer bipedHead;
	private ModelBase humanoidHead = new ModelSkeletonHead(0, 0, 64, 64);
	
	public LayerFruitGlass(ModelRenderer bipedHead) {
		this.bipedHead = bipedHead;
	}

	public void doRenderLayer(EntityPlayer player, float limbSwing,
			float limbSwingAmount, float partialTicks, float ageInTicks,
			float netHeadYaw, float headPitch, float scale) {
		FruitEquipmentProperties props = (FruitEquipmentProperties)player.getExtendedProperties("fruitphone:equipment");
		if (props == null) return;
		ItemStack itemstack = props.glasses;
		if (itemstack != null && itemstack.getItem() instanceof ItemFruitPassive) {
			ItemFruitPassive item = ((ItemFruitPassive)itemstack.getItem());
			GL11.glPushMatrix();; {
				GL11.glTranslatef(0f, 0.01f, 0f);
				if (player.isSneaking()) {
					GL11.glTranslatef(0f, 0.2f, 0f);
				}
	
				if (player.isChild()) {
					GL11.glTranslatef(0f, 0.5f * scale, 0f);
					GL11.glScalef(0.7f, 0.7f, 0.7f);
					GL11.glTranslatef(0f, 16f * scale, 0f);
				}
	
				bipedHead.postRender(0.0625f);
				
				int color = item.getColor(itemstack);
				float r = ((color >> 16) & 0xFF)/255f;
				float g = ((color >>  8) & 0xFF)/255f;
				float b = ((color      ) & 0xFF)/255f;
				
				GL11.glColor3f(r, g, b);
	
		        GL11.glScalef(-1.03125f, 1.03125f, -1.03125f);
		        
		        GL11.glDisable(GL11.GL_CULL_FACE);
		        GL11.glEnable(GL12.GL_RESCALE_NORMAL);;
		        GL11.glEnable(GL11.GL_ALPHA_TEST);

		        ItemStack helmet = player.getCurrentArmor(3);
		        if (helmet != null && helmet.getItem() instanceof ItemSkull) {
		        	if (helmet.getMetadata() == 4) {
		        		GL11.glScalef(1.2f, 1.2f, 1.2f);
		        		GL11.glTranslatef(0f, -0.11f, 0);
		        	} else if (helmet.getMetadata() == 5) {
		        		GL11.glScalef(1.75f, 1.75f, 1.75f);
		        		GL11.glTranslatef(0f, -0.125f, 0.075f);
		        	} else {
		        		GL11.glScalef(1.2f, 1.2f, 1.2f);
		        	}
		        }
		        Minecraft.getMinecraft().getTextureManager().bindTexture(PASSIVE);
		        render(limbSwing);
		        Minecraft.getMinecraft().getTextureManager().bindTexture(PASSIVE_OVERLAY);
		        GL11.glScalef(1.03125f, 1.03125f, 1.03125f);
		        GL11.glTranslatef(-0.015f, 0.025f, 0.015f);
		        render(limbSwing);
		        
			} GL11.glPopMatrix();
		}
	}

	private void render(float limbSwing) {
		GL11.glPushMatrix(); {
	        humanoidHead.render((Entity)null, limbSwing, 0f, 0f, 180f, 0f, 0.0625f);
		} GL11.glPopMatrix();
	}

}
