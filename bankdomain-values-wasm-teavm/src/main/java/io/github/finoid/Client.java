package io.github.finoid;

import io.github.finoid.bank.domain.BankAccountFormatter;
import io.github.finoid.bank.domain.BankAccountNumber;
import io.github.finoid.bank.domain.BankAndType;
import io.github.finoid.bank.domain.BankType;
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
                final HTMLElement output = document.getElementById("output");
                output.clear();
                output.appendChild(buildResult(document, input.getValue()));
            });
        }
    }

    @Export(name = "process")
    public static String process(final String input) {
        if (!BankAccountNumber.isValid(input)) {
            return String.format("Input %s is not valid", input);
        }

        final BankAccountNumber bankAccountNumber = BankAccountNumber.ofString(input);
        final BankAndType bankAndType = bankAccountNumber.getBankAndType();

        return String.format("Input %s is valid. Bank: %s. Type: %s",
            input, bankAndType.getBank().getName(), bankAndType.getBankType().typesAsString());
    }

    private static HTMLElement buildResult(final HTMLDocument document, final String input) {
        if (!BankAccountNumber.isValid(input)) {
            return buildInvalidResult(document, input);
        }

        return buildValidResult(document, input);
    }

    private static HTMLElement buildInvalidResult(final HTMLDocument document, final String input) {
        final HTMLElement result = document.createElement("div");
        result.setClassName("result result--invalid");

        result.withChild("div", status -> {
            status.setClassName("result__status");
            status.withText("Invalid");
        });

        result.withChild("p", message -> {
            message.setClassName("result__message");
            message.appendChild(document.createTextNode("The input "));
            message.withChild("strong", strong -> strong.withText(input));
            message.appendChild(document.createTextNode(" is not a valid Swedish bank account number."));
        });

        return result;
    }

    private static HTMLElement buildValidResult(final HTMLDocument document, final String input) {
        final BankAccountNumber bankAccountNumber = BankAccountNumber.ofString(input);
        final BankAndType bankAndType = bankAccountNumber.getBankAndType();
        final BankType bankType = bankAndType.getBankType();
        final String formatted = bankAccountNumber.toFormatted(BankAccountFormatter.Format.PRETTY);

        final HTMLElement result = document.createElement("div");
        result.setClassName("result result--valid");

        result.withChild("div", status -> {
            status.setClassName("result__status");
            status.withText("Valid");
        });

        result.withChild("div", grid -> {
            grid.setClassName("result__grid");
            appendRow(grid, "Bank", bankAndType.getBank().getName());
            appendRow(grid, "Clearing number", bankAccountNumber.getClearingNumber().toFormatted());
            appendRow(grid, "Account number", bankAccountNumber.getAccountNumber().getNumber());
            appendRow(grid, "Formatted", formatted);
            appendRow(grid, "Account type", "Type " + bankType.getType().toDigits());
        });

        return result;
    }

    private static void appendRow(final HTMLElement parent, final String label, final String value) {
        parent.withChild("div", labelEl -> {
            labelEl.setClassName("result__label");
            labelEl.withText(label);
        });
        parent.withChild("div", valueEl -> {
            valueEl.setClassName("result__value");
            valueEl.withText(value);
        });
    }
}