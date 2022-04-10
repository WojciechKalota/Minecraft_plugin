package me.regos.deadbodies;

import net.minecraft.server.level.ServerPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Body {

    private ServerPlayer npc;
    private UUID whoDied;
    private ItemStack[] contents;
    private long whenDied;
    private List<ArmorStand> armorStandList;


    public Body(){
        this.armorStandList = new ArrayList<>();
    }

    public Body(ServerPlayer npc, UUID whoDied, ItemStack[] contents, long whenDied) {
        this.npc = npc;
        this.whoDied = whoDied;
        this.contents = contents;
        this.whenDied = whenDied;
    }

    public List<ArmorStand> getArmorStandList() {
        return armorStandList;
    }

    public ServerPlayer getNpc() {
        return npc;
    }

    public void setNpc(ServerPlayer npc) {
        this.npc = npc;
    }

    public UUID getWhoDied() {
        return whoDied;
    }

    public void setWhoDied(UUID whoDied) {
        this.whoDied = whoDied;
    }

    public ItemStack[] getContents() {
        return contents;
    }

    public void setContents(ItemStack[] contents) {
        this.contents = contents;
    }

    public long getWhenDied() {
        return whenDied;
    }

    public void setWhenDied(long whenDied) {
        this.whenDied = whenDied;
    }
}
