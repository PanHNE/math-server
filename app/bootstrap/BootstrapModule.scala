package bootstrap

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule

/**
 * Moduł startowy aplikacji
 */
class BootstrapModule extends AbstractModule with ScalaModule {

  /**
   * Konfiguruje moduł
   */
  override def configure() = {
    bind(classOf[InitialData]).asEagerSingleton()
  }
}
