package me.empty.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@Table(name = "city", indexes = @Index(name = "city_country_idx", columnList = "country_id"))
@ToString
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Size(max = 35)
    @NotNull
    @Column(length = 35, nullable = false)
    private String name = "";

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "country_id", nullable = false, foreignKey = @ForeignKey(name = "city_country_FK"))
    private Country country;

    @Size(max = 20)
    @NotNull
    @Column(length = 20, nullable = false)
    private String district = "";

    @Column(nullable = false)
    private int population;
}
