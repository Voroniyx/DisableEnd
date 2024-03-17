package org.voroniyx.mcdisabledend.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.EndPortalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.voroniyx.mcdisabledend.noend.server.DisableEndServer;

import static org.voroniyx.mcdisabledend.noend.func.RespawnPosFinder.FindRespawnPos;

@Mixin(EndPortalBlock.class)
public class NoEndMixin {
    @Inject(method = "onEntityCollision", at = @At("HEAD"))
    private void injectOnEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo ci) {

        if (world.getGameRules().getBoolean(DisableEndServer.DISABLE_END) && world instanceof ServerWorld && entity.canUsePortals() && !entity.hasVehicle() && !entity.hasPassengers() && entity.getWorld().getRegistryKey() != World.END) {

            if(entity.isPlayer()) {

                if(entity.getServer().getPlayerManager().getPlayer(entity.getUuid()).getSpawnPointPosition() != null) {
                    System.out.println(entity.getServer().getPlayerManager().getPlayer(entity.getUuid()).getSpawnPointPosition());
                    double spawnx = entity.getServer().getPlayerManager().getPlayer(entity.getUuid()).getSpawnPointPosition().getX();
                    double spawnz = entity.getServer().getPlayerManager().getPlayer(entity.getUuid()).getSpawnPointPosition().getZ();
                    double spawnY = entity.getServer().getPlayerManager().getPlayer(entity.getUuid()).getSpawnPointPosition().getY();
                    int[] tpos = FindRespawnPos((int)spawnx, (int)spawnY, (int)spawnz, entity.getWorld());
                    entity.teleport(tpos[0], tpos[1], tpos[2]);
                } else {
                    entity.sendMessage(Text.literal("No bed found!").formatted(Formatting.ITALIC, Formatting.WHITE));
                    double spawnx = entity.getWorld().getSpawnPos().getX();
                    double spawnz = entity.getWorld().getSpawnPos().getZ();
                    double spawnY = entity.getWorld().getSpawnPos().getY();
                    entity.teleport(spawnx, spawnY, spawnz);
                }

                entity.sendMessage(Text.literal("The End is disabled").formatted(Formatting.RED, Formatting.BOLD));
                System.out.println("Respawned");
            } else {

                double spawnx = entity.getWorld().getSpawnPos().getX();
                double spawnz = entity.getWorld().getSpawnPos().getZ();
                double spawnY = entity.getWorld().getSpawnPos().getY();
                entity.teleport(spawnx, spawnY, spawnz);
                System.out.println("Teleported");
            }
        }
    }

}
