package me.empty.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "country_language")
@Getter
@Setter
public class Language {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Size(max = 30)
    @NotNull
    @Column(name = "language", length = 30, nullable = false)
    private String name;

    @Embedded
    private LangInfo info;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "country_id", nullable = false, foreignKey = @ForeignKey(name = "language_country_FK"))
    private Country country;
}

