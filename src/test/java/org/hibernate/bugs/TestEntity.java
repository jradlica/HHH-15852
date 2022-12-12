package org.hibernate.bugs;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
public class TestEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    Long id;

    String textIdentifier;

    @OneToMany(cascade = ALL, mappedBy = "testEntity", fetch = EAGER)
    private List<AnotherEntity> relatedEntities;

    public TestEntity() {
    }

    public TestEntity(String textIdentifier) {
        this.textIdentifier = textIdentifier;
        this.relatedEntities = new ArrayList<>();
    }

    public void addRelatedEntity(AnotherEntity relatedEntity) {
        this.relatedEntities.add(relatedEntity);
    }

    @Override
    public String toString() {
        return "TestEntity{" +
                "id=" + id +
                ", textIdentifier='" + textIdentifier + '\'' +
                ", relatedEntities=" + relatedEntities +
                '}';
    }
}
