package mod.cdv.mixin.quark.client;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.violetmoon.quark.api.IQuarkButtonAllowed;
import org.violetmoon.quark.base.client.handler.InventoryButtonHandler;

@Mixin(value = InventoryButtonHandler.class, remap = false)
public abstract class InventoryButtonHandlerMixin {
    @Definition(id = "IQuarkButtonAllowed", type = IQuarkButtonAllowed.class)
    @Expression("? instanceof IQuarkButtonAllowed")
    @ModifyExpressionValue(
            method = "initGui",
            at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private static boolean forciblyAddApocScreens(boolean original, @Local(name = "screen") Screen screen) {
        // Package classpath contains all screens from Apoc
        // Now makes quark add buttons even if the config is set to whitelist and doesn't include the Apoc screens
        if(screen.getClass().getName().startsWith("net.mcreator.apocalypsenow.client.gui."))
            return true;
        else
            return original;
    }
}
