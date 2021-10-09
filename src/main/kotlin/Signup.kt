import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

@DynamoDbBean
data class Signup(

    @get:DynamoDbPartitionKey
    var id: Long? = null,

    @get:DynamoDbSecondaryPartitionKey(indexNames = ["tableName-uuid-index"])
    var tableName: String? = null,

    @get:DynamoDbSecondarySortKey(indexNames = ["tableName-uuid-index"])
    var uuid: String? = null,

    var name: String? = null,

    var createdOn: Long? = Calendar.getInstance().timeInMillis / 1000,

    var expiresOn: Long? = 0

)
