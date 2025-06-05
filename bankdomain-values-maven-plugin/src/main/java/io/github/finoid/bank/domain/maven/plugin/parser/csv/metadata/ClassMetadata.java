package io.github.finoid.bank.domain.maven.plugin.parser.csv.metadata;

import lombok.Value;

import java.lang.annotation.Annotation;
import java.util.List;

@Value(staticConstructor = "ofFields")
public class ClassMetadata {
    FieldMetadataCollection fields;

    public List<FieldMetadata> getFieldsAnnotatedWith(final Class<? extends Annotation> annotationClazz) {
        return fields.getFieldsAnnotatedWith(annotationClazz);
    }
}
