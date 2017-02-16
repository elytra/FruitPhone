package com.elytradev.fruitphone.recipe;

import com.elytradev.fruitphone.item.ItemFruit;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class FruitUpgradeRecipe extends ShapedOreRecipe {

	public FruitUpgradeRecipe(Block result, Object... recipe) {
		super(result, recipe);
	}

	public FruitUpgradeRecipe(Item result, Object... recipe) {
		super(result, recipe);
	}

	public FruitUpgradeRecipe(ItemStack result, Object... recipe) {
		super(result, recipe);
	}
	
	@Override
	public ItemStack getCraftingResult(InventoryCrafting var1) {
		ItemStack out = super.getCraftingResult(var1);
		for (int i = 0; i < var1.getSizeInventory(); i++) {
			ItemStack is = var1.getStackInSlot(i);
			if (is != null && is.getItem() instanceof ItemFruit) {
				out.setTagCompound(is.getTagCompound());
			}
		}
		return out;
	}
	
}
