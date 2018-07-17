package Logger;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

import Logic.Game;

public class ObjectFilter implements Filter {

    private Object filtered;

    public ObjectFilter(Object toFilter) {
        filtered = toFilter;
    }

    @Override
    public boolean isLoggable(LogRecord rec) {
        if (rec.getParameters() != null) {
            Object temp = rec.getParameters()[0];
            return filtered == temp || filtered == Game.getInstance();
        }
        return false;
    }

}
