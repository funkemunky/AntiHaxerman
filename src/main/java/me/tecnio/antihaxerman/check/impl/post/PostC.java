package me.tecnio.antihaxerman.check.impl.post;

import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.utils.packet.PacketUtils;

@CheckInfo(name = "Post", type = "C")
public final class PostC extends Check {
    public PostC(PlayerData data) {
        super(data);
    }

    private boolean sent = false;
    public long lastFlying, lastPacket;

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (PacketUtils.isFlyingPacket(event.getPacketId())) {
            final long now = System.currentTimeMillis();
            final long delay = now - lastPacket;

            if (sent) {
                if (delay > 40L && delay < 100L) {
                    increaseBufferBy(0.25);

                    if (buffer > 0.75) {
                        flag();
                    }
                } else {
                    decreaseBufferBy(0.025);
                }

                sent = false;
            }

            this.lastFlying = now;
        } else if (event.getPacketId() == PacketType.Client.ARM_ANIMATION) {
            final long now = System.currentTimeMillis();
            final long delay = now - lastFlying;

            if (delay < 10L) {
                lastPacket = now;
                sent = true;
            } else {
                decreaseBufferBy(0.025);
            }
        }
    }
}
