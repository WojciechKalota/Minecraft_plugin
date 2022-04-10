package me.regos.deadbodies.listeners;

import com.mojang.authlib.GameProfile;
//import net.minecraft.server.MinecraftServer;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.server.level.ServerPlayer;
import com.mojang.authlib.properties.Property;
import me.regos.deadbodies.Body;
import me.regos.deadbodies.BodyManager;
import me.regos.deadbodies.DeadBodies;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.server.level.*;
import net.minecraft.server.*;
import net.minecraft.*;
//import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.*;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;

import static net.minecraft.world.entity.Pose.SLEEPING;

public class DeathListener implements Listener {

    private final DeadBodies bodied;

    public DeathListener(DeadBodies bodied){
        this.bodied = bodied;
    }

    @EventHandler
    public void onPlayerDeath (PlayerDeathEvent e){

        BodyManager bodyManager = bodied.getBodyManager();

        Player p = e.getEntity();
        p.sendMessage("Imagine dying");
        Body body = spawnBody(p);
        bodyManager.getBodies().add(body);

        e.getDrops().clear();
    }

    @EventHandler
    public void onPlayerRightClick2(PlayerInteractAtEntityEvent e){

        if(e.getRightClicked() instanceof ArmorStand){
            Player whoClicked = e.getPlayer();
            Iterator<Body> bodyIterator = bodied.getBodyManager().getBodies().iterator();
            ArmorStand armorStand = ((ArmorStand) e.getRightClicked());
            while(bodyIterator.hasNext()){
                Body body = bodyIterator.next();

                if(body.getArmorStandList().contains(armorStand)){

                    bodyIterator.remove();

                    bodied.getBodyManager().removeBodyNPC(body);

                    for(ItemStack item : body.getContents()){
                        whoClicked.getWorld().dropItem(body.getNpc().getBukkitEntity().getLocation(), item);
                    }

                    whoClicked.sendMessage("YOu have looted someone else's body. If they had any items it will appear.");
                    whoClicked.playSound(whoClicked.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0f, 1.0f);
                }
            }
        }

    }

    private Body spawnBody(Player p){

        Body body = new Body();
        body.setWhoDied(p.getUniqueId());
        body.setWhenDied(System.currentTimeMillis());
        body.setContents(Arrays.stream(p.getInventory().getContents()).filter(itemStack -> {
            if (itemStack != null){
                return true;
            }
            return false;
        }).toArray(ItemStack[]::new));

       CraftPlayer craftPlayer = (CraftPlayer) p;
        MinecraftServer server = craftPlayer.getHandle().getServer();
        ServerLevel level = craftPlayer.getHandle().getLevel();

        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), " ");
        GameProfile deadProfile = craftPlayer.getProfile();

        Property property = (Property) deadProfile.getProperties().get("textures").toArray()[0];

        String signature = property.getSignature();
        String texture = property.getValue();

        //String signature = "Vjix3LNJcr7hDKumqxUmW5YO3p7vdpP0dpXQb5cGbDgvuQqbIzjP1KKS4gcV39Q5lrjOpfCW4XdrdXHL2KnKpQ6gi63bEumU4joK9bXQw3T+4mzEsMK9PfJn8OB4+qMPNQIaSL7WSsPqJ6HYZfI2kY9cXQ9qi9BKNWMzkz+y5rTWq0e90uPc3rz+ao2Xeb6xl+PLNwzdJUOn7fjXccdjOpd5PazXITYT9nh0KhIpQ9jd3VgBuP3TDa++Wgd4yyU+apTWfqZxAzcnvafv4U31pAz5kfdx3Qr0nY6vc1CV9Vuo5ujOb2XI/JuiaDeHY74PsRL5VmtW2wS95xVFMLyUx34afLxLu8Z3YwsMtDS9CP3PH2jiKEhO16duct55gNmNe5KBL1gWQDhmoQAeYO+iMdNe8j3jid1khvRLDaN2uTskTOoRZadND1ty404PqQEVdlkm6hgYuK5MRuf6AFDdTd3sdsxENXOL+vvo2kJA0DRDUAYishxxW6ZjfJV44m09Cc1xfonemFUi+h1HZswsI//O5H23r0dfl4JSMwVtUt3cV19q5ZMRHLXq1tfHc+tt36rzxbz7JJ2NCRyEgFzs0DgSP4haLHk9h7dlEafXiit8zOamnWrr3Lz1xcZMA4tLzU5SCzzPRkdNBlE1fWTNbn6WH6fB5qrJbHW/hSntUCQ=";
        //String texture = "ewogICJ0aW1lc3RhbXAiIDogMTY0MDU5Mzg1Njc4NywKICAicHJvZmlsZUlkIiA6ICI2OTY0NTAyOTJjOTQ0NDFiYjFhNTJmNDhlOTZiNjRkYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJfUmVHb3NfIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzMyNjg5YWQxMjNmYWMzM2YwOGZkY2MwYWNmOTU2MDA0NWUxZmU2ZTcwMWZjMTY3ZjVkNDE5YmNjOGY4MWM0OTQiCiAgICB9CiAgfQp9";

        gameProfile.getProperties().put("textures", new Property("textures", texture, signature));

        ServerPlayer npc = new ServerPlayer(server, level, gameProfile);
        body.setNpc(npc);

        Location locationDied = p.getLocation();
        while (locationDied.getBlock().getType().equals(Material.AIR)){
            locationDied.subtract(0, 1, 0);
        }

        npc.setPos(locationDied.getX(), locationDied.getY() + 1, locationDied.getZ());
        npc.setPose(SLEEPING);

        ArmorStand armorStand1 = (ArmorStand) p.getWorld().spawnEntity(npc.getBukkitEntity().getLocation(), EntityType.ARMOR_STAND);
        armorStand1.setSmall(true);
        armorStand1.setInvisible(true);
        armorStand1.setInvulnerable(true);
        armorStand1.setGravity(false);

        ArmorStand armorStand2 = (ArmorStand) p.getWorld().spawnEntity(npc.getBukkitEntity().getLocation().subtract(1, 0, 0), EntityType.ARMOR_STAND);
        armorStand2.setSmall(true);
        armorStand2.setInvisible(true);
        armorStand2.setInvulnerable(true);
        armorStand2.setGravity(false);

        ArmorStand armorStand3 = (ArmorStand) p.getWorld().spawnEntity(npc.getBukkitEntity().getLocation().subtract(2, 0 ,0), EntityType.ARMOR_STAND);
        armorStand3.setSmall(true);
        armorStand3.setInvisible(true);
        armorStand3.setInvulnerable(true);
        armorStand3.setGravity(false);

        body.setNpc(npc);
        body.getArmorStandList().add(armorStand1);
        body.getArmorStandList().add(armorStand2);
        body.getArmorStandList().add(armorStand3);

        PlayerTeam team = new PlayerTeam(new Scoreboard(), npc.displayName);
        team.getPlayers().add(" ");
        team.setNameTagVisibility(Team.Visibility.NEVER);

        Bukkit.getOnlinePlayers().forEach(player -> {
            ServerGamePacketListenerImpl ps = ((CraftPlayer) player).getHandle().connection;
            ps.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, npc));
            ps.send(new ClientboundAddPlayerPacket(npc));
            ps.send(new ClientboundSetEntityDataPacket(npc.getId(), npc.getEntityData(), true));

            ps.send(ClientboundSetPlayerTeamPacket.createRemovePacket(team));
            ps.send(ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(team, true));

            new BukkitRunnable(){

                @Override
                public void run() {
                    ps.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER, npc)); //removes from tab
                }
            }.runTaskLaterAsynchronously(DeadBodies.getPlugin(), 20L);
        });

        return body;
    }
}
