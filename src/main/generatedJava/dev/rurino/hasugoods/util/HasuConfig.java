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
    private final Option<java.lang.Integer> chestBadgeDropMinCount = this.optionForKey(this.keys.chestBadgeDropMinCount);
    private final Option<java.lang.Integer> chestBadgeDropMaxCount = this.optionForKey(this.keys.chestBadgeDropMaxCount);
    private final Option<java.lang.Integer> chestEmptyDropWeight = this.optionForKey(this.keys.chestEmptyDropWeight);
    private final Option<java.lang.Integer> chestBadgeDropWeight = this.optionForKey(this.keys.chestBadgeDropWeight);
    private final Option<java.lang.Integer> chestBoxDropWeight = this.optionForKey(this.keys.chestBoxDropWeight);
    private final Option<java.lang.Integer> regularBadgeDropWeight = this.optionForKey(this.keys.regularBadgeDropWeight);
    private final Option<java.lang.Integer> secretBadgeDropWeight = this.optionForKey(this.keys.secretBadgeDropWeight);

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

    public int chestBadgeDropMinCount() {
        return chestBadgeDropMinCount.value();
    }

    public void chestBadgeDropMinCount(int value) {
        chestBadgeDropMinCount.set(value);
    }

    public int chestBadgeDropMaxCount() {
        return chestBadgeDropMaxCount.value();
    }

    public void chestBadgeDropMaxCount(int value) {
        chestBadgeDropMaxCount.set(value);
    }

    public int chestEmptyDropWeight() {
        return chestEmptyDropWeight.value();
    }

    public void chestEmptyDropWeight(int value) {
        chestEmptyDropWeight.set(value);
    }

    public int chestBadgeDropWeight() {
        return chestBadgeDropWeight.value();
    }

    public void chestBadgeDropWeight(int value) {
        chestBadgeDropWeight.set(value);
    }

    public int chestBoxDropWeight() {
        return chestBoxDropWeight.value();
    }

    public void chestBoxDropWeight(int value) {
        chestBoxDropWeight.set(value);
    }

    public int regularBadgeDropWeight() {
        return regularBadgeDropWeight.value();
    }

    public void regularBadgeDropWeight(int value) {
        regularBadgeDropWeight.set(value);
    }

    public int secretBadgeDropWeight() {
        return secretBadgeDropWeight.value();
    }

    public void secretBadgeDropWeight(int value) {
        secretBadgeDropWeight.set(value);
    }


    public static class Keys {
        public final Option.Key toutoshiDamage = new Option.Key("toutoshiDamage");
        public final Option.Key oshiProtectionDuration = new Option.Key("oshiProtectionDuration");
        public final Option.Key numBadgeInBox = new Option.Key("numBadgeInBox");
        public final Option.Key chestBadgeDropMinCount = new Option.Key("chestBadgeDropMinCount");
        public final Option.Key chestBadgeDropMaxCount = new Option.Key("chestBadgeDropMaxCount");
        public final Option.Key chestEmptyDropWeight = new Option.Key("chestEmptyDropWeight");
        public final Option.Key chestBadgeDropWeight = new Option.Key("chestBadgeDropWeight");
        public final Option.Key chestBoxDropWeight = new Option.Key("chestBoxDropWeight");
        public final Option.Key regularBadgeDropWeight = new Option.Key("regularBadgeDropWeight");
        public final Option.Key secretBadgeDropWeight = new Option.Key("secretBadgeDropWeight");
    }
}

