package dev.rurino.hasugoods.util;

import blue.endless.jankson.Jankson;
import io.wispforest.owo.config.ConfigWrapper;
import io.wispforest.owo.config.ConfigWrapper.BuilderConsumer;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.util.Observable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class HasuConfig extends ConfigWrapper<dev.rurino.hasugoods.util.HasuConfigModel> {

    public final Keys keys = new Keys();

    private final Option<java.lang.Float> toutoshiDamage = this.optionForKey(this.keys.toutoshiDamage);
    private final Option<java.lang.Integer> oshiProtectionDuration = this.optionForKey(this.keys.oshiProtectionDuration);
    private final Option<java.lang.Integer> numBadgeInBox = this.optionForKey(this.keys.numBadgeInBox);

    private HasuConfig() {
        super(dev.rurino.hasugoods.util.HasuConfigModel.class);
    }

    private HasuConfig(BuilderConsumer consumer) {
        super(dev.rurino.hasugoods.util.HasuConfigModel.class, consumer);
    }

    public static HasuConfig createAndLoad() {
        var wrapper = new HasuConfig();
        wrapper.load();
        return wrapper;
    }

    public static HasuConfig createAndLoad(BuilderConsumer consumer) {
        var wrapper = new HasuConfig(consumer);
        wrapper.load();
        return wrapper;
    }

    public float toutoshiDamage() {
        return toutoshiDamage.value();
    }

    public void toutoshiDamage(float value) {
        toutoshiDamage.set(value);
    }

    public int oshiProtectionDuration() {
        return oshiProtectionDuration.value();
    }

    public void oshiProtectionDuration(int value) {
        oshiProtectionDuration.set(value);
    }

    public int numBadgeInBox() {
        return numBadgeInBox.value();
    }

    public void numBadgeInBox(int value) {
        numBadgeInBox.set(value);
    }


    public static class Keys {
        public final Option.Key toutoshiDamage = new Option.Key("toutoshiDamage");
        public final Option.Key oshiProtectionDuration = new Option.Key("oshiProtectionDuration");
        public final Option.Key numBadgeInBox = new Option.Key("numBadgeInBox");
    }
}

