package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CollectionId;
import org.hibernate.annotations.Fetch;
import ru.practicum.shareit.user.User;

import javax.persistence.*;

@Data
@Builder
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NonNull
    @Column(nullable = false)
    String name;

    @Column(nullable = false)
    String description;

    @Column(nullable = false)
    boolean available;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    User requester;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return id != null && id.equals(((Item) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
