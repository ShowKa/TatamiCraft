package com.showka.mixin;

import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Sorts TatamiCraft recipes in the recipe book by recipe ID.
 * Only affects recipes with the "tatamicraft" namespace; other mods and vanilla are untouched.
 */
@Mixin(ClientRecipeBook.class)
public class ClientRecipeBookMixin {

    @Shadow
    private Map<?, List<RecipeCollection>> collectionsByTab;

    @Shadow
    private List<RecipeCollection> allCollections;

    @Inject(method = "setupCollections", at = @At("TAIL"))
    private void tatamicraft$sortRecipeCollections(Iterable<Recipe<?>> recipes, RegistryAccess registryAccess, CallbackInfo ci) {
        Comparator<Recipe<?>> recipeComparator = Comparator.comparing(
                recipe -> recipe.getId().toString()
        );

        for (List<RecipeCollection> collections : this.collectionsByTab.values()) {
            for (RecipeCollection collection : collections) {
                sortTatamicraftRecipes(collection, recipeComparator);
            }
        }
        for (RecipeCollection collection : this.allCollections) {
            sortTatamicraftRecipes(collection, recipeComparator);
        }
    }

    private static void sortTatamicraftRecipes(RecipeCollection collection, Comparator<Recipe<?>> comparator) {
        List<Recipe<?>> allRecipes = collection.getRecipes();
        boolean hasTatamicraft = allRecipes.stream()
                .anyMatch(r -> r.getId().getNamespace().equals("tatamicraft"));
        if (hasTatamicraft) {
            allRecipes.sort(comparator);
        }
    }
}
