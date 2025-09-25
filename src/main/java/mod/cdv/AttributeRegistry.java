package mod.cdv;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static mod.cdv.Constants.MODID;

public final class AttributeRegistry {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, MODID);
    public static final RegistryObject<Attribute> HEADSHOT_DATA = ATTRIBUTES.register("headshot", () -> new RangedAttribute("attributes.apoc_patches.headshot", 1.0, 0.0, Integer.MAX_VALUE));
}
