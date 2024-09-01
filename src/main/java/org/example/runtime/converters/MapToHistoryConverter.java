package org.example.runtime.converters;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapToHistoryConverter implements Converter {
    @Override
    public boolean canConvert(Class type) {
        return Map.class.isAssignableFrom(type);
    }

    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        Map<String, List<Object>> map = (Map<String, List<Object>>) source;

        for (Map.Entry<String, List<Object>> entry : map.entrySet()) {
            writer.startNode("field");
            writer.addAttribute("id", entry.getKey());

            context.convertAnother(entry.getValue());

            writer.endNode(); // end field
        }
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        Map<String, List<Object>> map = new HashMap<>();

        reader.moveDown(); // move to <history>
        while (reader.hasMoreChildren()) {
            reader.moveDown(); // move to <field>
            String key = reader.getAttribute("id");

            List<Object> list = (List<Object>) context.convertAnother(null, List.class);

            map.put(key, list);
            reader.moveUp(); // move up from <field>
        }
        reader.moveUp(); // move up from <history>

        return map;
    }
}