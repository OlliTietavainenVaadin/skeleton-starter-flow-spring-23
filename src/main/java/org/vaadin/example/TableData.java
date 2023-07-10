package org.vaadin.example;

import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;

public class TableData {
    public static int SIZE = 50;

    public static class Generator {
        public static final String[] KEYS;
        private static final String[] CHARS;
        private static final String[] SEGS;
        private static final Map<String, Function<Integer, String>> COLUMNS;

        public static final String[] STRINGS = {};

        static {
            CHARS = IntStream.range('A', 'Z' + 1)
                    .collect(LinkedList<String>::new, (list, i) -> list.add(String.valueOf((char) i)), List::addAll)
                    .toArray(STRINGS);
            KEYS = new Random(42).ints(SIZE)
                    .collect(
                            LinkedList<String>::new,
                            (list, i) -> list.add(generateKey(3, i)),
                            List::addAll)
                    .toArray(STRINGS);

            String[] con = new String[]{"k", "L", "M", "P", "T", "S", "G", "H", "Z", "R", ""};
            String[] voc = new String[]{"a", "e", "i", "o", "u"};
            List<String> r = new LinkedList<>();
            for (String c : con) {
                for (String v : voc) {
                    r.add(c + v);
                }
            }
            SEGS = r.toArray(STRINGS);

            Map<String, Function<Integer, String>> model = new HashMap<>(SIZE);
            for (String k : KEYS) {
                model.put(k, (k.hashCode() & 1) == 1 ? Generator::wordColumn : Generator::integerColumn);
            }
            COLUMNS = Collections.unmodifiableMap(model);
        }

        private static String generateKey(int length, int seed) {
            return new Random(seed).ints(length)
                    .map(Math::abs)
                    .map(i -> i % CHARS.length)
                    .collect(
                            StringBuilder::new,
                            (sb, i) -> sb.append(CHARS[i]),
                            StringBuilder::append)
                    .toString();
        }

        private static String integerColumn(int i) {
            return Integer.toString(Math.abs(i) % 10000);
        }

        private static String wordColumn(int seed) {
            return new Random(seed).ints(5)
                    .map(Math::abs)
                    .map(i -> i % SEGS.length)
                    .collect(
                            StringBuilder::new,
                            (sb, i) -> sb.append(SEGS[i]),
                            StringBuilder::append)
                    .toString()
                    .toLowerCase();
        }
    }

    private final Map<String, String> data = new HashMap<>(SIZE);

    public TableData(int seed) {
        Random r = new Random(seed);
        for (String k : Generator.KEYS) {
            int i = r.nextInt();
            data.put(k, Generator.COLUMNS.get(k).apply(i));
        }
    }

    public String get(String col) {
        return data.getOrDefault(col, "null");
    }
}
