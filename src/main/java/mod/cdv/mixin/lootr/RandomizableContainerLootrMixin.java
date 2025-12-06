package mod.cdv.mixin.lootr;

import mod.cdv.util.CounterRecheckBlock;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.api.blockentity.ILootBlockEntity;
import noobanidus.mods.lootr.block.entities.LootrBarrelBlockEntity;
import noobanidus.mods.lootr.block.entities.LootrChestBlockEntity;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.data.SpecialChestInventory;
import noobanidus.mods.lootr.util.ChestUtil;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Mixin(RandomizableContainerBlockEntity.class)
public abstract class RandomizableContainerLootrMixin extends BaseContainerBlockEntity implements ILootBlockEntity, CounterRecheckBlock {
    public Set<UUID> openers = new HashSet<>();
    protected ResourceLocation savedLootTable = null;
    protected long seed = -1L;
    protected UUID tileId = null;
    @Shadow protected ResourceLocation lootTable;
    private boolean savingToItem = false;
    boolean opened;
    final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        @Override
        protected void onOpen(Level p_155460_, BlockPos p_155461_, BlockState p_155462_) {

        }

        @Override
        protected void onClose(Level p_155473_, BlockPos p_155474_, BlockState p_155475_) {

        }

        @Override
        protected void openerCountChanged(Level p_155463_, BlockPos p_155464_, BlockState p_155465_, int p_155466_, int p_155467_) {

        }

        protected boolean isOwnContainer(Player player) {
            AbstractContainerMenu var3 = player.containerMenu;
            if (var3 instanceof ChestMenu menu) {
                Container var4 = menu.getContainer();
                if (var4 instanceof SpecialChestInventory chest) {
                    return getTileId().equals(chest.getTileId());
                }
            }

            return false;
        }
    };

    protected RandomizableContainerLootrMixin(BlockEntityType<?> p_155076_, BlockPos p_155077_, BlockState p_155078_) {
        super(p_155076_, p_155077_, p_155078_);
    }

    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        BlockEntity blockentity = pLevel.getBlockEntity(pPos);
        if (blockentity instanceof LootrBarrelBlockEntity barrel) {
            barrel.recheckOpen();
        }

    }

    @Override
    public void unpackLootTable(Player player, Container inventory, ResourceLocation overrideTable, long l) {
        if (this.level != null && this.savedLootTable != null && this.level.getServer() != null) {
            LootTable loottable = this.level.getServer().getLootData().getLootTable(overrideTable != null ? overrideTable : this.savedLootTable);
            if (loottable == LootTable.EMPTY) {
                Logger var10000 = LootrAPI.LOG;
                ResourceLocation var10001 = this.level.dimension().location();
                var10000.error("Unable to fill loot barrel in {} at {} as the loot table '{}' couldn't be resolved! Please search the loot table in `latest.log` to see if there are errors in loading.", var10001, this.worldPosition, overrideTable != null ? overrideTable : this.savedLootTable);
                if (ConfigManager.REPORT_UNRESOLVED_TABLES.get()) {
                    player.displayClientMessage(ChestUtil.getInvalidTable(overrideTable != null ? overrideTable : this.savedLootTable), false);
                }
            }

            if (player instanceof ServerPlayer) {
                CriteriaTriggers.GENERATE_LOOT.trigger((ServerPlayer) player, overrideTable != null ? overrideTable : this.lootTable);
            }

            LootParams.Builder builder = (new LootParams.Builder((ServerLevel) this.level)).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(this.worldPosition));
            if (player != null) {
                builder.withLuck(player.getLuck()).withParameter(LootContextParams.THIS_ENTITY, player);
            }

            loottable.fill(inventory, builder.create(LootContextParamSets.CHEST), LootrAPI.getLootSeed(this.seed));
        }
    }

    @Inject(method = "setLootTable(Lnet/minecraft/resources/ResourceLocation;J)V", at = @At("HEAD"))
    private void setLootTable(ResourceLocation lootTableIn, long seedIn, CallbackInfo ci) {
        this.savedLootTable = lootTableIn;
        this.seed = seedIn;
    }

    @Shadow
    public abstract void setLootTable(ResourceLocation lootTable, long seed);

    @Override
    public void load(CompoundTag compound) {
        if (compound.contains("specialLootChest_table", 8)) {
            this.savedLootTable = ResourceLocation.parse(compound.getString("specialLootChest_table"));
        }

        if (compound.contains("specialLootChest_seed", 4)) {
            this.seed = compound.getLong("specialLootChest_seed");
        }

        if (this.savedLootTable == null && compound.contains("LootTable", 8)) {
            this.savedLootTable = ResourceLocation.parse(compound.getString("LootTable"));
            if (compound.contains("LootTableSeed", 4)) {
                this.seed = compound.getLong("LootTableSeed");
            }

            this.setLootTable(this.savedLootTable, this.seed);
        }

        if (compound.hasUUID("tileId")) {
            this.tileId = compound.getUUID("tileId");
        }

        if (this.tileId == null) {
            this.getTileId();
        }

        if (compound.contains("LootrOpeners")) {
            ListTag openers = compound.getList("LootrOpeners", 11);
            this.openers.clear();

            for (Tag item : openers) {
                this.openers.add(NbtUtils.loadUUID(item));
            }
        }

        this.requestModelDataUpdate();
        super.load(compound);
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        if (this.savedLootTable != null) {
            compound.putString("LootTable", this.savedLootTable.toString());
        }

        if (this.seed != -1L) {
            compound.putLong("LootTableSeed", this.seed);
        }

        if (!LootrAPI.shouldDiscard() && !this.savingToItem) {
            compound.putUUID("tileId", this.getTileId());
            ListTag list = new ListTag();

            for (UUID opener : this.openers) {
                list.add(NbtUtils.createUUID(opener));
            }

            compound.put("LootrOpeners", list);
        }
    }

    @Override
    public ResourceLocation getTable() {
        return this.savedLootTable;
    }

    @Override
    public BlockPos getPosition() {
        return this.getBlockPos();
    }

    @Override
    public long getSeed() {
        return this.seed;
    }

    @Override
    public UUID getTileId() {
        if (this.tileId == null) {
            this.tileId = UUID.randomUUID();
        }

        return this.tileId;
    }

    @Override
    public void updatePacketViaState() {
        if (this.level != null && !this.level.isClientSide) {
            BlockState state = this.level.getBlockState(this.getBlockPos());
            this.level.sendBlockUpdated(this.getBlockPos(), state, state, 8);
        }
    }

    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag result = super.getUpdateTag();
        this.saveAdditional(result);
        return result;
    }

    @Override
    public void setOpened(boolean b) {
        this.opened = b;
    }

    @Override
    public Set<UUID> getOpeners() {
        return this.openers;
    }

    @Nullable
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, BlockEntity::getUpdateTag);
    }

    public void onDataPacket(@NotNull Connection net, @NotNull ClientboundBlockEntityDataPacket pkt) {
        if (pkt.getTag() != null) {
            this.load(pkt.getTag());
        }

    }

    public void startOpen(Player pPlayer) {
        if (!this.remove && !pPlayer.isSpectator()) {
            this.openersCounter.incrementOpeners(pPlayer, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    public void stopOpen(Player pPlayer) {
        if (!this.remove && !pPlayer.isSpectator()) {
            this.openersCounter.decrementOpeners(pPlayer, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    @Override
    public void refreshCounter() {
        this.recheckOpen();
    }

    public void recheckOpen() {
        if (!this.remove) {
            this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }
}
