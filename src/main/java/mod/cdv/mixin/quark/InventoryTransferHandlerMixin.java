package mod.cdv.mixin.quark;

import net.mcreator.apocalypsenow.block.MetalShelvesBlock;
import net.mcreator.apocalypsenow.init.ApocalypsenowModMenus;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.RegistryObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.violetmoon.quark.api.IQuarkButtonAllowed;
import org.violetmoon.quark.base.config.QuarkGeneralConfig;
import org.violetmoon.quark.base.handler.InventoryTransferHandler;

import java.util.HashSet;

import static mod.cdv.util.CacheKeyHolder.APOCALYPSE_NOW_TYPES;
import static mod.cdv.util.CacheKeyHolder.isApocalypseContainer;

@Mixin(value = InventoryTransferHandler.class, remap = false)
public abstract class InventoryTransferHandlerMixin {
    @Inject(
            method = "accepts",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void accepts(AbstractContainerMenu container, Player player, CallbackInfoReturnable<Boolean> cir) {
        if(container instanceof CreativeModeInventoryScreen.ItemPickerMenu || container instanceof InventoryMenu) {
            cir.setReturnValue(false);
            return;
        }
        if (!(container instanceof CraftingMenu)) {
            if(isApocalypseContainer(container.getType()))
                cir.setReturnValue(true);
        }
    }
}