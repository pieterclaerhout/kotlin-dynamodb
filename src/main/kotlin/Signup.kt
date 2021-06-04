import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey
import java.time.LocalDateTime
import java.time.ZoneOffset

@DynamoDbBean
data class Signup(
    @get:DynamoDbPartitionKey var tableName: String? = null,
    @get:DynamoDbSortKey var uuid: String? = null,
    var id: Long? = null,
    var name: String? = null,
    var createdOn: Long? = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
    var expiresOn: Long? = 0
)
