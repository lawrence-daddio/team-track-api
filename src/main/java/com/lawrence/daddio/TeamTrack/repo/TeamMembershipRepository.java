package com.lawrence.daddio.TeamTrack.repo;

import com.lawrence.daddio.TeamTrack.entity.TeamMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamMembershipRepository extends JpaRepository<TeamMembership, Long> {

    List<TeamMembership> findByTeamId(Long teamId);

    List<TeamMembership> findByEmployeeId(Long employeeId);

}
