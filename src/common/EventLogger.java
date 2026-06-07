package common;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Simülasyon olaylarını zaman damgalı şekilde kaydeder. Singleton tasarım deseni kullanır.
// Controller log() çağırır. View isteğe bağlı olarak getEntries() ile okuyabilir.
public class EventLogger {

    private static final int         MAX_LOG  = 200;
    private static final EventLogger INSTANCE = new EventLogger();

    private final List<String> entries = new ArrayList<>();

    private EventLogger() {}

    public static EventLogger getInstance() { return INSTANCE; }

    public void log(SimulationEvent event, String detail) {
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String msg  = "[" + time + "] " + event.name();
        if (detail != null && !detail.isEmpty()) msg += " — " + detail;
        entries.add(msg);
        // Liste MAX_LOG sınırına ulaşınca en eski kayıt silinir
        if (entries.size() > MAX_LOG) entries.remove(0);
    }

    public List<String> getEntries() { return Collections.unmodifiableList(entries); }

    public void clear() { entries.clear(); }
}
