package io.geekya215.ppf;

import io.geekya215.ppf.doc.*;
import io.geekya215.ppf.mode.Br;
import io.geekya215.ppf.mode.Fl;
import io.geekya215.ppf.mode.Mode;

import java.util.ArrayList;
import java.util.List;

public final class PPF {
    public static boolean fits(int w, List<IDoc> ids) {
        if (w < 0) {
            return false;
        }

        if (ids.isEmpty()) {
            return true;
        }

        IDoc x = ids.get(0);
        List<IDoc> z = ids.subList(1, ids.size());
        return switch (x) {
            case DDoc(_, _, Nil _) -> fits(w, z);
            case DDoc(_, _, Text(String s)) -> fits(w - s.length(), z);
            case DDoc(_, Fl _, Break(String s)) -> fits(w - s.length(), z);
            case DDoc(_, Br _, Break(_)) -> true;
            case DDoc(int i, Mode m, Nest(int j, Doc a)) -> fits(w, addFirst(new DDoc(i + j, m, a), z));
            case DDoc(int i, Mode m, Cons(Doc a, Doc b)) ->
                    fits(w, addFirst(new DDoc(i, m, a), addFirst(new DDoc(i, m, b), z)));
            case DDoc(int i, _, Group(Doc a)) -> fits(w, addFirst(new DDoc(i, new Fl(), a), z));
        };
    }

    public static String format(int w, int k, List<IDoc> ids) {
        if (ids.isEmpty()) {
            return "";
        }

        IDoc x = ids.get(0);
        List<IDoc> z = ids.subList(1, ids.size());
        return switch (x) {
            case DDoc(_, _, Nil _) -> format(w, k, z);
            case DDoc(_, _, Text(String s)) -> s + format(w, k + s.length(), z);
            case DDoc(_, Fl _, Break(String s)) -> s + format(w, k + s.length(), z);
            case DDoc(int i, Br _, Break(_)) -> "\n" + " ".repeat(i) + format(w, i, z);
            case DDoc(int i, Mode m, Nest(int j, Doc a)) -> format(w, k, addFirst(new DDoc(i + j, m, a), z));
            case DDoc(int i, Mode m, Cons(Doc a, Doc b)) ->
                    format(w, k, addFirst(new DDoc(i, m, a), addFirst(new DDoc(i, m, b), z)));
            case DDoc(int i, _, Group(Doc a)) -> format(w, k,
                    addFirst(new DDoc(i, fits(w - k, addFirst(new DDoc(i, new Fl(), a), z)) ? new Fl() : new Br(), a), z));
        };
    }

    public static String pretty(int w, Doc d) {
        return format(w, 0, new ArrayList<>() {{
            add(new DDoc(0, new Fl(), d));
        }});
    }

    public static <T> List<T> addFirst(T x, List<T> xs) {
        ArrayList<T> xxs = new ArrayList<>(xs);
        xxs.addFirst(x);
        return xxs;
    }

    public static Doc concat(Doc l, Doc r) {
        return new Cons(l, r);
    }
}
