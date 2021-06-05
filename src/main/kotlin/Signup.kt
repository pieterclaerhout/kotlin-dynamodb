import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

@DynamoDbBean
data class Signup(
    @get:DynamoDbPartitionKey var tableName: String? = null,
    @get:DynamoDbSortKey var uuid: String? = null,
    var id: Long? = null,
    var name: String? = null,
    var createdOn: Long? = Calendar.getInstance().timeInMillis / 1000,
    var expiresOn: Long? = 0
)
