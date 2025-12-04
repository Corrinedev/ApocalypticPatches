package mod.cdv.mixin.quark;

import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.violetmoon.quark.base.handler.InventoryTransferHandler;

@Mixin(value = InventoryTransferHandler.class, remap = false)
public abstract class InventoryTransferHandlerMixin {
    @Inject(
            method = "accepts",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void dead$add(AbstractContainerMenu container, Player player, CallbackInfoReturnable<Boolean> cir) {
        if(container instanceof InventoryMenu || container instanceof CraftingMenu || InventoryTransferHandlerMixin.dead$isCreativeMenu(player, container)) {
            cir.setReturnValue(false);
            return;
        }

        if(container.getClass().getName().startsWith("net.mcreator.apocalypsenow.world.inventory"))
            cir.setReturnValue(true);
    }

    @Unique
    private static boolean dead$isCreativeMenu(Player player, AbstractContainerMenu menu){
        if(!player.level().isClientSide())
            return false;
        else
            return menu instanceof CreativeModeInventoryScreen.ItemPickerMenu;
    }
}