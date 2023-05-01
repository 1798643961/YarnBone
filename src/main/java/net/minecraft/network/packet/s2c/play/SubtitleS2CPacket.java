/*
 * Decompiled with CFR 0.1.1 (FabricMC 57d88659).
 */
package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;

public class SubtitleS2CPacket
implements Packet<ClientPlayPacketListener> {
    private final Text subtitle;

    public SubtitleS2CPacket(Text subtitle) {
        this.subtitle = subtitle;
    }

    public SubtitleS2CPacket(PacketByteBuf buf) {
        this.subtitle = buf.readText();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeText(this.subtitle);
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onSubtitle(this);
    }

    public Text getSubtitle() {
        return this.subtitle;
    }
}

