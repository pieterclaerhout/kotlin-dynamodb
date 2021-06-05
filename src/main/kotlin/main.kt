import io.github.serpro69.kfaker.Faker
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.util.*

fun main() {

    val awsCreds = AwsBasicCredentials.create(
        System.getenv("AWS_ACCESS_KEY"),
        System.getenv("AWS_SECRET_KEY")
    )

    val client = DynamoDbClient
        .builder()
        .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
        .region(Region.EU_WEST_1)
        .build()

    val db = DynamoDbEnhancedClient.builder().dynamoDbClient(client).build()

    val repo = SignupRepo(db)

    val faker = Faker()

//    println("Creating items…")
//    for (i in 1L..100L) {
//        val recentSignup = Signup(
//            id = i,
//            uuid = UUID.randomUUID().toString(),
//            name = faker.name.name()
//        )
//        repo.save(recentSignup)
//    }
//    println("Created items")

    for (i in 1..5) {
        println()
        val items = repo.randomItems(3)
        if (items.size == 0) {
            println("No items found")
        }
        items.forEach {
            println("${i} ${it}")
        }
    }

}
