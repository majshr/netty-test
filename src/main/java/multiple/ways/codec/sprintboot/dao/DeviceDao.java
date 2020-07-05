package multiple.ways.codec.sprintboot.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import multiple.ways.codec.sprintboot.entity.DeviceEntity;

public interface DeviceDao extends JpaRepository<DeviceEntity, Long>{

}