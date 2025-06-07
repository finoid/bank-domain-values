@org.jspecify.annotations.NullMarked
module io.github.finoid.bank.domain {
    requires static lombok;
    requires static org.jspecify;

    requires org.apache.commons.lang3;

    exports io.github.finoid.bank.domain;
}