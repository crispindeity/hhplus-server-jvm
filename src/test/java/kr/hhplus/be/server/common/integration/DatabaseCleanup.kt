package kr.hhplus.be.server.common.integration

import jakarta.persistence.Entity
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.persistence.Table
import jakarta.persistence.metamodel.EntityType
import java.util.stream.Collectors
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Component
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@Component
@ActiveProfiles("test")
class DatabaseCleanup : InitializingBean {
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    private lateinit var tables: MutableList<String>

    private val excludedTableNames =
        setOf(
            "users",
            "seats",
            "concerts",
            "concert_schedules",
            "concert_seats",
            "point_wallets",
            "queue_numbers"
        )

    override fun afterPropertiesSet() {
        val entities: MutableSet<EntityType<*>> = entityManager.metamodel.entities

        tables =
            entities
                .stream()
                .filter { isEntity(it) && hasTableAnnotation(it) && !isExcluded(it) }
                .map {
                    val tableName: String = it.javaType.getAnnotation(Table::class.java).name
                    tableName.ifBlank { tableNameFromEntity(it) }
                }.collect(Collectors.toList())

        val entityNames: MutableList<String> =
            entities
                .stream()
                .filter { isEntity(it) && !hasTableAnnotation(it) && !isExcluded(it) }
                .map { it.name.toSnakeCase() }
                .toList()

        tables.addAll(entityNames)
    }

    private fun isExcluded(entity: EntityType<*>): Boolean {
        val tableName: String =
            if (entity.javaType.isAnnotationPresent(Table::class.java)) {
                entity.javaType.getAnnotation(Table::class.java).name.ifBlank {
                    entity.name.toSnakeCase()
                }
            } else {
                entity.name.toSnakeCase()
            }

        return tableName in excludedTableNames
    }

    @Transactional
    fun execute() {
        entityManager.flush()
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate()

        for (tableName: String in tables) {
            entityManager.createNativeQuery("TRUNCATE TABLE `$tableName`").executeUpdate()
            entityManager
                .createNativeQuery("ALTER TABLE `$tableName` AUTO_INCREMENT = 1")
                .executeUpdate()
        }

        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate()
    }

    private fun isEntity(entity: EntityType<*>): Boolean =
        entity.javaType.isAnnotationPresent(Entity::class.java)

    private fun hasTableAnnotation(entity: EntityType<*>): Boolean =
        entity.javaType.isAnnotationPresent(Table::class.java)

    private fun tableNameFromEntity(entity: EntityType<*>): String =
        entity.javaType.simpleName.toSnakeCase()

    private fun String.toSnakeCase(): String =
        this.replace(Regex("([a-z])([A-Z])"), "$1_$2").lowercase()
}
