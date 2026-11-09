package jas7277.verticalslabs.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import jas7277.verticalslabs.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class VerticalSlabsBlockTagProvider extends FabricTagsProvider.BlockTagsProvider {
    public VerticalSlabsBlockTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        copySlabTag(BlockTags.MINEABLE_WITH_AXE);
        copySlabTag(BlockTags.MINEABLE_WITH_HOE);
        copySlabTag(BlockTags.MINEABLE_WITH_PICKAXE);
        copySlabTag(BlockTags.MINEABLE_WITH_SHOVEL);
        copySlabTag(BlockTags.NEEDS_STONE_TOOL);
        copySlabTag(BlockTags.NEEDS_IRON_TOOL);
        copySlabTag(BlockTags.NEEDS_DIAMOND_TOOL);
    }

    private void copySlabTag(TagKey<Block> tag) {
        Set<Identifier> vanillaBlocks = new HashSet<>();
        collectVanillaTagValues(tag.location(), vanillaBlocks, new HashSet<>());
        List<ResourceKey<Block>> matchingSlabs = new ArrayList<>();
        ModBlocks.VERTICAL_SLABS.forEach((vanillaSlab, verticalSlab) -> {
            if (vanillaBlocks.contains(BuiltInRegistries.BLOCK.getKey(vanillaSlab))) {
                matchingSlabs.add(ResourceKey.create(Registries.BLOCK, BuiltInRegistries.BLOCK.getKey(verticalSlab)));
            }
        });

        if (!matchingSlabs.isEmpty()) {
            var builder = builder(tag);
            matchingSlabs.forEach(builder::add);
        }
    }

    private static void collectVanillaTagValues(Identifier tagId, Set<Identifier> blocks, Set<Identifier> visitedTags) {
        if (!visitedTags.add(tagId)) {
            return;
        }

        String resourcePath = "data/" + tagId.getNamespace() + "/tags/block/" + tagId.getPath() + ".json";
        // Vanilla tag lookups are not loaded during data generation, so read their source files.
        var vanillaContainer = FabricLoader.getInstance().getModContainer("minecraft")
                .orElseThrow(() -> new IllegalStateException("Minecraft mod container is unavailable"));
        var tagPath = vanillaContainer.findPath(resourcePath)
                .orElseThrow(() -> new IllegalStateException("Missing vanilla block tag resource: " + resourcePath));
        try (InputStream input = Files.newInputStream(tagPath)) {
            JsonElement root = JsonParser.parseReader(new InputStreamReader(input, StandardCharsets.UTF_8));
            for (JsonElement value : root.getAsJsonObject().getAsJsonArray("values")) {
                String entry = value.isJsonPrimitive()
                        ? value.getAsString()
                        : value.getAsJsonObject().get("id").getAsString();
                if (entry.startsWith("#")) {
                    collectVanillaTagValues(Identifier.parse(entry.substring(1)), blocks, visitedTags);
                } else {
                    blocks.add(Identifier.parse(entry));
                }
            }
        } catch (IOException exception) {
            throw new UncheckedIOException("Failed to read vanilla block tag " + tagId, exception);
        }
    }
}
