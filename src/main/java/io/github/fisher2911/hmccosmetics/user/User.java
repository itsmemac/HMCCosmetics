package io.github.fisher2911.hmccosmetics.user;

import io.github.fisher2911.hmccosmetics.gui.ArmorItem;
import io.github.fisher2911.hmccosmetics.inventory.PlayerArmor;
import io.github.fisher2911.hmccosmetics.message.MessageHandler;
import io.github.fisher2911.hmccosmetics.message.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class User {

    private final UUID uuid;
    private final PlayerArmor playerArmor;
    private ArmorStand attached;

    public User(final UUID uuid, final PlayerArmor playerArmor) {
        this.uuid = uuid;
        this.playerArmor = playerArmor;
    }

    public @Nullable Player getPlayer() {
        return Bukkit.getPlayer(this.uuid);
    }

    public UUID getUuid() {
        return uuid;
    }

    public PlayerArmor getPlayerArmor() {
        return playerArmor;
    }

    public void setBackpack(final ArmorItem backpack) {
        this.playerArmor.setBackpack(backpack);
    }

    public void setOrUnsetBackpack(final ArmorItem backpack, final MessageHandler messageHandler) {

        final Player player = this.getPlayer();

        if (player == null) {
            return;
        }

        if (backpack.getId().equals(this.playerArmor.getBackpack().getId())) {
            this.setBackpack(new ArmorItem(
                    new ItemStack(Material.AIR),
                    "",
                    "",
                    ArmorItem.Type.BACKPACK
            ));

            messageHandler.sendMessage(
                    player,
                    Messages.REMOVED_BACKPACK
            );

            return;
        }

        this.setBackpack(backpack);
        messageHandler.sendMessage(
                player,
                Messages.SET_BACKPACK
        );
    }


    public void setHat(final ArmorItem hat) {
        this.playerArmor.setHat(hat);
        this.getPlayer().getEquipment().setHelmet(this.playerArmor.getHat().getItemStack());
    }

    public void setOrUnsetHat(final ArmorItem hat, final MessageHandler messageHandler) {

        final Player player = this.getPlayer();

        if (player == null) {
            return;
        }

        if (hat.getId().equals(this.playerArmor.getHat().getId())) {
            this.setHat(new ArmorItem(
                    new ItemStack(Material.AIR),
                    "",
                    "",
                    ArmorItem.Type.HAT
            ));

            messageHandler.sendMessage(
                    player,
                    Messages.REMOVED_HAT
            );
            return;
        }

        this.setHat(hat);
        messageHandler.sendMessage(
                player,
                Messages.SET_HAT
        );
    }

    public void detach() {
        if (this.attached != null) {
            this.attached.remove();
        }
    }

    // teleports armor stand to the correct position
    // todo change to packets
    public void updateArmorStand() {
        final ArmorItem backpackArmorItem = this.playerArmor.getBackpack();
        if (backpackArmorItem == null ) {
            return;
        }

        final ItemStack backpackItem = backpackArmorItem.getItemStack();

        if (backpackItem == null) {
            return;
        }

        final Player player = this.getPlayer();

        if (player == null) {
            return;
        }

        if (this.attached == null) {
            this.attached = player.getWorld().spawn(player.getLocation(),
                    ArmorStand.class,
                    armorStand -> {
                        armorStand.setVisible(false);
                        armorStand.setMarker(true);
                        player.addPassenger(armorStand);
                    });
        }

        if (!player.getPassengers().contains(this.attached)) {
            player.addPassenger(this.attached);
        }

        final EntityEquipment equipment = this.attached.getEquipment();

        if (!backpackItem.equals(equipment.getChestplate())) {
            equipment.setChestplate(backpackItem);
        }

        this.attached.
                setRotation(
                        player.getLocation().getYaw(),
                        player.getLocation().getPitch());
    }

}
