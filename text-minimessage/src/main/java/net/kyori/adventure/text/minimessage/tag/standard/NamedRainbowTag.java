package net.kyori.adventure.text.minimessage.tag.standard;

import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.internal.serializer.SerializableResolver;
import net.kyori.adventure.text.minimessage.internal.serializer.TokenEmitter;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jspecify.annotations.Nullable;
import java.util.function.Consumer;

final class NamedRainbowTag extends AbstractColorChangingTag {

  static final String RAINBOW = "rainbow";

  static final TagResolver RESOLVER = SerializableResolver.claimingComponent(RAINBOW, NamedRainbowTag::create, AbstractColorChangingTag::claimComponent);

  private final boolean reversed;
  private final double dividedPhase;
  private final float saturation;
  private final int step;

  private int colorIndex = 0;

  static Tag create(final ArgumentQueue args, final Context ctx) {
    boolean reversed = args.flag("reversed").toBooleanOrElse(false);

    int phase = 0;
    if (args.isPresent("phase")) {
      Argument phaseArgument = args.orThrow("phase");
      try {
        phase = Integer.parseInt(phaseArgument.value());
      } catch (NumberFormatException e) {
        throw ctx.newException("Invalid phase argument: expected int, got " + phaseArgument.value(), e, args);
      }
    }

    float saturation = 1.0f;
    if (args.isPresent("saturation")) {
      final Argument saturationArgument = args.orThrow("saturation");
      try {
        saturation = Float.parseFloat(saturationArgument.value());
      } catch (NumberFormatException e) {
        throw ctx.newException("Invalid saturation argument: expected float, got " + saturationArgument.value(), e, args);
      }
      if (saturation < 0f || saturation > 1f) {
        throw ctx.newException("Invalid saturation argument: the value must be in range [0.0f, 1.0f]", args);
      }
    }

    int step = 0;
    if (args.isPresent("step")) {
      final Argument stepArgument = args.orThrow("phase");
      try {
        step = Integer.parseInt(stepArgument.value());
      } catch (NumberFormatException e) {
        throw ctx.newException("Invalid step argument: expected int, got " + stepArgument.value(), e, args);
      }
      if (step < 0) {
        throw ctx.newException("Invalid step argument: the value must not be less than 0", args);
      }
    }

    return new NamedRainbowTag(reversed, phase, saturation, step, ctx);
  }

  public NamedRainbowTag(boolean reversed, int phase, float saturation, int step, Context ctx) {
    super(ctx);
    this.reversed = reversed;
    this.dividedPhase = phase / 10d;
    this.saturation = saturation;
    this.step = step;
  }

  @Override
  protected void init() {

  }

  @Override
  protected void advanceColor() {

  }

  @Override
  protected TextColor color() {
    return null;
  }

  @Override
  protected Consumer<TokenEmitter> preserveData() {
    return null;
  }

  @Override
  public boolean equals(@Nullable Object other) {
    return false;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public String toString() {
    return "";
  }

}
