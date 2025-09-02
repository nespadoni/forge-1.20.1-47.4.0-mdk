package com.aurorionsmp.util;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class HistoricalData<T> implements List<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(HistoricalData.class);
	private final int maxHistory;
	private final List<T> history;

	public HistoricalData(T initialValue, int maxHistory) {
		if (maxHistory < 1) throw new IllegalArgumentException("Max History cannot be less than 1");
		this.maxHistory = maxHistory;
		this.history = Collections.synchronizedList(new ArrayList<>());

		if (initialValue != null) {
			this.history.add(initialValue);
		}
	}

	@Override
	public int size() {
		try {
			return history.size();
		} catch (Exception e) {
			LOGGER.error("Erro ao obter tamanho: {}", e.getMessage());
			return 0;
		}
	}

	@Override
	public boolean isEmpty() {
		try {
			return history.isEmpty();
		} catch (Exception e) {
			LOGGER.error("Erro ao verificar se está vazio: {}", e.getMessage());
			return true;
		}
	}

	@Override
	public boolean contains(Object o) {
		try {
			return history.contains(o);
		} catch (Exception e) {
			LOGGER.error("Erro ao verificar se contém: {}", e.getMessage());
			return false;
		}
	}

	@NotNull
	@Override
	public Iterator<T> iterator() {
		return new ArrayList<>(history).iterator(); // Cópia segura
	}

	@NotNull
	@Override
	public Object @NotNull [] toArray() {
		return history.toArray();
	}

	@NotNull
	@Override
	public <T1> T1 @NotNull [] toArray(@NotNull T1 @NotNull [] a) {
		return history.toArray(a);
	}

	@Override
	public boolean add(T value) {
		try {
			if (value == null) return false;

			history.add(value);
			if (history.size() > maxHistory) {
				history.remove(0);
			}
			return true;
		} catch (Exception e) {
			LOGGER.error("Erro ao adicionar valor: {}", e.getMessage());
			return false;
		}
	}

	@Override
	public void add(int index, T value) {
		throw new UnsupportedOperationException("Add with index is not supported.");
	}

	@Override
	public boolean addAll(@NotNull Collection<? extends T> c) {
		boolean modified = false;
		for (T value : c) {
			if (add(value)) {
				modified = true;
			}
		}
		return modified;
	}

	@Override
	public boolean addAll(int index, @NotNull Collection<? extends T> c) {
		throw new UnsupportedOperationException("AddAll with index is not supported.");
	}

	@Override
	public boolean containsAll(@NotNull Collection<?> c) {
		try {
			return new HashSet<>(history).containsAll(c);
		} catch (Exception e) {
			LOGGER.error("Erro ao verificar containsAll: {}", e.getMessage());
			return false;
		}
	}

	@Override
	public boolean remove(Object o) {
		try {
			return history.remove(o);
		} catch (Exception e) {
			LOGGER.error("Erro ao remover objeto: {}", e.getMessage());
			return false;
		}
	}

	@Override
	public T remove(int index) {
		try {
			return history.remove(index);
		} catch (Exception e) {
			LOGGER.error("Erro ao remover por índice: {}", e.getMessage());
			return null;
		}
	}

	@Override
	public boolean removeAll(@NotNull Collection<?> c) {
		try {
			return history.removeAll(c);
		} catch (Exception e) {
			LOGGER.error("Erro ao remover todos: {}", e.getMessage());
			return false;
		}
	}

	@Override
	public boolean retainAll(@NotNull Collection<?> c) {
		try {
			return history.retainAll(c);
		} catch (Exception e) {
			LOGGER.error("Erro ao reter todos: {}", e.getMessage());
			return false;
		}
	}

	@Override
	public void clear() {
		try {
			history.clear();
		} catch (Exception e) {
			LOGGER.error("Erro ao limpar: {}", e.getMessage());
		}
	}

	@Override
	public T get(int index) {
		try {
			if (index < 0 || index >= history.size()) return null;
			return history.get(index);
		} catch (Exception e) {
			LOGGER.error("Erro ao obter por índice: {}", e.getMessage());
			return null;
		}
	}

	@Override
	public T set(int index, T element) {
		try {
			return history.set(index, element);
		} catch (Exception e) {
			LOGGER.error("Erro ao definir por índice: {}", e.getMessage());
			return null;
		}
	}

	@Override
	public int indexOf(Object o) {
		try {
			return history.indexOf(o);
		} catch (Exception e) {
			LOGGER.error("Erro ao obter indexOf: {}", e.getMessage());
			return -1;
		}
	}

	@Override
	public int lastIndexOf(Object o) {
		try {
			return history.lastIndexOf(o);
		} catch (Exception e) {
			LOGGER.error("Erro ao obter lastIndexOf: {}", e.getMessage());
			return -1;
		}
	}

	@NotNull
	@Override
	public ListIterator<T> listIterator() {
		return new ArrayList<>(history).listIterator();
	}

	@NotNull
	@Override
	public ListIterator<T> listIterator(int index) {
		return new ArrayList<>(history).listIterator(index);
	}

	@NotNull
	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		try {
			return new ArrayList<>(history.subList(fromIndex, toIndex));
		} catch (Exception e) {
			LOGGER.error("Erro ao obter sublista: {}", e.getMessage());
			return new ArrayList<>();
		}
	}
}