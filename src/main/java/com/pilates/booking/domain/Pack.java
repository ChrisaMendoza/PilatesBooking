package com.pilates.booking.domain;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Pack.
 */
@Table("pack")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Pack implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("pack_name")
    private String packName;

    @Column("description")
    private String description;

    @NotNull(message = "must not be null")
    @Column("price")
    private Integer price;

    @Column("billing_period")
    private String billingPeriod;

    @NotNull(message = "must not be null")
    @Column("credits")
    private Integer credits;

    @Column("validity_days")
    private Integer validityDays;

    @org.springframework.data.annotation.Transient
    private User user;

    @Column("user_id")
    private Long userId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Pack id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPackName() {
        return this.packName;
    }

    public Pack packName(String packName) {
        this.setPackName(packName);
        return this;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

    public String getDescription() {
        return this.description;
    }

    public Pack description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPrice() {
        return this.price;
    }

    public Pack price(Integer price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getBillingPeriod() {
        return this.billingPeriod;
    }

    public Pack billingPeriod(String billingPeriod) {
        this.setBillingPeriod(billingPeriod);
        return this;
    }

    public void setBillingPeriod(String billingPeriod) {
        this.billingPeriod = billingPeriod;
    }

    public Integer getCredits() {
        return this.credits;
    }

    public Pack credits(Integer credits) {
        this.setCredits(credits);
        return this;
    }

    public void setCredits(Integer credits) {
        this.credits = credits;
    }

    public Integer getValidityDays() {
        return this.validityDays;
    }

    public Pack validityDays(Integer validityDays) {
        this.setValidityDays(validityDays);
        return this;
    }

    public void setValidityDays(Integer validityDays) {
        this.validityDays = validityDays;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
        this.userId = user != null ? user.getId() : null;
    }

    public Pack user(User user) {
        this.setUser(user);
        return this;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long user) {
        this.userId = user;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pack)) {
            return false;
        }
        return getId() != null && getId().equals(((Pack) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Pack{" +
            "id=" + getId() +
            ", packName='" + getPackName() + "'" +
            ", description='" + getDescription() + "'" +
            ", price=" + getPrice() +
            ", billingPeriod='" + getBillingPeriod() + "'" +
            ", credits=" + getCredits() +
            ", validityDays=" + getValidityDays() +
            "}";
    }
}
