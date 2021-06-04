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

    for (i in 1L..200L) {
        val recentSignup = Signup(
            i,
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString()
        )
        repo.save(recentSignup)
    }

    val signup = repo.retrieve(1)
    println(signup)

    val result = repo.query(1)
    result?.stream()?.forEach { p ->
        p.items().forEach { item ->
            println(item)
        }
    }

}