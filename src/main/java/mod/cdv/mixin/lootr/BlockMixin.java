package mod.cdv.mixin.lootr;

import mod.cdv.util.CounterRecheckBlock;
import net.mcreator.apocalypsenow.ApocalypsenowMod;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.api.blockentity.ILootBlockEntity;
import noobanidus.mods.lootr.block.LootrBarrelBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockMixin {

    @Shadow
    public abstract Block getBlock();

    BlockEntityTicker<?> lootTicker = new BlockEntityTicker<>() {
        @Override
        public void tick(Level level, BlockPos pos, BlockState state, BlockEntity entity) {
            if (entity instanceof CounterRecheckBlock iLootBlock) {
                iLootBlock.refreshCounter();
            }
        }
    };

    @Inject(method = "getTicker", at = @At("HEAD"), cancellable = true)
    private <T extends BlockEntity> void getTicker
            (Level level, BlockEntityType<T> eType, CallbackInfoReturnable<BlockEntityTicker<T>> cir) {
        if(this.getBlock() instanceof EntityBlock && this.getBlock().getDescriptionId().contains(ApocalypsenowMod.MODID)) {
            cir.setReturnValue((BlockEntityTicker<T>) lootTicker);
        }
    }
}
