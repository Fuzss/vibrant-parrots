package fuzs.vibrantparrots.common.advancements.criterion;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.puzzleslib.api.entity.v1.VariantUtils;
import fuzs.vibrantparrots.common.world.entity.animal.parrot.ParrotVariant;
import fuzs.vibrantparrots.common.world.entity.animal.parrot.VibrantParrot;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record ParrotPredicate(Optional<Either<Parrot.Variant, Holder<ParrotVariant>>> variant) implements EntitySubPredicate {
    public static final MapCodec<ParrotPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Codec.either(
                    Parrot.Variant.CODEC,
                    ParrotVariant.CODEC).optionalFieldOf(VariantUtils.TAG_VARIANT).forGetter(ParrotPredicate::variant))
            .apply(instance, ParrotPredicate::new));

    @Override
    public MapCodec<? extends EntitySubPredicate> codec() {
        return CODEC;
    }

    @Override
    public boolean matches(Entity entity, ServerLevel level, @Nullable Vec3 position) {
        if (!(entity instanceof Parrot parrot)) {
            return false;
        } else if (this.variant.isEmpty()) {
            return true;
        } else {
            return this.variant.get().map((Parrot.Variant variant) -> {
                return !(parrot instanceof VibrantParrot) && parrot.getVariant() == variant;
            }, (Holder<ParrotVariant> variant) -> {
                return parrot instanceof VibrantParrot
                        && ((VibrantParrot) parrot).getParrotVariant().value() == variant.value();
            });
        }
    }
}
