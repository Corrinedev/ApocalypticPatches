package mod.cdv;

import net.mcreator.apocalypsenow.ApocalypsenowMod;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import noobanidus.mods.lootr.advancement.GenericTrigger;
import noobanidus.mods.lootr.api.IHasOpeners;
import noobanidus.mods.lootr.api.MenuBuilder;
import noobanidus.mods.lootr.api.blockentity.ILootBlockEntity;
import noobanidus.mods.lootr.block.LootrShulkerBlock;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.data.DataStorage;
import noobanidus.mods.lootr.data.SpecialChestInventory;
import noobanidus.mods.lootr.init.ModAdvancements;
import noobanidus.mods.lootr.init.ModStats;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Objects;
import java.util.UUID;

import static mod.cdv.AttributeRegistry.ATTRIBUTES;
import static mod.cdv.Boot.Config.config;
import static noobanidus.mods.lootr.util.ChestUtil.*;

@Mod(Constants.MODID)
public final class Boot {
    public Boot(FMLJavaModLoadingContext context) {
        final var iModBus = context.getModEventBus();
        ATTRIBUTES.register(iModBus);
        iModBus.register(this);
        //MinecraftForge.EVENT_BUS.addListener(Boot::onRightClick);
        context.registerConfig(ModConfig.Type.COMMON, config.getRight(), "apoc_patches.toml");
    }

    @SubscribeEvent
    public void modifyAttributes(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, AttributeRegistry.HEADSHOT_DATA.get(), 1.25);
    }

    public static void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        var plr = event.getEntity();
        var state = plr.level().getBlockState(event.getPos());
        var level = plr.level();
        if (plr instanceof ServerPlayer && state.getBlock().getDescriptionId().contains(ApocalypsenowMod.MODID)) {
            var tileEntity = level.getBlockEntity(event.getPos());
            if(tileEntity instanceof RandomizableContainerBlockEntity c) {
                //c.unpackLootTable(plr);
                handleLootChest(state.getBlock(), level, event.getPos(), plr);
                event.setCanceled(true);
            }
        }
    }

    public static void handleLootChest(Block block, Level level, BlockPos pos, Player player) {
        if (!level.isClientSide() && !player.isSpectator()) {
            BlockEntity te = level.getBlockEntity(pos);
            if (te instanceof ILootBlockEntity tile) {
                UUID tileId = tile.getTileId();
                if (tileId == null) {
                    player.displayClientMessage(Component.translatable("lootr.message.invalid_block").setStyle(getInvalidStyle()), true);
                    return;
                }

                if (te instanceof BaseContainerBlockEntity) {
                    BaseContainerBlockEntity bce = (BaseContainerBlockEntity)te;
                    if (!bce.canOpen(player)) {
                        return;
                    }
                }

                if (DataStorage.isDecayed(tileId)) {
                    level.destroyBlock(pos, true);
                    notifyDecay(player, tileId);
                    return;
                }

                int decayValue = DataStorage.getDecayValue(tileId);
                if (decayValue > 0 && ConfigManager.shouldNotify(decayValue)) {
                    player.displayClientMessage(Component.translatable("lootr.message.decay_in", new Object[]{decayValue / 20}).setStyle(getDecayStyle()), true);
                } else if (decayValue == -1 && ConfigManager.isDecaying((ServerLevel)level, tile)) {
                    startDecay(player, tileId, decayValue);
                }

                GenericTrigger<UUID> trigger = ModAdvancements.CHEST_PREDICATE;
                if (block instanceof BarrelBlock) {
                    trigger = ModAdvancements.BARREL_PREDICATE;
                } else if (block instanceof LootrShulkerBlock) {
                    trigger = ModAdvancements.SHULKER_PREDICATE;
                }

                trigger.trigger((ServerPlayer)player, tileId);
                if (DataStorage.isRefreshed(tileId)) {
                    DataStorage.refreshInventory(level, pos, tileId, (ServerPlayer)player);
                    notifyRefresh(player, tileId);
                }

                int refreshValue = DataStorage.getRefreshValue(tileId);
                if (refreshValue > 0 && ConfigManager.shouldNotify(refreshValue)) {
                    player.displayClientMessage(Component.translatable("lootr.message.refresh_in", new Object[]{refreshValue / 20}).setStyle(getRefreshStyle()), true);
                } else if (refreshValue == -1 && ConfigManager.isRefreshing((ServerLevel)level, tile)) {
                    startRefresh(player, tileId, refreshValue);
                }

                ServerPlayer var10003 = (ServerPlayer)player;
                RandomizableContainerBlockEntity var10004 = (RandomizableContainerBlockEntity)te;
                Objects.requireNonNull(tile);
                SpecialChestInventory provider = DataStorage.getInventory(level, tileId, pos, var10003, var10004, tile::unpackLootTable);
                if (provider == null) {
                    return;
                }

                provider.setMenuBuilder(new MenuBuilder() {
                    @Override
                    public AbstractContainerMenu build(int i, Inventory inventory, Container container, int i1) {
                        return var10004.createMenu(i, inventory, inventory.player);
                    }
                });

                checkScore((ServerPlayer)player, tileId);
                if (addOpener(tile, player)) {
                    te.setChanged();
                    tile.updatePacketViaState();
                }

                player.openMenu(provider);
                PiglinAi.angerNearbyPiglins(player, true);
            }

        } else {
            if (player.isSpectator()) {
                player.openMenu(null);
            }

        }
    }

    private static void checkScore(ServerPlayer player, UUID tileId) {
        if (!DataStorage.isScored(player.getUUID(), tileId)) {
            player.awardStat(ModStats.LOOTED_STAT);
            ModAdvancements.SCORE_PREDICATE.trigger(player, null);
            DataStorage.score(player.getUUID(), tileId);
        }

    }

    private static boolean addOpener(IHasOpeners openable, Player player) {
        return openable.getOpeners().add(player.getUUID());
    }

    private static void notifyDecay(Player player, UUID tileId) {
        player.displayClientMessage(Component.translatable("lootr.message.decayed").setStyle(getDecayStyle()), true);
        DataStorage.removeDecayed(tileId);
    }

    private static void notifyRefresh(Player player, UUID tileId) {
        DataStorage.removeRefreshed(tileId);
        player.displayClientMessage(Component.translatable("lootr.message.refreshed").setStyle(getRefreshStyle()), true);
    }

    private static void startDecay(Player player, UUID tileId, int decayValue) {
        DataStorage.setDecaying(tileId, ConfigManager.DECAY_VALUE.get());
        player.displayClientMessage(Component.translatable("lootr.message.decay_start", new Object[]{(Integer)ConfigManager.DECAY_VALUE.get() / 20}).setStyle(getDecayStyle()), true);
    }

    private static void startRefresh(Player player, UUID tileId, int refreshValue) {
        DataStorage.setRefreshing(tileId, ConfigManager.REFRESH_VALUE.get());
        player.displayClientMessage(Component.translatable("lootr.message.refresh_start", new Object[]{(Integer)ConfigManager.REFRESH_VALUE.get() / 20}).setStyle(getRefreshStyle()), true);
    }

    public static class Config {
        public static final Pair<Config, ForgeConfigSpec> config = new ForgeConfigSpec.Builder().configure(Config::new);
        public static ForgeConfigSpec.ConfigValue<Boolean> allowHeadshots;
        public Config(ForgeConfigSpec.Builder builder) {
            allowHeadshots = builder.comment("Enable or disable headshots (Extra Feature)").define("allowHeadshots", false);
            builder.build();
        }
    }
}
