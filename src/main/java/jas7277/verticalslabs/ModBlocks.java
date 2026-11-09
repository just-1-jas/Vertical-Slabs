package jas7277.verticalslabs;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ModBlocks {
    private static final RegistrationData REGISTRATION_DATA = registerAll();

    public static final Map<Block, Block> VERTICAL_SLABS = REGISTRATION_DATA.verticalSlabs();
    public static final Map<Block, Block> FULL_BLOCKS = REGISTRATION_DATA.fullBlocks();
    public static final Block STONE = VERTICAL_SLABS.get(Blocks.STONE_SLAB);
    public static final ResourceKey<CreativeModeTab> VERTICAL_SLAB_TAB =
            ResourceKey.create(Registries.CREATIVE_MODE_TAB, VerticalSlabs.id("vertical_slabs"));

    public static void init() {
        Registry.register(
                BuiltInRegistries.CREATIVE_MODE_TAB,
                VERTICAL_SLAB_TAB,
                FabricCreativeModeTab.builder()
                        .title(Component.translatable("itemGroup.vertical-slabs.vertical_slabs"))
                        .icon(() -> new ItemStack(STONE))
                        .displayItems((parameters, output) ->
                                VERTICAL_SLABS.values().forEach(block -> output.accept(block.asItem())))
                        .build()
        );

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS).register(creativeTab -> {
            VERTICAL_SLABS.forEach((vanillaSlab, verticalSlab) -> {
                creativeTab.insertAfter(vanillaSlab.asItem(), verticalSlab.asItem());
            });
        });
    }

    private static RegistrationData registerAll() {
        List<Block> vanillaSlabs = new ArrayList<>();
        for (Block block : BuiltInRegistries.BLOCK) {
            Identifier id = BuiltInRegistries.BLOCK.getKey(block);
            if (block instanceof SlabBlock && id.getNamespace().equals("minecraft")) {
                vanillaSlabs.add(block);
            }
        }

        Map<Block, Block> verticalSlabs = new LinkedHashMap<>();
        Map<Block, Block> fullBlocks = new LinkedHashMap<>();
        for (Block slab : vanillaSlabs) {
            Identifier slabId = BuiltInRegistries.BLOCK.getKey(slab);
            String path = "vertical_" + slabId.getPath();
            Identifier verticalSlabId = VerticalSlabs.id(path);
            Block verticalSlab = register(
                    BlockItemId.create(verticalSlabId, verticalSlabId),
                    VerticalSlabBlock::new,
                    BlockBehaviour.Properties.ofFullCopy(slab)
            );

            verticalSlabs.put(slab, verticalSlab);
            fullBlocks.put(slab, findFullBlock(slabId));
        }

        return new RegistrationData(
                Collections.unmodifiableMap(verticalSlabs),
                Collections.unmodifiableMap(fullBlocks)
        );
    }

    private static Block findFullBlock(Identifier slabId) {
        String path = slabId.getPath();
        if (!path.endsWith("_slab")) {
            throw new IllegalStateException("Unexpected slab block id: " + slabId);
        }

        String base = path.substring(0, path.length() - "_slab".length());
        List<String> candidates = switch (path) {
            case "bamboo_slab" -> List.of("bamboo_planks");
            case "petrified_oak_slab" -> List.of("oak_planks");
            case "quartz_slab" -> List.of("quartz_block");
            case "purpur_slab" -> List.of("purpur_block");
            default -> List.of(base, base + "_planks", base + "s", base + "_block");
        };

        for (String candidate : candidates) {
            Identifier candidateId = Identifier.fromNamespaceAndPath("minecraft", candidate);
            Block block = BuiltInRegistries.BLOCK.getOptional(candidateId).orElse(null);
            if (block != null) {
                return block;
            }
        }

        throw new IllegalStateException("Could not find the full-block equivalent for " + slabId);
    }

    private static Block register(BlockItemId id, Function<BlockBehaviour.Properties, Block> blockFactory, BlockBehaviour.Properties properties) {
        Block block = register(id.block(), blockFactory, properties);

        BlockItem blockItem = new BlockItem(block, new Item.Properties().useBlockDescriptionPrefix().setId(id.item()));
        Registry.register(BuiltInRegistries.ITEM, id.item(), blockItem);

        return block;
    }

    private static Block register(ResourceKey<Block> id, Function<BlockBehaviour.Properties, Block> blockFactory, BlockBehaviour.Properties properties) {
        Block block = blockFactory.apply(properties.setId(id));
        return Registry.register(BuiltInRegistries.BLOCK, id, block);
    }

    private record RegistrationData(Map<Block, Block> verticalSlabs, Map<Block, Block> fullBlocks) {
    }
}
