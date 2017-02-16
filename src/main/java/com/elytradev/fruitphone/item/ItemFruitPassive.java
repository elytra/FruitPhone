package com.elytradev.fruitphone.item;

import java.util.List;

import com.elytradev.fruitphone.FruitPhone;
import com.elytradev.fruitphone.capability.FruitEquipmentCapability;
import com.elytradev.fruitphone.network.EquipmentDataPacket;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemFruitPassive extends ItemFruit {

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
		ItemStack itemStackIn = playerIn.getHeldItem(hand);
		if (playerIn.hasCapability(FruitPhone.inst.CAPABILITY_EQUIPMENT, null)) {
			FruitEquipmentCapability fec = playerIn.getCapability(FruitPhone.inst.CAPABILITY_EQUIPMENT, null);
			ItemStack oldGlasses = fec.glasses;
			fec.glasses = itemStackIn;
			playerIn.playSound(SoundEvents.ITEM_ARMOR_EQUIP_IRON, 1.0f, 2.0f);
			EquipmentDataPacket.forEntity(playerIn).ifPresent((m) -> m.sendToAllWatching(playerIn));
			return ActionResult.newResult(EnumActionResult.SUCCESS, oldGlasses);
		}
		return ActionResult.newResult(EnumActionResult.PASS, itemStackIn);
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		tooltip.add("\u00A77"+I18n.format("item.fruitphone.passive.hint"));
		super.addInformation(stack, playerIn, tooltip, advanced);
	}
	
}
