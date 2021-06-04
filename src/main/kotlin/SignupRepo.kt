import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest


class SignupRepo(private val client: DynamoDbEnhancedClient) {

    private val tableName = "signups"
    private val table = client.table(tableName, tableSchema)

    companion object {
        private val tableSchema = TableSchema.fromBean(Signup::class.java)
    }

    fun save(signup: Signup) {
        table.putItem(signup)
    }

    fun retrieve(id: Long): Signup? {
        val key = Key.builder().partitionValue(id).build()
        return table.getItem(key)
    }

    fun query(id: Long): PageIterable<Signup>? {
        val queryConditional = QueryConditional.keyEqualTo(Key.builder().partitionValue(id).build())
        return table.query(QueryEnhancedRequest.builder()
            .queryConditional(queryConditional)
            .limit(3)
            .build()
        )
    }

}