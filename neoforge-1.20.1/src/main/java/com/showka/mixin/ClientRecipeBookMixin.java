package com.showka.mixin;

import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Sorts TatamiCraft recipe collections in the recipe book by recipe ID.
 * Only affects RecipeCollections containing "tatamicraft" namespace recipes;
 * other mods and vanilla collections remain in their original positions.
 *
 * The fields collectionsByTab and allCollections are ImmutableMap/ImmutableList
 * after setupCollections(), so we replace them with new sorted mutable collections.
 */
@Mixin(ClientRecipeBook.class)
public class ClientRecipeBookMixin {

    @Shadow
    private Map<RecipeBookCategories, List<RecipeCollection>> collectionsByTab;

    @Shadow
    private List<RecipeCollection> allCollections;

    @Inject(method = "setupCollections", at = @At("TAIL"))
    private void tatamicraft$sortRecipeCollections(Iterable<Recipe<?>> recipes, RegistryAccess registryAccess, CallbackInfo ci) {
        // Replace collectionsByTab with a new mutable map containing sorted lists
        Map<RecipeBookCategories, List<RecipeCollection>> sortedByTab = new HashMap<>();
        for (Map.Entry<RecipeBookCategories, List<RecipeCollection>> entry : this.collectionsByTab.entrySet()) {
            sortedByTab.put(entry.getKey(), sortCollectionList(entry.getValue()));
        }
        this.collectionsByTab = sortedByTab;

        // Replace allCollections with a new sorted list
        this.allCollections = sortCollectionList(this.allCollections);
    }

    /**
     * Sorts tatamicraft RecipeCollections by their first recipe's ResourceLocation,
     * while keeping non-tatamicraft collections in their original positions.
     */
    private static List<RecipeCollection> sortCollectionList(List<RecipeCollection> original) {
        List<Integer> tatamicraftIndices = new ArrayList<>();
        List<RecipeCollection> tatamicraftCollections = new ArrayList<>();

        for (int i = 0; i < original.size(); i++) {
            RecipeCollection collection = original.get(i);
            if (isTatamicraftCollection(collection)) {
                tatamicraftIndices.add(i);
                tatamicraftCollections.add(collection);
            }
        }

        if (tatamicraftCollections.isEmpty()) {
            return original;
        }

        // Sort only the tatamicraft collections by their first recipe's ID
        tatamicraftCollections.sort(Comparator.comparing(ClientRecipeBookMixin::getCollectionSortKey));

        // Build the result: non-tatamicraft collections stay in place,
        // tatamicraft collections are placed back in their original slots but in sorted order
        List<RecipeCollection> result = new ArrayList<>(original);
        for (int i = 0; i < tatamicraftIndices.size(); i++) {
            result.set(tatamicraftIndices.get(i), tatamicraftCollections.get(i));
        }
        return result;
    }

    private static boolean isTatamicraftCollection(RecipeCollection collection) {
        List<Recipe<?>> recipes = collection.getRecipes();
        return !recipes.isEmpty() && recipes.get(0).getId().getNamespace().equals("tatamicraft");
    }

    private static String getCollectionSortKey(RecipeCollection collection) {
        List<Recipe<?>> recipes = collection.getRecipes();
        if (recipes.isEmpty()) {
            return "";
        }
        return recipes.get(0).getId().toString();
    }
}
