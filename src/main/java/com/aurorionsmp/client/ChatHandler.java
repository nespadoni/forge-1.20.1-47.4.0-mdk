package com.aurorionsmp.client;

import com.aurorionsmp.AurorionSMPMod;
import com.aurorionsmp.Config;
import com.aurorionsmp.network.ChatMessagePacket;
import com.aurorionsmp.network.NetworkHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod.EventBusSubscriber(modid = AurorionSMPMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ChatHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(ChatHandler.class);

	// Evento do servidor - quando alguém envia uma mensagem
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onServerChat(ServerChatEvent event) {
		ServerPlayer sender = event.getPlayer();
		String message = event.getRawText();

		LOGGER.info("Servidor: Chat capturado de {} -> '{}'", sender.getName().getString(), message);

		// Cancelar a mensagem original do chat
		event.setCanceled(true);

		// Enviar pacotes personalizados para todos os jogadores
		for (ServerPlayer player : sender.serverLevel().players()) {
			if (player == sender) continue; // Pular o remetente por enquanto

			double distance = sender.distanceTo(player);
			boolean isDistant = distance > Config.chatRange;

			LOGGER.info("Enviando para {}: distância={}, distante={}",
					player.getName().getString(), distance, isDistant);

			// Enviar pacote para cada jogador
			NetworkHandler.INSTANCE.send(
					PacketDistributor.PLAYER.with(() -> player),
					new ChatMessagePacket(sender.getUUID(), message, isDistant)
			);
		}

		// Também enviar para o próprio remetente (para F5 e terceira pessoa)
		NetworkHandler.INSTANCE.send(
				PacketDistributor.PLAYER.with(() -> sender),
				new ChatMessagePacket(sender.getUUID(), message, false)
		);
	}

	// Evento do cliente - interceptar mensagens que ainda passaram
	@Mod.EventBusSubscriber(modid = AurorionSMPMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
	public static class ClientChatHandler {
		@SubscribeEvent(priority = EventPriority.HIGHEST)
		public static void onClientChat(ClientChatReceivedEvent event) {
			Component message = event.getMessage();
			String messageText = message.getString();

			// Cancelar mensagens de chat de jogadores (formato: <Nome> mensagem)
			if (messageText.matches("^<[^>]+>.*")) {
				LOGGER.info("Cliente: Cancelando mensagem de chat: '{}'", messageText);
				event.setCanceled(true);
			}
		}
	}
}