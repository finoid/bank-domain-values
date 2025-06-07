package io.github.finoid.bank.domain.maven.plugin.codegen;

import com.palantir.javapoet.ArrayTypeName;
import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.CodeBlock;
import com.palantir.javapoet.JavaFile;
import com.palantir.javapoet.MethodSpec;
import com.palantir.javapoet.ParameterSpec;
import com.palantir.javapoet.ParameterizedTypeName;
import com.palantir.javapoet.TypeName;
import com.palantir.javapoet.TypeSpec;
import io.github.finoid.bank.domain.maven.plugin.Actor;
import io.github.finoid.bank.domain.maven.plugin.ActorAccountContext;
import io.github.finoid.bank.domain.maven.plugin.Type;
import lombok.NoArgsConstructor;
import org.codehaus.plexus.component.annotations.Component;

import javax.inject.Singleton;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Generates a Java {@code enum} representing supported banks, based on provided {@link ActorAccountContext} definitions grouped by {@link Actor}.
 * <p>
 * The generated enum includes metadata for clearing number range mapping and BankType associations.
 * <p>
 * Output is written to a generated source directory using {@link com.palantir.javapoet.JavaFile}.
 */
@Singleton
@NoArgsConstructor
@Component(role = BankEnumGenerator.class)
public class BankEnumGenerator {
    private static final ClassName BANK_ACCOUNT_TYPE = ClassName.get("io.github.finoid.bank.domain", "BankAccountType");
    private static final ClassName BANK_ACCOUNT_SUB_TYPE = ClassName.get("io.github.finoid.bank.domain", "BankAccountSubType");
    private static final ClassName BANK_TYPE = ClassName.get("io.github.finoid.bank.domain", "BankType");
    private static final ClassName INT_RANGE = ClassName.get("io.github.finoid.bank.domain", "IntRange");
    private static final ClassName INT_RANGES = ClassName.get("io.github.finoid.bank.domain", "IntRanges");

    /**
     * Generates the {@code Bank} enum class source file, based on the provided map of actors and their bank contexts.
     *
     * @param actors     a map from {@link Actor} to the corresponding list of {@link ActorAccountContext}
     * @param outputPath the file system path to which the generated file should be written
     * @throws IOException if writing the file fails
     */
    public void generate(final Map<Actor, List<ActorAccountContext>> actors, final Path outputPath) throws IOException {
        final String targetPackage = "io.github.finoid.generated.bank.domain";

        final TypeSpec.Builder enumBuilder = enumBuilder();

        enumBuilder
            .addField(String.class, "name", Modifier.PRIVATE, Modifier.FINAL)
            .addField(ParameterizedTypeName.get(ClassName.get(Set.class), ClassName.get("", "BankType")), "types", Modifier.PRIVATE, Modifier.FINAL)
            .addMethods(List.of(constructorMethodSpec(), getNameMethodSpec(), getTypesMethodSpec(), isWithinRangeMethodSpec(),
                ofClearingNumberMethodSpec(targetPackage)));

        // Enum constants
        actors.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEachOrdered(entry -> enumBuilder.addEnumConstant(entry.getKey().getNormalizedName(), enumConstantTypeSpec(entry)));

        final JavaFile javaFile = JavaFile.builder(targetPackage, enumBuilder.build())
            .indent("    ") // four spaces
            .build();

        javaFile.writeTo(outputPath);
    }

    private static TypeSpec enumConstantTypeSpec(final Map.Entry<Actor, List<ActorAccountContext>> entry) {
        final Map<BankEntryKey, List<ActorAccountContext>> byType = entry.getValue().stream()
            .collect(Collectors.groupingBy(BankEntryKey::ofContext, LinkedHashMap::new, Collectors.toList()));

        final List<CodeBlock> bankTypeCodeBlocks = byType.entrySet().stream()
            .map(it -> bankTypeCodeBlock(it.getKey(), it.getValue()))
            .toList();

        final String label = entry.getKey().getName();

        // TODO (nw) särhantera banker med kortare nummerserier, typ HANDELSBANK?

        final CodeBlock.Builder enumConstantCodeBlockBuilder = CodeBlock.builder()
            .add("$S, ", label);

        for (int i = 0; i < bankTypeCodeBlocks.size(); i++) {
            enumConstantCodeBlockBuilder.add("\n$>$L$<", bankTypeCodeBlocks.get(i));
            if (i < bankTypeCodeBlocks.size() - 1) {
                enumConstantCodeBlockBuilder.add(",");
            } else {
                enumConstantCodeBlockBuilder.add("\n");
            }
        }

        return TypeSpec.anonymousClassBuilder(enumConstantCodeBlockBuilder.build())
            .addJavadoc("Bank {@code $L} - supports $L.\n", label, enumConstantJavaDocDescription(byType))
            .build();
    }

    @SuppressWarnings("EnumOrdinal")
    private static String enumConstantJavaDocDescription(final Map<BankEntryKey, List<ActorAccountContext>> byType) {
        return byType.entrySet().stream()
            .map(entry -> {
                String ranges = entry.getValue().stream()
                    .map(ctx -> String.format("%d-%d", ctx.getAccountNumber().getStart(), ctx.getAccountNumber().getEnd()))
                    .collect(Collectors.joining(","));

                return String.format("%d:%d accounts in range %s",
                    entry.getKey().primaryType.ordinal() + 1,
                    entry.getKey().secondaryType.ordinal() + 1,
                    ranges);
            })
            .collect(Collectors.joining(" and "));
    }

    private static CodeBlock bankTypeCodeBlock(final BankEntryKey key, final List<ActorAccountContext> values) {
        final List<CodeBlock> rangeBlocks = values.stream()
            .map(it -> CodeBlock.of("$T.of($L, $L)", INT_RANGE, it.getAccountNumber().getStart(), it.getAccountNumber().getEnd()))
            .toList();

        final CodeBlock rangeList = CodeBlock.join(rangeBlocks, ", ");
        final CodeBlock typeEnumBlock = CodeBlock.of("$T.$L", BANK_ACCOUNT_TYPE, key.primaryType());
        final CodeBlock subTypeEnumBlock = CodeBlock.of("$T.$L", BANK_ACCOUNT_SUB_TYPE, key.secondaryType());

        return CodeBlock.builder()
            .add("$T.ofTypesAndRanges($L, $L, $T.of($L))", BANK_TYPE, typeEnumBlock, subTypeEnumBlock, INT_RANGES, rangeList).build();
    }

    private static TypeSpec.Builder enumBuilder() {
        return TypeSpec.enumBuilder("Bank")
            .addModifiers(Modifier.PUBLIC)
            .addJavadoc("Enum representing supported banks, along with their account length rules\n"
                        + "and associated {@link BankType} definitions for clearing number range mapping.\n"
                        + "<p>\n"
                        + "Keep in sync with <a href=\"https://www.bankinfrastruktur.se/framtidens-betalningsinfrastruktur/iban-och-svenskt-nationellt-kontonummer\">...</a>\n");
    }

    private static MethodSpec constructorMethodSpec() {
        return MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PRIVATE)
            .varargs(true)
            .addParameter(ParameterSpec.builder(String.class, "name").build())
            .addParameter(ParameterSpec.builder(ArrayTypeName.of(ClassName.get("io.github.finoid.bank.domain", "BankType")), "types")
                .addModifiers(Modifier.FINAL)
                .build())
            .addStatement("this.name = name")
            .addStatement("this.types = Set.of(types)")
            .build();
    }

    private static MethodSpec getNameMethodSpec() {
        return MethodSpec.methodBuilder("getName")
            .addModifiers(Modifier.PUBLIC)
            .returns(String.class)
            .addStatement("return name")
            .build();
    }

    private static MethodSpec getTypesMethodSpec() {
        return MethodSpec.methodBuilder("getTypes")
            .addModifiers(Modifier.PUBLIC)
            .returns(ParameterizedTypeName.get(
                ClassName.get(Set.class),
                BANK_TYPE
            ))
            .addStatement("return types")
            .build();
    }

    private static MethodSpec isWithinRangeMethodSpec() {
        return MethodSpec.methodBuilder("isWithinRange")
            .addModifiers(Modifier.PUBLIC)
            .returns(TypeName.BOOLEAN)
            .addParameter(TypeName.INT, "clearingNumber")
            .addJavadoc("Checks if the given clearing number falls within any of this bank’s {@link BankType}s.\n"
                        + " @param clearingNumber the clearing number to check\n"
                        + " @return {@code true} if within range; {@code false} otherwise\n")
            .addStatement("return types.stream().anyMatch(type -> type.isWithinRange(clearingNumber))")
            .build();
    }

    private static MethodSpec ofClearingNumberMethodSpec(final String targetPackage) {
        return MethodSpec.methodBuilder("ofClearingNumber")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(ParameterizedTypeName.get(
                ClassName.get(Optional.class),
                ClassName.get(targetPackage, "Bank")
            ))
            .addParameter(
                ClassName.get("io.github.finoid.bank.domain", "ClearingNumber"),
                "clearingNumber"
            )
            .addJavadoc("Resolves a {@link Bank} instance based on the provided {@link ClearingNumber}.\n"
                        + " @param clearingNumber the clearing number to match\n"
                        + " @return an {@link Optional} containing the matching bank, or empty if none match\n")
            .addStatement("return java.util.Arrays.stream(values())\n"
                          + "    .filter(bank -> bank.isWithinRange(clearingNumber.getClearingNumber()))\n"
                          + "    .findFirst()")
            .build();
    }

    record BankEntryKey(Type primaryType, Type secondaryType) {
        public static BankEntryKey ofContext(final ActorAccountContext context) {
            return new BankEntryKey(context.getAccountType().primary(), context.getAccountType().secondary());
        }
    }
}
