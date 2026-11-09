package jas7277.verticalslabs.client;

import jas7277.verticalslabs.ModBlocks;
import jas7277.verticalslabs.VerticalSlabBlock;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.advancements.predicates.StatePropertiesPredicate;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.MatchBlock;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

public class VerticalSlabsBlockLootTableProvider extends FabricBlockLootSubProvider {
    public VerticalSlabsBlockLootTableProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate() {
        for (Block block : ModBlocks.VERTICAL_SLABS.values()) {
            add(block, LootTable.lootTable().withPool(LootPool.lootPool().add(
                    applyExplosionDecay(
                            block,
                            LootItem.lootTableItem(block).apply(
                                    SetItemCountFunction.setCount(ContextIntProviders.exactly(2))
                                            .when(MatchBlock.blockMatches(
                                                    blocks,
                                                    block,
                                                    StatePropertiesPredicate.Builder.properties()
                                                            .hasProperty(VerticalSlabBlock.SINGLE, false)
                                            ))
                            )
                    )
            )));
        }
    }
}
