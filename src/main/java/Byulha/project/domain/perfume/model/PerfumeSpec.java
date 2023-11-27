package Byulha.project.domain.perfume.model;

import Byulha.project.domain.perfume.model.entity.Perfume;
import org.springframework.data.jpa.domain.Specification;

public class PerfumeSpec {

    public static Specification<Perfume> withForGender(String forGender) {
        if (forGender == null || forGender.equals("null")) {
            return Specification.where(null);
        }

        return (root, query, builder) ->
                builder.equal(root.get("forGender"), ForGender.valueOf(forGender));
    }

    public static Specification<Perfume> withSillage(String sillage) {
        if (sillage == null || sillage.equals("null")) {
            return Specification.where(null);
        }

        return (root, query, builder) ->
                builder.equal(root.get("sillage"), Sillage.valueOf(sillage));
    }

    public static Specification<Perfume> withPriceValue(String priceValue) {
        if (priceValue == null || priceValue.equals("null")) {
            return Specification.where(null);
        }

        return (root, query, builder) ->
                builder.equal(root.get("priceValue"), PriceValue.valueOf(priceValue));
    }
}