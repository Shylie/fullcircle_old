package com.github.shylie.fullcircle.data;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.github.shylie.fullcircle.FullCircle;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.LootTableProvider;
import net.minecraft.loot.LootParameterSet;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableManager;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.ValidationTracker;
import net.minecraft.loot.LootTable.Builder;
import net.minecraft.util.ResourceLocation;

public class FCLootTables extends LootTableProvider {
	public FCLootTables(DataGenerator dataGenerator) {
		super(dataGenerator);
	}

	private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> lootTableGenerators = ImmutableList.of(
		Pair.of(FCBlockLootTables::new, LootParameterSets.BLOCK)
	);

	@Override
	protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, Builder>>>, LootParameterSet>> getTables() {
		return lootTableGenerators;
	}

	@Override
	protected void validate(Map<ResourceLocation, LootTable> map, ValidationTracker validationtracker) {
		final Set<ResourceLocation> modLootTableIds = LootTables.getReadOnlyLootTables().stream().filter(lootTable -> lootTable.getNamespace().equals(FullCircle.MOD_ID)).collect(Collectors.toSet());

		for (ResourceLocation id : Sets.difference(modLootTableIds, map.keySet())) {
			validationtracker.addProblem("Missing mod loot table: " + id);
		}

		map.forEach((id, lootTable) -> LootTableManager.validateLootTable(validationtracker, id, lootTable));
	}

	@Override
	public String getName() {
		return FullCircle.MOD_ID + "_lootTables";
	}
}
