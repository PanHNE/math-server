package database

import io.getquill.{PostgresJdbcContext, SnakeCase}
import play.api.db.DBApiProvider

import java.io.Closeable
import javax.inject.{Inject, Singleton}
import javax.sql.DataSource

@Singleton
class DatabaseConnector @Inject()(val provider: DBApiProvider) {
  lazy val ctx = new PostgresJdbcContext(SnakeCase, provider.get.database("default").dataSource.asInstanceOf[DataSource with Closeable])
}
