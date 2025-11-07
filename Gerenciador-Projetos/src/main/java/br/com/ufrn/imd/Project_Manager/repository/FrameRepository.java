package br.com.ufrn.imd.Project_Manager.repository;

import br.com.ufrn.imd.Project_Manager.model.Frame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FrameRepository extends JpaRepository<Frame, Long> {
    List<Frame> findByNameIgnoreCase(String name);
    List<Frame> findByProjectIdOrderByOrderIndexAsc(Long projectId);
}
