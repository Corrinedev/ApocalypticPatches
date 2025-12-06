package mod.cdv.mixin.quark.client;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.violetmoon.quark.api.IQuarkButtonAllowed;
import org.violetmoon.quark.base.handler.InventoryTransferHandler;

@Mixin(value = InventoryTransferHandler.class, remap = false)
public abstract class InventoryTransferHandlerMixin {
    @Definition(id = "IQuarkButtonAllowed", type = IQuarkButtonAllowed.class)
    @Expression("? instanceof IQuarkButtonAllowed")
    @ModifyExpressionValue(
            method = "accepts",
            at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private static boolean addButtonsToApoc(boolean original, @Local(name = "container") AbstractContainerMenu menu) {
        // Package classpath contains all menus from Apoc
        if(menu.getClass().getName().startsWith("net.mcreator.apocalypsenow.world.inventory"))
            return true;

        return original;
    }
}
