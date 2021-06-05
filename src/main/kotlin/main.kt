import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import io.github.serpro69.kfaker.Faker
import org.slf4j.LoggerFactory
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.util.*

object Main {

    @JvmStatic
    fun main(args: Array<String>) {

        val rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
        rootLogger.level = Level.INFO

        val log = LoggerFactory.getLogger(Main::class.java)

        val awsCreds = AwsBasicCredentials.create(
            System.getenv("AWS_ACCESS_KEY"),
            System.getenv("AWS_SECRET_KEY")
        )

        val client = DynamoDbClient
            .builder()
            .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
            .region(Region.EU_WEST_1)
            .build()

        val repo = SignupRepo(client)

        val faker = Faker()

        log.info("Creating items…")
        val signups = mutableListOf<Signup>()
        for (i in 1L..100L) {
            val recentSignup = Signup(
                id = i,
                uuid = UUID.randomUUID().toString(),
                name = faker.name.name()
            )
            signups.add(recentSignup)
//        repo.save(recentSignup)
        }
        repo.saveMany(signups)
        log.info("Created items")

        for (i in 1..5) {
            log.info("")
            val items = repo.randomItems(3)
            if (items.size == 0) {
                log.info("No items found")
            }
            items.forEach {
                log.info("${i} ${it}")
            }
        }

    }

}
