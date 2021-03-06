package com.redgrapefruit.goldenforge.init

import com.redgrapefruit.goldenforge.block.FragmentCleanerBlock
import com.redgrapefruit.goldenforge.block.MetalFurnaceBlock
import com.redgrapefruit.goldenforge.block.MetalOreBlock
import com.redgrapefruit.goldenforge.block.PlateFactoryBlock
import com.redgrapefruit.goldenforge.blockentity.FragmentCleanerBlockEntity
import com.redgrapefruit.goldenforge.blockentity.MetalFurnaceBlockEntity
import com.redgrapefruit.goldenforge.blockentity.PlateFactoryBlockEntity
import com.redgrapefruit.goldenforge.util.IInitializer
import com.redgrapefruit.goldenforge.util.registerBlock
import com.redgrapefruit.goldenforge.util.registerBlockEntity
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.minecraft.block.Block
import net.minecraft.block.Material
import net.minecraft.block.entity.BlockEntityType

/** A registry for the mod's blocks (and their block items) */
object ModBlocks : IInitializer {
    // Ores
    val STEEL_ORE = MetalOreBlock(FabricBlockSettings.of(Material.METAL).hardness(1.5F), "steel")
    val DEEPSLATE_STEEL_ORE = MetalOreBlock(FabricBlockSettings.of(Material.METAL).hardness(1.8F), "steel")

    // Machines
    val FRAGMENT_CLEANER = FragmentCleanerBlock(FabricBlockSettings.of(Material.METAL).hardness(1.7F))
    val PLATE_FACTORY = PlateFactoryBlock(FabricBlockSettings.of(Material.METAL).hardness(1.9F))
    val METAL_FURNACE = MetalFurnaceBlock(FabricBlockSettings.of(Material.METAL).hardness(2.15F))

    // Block entities
    val FRAGMENT_CLEANER_BLOCK_ENTITY = makeType(::FragmentCleanerBlockEntity, FRAGMENT_CLEANER)
    val PLATE_FACTORY_BLOCK_ENTITY = makeType(::PlateFactoryBlockEntity, PLATE_FACTORY)
    val METAL_FURNACE_BLOCK_ENTITY = makeType(::MetalFurnaceBlockEntity, METAL_FURNACE)

    override fun initialize() {
        registerBlock("steel_ore", STEEL_ORE)
        registerBlock("deepslate_steel_ore", DEEPSLATE_STEEL_ORE)
        registerBlock("fragment_cleaner", FRAGMENT_CLEANER)
        registerBlock("plate_factory", PLATE_FACTORY)
        registerBlock("metal_furnace", METAL_FURNACE)

        registerBlockEntity("fragment_cleaner", FRAGMENT_CLEANER_BLOCK_ENTITY)
        registerBlockEntity("plate_factory", PLATE_FACTORY_BLOCK_ENTITY)
        registerBlockEntity("metal_furnace", METAL_FURNACE_BLOCK_ENTITY)
    }

    private fun makeType(factory: FabricBlockEntityTypeBuilder.Factory<*>, block: Block): BlockEntityType<*> {
        return FabricBlockEntityTypeBuilder.create(factory, block).build()
    }
}
