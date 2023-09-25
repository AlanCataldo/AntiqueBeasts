package net.mebahel.antiquebeasts.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class BloodInfusingRecipe implements Recipe<SimpleInventory> {
    private final Identifier id;
    private final ItemStack output;
    private final DefaultedList<Ingredient> recipeItems;

    public BloodInfusingRecipe(Identifier id, ItemStack output, DefaultedList<Ingredient> recipeItems) {
        this.id = id;
        this.output = output;
        this.recipeItems = recipeItems;
    }

    @Override
    public boolean matches(SimpleInventory inventory, World world) {
        if (world.isClient()) {
            return false;
        }

        int ingredientCount = recipeItems.size();
        if (ingredientCount == 1) {
            return recipeItems.get(0).test(inventory.getStack(0));
        }

        if (ingredientCount > inventory.size()) {
            return false;
        }

        DefaultedList<ItemStack> remainingItems = DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY);
        for (int i = 0; i < inventory.size(); i++) {
            remainingItems.set(i, inventory.getStack(i).copy());
        }

        for (Ingredient ingredient : recipeItems) {
            boolean ingredientFound = false;
            for (int i = 0; i < remainingItems.size(); i++) {
                ItemStack stack = remainingItems.get(i);
                if (ingredient.test(stack)) {
                    ingredientFound = true;
                    stack.decrement(1);
                    remainingItems.set(i, stack);
                    break;
                }
            }
            if (!ingredientFound) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack craft(SimpleInventory inventory) {
        return output;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public ItemStack getOutput() {
        return output.copy();
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<BloodInfusingRecipe> {
        private Type() { }
        public static final Type INSTANCE = new Type();
        public static final String ID = "gem_infusing";
    }

    public static class Serializer implements RecipeSerializer<BloodInfusingRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final String ID = "blood_infusing";
        // this is the name given in the json file

        @Override
        public BloodInfusingRecipe read(Identifier id, JsonObject json) {
            ItemStack output = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "output"));

            JsonArray ingredients = JsonHelper.getArray(json, "ingredients");
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(ingredients.size(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }

            return new BloodInfusingRecipe(id, output, inputs);
        }


        @Override
        public BloodInfusingRecipe read(Identifier id, PacketByteBuf buf) {
            ItemStack output = buf.readItemStack();

            int ingredientCount = buf.readInt();
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(ingredientCount, Ingredient.EMPTY);

            for (int i = 0; i < ingredientCount; i++) {
                inputs.set(i, Ingredient.fromPacket(buf));
            }

            return new BloodInfusingRecipe(id, output, inputs);
        }

        @Override
        public void write(PacketByteBuf buf, BloodInfusingRecipe recipe) {
            for (Ingredient ingredient : recipe.getIngredients()) {
                ingredient.write(buf);
            }

            buf.writeItemStack(recipe.getOutput());
        }
    }
}
