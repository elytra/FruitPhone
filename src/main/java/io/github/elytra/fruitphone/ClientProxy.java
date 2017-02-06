package io.github.elytra.fruitphone;

import org.lwjgl.opengl.GL11;

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
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends Proxy {
	@Override
	public void preInit() {
		super.preInit();

		ModelLoader.setCustomModelResourceLocation(FruitItems.HANDHELD, 0, new ModelResourceLocation("fruitphone:handheld#inventory"));
		ModelLoader.setCustomModelResourceLocation(FruitItems.HANDHELD, 1, new ModelResourceLocation("fruitphone:handheld_mini#inventory"));
		ModelLoader.setCustomModelResourceLocation(FruitItems.PASSIVE, 0, new ModelResourceLocation("fruitphone:passive#inventory"));
	}

	@Override
	public void postInit() {
		super.postInit();

		Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, tintIndex) -> {
			if (stack.getItem() == FruitItems.PASSIVE && tintIndex == 0) return -1;
			return ((ItemFruit)stack.getItem()).getColor(stack);
		}, FruitItems.HANDHELD, FruitItems.PASSIVE);
	}

	@SubscribeEvent
	public void onRenderHand(RenderSpecificHandEvent e) {
		if (e.getItemStack() != null && e.getItemStack().getItem() == FruitItems.HANDHELD) {
			Minecraft mc = Minecraft.getMinecraft();
			
			AbstractClientPlayer p = mc.thePlayer;
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
			
			IBakedModel model = mc.getRenderItem().getItemModelWithOverrides(e.getItemStack(), mc.theWorld, p);
			
			GlStateManager.pushMatrix();
				float f = -0.4F * MathHelper.sin(MathHelper.sqrt_float(swing) * (float) Math.PI);
				float f1 = 0.2F * MathHelper.sin(MathHelper.sqrt_float(swing) * ((float) Math.PI * 2F));
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
				Gui.drawRect(30, 50, 130, 110, 0x44555555);
				GlStateManager.translate(0, 0, 40);
				
				
				
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
