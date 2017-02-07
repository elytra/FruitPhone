package io.github.elytra.fruitphone.client.render;

import io.github.elytra.fruitphone.FruitPhone;
import io.github.elytra.fruitphone.item.ItemFruitPassive;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelHumanoidHead;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class LayerFruitGlass implements LayerRenderer<EntityPlayer> {

	private static final ResourceLocation PASSIVE = new ResourceLocation("fruitphone", "textures/model/passive.png");
	private static final ResourceLocation PASSIVE_OVERLAY = new ResourceLocation("fruitphone", "textures/model/passive_overlay.png");
	
	private ModelRenderer bipedHead;
	private ModelBase humanoidHead = new ModelHumanoidHead();
	
	public LayerFruitGlass(ModelRenderer bipedHead) {
		this.bipedHead = bipedHead;
	}

	@Override
	public void doRenderLayer(EntityPlayer player, float limbSwing,
			float limbSwingAmount, float partialTicks, float ageInTicks,
			float netHeadYaw, float headPitch, float scale) {
		if (player.hasCapability(FruitPhone.inst.CAPABILITY_EQUIPMENT, null)) {
			ItemStack itemstack = player.getCapability(FruitPhone.inst.CAPABILITY_EQUIPMENT, null).glasses;
	
			if (itemstack != null && itemstack.getItem() != null && itemstack.getItem() instanceof ItemFruitPassive) {
				ItemFruitPassive item = ((ItemFruitPassive)itemstack.getItem());
				GlStateManager.pushMatrix(); {
					if (player.isSneaking()) {
						GlStateManager.translate(0f, 0.2f, 0f);
					}
		
					if (player.isChild()) {
						GlStateManager.translate(0f, 0.5f * scale, 0f);
						GlStateManager.scale(0.7f, 0.7f, 0.7f);
						GlStateManager.translate(0f, 16f * scale, 0f);
					}
		
					bipedHead.postRender(0.0625f);
					
					int color = item.getColor(itemstack);
					float r = ((color >> 16) & 0xFF)/255f;
					float g = ((color >>  8) & 0xFF)/255f;
					float b = ((color      ) & 0xFF)/255f;
					
					GlStateManager.color(r, g, b);
		
			        GlStateManager.scale(-1.0625f, 1.0625f, -1.0625f);
			        
			        GlStateManager.disableCull();
			        GlStateManager.enableRescaleNormal();
			        GlStateManager.enableAlpha();
			        
			        Minecraft.getMinecraft().getTextureManager().bindTexture(PASSIVE);
			        render(limbSwing);
			        Minecraft.getMinecraft().getTextureManager().bindTexture(PASSIVE_OVERLAY);
			        GlStateManager.scale(1.0625f, 1.0625f, 1.0625f);
			        GlStateManager.translate(-0.03f, 0.035f, 0);
			        render(limbSwing);
			        
				} GlStateManager.popMatrix();
			}
		}
	}

	private void render(float limbSwing) {
		GlStateManager.pushMatrix(); {
	        humanoidHead.render((Entity)null, limbSwing, 0f, 0f, 180f, 0f, 0.0625f);
        } GlStateManager.popMatrix();
	}

	@Override
	public boolean shouldCombineTextures() {
		// TODO Auto-generated method stub
		return false;
	}

}
