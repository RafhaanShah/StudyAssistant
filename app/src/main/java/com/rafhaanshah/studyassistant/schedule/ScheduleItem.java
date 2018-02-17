package com.rafhaanshah.studyassistant.schedule;

import io.realm.RealmObject;

public class ScheduleItem extends RealmObject {

    public static final String ScheduleItem_ID = "ID";
    static final String ScheduleItem_TITLE = "title";
    static final String ScheduleItem_TYPE = "type";
    static final String ScheduleItem_NOTES = "notes";
    static final String ScheduleItem_TIME = "time";
    static final String ScheduleItem_COMPLETED = "completed";
    private int ID;
    private String title, notes, type;
    private Long time;
    private boolean completed;

    int getID() {
        return ID;
    }

    void setID(int ID) {
        this.ID = ID;
    }

    String getTitle() {
        return title;
    }

    void setTitle(String title) {
        this.title = title;
    }

    ScheduleItemType getType() {
        return ScheduleItemType.valueOf(type);
    }

    void setType(ScheduleItemType type) {
        this.type = type.name();
    }

    String getNotes() {
        return notes;
    }

    void setNotes(String notes) {
        this.notes = notes;
    }

    Long getTime() {
        return time;
    }

    void setTime(Long time) {
        this.time = time;
    }

    boolean isCompleted() {
        return completed;
    }

    void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public enum ScheduleItemType {
        HOMEWORK, COURSEWORK, TEST, EXAM
    }
}


