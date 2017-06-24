/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2017 William Thompson (unascribed)
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

package com.elytradev.fruitphone.recipe;

import java.util.List;

import com.elytradev.fruitphone.FruitPhone;
import com.elytradev.fruitphone.item.FruitItems;
import com.google.common.collect.Lists;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;

public class FruitRecipes {

	public static List<Integer> craftableColors = Lists.newArrayList();

	@SubscribeEvent
	public static void register(RegistryEvent.Register<IRecipe> registryEvent) {
		IForgeRegistry<IRecipe> registry = registryEvent.getRegistry();
		OreDictionary.registerOre("blockObsidian", Blocks.OBSIDIAN);
		OreDictionary.registerOre("clay", Items.CLAY_BALL);
		// using the (broken) listAllfruit name for Pam's HarvestCraft compat
		OreDictionary.registerOre("listAllfruit", Items.APPLE);
		OreDictionary.registerOre("listAllfruit", Items.MELON);
		OreDictionary.registerOre("listAllfruit", Items.CHORUS_FRUIT);

		registry.register(createOreRecipe(new ResourceLocation(FruitPhone.MODID, "create_handheld"),
				FruitItems.HANDHELD,
				"ingotIron", "listAllfruit"));
		registry.register(new FruitUpgradeRecipe(new ResourceLocation(FruitPhone.MODID, "create_passive"),
				FruitItems.PASSIVE,
				"/ /",
				"ghg",
				'/', "ingotIron",
				'g', "blockGlass",
				'h', new ItemStack(FruitItems.HANDHELD, 1, OreDictionary.WILDCARD_VALUE)));
		
		// Pad <-> Phone
		registry.register(new FruitUpgradeRecipe(new ResourceLocation(FruitPhone.MODID, "convert_pad_phone"),
				new ItemStack(FruitItems.HANDHELD, 1, 0),
				"p",
				'p', new ItemStack(FruitItems.HANDHELD, 1, 1)));

		registry.register(new FruitUpgradeRecipe(new ResourceLocation(FruitPhone.MODID, "convert_phone_pad"),
				new ItemStack(FruitItems.HANDHELD, 1, 1),
				"p",
				'p', new ItemStack(FruitItems.HANDHELD, 1, 0)));
		
		// Glasses <-> Contacts
		registry.register(new FruitUpgradeRecipe(new ResourceLocation(FruitPhone.MODID, "convert_glasses_contacts"),
				new ItemStack(FruitItems.PASSIVE, 1, 0),
				"p",
				'p', new ItemStack(FruitItems.PASSIVE, 1, 1)));
		
		registry.register(new FruitUpgradeRecipe(new ResourceLocation(FruitPhone.MODID, "convert_contacts_glasses"),
				new ItemStack(FruitItems.PASSIVE, 1, 1),
				"p",
				'p', new ItemStack(FruitItems.PASSIVE, 1, 0)));
		
		registry.register(new FruitUpgradeRecipe(new ResourceLocation(FruitPhone.MODID, "create_remover"),
				FruitItems.REMOVER,
				"i ",
				"/w",
				'/', "stickWood",
				'w', "plankWood",
				'i', "ingotIron"));
		
		// Elegant Tungsten
		colorRecipe(registry, 0x2C2D2D, "ingotTungsten");
		colorRecipe(registry, 0x2C2D2D, "dyeBlack", "ingotIron");
		// Obsidian
		colorRecipe(registry, 0x322D44, "blockObsidian");
		colorRecipe(registry, 0x322D44, "dyeBlack", "dyePurple");
		// Silver
		colorRecipe(registry, 0xCDCDCD, "ingotSilver");
		colorRecipe(registry, 0xCDCDCD, "dyeLightGray");
		// White
		colorRecipe(registry, 0xFFFFFF, "dyeWhite");
		
		// Slate Red
		colorRecipe(registry, 0xB15573, "dustRedstone");
		colorRecipe(registry, 0xB15573, "dyeRed", "dyeGray");
		// Dull Pink
		colorRecipe(registry, 0xAC5596, "dyeMagenta", "dyeGray");
		// Lucious Lavender
		colorRecipe(registry, 0x8851AC, "cropLavender");
		colorRecipe(registry, 0x8851AC, "dyePurple", "dyeGray");
		// Kinda Blue
		colorRecipe(registry, 0x5557B1, "dyeBlue", "dyeGray");
		// Steel Blue
		colorRecipe(registry, 0x5589B1, "ingotSteel");
		colorRecipe(registry, 0x5589B1, "dyeLightBlue", "dyeGray");
		// Sea Green
		colorRecipe(registry, 0x55A9B1, "dyeGreen", "dyeCyan", "dyeGray");
		// Real Teal
		colorRecipe(registry, 0x55AF90, "dyeCyan", "dyeGray");
		// Cactus Green
		colorRecipe(registry, 0x73B155, "dyeGreen", "dyeGray");
		// Wasabi
		colorRecipe(registry, 0x9CB055, "dyeLime", "dyeGreen", "dyeGray");
		// Sulfur
		colorRecipe(registry, 0xB8AA4D, "dustSulfur");
		colorRecipe(registry, 0xB8AA4D, "dustSulphur");
		colorRecipe(registry, 0xB8AA4D, "dyeYellow", "dyeGray");
		// Cinnamon
		colorRecipe(registry, 0xB18255, "dyeBrown", "dyeGray");
		// Wet Clay
		colorRecipe(registry, 0xB17355, "clay");
		colorRecipe(registry, 0xB17355, "dyeBrown", "dyeOrange", "dyeGray");
		// Scarlet
		colorRecipe(registry, 0xAF5454, "dyeRed", "dyeGray");
		
		// Cherry Red
		colorRecipe(registry, 0xFD004D, "dustGlowstone", "dyeRed");
		// Terrific Pink
		colorRecipe(registry, 0xE300FF, "dustGlowstone", "dyeMagenta");
		// Vivid Violet
		colorRecipe(registry, 0x9C00FF, "dustGlowstone", "dyePurple");
		// Really Blue
		colorRecipe(registry, 0x1B1BEC, "dustGlowstone", "dyeBlue");
		// Cerulean
		colorRecipe(registry, 0x0078FF, "dustGlowstone", "dyeLightBlue");
		// Sky Blue
		colorRecipe(registry, 0x00BAFF, "dustGlowstone", "dyeCyan");
		// Brilliant Verdant
		colorRecipe(registry, 0x00FFCC, "dustGlowstone", "dyeLime");
		Item misc = Item.getByNameOrId("correlated:misc");
		if (misc != null) {
			ItemStack lum = new ItemStack(misc, 1, 3);
			colorRecipe(registry, 0x00FFCC, lum);
		} else {
			colorRecipe(registry, 0x00FFCC, "dustGlowstone", "enderpearl");
		}
		// Mean Green
		colorRecipe(registry, 0x8BFF00, "dustGlowstone", "dyeGreen");
		// Electric Green
		colorRecipe(registry, 0xCCFE00, "dustGlowstone", "dyeLime", "dyeLime");
		// Mustard Yellow
		colorRecipe(registry, 0xF6E700, "dustGlowstone", "cropMustard");
		colorRecipe(registry, 0xF6E700, "dustGlowstone", "seedMustard");
		colorRecipe(registry, 0xF6E700, "dustGlowstone", "dyeYellow", "dyeBrown");
		// Dandelion Yellow
		colorRecipe(registry, 0xFEC501, "dustGlowstone", "dyeYellow");
		// Sunset Orange
		colorRecipe(registry, 0xFF9C00, "dustGlowstone", "dyeOrange");
		// Vibrant Vermillion
		colorRecipe(registry, 0xFF5C00, "dustGlowstone", "dyeOrange", "dyeRed");
		
		// Rose Gold
		if (doesOreExist("ingotCopper")) {
			if (doesOreExist("ingotSilver")) {
				colorRecipe(registry, 0xEFC6BF, "ingotGold", "ingotSilver", "ingotCopper");
				colorRecipe(registry, 0xEFC6BF, "dustGold", "dustSilver", "dustCopper");
			} else {
				colorRecipe(registry, 0xEFC6BF, "ingotGold", "ingotCopper");
				colorRecipe(registry, 0xEFC6BF, "dustGold", "dustCopper");
			}
		} else {
			colorRecipe(registry, 0xEFC6BF, "ingotGold", "dyeRed");
			colorRecipe(registry, 0xEFC6BF, "dustGold", "dyeRed");
		}
		// Platinum
		if (doesOreExist("ingotPlatinum")) {
			colorRecipe(registry, 0xC9D2D2, "ingotPlatinum");
			colorRecipe(registry, 0xC9D2D2, "dustPlatinum");
		} else if (doesOreExist("ingotSilver")) {
			colorRecipe(registry, 0xC9D2D2, "ingotSilver", "gemDiamond");
			colorRecipe(registry, 0xC9D2D2, "dustSilver", "gemDiamond");
		} else {
			colorRecipe(registry, 0xC9D2D2, "ingotIron", "gemDiamond", "gemDiamond");
			colorRecipe(registry, 0xC9D2D2, "dustIron", "gemDiamond", "gemDiamond");
		}
		// Sriracha Red
		if (doOresExist("cropTomato", "cropChilipepper", "cropGarlic", "foodVinegar")) {
			colorRecipe(registry, 0xB31E02, "cropTomato", "cropChilipepper", "cropGarlic", "foodVinegar");
		} else if (doOresExist("cropTomato", "cropGarlic")) {
			colorRecipe(registry, 0xB31E02, "cropTomato", "cropGarlic");
		} else {
			colorRecipe(registry, 0xB31E02, "dyeRed", "dyeRed", "dyeRed");
		}
		// Ender Green
		colorRecipe(registry, 0x258474, "enderpearl");
	}

	private static boolean doesOreExist(String ore) {
		return !OreDictionary.getOres(ore, false).isEmpty();
	}
	
	private static boolean doOresExist(String... ores) {
		for (String s : ores) {
			if (!doesOreExist(s)) return false;
		}
		return true;
	}

	private static void colorRecipe(IForgeRegistry<IRecipe> registry, int color, Object... ingredients) {
		craftableColors.add(color);
		
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("fruitphone:color", color);
		
		ItemStack handheld = new ItemStack(FruitItems.HANDHELD);
		ItemStack handheldMini = new ItemStack(FruitItems.HANDHELD, 1, 1);
		ItemStack passive = new ItemStack(FruitItems.PASSIVE);
		ItemStack passiveInvisible = new ItemStack(FruitItems.PASSIVE, 1, 1);
		
		handheld.setTagCompound(tag.copy());
		handheldMini.setTagCompound(tag.copy());
		passive.setTagCompound(tag.copy());
		passiveInvisible.setTagCompound(tag.copy());
		
		Object[] handheldIngredients = new Object[ingredients.length+1];
		Object[] handheldMiniIngredients = new Object[ingredients.length+1];
		Object[] passiveIngredients = new Object[ingredients.length+1];
		Object[] passiveInvisibleIngredients = new Object[ingredients.length+1];
		
		handheldIngredients[0] = FruitItems.HANDHELD;
		handheldMiniIngredients[0] = new ItemStack(FruitItems.HANDHELD, 1, 1);
		passiveIngredients[0] = FruitItems.PASSIVE;
		passiveInvisibleIngredients[0] = new ItemStack(FruitItems.PASSIVE, 1, 1);
		
		System.arraycopy(ingredients, 0, handheldIngredients, 1, ingredients.length);
		System.arraycopy(ingredients, 0, handheldMiniIngredients, 1, ingredients.length);
		System.arraycopy(ingredients, 0, passiveIngredients, 1, ingredients.length);
		System.arraycopy(ingredients, 0, passiveInvisibleIngredients, 1, ingredients.length);

		String suffix = String.valueOf(color);
		int variant = 0;
		while(registry.containsKey(new ResourceLocation(FruitPhone.MODID, "dye_handheld_" + suffix))){
			suffix = color + "_variant_" + variant;
			variant++;
		}

		registry.register(createOreRecipe(
				new ResourceLocation(FruitPhone.MODID, "dye_handheld_" + suffix),
				handheld, handheldIngredients));
		registry.register(createOreRecipe(
				new ResourceLocation(FruitPhone.MODID, "dye_handheld_mini_" + suffix),
				handheldMini, handheldMiniIngredients));
		registry.register(createOreRecipe(
				new ResourceLocation(FruitPhone.MODID, "dye_passive_" + suffix),
				passive, passiveIngredients));
		registry.register(createOreRecipe(
				new ResourceLocation(FruitPhone.MODID, "dye_passive_invisible_" + suffix),
				passiveInvisible, passiveInvisibleIngredients));
		
		Object[] handheldDirectIngredients = new Object[ingredients.length+2];
		
		handheldDirectIngredients[0] = "ingotIron";
		handheldDirectIngredients[1] = "listAllfruit";
		
		System.arraycopy(ingredients, 0, handheldDirectIngredients, 2, ingredients.length);
		
		registry.register(createOreRecipe(new ResourceLocation(FruitPhone.MODID, "dye_direct_handheld_" + suffix),
				handheld, handheldDirectIngredients));
	}

	private static ShapelessOreRecipe createOreRecipe(ResourceLocation location, Item out, Object... in){
		return (ShapelessOreRecipe) new ShapelessOreRecipe(location, out, in).setRegistryName(location);
	}

	private static ShapelessOreRecipe createOreRecipe(ResourceLocation location, ItemStack out, Object... in){
		return (ShapelessOreRecipe) new ShapelessOreRecipe(location, out, in).setRegistryName(location);
	}

}

