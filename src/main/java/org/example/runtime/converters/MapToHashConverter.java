package org.example.runtime.converters;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.HashMap;
import java.util.Map;

public class MapToHashConverter implements Converter {
    @Override
    public boolean canConvert(Class type) {
        return Map.class.isAssignableFrom(type);
    }

    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        Map<String, Integer> map = (Map<String, Integer>) source;

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            writer.startNode("field");
            writer.addAttribute("id", entry.getKey());
            writer.addAttribute("hash", String.valueOf(entry.getValue()));

            writer.endNode(); // end field
        }
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        Map<String, Integer> map = new HashMap<>();

        reader.moveDown(); // move to <history>
        while (reader.hasMoreChildren()) {
            reader.moveDown(); // move to <field>
            String key = reader.getAttribute("id");
            Integer value = Integer.parseInt(reader.getAttribute("hash"));

            map.put(key, value);
            reader.moveUp(); // move up from <field>
        }
        reader.moveUp(); // move up from <history>

        return map;
    }
}