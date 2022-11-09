package view
import model.entities.Entity
import model.common.Utilities.MetaData

object View {
  trait Renderer:
    def renderEntities(entities: List[Entity]): Unit

    /**
     *
     * @param metaData
     */
    def renderMetadata(metaData: MetaData): Unit
}

