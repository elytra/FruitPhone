package io.github.elytra.fruitphone.item;

import io.github.elytra.fruitphone.FruitPhone;
import io.github.elytra.fruitphone.capability.FruitEquipmentCapability;
import io.github.elytra.fruitphone.network.EquipmentDataPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemFruitPassive extends ItemFruit {

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
		if (playerIn.hasCapability(FruitPhone.inst.CAPABILITY_EQUIPMENT, null)) {
			FruitEquipmentCapability fec = playerIn.getCapability(FruitPhone.inst.CAPABILITY_EQUIPMENT, null);
			ItemStack oldGlasses = fec.glasses;
			fec.glasses = itemStackIn;
			playerIn.playSound(SoundEvents.ITEM_ARMOR_EQUIP_IRON, 1.0f, 2.0f);
			EquipmentDataPacket.forEntity(playerIn).ifPresent((m) -> m.sendToAllWatching(playerIn));
			// TODO 1.11: use ItemStack.EMPTY
			if (oldGlasses == null) {
				oldGlasses = itemStackIn.copy();
				oldGlasses.stackSize = 0;
			}
			return ActionResult.newResult(EnumActionResult.SUCCESS, oldGlasses);
		}
		return ActionResult.newResult(EnumActionResult.PASS, itemStackIn);
	}
	
}
