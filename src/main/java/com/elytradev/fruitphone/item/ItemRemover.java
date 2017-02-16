package com.elytradev.fruitphone.item;

import java.util.List;

import com.elytradev.fruitphone.FruitPhone;
import com.elytradev.fruitphone.capability.FruitEquipmentCapability;
import com.elytradev.fruitphone.network.EquipmentDataPacket;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemRemover extends Item {

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
		ItemStack itemStackIn = playerIn.getHeldItem(hand);
		if (playerIn.hasCapability(FruitPhone.inst.CAPABILITY_EQUIPMENT, null)) {
			FruitEquipmentCapability fec = playerIn.getCapability(FruitPhone.inst.CAPABILITY_EQUIPMENT, null);
			ItemStack oldGlasses = fec.glasses;
			fec.glasses = ItemStack.EMPTY;
			if (!oldGlasses.isEmpty()) {
				playerIn.playSound(SoundEvents.ITEM_ARMOR_EQUIP_IRON, 1.0f, 0.5f);
				EquipmentDataPacket.forEntity(playerIn).ifPresent((m) -> m.sendToAllWatching(playerIn));
				if (!playerIn.inventory.addItemStackToInventory(oldGlasses)) {
					playerIn.dropItem(oldGlasses, false);
				}
				return ActionResult.newResult(EnumActionResult.SUCCESS, itemStackIn);
			} else {
				return ActionResult.newResult(EnumActionResult.FAIL, itemStackIn);
			}
		}
		return ActionResult.newResult(EnumActionResult.PASS, itemStackIn);
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		tooltip.add("\u00A77"+I18n.format("item.fruitphone.remover.hint"));
	}
	
}
