package bg.qponer.qponerbackend

import bg.qponer.qponerbackend.domain.data.*
import bg.qponer.qponerbackend.domain.repo.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.core.env.Environment
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.*
import java.util.logging.Logger
import javax.annotation.PostConstruct
import javax.sql.DataSource

@Component
@Profile("dev")
class ApplicationStartup(
        @Autowired val businessRepo: BusinessRepo,
        @Autowired val contributorRepo: ContributorRepo,
        @Autowired val adminRepo: AdminRepo,
        @Autowired val cityRepo: CityRepo,
        @Autowired val countryRepo: CountryRepo,
        @Autowired val accumulatedValueRepo: AccumulatedValueRepo,
        @Autowired val voucherTypeRepo: VoucherTypeRepo,
        @Autowired val passwordEncoder: PasswordEncoder,
        @Autowired val dataSource: DataSource,
        @Autowired val environment: Environment
) {

    val logger = Logger.getLogger(ApplicationStartup::class.qualifiedName)

    @PostConstruct
    fun init() {
        if (environment.activeProfiles.contains("int")) {
            logger.info("profile 'int' is active, so we'll prevent data insert")
            return;
        }
        val country = Country(code = "BG", name = "Bulgaria")
        countryRepo.save(country)

        val city = City(name = "Sofia", country = country)
        cityRepo.save(city);

        val address = Address(line1 = "Mladost", city = city, country = country, postalCode = "1000", region = "Sofia")

        val contributor = Contributor(
                username = "user",
                password = passwordEncoder.encode("user"),
                email = "pocko@mail.com",
                phone = "234567",
                firstName = "Pocko",
                lastName = "Pockov",
                address = address,
                dateOfBirth = Calendar.getInstance(),
                nationality = country,
                countryOfResidence = country,
                walletUserId = "1",
                walletId = "1"
        )
        val contributor1 = Contributor(
                username = "pocko2",
                password = passwordEncoder.encode("password"),
                email = "pocko2@mail.com",
                phone = "654321",
                firstName = "Pocko2",
                lastName = "Pockov",
                address = address,
                dateOfBirth = Calendar.getInstance(),
                nationality = country,
                countryOfResidence = country,
                walletUserId = "1",
                walletId = "1"
        )
        val contributor2 = Contributor(
                username = "pocko2123",
                password = passwordEncoder.encode("password"),
                email = "pocko2123@mail.com",
                phone = "654321123",
                firstName = "Pocko2123",
                lastName = "Pockov123",
                address = address,
                dateOfBirth = Calendar.getInstance(),
                nationality = country,
                countryOfResidence = country,
                walletUserId = "1",
                walletId = "1"
        )
        contributorRepo.saveAll(listOf(contributor, contributor1, contributor2))

        val admin = QponerAdmin(
                username = "adm1n",
                password = passwordEncoder.encode("w0rdp4ss"),
                email = "pocko123@123.com",
                phone = "1982352"
        )
        adminRepo.save(admin)

        val bronzeVoucher = VoucherType(typeName = VoucherTypeName.BRONZE, value = BigDecimal.valueOf(10L))
        val silverVoucher = VoucherType(typeName = VoucherTypeName.SILVER, value = BigDecimal.valueOf(20L))
        val goldVoucher = VoucherType(typeName = VoucherTypeName.GOLD, value = BigDecimal.valueOf(50L))
        voucherTypeRepo.saveAll(listOf(bronzeVoucher, silverVoucher, goldVoucher))

//        createVouchers(contributor, businessOwner, arrayOf(goldVoucher, goldVoucher));
//        createVouchers(contributor1, businessOwner, arrayOf(silverVoucher, silverVoucher, silverVoucher));

        initClients()
    }

    private fun initClients() {
        val connection = dataSource.connection
        val password = passwordEncoder.encode("password");
        val prepareStatement = connection.prepareStatement("INSERT INTO oauth_client_details " +
                "    (client_id, client_secret, scope, authorized_grant_types, " +
                "    web_server_redirect_uri, authorities, access_token_validity, " +
                "    refresh_token_validity, additional_information, autoapprove) " +
                "VALUES " +
                "    ('client', ?, 'read,write', " +
                "    'password,authorization_code,refresh_token,implicit', 'http://localhost:4200/', null, 3600, 144000, null, ?);")
        prepareStatement.setString(1, password)
        prepareStatement.setBoolean(2, true)
        prepareStatement.executeUpdate()
    }
}
