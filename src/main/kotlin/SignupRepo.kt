import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest
import java.util.*


class SignupRepo(private val client: DynamoDbEnhancedClient) {

    private val tableName = "signups_v3"
    private val table = client.table(tableName, tableSchema)

    companion object {
        private val tableSchema = TableSchema.fromBean(Signup::class.java)
    }

    fun save(signup: Signup) {
        signup.tableName = tableName
        signup.expiresOn = signup.createdOn?.plus(3600)
        table.putItem(signup)
    }

    fun retrieve(id: Long): Signup? {
        val key = Key.builder().partitionValue(tableName).sortValue(id).build()
        return table.getItem(key)
    }

    fun queryLimit(limit: Int): PageIterable<Signup>? {
        val key = Key.builder().partitionValue(tableName).sortValue(1).build()
        var cond = QueryConditional.sortGreaterThanOrEqualTo(key)
        return table.query(
            QueryEnhancedRequest.builder()
                .queryConditional(cond)
                .limit(limit)
                .build()
        )
    }

    fun randomItems(limit: Int) : List<Signup> {
        val key = Key.builder().partitionValue(tableName).sortValue(UUID.randomUUID().toString()).build()
        val cond = QueryConditional.sortGreaterThan(key)
        return table.query(
            QueryEnhancedRequest.builder()
                .queryConditional(cond)
                .limit(limit)
                .build()
        )?.first()?.items().orEmpty()
    }

}