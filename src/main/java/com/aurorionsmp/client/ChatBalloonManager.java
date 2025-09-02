package com.aurorionsmp.client;

import com.aurorionsmp.Config;
import com.aurorionsmp.util.HistoricalData;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class ChatBalloonManager {
	private static final Map<UUID, HistoricalData<String>> playerBalloons = new ConcurrentHashMap<>();
	private static final Map<UUID, Map<Integer, Boolean>> distantMessages = new ConcurrentHashMap<>();
	private static final Map<UUID, ConcurrentLinkedDeque<Supplier<Boolean>>> queuedTickEvents = new ConcurrentHashMap<>();

	public static void addBalloon(Player player, String message, boolean isDistant) {
		if (message == null || message.trim().isEmpty()) return;

		UUID playerId = player.getUUID();

		// Criar ou obter histórico de mensagens
		HistoricalData<String> messages = playerBalloons.computeIfAbsent(playerId,
				k -> new HistoricalData<>(message, Config.maxBalloons));

		if (playerBalloons.get(playerId) != messages) {
			messages.add(message);
		}

		// Marcar se a mensagem é distante
		distantMessages.computeIfAbsent(playerId, k -> new ConcurrentHashMap<>())
				.put(messages.size() - 1, isDistant);

		// Configurar remoção automática
		var currentTick = new AtomicInteger(0);
		int timeToRemove = Config.balloonAge * 20; // Converter segundos para ticks

		queuedTickEvents.computeIfAbsent(playerId, k -> new ConcurrentLinkedDeque<>())
				.add(() -> {
					if (currentTick.getAndIncrement() >= timeToRemove) {
						messages.remove(message);
						return true;
					}
					return false;
				});
	}

	public static HistoricalData<String> getBalloonMessages(UUID playerId) {
		return playerBalloons.get(playerId);
	}

	public static boolean isDistantMessage(UUID playerId, int index) {
		Map<Integer, Boolean> playerDistantMessages = distantMessages.get(playerId);
		return playerDistantMessages != null && playerDistantMessages.getOrDefault(index, false);
	}

	public static void tick() {
		// Processar eventos de tick para cada jogador
		for (Map.Entry<UUID, ConcurrentLinkedDeque<Supplier<Boolean>>> entry : queuedTickEvents.entrySet()) {
			var events = entry.getValue();
			events.removeIf(Supplier::get);
		}

		// Limpar entradas vazias
		playerBalloons.entrySet().removeIf(entry -> entry.getValue().isEmpty());
		distantMessages.entrySet().removeIf(entry -> entry.getValue().isEmpty());
		queuedTickEvents.entrySet().removeIf(entry -> entry.getValue().isEmpty());
	}

	public static void clearAll() {
		playerBalloons.clear();
		distantMessages.clear();
		queuedTickEvents.clear();
	}

	public static void clearPlayer(UUID playerId) {
		playerBalloons.remove(playerId);
		distantMessages.remove(playerId);
		queuedTickEvents.remove(playerId);
	}
}