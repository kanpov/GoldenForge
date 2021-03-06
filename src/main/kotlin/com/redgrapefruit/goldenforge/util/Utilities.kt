package com.redgrapefruit.goldenforge.util

import net.fabricmc.fabric.api.biome.v1.BiomeModifications
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.block.Block
import net.minecraft.block.entity.AbstractFurnaceBlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.item.*
import net.minecraft.util.Identifier
import net.minecraft.util.Language
import net.minecraft.util.registry.BuiltinRegistries
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World
import net.minecraft.world.gen.GenerationStep
import net.minecraft.world.gen.feature.ConfiguredFeature
import net.minecraft.world.gen.feature.PlacedFeature
import net.minecraft.world.gen.random.AbstractRandom
import net.minecraft.world.gen.random.RandomSeed
import net.minecraft.world.gen.random.Xoroshiro128PlusPlusRandom
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.random.Random

// Miscellaneous utilities

/** Typed mod ID, use for Identifiers */
const val ModID = "goldenforge"

/** Pretty full name of the mod */
const val ModName = "GoldenForge"

/** Shared [Logger] used by the mod. */
val logger: Logger = LoggerFactory.getLogger(ModName)

/** Easily accessible [FabricLoader] instance upon demand. */
val loader: FabricLoader by lazy { FabricLoader.getInstance() }

/** Obtains the mod version of the given [mod] with the Fabric Loader API.
 *  The resulting version is equal to the one specified in the `fabric.mod.json` file */
fun FabricLoader.getModVersion(mod: String): String {
    return getModContainer(mod)
        .orElseThrow { RuntimeException("Mod $mod isn't present!") }
        .metadata
        .version
        .friendlyString
}

/** Converts this [String] into an [Identifier] with the namespace being [ModID] */
inline val String.id: Identifier
    get() = Identifier(ModID, this)

/** Converts this [String] into an [Identifier], given that the string has the ID form, for example, "minecraft:shears". */
inline val String.parsedId: Identifier
    get() = Identifier.tryParse(this) ?: throw RuntimeException("Invalid Identifier form: '$this'")

/** Executes the given [action] randomly with the inputted percentage [chance]. */
inline fun applyChance(chance: Int, action: () -> Unit) {
    if (Random.nextInt(101) <= chance) {
        action.invoke()
    }
}

/** Translates the given translation [key] into the current game language using the lang file's contents. */
fun translate(key: String): String {
    return Language.getInstance().get(key)
}

/** A helper to make `if (!world.isClient)` look more meaningful */
val World.isServer get() = !isClient

/** Checks if [stack] is a vanilla fuel, using the furnace's fuel registry under the hood. */
fun isFuel(stack: ItemStack): Boolean {
    return AbstractFurnaceBlockEntity.canUseAsFuel(stack)
}

// Registering / Initialization

val IdNull = "null".id

fun Identifier.referencesNull() = this == IdNull

/** Item group / Creative tab for the mod's items. */
val sharedItemGroup: ItemGroup = FabricItemGroupBuilder
    .create("main".id)
    .icon { Items.IRON_ORE.defaultStack }
    .build()

/** Base item settings applied to every item. */
val sharedItemSettings: Item.Settings = Item.Settings().group(sharedItemGroup)

/**
 *  Defines an `object` in the `init` package that performs registering of some type of objects.
 *  One [IInitializer] should only be responsible for one type of objects.
 */
interface IInitializer {
    /** Run the registering code in the implementation of this method */
    fun initialize()
}

// Registering Helpers

fun registerBlock(name: String, block: Block) {
    Registry.register(Registry.BLOCK, name.id, block)
    val blockItem = BlockItem(block, sharedItemSettings)
    Registry.register(Registry.ITEM, name.id, blockItem)
    Item.BLOCK_ITEMS[block] = blockItem // why was this necessary?
}

fun registerItem(name: String, item: Item) {
    Registry.register(Registry.ITEM, name.id, item)
}

fun registerConfiguredFeature(name: String, feature: ConfiguredFeature<*, *>) {
    Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, name.id, feature)
}

fun registerPlacedFeature(name: String, feature: PlacedFeature) {
    Registry.register(BuiltinRegistries.PLACED_FEATURE, name.id, feature)
}

fun registerOreBiomeModification(name: String) {
    BiomeModifications.addFeature(
        BiomeSelectors.foundInOverworld(),
        GenerationStep.Feature.UNDERGROUND_ORES,
        RegistryKey.of(Registry.PLACED_FEATURE_KEY, name.id))
}

fun registerBlockEntity(name: String, type: BlockEntityType<*>) {
    Registry.register(Registry.BLOCK_ENTITY_TYPE, name.id, type)
}

// Randomness

/**
 * A more advanced (Xoroshiro 128 ++) random implementation for this mod.
 * Initialized lazily and captures the current time as the base for the seed.
 */
val sharedRandom: AbstractRandom by lazy { Xoroshiro128PlusPlusRandom(RandomSeed.getSeed()) }

// Constants; math

object Constants {
    const val SECOND_LENGTH_IN_TICKS = 20
    const val MINUTE_LENGTH_IN_SECONDS = 60
    const val MINUTE_LENGTH_IN_TICKS = SECOND_LENGTH_IN_TICKS * MINUTE_LENGTH_IN_SECONDS
    const val TICK_TO_QUALITY_DIVISOR = 1000
}

fun ensurePositive(i: Int): Int {
    return if (i > 0) i else 0
}
