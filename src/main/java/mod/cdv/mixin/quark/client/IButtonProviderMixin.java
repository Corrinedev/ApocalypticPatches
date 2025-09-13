package mod.cdv.mixin.quark.client;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.violetmoon.quark.api.IQuarkButtonAllowed;

@Mixin(AbstractContainerScreen.class)
public class IButtonProviderMixin implements IQuarkButtonAllowed { }
