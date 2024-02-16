package me.empty.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.Year;
import java.util.Set;

@Entity
@Table(name = "country", indexes = @Index(name = "country_city_idx", columnList = "capital"))
@Getter
@Setter
@ToString
public class Country {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Size(max = 3)
    @NotNull
    @Column(length = 3, nullable = false)
    private String code = "";

    @Size(max = 2)
    @NotNull
    @Column(name = "code_2", length = 2, nullable = false)
    private String code2 = "";

    @Column(length = 52, nullable = false)
    @NotNull
    @Size(max = 52)
    private String name = "";

    @Column(nullable = false)
    @NotNull
    @Enumerated(EnumType.ORDINAL)
    private Continent continent;

    @Size(max = 25)
    @NotNull
    @Column(length = 26, nullable = false)
    private String region = "";

    @Column(name = "surface_area", precision = 10, scale = 2, nullable = false)
    @NotNull
    private BigDecimal surfaceArea = BigDecimal.ZERO;

    @Column(name = "indep_year")
    @Convert(converter = YearConverter.class)
    private Year indepYear;

    @NotNull
    @Column(nullable = false)
    private int population = 0;

    @Column(name = "life_expectancy", precision = 3, scale = 1)
    private BigDecimal lifeExpectancy;


    @Column(precision = 10, scale = 2)
    private BigDecimal gnp;

    @Column(name = "gnpo_id", precision = 10, scale = 2)
    private BigDecimal gnpoId;

    @Size(max = 45)
    @NotNull
    @Column(name = "local_name", length = 45, nullable = false)
    private String localName = "";

    @Size(max = 45)
    @NotNull
    @Column(name = "government_form", length = 45, nullable = false)
    private String governmentForm = "";

    @Size(max = 60)
    @Column(name = "head_of_state", length = 60)
    private String headOfState;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "capital", foreignKey = @ForeignKey(name = "country_city_capital_FK"), unique = true)
    @ToString.Exclude
    private City capital;

    @OneToMany(mappedBy = "country")
    private Set<Language> languages;

    public void setCapital(City capital) {
        this.capital = capital;
        capital.setCountry(this);
    }

    @Converter(autoApply = true)
    static class YearConverter implements AttributeConverter<Year, Short> {

        @Override
        public Short convertToDatabaseColumn(Year attribute) {
            return (short) attribute.getValue();
        }

        @Override
        public Year convertToEntityAttribute(Short dbData) {

            return dbData == null ? null : Year.of(dbData);
        }
    }
}
