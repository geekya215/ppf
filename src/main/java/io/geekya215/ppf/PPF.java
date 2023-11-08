package io.geekya215.ppf;

import io.geekya215.ppf.doc.*;

public final class PPF {
    // -- flattening in-place
    // layout :: Doc -> String
    // layout (Nest n (Nest m x)) = layout (Nest (n + m) x)
    // layout (Nest n (Cons x y)) = layout (Nest n x) ++ layout (Nest n y)
    // layout (Nest n (Break s))  = "\n" ++ (replicate n ' ')
    // layout (Nest n x)          = layout x
    // layout Nil                 = ""
    // layout (Break s)           = "\n"
    // layout (Text t)            = t
    // layout (Cons x y)          = layout x ++ layout y
    // layout (Group d)           = layout d
    public static String layout(Doc d) {
        return switch (d) {
            case Nest(int n, Doc dd) -> switch (dd) {
                case Nest(int m, Doc x) -> layout(new Nest(n + m, x));
                case Cons(Doc x, Doc y) -> layout(new Nest(n, x)) + layout(new Nest(n, y));
                case Break _ -> "\n" + " ".repeat(n);
                default -> layout(dd);
            };
            case Nil _ -> "";
            case Break _ -> "\n";
            case Text(String t) -> t;
            case Cons(Doc x, Doc y) -> layout(x) + layout(y);
            case Group(Doc g) -> layout(g);
        };
    }

    // type SDoc = Doc
    //
    // normalize :: Doc -> SDoc
    // normalize (Nest n Nil)        = Nil
    // normalize (Nest n (Nest m x)) = normalize (Nest (n+m) x)
    // normalize (Nest n (Cons x y)) = normalize (Nest n x) <> normalize (Nest n y)
    // normalize (Nest n (Text s))   = Text s
    // normalize (Nest n (Break s))  = Nest n (Break s)
    // normalize (Nest n (Group x))  = normalize (Nest n x)
    // normalize (Cons x y)          = normalize x <> normalize y
    // normalize (Group x)           = normalize x
    // normalize d                   = d
    public static Doc normalize(Doc d) {
        return switch (d) {
            case Nest(int n, Doc dd) -> switch (dd) {
                case Nil _ -> new Nil();
                case Nest(int m, Doc x) -> normalize(new Nest(n + m, x));
                case Cons(Doc x, Doc y) -> concat(normalize(new Nest(n, x)), normalize(new Nest(n, y)));
                case Text(String s) -> new Text(s);
                case Break(String s) -> new Nest(n, new Break(s));
                case Group(Doc x) -> normalize(new Nest(n, x));
            };
            case Cons(Doc x, Doc y) -> concat(normalize(x), normalize(y));
            case Group(Doc x) -> normalize(x);
            default -> d;
        };
    }

    // layoutSDoc :: SDoc -> String
    // layoutSDoc Nil                = ""
    // layoutSDoc (Nest n (Break s)) = "\n" ++ replicate n ' '
    // layoutSDoc (Text s)           = s
    // layoutSDoc (Cons x y)         = layoutSDoc x ++ layoutSDoc y
    // layoutSDoc (Break s)          = "\n"
    // layoutSDoc d                  = undefined
    public static String layoutDoc(Doc d) {
        return switch (d) {
            case Nil _ -> "";
            case Nest(int n, Break _) -> "\n" + " ".repeat(n);
            case Text(String s) -> s;
            case Cons(Doc x, Doc y) -> layoutDoc(x) + layoutDoc(y);
            case Break _ -> "\n";
            default -> throw new RuntimeException("Unknown doc");
        };
    }

    public static Doc concat(Doc l, Doc r) {
        return new Cons(l, r);
    }
}
