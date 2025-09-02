package com.aurorionsmp.network;

import com.aurorionsmp.Config;
import com.aurorionsmp.client.ChatBalloonManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class ChatMessagePacket {
	private final UUID senderId;
	private final String message;
	private final boolean isDistant;

	public ChatMessagePacket(UUID senderId, String message, boolean isDistant) {
		this.senderId = senderId;
		this.message = message;
		this.isDistant = isDistant;
	}

	public static void encode(ChatMessagePacket packet, FriendlyByteBuf buf) {
		buf.writeUUID(packet.senderId);
		buf.writeUtf(packet.message);
		buf.writeBoolean(packet.isDistant);
	}

	public static ChatMessagePacket decode(FriendlyByteBuf buf) {
		return new ChatMessagePacket(
				buf.readUUID(),
				buf.readUtf(),
				buf.readBoolean()
		);
	}

	public static void handle(ChatMessagePacket packet, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			// Verificar se estamos no cliente
			if (ctx.get().getDirection().getReceptionSide().isClient()) {
				handleClient(packet);
			}
		});
		ctx.get().setPacketHandled(true);
	}

	private static void handleClient(ChatMessagePacket packet) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level != null) {
			Player sender = mc.level.getPlayerByUUID(packet.senderId);
			if (sender != null) {
				// Verificar se deve mostrar próprios balões
				if (sender == mc.player && !Config.showOwnBalloon) {
					return;
				}

				String displayMessage = packet.isDistant ?
						"distante demais para conseguir ouvir" :
						packet.message;

				ChatBalloonManager.addBalloon(sender, displayMessage, packet.isDistant);
			}
		}
	}
}