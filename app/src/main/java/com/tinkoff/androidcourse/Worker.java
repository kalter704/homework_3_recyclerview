package com.tinkoff.androidcourse;

import java.util.Objects;

public class Worker {
    private int id;
    private String name;
    private Integer photo;
    private String age;
    private String position;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPhoto() {
        return photo;
    }

    public void setPhoto(Integer photo) {
        this.photo = photo;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Worker worker = (Worker) o;
        return id == worker.id &&
                Objects.equals(name, worker.name) &&
                Objects.equals(photo, worker.photo) &&
                Objects.equals(age, worker.age) &&
                Objects.equals(position, worker.position);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, photo, age, position);
    }
}
