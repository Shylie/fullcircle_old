package com.github.shylie.fullcircle.data;

import com.github.shylie.fullcircle.FullCircle;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = FullCircle.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FCDataGenerators {
	private FCDataGenerators() { }

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator gen = event.getGenerator();
		ExistingFileHelper efh = event.getExistingFileHelper();

		gen.addProvider(new FCBlockStateProvider(gen, efh));
		gen.addProvider(new FCLootTables(gen));
	}
}
