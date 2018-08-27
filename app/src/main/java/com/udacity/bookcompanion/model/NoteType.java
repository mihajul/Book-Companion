package com.udacity.bookcompanion.model;

public enum NoteType {
    CHAPTER,
    TEXT,
    IMAGE;

    public static NoteType parseInt(int noteType) {
        switch (noteType) {
            case 0:
                return CHAPTER;
            case 1:
                return TEXT;
            case 2:
                return IMAGE;
        }
        return null;
    }
}
