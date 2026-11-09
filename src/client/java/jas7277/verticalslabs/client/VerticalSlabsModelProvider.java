package jas7277.verticalslabs.client;

import java.util.Optional;

import jas7277.verticalslabs.ModBlocks;
import jas7277.verticalslabs.VerticalSlabBlock;
import jas7277.verticalslabs.VerticalSlabs;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import org.jspecify.annotations.NonNull;

// #region provider
public class VerticalSlabsModelProvider extends FabricModelProvider {
    public VerticalSlabsModelProvider(FabricPackOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        // #endregion provider

        ModBlocks.VERTICAL_SLABS.forEach((vanillaSlab, verticalSlab) -> {
            Block fullBlock = ModBlocks.FULL_BLOCKS.get(vanillaSlab);
            Block modelBlock = CustomBlockStateModelGenerator.modelBlockFor(fullBlock);
            CustomBlockStateModelGenerator.registerVerticalSlab(
                    blockStateModelGenerator,
                    verticalSlab,
                    modelBlock,
                    CustomBlockStateModelGenerator.texturesFor(modelBlock)
            );
        });

        // #region provider
    }

    @Override
    public void generateItemModels(@NonNull ItemModelGenerators itemModelGenerators) {

    }
    // #endregion provider

    // Inner class containing custom objects for item model generation.
    // #region custom_item_model_generator
    public static class CustomItemModelGenerator {
        // #region custom_item_model
        public static final ModelTemplate SCALED2X = item("scaled2x", TextureSlot.LAYER0);
        // #endregion custom_item_model

        // #region custom_item_datagen_method
        public static void registerScaled2x(Item item, ItemModelGenerators generator) {
            Identifier itemModel = SCALED2X.create(item, TextureMapping.singleSlot(TextureSlot.LAYER0, new Material(ModelLocationUtils.getModelLocation(item))), generator.modelOutput);
            generator.itemModelOutput.accept(item, ItemModelUtils.plainModel(itemModel));
        }

        // #endregion custom_item_datagen_method

        @SuppressWarnings("SameParameterValue")
        // #region custom_item_model

        private static ModelTemplate item(String parent, TextureSlot requiredTextureKeys) {
            return new ModelTemplate(Optional.of(VerticalSlabs.id("item/" + parent)), Optional.empty(), requiredTextureKeys);
        }

        // #endregion custom_item_model
    }
    // #endregion custom_item_model_generator

    // Inner class containing all Objects needed for the custom datagen tutorial.
    // #region custom_blockstate_model_generator
    public static class CustomBlockStateModelGenerator {
        // #region custom_model
        public static final ModelTemplate VERTICAL_SLAB = block("vertical_slab", TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE);

        // helper method for creating Models
        private static ModelTemplate block(String parent, TextureSlot... requiredTextureKeys) {
            return new ModelTemplate(Optional.of(VerticalSlabs.id("block/" + parent)), Optional.empty(), requiredTextureKeys);
        }

        // helper method for creating Models with variants
        private static ModelTemplate block(String parent, String variant, TextureSlot... requiredTextureKeys) {
            return new ModelTemplate(Optional.of(VerticalSlabs.id("block/" + parent)), Optional.of(variant), requiredTextureKeys);
        }

        // #endregion custom_model

        // #region custom_texture_map
        public static TextureMapping blockWithSameTextureForAllFaces(Block block) {
            return blockWithSameTextureForAllFaces(new Material(blockModelLocation(block)));
        }

        private static Identifier blockModelLocation(Block block) {
            Identifier id = BuiltInRegistries.BLOCK.getKey(block);
            return Identifier.fromNamespaceAndPath(id.getNamespace(), "block/" + id.getPath());
        }

        private static TextureMapping blockWithSameTextureForAllFaces(String texturePath) {
            return blockWithSameTextureForAllFaces(texture(texturePath));
        }

        private static TextureMapping blockWithSameTextureForAllFaces(Material texture) {
            return new TextureMapping()
                    .put(TextureSlot.TOP, texture)
                    .put(TextureSlot.BOTTOM, texture)
                    .put(TextureSlot.SIDE, texture);
        }

        public static Block modelBlockFor(Block block) {
            Identifier id = BuiltInRegistries.BLOCK.getKey(block);
            String path = id.getPath();
            if (path.startsWith("waxed_")) {
                Identifier unwaxedId = Identifier.fromNamespaceAndPath(id.getNamespace(), path.substring("waxed_".length()));
                return BuiltInRegistries.BLOCK.getOptional(unwaxedId)
                        .orElseThrow(() -> new IllegalStateException("Missing unwaxed block for " + id));
            }
            return block;
        }

        public static TextureMapping texturesFor(Block block) {
            String path = BuiltInRegistries.BLOCK.getKey(block).getPath();
            return switch (path) {
                case "sandstone", "red_sandstone" -> blockWithDistinctTopAndBottom(
                        path,
                        path + "_top",
                        path + "_bottom"
                );
                case "quartz_block" -> blockWithDistinctTopAndBottom(
                        "quartz_block_side",
                        "quartz_block_top",
                        "quartz_block_bottom"
                );
                case "smooth_sandstone" -> blockWithSameTextureForAllFaces("sandstone_top");
                case "smooth_red_sandstone" -> blockWithSameTextureForAllFaces("red_sandstone_top");
                case "smooth_quartz" -> blockWithSameTextureForAllFaces("quartz_block_bottom");
                default -> blockWithSameTextureForAllFaces(block);
            };
        }

        private static TextureMapping blockWithDistinctTopAndBottom(String side, String top, String bottom) {
            return new TextureMapping()
                    .put(TextureSlot.TOP, texture(top))
                    .put(TextureSlot.BOTTOM, texture(bottom))
                    .put(TextureSlot.SIDE, texture(side));
        }

        private static Material texture(String path) {
            return new Material(Identifier.fromNamespaceAndPath("minecraft", "block/" + path));
        }

        // #endregion custom_texture_map

        // #region custom_supplier
        private static BlockModelDefinitionGenerator createVerticalSlabBlockStates(Block vertSlabBlock, Identifier vertSlabId, Identifier fullBlockId) {
            MultiVariant vertSlabModel = BlockModelGenerators.plainVariant(vertSlabId);
            MultiVariant fullBlockModel = BlockModelGenerators.plainVariant(fullBlockId);
            return MultiVariantGenerator.dispatch(vertSlabBlock)
                    .with(PropertyDispatch.initial(VerticalSlabBlock.FACING, VerticalSlabBlock.SINGLE)
                            .select(Direction.NORTH, true, vertSlabModel.with(BlockModelGenerators.UV_LOCK))
                            .select(Direction.EAST, true, vertSlabModel.with(BlockModelGenerators.UV_LOCK).with(BlockModelGenerators.Y_ROT_90))
                            .select(Direction.SOUTH, true, vertSlabModel.with(BlockModelGenerators.UV_LOCK).with(BlockModelGenerators.Y_ROT_180))
                            .select(Direction.WEST, true, vertSlabModel.with(BlockModelGenerators.UV_LOCK).with(BlockModelGenerators.Y_ROT_270))
                            .select(Direction.NORTH, false, fullBlockModel.with(BlockModelGenerators.UV_LOCK))
                            .select(Direction.EAST, false, fullBlockModel.with(BlockModelGenerators.UV_LOCK))
                            .select(Direction.SOUTH, false, fullBlockModel.with(BlockModelGenerators.UV_LOCK))
                            .select(Direction.WEST, false, fullBlockModel.with(BlockModelGenerators.UV_LOCK))
                    );
        }

        // #endregion custom_supplier

        // #region custom_gen
        public static void registerVerticalSlab(BlockModelGenerators generator, Block vertSlabBlock, Block fullBlock, TextureMapping textures) {
            Identifier slabModel = VERTICAL_SLAB.create(vertSlabBlock, textures, generator.modelOutput);
            Identifier fullBlockModel = blockModelLocation(fullBlock);
            generator.blockStateOutput.accept(createVerticalSlabBlockStates(vertSlabBlock, slabModel, fullBlockModel));
            generator.registerSimpleItemModel(vertSlabBlock, slabModel);
        }

        // #endregion custom_gen
    }
    // #endregion custom_blockstate_model_generator

    // #region provider

    @Override
    public String getName() {
        return "VerticalSlabsModelProvider";
    }
}
// #endregion provider