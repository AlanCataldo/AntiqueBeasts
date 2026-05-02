package net.mebahel.antiquebeasts.util;

public final class PreDamageResult {
    public final boolean cancel;      // true => on annule le hit (return false)
    public final float finalAmount;   // dégâts après multipliers
    public final DamageElementUtil.ElementResult element; // debug / effets

    public PreDamageResult(boolean cancel, float finalAmount, DamageElementUtil.ElementResult element) {
        this.cancel = cancel;
        this.finalAmount = finalAmount;
        this.element = element;
    }

    public static PreDamageResult cancel() {
        return new PreDamageResult(true, 0f, null);
    }
}
