import org.slf4j.Logger
import org.slf4j.LoggerFactory
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException
import software.amazon.awssdk.services.dynamodb.model.TimeToLiveSpecification
import software.amazon.awssdk.services.dynamodb.model.UpdateTimeToLiveRequest
import java.util.*


class SignupRepo(private val client: DynamoDbClient) {

    private val tableName = "signups_v4"
    private val enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(client).build()
    private val table = enhancedClient.table(tableName, tableSchema)

    init {

        try {
            // Async…
            table.createTable()
            log.info("Created: ${table.tableName()}")
            Thread.sleep(5000L)
        } catch (e: ResourceInUseException) {
            log.info("Exists: ${table.tableName()}")
        }

        try {
            updateTtl("expiresOn")
            log.info("Set TTL: ${table.tableName()}")
        } catch (e: Exception) {
            log.info("TTL already enabled")
        }

    }

    companion object {
        private val tableSchema = TableSchema.fromBean(Signup::class.java)
        val log = LoggerFactory.getLogger(Main::class.java)
    }

    fun saveMany(signups: List<Signup>) {

        signups.forEach {
            it.tableName = tableName
            it.expiresOn = it.createdOn?.plus(3600)
        }

        signups.chunked(25).forEach {
            val batch = WriteBatch.builder(Signup::class.java).mappedTableResource(table)
            it.forEach { signup ->
                batch.addPutItem(signup)
            }
            enhancedClient.batchWriteItem {
                it.writeBatches(batch.build()).build()
            }
        }

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

    fun randomItems(limit: Int): List<Signup> {
        val key = Key.builder().partitionValue(tableName).sortValue(UUID.randomUUID().toString()).build()
        val cond = QueryConditional.sortGreaterThan(key)
        return table.query(
            QueryEnhancedRequest.builder().queryConditional(cond).limit(limit).build()
        )?.first()?.items().orEmpty()
    }

    private fun updateTtl(field: String) {

        val req = UpdateTimeToLiveRequest.builder()
            .tableName(tableName)
            .timeToLiveSpecification(
                TimeToLiveSpecification.builder().attributeName(field).enabled(true).build()
            ).build()

        client.updateTimeToLive(req)

    }

}