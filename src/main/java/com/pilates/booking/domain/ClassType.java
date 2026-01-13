package com.pilates.booking.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A ClassType.
 */
@Table("class_type")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ClassType implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("name")
    private String name;

    @Column("description")
    private String description;

    @NotNull(message = "must not be null")
    @Column("duration")
    private Integer duration;

    @Column("capacity")
    private Integer capacity;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "studio", "classType" }, allowSetters = true)
    private Set<ClassSession> classSessions = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ClassType id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public ClassType name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public ClassType description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDuration() {
        return this.duration;
    }

    public ClassType duration(Integer duration) {
        this.setDuration(duration);
        return this;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getCapacity() {
        return this.capacity;
    }

    public ClassType capacity(Integer capacity) {
        this.setCapacity(capacity);
        return this;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Set<ClassSession> getClassSessions() {
        return this.classSessions;
    }

    public void setClassSessions(Set<ClassSession> classSessions) {
        if (this.classSessions != null) {
            this.classSessions.forEach(i -> i.setClassType(null));
        }
        if (classSessions != null) {
            classSessions.forEach(i -> i.setClassType(this));
        }
        this.classSessions = classSessions;
    }

    public ClassType classSessions(Set<ClassSession> classSessions) {
        this.setClassSessions(classSessions);
        return this;
    }

    public ClassType addClassSession(ClassSession classSession) {
        this.classSessions.add(classSession);
        classSession.setClassType(this);
        return this;
    }

    public ClassType removeClassSession(ClassSession classSession) {
        this.classSessions.remove(classSession);
        classSession.setClassType(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClassType)) {
            return false;
        }
        return getId() != null && getId().equals(((ClassType) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClassType{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", duration=" + getDuration() +
            ", capacity=" + getCapacity() +
            "}";
    }
}
