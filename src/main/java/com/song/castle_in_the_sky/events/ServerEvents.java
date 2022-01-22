package com.song.castle_in_the_sky.events;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.mojang.serialization.Codec;
import com.song.castle_in_the_sky.CastleInTheSky;
import com.song.castle_in_the_sky.blocks.block_entities.LaputaCoreBE;
import com.song.castle_in_the_sky.config.ConfigCommon;
import com.song.castle_in_the_sky.effects.EffectRegister;
import com.song.castle_in_the_sky.features.CastleStructure;
import com.song.castle_in_the_sky.features.StructureFeatureRegister;
import com.song.castle_in_the_sky.features.StructureRegister;
import com.song.castle_in_the_sky.items.ItemsRegister;
import com.song.castle_in_the_sky.items.LevitationStone;
import com.song.castle_in_the_sky.network.Channel;
import com.song.castle_in_the_sky.network.ClientHandlerClass;
import com.song.castle_in_the_sky.network.ServerToClientInfoPacket;
import com.song.castle_in_the_sky.utils.CapabilityCastle;
import com.song.castle_in_the_sky.utils.MyTradingRecipe;
import com.song.castle_in_the_sky.utils.RandomTradeBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.StructureSpawnListGatherEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.network.PacketDistributor;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerEvents {

    /**
     * Adapted from https://github.com/TelepathicGrunt/StructureTutorialMod
     * Check the link above for a detailed tutorial on structure generation
     * Tells the chunkgenerator which biomes our structure can spawn in.
     * Will go into the world's chunkgenerator and manually add our structure spacing.
     * If the spacing is not added, the structure doesn't spawn.
     *
     * Use this for dimension blacklists for your structure.
     * (Don't forget to attempt to remove your structure too from the map if you are blacklisting that dimension!)
     * (It might have your structure in it already.)
     *
     * Basically use this to make absolutely sure the chunkgenerator can or cannot spawn your structure.
     */
    private static Method GETCODEC_METHOD;
    @SubscribeEvent
    public void addDimensionalSpacing(final WorldEvent.Load event) {
        if(event.getWorld() instanceof ServerLevel serverLevel){
            ChunkGenerator chunkGenerator = serverLevel.getChunkSource().getGenerator();
            // Skip superflat to prevent issues with it. Plus, users don't want structures clogging up their superflat worlds.
            if (chunkGenerator instanceof FlatLevelSource && serverLevel.dimension().equals(Level.OVERWORLD)) {
                return;
            }

            StructureSettings worldStructureConfig = chunkGenerator.getSettings();

            //////////// BIOME BASED STRUCTURE SPAWNING ////////////
            /*
             * NOTE: BiomeLoadingEvent from Forge API does not work with structures anymore.
             * Instead, we will use the below to add our structure to overworld biomes.
             * Remember, this is temporary until Forge API finds a better solution for adding structures to biomes.
             */

            // Create a mutable map we will use for easier adding to biomes
            HashMap<StructureFeature<?>, HashMultimap<ConfiguredStructureFeature<?, ?>, ResourceKey<Biome>>> STStructureToMultiMap = new HashMap<>();

            // Add the resourcekey of all biomes that this Configured Structure can spawn in.
            for(Map.Entry<ResourceKey<Biome>, Biome> biomeEntry : serverLevel.registryAccess().ownedRegistryOrThrow(Registry.BIOME_REGISTRY).entrySet()) {
                // Skip all ocean, end, nether, and none category biomes.
                // You can do checks for other traits that the biome has.
//                if(biomeEntry.getKey() == Biomes.DEEP_OCEAN || biomeEntry.getKey() == Biomes.DEEP_COLD_OCEAN || biomeEntry.getKey() == Biomes.DEEP_LUKEWARM_OCEAN|| biomeEntry.getKey() == Biomes.DEEP_FROZEN_OCEAN) {
//                    associateBiomeToConfiguredStructure(STStructureToMultiMap, StructureFeatureRegister.CONFIGURED_CASTLE_IN_THE_SKY, biomeEntry.getKey());
//                }
                associateBiomeToConfiguredStructure(STStructureToMultiMap, StructureFeatureRegister.CONFIGURED_CASTLE_IN_THE_SKY, biomeEntry.getKey());
            }

            // Alternative way to add our structures to a fixed set of biomes by creating a set of biome resource keys.
            // To create a custom resource key that points to your own biome, do this:
            // ResourceKey.of(Registry.BIOME_REGISTRY, new ResourceLocation("modid", "custom_biome"))
//            ImmutableSet<ResourceKey<Biome>> overworldBiomes = ImmutableSet.<ResourceKey<Biome>>builder()
//                    .add(Biomes.FOREST)
//                    .add(Biomes.MEADOW)
//                    .add(Biomes.PLAINS)
//                    .add(Biomes.SAVANNA)
//                    .add(Biomes.SNOWY_PLAINS)
//                    .add(Biomes.SWAMP)
//                    .add(Biomes.SUNFLOWER_PLAINS)
//                    .add(Biomes.TAIGA)
//                    .build();
//            overworldBiomes.forEach(biomeKey -> associateBiomeToConfiguredStructure(STStructureToMultiMap, STConfiguredStructures.CONFIGURED_RUN_DOWN_HOUSE, biomeKey));

            // Grab the map that holds what ConfigureStructures a structure has and what biomes it can spawn in.
            // Requires AccessTransformer  (see resources/META-INF/accesstransformer.cfg)
            ImmutableMap.Builder<StructureFeature<?>, ImmutableMultimap<ConfiguredStructureFeature<?, ?>, ResourceKey<Biome>>> tempStructureToMultiMap = ImmutableMap.builder();
            worldStructureConfig.configuredStructures.entrySet().stream().filter(entry -> !STStructureToMultiMap.containsKey(entry.getKey())).forEach(tempStructureToMultiMap::put);

            // Add our structures to the structure map/multimap and set the world to use this combined map/multimap.
            STStructureToMultiMap.forEach((key, value) -> tempStructureToMultiMap.put(key, ImmutableMultimap.copyOf(value)));

            // Requires AccessTransformer  (see resources/META-INF/accesstransformer.cfg)
            worldStructureConfig.configuredStructures = tempStructureToMultiMap.build();


            //////////// DIMENSION BASED STRUCTURE SPAWNING (OPTIONAL) ////////////
            /*
             * Skip Terraforged's chunk generator as they are a special case of a mod locking down their chunkgenerator.
             * They will handle your structure spacing for your if you add to BuiltinRegistries.NOISE_GENERATOR_SETTINGS in your structure's registration.
             * This here is done with reflection as this tutorial is not about setting up and using Mixins.
             * If you are using mixins, you can call the codec method with an invoker mixin instead of using reflection.
             */
            try {
                if(GETCODEC_METHOD == null) GETCODEC_METHOD = ObfuscationReflectionHelper.findMethod(ChunkGenerator.class, "codec");
                ResourceLocation cgRL = Registry.CHUNK_GENERATOR.getKey((Codec<? extends ChunkGenerator>) GETCODEC_METHOD.invoke(chunkGenerator));
                if(cgRL != null && cgRL.getNamespace().equals("terraforged")) return;
            }
            catch(Exception e){
                CastleInTheSky.LOGGER.error("Was unable to check if " + serverLevel.dimension().location() + " is using Terraforged's ChunkGenerator.");
            }

            /*
             * Prevent spawning our structure in Vanilla's superflat world as
             * people seem to want their superflat worlds free of modded structures.
             * Also that vanilla superflat is really tricky and buggy to work with in my experience.
             */
            if(chunkGenerator instanceof FlatLevelSource &&
                    serverLevel.dimension().equals(Level.OVERWORLD)){
                return;
            }

            /*
             * putIfAbsent so people can override the spacing with dimension datapacks themselves if they wish to customize spacing more precisely per dimension.
             * Requires AccessTransformer  (see resources/META-INF/accesstransformer.cfg)
             *
             * NOTE: if you add per-dimension spacing configs, you can't use putIfAbsent as BuiltinRegistries.NOISE_GENERATOR_SETTINGS in FMLCommonSetupEvent
             * already added your default structure spacing to some dimensions. You would need to override the spacing with .put(...)
             * And if you want to do dimension blacklisting, you need to remove the spacing entry entirely from the map below to prevent generation safely.
             */
            Map<StructureFeature<?>, StructureFeatureConfiguration> tempMap = new HashMap<>(worldStructureConfig.structureConfig());
            tempMap.putIfAbsent(StructureRegister.CASTLE_IN_THE_SKY.get(), StructureSettings.DEFAULTS.get(StructureRegister.CASTLE_IN_THE_SKY.get()));
            worldStructureConfig.structureConfig = tempMap;
        }
    }

    /**
     * Helper method that handles setting up the map to multimap relationship to help prevent issues.
     */
    private static void associateBiomeToConfiguredStructure(Map<StructureFeature<?>, HashMultimap<ConfiguredStructureFeature<?, ?>, ResourceKey<Biome>>> STStructureToMultiMap, ConfiguredStructureFeature<?, ?> configuredStructureFeature, ResourceKey<Biome> biomeRegistryKey) {
        STStructureToMultiMap.putIfAbsent(configuredStructureFeature.feature, HashMultimap.create());
        HashMultimap<ConfiguredStructureFeature<?, ?>, ResourceKey<Biome>> configuredStructureToBiomeMultiMap = STStructureToMultiMap.get(configuredStructureFeature.feature);
        if(configuredStructureToBiomeMultiMap.containsValue(biomeRegistryKey)) {
            CastleInTheSky.LOGGER.error("""
                    Detected 2 ConfiguredStructureFeatures that share the same base StructureFeature trying to be added to same biome. One will be prevented from spawning.
                    This issue happens with vanilla too and is why a Snowy Village and Plains Village cannot spawn in the same biome because they both use the Village base structure.
                    The two conflicting ConfiguredStructures are: {}, {}
                    The biome that is attempting to be shared: {}
                """,
                    BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(configuredStructureFeature),
                    BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(configuredStructureToBiomeMultiMap.entries().stream().filter(e -> e.getValue() == biomeRegistryKey).findFirst().get().getKey()),
                    biomeRegistryKey
            );
        }
        else{
            configuredStructureToBiomeMultiMap.put(configuredStructureFeature, biomeRegistryKey);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onGatherStructureSpawn(StructureSpawnListGatherEvent event){
        if (event.getStructure() instanceof CastleStructure){
            event.addEntitySpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.ZOMBIE, 3, 2, 5));
            event.addEntitySpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.SKELETON, 3, 2, 5));
            event.addEntitySpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.SPIDER, 1, 2, 3));
        }

    }

    private static final Set<String> DESTRUCTION_INCANTATIONS = new HashSet<>(Arrays.asList("BALSE", "BALUS", "バルス", "巴鲁斯"));
    private static final int SEARCH_RADIUS = 5;
    private static final int SEARCH_RADIUS2 = SEARCH_RADIUS * SEARCH_RADIUS;
    private static final int SEARCH_HEIGHT=3;

    @SubscribeEvent
    public void onPlayerChat(final ServerChatEvent event){
        if (DESTRUCTION_INCANTATIONS.contains(event.getMessage())){
            AtomicBoolean warned = new AtomicBoolean(false);
            event.getPlayer().getCapability(CapabilityCastle.CASTLE_CAPS).ifPresent(
                    (data -> warned.set(data.isIncantationWarned()))
            );
            if (! warned.get()){
                event.getPlayer().sendMessage(new TranslatableComponent("info."+CastleInTheSky.MOD_ID+".destruction_warning").withStyle(ChatFormatting.RED, ChatFormatting.BOLD), event.getPlayer().getUUID());
                event.getPlayer().getCapability(CapabilityCastle.CASTLE_CAPS).ifPresent((data -> data.setIncantationWarned(true)));
                return;
            }
            if (event.getPlayer().getMainHandItem().getItem() instanceof LevitationStone){
                boolean found = false;
                for (int dy=-SEARCH_HEIGHT; !found && dy<=SEARCH_HEIGHT; dy++){
                    for (int dx=-SEARCH_RADIUS; !found && dx<+SEARCH_RADIUS; dx++){
                        for (int dz=-SEARCH_RADIUS; !found && dz<+SEARCH_RADIUS; dz++){
                            if (dx*dx+dy*dy < SEARCH_RADIUS2){
                                BlockEntity blockEntity = event.getPlayer().level.getBlockEntity(event.getPlayer().blockPosition().offset(dx, dy, dz));
                                if (blockEntity instanceof LaputaCoreBE){
                                    ((LaputaCoreBE) blockEntity).setDestroying(true);
                                    event.getPlayer().getCapability(CapabilityCastle.CASTLE_CAPS).ifPresent((data -> data.setIncantationWarned(false)));
                                    found = true;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onAttachCapEntity(final AttachCapabilitiesEvent<Entity> event){
        if (event.getObject() instanceof Player){
            event.addCapability(new ResourceLocation(CastleInTheSky.MOD_ID, "castle_caps"), new CapabilityCastle());
        }
    }

    @SubscribeEvent
    public void registerCaps(RegisterCapabilitiesEvent event) {
        event.register(CapabilityCastle.class);
    }

    @SubscribeEvent
    public void onVillageTradeRegister(VillagerTradesEvent event){
        for (MyTradingRecipe recipe: ConfigCommon.MY_TRADING_RECIPES){
            if((recipe.getItem1()!=null || recipe.getItem2() != null) && Objects.requireNonNull(event.getType().getRegistryName()).toString().equals(recipe.getStringProfession())){
                int level = recipe.getLevel();
                List<VillagerTrades.ItemListing> tmp = event.getTrades().get(level);
                ArrayList<VillagerTrades.ItemListing> mutableTrades = new ArrayList<>(tmp);
                mutableTrades.add(
                        new RandomTradeBuilder(64, 25, 0.05f)
                                .setPrice(recipe.getItem1(), recipe.price1Min().get(), recipe.price1Max().get())
                                .setPrice2(recipe.getItem2(), recipe.price2Min().get(), recipe.price2Max().get())
                                .setForSale(recipe.getOutput(), recipe.outputMin().get(), recipe.outputMax().get())
                                .build());
                event.getTrades().put(level, mutableTrades);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void modifyStructureSpawnList(StructureSpawnListGatherEvent event){
//        if(event.getStructure() == StructureRegister.CASTLE_IN_THE_SKY.get()){
//            // No mob should spawn here
//            // TODO: add misc spawn like fish, iron golems, etc..
//            List<MobSpawnInfo.Spawners> spawners = event.getEntitySpawns(EntityClassification.MONSTER);
//            for(MobSpawnInfo.Spawners spawner: spawners){
//                event.removeEntitySpawn(EntityClassification.MONSTER, spawner);
//            }
//        }
    }

    @SubscribeEvent
    public void onBlockBreak(PlayerEvent.BreakSpeed event){
        Player playerEntity = event.getPlayer();
        if(playerEntity.hasEffect(EffectRegister.SACRED_CASTLE_EFFECT.get()) && !playerEntity.isCreative()){
            event.setCanceled(true);
            if(playerEntity.level.isClientSide()){
                ClientHandlerClass.showSacredCastleInfoBreak();
            }
        }
    }

    @SubscribeEvent
    public void onBlockPlaced(BlockEvent.EntityPlaceEvent event){
        Entity entity = event.getEntity();
        if(entity instanceof Player && ((Player)entity).hasEffect(EffectRegister.SACRED_CASTLE_EFFECT.get()) && !((Player) entity).isCreative()){
            event.setCanceled(true);
            if(entity instanceof ServerPlayer){
                Channel.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) entity),
                        new ServerToClientInfoPacket(new TranslatableComponent(String.format("info.%s.sacred_castle_effect.place", CastleInTheSky.MOD_ID)).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD)));
            }
        }
    }

    @SubscribeEvent
    public void onMobDrop(LivingDropsEvent event){
        DamageSource damageSource = event.getSource();
        if(damageSource instanceof EntityDamageSource){
            Entity killer = damageSource.getEntity();
            if(killer instanceof LivingEntity && ((LivingEntity) killer).hasEffect(EffectRegister.SACRED_CASTLE_EFFECT.get())){
                LivingEntity dropper = event.getEntityLiving();
                if(dropper.getRandom().nextDouble()< ConfigCommon.YELLOW_KEY_DROP_RATE.get()){
                    event.getDrops().add(new ItemEntity(dropper.level, dropper.position().x, dropper.position().y, dropper.position().z, new ItemStack(ItemsRegister.YELLOW_KEY.get())));
                }
                if(dropper.getRandom().nextDouble()<ConfigCommon.BLUE_KEY_DROP_RATE.get()){
                    event.getDrops().add(new ItemEntity(dropper.level, dropper.position().x, dropper.position().y, dropper.position().z, new ItemStack(ItemsRegister.BLUE_KEY.get())));
                }
                if(dropper.getRandom().nextDouble()<ConfigCommon.RED_KEY_DROP_RATE.get()){
                    event.getDrops().add(new ItemEntity(dropper.level, dropper.position().x, dropper.position().y, dropper.position().z, new ItemStack(ItemsRegister.RED_KEY.get())));
                }
            }
        }
    }
}
