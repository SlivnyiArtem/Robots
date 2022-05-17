package log;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Что починить:
 * 1. Этот класс порождает утечку ресурсов (связанные слушатели оказываются
 * удерживаемыми в памяти)
 * 2. Этот класс хранит активные сообщения лога, но в такой реализации он
 * их лишь накапливает. Надо же, чтобы количество сообщений в логе было ограничено
 * величиной m_iQueueLength (т.е. реально нужна очередь сообщений
 * ограниченного размера)
 */
public class LogWindowSource {
    public final LinkedList<LogEntry> m_messages;
    public final ArrayList<LogChangeListener> m_listeners;
    private volatile LogChangeListener[] m_activeListeners;
    private final int m_iQueueLength = 5;


    public LogWindowSource(int i) {
        m_messages = new LinkedList<>();
        m_listeners = new ArrayList<>();
    }

    public LogWindowSource(LinkedList<LogEntry> messages, ArrayList<LogChangeListener> listeners) {
        m_messages = messages;
        m_listeners = listeners;
    }

    public void registerListener(LogChangeListener listener) {
        synchronized (m_listeners) {
            m_listeners.add(listener);
            m_activeListeners = null;
        }
    }

    public void unregisterListener(LogChangeListener listener) {
        synchronized (m_listeners) {
            m_listeners.remove(listener);
            m_activeListeners = null;
        }
    }

    public void append(LogLevel logLevel, String strMessage) {
        LogEntry entry = new LogEntry(logLevel, strMessage);

        synchronized (m_listeners) {
            m_messages.add(entry);
            if (m_messages.size() > m_iQueueLength) {
                m_messages.removeFirst();
            }
        }

        if (m_activeListeners == null) {
            synchronized (m_listeners) {
                if (m_activeListeners == null) {
                    this.m_activeListeners =
                            m_listeners.toArray(new LogChangeListener[0]); // аргумент - это тип финального массива
                }
            }
        }
        for (LogChangeListener listener : m_activeListeners) {
            listener.onLogChanged();
        }
    }

    public int size() {
        return m_messages.size();
    }

    public Iterable<LogEntry> range(int startFrom, int count) {
        if (startFrom < 0 || startFrom >= m_messages.size()) {
            return Collections.emptyList();
        }
        int indexTo = Math.min(startFrom + count, m_messages.size());
        return m_messages.subList(startFrom, indexTo);
    }

    public Iterable<LogEntry> all() {
        return m_messages;
    }
}