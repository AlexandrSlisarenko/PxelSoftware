package ru.slisarenko.pxelsoftware.db.querydsl;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QPredicates {

    private final List<Predicate> predicates = new ArrayList<>();

    public static QPredicates builder() {
        return new QPredicates();
    }

    public <T> QPredicates and(T object, Function<T, Predicate> function) {
        if (object != null) {
            if(object.getClass().getName().equals("java.lang.String")) {
                if(!object.toString().equals(" ")) {
                    predicates.add(function.apply(object));
                }
            } else {
                predicates.add(function.apply(object));
            }
        }

        return this;
    }


    public Predicate build(){
        return ExpressionUtils.allOf(predicates);
    }
}
