package jas7277.verticalslabs.client;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class VerticalSlabsDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(VerticalSlabsEnglishLangProvider::new);
		pack.addProvider(VerticalSlabsModelProvider::new);
		pack.addProvider(VerticalSlabsBlockTagProvider::new);
		pack.addProvider(VerticalSlabsBlockLootTableProvider::new);
	}
}
