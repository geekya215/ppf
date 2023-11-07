package io.geekya215.ppf.doc;

public sealed interface Doc permits Concat, Line, Nest, Nil, Text {
}
