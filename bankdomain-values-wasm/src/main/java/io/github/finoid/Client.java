package io.github.finoid;

import org.teavm.jso.dom.html.HTMLDocument;

public class Client {
    public static void main(String[] args) {
        var document = HTMLDocument.current();

        var div = document.createElement("div");
        div.appendChild(document.createTextNode("TeaVM generated element"));

        // Input field
        // True/false and information about the bank

        document.getBody().appendChild(div);
    }
}
