package me.regos.deadbodies;

import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntityPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;

import java.util.ArrayList;
import java.util.List;

public class BodyManager {

    private List<Body> bodies;

    public BodyManager(){
        this.bodies = new ArrayList<>();
    }

    public List<Body> getBodies(){
        return bodies;
    }

    public void removeBodyNPC(Body body){
        Bukkit.getOnlinePlayers().forEach(player -> {
            ServerGamePacketListenerImpl ps = ((CraftPlayer) player).getHandle().connection;

            ps.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER, body.getNpc()));
            ps.send(new ClientboundRemoveEntityPacket(body.getNpc().getId()));
        });
    }
}
