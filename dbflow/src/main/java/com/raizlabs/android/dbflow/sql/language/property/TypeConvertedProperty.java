package com.raizlabs.android.dbflow.sql.language.property;

import com.raizlabs.android.dbflow.converter.TypeConverter;
import com.raizlabs.android.dbflow.sql.language.Condition;
import com.raizlabs.android.dbflow.sql.language.NameAlias;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import static com.raizlabs.android.dbflow.sql.language.Condition.column;

/**
 * Description: Provides convenience methods for {@link TypeConverter} when constructing queries.
 *
 * @author Andrew Grosner (fuzz)
 */

public class TypeConvertedProperty<T, V> extends Property<V> {

    /**
     * Generated by the compiler, looks up the type converter based on {@link ModelAdapter} when needed.
     * This is so we can properly retrieve the type converter at any time.
     */
    public interface TypeConverterGetter {

        TypeConverter getTypeConverter(Class<?> modelClass);
    }

    private TypeConvertedProperty<V, T> databaseProperty;

    private boolean convertToDB;

    private final TypeConverterGetter getter;

    public TypeConvertedProperty(Class<?> table, NameAlias nameAlias,
                                 boolean convertToDB,
                                 TypeConverterGetter getter) {
        super(table, nameAlias);
        this.convertToDB = convertToDB;
        this.getter = getter;
    }

    public TypeConvertedProperty(Class<?> table, String columnName,
                                 boolean convertToDB,
                                 TypeConverterGetter getter) {
        super(table, columnName);
        this.convertToDB = convertToDB;
        this.getter = getter;
    }

    @Override
    protected Condition getCondition() {
        return column(getNameAlias(), getter.getTypeConverter(table), convertToDB);
    }

    /**
     * @return A new {@link Property} that corresponds to the inverted type of the {@link TypeConvertedProperty}.
     * Provides a convenience for supplying type converted methods within the DataClass of the {@link TypeConverter}
     */
    public Property<T> invertProperty() {
        if (databaseProperty == null) {
            databaseProperty = new TypeConvertedProperty<>(table, nameAlias,
                    !convertToDB, new TypeConverterGetter() {
                @Override
                public TypeConverter getTypeConverter(Class<?> modelClass) {
                    return getter.getTypeConverter(modelClass);
                }
            });
        }
        return databaseProperty;
    }

}
