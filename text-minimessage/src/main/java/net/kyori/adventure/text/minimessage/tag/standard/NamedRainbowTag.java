package net.kyori.adventure.text.minimessage.tag.standard;

import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.internal.serializer.SerializableResolver;
import net.kyori.adventure.text.minimessage.internal.serializer.TokenEmitter;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.util.HSVLike;
import org.jspecify.annotations.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

final class NamedRainbowTag extends AbstractColorChangingTag {

  static final String RAINBOW = "rainbow";

  static final TagResolver RESOLVER = SerializableResolver.claimingComponent(RAINBOW, NamedRainbowTag::create, AbstractColorChangingTag::claimComponent);

  private final boolean reversed;
  private double hue;
  private final float saturation;
  private int step;
  private double hueStep = 0f;

  static Tag create(final ArgumentQueue args, final Context ctx) {
    boolean reversed = args.flag("reverse").toBooleanOrElse(false);

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

  private NamedRainbowTag(boolean reversed, int phase, float saturation, int step, Context ctx) {
    super(ctx);
    this.reversed = reversed;
    this.hue = (phase % 10) / 10d;
    this.saturation = saturation;
    this.step = step;
  }

  @Override
  protected void init() {
    if (this.step == 0) {
      this.step = this.size();
    }
    this.hueStep = (this.reversed ? -1.0d : 1.0d) / this.step;
    if (reversed) this.advanceColor();
  }

  @Override
  protected void advanceColor() {
    this.hue += hueStep;
    this.hue %= 1d;
    if (this.hue < 0d) {
      this.hue += 1d;
    }
  }

  @Override
  protected TextColor color() {
    return TextColor.color(HSVLike.hsvLike((float) hue, saturation, 1.0f));
  }

  @Override
  protected Consumer<TokenEmitter> preserveData() {
    return emit -> {
      emit.tag("rainbow");
      if (!this.reversed) {
        emit.flag("reverse", true);
      }
      if (this.hue != 0d) {
        emit.namedArgument("phase", Integer.toString((int) this.hue * 10));
      }
      if (this.saturation != 1f) {
        emit.namedArgument("saturation", Float.toString(this.saturation));
      }
      if (this.step != 0) {
        emit.namedArgument("step", Integer.toString(this.step));
      }
    };
  }

  @Override
  public boolean equals(@Nullable Object other) {
    if (this == other) return true;
    if (!(other instanceof final NamedRainbowTag that)) return false;
    return this.hue == that.hue && this.saturation == that.saturation && this.step == that.step;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.hue, this.saturation, this.step);
  }

  @Override
  public String toString() {
    return "NamedRainbowTag{reversed=%b, hue=%f, saturation=%f, step=%d, hueStep=%f}"
      .formatted(this.reversed, this.hue, this.saturation, this.step, this.hueStep);
  }
}
