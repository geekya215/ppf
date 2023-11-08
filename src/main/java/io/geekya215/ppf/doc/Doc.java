package io.geekya215.ppf.doc;

public sealed interface Doc permits Break, Cons, Group, Nest, Nil, Text {
}
