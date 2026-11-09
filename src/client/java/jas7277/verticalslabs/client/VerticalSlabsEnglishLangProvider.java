package jas7277.verticalslabs.client;

import jas7277.verticalslabs.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VerticalSlabsEnglishLangProvider extends FabricLanguageProvider {
    public VerticalSlabsEnglishLangProvider(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, "en_us", registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider holderLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add("itemGroup.vertical-slabs.vertical_slabs", "Vertical Slabs");
        ModBlocks.VERTICAL_SLABS.forEach((vanillaSlab, verticalSlab) -> {
            String slabName = BuiltInRegistries.BLOCK.getKey(vanillaSlab).getPath();
            String materialName = slabName.substring(0, slabName.length() - "_slab".length());
            String title = Stream.of(materialName.split("_"))
                    .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                    .collect(Collectors.joining(" "));
            translationBuilder.add(verticalSlab, "Vertical " + title + " Slab");
        });
    }
}
