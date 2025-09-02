package com.aurorionsmp.client;

import com.aurorionsmp.Config;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChatBalloonManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(ChatBalloonManager.class);
	private static final Map<UUID, List<BalloonData>> playerBalloons = new ConcurrentHashMap<>();
	private static final long BALLOON_DURATION = 5000; // 5 segundos em milissegundos

	public static void addBalloon(Player player, String message, boolean isDistant) {
		try {
			if (player == null || message == null || message.trim().isEmpty()) return;

			UUID playerId = player.getUUID();
			LOGGER.info("Adicionando balão para {}: '{}' (distante: {})", player.getName().getString(), message, isDistant);

			// Obter ou criar lista de balões
			List<BalloonData> balloons = playerBalloons.computeIfAbsent(playerId, k -> Collections.synchronizedList(new ArrayList<>()));

			// Adicionar novo balão
			balloons.add(new BalloonData(message, isDistant));

			// Limitar número máximo de balões
			while (balloons.size() > Config.maxBalloons) {
				balloons.remove(0); // Remove o mais antigo
			}

		} catch (Exception e) {
			LOGGER.error("Erro ao adicionar balão: {}", e.getMessage());
		}
	}

	public static List<String> getBalloonMessages(UUID playerId) {
		try {
			List<BalloonData> balloons = playerBalloons.get(playerId);
			if (balloons == null) return new ArrayList<>();

			// Remover balões expirados
			balloons.removeIf(balloon -> balloon.isExpired(BALLOON_DURATION));

			// Converter para lista de strings
			List<String> messages = new ArrayList<>();
			for (BalloonData balloon : balloons) {
				messages.add(balloon.getMessage());
			}

			return messages;
		} catch (Exception e) {
			LOGGER.error("Erro ao obter mensagens do balão: {}", e.getMessage());
			return new ArrayList<>();
		}
	}

	public static boolean isDistantMessage(UUID playerId, int index) {
		try {
			List<BalloonData> balloons = playerBalloons.get(playerId);
			if (balloons == null || index >= balloons.size() || index < 0) return false;

			// Remover expirados primeiro
			balloons.removeIf(balloon -> balloon.isExpired(BALLOON_DURATION));

			if (index >= balloons.size()) return false;

			return balloons.get(index).isDistant();
		} catch (Exception e) {
			LOGGER.error("Erro ao verificar se mensagem é distante: {}", e.getMessage());
			return false;
		}
	}

	public static void tick() {
		try {
			// Limpar balões expirados de todos os jogadores
			for (Map.Entry<UUID, List<BalloonData>> entry : playerBalloons.entrySet()) {
				List<BalloonData> balloons = entry.getValue();
				balloons.removeIf(balloon -> balloon.isExpired(BALLOON_DURATION));

				// Remover jogadores sem balões
				if (balloons.isEmpty()) {
					playerBalloons.remove(entry.getKey());
				}
			}
		} catch (Exception e) {
			LOGGER.error("Erro no tick de limpeza: {}", e.getMessage());
		}
	}

	public static void clearAll() {
		try {
			playerBalloons.clear();
			LOGGER.info("Todos os balões foram limpos");
		} catch (Exception e) {
			LOGGER.error("Erro ao limpar balões: {}", e.getMessage());
		}
	}

	public static void clearPlayer(UUID playerId) {
		try {
			playerBalloons.remove(playerId);
		} catch (Exception e) {
			LOGGER.error("Erro ao limpar balões do jogador: {}", e.getMessage());
		}
	}
}