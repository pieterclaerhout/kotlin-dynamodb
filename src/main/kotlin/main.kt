import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.util.*

fun main() {

    val awsCreds = AwsBasicCredentials.create(
        "AKIAS7D5VHOZLONHAUVP",
        "Z8HDTp1um18UTHKGtu3xb05aaPHsy8Zdvjobgggr"
    )

    val client = DynamoDbClient
        .builder()
        .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
        .region(Region.EU_WEST_1)
        .build()

    val db = DynamoDbEnhancedClient.builder().dynamoDbClient(client).build()

    val repo = SignupRepo(db)

    println("Creating items…")
    for (i in 1L..100L) {
        val recentSignup = Signup(
            id = i,
            uuid = UUID.randomUUID().toString(),
            name = UUID.randomUUID().toString()
        )
        repo.save(recentSignup)
    }
    println("Created items")

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
