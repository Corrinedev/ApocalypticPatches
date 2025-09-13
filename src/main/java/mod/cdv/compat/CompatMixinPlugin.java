package mod.cdv.compat;

import net.minecraftforge.fml.loading.LoadingModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public abstract class CompatMixinPlugin implements IMixinConfigPlugin {

    public abstract String getModId();

    public void onLoad(String mixinPackage) {

    }

    public String getRefMapperConfig() {
        return "";
    }

    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return LoadingModList.get().getModFileById(getModId()) != null;
    }

    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    public List<String> getMixins() {
        return List.of();
    }

    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}