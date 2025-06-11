package io.github.finoid;

import io.github.finoid.bank.domain.BankAccountNumber;
import io.github.finoid.bank.domain.BankAndType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.teavm.interop.Export;
import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLElement;
import org.teavm.jso.dom.html.HTMLInputElement;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Client {
    public static void main(String[] args) {
        final HTMLDocument document = Window.current().getDocument();

        final HTMLElement button = document.getElementById("submit");

        if (button != null) {
            button.addEventListener("click", evt -> {
                final HTMLInputElement input = (HTMLInputElement) document.getElementById("name");
                final String value = input.getValue();
                final String result = process(value);
                final HTMLElement output = document.getElementById("output");
                output.setInnerHTML("Result: " + result);
            });
        }
    }

    @Export(name = "process")
    public static String process(final String input) {
        final boolean isValid = BankAccountNumber.isValid(input);

        if (isValid) {
            final BankAccountNumber bankAccountNumber = BankAccountNumber.ofString(input);

            final BankAndType bankAndType = bankAccountNumber.getBankAndType();

            return String.format("Input %s is valid. Bank: %s. Type: %s", input, bankAndType.getBank().getName(), bankAndType.getBankType().typesAsString());
        }

        return String.format("Input %s is not valid", input);
    }
}
