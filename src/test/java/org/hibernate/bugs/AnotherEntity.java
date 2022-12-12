package org.hibernate.bugs;

import jakarta.persistence.*;

import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.GenerationType.IDENTITY;
import static java.util.Objects.nonNull;

@Entity
public class AnotherEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "entity_id")
    private TestEntity testEntity;

    private String type;

    @Lob
    @Basic(fetch = EAGER)
    private byte[] randomBytes;

    public AnotherEntity() {
    }

    public AnotherEntity(TestEntity testEntity, String type, byte[] randomBytes) {
        this.testEntity = testEntity;
        this.type = type;
        this.randomBytes = randomBytes;
    }

    @Override
    public String toString() {
        return "AnotherEntity{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", randomBytesNonNull=" + nonNull(randomBytes) +
                '}';
    }
}
