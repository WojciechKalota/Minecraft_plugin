package me.regos.deadbodies.tasks;

import me.regos.deadbodies.Body;
import me.regos.deadbodies.BodyManager;
import me.regos.deadbodies.DeadBodies;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;

public class BodyRemoverTask extends BukkitRunnable {

    private final DeadBodies bodied;
    private final BodyManager bodyManager;

    public BodyRemoverTask(DeadBodies bodied, BodyManager bodyManager) {
        this.bodied = bodied;
        this.bodyManager = bodyManager;
    }

    @Override
    public void run() {
        Iterator<Body> bodyIterator = bodyManager.getBodies().iterator();
        while(bodyIterator.hasNext()){
            Body body = bodyIterator.next();

            long now = System.currentTimeMillis();
            if((now - body.getWhenDied()) >= 10000){
                bodyIterator.remove();

                new BukkitRunnable(){

                    @Override
                    public void run() {
                        Location location = body.getNpc().getBukkitEntity().getLocation();
                        Bukkit.getOnlinePlayers().forEach(player -> {
                            ServerGamePacketListenerImpl ps = ((CraftPlayer) player).getHandle().connection;
                            body.getNpc().setPos(location.getX(), location.getY() - 0.01, location.getZ());

                            ps.send(new ClientboundTeleportEntityPacket(body.getNpc()));
                        });
                        if(!location.add(0, 1, 0).getBlock().isPassable()){

                            bodyManager.removeBodyNPC(body);

                            this.cancel();
                        }

                    }
                }.runTaskTimerAsynchronously(bodied, 0L, 5L);

                Player playerWhoDied = Bukkit.getPlayer(body.getWhoDied());
                if (playerWhoDied != null){
                    playerWhoDied.sendMessage("Your body has not been claimed within 10 seconds and has rotten. Your items have been returned to you. Try not to die again");

                    Inventory inventory = playerWhoDied.getInventory();
                    inventory.addItem(body.getContents()).values().stream().forEach(itemStack -> {
                        playerWhoDied.getWorld().dropItem(playerWhoDied.getLocation(), itemStack);
                    });
                }
            }
        }
    }
}
