package com.pilates.booking.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Studio entity
 */
@Schema(description = "Studio entity")
@Table("studio")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Studio implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("name")
    private String name;

    @Column("address")
    private String address;

    @Column("category")
    private String category;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "studio", "classType" }, allowSetters = true)
    private Set<Event> events = new HashSet<>();

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "studio", "classType" }, allowSetters = true)
    private Set<ClassSession> classSessions = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Studio id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Studio name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return this.address;
    }

    public Studio address(String address) {
        this.setAddress(address);
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCategory() {
        return this.category;
    }

    public Studio category(String category) {
        this.setCategory(category);
        return this;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Set<Event> getEvents() {
        return this.events;
    }

    public void setEvents(Set<Event> events) {
        if (this.events != null) {
            this.events.forEach(i -> i.setStudio(null));
        }
        if (events != null) {
            events.forEach(i -> i.setStudio(this));
        }
        this.events = events;
    }

    public Studio events(Set<Event> events) {
        this.setEvents(events);
        return this;
    }

    public Studio addEvent(Event event) {
        this.events.add(event);
        event.setStudio(this);
        return this;
    }

    public Studio removeEvent(Event event) {
        this.events.remove(event);
        event.setStudio(null);
        return this;
    }

    public Set<ClassSession> getClassSessions() {
        return this.classSessions;
    }

    public void setClassSessions(Set<ClassSession> classSessions) {
        if (this.classSessions != null) {
            this.classSessions.forEach(i -> i.setStudio(null));
        }
        if (classSessions != null) {
            classSessions.forEach(i -> i.setStudio(this));
        }
        this.classSessions = classSessions;
    }

    public Studio classSessions(Set<ClassSession> classSessions) {
        this.setClassSessions(classSessions);
        return this;
    }

    public Studio addClassSession(ClassSession classSession) {
        this.classSessions.add(classSession);
        classSession.setStudio(this);
        return this;
    }

    public Studio removeClassSession(ClassSession classSession) {
        this.classSessions.remove(classSession);
        classSession.setStudio(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Studio)) {
            return false;
        }
        return getId() != null && getId().equals(((Studio) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Studio{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", address='" + getAddress() + "'" +
            ", category='" + getCategory() + "'" +
            "}";
    }
}
