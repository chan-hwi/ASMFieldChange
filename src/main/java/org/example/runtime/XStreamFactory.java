package org.example.runtime;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import org.example.runtime.converters.MapToHashConverter;
import org.example.runtime.converters.MapToHistoryConverter;

import java.util.Map;

public class XStreamFactory {
    public static enum XStreamType {
        HISTORY,
        HASH
    }
    static Converter historyConverter = new MapToHistoryConverter();
    static Converter hashConverter = new MapToHashConverter();

    public XStream getXStream(XStreamType type) {
        XStream xstream = new XStream();
        if (type.equals(XStreamType.HISTORY)) {
            xstream.alias("history", Map.class);
            xstream.registerConverter(historyConverter);
            return xstream;
        } else if (type.equals(XStreamType.HASH)) {
            xstream.alias("hash", Map.class);
            xstream.registerConverter(hashConverter);
            return xstream;
        }
        return xstream;
    }
}
