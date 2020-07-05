package multiple.ways.codec.sprintboot.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "device_entity")
public class DeviceEntity extends BaseEntity{
    @Column(name = "name")
    private String name;

    @Column(name = "temperature")
    private Double temperature;

    @Column(name = "created_time")
    private Long createdTime;

    @Column(name = "humidity")
    private Double humidity;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getTemperature() {
		return temperature;
	}

	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}

	public Long getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Long createdTime) {
		this.createdTime = createdTime;
	}

	public Double getHumidity() {
		return humidity;
	}

	public void setHumidity(Double humidity) {
		this.humidity = humidity;
	}
    
}
