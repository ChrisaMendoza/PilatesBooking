package com.pilates.booking.domain;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A PeriodSubscription.
 */
@Table("period_subscription")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PeriodSubscription implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("subscription_name")
    private String subscriptionName;

    @Column("description")
    private String description;

    @NotNull(message = "must not be null")
    @Column("price")
    private Integer price;

    @Column("billing_period")
    private String billingPeriod;

    @NotNull(message = "must not be null")
    @Column("credits_per_period")
    private Integer creditsPerPeriod;

    @Column("start_date")
    private LocalDate startDate;

    @Column("end_date")
    private LocalDate endDate;

    @org.springframework.data.annotation.Transient
    private User user;

    @Column("user_id")
    private Long userId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PeriodSubscription id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubscriptionName() {
        return this.subscriptionName;
    }

    public PeriodSubscription subscriptionName(String subscriptionName) {
        this.setSubscriptionName(subscriptionName);
        return this;
    }

    public void setSubscriptionName(String subscriptionName) {
        this.subscriptionName = subscriptionName;
    }

    public String getDescription() {
        return this.description;
    }

    public PeriodSubscription description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPrice() {
        return this.price;
    }

    public PeriodSubscription price(Integer price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getBillingPeriod() {
        return this.billingPeriod;
    }

    public PeriodSubscription billingPeriod(String billingPeriod) {
        this.setBillingPeriod(billingPeriod);
        return this;
    }

    public void setBillingPeriod(String billingPeriod) {
        this.billingPeriod = billingPeriod;
    }

    public Integer getCreditsPerPeriod() {
        return this.creditsPerPeriod;
    }

    public PeriodSubscription creditsPerPeriod(Integer creditsPerPeriod) {
        this.setCreditsPerPeriod(creditsPerPeriod);
        return this;
    }

    public void setCreditsPerPeriod(Integer creditsPerPeriod) {
        this.creditsPerPeriod = creditsPerPeriod;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public PeriodSubscription startDate(LocalDate startDate) {
        this.setStartDate(startDate);
        return this;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public PeriodSubscription endDate(LocalDate endDate) {
        this.setEndDate(endDate);
        return this;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
        this.userId = user != null ? user.getId() : null;
    }

    public PeriodSubscription user(User user) {
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
        if (!(o instanceof PeriodSubscription)) {
            return false;
        }
        return getId() != null && getId().equals(((PeriodSubscription) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PeriodSubscription{" +
            "id=" + getId() +
            ", subscriptionName='" + getSubscriptionName() + "'" +
            ", description='" + getDescription() + "'" +
            ", price=" + getPrice() +
            ", billingPeriod='" + getBillingPeriod() + "'" +
            ", creditsPerPeriod=" + getCreditsPerPeriod() +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            "}";
    }
}
