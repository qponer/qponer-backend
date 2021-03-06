package bg.qponer.qponerbackend.domain.repo

import bg.qponer.qponerbackend.domain.data.City
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CityRepo : CrudRepository<City, Long> {

    fun findByCountry_Id(@Param("countryId") countryId: Long): List<City>

}
