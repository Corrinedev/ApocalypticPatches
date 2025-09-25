package mod.cdv.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static mod.cdv.util.CacheKeyHolder.isApocalypseContainer;

@Mixin(value = NetworkHooks.class, remap = false)
public class NetworkHooksCancelScreenMixin {
    @Inject(method = "openScreen(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/MenuProvider;)V", at = @At("HEAD"), cancellable = true)
    private static void overrideUseOnContainer(ServerPlayer player, MenuProvider containerSupplier, CallbackInfo ci) {
        if(player.containerMenu != null && isApocalypseContainer(player.containerMenu)) {
            System.out.println("cancel menu");
            ci.cancel();
        }
    }
}
