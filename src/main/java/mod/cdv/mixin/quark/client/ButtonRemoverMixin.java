package mod.cdv.mixin.quark.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.violetmoon.quark.base.client.handler.InventoryButtonHandler;
import org.violetmoon.zeta.client.event.play.ZScreen;

import java.util.function.Predicate;

@Mixin(value = InventoryButtonHandler.class, remap = false)
public abstract class ButtonRemoverMixin {

    @Shadow
    protected static void applyProviders(ZScreen.Init.Post event, InventoryButtonHandler.ButtonTargetType type, AbstractContainerScreen<?> screen, Predicate<Slot> slotPred) {
    }

    @Inject(method = "initGui", at = @At(value = "HEAD"), cancellable = true)
    private static void deinitGui(ZScreen.Init.Post event, CallbackInfo ci) {
        var mc = Minecraft.getInstance();
        var screen = event.getScreen();
        if(screen instanceof InventoryScreen inv && !mc.player.isCreative()) {
            applyProviders(event, InventoryButtonHandler.ButtonTargetType.PLAYER_INVENTORY, inv, (s) -> s.container == mc.player.getInventory() && s.getSlotIndex() == 17);
            ci.cancel();
            return;
        }
        if(screen instanceof CreativeModeInventoryScreen || screen instanceof InventoryScreen) {
            ci.cancel();
        }
    }
}
