package rockland.elysiancrest.com.data_service.util;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class FreeTextSearchSpecifications<T> {
    private final List<SearchCriterion> entityCriteria = new ArrayList<>();
    private final List<JoinCriterion> joinCriteria = new ArrayList<>();
    private String searchString;

    public static <T> FreeTextSearchSpecifications<T> builder() {
        return new FreeTextSearchSpecifications<>();
    }

    public FreeTextSearchSpecifications<T> withSearchString(String searchString) {
        this.searchString = searchString;
        return this;
    }

    public FreeTextSearchSpecifications<T> addEntityField(String fieldName, Class<?> fieldType) {
        this.entityCriteria.add(new SearchCriterion(fieldName, fieldType));
        return this;
    }

    public FreeTextSearchSpecifications<T> addJoinField(String joinAlias, String fieldName, Class<?> fieldType) {
        this.joinCriteria.add(new JoinCriterion(joinAlias, fieldName, fieldType));
        return this;
    }

    public Specification<T> build() {
        return (root, query, criteriaBuilder) -> {
            if (searchString == null || searchString.trim().isEmpty()) {
                return criteriaBuilder.conjunction(); // No filtering if search string is empty
            }

            String searchPattern = "%" + searchString.trim() + "%";
            List<Predicate> predicates = new ArrayList<>();

            // Add predicates for fields in the main entity
            for (SearchCriterion criterion : entityCriteria) {
                Predicate predicate = createPredicate(criteriaBuilder, root.get(criterion.getFieldName()), criterion.getFieldType(), searchPattern);
                if (predicate != null) {
                    predicates.add(predicate);
                }
            }

            // Add predicates for fields in joined entities
            for (JoinCriterion criterion : joinCriteria) {
                Join<Object, Object> join = root.join(criterion.getJoinAlias());
                Predicate predicate = createPredicate(criteriaBuilder, join.get(criterion.getFieldName()), criterion.getFieldType(), searchPattern);
                if (predicate != null) {
                    predicates.add(predicate);
                }
            }

            // Combine all predicates with OR
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }

    private Predicate createPredicate(CriteriaBuilder criteriaBuilder, Path<?> path, Class<?> fieldType, String searchPattern) {
        if (fieldType == String.class) {
            return criteriaBuilder.like(path.as(String.class), searchPattern);
        } else if (Number.class.isAssignableFrom(fieldType)) {
            try {
                Long longValue = Long.parseLong(searchString);
                return criteriaBuilder.equal(path, longValue);
            } catch (NumberFormatException ignored) {
                // Skip if the searchString cannot be converted to a number
            }
        }
        // Extend this to handle other field types like Date if needed
        return null;
    }

    private static class SearchCriterion {
        private final String fieldName;
        private final Class<?> fieldType;

        public SearchCriterion(String fieldName, Class<?> fieldType) {
            this.fieldName = fieldName;
            this.fieldType = fieldType;
        }

        public String getFieldName() {
            return fieldName;
        }

        public Class<?> getFieldType() {
            return fieldType;
        }
    }

    private static class JoinCriterion {
        private final String joinAlias;
        private final String fieldName;
        private final Class<?> fieldType;

        public JoinCriterion(String joinAlias, String fieldName, Class<?> fieldType) {
            this.joinAlias = joinAlias;
            this.fieldName = fieldName;
            this.fieldType = fieldType;
        }

        public String getJoinAlias() {
            return joinAlias;
        }

        public String getFieldName() {
            return fieldName;
        }

        public Class<?> getFieldType() {
            return fieldType;
        }
    }
}
