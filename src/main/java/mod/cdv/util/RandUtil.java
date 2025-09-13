package mod.cdv.util;

import net.minecraft.util.RandomSource;

import java.util.Random;

public class RandUtil {
    public static final Random rand = new Random(System.currentTimeMillis());
    public static final RandomSource randSource = RandomSource.create(System.currentTimeMillis());
}
