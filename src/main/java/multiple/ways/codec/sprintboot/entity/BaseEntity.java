package multiple.ways.codec.sprintboot.entity;

import javax.persistence.Column;
import javax.persistence.Id;

public class BaseEntity {
    @Id
    @Column(name = "id")
    private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
    
}
