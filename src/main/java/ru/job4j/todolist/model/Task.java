package ru.job4j.todolist.model;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity()
@Table(name = "tasks")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class Task {
    @Id @GeneratedValue
    @EqualsAndHashCode.Include
    private int id;
    private String description;
    private LocalDateTime created;
    private boolean done;
}
