package io.github.finoid;

import io.github.finoid.bank.domain.BankAccountFormatter;
import io.github.finoid.bank.domain.BankAccountNumber;
import io.github.finoid.bank.domain.BankAndType;
import io.github.finoid.bank.domain.BankType;
import org.graalvm.webimage.api.JS;
import org.graalvm.webimage.api.JSObject;
import org.graalvm.webimage.api.JSString;

public class WebMain {
    public static final JSObject COMPILE_BUTTON = getElementById("submit");
    public static final JSObject OUTPUT = getElementById("output");
    public static final JSObject INPUT = getElementById("name");

    public static void main(String[] args) {
        addEventListener(COMPILE_BUTTON, "click", e -> compileCallback());
    }

    private static void compileCallback() {
        runAsync(() -> {
            String source = ((JSString) INPUT.get("value")).asString();
            clearChildren(OUTPUT);
            appendChild(OUTPUT, buildResult(source));
        });
    }

    private static JSObject buildResult(final String input) {
        if (!BankAccountNumber.isValid(input)) {
            return buildInvalidResult(input);
        }

        return buildValidResult(input);
    }

    private static JSObject buildInvalidResult(final String input) {
        final JSObject result = createElement("div");
        setAttribute(result, "class", "result result--invalid");

        final JSObject status = createElement("div");
        setAttribute(status, "class", "result__status");
        status.set("textContent", "Invalid");
        appendChild(result, status);

        final JSObject message = createElement("p");
        setAttribute(message, "class", "result__message");
        appendChild(message, createTextNode("The input "));
        final JSObject strong = createElement("strong");
        strong.set("textContent", input);
        appendChild(message, strong);
        appendChild(message, createTextNode(" is not a valid Swedish bank account number."));
        appendChild(result, message);

        return result;
    }

    private static JSObject buildValidResult(final String input) {
        final BankAccountNumber bankAccountNumber = BankAccountNumber.ofString(input);
        final BankAndType bankAndType = bankAccountNumber.getBankAndType();
        final BankType bankType = bankAndType.getBankType();
        final String formatted = bankAccountNumber.toFormatted(BankAccountFormatter.Format.PRETTY);

        final JSObject result = createElement("div");
        setAttribute(result, "class", "result result--valid");

        final JSObject status = createElement("div");
        setAttribute(status, "class", "result__status");
        status.set("textContent", "Valid");
        appendChild(result, status);

        final JSObject grid = createElement("div");
        setAttribute(grid, "class", "result__grid");
        appendRow(grid, "Bank", bankAndType.getBank().getName());
        appendRow(grid, "Clearing number", bankAccountNumber.getClearingNumber().toFormatted());
        appendRow(grid, "Account number", bankAccountNumber.getAccountNumber().getNumber());
        appendRow(grid, "Formatted", formatted);
        appendRow(grid, "Account type", "Type " + bankType.getType().toDigits());
        appendChild(result, grid);

        return result;
    }

    private static void appendRow(final JSObject parent, final String label, final String value) {
        final JSObject labelEl = createElement("div");
        setAttribute(labelEl, "class", "result__label");
        labelEl.set("textContent", label);
        appendChild(parent, labelEl);

        final JSObject valueEl = createElement("div");
        setAttribute(valueEl, "class", "result__value");
        valueEl.set("textContent", value);
        appendChild(parent, valueEl);
    }

    @JS.Coerce
    @JS("setTimeout(r, 0);")
    private static native void runAsync(final Runnable r);

    @JS.Coerce
    @JS("o.addEventListener(event, (e) => handler(e));")
    static native void addEventListenerImpl(final JSObject o, final String event, final EventHandler handler);

    static void addEventListener(final JSObject o, final String event, final EventHandler handler) {
        addEventListenerImpl(o, event, e -> {
            try {
                handler.handleEvent(e);
            } catch (Throwable t) {
                System.err.println("Uncaught exception in event listener");
                t.printStackTrace();
            }
        });
    }

    @JS.Coerce
    @JS("return document.getElementById(id);")
    public static native JSObject getElementById(final String id);

    @JS.Coerce
    @JS("return document.createElement(tag);")
    public static native JSObject createElement(final String tag);

    @JS.Coerce
    @JS("return document.createTextNode(text);")
    public static native JSObject createTextNode(final String text);

    @JS.Coerce
    @JS("elem.setAttribute(attribute, value);")
    public static native void setAttribute(final JSObject elem, final String attribute, final Object value);

    @JS.Coerce
    @JS("parent.appendChild(child);")
    public static native void appendChild(final JSObject parent, final JSObject child);

    @JS.Coerce
    @JS("elem.innerHTML = '';")
    public static native void clearChildren(final JSObject elem);
}

@FunctionalInterface
interface EventHandler {
    void handleEvent(final JSObject event);
}