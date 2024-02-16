package me.empty.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Embeddable
@Getter
@Setter
public class LangInfo {

    @NotNull
    @Column(name = "is_official", nullable = false)
    private boolean isOfficial;

    @Column(precision = 4, scale = 1)
    private BigDecimal percentage;
}
