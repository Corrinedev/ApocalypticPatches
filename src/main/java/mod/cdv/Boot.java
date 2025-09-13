package mod.cdv;

import net.minecraft.world.entity.EntityType;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static mod.cdv.AttributeRegistry.ATTRIBUTES;

@Mod(Constants.MODID)
public final class Boot {

    public Boot() {
        final var iModBus = FMLJavaModLoadingContext.get().getModEventBus();
        ATTRIBUTES.register(iModBus);
        iModBus.register(this);
    }
    @SubscribeEvent
    public void modifyAttributes(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, AttributeRegistry.HEADSHOT_DATA.get(), 1.25);
    }
}
