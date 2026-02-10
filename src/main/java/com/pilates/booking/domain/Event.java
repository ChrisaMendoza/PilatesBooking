package com.pilates.booking.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZonedDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Event entity (formerly ClassSession)
 */
@Schema(description = "Event entity (formerly ClassSession)")
@Table("event")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Event implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("coach_name")
    private String coachName;

    @NotNull(message = "must not be null")
    @Column("start_at")
    private ZonedDateTime startAt;

    @NotNull(message = "must not be null")
    @Column("end_at")
    private ZonedDateTime endAt;

    @NotNull(message = "must not be null")
    @Column("capacity")
    private Integer capacity;

    @Column("status")
    private String status;

    @NotNull(message = "must not be null")
    @Column("waitlist_open")
    private Boolean waitlistOpen = Boolean.FALSE;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "events", "classSessions" }, allowSetters = true)
    private Studio studio;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "events", "classSessions" }, allowSetters = true)
    private ClassType classType;

    @Column("studio_id")
    private Long studioId;

    @Column("class_type_id")
    private Long classTypeId;

    public Long getId() {
        return this.id;
    }

    public Event id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCoachName() {
        return this.coachName;
    }

    public Event coachName(String coachName) {
        this.setCoachName(coachName);
        return this;
    }

    public void setCoachName(String coachName) {
        this.coachName = coachName;
    }

    public ZonedDateTime getStartAt() {
        return this.startAt;
    }

    public Event startAt(ZonedDateTime startAt) {
        this.setStartAt(startAt);
        return this;
    }

    public void setStartAt(ZonedDateTime startAt) {
        this.startAt = startAt;
    }

    public ZonedDateTime getEndAt() {
        return this.endAt;
    }

    public Event endAt(ZonedDateTime endAt) {
        this.setEndAt(endAt);
        return this;
    }

    public void setEndAt(ZonedDateTime endAt) {
        this.endAt = endAt;
    }

    public Integer getCapacity() {
        return this.capacity;
    }

    public Event capacity(Integer capacity) {
        this.setCapacity(capacity);
        return this;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getStatus() {
        return this.status;
    }

    public Event status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getWaitlistOpen() {
        return waitlistOpen;
    }

    public void setWaitlistOpen(Boolean waitlistOpen) {
        this.waitlistOpen = waitlistOpen;
    }

    public Event waitlistOpen(Boolean waitlistOpen) {
        this.setWaitlistOpen(waitlistOpen);
        return this;
    }

    public Studio getStudio() {
        return this.studio;
    }

    public void setStudio(Studio studio) {
        this.studio = studio;
        this.studioId = studio != null ? studio.getId() : null;
    }

    public Event studio(Studio studio) {
        this.setStudio(studio);
        return this;
    }

    public ClassType getClassType() {
        return this.classType;
    }

    public void setClassType(ClassType classType) {
        this.classType = classType;
        this.classTypeId = classType != null ? classType.getId() : null;
    }

    public Event classType(ClassType classType) {
        this.setClassType(classType);
        return this;
    }

    public Long getStudioId() {
        return this.studioId;
    }

    public void setStudioId(Long studio) {
        this.studioId = studio;
    }

    public Long getClassTypeId() {
        return this.classTypeId;
    }

    public void setClassTypeId(Long classType) {
        this.classTypeId = classType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Event)) {
            return false;
        }
        return getId() != null && getId().equals(((Event) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Event{" +
            "id=" + getId() +
            ", coachName='" + getCoachName() + "'" +
            ", startAt='" + getStartAt() + "'" +
            ", endAt='" + getEndAt() + "'" +
            ", capacity=" + getCapacity() +
            ", status='" + getStatus() + "'" +
            ", waitlistOpen='" + getWaitlistOpen() + "'" +
            "}";
    }
}
