/*
 * Decompiled with CFR 0.1.1 (FabricMC 57d88659).
 */
package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.world.border.WorldBorder;

public class WorldBorderWarningTimeChangedS2CPacket
implements Packet<ClientPlayPacketListener> {
    private final int warningTime;

    public WorldBorderWarningTimeChangedS2CPacket(WorldBorder worldBorder) {
        this.warningTime = worldBorder.getWarningTime();
    }

    public WorldBorderWarningTimeChangedS2CPacket(PacketByteBuf buf) {
        this.warningTime = buf.readVarInt();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeVarInt(this.warningTime);
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onWorldBorderWarningTimeChanged(this);
    }

    public int getWarningTime() {
        return this.warningTime;
    }
}

