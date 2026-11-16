package com.quickload.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Quick Load (cliente, Minecraft 1.21.11, mappings oficiales de Mojang).
 *
 * Todo pasa en slotClicked(), que es el metodo por el que la pantalla de
 * inventario/cofre envia cada clic al servidor. Si cancelamos aqui, el clic
 * simplemente no se envia (no hay desincronizacion).
 *
 * 1) Shift + clic DERECHO sobre un totem del inventario -> lo manda a la offhand
 *    (igual que shift + clic izquierdo con Inventory Control Tweaks).
 * 2) Shift + doble clic ("mover todos los iguales"): no toca los totems de la
 *    hotbar ni de la offhand.
 * 3) Doble clic normal ("recoger todos los iguales", solo tiene efecto si los
 *    totems se apilan en tu servidor): no recoge si tienes un totem en la
 *    hotbar o en la offhand.
 */
@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {

    // Campo privado de AbstractContainerScreen: true mientras se resuelve un doble clic.
    @Shadow
    private boolean doubleclick;

    @Shadow
    protected abstract void slotClicked(Slot slot, int slotId, int mouseButton, ClickType type);

    @Inject(method = "slotClicked", at = @At("HEAD"), cancellable = true)
    private void quickload$onSlotClicked(Slot slot, int slotId, int mouseButton, ClickType type, CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        boolean inPlayerInventory = slot != null && slot.container instanceof Inventory;
        int idx = inPlayerInventory ? slot.getContainerSlot() : -1;

        // 1) Shift + clic derecho sobre un totem -> offhand (solo si la offhand esta vacia).
        if (type == ClickType.QUICK_MOVE && mouseButton == 1
                && inPlayerInventory
                && idx >= 0 && idx < 36 // inventario principal + hotbar
                && slot.getItem().is(Items.TOTEM_OF_UNDYING)
                && player.getOffhandItem().isEmpty()) {
            // Igual que la tecla "intercambiar con la otra mano" sobre un slot: SWAP con boton 40.
            this.slotClicked(slot, slotId, 40, ClickType.SWAP);
            ci.cancel();
            return;
        }

        // 2) Shift + doble clic: proteger los totems de la hotbar (0-8) y la offhand (40).
        if (type == ClickType.QUICK_MOVE && this.doubleclick
                && inPlayerInventory
                && ((idx >= 0 && idx < 9) || idx == 40)
                && slot.getItem().is(Items.TOTEM_OF_UNDYING)) {
            ci.cancel();
            return;
        }

        // 3) Doble clic normal (recoger todos): no si hay un totem en hotbar/offhand.
        if (type == ClickType.PICKUP_ALL
                && player.containerMenu.getCarried().is(Items.TOTEM_OF_UNDYING)
                && quickload$hasProtectedTotem(player.getInventory())) {
            ci.cancel();
        }
    }

    @Unique
    private static boolean quickload$hasProtectedTotem(Inventory inventory) {
        for (int i = 0; i < 9; i++) {
            if (inventory.getItem(i).is(Items.TOTEM_OF_UNDYING)) {
                return true;
            }
        }
        return inventory.getItem(40).is(Items.TOTEM_OF_UNDYING);
    }
}
