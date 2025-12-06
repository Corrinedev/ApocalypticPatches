package mod.cdv.mixin.quark.client;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.violetmoon.quark.api.IQuarkButtonAllowed;
import org.violetmoon.quark.content.client.module.ChestSearchingModule;

@Mixin(value = ChestSearchingModule.Client.class, remap = false)
public abstract class ChestSearchingModuleMixin {
    @Definition(id = "IQuarkButtonAllowed", type = IQuarkButtonAllowed.class)
    @Expression("? instanceof IQuarkButtonAllowed")
    @ModifyExpressionValue(
            method = "initGui",
            at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private static boolean addButtonsToApoc(boolean original, @Local(name = "gui") Screen screen) {
        // Package classpath contains all menus from Apoc
        if(screen.getClass().getName().startsWith("net.mcreator.apocalypsenow.client.gui"))
            return true;

        return original;
    }
}
